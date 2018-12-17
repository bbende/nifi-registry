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

public interface QueryBuilder {

    QueryBuilder select(Table table, SortedSet<Column> columns);

    QueryBuilder select(Table table, Column column);

    QueryBuilder select(Table table, Column column, String columnAlias);

    QueryBuilder select(String expression);

    QueryBuilder from(Table table);

    QueryBuilder whereEqual(Table table, Column column);

    QueryBuilder whereNotEqual(Table table, Column column);

    QueryBuilder whereLike(Table table, Column column);

    QueryBuilder whereIn(Table table, Column column, int count);

    QueryBuilder where(String clause);

    String build();
}
