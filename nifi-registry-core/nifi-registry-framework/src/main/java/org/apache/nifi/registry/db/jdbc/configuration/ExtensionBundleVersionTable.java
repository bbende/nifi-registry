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
import org.apache.nifi.registry.jdbc.commons.AbstractTable;
import org.apache.nifi.registry.jdbc.commons.StandardColumn;
import org.apache.nifi.registry.jdbc.commons.UUIDStringGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class ExtensionBundleVersionTable extends AbstractTable<String> {

    public final Column ID;
    public final Column EXTENSION_BUNDLE_ID;
    public final Column VERSION;
    public final Column CREATED;
    public final Column CREATED_BY;
    public final Column DESCRIPTION;
    public final Column SHA_256_HEX;
    public final Column SHA_256_SUPPLIED;
    public final Column CONTENT_SIZE;

    private SortedSet<Column> allColumns;

    ExtensionBundleVersionTable() {
        super("EXTENSION_BUNDLE_VERSION", "ebv", new UUIDStringGenerator());
        ID = StandardColumn.create(this, "ID");
        EXTENSION_BUNDLE_ID = StandardColumn.create(this, "EXTENSION_BUNDLE_ID");
        VERSION = StandardColumn.create(this, "VERSION");
        CREATED = StandardColumn.create(this, "CREATED");
        CREATED_BY = StandardColumn.create(this, "CREATED_BY");
        DESCRIPTION = StandardColumn.create(this, "DESCRIPTION");
        SHA_256_HEX = StandardColumn.create(this, "SHA_256_HEX");
        SHA_256_SUPPLIED = StandardColumn.create(this, "SHA_256_SUPPLIED");
        CONTENT_SIZE = StandardColumn.create(this, "CONTENT_SIZE");

        allColumns = Collections.unmodifiableSortedSet(
                new TreeSet<>(Arrays.asList(
                        ID,
                        EXTENSION_BUNDLE_ID,
                        VERSION,
                        CREATED,
                        CREATED_BY,
                        DESCRIPTION,
                        SHA_256_HEX,
                        SHA_256_SUPPLIED,
                        CONTENT_SIZE
                )));
    }

    @Override
    public Column getIdColumn() {
        return ID;
    }

    @Override
    public SortedSet<Column> getColumns() {
        return allColumns;
    }

}
