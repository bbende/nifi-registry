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

import org.apache.nifi.registry.jdbc.api.IDGenerator;
import org.apache.nifi.registry.jdbc.api.Table;

import java.util.Objects;
import java.util.Optional;

public abstract class AbstractTable<ID> implements Table<ID> {

    private final String name;
    private final String alias;
    private final Optional<IDGenerator<ID>> idGenerator;

    public AbstractTable(final String name, final String alias) {
       this(name, alias, null);
    }

    public AbstractTable(final String name, final String alias, final IDGenerator<ID> idGenerator) {
        this.name = Objects.requireNonNull(name);
        this.alias = Objects.requireNonNull(alias);
        this.idGenerator = Optional.ofNullable(idGenerator);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public Optional<IDGenerator<ID>> getIDGenerator() {
        return idGenerator;
    }

}
