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
package org.apache.nifi.registry.jdbc.commons;

import org.apache.nifi.registry.jdbc.api.Column;
import org.apache.nifi.registry.jdbc.api.IDGenerator;
import org.apache.nifi.registry.jdbc.api.Table;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

public class StandardTable<ID> implements Table<ID> {

    private final String name;
    private final String alias;
    private final Column idColumn;
    private final SortedSet<Column> columns;
    private final Optional<IDGenerator<ID>> idGenerator;

    private StandardTable(final Builder<ID> builder) {
        this.name = Objects.requireNonNull(builder.name);
        this.alias = Objects.requireNonNull(builder.alias);
        this.idColumn = Objects.requireNonNull(builder.idColumn);
        this.columns = Collections.unmodifiableSortedSet(
                new TreeSet<>(Objects.requireNonNull(builder.columns)));
        this.idGenerator = Optional.ofNullable(builder.idGenerator);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public Column getIdColumn() {
        return idColumn;
    }

    @Override
    public SortedSet<Column> getColumns() {
        return columns;
    }

    public Optional<IDGenerator<ID>> getIDGenerator() {
        return idGenerator;
    }

    public static class Builder<ID> {
        private String name;
        private String alias;
        private Column idColumn;
        private SortedSet<Column> columns = new TreeSet<>();
        private IDGenerator<ID> idGenerator;

        public Builder() {

        }

        public Builder(final String name, final String alias) {
            this.name = name;
            this.alias = alias;
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder alias(final String alias) {
            this.alias = alias;
            return this;
        }

        public Builder idColumn(final Column idColumn) {
            this.idColumn = idColumn;
            return this;
        }

        public Builder addColumn(final Column column) {
            if (column != null) {
                this.columns.add(column);
            }
            return this;
        }

        public Builder addColumns(final Column ... columns) {
            if (columns != null) {
                for (Column column : columns) {
                    this.columns.add(column);
                }
            }
            return this;
        }

        public Builder idGenerator(final IDGenerator<ID> idGenerator) {
            this.idGenerator = idGenerator;
            return this;
        }

        public Table<ID> build() {
            return new StandardTable<>(this);
        }

    }
}
