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
package org.apache.nifi.registry.db.jdbc.repository.impl;

import org.apache.nifi.registry.db.entity.BucketItemEntity;
import org.apache.nifi.registry.db.entity.FlowEntity;
import org.apache.nifi.registry.db.jdbc.mapper.BucketItemMapper;
import org.apache.nifi.registry.db.jdbc.mapper.FlowMapper;
import org.apache.nifi.registry.db.jdbc.repository.FlowRepository;
import org.apache.nifi.registry.jdbc.api.Column;
import org.apache.nifi.registry.jdbc.api.JdbcEntityTemplate;
import org.apache.nifi.registry.jdbc.api.QueryBuilder;
import org.apache.nifi.registry.jdbc.api.QueryOperator;
import org.apache.nifi.registry.jdbc.api.Table;
import org.apache.nifi.registry.jdbc.api.TableConfiguration;
import org.apache.nifi.registry.jdbc.commons.StandardQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.SortedSet;

@Repository
public class StandardFlowRepository implements FlowRepository {

    private static final FlowMapper FLOW_MAPPER = new FlowMapper();
    private static final BucketItemMapper BUCKET_ITEM_MAPPER = new BucketItemMapper();

    private final JdbcEntityTemplate jdbcEntityTemplate;
    private final TableConfiguration tableConfiguration;

    private final Table<String> bucketItemTable;
    private final Table<String> flowTable;
    private final QueryBuilder baseSelectFlowQuery;

    @Autowired
    public StandardFlowRepository(final TableConfiguration tableConfiguration, final JdbcEntityTemplate jdbcEntityTemplate) {
        this.jdbcEntityTemplate = Objects.requireNonNull(jdbcEntityTemplate);
        this.tableConfiguration = Objects.requireNonNull(tableConfiguration);

        this.bucketItemTable = tableConfiguration.getTable(BucketItemEntity.class);
        this.flowTable = tableConfiguration.getTable(FlowEntity.class);

        if (this.bucketItemTable == null || this.flowTable == null) {
            throw new IllegalStateException("BucketItemEntity and FlowEntity must be mapped to Tables");
        }

        final String whereBucketItemIdEqualsFlowId =
                bucketItemTable.getAlias() + "." + bucketItemTable.getIdColumn().getName()
                        + " " + QueryOperator.EQ.getOperator() + " "
                        + flowTable.getAlias() + "." + flowTable.getIdColumn().getName();

        baseSelectFlowQuery = new StandardQueryBuilder()
                .select(bucketItemTable, bucketItemTable.getColumns())
                .from(bucketItemTable)
                .from(flowTable)
                .where(whereBucketItemIdEqualsFlowId);
    }

    @Override
    public FlowEntity create(final FlowEntity entity) {
        jdbcEntityTemplate.insert(bucketItemTable, entity, BUCKET_ITEM_MAPPER);
        jdbcEntityTemplate.insert(flowTable, entity, FLOW_MAPPER);
        return entity;
    }

    @Override
    public FlowEntity update(final FlowEntity entity) {
        final SortedSet<Column> updatableColumns = bucketItemTable.getUpdatableColumns();
        return jdbcEntityTemplate.update(bucketItemTable, entity, updatableColumns, FLOW_MAPPER);
    }

    @Override
    public Optional<FlowEntity> findById(final String id) {
        final String sql = baseSelectFlowQuery
                .copy()
                .whereEqual(flowTable, flowTable.getIdColumn())
                .build();

        return jdbcEntityTemplate.queryForObject(sql, id, FLOW_MAPPER);
    }

    @Override
    public boolean existsById(final String id) {
        return findById(id).isPresent();
    }

    @Override
    public List<FlowEntity> findAll() {
        final String sql = baseSelectFlowQuery.build();
        return jdbcEntityTemplate.query(sql, Collections.emptyList(), FLOW_MAPPER);
    }

    @Override
    public List<FlowEntity> findAllById(final Iterable<String> ids) {
        final List<Object> args = new ArrayList<>();
        ids.forEach(id -> args.add(id));

        final QueryBuilder queryBuilder = baseSelectFlowQuery
                .copy()
                .whereIn(flowTable, flowTable.getIdColumn(), args.size());

        return jdbcEntityTemplate.query(queryBuilder.build(), args, FLOW_MAPPER);
    }

    @Override
    public List<FlowEntity> findByFields(final SortedMap<Column, Object> params) {
        final List<Object> argValues = new ArrayList<>();

        final QueryBuilder queryBuilder = baseSelectFlowQuery.copy();
        for (final Map.Entry<Column,Object> arg : params.entrySet()) {
            final Column column = arg.getKey();
            queryBuilder.whereEqual(bucketItemTable, column);
            argValues.add(arg.getValue());
        }

        return jdbcEntityTemplate.query(queryBuilder.build(), argValues, FLOW_MAPPER);
    }

    @Override
    public void deleteById(final String id) {
        jdbcEntityTemplate.deleteById(flowTable, id);
        jdbcEntityTemplate.deleteById(bucketItemTable, id);
    }

    @Override
    public void delete(final FlowEntity entity) {
        jdbcEntityTemplate.deleteByEntity(flowTable, entity);
        jdbcEntityTemplate.deleteByEntity(bucketItemTable, entity);
    }
}
