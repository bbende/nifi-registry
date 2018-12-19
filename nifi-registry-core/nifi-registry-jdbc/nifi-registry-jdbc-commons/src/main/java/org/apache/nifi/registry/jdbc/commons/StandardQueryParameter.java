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
import org.apache.nifi.registry.jdbc.api.QueryOperator;
import org.apache.nifi.registry.jdbc.api.QueryParameter;
import org.apache.nifi.registry.jdbc.api.Table;

import java.util.Collection;
import java.util.Objects;

public class StandardQueryParameter implements QueryParameter {

    private final Table table;
    private final Column column;
    private final QueryOperator operator;
    private final Object value;

    private StandardQueryParameter(final Table table, final Column column, final QueryOperator operator, final Object value) {
        this.table = Objects.requireNonNull(table);
        this.column = Objects.requireNonNull(column);
        this.operator = Objects.requireNonNull(operator);
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public Column getColumn() {
        return column;
    }

    @Override
    public QueryOperator getOperator() {
        return operator;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public static QueryParameter eq(final Table table, final Column column, final Object value) {
        return new StandardQueryParameter(table, column, QueryOperator.EQ, value);
    }

    public static QueryParameter neq(final Table table, final Column column, final Object value) {
        return new StandardQueryParameter(table, column, QueryOperator.NEQ, value);
    }

    public static QueryParameter like(final Table table, final Column column, final Object value) {
        return new StandardQueryParameter(table, column, QueryOperator.LIKE, value);
    }

    public static QueryParameter neq(final Table table, final Column column, final Collection<Object> values) {
        return new StandardQueryParameter(table, column, QueryOperator.IN, values);
    }

}
