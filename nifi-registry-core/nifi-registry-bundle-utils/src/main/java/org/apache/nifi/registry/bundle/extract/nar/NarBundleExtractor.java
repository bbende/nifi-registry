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
package org.apache.nifi.registry.bundle.extract.nar;

import org.apache.nifi.registry.bundle.extract.BundleException;
import org.apache.nifi.registry.bundle.extract.BundleExtractor;
import org.apache.nifi.registry.bundle.extract.nar.docs.ExtensionDocsParser;
import org.apache.nifi.registry.bundle.extract.nar.docs.SAXExtensionDocsParser;
import org.apache.nifi.registry.bundle.model.BuildInfo;
import org.apache.nifi.registry.bundle.model.BundleCoordinate;
import org.apache.nifi.registry.bundle.model.BundleDetails;
import org.apache.nifi.registry.bundle.model.ExtensionInfo;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

/**
 * Implementation of ExtensionBundleExtractor for NAR bundles.
 */
public class NarBundleExtractor implements BundleExtractor {

    /**
     * The name of the JarEntry that contains the extension-docs.xml file.
     */
    private static String EXTENSION_DESCRIPTOR_ENTRY = "META-INF/docs/extension-docs.xml";

    /**
     * The pattern of a JarEntry for additionalDetails.html entries.
     */
    private static Pattern ADDITIONAL_DETAILS_ENTRY_PATTERN =
            Pattern.compile("META-INF\\/docs\\/additional-details\\/.+\\/additionalDetails.html");

    /**
     * The format of the date string in the NAR MANIFEST for Built-Timestamp.
     */
    private static String BUILT_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    @Override
    public BundleDetails extract(final InputStream inputStream) throws IOException {
        try (final JarInputStream jarInputStream = new JarInputStream(inputStream)) {
            final Manifest manifest = jarInputStream.getManifest();
            if (manifest == null) {
                throw new BundleException("NAR bundles must contain a valid MANIFEST");
            }

            final Attributes attributes = manifest.getMainAttributes();
            final BundleCoordinate bundleCoordinate = getBundleCoordinate(attributes);
            final BundleCoordinate dependencyCoordinate = getDependencyBundleCoordinate(attributes);
            final ExtensionInfo extensionInfo = getExtensionInfo(jarInputStream);
            final BuildInfo buildInfo = getBuildInfo(attributes);

            final BundleDetails.Builder builder = new BundleDetails.Builder()
                    .coordinate(bundleCoordinate)
                    .addDependencyCoordinate(dependencyCoordinate)
                    .extensionInfo(extensionInfo)
                    .buildInfo(buildInfo);

            return builder.build();
        }
    }

    private BundleCoordinate getBundleCoordinate(final Attributes attributes) {
        try {
            final String groupId = attributes.getValue(NarManifestEntry.NAR_GROUP.getManifestName());
            final String artifactId = attributes.getValue(NarManifestEntry.NAR_ID.getManifestName());
            final String version = attributes.getValue(NarManifestEntry.NAR_VERSION.getManifestName());

            return new BundleCoordinate(groupId, artifactId, version);
        } catch (Exception e) {
            throw new BundleException("Unable to obtain bundle coordinate due to: " + e.getMessage(), e);
        }
    }

    private BundleCoordinate getDependencyBundleCoordinate(final Attributes attributes) {
        try {
            final String dependencyGroupId = attributes.getValue(NarManifestEntry.NAR_DEPENDENCY_GROUP.getManifestName());
            final String dependencyArtifactId = attributes.getValue(NarManifestEntry.NAR_DEPENDENCY_ID.getManifestName());
            final String dependencyVersion = attributes.getValue(NarManifestEntry.NAR_DEPENDENCY_VERSION.getManifestName());

            final BundleCoordinate dependencyCoordinate;
            if (dependencyArtifactId != null) {
                dependencyCoordinate = new BundleCoordinate(dependencyGroupId, dependencyArtifactId, dependencyVersion);
            } else {
                dependencyCoordinate = null;
            }
            return dependencyCoordinate;
        } catch (Exception e) {
            throw new BundleException("Unable to obtain bundle coordinate for dependency due to: " + e.getMessage(), e);
        }
    }

    private BuildInfo getBuildInfo(final Attributes attributes) {
        final String buildBranch = attributes.getValue(NarManifestEntry.BUILD_BRANCH.getManifestName());
        final String buildTag = attributes.getValue(NarManifestEntry.BUILD_TAG.getManifestName());
        final String buildRevision = attributes.getValue(NarManifestEntry.BUILD_REVISION.getManifestName());
        final String buildTimestamp = attributes.getValue(NarManifestEntry.BUILD_TIMESTAMP.getManifestName());
        final String buildJdk = attributes.getValue(NarManifestEntry.BUILD_JDK.getManifestName());
        final String builtBy = attributes.getValue(NarManifestEntry.BUILT_BY.getManifestName());

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BUILT_TIMESTAMP_FORMAT);
        try {
            final Date buildDate = simpleDateFormat.parse(buildTimestamp);

            final BuildInfo buildInfo = new BuildInfo.Builder()
                    .tool(buildJdk)
                    .branch(buildBranch)
                    .tag(buildTag)
                    .revision(buildRevision)
                    .timestamp(buildDate.getTime())
                    .builtBy(builtBy)
                    .build();

            return buildInfo;

        } catch (ParseException e) {
            throw new BundleException("Unable to parse " + NarManifestEntry.BUILD_TIMESTAMP.getManifestName(), e);
        } catch (Exception e) {
            throw new BundleException("Unable to create build info for bundle due to: " + e.getMessage(), e);
        }
    }

    private ExtensionInfo getExtensionInfo(final JarInputStream jarInputStream) throws IOException {
        JarEntry jarEntry;
        while((jarEntry = jarInputStream.getNextJarEntry()) != null) {
            if (jarEntry.getName().equals(EXTENSION_DESCRIPTOR_ENTRY)) {
                try {
                    final ExtensionDocsParser docsParser = new SAXExtensionDocsParser();
                    return docsParser.parse(jarInputStream);
                } catch (Exception e) {
                    throw new BundleException("Unable to obtain extension info for bundle due to: " + e.getMessage(), e);
                }
            }
        }

        throw new BundleException("Unable to find descriptor at '" + EXTENSION_DESCRIPTOR_ENTRY + "'. " +
                "This NAR may need to be rebuilt with the latest version of the NiFi NAR Maven Plugin.");
    }

}
