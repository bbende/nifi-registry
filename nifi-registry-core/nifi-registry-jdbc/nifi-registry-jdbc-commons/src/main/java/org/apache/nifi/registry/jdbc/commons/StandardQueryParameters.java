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
import org.apache.nifi.registry.jdbc.api.QueryParameter;
import org.apache.nifi.registry.jdbc.api.QueryParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class StandardQueryParameters implements QueryParameters {

    private final SortedSet<QueryParameter> parameters;

    private StandardQueryParameters(final QueryParameter[] parameters) {
        this.parameters = Collections.unmodifiableSortedSet(
                new TreeSet<>(Arrays.asList(parameters)));
    }

    @Override
    public SortedSet<Column> getColumns() {
        final SortedSet<Column> columns = new TreeSet<>();
        parameters.forEach(p -> columns.add(p.getColumn()));
        return columns;
    }

    @Override
    public List<Object> getValues() {
        final List<Object> values = new ArrayList<>();
        parameters.forEach(p -> values.add(p.getValue()));
        return values;
    }

    @Override
    public SortedSet<QueryParameter> getParameters() {
        return parameters;
    }

    public static QueryParameters of(final QueryParameter... parameters) {
        return new StandardQueryParameters(parameters);
    }

    public static QueryParameters empty() {
        return new StandardQueryParameters(new QueryParameter[0]);
    }

}
