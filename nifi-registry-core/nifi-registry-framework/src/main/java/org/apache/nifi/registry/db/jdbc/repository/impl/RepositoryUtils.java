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

import org.apache.nifi.registry.db.jdbc.configuration.Tables;
import org.apache.nifi.registry.jdbc.api.JdbcEntityTemplate;
import org.apache.nifi.registry.jdbc.commons.SqlFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class RepositoryUtils {

    public static Map<String,Long> getFlowSnapshotCounts(final JdbcEntityTemplate jdbcEntityTemplate) {
        final String sql = SqlFactory
                .query()
                .select(Tables.FLOW_SNAPSHOT.FLOW_ID)
                .selectCount(Tables.FLOW_SNAPSHOT.FLOW_ID)
                .from(Tables.FLOW_SNAPSHOT)
                .groupBy(Tables.FLOW_SNAPSHOT.FLOW_ID)
                .build();

        return getCountMap(jdbcEntityTemplate, sql);
    }

    public static Long getFlowSnapshotCount(final JdbcEntityTemplate jdbcEntityTemplate, final String flowIdentifier) {
        final String sql = SqlFactory.query()
                .selectCount(Tables.FLOW_SNAPSHOT.FLOW_ID)
                .from(Tables.FLOW_SNAPSHOT)
                .whereEqual(Tables.FLOW_SNAPSHOT.FLOW_ID)
                .build();

        return getCountForId(jdbcEntityTemplate, flowIdentifier, sql);
    }

    public static Map<String,Long> getExtensionBundleVersionCounts(final JdbcEntityTemplate jdbcEntityTemplate) {
        final String sql = SqlFactory
                .query()
                .select(Tables.EXTENSION_BUNDLE_VERSION.EXTENSION_BUNDLE_ID)
                .selectCount(Tables.EXTENSION_BUNDLE_VERSION.EXTENSION_BUNDLE_ID)
                .from(Tables.EXTENSION_BUNDLE_VERSION)
                .groupBy(Tables.EXTENSION_BUNDLE_VERSION.EXTENSION_BUNDLE_ID)
                .build();

        return getCountMap(jdbcEntityTemplate, sql);
    }

    public static Long getExtensionBundleVersionCount(final JdbcEntityTemplate jdbcEntityTemplate,
                                                final String extensionBundleIdentifier) {
        final String sql = SqlFactory.query()
                .selectCount(Tables.EXTENSION_BUNDLE_VERSION.EXTENSION_BUNDLE_ID)
                .from(Tables.EXTENSION_BUNDLE_VERSION)
                .whereEqual(Tables.EXTENSION_BUNDLE_VERSION.EXTENSION_BUNDLE_ID)
                .build();

        return getCountForId(jdbcEntityTemplate, extensionBundleIdentifier, sql);
    }

    public static Map<String, Long> getCountMap(final JdbcEntityTemplate jdbcEntityTemplate, final String sql) {
        final Map<String, Long> results = new HashMap<>();
        jdbcEntityTemplate.query(sql, Collections.emptyList(), (rs) -> {
            results.put(rs.getString(1), rs.getLong(2));
        });
        return results;
    }

    public static Long getCountForId(final JdbcEntityTemplate jdbcEntityTemplate, final String identifier, final String sql) {
        final AtomicReference<Long> longHolder = new AtomicReference<>(null);
        jdbcEntityTemplate.query(sql, Collections.singletonList(identifier), (rs) -> {
            longHolder.set(rs.getLong(1));
        });

        return longHolder.get();
    }
}
