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

import org.apache.nifi.registry.db.entity.ExtensionEntity;
import org.apache.nifi.registry.db.entity.ExtensionEntityCategory;
import org.apache.nifi.registry.jdbc.api.Column;
import org.apache.nifi.registry.jdbc.api.EntityRowMapper;
import org.apache.nifi.registry.jdbc.api.EntityValueMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.apache.nifi.registry.db.jdbc.configuration.Tables.EXTENSION;

public class ExtensionMapper implements EntityRowMapper<ExtensionEntity>, EntityValueMapper<String,ExtensionEntity> {

    @Override
    public ExtensionEntity mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        final ExtensionEntity entity = new ExtensionEntity();
        entity.setId(rs.getString(EXTENSION.ID.getAlias()));
        entity.setExtensionBundleVersionId(rs.getString(EXTENSION.EXTENSION_BUNDLE_VERSION_ID.getAlias()));
        entity.setType(rs.getString(EXTENSION.TYPE.getAlias()));
        entity.setTypeDescription(rs.getString(EXTENSION.DESCRIPTION.getAlias()));
        entity.setRestricted(rs.getInt(EXTENSION.IS_RESTRICTED.getAlias()) == 1);
        entity.setTags(rs.getString(EXTENSION.TAGS.getName()));

        final String categoryString = rs.getString(EXTENSION.CATEGORY.getAlias());
        entity.setCategory(ExtensionEntityCategory.valueOf(categoryString));
        return entity;
    }

    @Override
    public Object mapValue(final Column column, final ExtensionEntity entity) {
        if (column == EXTENSION.ID) {
            return entity.getId();
        } else if (column == EXTENSION.EXTENSION_BUNDLE_VERSION_ID) {
            return entity.getExtensionBundleVersionId();
        } else if (column == EXTENSION.TYPE){
            return entity.getType();
        } else if (column == EXTENSION.DESCRIPTION) {
            return entity.getTypeDescription();
        } else if (column == EXTENSION.IS_RESTRICTED) {
            return entity.isRestricted();
        } else if (column == EXTENSION.CATEGORY) {
            return entity.getCategory().name();
        } else if (column == EXTENSION.TAGS) {
            return entity.getTags();
        } else {
            throw new IllegalArgumentException("Unexpected column: " + column.getName());
        }
    }

    @Override
    public Object mapIdValue(final Column column, final String id) {
        if (column == null) {
            throw new IllegalArgumentException("Column cannot be null");
        }
        if (column != EXTENSION.ID) {
            throw new IllegalArgumentException("Unexpected id column: " + column.getName());
        }
        return id;
    }
}
