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
package org.apache.nifi.registry.db.jdbc.mapper;

import org.apache.nifi.registry.db.entity.BucketItemEntityType;
import org.apache.nifi.registry.db.entity.ExtensionBundleEntity;
import org.apache.nifi.registry.db.entity.ExtensionBundleEntityType;
import org.apache.nifi.registry.jdbc.api.Column;
import org.apache.nifi.registry.jdbc.api.EntityRowMapper;
import org.apache.nifi.registry.jdbc.api.EntityValueMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.apache.nifi.registry.db.jdbc.configuration.Tables.BUCKET_ITEM;
import static org.apache.nifi.registry.db.jdbc.configuration.Tables.EXTENSION_BUNDLE;

public class ExtensionBundleMapper implements EntityRowMapper<ExtensionBundleEntity>, EntityValueMapper<String,ExtensionBundleEntity> {

    @Override
    public ExtensionBundleEntity mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        final ExtensionBundleEntity entity = new ExtensionBundleEntity();

        // BucketItemEntity fields
        entity.setId(rs.getString(BUCKET_ITEM.ID.getAlias()));
        entity.setName(rs.getString(BUCKET_ITEM.NAME.getAlias()));
        entity.setDescription(rs.getString(BUCKET_ITEM.DESCRIPTION.getAlias()));
        entity.setCreated(rs.getTimestamp(BUCKET_ITEM.CREATED.getAlias()));
        entity.setModified(rs.getTimestamp(BUCKET_ITEM.MODIFIED.getAlias()));
        entity.setBucketId(rs.getString(BUCKET_ITEM.BUCKET_ID.getAlias()));
        entity.setType(BucketItemEntityType.EXTENSION_BUNDLE);

        // ExtensionBundleEntity fields
        entity.setBundleType(ExtensionBundleEntityType.valueOf(rs.getString(EXTENSION_BUNDLE.BUNDLE_TYPE.getAlias())));
        entity.setGroupId(rs.getString(EXTENSION_BUNDLE.GROUP_ID.getAlias()));
        entity.setArtifactId(rs.getString(EXTENSION_BUNDLE.ARTIFACT_ID.getAlias()));

        return entity;
    }

    @Override
    public Object mapValue(final Column column, final ExtensionBundleEntity entity) {
        if (column == EXTENSION_BUNDLE.ID) {
            return entity.getId();
        } else if (column == EXTENSION_BUNDLE.BUCKET_ID) {
            return entity.getBucketId();
        } else if (column == EXTENSION_BUNDLE.BUNDLE_TYPE) {
            return entity.getBundleType().toString();
        } else if (column == EXTENSION_BUNDLE.GROUP_ID) {
            return entity.getGroupId();
        } else if (column == EXTENSION_BUNDLE.ARTIFACT_ID) {
            return entity.getArtifactId();
        } else {
            throw new IllegalArgumentException("Unexpected column: " + column.getName());
        }
    }

    @Override
    public Object mapIdValue(final Column column, final String id) {
        if (column == null) {
            throw new IllegalArgumentException("Column cannot be null");
        }
        if (column != EXTENSION_BUNDLE.ID) {
            throw new IllegalArgumentException("Unexpected id column: " + column.getName());
        }
        return id;
    }

}
