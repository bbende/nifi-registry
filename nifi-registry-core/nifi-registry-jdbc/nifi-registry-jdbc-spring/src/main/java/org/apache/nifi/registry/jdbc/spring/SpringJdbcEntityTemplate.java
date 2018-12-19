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
import java.util.TreeMap;
import java.util.TreeSet;

@Component
public class SpringJdbcEntityTemplate implements JdbcEntityTemplate {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SpringJdbcEntityTemplate(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
    }

    @Override
    public <I, E extends Entity<I>> E insert(final Table<I> table, final E entity, final EntityValueMapper<E> entityValueMapper) {
        final String sql = SqlFactory.insert(table);
        final SortedMap<Column,Object> entityValues = getEntityValues(entity, table.getColumns(), entityValueMapper);
        final List<Object> values = getArgs(entityValues);
        jdbcTemplate.update(sql, values.toArray());
        return entity;
    }

    @Override
    public <I, E extends Entity<I>> E update(final Table<I> table, final E entity, final SortedSet<Column> columns,
                                             final EntityValueMapper<E> entityValueMapper) {
        final SortedMap<Column,Object> entityValues = getEntityValues(entity, columns, entityValueMapper);

        final SortedMap<Column,Object> nonNullEntityValues = new TreeMap<>();
        for (Map.Entry<Column,Object> entry : entityValues.entrySet()) {
            if (entry.getValue() != null) {
                nonNullEntityValues.put(entry.getKey(), entry.getValue());
            }
        }

        final String sql = SqlFactory.update(table, nonNullEntityValues);

        // Get the args based on the values map, but then add an additional arg for the id in the where statement
        final List<Object> values = getArgs(nonNullEntityValues);
        values.add(entityValueMapper.map(table.getIdColumn(), entity));

        jdbcTemplate.update(sql, values.toArray());
        return entity;
    }

    @Override
    public <I, E extends Entity<I>> Optional<E> queryForObject(final Table<I> table, final I id,
                                                               final EntityRowMapper<E> entityRowMapper) {
        final String sql = SqlFactory.selectById(table);
        return queryForObject(sql, id, entityRowMapper);
    }

    public <I, E extends Entity<I>> Optional<E> queryForObject(final String sql, final I id,
                                                               final EntityRowMapper<E> entityRowMapper) {
        final RowMapper<E> rowMapper = createSpringRowMapper(entityRowMapper);
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public <I, E extends Entity<I>> List<E> query(final Table<I> table, final SortedMap<Column,Object> args,
                                                  final EntityRowMapper<E> entityRowMapper) {

        final List<Object> argValues = new ArrayList<>();
        final SortedSet<Column> columns = new TreeSet<>();

        for (final Map.Entry<Column,Object> arg : args.entrySet()) {
            columns.add(arg.getKey());
            argValues.add(arg.getValue());
        }

        final QueryBuilder queryBuilder = SqlFactory.query()
                .select(table.getColumns())
                .from(table)
                .whereEqual(columns);

        return query(queryBuilder.build(), argValues, entityRowMapper);
    }

    @Override
    public <I, E extends Entity<I>> List<E> query(final String sql, final List<Object> args,
                                                  final EntityRowMapper<E> entityRowMapper) {
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

    private <I, E extends Entity<I>> SortedMap<Column,Object> getEntityValues(
            final E entity, final SortedSet<Column> columns, final EntityValueMapper<E> entityValueMapper) {
        final SortedMap<Column,Object> values = new TreeMap<>();
        columns.forEach(c -> values.put(c, entityValueMapper.map(c, entity)));
        return values;
    }

    private List<Object> getArgs(final SortedMap<Column,Object> entityValues) {
        final List<Object> values = new ArrayList<>();
        entityValues.entrySet().forEach(e -> values.add(e.getValue()));
        return values;
    }

    private <I, E extends Entity<I>> RowMapper<E> createSpringRowMapper(final EntityRowMapper<E> entityRowMapper) {
        return (rs, rowNum) -> {
          return entityRowMapper.mapRow(rs, rowNum);
        };
    }
}
