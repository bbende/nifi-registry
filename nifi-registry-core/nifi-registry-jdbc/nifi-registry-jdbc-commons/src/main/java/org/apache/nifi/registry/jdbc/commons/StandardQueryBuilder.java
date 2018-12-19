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
import org.apache.nifi.registry.jdbc.api.QueryOperator;
import org.apache.nifi.registry.jdbc.api.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

public class StandardQueryBuilder implements QueryBuilder {

    private List<String> returnFields = new ArrayList<>();
    private List<String> from = new ArrayList<>();
    private List<String> whereClauses= new ArrayList<>();

    @Override
    public QueryBuilder select(final SortedSet<Column> columns) {
        columns.forEach(c -> select(c));
        return this;
    }

    @Override
    public QueryBuilder select(final Column column) {
        return select(column, column.getName());
    }

    @Override
    public QueryBuilder select(final Column column, final String columnAlias) {
        returnFields.add(column.getTable().getAlias() + "." + column.getName() + " AS " + columnAlias);
        return this;
    }

    @Override
    public QueryBuilder select(final String expression) {
        returnFields.add(expression);
        return this;
    }

    @Override
    public QueryBuilder from(final Table ... tables) {
        if (tables != null) {
            for (final Table table : tables) {
                from.add(table.getName() + " " + table.getAlias());
            }
        }
        return this;
    }

    @Override
    public QueryBuilder whereEqual(final Column column) {
        whereClauses.add(
                new StringBuilder(column.getTable().getAlias())
                        .append(".")
                        .append(column.getName())
                        .append(" ")
                        .append(QueryOperator.EQ.getOperator())
                        .append(" ?")
                        .toString());
        return this;
    }

    @Override
    public QueryBuilder whereEqual(final Column column1, final Column column2) {
        whereClauses.add(
                new StringBuilder(column1.getTable().getAlias()).append(".").append(column1.getName()).append(" ")
                        .append(QueryOperator.EQ.getOperator())
                        .append(column2.getTable().getAlias()).append(".").append(column2.getName())
                        .toString());
        return this;
    }

    @Override
    public QueryBuilder whereEqual(final SortedSet<Column> columns) {
        if (columns == null || columns.isEmpty()) {
            return this;
        }

        columns.forEach(c -> whereEqual(c));
        return this;
    }

    @Override
    public QueryBuilder whereNotEqual(final Column column) {
        whereClauses.add(
                new StringBuilder(column.getTable().getAlias())
                        .append(".")
                        .append(column.getName())
                        .append(" ")
                        .append(QueryOperator.NEQ.getOperator())
                        .append(" ?")
                        .toString());
        return this;
    }

    @Override
    public QueryBuilder whereNotEqual(final Column column1, final Column column2) {
        whereClauses.add(
                new StringBuilder(column1.getTable().getAlias()).append(".").append(column1.getName()).append(" ")
                        .append(QueryOperator.NEQ.getOperator())
                        .append(column2.getTable().getAlias()).append(".").append(column2.getName())
                        .toString());
        return this;
    }

    @Override
    public QueryBuilder whereLike(final Column column) {
        whereClauses.add(
                new StringBuilder(column.getTable().getAlias())
                        .append(".")
                        .append(column.getName())
                        .append(" ")
                        .append(QueryOperator.LIKE.getOperator())
                        .append(" ?")
                        .toString());
        return this;
    }

    @Override
    public QueryBuilder whereIn(final Column column, final int count) {
        final StringBuilder builder = new StringBuilder(column.getTable().getAlias())
                .append(".").append(column.getName())
                .append(" ").append(QueryOperator.IN.getOperator()).append(" ( ");

        SqlUtils.appendValues(builder, "?", count);

        builder.append(" ) ");
        whereClauses.add(builder.toString());
        return this;
    }

    @Override
    public QueryBuilder where(final String clause) {
        whereClauses.add(clause);
        return this;
    }

    @Override
    public QueryBuilder copy() {
        final StandardQueryBuilder copy = new StandardQueryBuilder();
        copy.returnFields.addAll(this.returnFields);
        copy.from.addAll(this.from);
        copy.whereClauses.addAll(this.whereClauses);
        return copy;
    }

    @Override
    public String build() {
        if (returnFields.isEmpty()) {
            throw new IllegalStateException("At least one column must be selected to return");
        }

        if (from.isEmpty()) {
            throw new IllegalStateException("At least one table be specified");
        }

        final StringBuilder builder = new StringBuilder("SELECT ");
        SqlUtils.appendValues(builder, returnFields);

        builder.append(" FROM ");
        SqlUtils.appendValues(builder, from);

        if (!whereClauses.isEmpty()) {
            builder.append(" WHERE ");
            SqlUtils.appendValues(builder, whereClauses, QueryOperator.AND.getOperator());
        }
        return builder.toString();
    }
}
