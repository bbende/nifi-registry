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

public class FlowSnapshotTable extends AbstractTable<String> {

    public final Column FLOW_ID;
    public final Column VERSION;
    public final Column CREATED;
    public final Column CREATED_BY;
    public final Column COMMENTS;

    private SortedSet<Column> allColumns;

    FlowSnapshotTable() {
        super("FLOW_SNAPSHOT", "fs");
        FLOW_ID = StandardColumn.create(this, "FLOW_ID");
        VERSION = StandardColumn.create(this, "VERSION");
        CREATED = StandardColumn.create(this, "CREATED");
        CREATED_BY = StandardColumn.create(this, "CREATED_BY");
        COMMENTS = StandardColumn.create(this, "COMMENTS");

        allColumns = Collections.unmodifiableSortedSet(
                new TreeSet<>(Arrays.asList(FLOW_ID, VERSION, CREATED, CREATED_BY, COMMENTS)));
    }

    @Override
    public Column getIdColumn() {
        // TODO this table needs a composite key
        return FLOW_ID;
    }

    @Override
    public SortedSet<Column> getColumns() {
        return allColumns;
    }

}
