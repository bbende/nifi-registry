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

import java.util.Collection;

public class SqlUtils {

    static void appendValues(final StringBuilder builder, final Collection<String> values) {
        appendValues(builder, values, ",");
    }

    static void appendValues(final StringBuilder builder, final Collection<String> values, final String separator) {
        boolean first = true;
        for (final String value : values) {
            if (!first) {
                builder.append(" ").append(separator);
            }
            builder.append(value);
            first = false;
        }
    }

    static void appendValues(final StringBuilder builder, final String value, final int count) {
        for (int i=0; i < count; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(value);
        }
    }

}
