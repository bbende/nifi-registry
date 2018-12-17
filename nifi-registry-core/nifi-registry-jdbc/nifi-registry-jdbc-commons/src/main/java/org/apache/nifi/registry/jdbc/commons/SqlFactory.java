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
import org.apache.nifi.registry.jdbc.api.QueryBuilder;
import org.apache.nifi.registry.jdbc.api.Table;

import java.util.SortedSet;
import java.util.TreeSet;

public class SqlFactory {

    public static String insert(final Table<?> table) {
        final StringBuilder builder = new StringBuilder("INSERT INTO ")
                .append(table.getName())
                .append(" ( ");
            SqlUtils.appendValues(builder, getColumnNames(table));
        builder.append(" ) VALUES (");
            SqlUtils.appendValues(builder, "?", table.getColumns().size());
        builder.append(")");
        return builder.toString();
    }

    public static String update(final Table<?> table, final SortedSet<Column> columns) {
        final String idColumn = table.getIdColumn().getName();

        final StringBuilder builder = new StringBuilder("UPDATE ")
                .append(table.getName())
                .append(" SET ");

        boolean first = true;
        for (final Column column : columns) {
            if (!first) {
                builder.append(", ");
            }
            builder.append(column.getName()).append(" = ? ");
            first = false;
        }

        builder.append(" WHERE ").append(idColumn).append(" = ?");

        return builder.toString();
    }

    public static QueryBuilder query() {
        return new StandardQueryBuilder();
    }

    public static String selectById(final Table<?> table) {
        return selectById(table, table.getColumns());
    }

    public static String selectById(final Table<?> table, final SortedSet<Column> columns) {
        final String sql = query()
                .select(table, columns)
                .from(table)
                .whereEqual(table, table.getIdColumn())
                .build();

        return sql;
    }

    public static String delete(final Table<?> table) {
        final String idColumn = table.getIdColumn().getName();

        return new StringBuilder("DELETE FROM ").append(table.getName())
                .append(" WHERE ").append(idColumn).append(" = ?")
                .toString();
    }

    private static SortedSet<String> getColumnNames(final Table<?> table) {
        final SortedSet<String> cols = new TreeSet<>();
        table.getColumns().forEach(c -> cols.add(c.getName()));
        return cols;
    }

}
