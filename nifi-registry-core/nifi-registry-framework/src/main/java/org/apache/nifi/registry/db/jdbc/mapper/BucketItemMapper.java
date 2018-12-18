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
import org.apache.nifi.registry.db.jdbc.configuration.BucketItemColumns;
import org.apache.nifi.registry.jdbc.api.Column;
import org.apache.nifi.registry.jdbc.api.EntityRowMapper;
import org.apache.nifi.registry.jdbc.api.EntityValueMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BucketItemMapper implements EntityRowMapper<BucketItemEntity>, EntityValueMapper<BucketItemEntity> {

    @Override
    public BucketItemEntity mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        // TODO
        return null;
    }

    @Override
    public Object map(final Column column, final BucketItemEntity entity) {
        if (column == BucketItemColumns.ID) {
            return entity.getId();
        } else if (column == BucketItemColumns.NAME) {
            return entity.getName();
        } else if (column == BucketItemColumns.DESCRIPTION) {
            return entity.getDescription();
        } else if (column == BucketItemColumns.CREATED) {
            return entity.getCreated();
        } else if (column == BucketItemColumns.MODIFIED) {
            return entity.getModified();
        } else if (column == BucketItemColumns.BUCKET_ID) {
            return entity.getBucketId();
        } else if (column == BucketItemColumns.ITEM_TYPE) {
            return entity.getType().toString();
        } else {
            throw new IllegalArgumentException("Unexpected column: " + column.getName());
        }
    }

}
