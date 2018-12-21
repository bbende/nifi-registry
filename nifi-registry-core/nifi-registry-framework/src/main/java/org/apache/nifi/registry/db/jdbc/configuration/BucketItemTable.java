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
import java.util.stream.Collectors;

public class BucketItemTable extends AbstractTable {

    public final Column ID;
    public final Column NAME;
    public final Column DESCRIPTION;
    public final Column CREATED;
    public final Column MODIFIED;
    public final Column ITEM_TYPE;
    public final Column BUCKET_ID;

    private final SortedSet<Column> allColumns;
    private final SortedSet<Column> updatableColumns;

    BucketItemTable() {
        super("BUCKET_ITEM", "bi");
        ID = StandardColumn.create(this,"ID");
        NAME = StandardColumn.createUpdatable(this,"NAME");
        DESCRIPTION = StandardColumn.createUpdatable(this,"DESCRIPTION");
        CREATED = StandardColumn.create(this,"CREATED");
        MODIFIED = StandardColumn.createUpdatable(this,"MODIFIED");
        ITEM_TYPE = StandardColumn.create(this,"ITEM_TYPE");
        BUCKET_ID = StandardColumn.create(this,"BUCKET_ID");

        allColumns = Collections.unmodifiableSortedSet(new TreeSet<>(
                Arrays.asList(ID, NAME, DESCRIPTION, CREATED, MODIFIED, ITEM_TYPE, BUCKET_ID)
        ));

        updatableColumns = Collections.unmodifiableSortedSet(new TreeSet<>(
                allColumns.stream().filter(c -> c.isUpdatable()).collect(Collectors.toSet())
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

    @Override
    public SortedSet<Column> getUpdatableColumns() {
        return updatableColumns;
    }

}
