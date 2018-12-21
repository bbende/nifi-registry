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
import org.apache.nifi.registry.db.entity.ExtensionBundleEntity;
import org.apache.nifi.registry.db.entity.ExtensionBundleVersionDependencyEntity;
import org.apache.nifi.registry.db.entity.ExtensionBundleVersionEntity;
import org.apache.nifi.registry.db.entity.ExtensionEntity;
import org.apache.nifi.registry.db.entity.ExtensionTagEntity;
import org.apache.nifi.registry.db.entity.FlowEntity;
import org.apache.nifi.registry.db.entity.FlowSnapshotEntity;
import org.apache.nifi.registry.db.entity.KeyEntity;
import org.apache.nifi.registry.jdbc.api.TableConfiguration;
import org.apache.nifi.registry.jdbc.commons.StandardTableConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Tables {

    public static final BucketTable BUCKET = new BucketTable();
    public static final BucketItemTable BUCKET_ITEM = new BucketItemTable();

    public static final FlowTable FLOW = new FlowTable();
    public static final FlowSnapshotTable FLOW_SNAPSHOT = new FlowSnapshotTable();

    public static final ExtensionBundleTable EXTENSION_BUNDLE = new ExtensionBundleTable();
    public static final ExtensionBundleVersionTable EXTENSION_BUNDLE_VERSION = new ExtensionBundleVersionTable();
    public static final ExtensionBundleVersionDependencyTable EXTENSION_BUNDLE_VERSION_DEPENDENCY = new ExtensionBundleVersionDependencyTable();

    public static final ExtensionTable EXTENSION = new ExtensionTable();
    public static final ExtensionTagTable EXTENSION_TAG = new ExtensionTagTable();

    public static final SigningKeyTable SIGNING_KEY = new SigningKeyTable();


    private final TableConfiguration tableConfiguration;

    public Tables() {
        tableConfiguration = new StandardTableConfiguration();
        tableConfiguration.register(BucketEntity.class, BUCKET);
        tableConfiguration.register(BucketItemEntity.class, BUCKET_ITEM);
        tableConfiguration.register(FlowEntity.class, FLOW);
        tableConfiguration.register(FlowSnapshotEntity.class, FLOW_SNAPSHOT);
        tableConfiguration.register(KeyEntity.class, SIGNING_KEY);
        tableConfiguration.register(ExtensionBundleEntity.class, EXTENSION_BUNDLE);
        tableConfiguration.register(ExtensionBundleVersionEntity.class, EXTENSION_BUNDLE_VERSION);
        tableConfiguration.register(ExtensionBundleVersionDependencyEntity.class, EXTENSION_BUNDLE_VERSION_DEPENDENCY);
        tableConfiguration.register(ExtensionEntity.class, EXTENSION);
        tableConfiguration.register(ExtensionTagEntity.class, EXTENSION_TAG);
    }

    @Bean
    public TableConfiguration getTableConfiguration() {
        return tableConfiguration;
    }

}
