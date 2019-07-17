/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.registry.security.authorization.database;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.nifi.registry.properties.NiFiRegistryProperties;
import org.apache.nifi.registry.properties.util.IdentityMapping;
import org.apache.nifi.registry.properties.util.IdentityMappingUtil;
import org.apache.nifi.registry.security.authorization.AuthorizerConfigurationContext;
import org.apache.nifi.registry.security.authorization.ConfigurableUserGroupProvider;
import org.apache.nifi.registry.security.authorization.Group;
import org.apache.nifi.registry.security.authorization.IdentifierUtil;
import org.apache.nifi.registry.security.authorization.User;
import org.apache.nifi.registry.security.authorization.UserAndGroups;
import org.apache.nifi.registry.security.authorization.UserGroupProviderInitializationContext;
import org.apache.nifi.registry.security.authorization.annotation.AuthorizerContext;
import org.apache.nifi.registry.security.authorization.database.entity.DatabaseGroup;
import org.apache.nifi.registry.security.authorization.database.entity.DatabaseUser;
import org.apache.nifi.registry.security.authorization.database.mapper.DatabaseGroupRowMapper;
import org.apache.nifi.registry.security.authorization.database.mapper.DatabaseUserRowMapper;
import org.apache.nifi.registry.security.authorization.exception.AuthorizationAccessException;
import org.apache.nifi.registry.security.authorization.exception.UninheritableAuthorizationsException;
import org.apache.nifi.registry.security.exception.SecurityProviderCreationException;
import org.apache.nifi.registry.security.exception.SecurityProviderDestructionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Database implementation of ConfigurableUserGroupProvider.
 *
 * This implementation is meant to be used when there is an external database accessed by multiple application servers.
 *
 * Optimistic locking will be used to ensure concurrent updates are handle correctly.
 *
 * Fingerprinting is not supported since each application does not have it's own copy of the data.
 */
@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
public class DatabaseUserGroupProvider implements ConfigurableUserGroupProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseUserGroupProvider.class);

    static final String PROP_INITIAL_USER_IDENTITY_PREFIX = "Initial User Identity ";
    static final Pattern INITIAL_USER_IDENTITY_PATTERN = Pattern.compile(PROP_INITIAL_USER_IDENTITY_PREFIX + "\\S+");

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private NiFiRegistryProperties properties;

    @AuthorizerContext
    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @AuthorizerContext
    public void setProperties(final NiFiRegistryProperties properties) {
        this.properties = properties;
    }

    @Override
    public void initialize(final UserGroupProviderInitializationContext initializationContext) throws SecurityProviderCreationException {
        if (this.dataSource == null) {
            throw new IllegalStateException("DataSource cannot be null");
        }

        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void onConfigured(final AuthorizerConfigurationContext configurationContext) throws SecurityProviderCreationException {
        // extract the identity mappings from nifi-registry.properties if any are provided
        final List<IdentityMapping> identityMappings = Collections.unmodifiableList(IdentityMappingUtil.getIdentityMappings(properties));

        // extract any initial user identities
        final Set<String> initialUserIdentities = new HashSet<>();
        for (Map.Entry<String,String> entry : configurationContext.getProperties().entrySet()) {
            Matcher matcher = INITIAL_USER_IDENTITY_PATTERN.matcher(entry.getKey());
            if (matcher.matches() && !StringUtils.isBlank(entry.getValue())) {
                initialUserIdentities.add(IdentityMappingUtil.mapIdentity(entry.getValue(), identityMappings));
            }
        }

        // create initial users when none exist
        final Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM UGP_USER", Integer.class);
        final Integer groupCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM UGP_GROUP", Integer.class);

        if (userCount > 0 || groupCount > 0) {
            LOGGER.info("Found existing users and/or groups, will not create initial users");
        } else  {
            for (final String initialUserIdentity : initialUserIdentities) {
                final User initialUser = new User.Builder()
                        .identifier(IdentifierUtil.getIdentifier(initialUserIdentity))
                        .identity(initialUserIdentity)
                        .build();
                addUser(initialUser);
                LOGGER.info("Created initial user with identity {}", new Object[]{initialUserIdentity});
            }
        }
    }

    @Override
    public void preDestruction() throws SecurityProviderDestructionException {

    }

    //-- fingerprint methods

    @Override
    public String getFingerprint() throws AuthorizationAccessException {
        throw new UnsupportedOperationException("Fingerprinting is not supported by this provider");
    }

    @Override
    public void inheritFingerprint(final String fingerprint) throws AuthorizationAccessException {
        throw new UnsupportedOperationException("Fingerprinting is not supported by this provider");
    }

    @Override
    public void checkInheritability(final String proposedFingerprint) throws AuthorizationAccessException, UninheritableAuthorizationsException {
        throw new UnsupportedOperationException("Fingerprinting is not supported by this provider");
    }

    //-- User CRUD

    @Override
    public User addUser(final User user) throws AuthorizationAccessException {
        Validate.notNull(user);
        final String sql = "INSERT INTO UGP_USER(IDENTIFIER, IDENTITY, VERSION) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, new Object[] {user.getIdentifier(), user.getIdentity(), 0});
        return user;
    }

    @Override
    public User updateUser(final User user) throws AuthorizationAccessException {
        Validate.notNull(user);

        // retrieve the database user to ensure it exists...
        final String userIdentifier = user.getIdentifier();
        final DatabaseUser databaseUser = getDatabaseUser(userIdentifier);
        if (databaseUser == null) {
            return null;
        }

        // update with optimistic-locking...
        final String sql =
                "UPDATE UGP_USER SET " +
                    "IDENTITY = ?, " +
                    "VERSION = ? " +
                "WHERE " +
                    "IDENTIFIER = ? AND " +
                    "VERSION = ?";

        final String userIdentity = user.getIdentity();
        final Long currVersion = user.getRevision();
        final Long nextVersion = currVersion + 1;

        // if no rows were impacted then the current update is stale so throw an exception
        final int updated = jdbcTemplate.update(sql, userIdentity, nextVersion, userIdentifier, currVersion);
        if (updated <= 0) {
            throw new OptimisticLockingFailureException(user.getRevision() + " is not the most up to date revision. " +
                    "This user appears to have been modified by another request.");
        }

        // otherwise the update succeeded, so return user with updated revision
        return new User.Builder(user).revision(nextVersion).build();
    }

    @Override
    public Set<User> getUsers() throws AuthorizationAccessException {
        final String sql = "SELECT * FROM UGP_USER";
        final List<DatabaseUser> databaseUsers = jdbcTemplate.query(sql, new DatabaseUserRowMapper());

        final Set<User> users = new HashSet<>();
        databaseUsers.forEach(u -> {
            users.add(mapToUser(u));
        });
        return users;
    }

    @Override
    public User getUser(final String identifier) throws AuthorizationAccessException {
        Validate.notBlank(identifier);

        final DatabaseUser databaseUser = getDatabaseUser(identifier);
        if (databaseUser == null) {
            return null;
        }

        return mapToUser(databaseUser);
    }

    @Override
    public User getUserByIdentity(final String identity) throws AuthorizationAccessException {
        Validate.notBlank(identity);

        final String sql = "SELECT * FROM UGP_USER WHERE IDENTITY = ?";
        final DatabaseUser databaseUser = queryForObject(sql, new Object[] {identity}, new DatabaseUserRowMapper());
        if (databaseUser == null) {
            return null;
        }

        return mapToUser(databaseUser);
    }

    @Override
    public UserAndGroups getUserAndGroups(final String userIdentity) throws AuthorizationAccessException {
        Validate.notBlank(userIdentity);

        // retrieve the user
        final User user = getUserByIdentity(userIdentity);

        // if the user exists, then retrieve the groups for the user
        final Set<Group> groups;
        if (user == null) {
            groups = null;
        } else {
            final String userGroupSql =
                    "SELECT " +
                            "G.IDENTIFIER AS IDENTIFIER, " +
                            "G.IDENTITY AS IDENTITY, " +
                            "G.VERSION AS VERSION " +
                    "FROM " +
                            "UGP_GROUP AS G, " +
                            "UGP_USER_GROUP AS UG " +
                    "WHERE " +
                            "G.IDENTIFIER = UG.GROUP_IDENTIFIER AND " +
                            "UG.USER_IDENTIFIER = ?";

            final Object[] args = {user.getIdentifier()};
            final List<DatabaseGroup> databaseGroups = jdbcTemplate.query(userGroupSql, args, new DatabaseGroupRowMapper());

            groups = new HashSet<>();
            databaseGroups.forEach(g -> {
                final Set<String> userIdentifiers = getUserIdentifiers(g.getIdentifier());
                groups.add(mapToGroup(g, userIdentifiers));
            });
        }

        return new UserAndGroups() {
            @Override
            public User getUser() {
                return user;
            }

            @Override
            public Set<Group> getGroups() {
                return groups;
            }
        };
    }

    @Override
    public User deleteUser(final User user) throws AuthorizationAccessException {
        Validate.notNull(user);

        final String sql = "DELETE FROM UGP_USER WHERE IDENTIFIER = ? AND VERSION = ?";
        final int rowsUpdated = jdbcTemplate.update(sql, user.getIdentifier(), user.getRevision());

        if (rowsUpdated <= 0) {
            throw new OptimisticLockingFailureException(user.getRevision() + " is not the most up to date revision. " +
                    "This user appears to have been modified by another request.");
        }

        return user;
    }

    @Override
    public User deleteUser(final String userIdentifier) throws AuthorizationAccessException {
        throw new UnsupportedOperationException("Deleting a user by identifier is not supported by this provider");
    }

    private DatabaseUser getDatabaseUser(final String userIdentifier) {
        final String sql = "SELECT * FROM UGP_USER WHERE IDENTIFIER = ?";
        return queryForObject(sql, new Object[] {userIdentifier}, new DatabaseUserRowMapper());
    }

    private User mapToUser(final DatabaseUser databaseUser) {
        return new User.Builder()
                .identifier(databaseUser.getIdentifier())
                .identity(databaseUser.getIdentity())
                .revision(databaseUser.getVersion())
                .build();
    }

    //-- Group CRUD

    @Override
    public Group addGroup(final Group group) throws AuthorizationAccessException {
        Validate.notNull(group);

        // insert to the group table...
        final String groupSql = "INSERT INTO UGP_GROUP(IDENTIFIER, IDENTITY, VERSION) VALUES (?, ?, ?)";
        jdbcTemplate.update(groupSql, group.getIdentifier(), group.getName(), 0);

        // insert to the user-group table...
        createUserGroups(group);

        return group;
    }

    @Override
    public Group updateGroup(final Group group) throws AuthorizationAccessException {
        Validate.notNull(group);

        // retrieve the existing group or return null
        final String groupIdentifier = group.getIdentifier();
        final DatabaseGroup databaseGroup = getDatabaseGroup(groupIdentifier);
        if (databaseGroup == null) {
            return null;
        }

        // delete any user-group associations and re-create them
        final String deleteUserGroups = "DELETE FROM UGP_USER_GROUP WHERE GROUP_IDENTIFIER = ?";
        jdbcTemplate.update(deleteUserGroups, groupIdentifier);
        createUserGroups(group);

        // update the group row with optimistic-locking...
        final String updateGroupSql =
                "UPDATE UGP_GROUP SET " +
                        "IDENTITY = ?, " +
                        "VERSION = ? " +
                "WHERE " +
                        "IDENTIFIER = ? AND " +
                        "VERSION = ?";


        final String groupIdentity = group.getName();
        final Long currVersion = group.getRevision();
        final Long nextVersion = currVersion + 1;

        // if no rows were impacted then the current update is stale so throw an exception
        final int updated = jdbcTemplate.update(updateGroupSql, groupIdentity, nextVersion, groupIdentifier, currVersion);
        if (updated <= 0) {
            throw new OptimisticLockingFailureException(group.getRevision() + " is not the most up to date revision. " +
                    "This group appears to have been modified by another request.");
        }

        // otherwise the update succeeded so return the group with updated revision
        return new Group.Builder(group).revision(nextVersion).build();
    }

    @Override
    public Set<Group> getGroups() throws AuthorizationAccessException {
        // retrieve all the groups
        final String sql = "SELECT * FROM UGP_GROUP";
        final List<DatabaseGroup> databaseGroups = jdbcTemplate.query(sql, new DatabaseGroupRowMapper());

        // retrieve all the users in the groups, mapped by group id
        final Map<String,Set<String>> groupToUsers = new HashMap<>();
        jdbcTemplate.query("SELECT * FROM UGP_USER_GROUP", (rs) -> {
            final String groupIdentifier = rs.getString("GROUP_IDENTIFIER");
            final String userIdentifier = rs.getString("USER_IDENTIFIER");

            final Set<String> userIdentifiers = groupToUsers.computeIfAbsent(groupIdentifier, (k) -> new HashSet<>());
            userIdentifiers.add(userIdentifier);
        });

        // convert from database model to api model
        final Set<Group> groups = new HashSet<>();
        databaseGroups.forEach(g -> {
            groups.add(mapToGroup(g, groupToUsers.get(g.getIdentifier())));
        });
        return groups;
    }

    @Override
    public Group getGroup(final String groupIdentifier) throws AuthorizationAccessException {
        Validate.notBlank(groupIdentifier);

        final DatabaseGroup databaseGroup = getDatabaseGroup(groupIdentifier);
        if (databaseGroup == null) {
            return null;
        }

        final Set<String> userIdentifiers = getUserIdentifiers(groupIdentifier);
        return mapToGroup(databaseGroup, userIdentifiers);
    }

    @Override
    public Group deleteGroup(final Group group) throws AuthorizationAccessException {
        Validate.notNull(group);

        final String sql = "DELETE FROM UGP_GROUP WHERE IDENTIFIER = ? AND VERSION = ?";
        final int rowsUpdated = jdbcTemplate.update(sql, group.getIdentifier(), group.getRevision());

        if (rowsUpdated <= 0) {
            throw new OptimisticLockingFailureException(group.getRevision() + " is not the most up to date revision. " +
                    "This group appears to have been modified by another request.");
        }

        return group;
    }

    @Override
    public Group deleteGroup(final String groupIdentifier) throws AuthorizationAccessException {
        throw new UnsupportedOperationException("Deleting a group by identifier is not supported by this provider");
    }

    private void createUserGroups(final Group group) {
        if (group.getUsers() != null) {
            for (final String userIdentifier : group.getUsers()) {
                final String userGroupSql = "INSERT INTO UGP_USER_GROUP (USER_IDENTIFIER, GROUP_IDENTIFIER) VALUES (?, ?)";
                jdbcTemplate.update(userGroupSql, userIdentifier, group.getIdentifier());
            }
        }
    }

    private DatabaseGroup getDatabaseGroup(final String groupIdentifier) {
        final String sql = "SELECT * FROM UGP_GROUP WHERE IDENTIFIER = ?";
        return queryForObject(sql, new Object[] {groupIdentifier}, new DatabaseGroupRowMapper());
    }

    private Set<String> getUserIdentifiers(final String groupIdentifier) {
        final String sql = "SELECT * FROM UGP_USER_GROUP WHERE GROUP_IDENTIFIER = ?";

        final Set<String> userIdentifiers = new HashSet<>();
        jdbcTemplate.query(sql, new Object[]{groupIdentifier}, (rs) -> {
           userIdentifiers.add(rs.getString("USER_IDENTIFIER"));
        });

        return userIdentifiers;
    }

    private Group mapToGroup(final DatabaseGroup databaseGroup, final Set<String> userIdentifiers) {
        return new Group.Builder()
                .identifier(databaseGroup.getIdentifier())
                .name(databaseGroup.getIdentity())
                .addUsers(userIdentifiers == null ? Collections.emptySet() : userIdentifiers)
                .revision(databaseGroup.getVersion())
                .build();
    }

    //-- util methods

    private <T> T queryForObject(final String sql, final Object[] args, final RowMapper<T> rowMapper) {
        try {
            return jdbcTemplate.queryForObject(sql, args, rowMapper);
        } catch(final EmptyResultDataAccessException e) {
            return null;
        }
    }
}
