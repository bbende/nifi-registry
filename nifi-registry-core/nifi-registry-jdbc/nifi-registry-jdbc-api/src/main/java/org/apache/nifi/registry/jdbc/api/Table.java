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

import java.util.Optional;
import java.util.SortedSet;

/**
 * Represents a table in the database.
 *
 * @param <ID> the type of id for the table
 */
public interface Table<ID> {

    /**
     * @return the name of the table
     */
    String getName();

    /**
     * @return an alias to use for the table in sql statements
     */
    String getAlias();

    /**
     * @return the id column of the table
     */
    Column getIdColumn();

    /**
     * @return all columns in the table
     */
    SortedSet<Column> getColumns();

    /**
     * @return columns that allow updating
     */
    SortedSet<Column> getUpdatableColumns();

    /**
     * @return an ID generator for the table
     */
    Optional<IDGenerator<ID>> getIDGenerator();

}
