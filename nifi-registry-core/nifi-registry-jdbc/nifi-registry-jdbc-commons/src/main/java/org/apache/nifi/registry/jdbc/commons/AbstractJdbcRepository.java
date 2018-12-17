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
package org.apache.nifi.registry.jdbc.commons;

import org.apache.nifi.registry.jdbc.api.Column;
import org.apache.nifi.registry.jdbc.api.Entity;
import org.apache.nifi.registry.jdbc.api.EntityRowMapper;
import org.apache.nifi.registry.jdbc.api.EntityValueMapper;
import org.apache.nifi.registry.jdbc.api.IDGenerator;
import org.apache.nifi.registry.jdbc.api.JdbcEntityTemplate;
import org.apache.nifi.registry.jdbc.api.Repository;
import org.apache.nifi.registry.jdbc.api.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeMap;

public abstract class AbstractJdbcRepository<I, E extends Entity<I>> implements Repository<I, E> {

    protected final Table<I> table;
    protected final EntityValueMapper<I,E> entityValueMapper;
    protected final EntityRowMapper<I,E> entityRowMapper;
    protected final JdbcEntityTemplate jdbcEntityTemplate;

    public AbstractJdbcRepository(final Table<I> table,
                                  final EntityValueMapper<I, E> entityValueMapper,
                                  final EntityRowMapper<I, E> entityRowMapper,
                                  final JdbcEntityTemplate jdbcEntityTemplate) {
        this.table = table;
        this.entityValueMapper = entityValueMapper;
        this.entityRowMapper = entityRowMapper;
        this.jdbcEntityTemplate = jdbcEntityTemplate;
    }

    @Override
    public E create(final E entity) {
        ensureTablesMatch(entity);

        if (entity.getId() == null) {
            final Optional<IDGenerator<I>> idGenerator = table.getIDGenerator();
            if (idGenerator.isPresent()) {
                final I id = idGenerator.get().generate();
                entity.setId(id);
            }
        }
        return jdbcEntityTemplate.insert(entity, entityValueMapper);
    }

    @Override
    public E update(final E entity) {
        ensureTablesMatch(entity);
        final SortedSet<Column> columnsToUpdate = getColumnsToUpdate(entity);
        if (columnsToUpdate.isEmpty()) {
            throw new IllegalStateException("This repository does not support updating columns");
        }
        return jdbcEntityTemplate.update(entity, columnsToUpdate, entityValueMapper);
    }

    protected abstract SortedSet<Column> getColumnsToUpdate(final E entity);

    @Override
    public Optional<E> findById(final I i) {
        return jdbcEntityTemplate.queryForObject(table, i, entityRowMapper);
    }

    @Override
    public boolean existsById(final I i) {
        final Optional<E> optional = jdbcEntityTemplate.queryForObject(table, i, entityRowMapper);
        return optional.isPresent();
    }

    @Override
    public Iterable<E> findAll() {
        return jdbcEntityTemplate.query(table, new TreeMap<>(), entityRowMapper);
    }

    @Override
    public Iterable<E> findAllById(final Iterable<I> ids) {
        final List<Object> idArgs = new ArrayList<>();
        ids.forEach(i -> idArgs.add(i));

        final String sql = SqlFactory.query()
                .select(table, table.getColumns())
                .from(table)
                .whereIn(table, table.getIdColumn(), idArgs.size())
                .build();

        return jdbcEntityTemplate.query(sql, idArgs, entityRowMapper);
    }

    @Override
    public void deleteById(final I i) {
        jdbcEntityTemplate.deleteById(table, i);
    }

    @Override
    public void delete(final E entity) {
        ensureTablesMatch(entity);
        jdbcEntityTemplate.deleteByEntity(entity);
    }

    private void ensureTablesMatch(final E entity) {
        final String repositoryTableName = this.table.getName();
        final String entityTableName = entity.getTable().getName();
        if (!entityTableName.equals(repositoryTableName)) {
            throw new IllegalArgumentException("Repository table name '"
                    + repositoryTableName + "' must match entity table name '" + entityTableName + "'" );
        }
    }

}
