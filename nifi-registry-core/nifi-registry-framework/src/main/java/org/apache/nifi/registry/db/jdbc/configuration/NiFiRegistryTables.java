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
package org.apache.nifi.registry.db.jdbc.configuration;

import org.apache.nifi.registry.jdbc.api.Table;
import org.apache.nifi.registry.jdbc.commons.StandardTable;
import org.apache.nifi.registry.jdbc.commons.UUIDStringGenerator;

public interface NiFiRegistryTables {

    Table<String> BUCKET =
            new StandardTable.Builder<String>("BUCKET", "b")
                    .addColumns(BucketColumns.values())
                    .idColumn(BucketColumns.ID)
                    .idGenerator(new UUIDStringGenerator())
                    .build();

    Table<String> BUCKET_ITEM =
            new StandardTable.Builder<String>("BUCKET_ITEM", "bi")
                    .addColumns(BucketItemColumns.values())
                    .idColumn(BucketItemColumns.ID)
                    .idGenerator(new UUIDStringGenerator())
                    .build();

    Table<String> FLOW =
            new StandardTable.Builder<String>("FLOW", "f")
                    .addColumns(FlowColumns.values())
                    .idColumn(FlowColumns.ID)
                    .idGenerator(null)
                    .build();

    Table<String> EXTENSION =
            new StandardTable.Builder<String>("EXTENSION", "ext")
                    .addColumns(ExtensionColumns.values())
                    .idColumn(ExtensionColumns.ID)
                    .idGenerator(new UUIDStringGenerator())
                    .build();


}
