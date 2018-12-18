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

import org.apache.nifi.registry.jdbc.api.Column;
import org.apache.nifi.registry.jdbc.commons.StandardColumn;

public interface BucketItemColumns {

    Column ID = new StandardColumn("ID");
    Column NAME = new StandardColumn("NAME");
    Column DESCRIPTION = new StandardColumn("DESCRIPTION");
    Column CREATED = new StandardColumn("CREATED");
    Column MODIFIED = new StandardColumn("MODIFIED");
    Column ITEM_TYPE = new StandardColumn("ITEM_TYPE");
    Column BUCKET_ID = new StandardColumn("BUCKET_ID");

    static Column[] values() {
        return new Column[] { ID, NAME, DESCRIPTION, CREATED, MODIFIED, ITEM_TYPE, BUCKET_ID};
    }
}
