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

public class ExtensionBundleVersionDependencyTable extends AbstractTable<String> {

    public final Column ID;
    public final Column EXTENSION_BUNDLE_ID;
    public final Column GROUP_ID;
    public final Column ARTIFACT_ID;
    public final Column VERSION;

    private SortedSet<Column> allColumns;

    ExtensionBundleVersionDependencyTable() {
        super("EXTENSION_BUNDLE_VERSION_DEPENDENCY", "ebvd", new UUIDStringGenerator());
        ID = StandardColumn.create(this, "ID");
        EXTENSION_BUNDLE_ID = StandardColumn.create(this, "EXTENSION_BUNDLE_ID");
        GROUP_ID = StandardColumn.create(this, "GROUP_ID");
        ARTIFACT_ID = StandardColumn.create(this, "ARTIFACT_ID");
        VERSION = StandardColumn.create(this, "VERSION");

        allColumns = Collections.unmodifiableSortedSet(
                new TreeSet<>(Arrays.asList(ID, EXTENSION_BUNDLE_ID, GROUP_ID, ARTIFACT_ID, VERSION)));
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
