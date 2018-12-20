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

public class ExtensionBundleTable extends AbstractTable<String> {

    public final Column ID;
    public final Column BUCKET_ID;
    public final Column BUNDLE_TYPE;
    public final Column GROUP_ID;
    public final Column ARTIFACT_ID;

    private SortedSet<Column> allColumns;

    ExtensionBundleTable() {
        super("EXTENSION_BUNDLE", "eb", new UUIDStringGenerator());
        ID = StandardColumn.create(this, "ID");
        BUCKET_ID = StandardColumn.create(this, "BUCKET_ID");
        BUNDLE_TYPE = StandardColumn.create(this, "BUNDLE_TYPE");
        GROUP_ID = StandardColumn.create(this, "GROUP_ID");
        ARTIFACT_ID = StandardColumn.create(this, "ARTIFACT_ID");

        allColumns = Collections.unmodifiableSortedSet(
                new TreeSet<>(Arrays.asList(ID, BUCKET_ID, BUNDLE_TYPE, GROUP_ID, ARTIFACT_ID)));
    }

    @Override
    public Column getIdColumn() {
        return ID;
    }

    @Override
    public SortedSet<Column> getColumns() {
        return allColumns;
    }

    @Override
    public SortedSet<Column> getUpdatableColumns() {
        return Collections.emptySortedSet();
    }

}
