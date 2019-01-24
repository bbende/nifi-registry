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

import org.apache.nifi.registry.db.entity.BucketEntity;
import org.apache.nifi.registry.db.entity.BucketItemEntity;
import org.apache.nifi.registry.db.entity.ExtensionBundleEntity;
import org.apache.nifi.registry.db.jdbc.mapper.BucketItemMapper;
import org.apache.nifi.registry.db.jdbc.mapper.ExtensionBundleMapper;
import org.apache.nifi.registry.db.jdbc.repository.ExtensionBundleRepository;
import org.apache.nifi.registry.jdbc.api.JdbcEntityTemplate;
import org.apache.nifi.registry.jdbc.api.QueryBuilder;
import org.apache.nifi.registry.jdbc.api.QueryParameters;
import org.apache.nifi.registry.jdbc.api.Table;
import org.apache.nifi.registry.jdbc.api.TableConfiguration;
import org.apache.nifi.registry.jdbc.commons.SqlFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class StandardExtensionBundleRepository implements ExtensionBundleRepository {

    private static final ExtensionBundleMapper EXTENSION_BUNDLE_MAPPER = new ExtensionBundleMapper();
    private static final BucketItemMapper BUCKET_ITEM_MAPPER = new BucketItemMapper();

    private final JdbcEntityTemplate jdbcEntityTemplate;

    private final Table bucketTable;
    private final Table bucketItemTable;
    private final Table extensionBundleTable;
    private final QueryBuilder baseSelectFlowQuery;

    @Autowired
    public StandardExtensionBundleRepository(final TableConfiguration tableConfiguration,
                                             final JdbcEntityTemplate jdbcEntityTemplate) {
        this.jdbcEntityTemplate = Objects.requireNonNull(jdbcEntityTemplate);

        Objects.requireNonNull(tableConfiguration);
        this.bucketTable = tableConfiguration.getTable(BucketEntity.class);
        this.bucketItemTable = tableConfiguration.getTable(BucketItemEntity.class);
        this.extensionBundleTable = tableConfiguration.getTable(ExtensionBundleEntity.class);

        if (this.bucketTable == null || this.bucketItemTable == null || this.extensionBundleTable == null) {
            throw new IllegalStateException("BucketEntity, BucketItemEntity, and ExtensionBundleEntity must be mapped to Tables");
        }

        baseSelectFlowQuery = SqlFactory.query()
                .select(bucketTable.getColumns())
                .select(bucketItemTable.getColumns())
                .select(extensionBundleTable.getColumns())
                .from(bucketTable, bucketItemTable, extensionBundleTable)
                .whereEqual(bucketTable.getIdColumn(), bucketItemTable.getIdColumn())
                .whereEqual(bucketItemTable.getIdColumn(), extensionBundleTable.getIdColumn());
    }

    @Override
    public ExtensionBundleEntity create(final ExtensionBundleEntity entity) {
        jdbcEntityTemplate.insert(bucketItemTable, entity, BUCKET_ITEM_MAPPER);
        jdbcEntityTemplate.insert(extensionBundleTable, entity, EXTENSION_BUNDLE_MAPPER);
        return entity;
    }

    @Override
    public ExtensionBundleEntity update(final ExtensionBundleEntity entity) {
        throw new UnsupportedOperationException("ExtensionBundles cannot be updated");
    }

    @Override
    public Optional<ExtensionBundleEntity> findById(final String id) {
        final String sql = baseSelectFlowQuery.copy()
                .whereEqual(extensionBundleTable.getIdColumn())
                .build();

        return jdbcEntityTemplate.queryForObject(sql, Collections.singletonList(id), EXTENSION_BUNDLE_MAPPER);
    }

    @Override
    public boolean existsById(final String s) {
        return false;
    }

    @Override
    public List<ExtensionBundleEntity> findAll() {
        return null;
    }

    @Override
    public List<ExtensionBundleEntity> findAllById(final Collection<String> strings) {
        return null;
    }

    @Override
    public List<ExtensionBundleEntity> findByQueryParams(final QueryParameters params) {
        return null;
    }

    @Override
    public void deleteById(final String s) {

    }

    @Override
    public void delete(final ExtensionBundleEntity entity) {

    }
}
