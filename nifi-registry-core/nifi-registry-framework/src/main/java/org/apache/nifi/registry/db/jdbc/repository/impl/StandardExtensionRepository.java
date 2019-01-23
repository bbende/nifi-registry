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

import org.apache.nifi.registry.db.entity.ExtensionEntity;
import org.apache.nifi.registry.db.jdbc.mapper.ExtensionMapper;
import org.apache.nifi.registry.db.jdbc.repository.ExtensionRepository;
import org.apache.nifi.registry.jdbc.api.IDGenerator;
import org.apache.nifi.registry.jdbc.api.JdbcEntityTemplate;
import org.apache.nifi.registry.jdbc.api.TableConfiguration;
import org.apache.nifi.registry.jdbc.commons.AbstractJdbcRepository;
import org.apache.nifi.registry.jdbc.commons.UUIDStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class StandardExtensionRepository extends AbstractJdbcRepository<String, ExtensionEntity> implements ExtensionRepository {

    private static final ExtensionMapper MAPPER = new ExtensionMapper();

    @Autowired
    public StandardExtensionRepository(final JdbcEntityTemplate jdbcEntityTemplate,
                                       final TableConfiguration tableConfiguration) {
        super(ExtensionEntity.class, tableConfiguration, MAPPER, MAPPER, jdbcEntityTemplate);
    }

    @Override
    protected Optional<IDGenerator<String>> getIDGenerator() {
        return Optional.of(new UUIDStringGenerator());
    }
}
