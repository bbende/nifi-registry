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

import org.apache.nifi.registry.jdbc.api.Entity;
import org.apache.nifi.registry.jdbc.api.Table;
import org.apache.nifi.registry.jdbc.api.TableConfiguration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StandardTableConfiguration implements TableConfiguration {

    private Map<Class<? extends Entity>,Table> tableMap = new HashMap<>();

    @Override
    public <I, E extends Entity<I>> void register(final Class<E> entityClass, final Table table) {
        if (tableMap.containsKey(entityClass)) {
            throw new IllegalStateException("Entity class '" + entityClass.getCanonicalName()
                    + "' already registered to a different table");
        }

        tableMap.put(entityClass, table);
    }

    @Override
    public <I> Table getTable(final Entity<I> entity) {
        return tableMap.get(entity.getClass());
    }

    @Override
    public <I, E extends Entity<I>> Table getTable(final Class<E> entityClass) {
        return tableMap.get(entityClass);
    }

    @Override
    public Collection<Table> getTables() {
        return tableMap.values();
    }
}
