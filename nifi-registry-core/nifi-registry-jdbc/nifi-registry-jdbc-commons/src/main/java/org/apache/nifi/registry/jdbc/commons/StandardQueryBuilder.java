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
import org.apache.nifi.registry.jdbc.api.SortOrder;
import org.apache.nifi.registry.jdbc.api.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

public class StandardQueryBuilder implements QueryBuilder {

    private List<String> returnFields = new ArrayList<>();
    private List<String> from = new ArrayList<>();
    private List<String> joins = new ArrayList<>();
    private List<String> whereClauses= new ArrayList<>();
    private List<String> groupBy = new ArrayList<>();
    private List<String> orderBy = new ArrayList<>();
    private Integer limit = null;

    @Override
    public QueryBuilder select(final SortedSet<Column> columns) {
        columns.forEach(c -> select(c));
        return this;
    }

    @Override
    public QueryBuilder select(final Column column) {
        returnFields.add(getQualifiedColumnName(column) + " AS " + column.getAlias());
        return this;
    }

    @Override
    public QueryBuilder selectCount(final Column column) {
        returnFields.add("count(" + getQualifiedColumnName(column) + ") AS " + column.getAlias() + "_COUNT");
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
    public QueryBuilder innerJoin(final Table table, final Column column1, final Column column2) {
        return join("INNER", table, column1, column2);
    }

    @Override
    public QueryBuilder outerJoin(final Table table, final Column column1, final Column column2) {
        return join("OUTER", table, column1, column2);
    }

    @Override
    public QueryBuilder leftJoin(final Table table, final Column column1, final Column column2) {
        return join("LEFT", table, column1, column2);
    }

    @Override
    public QueryBuilder rightJoin(final Table table, final Column column1, final Column column2) {
        return join("RIGHT", table, column1, column2);
    }

    private QueryBuilder join(final String joinType, final Table table, final Column column1, final Column column2) {
        joins.add(
                new StringBuilder(joinType)
                        .append(" JOIN ")
                        .append(table.getName()).append(" ").append(table.getAlias())
                        .append(" ON ")
                        .append(getQualifiedColumnName(column1))
                        .append(" ")
                        .append(QueryOperator.EQ.getOperator())
                        .append(" ")
                        .append(getQualifiedColumnName(column2))
                        .toString()
        );
        return this;
    }

    @Override
    public QueryBuilder whereEqual(final Column column) {
        whereClauses.add(
                new StringBuilder(getQualifiedColumnName(column))
                        .append(" ")
                        .append(QueryOperator.EQ.getOperator())
                        .append(" ?")
                        .toString());
        return this;
    }

    @Override
    public QueryBuilder whereEqual(final Column column1, final Column column2) {
        whereClauses.add(
                new StringBuilder(getQualifiedColumnName(column1))
                        .append(" ")
                        .append(QueryOperator.EQ.getOperator())
                        .append(getQualifiedColumnName(column2))
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
                new StringBuilder(getQualifiedColumnName(column))
                        .append(" ")
                        .append(QueryOperator.NEQ.getOperator())
                        .append(" ?")
                        .toString());
        return this;
    }

    @Override
    public QueryBuilder whereNotEqual(final Column column1, final Column column2) {
        whereClauses.add(
                new StringBuilder(getQualifiedColumnName(column1)).append(" ")
                        .append(QueryOperator.NEQ.getOperator())
                        .append(getQualifiedColumnName(column2))
                        .toString());
        return this;
    }

    @Override
    public QueryBuilder whereLike(final Column column) {
        whereClauses.add(
                new StringBuilder(getQualifiedColumnName(column))
                        .append(" ")
                        .append(QueryOperator.LIKE.getOperator())
                        .append(" ?")
                        .toString());
        return this;
    }

    @Override
    public QueryBuilder whereIn(final Column column, final int count) {
        final StringBuilder builder = new StringBuilder(getQualifiedColumnName(column))
                .append(" ")
                .append(QueryOperator.IN.getOperator())
                .append(" ( ");

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
    public QueryBuilder groupBy(final Column... columns) {
        if (columns != null) {
            for (final Column column : columns) {
                groupBy.add(getQualifiedColumnName(column));
            }
        }
        return this;
    }

    @Override
    public QueryBuilder orderBy(final Column column, final SortOrder sortOrder) {
        orderBy.add(getQualifiedColumnName(column) + " " + sortOrder.toString());
        return this;
    }

    @Override
    public QueryBuilder limit(final Integer limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public QueryBuilder copy() {
        final StandardQueryBuilder copy = new StandardQueryBuilder();
        copy.returnFields.addAll(this.returnFields);
        copy.from.addAll(this.from);
        copy.joins.addAll(this.joins);
        copy.whereClauses.addAll(this.whereClauses);
        copy.groupBy.addAll(this.groupBy);
        copy.orderBy.addAll(this.orderBy);
        if (this.limit != null) {
            copy.limit = new Integer(this.limit.intValue());
        }
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

        if (!joins.isEmpty()) {
            builder.append(" ");
            SqlUtils.appendValues(builder, joins, " ");
        }

        if (!whereClauses.isEmpty()) {
            builder.append(" WHERE ");
            SqlUtils.appendValues(builder, whereClauses, QueryOperator.AND.getOperator());
        }

        if (!groupBy.isEmpty()) {
            builder.append(" GROUP BY ");
            SqlUtils.appendValues(builder, groupBy);
        }

        if (!orderBy.isEmpty()) {
            builder.append(" ORDER BY ");
            SqlUtils.appendValues(builder, orderBy);
        }

        if (limit != null) {
            builder.append(" LIMIT ").append(limit);
        }

        return builder.toString();
    }

    private String getQualifiedColumnName(final Column column){
        return column.getTable().getAlias() + "." + column.getName();
    }
}
