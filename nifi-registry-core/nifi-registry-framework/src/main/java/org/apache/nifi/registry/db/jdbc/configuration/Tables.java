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
package org.apache.nifi.registry.db.jdbc.configuration;

import org.apache.nifi.registry.db.entity.BucketEntity;
import org.apache.nifi.registry.db.entity.BucketItemEntity;
import org.apache.nifi.registry.db.entity.ExtensionEntity;
import org.apache.nifi.registry.db.entity.FlowEntity;
import org.apache.nifi.registry.jdbc.api.TableConfiguration;
import org.apache.nifi.registry.jdbc.commons.StandardTableConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Tables {

    public static final BucketTable BUCKET = new BucketTable();
    public static final BucketItemTable BUCKET_ITEM = new BucketItemTable();
    public static final FlowTable FLOW = new FlowTable();
    public static final ExtensionTable EXTENSION = new ExtensionTable();

    private final TableConfiguration tableConfiguration;

    public Tables() {
        tableConfiguration = new StandardTableConfiguration();
        tableConfiguration.register(BucketEntity.class, Tables.BUCKET);
        tableConfiguration.register(BucketItemEntity.class, Tables.BUCKET_ITEM);
        tableConfiguration.register(FlowEntity.class, Tables.FLOW);
        tableConfiguration.register(ExtensionEntity.class, Tables.EXTENSION);
    }

    @Bean
    public TableConfiguration getTableConfiguration() {
        return tableConfiguration;
    }

}
