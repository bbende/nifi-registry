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
package org.apache.nifi.registry.bundle.model;


import org.apache.nifi.registry.bundle.extract.BundleExtractor;
import org.apache.nifi.registry.bundle.util.BundleUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Details for a given bundle which are obtained from a given {@link BundleExtractor}.
 */
public class BundleDetails {

    private final BundleCoordinate bundleCoordinate;
    private final Set<BundleCoordinate> dependencyBundleCoordinates;
    private final ExtensionInfo extensionInfo;
    private final BuildInfo buildInfo;

    private BundleDetails(final Builder builder) {
        this.bundleCoordinate = builder.bundleCoordinate;
        this.dependencyBundleCoordinates = Collections.unmodifiableSet(new HashSet<>(builder.dependencyBundleCoordinates));
        this.extensionInfo = builder.extensionInfo;
        this.buildInfo = builder.buildInfo;

        BundleUtils.validateNotNull("Bundle Coordinate", this.bundleCoordinate);
        BundleUtils.validateNotNull("Dependency Coordinates", this.dependencyBundleCoordinates);
        BundleUtils.validateNotNull("Extension Info", this.extensionInfo);
        BundleUtils.validateNotNull("Build Info", this.buildInfo);
    }

    public BundleCoordinate getBundleCoordinate() {
        return bundleCoordinate;
    }

    public Set<BundleCoordinate> getDependencyBundleCoordinates() {
        return dependencyBundleCoordinates;
    }

    public ExtensionInfo getExtensionInfo() {
        return extensionInfo;
    }

    public BuildInfo getBuildInfo() {
        return buildInfo;
    }

    /**
     * Builder for creating instances of BundleDetails.
     */
    public static class Builder {

        private BundleCoordinate bundleCoordinate;
        private Set<BundleCoordinate> dependencyBundleCoordinates = new HashSet<>();
        private ExtensionInfo extensionInfo;
        private BuildInfo buildInfo;

        public Builder coordinate(final BundleCoordinate bundleCoordinate) {
            this.bundleCoordinate = bundleCoordinate;
            return this;
        }

        public Builder addDependencyCoordinate(final BundleCoordinate dependencyCoordinate) {
            if (dependencyCoordinate != null) {
                this.dependencyBundleCoordinates.add(dependencyCoordinate);
            }
            return this;
        }

        public Builder extensionInfo(final ExtensionInfo extensionInfo) {
            this.extensionInfo = extensionInfo;
            return this;
        }

        public Builder buildInfo(final BuildInfo buildInfo) {
            this.buildInfo = buildInfo;
            return this;
        }

        public BundleDetails build() {
            return new BundleDetails(this);
        }
    }

}
