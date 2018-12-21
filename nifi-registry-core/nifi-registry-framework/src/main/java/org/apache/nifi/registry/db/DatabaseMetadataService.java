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
package org.apache.nifi.registry.db;

import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.registry.db.entity.BucketEntity;
import org.apache.nifi.registry.db.entity.BucketItemEntity;
import org.apache.nifi.registry.db.entity.ExtensionBundleEntity;
import org.apache.nifi.registry.db.entity.ExtensionBundleVersionDependencyEntity;
import org.apache.nifi.registry.db.entity.ExtensionBundleVersionEntity;
import org.apache.nifi.registry.db.entity.ExtensionEntity;
import org.apache.nifi.registry.db.entity.ExtensionEntityCategory;
import org.apache.nifi.registry.db.entity.FlowEntity;
import org.apache.nifi.registry.db.entity.FlowSnapshotEntity;
import org.apache.nifi.registry.db.jdbc.configuration.Tables;
import org.apache.nifi.registry.db.jdbc.repository.BucketItemRepository;
import org.apache.nifi.registry.db.jdbc.repository.BucketRepository;
import org.apache.nifi.registry.db.jdbc.repository.FlowRepository;
import org.apache.nifi.registry.db.jdbc.repository.FlowSnapshotRepository;
import org.apache.nifi.registry.db.jdbc.repository.impl.RepositoryUtils;
import org.apache.nifi.registry.db.mapper.ExtensionBundleEntityRowMapper;
import org.apache.nifi.registry.db.mapper.ExtensionBundleEntityWithBucketNameRowMapper;
import org.apache.nifi.registry.db.mapper.ExtensionBundleVersionDependencyEntityRowMapper;
import org.apache.nifi.registry.db.mapper.ExtensionBundleVersionEntityRowMapper;
import org.apache.nifi.registry.db.mapper.ExtensionEntityRowMapper;
import org.apache.nifi.registry.db.mapper.FlowSnapshotEntityRowMapper;
import org.apache.nifi.registry.extension.filter.ExtensionBundleFilterParams;
import org.apache.nifi.registry.extension.filter.ExtensionBundleVersionFilterParams;
import org.apache.nifi.registry.jdbc.api.JdbcEntityTemplate;
import org.apache.nifi.registry.jdbc.api.QueryParameters;
import org.apache.nifi.registry.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.apache.nifi.registry.jdbc.commons.StandardQueryParameter.eq;
import static org.apache.nifi.registry.jdbc.commons.StandardQueryParameters.of;

@Repository
public class DatabaseMetadataService implements MetadataService {

    private final JdbcTemplate jdbcTemplate;
    private JdbcEntityTemplate jdbcEntityTemplate;
    private final BucketRepository bucketRepository;
    private final BucketItemRepository itemRepository;
    private final FlowRepository flowRepository;
    private final FlowSnapshotRepository flowSnapshotRepository;

    @Autowired
    public DatabaseMetadataService(final JdbcTemplate jdbcTemplate,
                                   final JdbcEntityTemplate jdbcEntityTemplate,
                                   final BucketRepository bucketRepository,
                                   final BucketItemRepository itemRepository,
                                   final FlowRepository flowRepository,
                                   final FlowSnapshotRepository flowSnapshotRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcEntityTemplate = jdbcEntityTemplate;
        this.bucketRepository = bucketRepository;
        this.itemRepository = itemRepository;
        this.flowRepository = flowRepository;
        this.flowSnapshotRepository = flowSnapshotRepository;
    }

    //----------------- Buckets ---------------------------------

    @Override
    public BucketEntity createBucket(final BucketEntity b) {
        return bucketRepository.create(b);
    }

    @Override
    public BucketEntity getBucketById(final String bucketIdentifier) {
        final Optional<BucketEntity> result = bucketRepository.findById(bucketIdentifier);
        return result.isPresent() ? result.get() : null;
    }

    @Override
    public List<BucketEntity> getBucketsByName(final String name) {
        final QueryParameters params = of(eq(Tables.BUCKET.NAME, name));
        return bucketRepository.findByQueryParams(params);
    }

    @Override
    public BucketEntity updateBucket(final BucketEntity bucket) {
        return bucketRepository.update(bucket);
    }

    @Override
    public void deleteBucket(final BucketEntity bucket) {
        bucketRepository.delete(bucket);
    }

    @Override
    public List<BucketEntity> getBuckets(final Set<String> bucketIds) {
        if (bucketIds == null || bucketIds.isEmpty()) {
            return Collections.emptyList();
        }
        return bucketRepository.findAllById(bucketIds);
    }

    @Override
    public List<BucketEntity> getAllBuckets() {
        return bucketRepository.findAll();
    }

    //----------------- BucketItems ---------------------------------

    @Override
    public List<BucketItemEntity> getBucketItems(final String bucketIdentifier) {
        return itemRepository.getBucketItems(bucketIdentifier);
    }

    @Override
    public List<BucketItemEntity> getBucketItems(final Set<String> bucketIds) {
        if (bucketIds == null || bucketIds.isEmpty()) {
            return Collections.emptyList();
        }

        return itemRepository.getBucketItems(bucketIds);
    }

    //----------------- Flows ---------------------------------

    @Override
    public FlowEntity createFlow(final FlowEntity flow) {
        return flowRepository.create(flow);
    }

    @Override
    public FlowEntity getFlowById(final String flowIdentifier) {
        final Optional<FlowEntity> result = flowRepository.findById(flowIdentifier);
        return result.isPresent() ? result.get() : null;
    }

    @Override
    public FlowEntity getFlowByIdWithSnapshotCounts(final String flowIdentifier) {
        final Optional<FlowEntity> result = flowRepository.findByIdWithSnapshotCounts(flowIdentifier);
        return result.isPresent() ? result.get() : null;
    }

    @Override
    public List<FlowEntity> getFlowsByName(final String name) {
        final QueryParameters params = of(eq(Tables.BUCKET_ITEM.NAME, name));
        return flowRepository.findByQueryParams(params);
    }

    @Override
    public List<FlowEntity> getFlowsByName(final String bucketIdentifier, final String name) {
        final QueryParameters params = of(
                eq(Tables.BUCKET_ITEM.BUCKET_ID, bucketIdentifier),
                eq(Tables.BUCKET_ITEM.NAME, name)
        );

        return flowRepository.findByQueryParams(params);
    }

    @Override
    public List<FlowEntity> getFlowsByBucket(final String bucketIdentifier) {
        return flowRepository.findByBucket(bucketIdentifier);
    }

    @Override
    public FlowEntity updateFlow(final FlowEntity flow) {
        flow.setModified(new Date());
        return flowRepository.update(flow);
    }

    @Override
    public void deleteFlow(final FlowEntity flow) {
        flowRepository.delete(flow);
    }

    //----------------- Flow Snapshots ---------------------------------

    @Override
    public FlowSnapshotEntity createFlowSnapshot(final FlowSnapshotEntity flowSnapshot) {
        return flowSnapshotRepository.create(flowSnapshot);
    }

    @Override
    public FlowSnapshotEntity getFlowSnapshot(final String flowIdentifier, final Integer version) {
        final String sql =
                "SELECT " +
                        "fs.flow_id, " +
                        "fs.version, " +
                        "fs.created, " +
                        "fs.created_by, " +
                        "fs.comments " +
                "FROM " +
                        "flow_snapshot fs, " +
                        "flow f, " +
                        "bucket_item item " +
                "WHERE " +
                        "item.id = f.id AND " +
                        "f.id = ? AND " +
                        "f.id = fs.flow_id AND " +
                        "fs.version = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new FlowSnapshotEntityRowMapper(),
                    flowIdentifier, version);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public FlowSnapshotEntity getLatestSnapshot(final String flowIdentifier) {
        final String sql = "SELECT * FROM flow_snapshot WHERE flow_id = ? ORDER BY version DESC LIMIT 1";

        try {
            return jdbcTemplate.queryForObject(sql, new FlowSnapshotEntityRowMapper(), flowIdentifier);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<FlowSnapshotEntity> getSnapshots(final String flowIdentifier) {
        final String sql =
                "SELECT " +
                        "fs.flow_id, " +
                        "fs.version, " +
                        "fs.created, " +
                        "fs.created_by, " +
                        "fs.comments " +
                "FROM " +
                        "flow_snapshot fs, " +
                        "flow f, " +
                        "bucket_item item " +
                "WHERE " +
                        "item.id = f.id AND " +
                        "f.id = ? AND " +
                        "f.id = fs.flow_id";

        final Object[] args = new Object[] { flowIdentifier };
        return jdbcTemplate.query(sql, args, new FlowSnapshotEntityRowMapper());
    }

    @Override
    public void deleteFlowSnapshot(final FlowSnapshotEntity flowSnapshot) {
        final String sql = "DELETE FROM flow_snapshot WHERE flow_id = ? AND version = ?";
        jdbcTemplate.update(sql, flowSnapshot.getFlowId(), flowSnapshot.getVersion());
    }

    //----------------- Extension Bundles ---------------------------------

    @Override
    public ExtensionBundleEntity createExtensionBundle(final ExtensionBundleEntity extensionBundle) {
        final String itemSql =
                "INSERT INTO bucket_item (" +
                    "ID, " +
                    "NAME, " +
                    "DESCRIPTION, " +
                    "CREATED, " +
                    "MODIFIED, " +
                    "ITEM_TYPE, " +
                    "BUCKET_ID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(itemSql,
                extensionBundle.getId(),
                extensionBundle.getName(),
                extensionBundle.getDescription(),
                extensionBundle.getCreated(),
                extensionBundle.getModified(),
                extensionBundle.getType().toString(),
                extensionBundle.getBucketId());

        final String bundleSql =
                "INSERT INTO extension_bundle (" +
                    "ID, " +
                    "BUCKET_ID, " +
                    "BUNDLE_TYPE, " +
                    "GROUP_ID, " +
                    "ARTIFACT_ID) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(bundleSql,
                extensionBundle.getId(),
                extensionBundle.getBucketId(),
                extensionBundle.getBundleType().toString(),
                extensionBundle.getGroupId(),
                extensionBundle.getArtifactId());

        return extensionBundle;
    }

    @Override
    public ExtensionBundleEntity getExtensionBundle(final String extensionBundleId) {
        final String sql =
                "SELECT * " +
                "FROM extension_bundle eb, bucket_item item " +
                "WHERE eb.id = ? AND item.id = eb.id";
        try {
            final ExtensionBundleEntity entity = jdbcTemplate.queryForObject(sql, new ExtensionBundleEntityRowMapper(), extensionBundleId);

            final Long versionCount = RepositoryUtils.getExtensionBundleVersionCount(jdbcEntityTemplate, extensionBundleId);
            if (versionCount != null) {
                entity.setVersionCount(versionCount);
            }

            return entity;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public ExtensionBundleEntity getExtensionBundle(final String bucketId, final String groupId, final String artifactId) {
        final String sql =
                "SELECT * " +
                "FROM " +
                        "extension_bundle eb, " +
                        "bucket_item item " +
                "WHERE " +
                        "item.id = eb.id AND " +
                        "eb.bucket_id = ? AND " +
                        "eb.group_id = ? AND " +
                        "eb.artifact_id = ?";
        try {
            final ExtensionBundleEntity entity = jdbcTemplate.queryForObject(sql, new ExtensionBundleEntityRowMapper(), bucketId, groupId, artifactId);

            final Long versionCount = RepositoryUtils.getExtensionBundleVersionCount(jdbcEntityTemplate, entity.getId());
            if (versionCount != null) {
                entity.setVersionCount(versionCount);
            }

            return entity;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<ExtensionBundleEntity> getExtensionBundles(final Set<String> bucketIds, final ExtensionBundleFilterParams filterParams) {
        if (bucketIds == null || bucketIds.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Object> args = new ArrayList<>();

        final StringBuilder sqlBuilder = new StringBuilder(
                "SELECT " +
                        "item.id as ID, " +
                        "item.name as NAME, " +
                        "item.description as DESCRIPTION, " +
                        "item.created as CREATED, " +
                        "item.modified as MODIFIED, " +
                        "item.item_type as ITEM_TYPE, " +
                        "b.id as BUCKET_ID, " +
                        "b.name as BUCKET_NAME ," +
                        "eb.bundle_type as BUNDLE_TYPE, " +
                        "eb.group_id as BUNDLE_GROUP_ID, " +
                        "eb.artifact_id as BUNDLE_ARTIFACT_ID " +
                "FROM " +
                    "extension_bundle eb, " +
                    "bucket_item item, " +
                    "bucket b " +
                "WHERE " +
                    "item.id = eb.id AND " +
                    "b.id = item.bucket_id");

        if (filterParams != null) {
            final String groupId = filterParams.getGroupId();
            if (!StringUtils.isBlank(groupId)) {
                sqlBuilder.append(" AND eb.group_id LIKE ? ");
                args.add(groupId);
            }

            final String artifactId = filterParams.getArtifactId();
            if (!StringUtils.isBlank(artifactId)) {
                sqlBuilder.append(" AND eb.artifact_id LIKE ? ");
                args.add(artifactId);
            }
        }

        addBucketIdentifiersClause(sqlBuilder, "item.bucket_id", bucketIds);
        sqlBuilder.append("ORDER BY eb.group_id ASC, eb.artifact_id ASC");

        args.addAll(bucketIds);

        final List<ExtensionBundleEntity> bundleEntities = jdbcTemplate.query(sqlBuilder.toString(), args.toArray(), new ExtensionBundleEntityWithBucketNameRowMapper());
        return populateVersionCounts(bundleEntities);
    }

    @Override
    public List<ExtensionBundleEntity> getExtensionBundlesByBucket(final String bucketId) {
        final String sql =
                "SELECT * " +
                "FROM " +
                    "extension_bundle eb, " +
                    "bucket_item item " +
                "WHERE " +
                    "item.id = eb.id AND " +
                    "item.bucket_id = ? " +
                    "ORDER BY eb.group_id ASC, eb.artifact_id ASC";

        final List<ExtensionBundleEntity> bundles = jdbcTemplate.query(sql, new Object[]{bucketId}, new ExtensionBundleEntityRowMapper());
        return populateVersionCounts(bundles);
    }

    @Override
    public List<ExtensionBundleEntity> getExtensionBundlesByBucketAndGroup(String bucketId, String groupId) {
        final String sql =
                "SELECT * " +
                    "FROM " +
                        "extension_bundle eb, " +
                        "bucket_item item " +
                    "WHERE " +
                        "item.id = eb.id AND " +
                        "item.bucket_id = ? AND " +
                        "eb.group_id = ?" +
                    "ORDER BY eb.group_id ASC, eb.artifact_id ASC";

        final List<ExtensionBundleEntity> bundles = jdbcTemplate.query(sql, new Object[]{bucketId, groupId}, new ExtensionBundleEntityRowMapper());
        return populateVersionCounts(bundles);
    }

    private List<ExtensionBundleEntity> populateVersionCounts(final List<ExtensionBundleEntity> bundles) {
        if (!bundles.isEmpty()) {
            final Map<String, Long> versionCounts = RepositoryUtils.getExtensionBundleVersionCounts(jdbcEntityTemplate);
            for (final ExtensionBundleEntity entity : bundles) {
                final Long versionCount = versionCounts.get(entity.getId());
                if (versionCount != null) {
                    entity.setVersionCount(versionCount);
                }
            }
        }

        return bundles;
    }

    @Override
    public void deleteExtensionBundle(final ExtensionBundleEntity extensionBundle) {
        deleteExtensionBundle(extensionBundle.getId());
    }

    @Override
    public void deleteExtensionBundle(final String extensionBundleId) {
        // NOTE: All of the foreign key constraints for extension related tables are set to cascade on delete
        final String itemDeleteSql = "DELETE FROM bucket_item WHERE id = ?";
        jdbcTemplate.update(itemDeleteSql, extensionBundleId);
    }

    //----------------- Extension Bundle Versions ---------------------------------

    @Override
    public ExtensionBundleVersionEntity createExtensionBundleVersion(final ExtensionBundleVersionEntity extensionBundleVersion) {
        final String sql =
                "INSERT INTO extension_bundle_version (" +
                    "ID, " +
                    "EXTENSION_BUNDLE_ID, " +
                    "VERSION, " +
                    "CREATED, " +
                    "CREATED_BY, " +
                    "DESCRIPTION, " +
                    "SHA_256_HEX, " +
                    "SHA_256_SUPPLIED," +
                    "CONTENT_SIZE " +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                extensionBundleVersion.getId(),
                extensionBundleVersion.getExtensionBundleId(),
                extensionBundleVersion.getVersion(),
                extensionBundleVersion.getCreated(),
                extensionBundleVersion.getCreatedBy(),
                extensionBundleVersion.getDescription(),
                extensionBundleVersion.getSha256Hex(),
                extensionBundleVersion.getSha256Supplied() ? 1 : 0,
                extensionBundleVersion.getContentSize());

        return extensionBundleVersion;
    }

    private static final String BASE_EXTENSION_BUNDLE_VERSION_SQL =
            "SELECT " +
                "ebv.id AS ID," +
                "ebv.extension_bundle_id AS EXTENSION_BUNDLE_ID, " +
                "ebv.version AS VERSION, " +
                "ebv.created AS CREATED, " +
                "ebv.created_by AS CREATED_BY, " +
                "ebv.description AS DESCRIPTION, " +
                "ebv.sha_256_hex AS SHA_256_HEX, " +
                "ebv.sha_256_supplied AS SHA_256_SUPPLIED ," +
                "ebv.content_size AS CONTENT_SIZE, " +
                "eb.bucket_id AS BUCKET_ID " +
            "FROM extension_bundle eb, extension_bundle_version ebv " +
            "WHERE eb.id = ebv.extension_bundle_id ";

    @Override
    public ExtensionBundleVersionEntity getExtensionBundleVersion(final String extensionBundleId, final String version) {
        final String sql = BASE_EXTENSION_BUNDLE_VERSION_SQL +
                " AND ebv.extension_bundle_id = ? AND ebv.version = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new ExtensionBundleVersionEntityRowMapper(), extensionBundleId, version);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public ExtensionBundleVersionEntity getExtensionBundleVersion(final String bucketId, final String groupId, final String artifactId, final String version) {
        final String sql = BASE_EXTENSION_BUNDLE_VERSION_SQL +
                    "AND eb.bucket_id = ? " +
                    "AND eb.group_id = ? " +
                    "AND eb.artifact_id = ? " +
                    "AND ebv.version = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new ExtensionBundleVersionEntityRowMapper(), bucketId, groupId, artifactId, version);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<ExtensionBundleVersionEntity> getExtensionBundleVersions(final Set<String> bucketIdentifiers, final ExtensionBundleVersionFilterParams filterParams) {
        if (bucketIdentifiers == null || bucketIdentifiers.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Object> args = new ArrayList<>();
        final StringBuilder sqlBuilder = new StringBuilder(BASE_EXTENSION_BUNDLE_VERSION_SQL);

        if (filterParams != null) {
            final String groupId = filterParams.getGroupId();
            if (!StringUtils.isBlank(groupId)) {
                sqlBuilder.append(" AND eb.group_id LIKE ? ");
                args.add(groupId);
            }

            final String artifactId = filterParams.getArtifactId();
            if (!StringUtils.isBlank(artifactId)) {
                sqlBuilder.append(" AND eb.artifact_id LIKE ? ");
                args.add(artifactId);
            }

            final String version = filterParams.getVersion();
            if (!StringUtils.isBlank(version)) {
                sqlBuilder.append(" AND ebv.version LIKE ? ");
                args.add(version);
            }
        }

        addBucketIdentifiersClause(sqlBuilder, "eb.bucket_id", bucketIdentifiers);
        args.addAll(bucketIdentifiers);

        final List<ExtensionBundleVersionEntity> bundleVersionEntities = jdbcTemplate.query(
                sqlBuilder.toString(), args.toArray(), new ExtensionBundleVersionEntityRowMapper());

        return bundleVersionEntities;
    }

    private void addBucketIdentifiersClause(StringBuilder sqlBuilder, String bucketField, Set<String> bucketIdentifiers) {
        sqlBuilder.append(" AND ").append(bucketField).append(" IN (");
        for (int i = 0; i < bucketIdentifiers.size(); i++) {
            if (i > 0) {
                sqlBuilder.append(", ");
            }
            sqlBuilder.append("?");
        }
        sqlBuilder.append(") ");
    }

    @Override
    public List<ExtensionBundleVersionEntity> getExtensionBundleVersions(final String extensionBundleId) {
        final String sql = BASE_EXTENSION_BUNDLE_VERSION_SQL + " AND ebv.extension_bundle_id = ?";
        return jdbcTemplate.query(sql, new Object[]{extensionBundleId}, new ExtensionBundleVersionEntityRowMapper());
    }

    @Override
    public List<ExtensionBundleVersionEntity> getExtensionBundleVersions(final String bucketId, final String groupId, final String artifactId) {
        final String sql = BASE_EXTENSION_BUNDLE_VERSION_SQL +
                    "AND eb.bucket_id = ? " +
                    "AND eb.group_id = ? " +
                    "AND eb.artifact_id = ? ";

        final Object[] args = {bucketId, groupId, artifactId};
        return jdbcTemplate.query(sql, args, new ExtensionBundleVersionEntityRowMapper());
    }

    @Override
    public List<ExtensionBundleVersionEntity> getExtensionBundleVersionsGlobal(final String groupId, final String artifactId, final String version) {
        final String sql = BASE_EXTENSION_BUNDLE_VERSION_SQL +
                "AND eb.group_id = ? " +
                "AND eb.artifact_id = ? " +
                "AND ebv.version = ?";

        final Object[] args = {groupId, artifactId, version};
        return jdbcTemplate.query(sql, args, new ExtensionBundleVersionEntityRowMapper());
    }

    @Override
    public void deleteExtensionBundleVersion(final ExtensionBundleVersionEntity extensionBundleVersion) {
        deleteExtensionBundleVersion(extensionBundleVersion.getId());
    }

    @Override
    public void deleteExtensionBundleVersion(final String extensionBundleVersionId) {
        // NOTE: All of the foreign key constraints for extension related tables are set to cascade on delete
        final String sql = "DELETE FROM extension_bundle_version WHERE id = ?";
        jdbcTemplate.update(sql, extensionBundleVersionId);
    }

    //------------ Extension Bundle Version Dependencies ------------

    @Override
    public ExtensionBundleVersionDependencyEntity createDependency(final ExtensionBundleVersionDependencyEntity dependencyEntity) {
        final String dependencySql =
                "INSERT INTO extension_bundle_version_dependency (" +
                    "ID, " +
                    "EXTENSION_BUNDLE_VERSION_ID, " +
                    "GROUP_ID, " +
                    "ARTIFACT_ID, " +
                    "VERSION " +
                ") VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(dependencySql,
                dependencyEntity.getId(),
                dependencyEntity.getExtensionBundleVersionId(),
                dependencyEntity.getGroupId(),
                dependencyEntity.getArtifactId(),
                dependencyEntity.getVersion());

        return dependencyEntity;
    }

    @Override
    public List<ExtensionBundleVersionDependencyEntity> getDependenciesForBundleVersion(final String extensionBundleVersionId) {
        final String sql = "SELECT * FROM extension_bundle_version_dependency WHERE extension_bundle_version_id = ?";
        final Object[] args = {extensionBundleVersionId};
        return jdbcTemplate.query(sql, args, new ExtensionBundleVersionDependencyEntityRowMapper());
    }


    //----------------- Extensions ---------------------------------

    @Override
    public ExtensionEntity createExtension(final ExtensionEntity extension) {
        final String insertExtensionSql =
                "INSERT INTO extension (" +
                    "ID, " +
                    "EXTENSION_BUNDLE_VERSION_ID, " +
                    "TYPE, " +
                    "TYPE_DESCRIPTION, " +
                    "IS_RESTRICTED, " +
                    "CATEGORY, " +
                    "TAGS " +
                ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(insertExtensionSql,
                extension.getId(),
                extension.getExtensionBundleVersionId(),
                extension.getType(),
                extension.getTypeDescription(),
                extension.isRestricted() ? 1 : 0,
                extension.getCategory().toString(),
                extension.getTags()
        );

        final String insertTagSql = "INSERT INTO extension_tag (ID, EXTENSION_ID, TAG) VALUES (?, ?, ?);";

        if (extension.getTags() != null) {
            final String tags[] = extension.getTags().split("[,]");
            for (final String tag : tags) {
                if (tag != null) {
                    final Object[] args = new Object[] {UUID.randomUUID().toString(), extension.getId(), tag.trim().toLowerCase()};
                    jdbcTemplate.update(insertTagSql, args);
                }
            }
        }

        return extension;
    }

    @Override
    public ExtensionEntity getExtensionById(final String id) {
        final String selectSql = "SELECT * FROM extension WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(selectSql, new ExtensionEntityRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<ExtensionEntity> getAllExtensions() {
        final String selectSql = "SELECT * FROM extension ORDER BY type ASC";
        return jdbcTemplate.query(selectSql, new ExtensionEntityRowMapper());
    }

    @Override
    public List<ExtensionEntity> getExtensionsByBundleVersionId(final String extensionBundleVersionId) {
        final String selectSql =
                "SELECT * " +
                "FROM extension " +
                "WHERE extension_bundle_version_id = ?";

        final Object[] args = { extensionBundleVersionId };
        return jdbcTemplate.query(selectSql, args, new ExtensionEntityRowMapper());
    }

    @Override
    public List<ExtensionEntity> getExtensionsByBundleCoordinate(final String bucketId, final String groupId, final String artifactId, final String version) {
        final String sql =
                "SELECT * " +
                "FROM extension_bundle eb, extension_bundle_version ebv, extension e " +
                "WHERE eb.id = ebv.extension_bundle_id " +
                    "AND ebv.id = e.extension_bundle_version_id " +
                    "AND eb.bucket_id = ? " +
                    "AND eb.group_id = ? " +
                    "AND eb.artifact_id = ? " +
                    "AND ebv.version = ?";

        final Object[] args = { bucketId, groupId, artifactId, version };
        return jdbcTemplate.query(sql, args, new ExtensionEntityRowMapper());
    }

    @Override
    public List<ExtensionEntity> getExtensionsByCategory(final ExtensionEntityCategory category) {
        final String selectSql = "SELECT * FROM extension WHERE category = ?";
        final Object[] args = { category.toString() };
        return jdbcTemplate.query(selectSql, args, new ExtensionEntityRowMapper());
    }

    @Override
    public List<ExtensionEntity> getExtensionsByTag(final String tag) {
        final String selectSql =
                "SELECT * " +
                "FROM extension e, extension_tag et " +
                "WHERE e.id = et.extension_id AND et.tag = ?";

        final Object[] args = { tag.trim().toLowerCase() };
        return jdbcTemplate.query(selectSql, args, new ExtensionEntityRowMapper());
    }

    @Override
    public Set<String> getAllExtensionTags() {
        final String selectSql = "SELECT DISTINCT tag FROM extension_tag ORDER BY tag ASC";

        final Set<String> tags = new LinkedHashSet<>();
        final RowCallbackHandler handler = (rs) -> tags.add(rs.getString(1));
        jdbcTemplate.query(selectSql, handler);
        return tags;
    }

    @Override
    public void deleteExtension(final ExtensionEntity extension) {
        // NOTE: All of the foreign key constraints for extension related tables are set to cascade on delete
        final String deleteSql = "DELETE FROM extension WHERE id = ?";
        jdbcTemplate.update(deleteSql, extension.getId());
    }


    //----------------- Fields ---------------------------------

    @Override
    public Set<String> getBucketFields() {
        final Set<String> fields = new LinkedHashSet<>();
        fields.add("ID");
        fields.add("NAME");
        fields.add("DESCRIPTION");
        fields.add("CREATED");
        return fields;
    }

    @Override
    public Set<String> getBucketItemFields() {
        final Set<String> fields = new LinkedHashSet<>();
        fields.add("ID");
        fields.add("NAME");
        fields.add("DESCRIPTION");
        fields.add("CREATED");
        fields.add("MODIFIED");
        fields.add("ITEM_TYPE");
        fields.add("BUCKET_ID");
        return fields;
    }

    @Override
    public Set<String> getFlowFields() {
        final Set<String> fields = new LinkedHashSet<>();
        fields.add("ID");
        fields.add("NAME");
        fields.add("DESCRIPTION");
        fields.add("CREATED");
        fields.add("MODIFIED");
        fields.add("ITEM_TYPE");
        fields.add("BUCKET_ID");
        return fields;
    }
}
