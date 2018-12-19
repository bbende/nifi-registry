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

import org.apache.nifi.registry.db.entity.BucketEntity;
import org.apache.nifi.registry.db.jdbc.configuration.Tables;
import org.apache.nifi.registry.jdbc.api.Column;
import org.apache.nifi.registry.jdbc.api.EntityRowMapper;
import org.apache.nifi.registry.jdbc.api.EntityValueMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BucketMapper implements EntityRowMapper<BucketEntity>, EntityValueMapper<BucketEntity> {

    @Override
    public BucketEntity mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        final BucketEntity b = new BucketEntity();
        b.setId(rs.getString(Tables.BUCKET.ID.getName()));
        b.setName(rs.getString(Tables.BUCKET.NAME.getName()));
        b.setDescription(rs.getString(Tables.BUCKET.DESCRIPTION.getName()));
        b.setCreated(rs.getTimestamp(Tables.BUCKET.CREATED.getName()));
        b.setAllowExtensionBundleRedeploy(rs.getInt(Tables.BUCKET.ALLOW_EXTENSION_BUNDLE_REDEPLOY.getName()) == 0 ? false : true);
        return b;
    }

    @Override
    public Object map(final Column column, final BucketEntity entity) {
        if (column == Tables.BUCKET.ID) {
            return entity.getId();
        } else if (column == Tables.BUCKET.NAME) {
            return entity.getName();
        } else if (column == Tables.BUCKET.DESCRIPTION) {
            return entity.getDescription();
        } else if (column == Tables.BUCKET.CREATED) {
            return entity.getCreated();
        } else if (column == Tables.BUCKET.ALLOW_EXTENSION_BUNDLE_REDEPLOY) {
            return entity.isAllowExtensionBundleRedeploy() ? 1 : 0;
        } else {
            throw new IllegalArgumentException("Unexpected colum: " + column.getName());
        }
    }
}
