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
package org.apache.nifi.registry.db.jdbc;

import org.apache.nifi.registry.db.entity.ExtensionEntity;
import org.apache.nifi.registry.jdbc.api.Column;
import org.apache.nifi.registry.jdbc.api.JdbcEntityTemplate;
import org.apache.nifi.registry.jdbc.commons.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.SortedSet;

@Repository
public class ExtensionRepository extends AbstractJdbcRepository<String, ExtensionEntity> {

    private static final ExtensionMapper mapper = new ExtensionMapper();

    @Autowired
    public ExtensionRepository(final JdbcEntityTemplate jdbcEntityTemplate) {
        super(Tables.EXTENSION, mapper, mapper, jdbcEntityTemplate);
    }

    @Override
    protected SortedSet<Column> getColumnsToUpdate(final ExtensionEntity entity) {
        return Collections.emptySortedSet();
    }

}
