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

import java.util.Comparator;
import java.util.Objects;

public class StandardColumn implements Column {

    private final String name;
    private final boolean updatable;

    private StandardColumn(final Builder builder) {
        this.name = Objects.requireNonNull(builder.name);
        this.updatable = builder.updatable;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isUpdatable() {
        return updatable;
    }

    @Override
    public int compareTo(Column o) {
        return Comparator.comparing(Column::getName).compare(this, o);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Column)) {
            return false;
        }

        final Column other = (Column)obj;
        return Objects.equals(name, other.getName());
    }

    public static Column create(final String name) {
        return new Builder().name(name).updatable(false).build();
    }

    public static Column createUpdatable(final String name) {
        return new Builder().name(name).updatable().build();
    }

    public static class Builder {

        private String name;
        private boolean updatable;

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder updatable(boolean updatable) {
            this.updatable = updatable;
            return this;
        }

        public Builder updatable() {
            this.updatable = true;
            return this;
        }

        public Column build() {
            return new StandardColumn(this);
        }

    }
}
