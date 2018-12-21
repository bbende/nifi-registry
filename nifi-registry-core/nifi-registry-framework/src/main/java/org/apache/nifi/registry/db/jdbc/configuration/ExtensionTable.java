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

import java.util.Arrays;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class ExtensionTable extends AbstractTable {

    public final Column ID;
    public final Column EXTENSION_BUNDLE_VERSION_ID;
    public final Column TYPE;
    public final Column DESCRIPTION;
    public final Column IS_RESTRICTED;
    public final Column CATEGORY;
    public final Column TAGS;

    private final SortedSet<Column> allColumns;

    ExtensionTable() {
        super("EXTENSION", "ext");
        ID = StandardColumn.create(this, "ID");
        EXTENSION_BUNDLE_VERSION_ID = StandardColumn.create(this, "EXTENSION_BUNDLE_VERSION_ID");
        TYPE = StandardColumn.create(this, "TYPE");
        DESCRIPTION = StandardColumn.create(this, "TYPE_DESCRIPTION");
        IS_RESTRICTED = StandardColumn.create(this, "IS_RESTRICTED");
        CATEGORY = StandardColumn.create(this, "CATEGORY");
        TAGS = StandardColumn.create(this, "TAGS");

        allColumns = Collections.unmodifiableSortedSet(new TreeSet<>(
                Arrays.asList(ID, EXTENSION_BUNDLE_VERSION_ID, TYPE, DESCRIPTION, IS_RESTRICTED, CATEGORY, TAGS)
        ));
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
