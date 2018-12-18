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
package org.apache.nifi.registry.jdbc.spring;

import org.apache.nifi.registry.jdbc.api.Column;
import org.apache.nifi.registry.jdbc.api.Entity;
import org.apache.nifi.registry.jdbc.api.EntityRowMapper;
import org.apache.nifi.registry.jdbc.api.EntityValueMapper;
import org.apache.nifi.registry.jdbc.api.JdbcEntityTemplate;
import org.apache.nifi.registry.jdbc.api.QueryBuilder;
import org.apache.nifi.registry.jdbc.api.Table;
import org.apache.nifi.registry.jdbc.commons.SqlFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.SortedSet;

@Component
public class SpringJdbcEntityTemplate implements JdbcEntityTemplate {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SpringJdbcEntityTemplate(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
    }

    @Override
    public <I, E extends Entity<I>> E insert(final Table<I> table, final E entity, final EntityValueMapper<I, E> entityValueMapper) {
        final String sql = SqlFactory.insert(table);
        return update(sql, entity, table.getColumns(), entityValueMapper);
    }

    @Override
    public <I, E extends Entity<I>> E update(final Table<I> table, final E entity, final SortedSet<Column> columns,
                                             final EntityValueMapper<I, E> entityValueMapper) {
        final String sql = SqlFactory.update(table, columns);
        return update(sql, entity, columns, entityValueMapper);
    }

    @Override
    public <I, E extends Entity<I>> E update(final String sql, final E entity,
                                             final SortedSet<Column> columns, final EntityValueMapper<I, E> entityValueMapper) {
        final Object[] values = getEntityValues(entity, columns, entityValueMapper);
        jdbcTemplate.update(sql, values);
        return entity;
    }

    @Override
    public <I, E extends Entity<I>> Optional<E> queryForObject(final Table<I> table, final I id,
                                                               final EntityRowMapper<I, E> entityRowMapper) {
        final String sql = SqlFactory.selectById(table);
        final RowMapper<E> rowMapper = createSpringRowMapper(entityRowMapper);
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public <I, E extends Entity<I>> List<E> query(final Table<I> table, final SortedMap<Column,Object> args,
                                                  final EntityRowMapper<I, E> entityRowMapper) {

        final List<Object> argValues = new ArrayList<>();

        final QueryBuilder queryBuilder = SqlFactory.query()
                .select(table, table.getColumns())
                .from(table);

        for (final Map.Entry<Column,Object> arg : args.entrySet()) {
            final Column column = arg.getKey();
            queryBuilder.whereEqual(table, column);
            argValues.add(arg.getValue());
        }

        return query(queryBuilder.build(), argValues, entityRowMapper);
    }

    @Override
    public <I, E extends Entity<I>> List<E> query(final String sql, final List<Object> args, final EntityRowMapper<I, E> entityRowMapper) {
        final RowMapper<E> rowMapper = createSpringRowMapper(entityRowMapper);
        return jdbcTemplate.query(sql, args.toArray(), rowMapper);
    }

    @Override
    public <I, E extends Entity<I>> void deleteByEntity(final Table<I> table, final E entity) {
        final String sql = SqlFactory.delete(table);
        jdbcTemplate.update(sql, new Object[]{entity.getId()});
    }

    @Override
    public <I, E extends Entity<I>> void deleteById(final Table<I> table, final I id) {
        final String sql = SqlFactory.delete(table);
        jdbcTemplate.update(sql, new Object[]{id});
    }

    private <I, E extends Entity<I>> Object[] getEntityValues(final E entity, final SortedSet<Column> columns,
                                                                final EntityValueMapper<I, E> entityValueMapper) {
        final List<Object> values = new ArrayList<>();
        columns.forEach(c -> values.add(entityValueMapper.map(c, entity)));
        return values.toArray();
    }

    private <I, E extends Entity<I>> RowMapper<E> createSpringRowMapper(final EntityRowMapper<I, E> entityRowMapper) {
        return (rs, rowNum) -> {
          return entityRowMapper.mapRow(rs, rowNum);
        };
    }
}
