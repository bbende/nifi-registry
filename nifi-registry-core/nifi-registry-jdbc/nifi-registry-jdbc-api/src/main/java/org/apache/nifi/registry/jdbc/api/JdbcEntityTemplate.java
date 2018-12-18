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

import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.SortedSet;

public interface JdbcEntityTemplate {

    <I, E extends Entity<I>> E insert(Table<I> table, E entity, EntityValueMapper<I, E> entityValueMapper);

    <I, E extends Entity<I>> E update(Table<I> table, E entity, SortedSet<Column> columns, EntityValueMapper<I, E> entityValueMapper);

    <I, E extends Entity<I>> E update(String sql, E entity, SortedSet<Column> columns, EntityValueMapper<I, E> entityValueMapper);

    <I, E extends Entity<I>> Optional<E> queryForObject(Table<I> table, I id, EntityRowMapper<I,E> rowMapper);

    <I, E extends Entity<I>> List<E> query(Table<I> table, SortedMap<Column,Object> args, EntityRowMapper<I,E> rowMapper);

    <I, E extends Entity<I>> List<E> query(String sql, List<Object> args, EntityRowMapper<I,E> rowMapper);

    <I, E extends Entity<I>> void deleteByEntity(Table<I> table, E entity);

    <I, E extends Entity<I>> void deleteById(Table<I> table, I id);

}
