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

import java.util.Collection;

/**
 * Configures the mapping from Entity class to Table and used to resolve this mapping later.
 */
public interface TableConfiguration {

    /**
     * Maps the given entity class to the given table.
     *
     * @param entityClass the entity class
     * @param table the table
     * @param <I> the type of ID of the table
     * @param <E> the type of the entity
     */
    <I, E extends Entity<I>> void register(Class<E> entityClass, Table table);

    /**
     * Retrieves the Table for the given Entity instance.
     *
     * @param entity the entity instance
     * @param <I> the type of ID for the table and Entity
     * @return the Table
     */
    <I> Table getTable(Entity<I> entity);

    /**
     * Retrieves the Table for the given Entity class.
     *
     * @param entityClass the entity class
     * @param <I> the type of ID for the table and Entity
     * @param <E> the type of Entity
     * @return the Table
     */
    <I, E extends Entity<I>> Table getTable(Class<E> entityClass);

    /**
     * @return all tables that are registered to an Entity
     */
    Collection<Table> getTables();

}
