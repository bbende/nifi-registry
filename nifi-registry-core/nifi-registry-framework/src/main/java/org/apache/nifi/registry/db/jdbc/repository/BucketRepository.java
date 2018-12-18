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
package org.apache.nifi.registry.db.jdbc.repository;

import org.apache.nifi.registry.db.entity.BucketEntity;
import org.apache.nifi.registry.db.jdbc.configuration.BucketColumns;
import org.apache.nifi.registry.db.jdbc.mapper.BucketMapper;
import org.apache.nifi.registry.jdbc.api.Column;
import org.apache.nifi.registry.jdbc.api.JdbcEntityTemplate;
import org.apache.nifi.registry.jdbc.api.TableConfiguration;
import org.apache.nifi.registry.jdbc.commons.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

@Repository
public class BucketRepository extends AbstractJdbcRepository<String, BucketEntity> {

    private static final BucketMapper MAPPER = new BucketMapper();

    private final SortedSet<Column> updateColumns;

    @Autowired
    public BucketRepository(final TableConfiguration tableConfiguration, final JdbcEntityTemplate jdbcEntityTemplate) {
        super(BucketEntity.class, tableConfiguration, MAPPER, MAPPER, jdbcEntityTemplate);

        this.updateColumns = new TreeSet<>();
        this.updateColumns.add(BucketColumns.NAME);
        this.updateColumns.add(BucketColumns.DESCRIPTION);
        this.updateColumns.add(BucketColumns.ALLOW_EXTENSION_BUNDLE_REDEPLOY);
    }

    @Override
    protected SortedSet<Column> getColumnsToUpdate(final BucketEntity entity) {
        return Collections.unmodifiableSortedSet(updateColumns);
    }
}
