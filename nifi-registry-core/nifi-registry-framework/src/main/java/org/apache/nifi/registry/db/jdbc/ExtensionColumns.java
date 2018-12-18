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
package org.apache.nifi.registry.db.jdbc;

import org.apache.nifi.registry.jdbc.api.Column;
import org.apache.nifi.registry.jdbc.commons.StandardColumn;

public class ExtensionColumns {

    public static final Column ID = new StandardColumn("ID");
    public static final Column EXTENSION_BUNDLE_VERSION_ID = new StandardColumn("EXTENSION_BUNDLE_VERSION_ID");
    public static final Column TYPE = new StandardColumn("TYPE");
    public static final Column DESCRIPTION = new StandardColumn("TYPE_DESCRIPTION");
    public static final Column IS_RESTRICTED = new StandardColumn("IS_RESTRICTED");
    public static final Column CATEGORY = new StandardColumn("CATEGORY");
    public static final Column TAGS = new StandardColumn("TAGS");

    public static Column[] values() {
        return new Column[] {
                ID, EXTENSION_BUNDLE_VERSION_ID, TYPE, DESCRIPTION, IS_RESTRICTED, CATEGORY, TAGS
        };
    }
}
