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
import java.util.List;
import java.util.Optional;

/**
 * A repository than can perform CRUD operations for a given type of Entity.
 *
 * @param <ID> the type of ID for the Entity
 * @param <E> the type of Entity
 */
public interface JdbcRepository<ID, E extends Entity<ID>> {

    /**
     * Creates the given entity.
     *
     * @param entity the entity to create
     * @return the created entity
     */
    E create(E entity);

    /**
     * Updates the given entity using any non-null fields.
     *
     * @param entity the entity to update
     * @return the updated entity
     */
    E update(E entity);

    /**
     * Retrieves an entity by id.
     *
     * @param id the id of the entity to retrieve
     * @return the optional entity
     */
    Optional<E> findById(ID id);

    /**
     * Determines if an entity exists with the given id.
     *
     * @param id the of the entity
     * @return true if the entity exists, false otherwise
     */
    boolean existsById(ID id);

    /**
     * @return all entities of the given type
     */
    List<E> findAll();

    /**
     * @param ids the ids of the entities to return
     * @return the entities with the given ids
     */
    List<E> findAllById(Collection<ID> ids);

    /**
     * Retrieves entities according to the supplied query parameters.
     *
     * @param params the query params
     * @return the matching entities
     */
    List<E> findByQueryParams(QueryParameters params);

    /**
     * Deletes an entity by id.
     *
     * @param id the id of the entity to delete
     */
    void deleteById(ID id);

    /**
     * Deletes the given entity.
     *
     * @param entity the entity to delete
     */
    void delete(E entity);

}
