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
import org.apache.nifi.registry.db.jdbc.configuration.ExtensionColumns;
import org.apache.nifi.registry.jdbc.api.Column;
import org.apache.nifi.registry.jdbc.api.EntityRowMapper;
import org.apache.nifi.registry.jdbc.api.EntityValueMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExtensionMapper implements EntityRowMapper<String, ExtensionEntity>, EntityValueMapper<String,ExtensionEntity> {

    @Override
    public ExtensionEntity mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        final ExtensionEntity entity = new ExtensionEntity();
        entity.setId(rs.getString(ExtensionColumns.ID.getName()));
        entity.setExtensionBundleVersionId(rs.getString(ExtensionColumns.EXTENSION_BUNDLE_VERSION_ID.getName()));
        entity.setType(rs.getString(ExtensionColumns.TYPE.getName()));
        entity.setTypeDescription(rs.getString(ExtensionColumns.DESCRIPTION.getName()));
        entity.setRestricted(rs.getInt(ExtensionColumns.IS_RESTRICTED.getName()) == 1);
        entity.setCategory(ExtensionEntityCategory.valueOf(rs.getString(ExtensionColumns.CATEGORY.getName())));
        entity.setTags(rs.getString(ExtensionColumns.TAGS.getName()));
        return entity;
    }

    @Override
    public Object map(final Column column, final ExtensionEntity entity) {
        if (column == ExtensionColumns.ID) {
            return entity.getId();
        } else if (column == ExtensionColumns.EXTENSION_BUNDLE_VERSION_ID) {
            return entity.getExtensionBundleVersionId();
        } else if (column == ExtensionColumns.TYPE){
            return entity.getType();
        } else if (column == ExtensionColumns.DESCRIPTION) {
            return entity.getTypeDescription();
        } else if (column == ExtensionColumns.IS_RESTRICTED) {
            return entity.isRestricted();
        } else if (column == ExtensionColumns.CATEGORY) {
            return entity.getCategory().name();
        } else if (column == ExtensionColumns.TAGS) {
            return entity.getTags();
        } else {
            throw new IllegalArgumentException("Unexpected column: " + column.getName());
        }
    }
}
