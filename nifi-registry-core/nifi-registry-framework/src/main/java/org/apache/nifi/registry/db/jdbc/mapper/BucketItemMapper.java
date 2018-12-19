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
import org.apache.nifi.registry.db.jdbc.configuration.Tables;
import org.apache.nifi.registry.jdbc.api.Column;
import org.apache.nifi.registry.jdbc.api.EntityValueMapper;

public class BucketItemMapper implements EntityValueMapper<BucketItemEntity> {

    @Override
    public Object map(final Column column, final BucketItemEntity entity) {
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

}
