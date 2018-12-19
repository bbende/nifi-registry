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
import org.apache.nifi.registry.jdbc.api.JdbcRepository;
import org.apache.nifi.registry.jdbc.api.QueryParameters;
import org.apache.nifi.registry.jdbc.api.Table;
import org.apache.nifi.registry.jdbc.api.TableConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;

public abstract class AbstractJdbcRepository<I, E extends Entity<I>> implements JdbcRepository<I, E> {

    protected final Table<I> table;
    protected final Class<E> entityClass;
    protected final TableConfiguration tableConfiguration;
    protected final EntityValueMapper<E> entityValueMapper;
    protected final EntityRowMapper<E> entityRowMapper;
    protected final JdbcEntityTemplate jdbcEntityTemplate;

    public AbstractJdbcRepository(final Class<E> entityClass,
                                  final TableConfiguration tableConfiguration,
                                  final EntityValueMapper<E> entityValueMapper,
                                  final EntityRowMapper<E> entityRowMapper,
                                  final JdbcEntityTemplate jdbcEntityTemplate) {
        this.entityClass = Objects.requireNonNull(entityClass);
        this.tableConfiguration = Objects.requireNonNull(tableConfiguration);
        this.entityValueMapper = Objects.requireNonNull(entityValueMapper);
        this.entityRowMapper = Objects.requireNonNull(entityRowMapper);
        this.jdbcEntityTemplate = Objects.requireNonNull(jdbcEntityTemplate);

        this.table = tableConfiguration.getTable(entityClass);
        if (this.table == null) {
            throw new IllegalStateException("Entity class must be registered to a Table");
        }
    }

    @Override
    public E create(final E entity) {
        if (entity.getId() == null) {
            final Optional<IDGenerator<I>> idGenerator = table.getIDGenerator();
            if (idGenerator.isPresent()) {
                final I id = idGenerator.get().generate();
                entity.setId(id);
            }
        }
        return jdbcEntityTemplate.insert(table, entity, entityValueMapper);
    }

    @Override
    public E update(final E entity) {
        final SortedSet<Column> columnsToUpdate = table.getUpdatableColumns();
        if (columnsToUpdate.contains(table.getIdColumn())) {
            columnsToUpdate.remove(table.getIdColumn());
        }
        if (columnsToUpdate.isEmpty()) {
            throw new IllegalStateException("This repository does not support updating columns");
        }
        return jdbcEntityTemplate.update(table, entity, columnsToUpdate, entityValueMapper);
    }

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
    public List<E> findAll() {
        return jdbcEntityTemplate.query(table, StandardQueryParameters.empty(), entityRowMapper);
    }

    @Override
    public List<E> findAllById(final Iterable<I> ids) {
        final List<Object> idArgs = new ArrayList<>();
        ids.forEach(i -> idArgs.add(i));

        final String sql = SqlFactory.query()
                .select(table.getColumns())
                .from(table)
                .whereIn(table.getIdColumn(), idArgs.size())
                .build();

        return jdbcEntityTemplate.query(sql, idArgs, entityRowMapper);
    }

    @Override
    public List<E> findByQueryParams(final QueryParameters params) {
        return jdbcEntityTemplate.query(table, params, entityRowMapper);
    }

    @Override
    public void deleteById(final I i) {
        jdbcEntityTemplate.deleteById(table, i);
    }

    @Override
    public void delete(final E entity) {
        jdbcEntityTemplate.deleteByEntity(table, entity);
    }

}
