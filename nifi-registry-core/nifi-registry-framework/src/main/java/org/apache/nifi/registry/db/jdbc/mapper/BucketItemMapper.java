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

import org.apache.nifi.registry.db.entity.BucketItemEntity;
import org.apache.nifi.registry.db.entity.BucketItemEntityType;
import org.apache.nifi.registry.db.entity.ExtensionBundleEntity;
import org.apache.nifi.registry.db.entity.ExtensionBundleEntityType;
import org.apache.nifi.registry.db.entity.FlowEntity;
import org.apache.nifi.registry.db.jdbc.configuration.Tables;
import org.apache.nifi.registry.jdbc.api.Column;
import org.apache.nifi.registry.jdbc.api.EntityRowMapper;
import org.apache.nifi.registry.jdbc.api.EntityValueMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BucketItemMapper implements EntityRowMapper<BucketItemEntity>, EntityValueMapper<String,BucketItemEntity> {

    @Override
    public BucketItemEntity mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        final String typeString = rs.getString(Tables.BUCKET_ITEM.ITEM_TYPE.getAlias());
        final BucketItemEntityType type = BucketItemEntityType.valueOf(typeString);

        // Create the appropriate type of sub-class, eventually populate specific data for each type
        final BucketItemEntity item;
        switch (type) {
            case FLOW:
                item = new FlowEntity();
                break;
            case EXTENSION_BUNDLE:
                final String bundleTypeString = rs.getString(Tables.EXTENSION_BUNDLE.BUNDLE_TYPE.getAlias());

                final ExtensionBundleEntity bundleEntity = new ExtensionBundleEntity();
                bundleEntity.setBundleType(ExtensionBundleEntityType.valueOf(bundleTypeString));
                bundleEntity.setGroupId(rs.getString(Tables.EXTENSION_BUNDLE.GROUP_ID.getAlias()));
                bundleEntity.setArtifactId(rs.getString(Tables.EXTENSION_BUNDLE.ARTIFACT_ID.getAlias()));
                item = bundleEntity;
                break;
            default:
                // should never happen
                item = new BucketItemEntity();
                break;
        }

        // populate fields common to all bucket items
        item.setId(rs.getString(Tables.BUCKET_ITEM.ID.getAlias()));
        item.setName(rs.getString(Tables.BUCKET_ITEM.NAME.getAlias()));
        item.setDescription(rs.getString(Tables.BUCKET_ITEM.DESCRIPTION.getAlias()));
        item.setCreated(rs.getTimestamp(Tables.BUCKET_ITEM.CREATED.getAlias()));
        item.setModified(rs.getTimestamp(Tables.BUCKET_ITEM.MODIFIED.getAlias()));
        item.setBucketId(rs.getString(Tables.BUCKET_ITEM.BUCKET_ID.getAlias()));
        item.setBucketName(rs.getString(Tables.BUCKET.NAME.getAlias()));
        item.setType(type);
        return item;
    }

    @Override
    public Object mapValue(final Column column, final BucketItemEntity entity) {
        if (column == Tables.BUCKET_ITEM.ID) {
            return entity.getId();
        } else if (column == Tables.BUCKET_ITEM.NAME) {
            return entity.getName();
        } else if (column == Tables.BUCKET_ITEM.DESCRIPTION) {
            return entity.getDescription();
        } else if (column == Tables.BUCKET_ITEM.CREATED) {
            return entity.getCreated();
        } else if (column == Tables.BUCKET_ITEM.MODIFIED) {
            return entity.getModified();
        } else if (column == Tables.BUCKET_ITEM.BUCKET_ID) {
            return entity.getBucketId();
        } else if (column == Tables.BUCKET_ITEM.ITEM_TYPE) {
            return entity.getType().toString();
        } else {
            throw new IllegalArgumentException("Unexpected column: " + column.getName());
        }
    }

    @Override
    public Object mapIdValue(final Column column, final String id) {
        if (column == null) {
            throw new IllegalArgumentException("Column cannot be null");
        }
        if (column != Tables.BUCKET_ITEM.ID) {
            throw new IllegalArgumentException("Unexpected id column: " + column.getName());
        }
        return id;
    }

}
