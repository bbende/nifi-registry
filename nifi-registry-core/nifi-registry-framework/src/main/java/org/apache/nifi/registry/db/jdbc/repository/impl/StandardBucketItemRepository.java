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
import org.apache.nifi.registry.db.entity.BucketItemEntityType;
import org.apache.nifi.registry.db.entity.ExtensionBundleEntity;
import org.apache.nifi.registry.db.entity.FlowEntity;
import org.apache.nifi.registry.db.jdbc.configuration.Tables;
import org.apache.nifi.registry.db.jdbc.mapper.BucketItemMapper;
import org.apache.nifi.registry.db.jdbc.repository.BucketItemRepository;
import org.apache.nifi.registry.jdbc.api.JdbcEntityTemplate;
import org.apache.nifi.registry.jdbc.api.QueryBuilder;
import org.apache.nifi.registry.jdbc.api.Table;
import org.apache.nifi.registry.jdbc.api.TableConfiguration;
import org.apache.nifi.registry.jdbc.commons.SqlFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Repository
public class StandardBucketItemRepository implements BucketItemRepository {

    private static final BucketItemMapper BUCKET_ITEM_MAPPER = new BucketItemMapper();

    private final JdbcEntityTemplate jdbcEntityTemplate;

    private final Table<String> bucketTable;
    private final Table<String> bucketItemTable;
    private final Table<String> flowTable;
    private final Table<String> extensionBundleTable;

    private final QueryBuilder baseSelectItemsQuery;

    @Autowired
    public StandardBucketItemRepository(final TableConfiguration tableConfiguration,
                                        final JdbcEntityTemplate jdbcEntityTemplate) {
        this.jdbcEntityTemplate = Objects.requireNonNull(jdbcEntityTemplate);

        Objects.requireNonNull(tableConfiguration);

        this.bucketTable = tableConfiguration.getTable(BucketEntity.class);
        if (this.bucketTable == null) {
            throw new IllegalStateException("BucketEntity must be mapped to a Table");
        }

        this.bucketItemTable = tableConfiguration.getTable(BucketItemEntity.class);
        if (this.bucketItemTable == null) {
            throw new IllegalStateException("BucketItemEntity must be mapped to a Table");
        }

        this.flowTable = tableConfiguration.getTable(FlowEntity.class);
        if (this.flowTable == null) {
            throw new IllegalStateException("FlowEntity must be mapped to a Table");
        }

        this.extensionBundleTable = tableConfiguration.getTable(ExtensionBundleEntity.class);
        if (this.extensionBundleTable == null) {
            throw new IllegalStateException("ExtensionBundleEntity must be mapped to a Table");
        }

        baseSelectItemsQuery = SqlFactory
                .query()
                    .select(Tables.BUCKET.NAME)
                    .select(bucketItemTable.getColumns())
                    .select(extensionBundleTable.getColumns())
                .from(bucketItemTable)
                .innerJoin(bucketTable, Tables.BUCKET_ITEM.BUCKET_ID, bucketTable.getIdColumn())
                .leftJoin(extensionBundleTable, bucketItemTable.getIdColumn(), extensionBundleTable.getIdColumn());
    }

    @Override
    public List<BucketItemEntity> getBucketItems(final String bucketIdentifier) {
        final String sql = baseSelectItemsQuery.copy()
                .whereEqual(Tables.BUCKET_ITEM.BUCKET_ID)
                .build();

        final List<BucketItemEntity> items = jdbcEntityTemplate.query(sql, Collections.singletonList(bucketIdentifier), BUCKET_ITEM_MAPPER);
        return getItemsWithCounts(items);
    }

    @Override
    public List<BucketItemEntity> getBucketItems(final Set<String> bucketIds) {
        final List<Object> idArgs = new ArrayList<>(bucketIds);

        final String sql = baseSelectItemsQuery.copy()
                .whereIn(Tables.BUCKET_ITEM.BUCKET_ID, idArgs.size())
                .build();

        final List<BucketItemEntity> items = jdbcEntityTemplate.query(sql, idArgs, BUCKET_ITEM_MAPPER);
        return getItemsWithCounts(items);
    }

    private List<BucketItemEntity> getItemsWithCounts(final Iterable<BucketItemEntity> items) {
        final Map<String,Long> snapshotCounts = RepositoryUtils.getFlowSnapshotCounts(jdbcEntityTemplate);
        final Map<String,Long> extensionBundleVersionCounts = RepositoryUtils.getExtensionBundleVersionCounts(jdbcEntityTemplate);

        final List<BucketItemEntity> itemWithCounts = new ArrayList<>();
        for (final BucketItemEntity item : items) {
            if (item.getType() == BucketItemEntityType.FLOW) {
                final Long snapshotCount = snapshotCounts.get(item.getId());
                if (snapshotCount != null) {
                    final FlowEntity flowEntity = (FlowEntity) item;
                    flowEntity.setSnapshotCount(snapshotCount);
                }
            } else if (item.getType() == BucketItemEntityType.EXTENSION_BUNDLE) {
                final Long versionCount = extensionBundleVersionCounts.get(item.getId());
                if (versionCount != null) {
                    final ExtensionBundleEntity extensionBundleEntity = (ExtensionBundleEntity) item;
                    extensionBundleEntity.setVersionCount(versionCount);
                }
            }

            itemWithCounts.add(item);
        }

        return itemWithCounts;
    }

}
