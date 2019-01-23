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

import org.apache.nifi.registry.db.entity.FlowSnapshotEntity;
import org.apache.nifi.registry.db.entity.FlowSnapshotId;
import org.apache.nifi.registry.db.jdbc.configuration.Tables;
import org.apache.nifi.registry.jdbc.api.Column;
import org.apache.nifi.registry.jdbc.api.EntityRowMapper;
import org.apache.nifi.registry.jdbc.api.EntityValueMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FlowSnapshotMapper implements EntityRowMapper<FlowSnapshotEntity>, EntityValueMapper<FlowSnapshotId,FlowSnapshotEntity> {

    @Override
    public FlowSnapshotEntity mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        final FlowSnapshotEntity entity = new FlowSnapshotEntity();
        entity.setFlowId(rs.getString(Tables.FLOW_SNAPSHOT.FLOW_ID.getAlias()));
        entity.setVersion(rs.getInt(Tables.FLOW_SNAPSHOT.VERSION.getAlias()));
        entity.setCreated(rs.getTimestamp(Tables.FLOW_SNAPSHOT.CREATED.getAlias()));
        entity.setCreatedBy(rs.getString(Tables.FLOW_SNAPSHOT.CREATED_BY.getAlias()));
        entity.setComments(rs.getString(Tables.FLOW_SNAPSHOT.COMMENTS.getAlias()));
        return entity;
    }

    @Override
    public Object mapValue(final Column column, final FlowSnapshotEntity entity) {
        if (column == Tables.FLOW_SNAPSHOT.FLOW_ID) {
            return entity.getFlowId();
        } else if (column == Tables.FLOW_SNAPSHOT.VERSION) {
            return entity.getVersion();
        } else if (column == Tables.FLOW_SNAPSHOT.CREATED) {
            return entity.getCreated();
        } else if (column == Tables.FLOW_SNAPSHOT.CREATED_BY) {
            return entity.getCreatedBy();
        } else if (column == Tables.FLOW_SNAPSHOT.COMMENTS) {
            return entity.getComments();
        } else {
            throw new IllegalArgumentException("Unexpected column: " + column.getName());
        }
    }

    @Override
    public Object mapIdValue(final Column column, final FlowSnapshotId flowSnapshotId) {
        if (column == null) {
            throw new IllegalArgumentException("Column cannot be null");
        }

        if (column == Tables.FLOW_SNAPSHOT.FLOW_ID) {
            return flowSnapshotId.getFlowId();
        } else if (column == Tables.FLOW_SNAPSHOT.VERSION) {
            return flowSnapshotId.getVersion();
        } else {
            throw new IllegalArgumentException("Unexpected id column: " + column.getName());
        }
    }

}
