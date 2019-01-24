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
package org.apache.nifi.registry.jdbc.api;

import java.util.SortedSet;

/**
 * Builder to create a SQL select statement based on Tables and Columns.
 */
public interface QueryBuilder {

    /**
     * @param columns columns to add to the return fields using their default aliases
     * @return this builder
     */
    QueryBuilder select(SortedSet<Column> columns);

    /**
     *
     * @param column the column to add to the return fields using it's default alias
     * @return this builder
     */
    QueryBuilder select(Column column);

    /**
     * @param column a column to count in the return fields
     * @return this builder
     */
    QueryBuilder selectCount(Column column);

    /**
     * @param tables the tables for the from clause
     * @return this builder
     */
    QueryBuilder from(Table ... tables);

    /**
     * @param table the table to join on
     * @param column1 the first column of the join
     * @param column2 the second column of the join
     * @return this builder
     */
    QueryBuilder innerJoin(Table table, Column column1, Column column2);

    /**
     * @param table the table to join on
     * @param column1 the first column of the join
     * @param column2 the second column of the join
     * @return this builder
     */
    QueryBuilder outerJoin(Table table, Column column1, Column column2);

    /**
     * @param table the table to join on
     * @param column1 the first column of the join
     * @param column2 the second column of the join
     * @return this builder
     */
    QueryBuilder leftJoin(Table table, Column column1, Column column2);

    /**
     * @param table the table to join on
     * @param column1 the first column of the join
     * @param column2 the second column of the join
     * @return this builder
     */
    QueryBuilder rightJoin(Table table, Column column1, Column column2);

    /**
     * @param column a column to add to the where clause with am equal operator and a prepared statement placeholder
     * @return this builder
     */
    QueryBuilder whereEqual(Column column);

    /**
     * @param column1 the first column for the equal clause
     * @param column2 the second column for the equal clause
     * @return this builder
     */
    QueryBuilder whereEqual(Column column1, Column column2);

    /**
     * @param columns the columns to add to the where clause with equal operators and prepared statement placeholders
     * @return this builder
     */
    QueryBuilder whereEqual(SortedSet<Column> columns);

    /**
     * @param column a column to add to the where clause with a not-equal operator and prepared statement placeholder
     * @return this builder
     */
    QueryBuilder whereNotEqual(Column column);

    /**
     * @param column1 the first column of a not equal clause
     * @param column2 the second column of a not equal clause
     * @return this builder
     */
    QueryBuilder whereNotEqual(Column column1, Column column2);

    /**
     * @param column a column to add to the where clause with a like operator and a prepared statement placeholder.
     * @return this builder
     */
    QueryBuilder whereLike(Column column);

    /**
     * @param column a column to add to the where clause with an IN operator
     * @param count the number of values in the IN clause
     * @return this builder
     */
    QueryBuilder whereIn(Column column, int count);

    /**
     * @param clause a raw string to add to the where clause
     * @return this builder
     */
    QueryBuilder where(String clause);

    /**
     * @param columns the columns to group by
     * @return this builder
     */
    QueryBuilder groupBy(Column ... columns);

    /**
     * @param column the column to order by
     * @param sortOrder the order (ASC or DESC)
     * @return this builder
     */
    QueryBuilder orderBy(Column column, SortOrder sortOrder);

    /**
     * @param limit the number of rows to limit the results to
     * @return this builder
     */
    QueryBuilder limit(Integer limit);

    /**
     * @return a copy of this builder
     */
    QueryBuilder copy();

    /**
     * @return the SQL string created from this builder
     */
    String build();

}
