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
import org.apache.nifi.registry.jdbc.api.CompositeIDTable;
import org.apache.nifi.registry.jdbc.api.QueryBuilder;
import org.apache.nifi.registry.jdbc.api.Table;

import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

public class SqlFactory {

    public static String insert(final Table table) {
        final StringBuilder builder = new StringBuilder("INSERT INTO ")
                .append(table.getName())
                .append(" ( ");
        SqlUtils.appendValues(builder, getColumnNames(table));
        builder.append(" ) VALUES (");
        SqlUtils.appendValues(builder, "?", table.getColumns().size());
        builder.append(")");
        return builder.toString();
    }

    public static String update(final Table table, final SortedMap<Column,Object> values) {

        final StringBuilder builder = new StringBuilder("UPDATE ")
                .append(table.getName())
                .append(" SET ");

        boolean first = true;
        for (final Map.Entry<Column,Object> entry : values.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }

            final Column column = entry.getKey();
            if (!first) {
                builder.append(", ");
            }
            builder.append(column.getName()).append(" = ? ");
            first = false;
        }

        builder.append(" WHERE ");
        appendIDClause(table, builder);
        return builder.toString();
    }

    public static QueryBuilder query() {
        return new StandardQueryBuilder();
    }

    public static String selectById(final Table table) {
        return selectById(table, table.getColumns());
    }

    public static String selectById(final Table table, final SortedSet<Column> returnColumns) {
        final String sql = query().select(returnColumns).from(table).build();

        final StringBuilder builder = new StringBuilder(sql).append(" WHERE ");
        appendIDClause(table, builder);
        return builder.toString();
    }

    public static String delete(final Table table) {
        final StringBuilder builder = new StringBuilder("DELETE FROM ")
                .append(table.getName())
                .append(" WHERE ");
        appendIDClause(table, builder);
        return builder.toString();
    }

    private static void appendIDClause(final Table table, final StringBuilder builder) {
        if (table instanceof CompositeIDTable) {
            final CompositeIDTable compositeIDTable = (CompositeIDTable)table;

            boolean first = false;
            for (final Column idColumn : compositeIDTable.getIdColumns()) {
                if (!first) {
                    builder.append(" AND ");
                }
                builder.append(idColumn).append(" = ?");
                first = false;
            }

        } else {
            final String idColumn = table.getIdColumn().getName();
            builder.append(idColumn).append(" = ?");
        }
    }

    private static SortedSet<String> getColumnNames(final Table table) {
        final SortedSet<String> cols = new TreeSet<>();
        table.getColumns().forEach(c -> cols.add(c.getName()));
        return cols;
    }

}
