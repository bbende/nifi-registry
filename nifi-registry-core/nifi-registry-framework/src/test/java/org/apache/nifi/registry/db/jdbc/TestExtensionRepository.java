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

import org.apache.nifi.registry.db.DatabaseBaseTest;
import org.apache.nifi.registry.db.entity.ExtensionEntity;
import org.apache.nifi.registry.db.entity.ExtensionEntityCategory;
import org.apache.nifi.registry.jdbc.api.Repository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestExtensionRepository extends DatabaseBaseTest {

    @Autowired
    private Repository<String, ExtensionEntity> repository;

    @Test
    public void testCreateAndRetrieve() {
        final ExtensionEntity entity = new ExtensionEntity();
        entity.setExtensionBundleVersionId("eb2-v1");
        entity.setType("com.foo.Foo");
        entity.setTypeDescription("Description");
        entity.setCategory(ExtensionEntityCategory.PROCESSOR);
        entity.setRestricted(false);
        entity.setTags("a, b, c");

        final ExtensionEntity createdEntity = repository.create(entity);
        assertNotNull(createdEntity);
        assertNotNull(createdEntity.getId());

        final Optional<ExtensionEntity> retrievedEntity = repository.findById(createdEntity.getId());
        assertTrue(retrievedEntity.isPresent());
        assertEquals(createdEntity.getId(), retrievedEntity.get().getId());
    }
}
