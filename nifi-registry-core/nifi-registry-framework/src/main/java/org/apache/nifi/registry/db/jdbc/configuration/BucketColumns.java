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

public interface BucketColumns {

    Column ID = StandardColumn.create("ID");
    Column NAME = StandardColumn.createUpdatable("NAME");
    Column DESCRIPTION = StandardColumn.createUpdatable("DESCRIPTION");
    Column CREATED = StandardColumn.create("CREATED");
    Column ALLOW_EXTENSION_BUNDLE_REDEPLOY = StandardColumn.createUpdatable("ALLOW_EXTENSION_BUNDLE_REDEPLOY");

    static Column[] values() {
        return new Column[] { ID, NAME, DESCRIPTION, CREATED, ALLOW_EXTENSION_BUNDLE_REDEPLOY };
    }
}
