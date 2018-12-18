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
import org.apache.nifi.registry.db.entity.FlowEntity;
import org.apache.nifi.registry.db.jdbc.configuration.BucketItemColumns;
import org.apache.nifi.registry.db.jdbc.configuration.FlowColumns;
import org.apache.nifi.registry.jdbc.api.Column;
import org.apache.nifi.registry.jdbc.api.EntityRowMapper;
import org.apache.nifi.registry.jdbc.api.EntityValueMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FlowMapper implements EntityRowMapper<FlowEntity>, EntityValueMapper<FlowEntity> {

    @Override
    public FlowEntity mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        final FlowEntity flowEntity = new FlowEntity();
        flowEntity.setId(rs.getString(FlowColumns.ID.getName()));
        flowEntity.setName(rs.getString(BucketItemColumns.NAME.getName()));
        flowEntity.setDescription(rs.getString(BucketItemColumns.DESCRIPTION.getName()));
        flowEntity.setCreated(rs.getTimestamp(BucketItemColumns.CREATED.getName()));
        flowEntity.setModified(rs.getTimestamp(BucketItemColumns.MODIFIED.getName()));
        flowEntity.setBucketId(rs.getString(BucketItemColumns.BUCKET_ID.getName()));
        flowEntity.setType(BucketItemEntityType.FLOW);
        return flowEntity;
    }

    @Override
    public Object map(final Column column, final FlowEntity entity) {
        final EntityValueMapper<BucketItemEntity> bucketItemMapper = new BucketItemMapper();
        return bucketItemMapper.map(column, entity);
    }

}
