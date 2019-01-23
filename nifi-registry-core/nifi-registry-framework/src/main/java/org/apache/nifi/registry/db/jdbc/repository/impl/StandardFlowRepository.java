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
import org.apache.nifi.registry.db.jdbc.configuration.Tables;
import org.apache.nifi.registry.db.jdbc.mapper.BucketItemMapper;
import org.apache.nifi.registry.db.jdbc.mapper.FlowMapper;
import org.apache.nifi.registry.db.jdbc.repository.FlowRepository;
import org.apache.nifi.registry.jdbc.api.Column;
import org.apache.nifi.registry.jdbc.api.JdbcEntityTemplate;
import org.apache.nifi.registry.jdbc.api.QueryBuilder;
import org.apache.nifi.registry.jdbc.api.QueryParameters;
import org.apache.nifi.registry.jdbc.api.Table;
import org.apache.nifi.registry.jdbc.api.TableConfiguration;
import org.apache.nifi.registry.jdbc.commons.SqlFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;

import static org.apache.nifi.registry.jdbc.commons.StandardQueryParameter.eq;
import static org.apache.nifi.registry.jdbc.commons.StandardQueryParameters.of;

@Repository
public class StandardFlowRepository implements FlowRepository {

    private static final FlowMapper FLOW_MAPPER = new FlowMapper();
    private static final BucketItemMapper BUCKET_ITEM_MAPPER = new BucketItemMapper();

    private final JdbcEntityTemplate jdbcEntityTemplate;

    private final Table bucketItemTable;
    private final Table flowTable;
    private final QueryBuilder baseSelectFlowQuery;

    @Autowired
    public StandardFlowRepository(final TableConfiguration tableConfiguration, final JdbcEntityTemplate jdbcEntityTemplate) {
        this.jdbcEntityTemplate = Objects.requireNonNull(jdbcEntityTemplate);

        Objects.requireNonNull(tableConfiguration);
        this.bucketItemTable = tableConfiguration.getTable(BucketItemEntity.class);
        this.flowTable = tableConfiguration.getTable(FlowEntity.class);

        if (this.bucketItemTable == null || this.flowTable == null) {
            throw new IllegalStateException("BucketItemEntity and FlowEntity must be mapped to Tables");
        }

        baseSelectFlowQuery = SqlFactory.query()
                .select(bucketItemTable.getColumns())
                .select(flowTable.getColumns())
                .from(bucketItemTable, flowTable)
                .whereEqual(bucketItemTable.getIdColumn(), flowTable.getIdColumn());
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
        jdbcEntityTemplate.update(bucketItemTable, entity, updatableColumns, BUCKET_ITEM_MAPPER);
        return entity;
    }

    @Override
    public Optional<FlowEntity> findById(final String id) {
        final String sql = baseSelectFlowQuery.copy()
                .whereEqual(flowTable.getIdColumn())
                .build();

        return jdbcEntityTemplate.queryForObject(sql, Collections.singletonList(id), FLOW_MAPPER);
    }

    @Override
    public Optional<FlowEntity> findByIdWithSnapshotCounts(final String flowIdentifier) {
        final Optional<FlowEntity> flowEntity = findById(flowIdentifier);
        if (!flowEntity.isPresent()) {
            return flowEntity;
        }

        final Long snapshotCount = RepositoryUtils.getFlowSnapshotCount(jdbcEntityTemplate, flowIdentifier);
        if (snapshotCount != null) {
            flowEntity.get().setSnapshotCount(snapshotCount);
        }

        return flowEntity;
    }

    @Override
    public List<FlowEntity> findByBucket(final String bucketIdentifier) {
        final QueryParameters params = of(
                eq(Tables.BUCKET_ITEM.BUCKET_ID, bucketIdentifier)
        );

        final List<FlowEntity> flows = findByQueryParams(params);

        final Map<String,Long> snapshotCounts = RepositoryUtils.getFlowSnapshotCounts(jdbcEntityTemplate);
        for (final FlowEntity flowEntity : flows) {
            final Long snapshotCount = snapshotCounts.get(flowEntity.getId());
            if (snapshotCount != null) {
                flowEntity.setSnapshotCount(snapshotCount);
            }
        }

        return flows;
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
    public List<FlowEntity> findAllById(final Collection<String> ids) {
        final List<Object> args = new ArrayList<>();
        ids.forEach(id -> args.add(id));

        final QueryBuilder queryBuilder = baseSelectFlowQuery.copy()
                .whereIn(flowTable.getIdColumn(), args.size());

        return jdbcEntityTemplate.query(queryBuilder.build(), args, FLOW_MAPPER);
    }

    @Override
    public List<FlowEntity> findByQueryParams(final QueryParameters params) {
        final List<Object> argValues = params.getValues();
        final SortedSet<Column> columns = params.getColumns();

        final QueryBuilder queryBuilder = baseSelectFlowQuery.copy()
                .whereEqual(columns);

        return jdbcEntityTemplate.query(queryBuilder.build(), argValues, FLOW_MAPPER);
    }

    @Override
    public void deleteById(final String id) {
        jdbcEntityTemplate.deleteById(flowTable, id, FLOW_MAPPER);
        jdbcEntityTemplate.deleteById(bucketItemTable, id, FLOW_MAPPER);
    }

    @Override
    public void delete(final FlowEntity entity) {
        jdbcEntityTemplate.deleteByEntity(flowTable, entity);
        jdbcEntityTemplate.deleteByEntity(bucketItemTable, entity);
    }
}
