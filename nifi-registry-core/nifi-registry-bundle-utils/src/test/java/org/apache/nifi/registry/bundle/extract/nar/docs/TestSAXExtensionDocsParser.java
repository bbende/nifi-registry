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
package org.apache.nifi.registry.bundle.extract.nar.docs;

import org.apache.nifi.registry.bundle.model.ExtensionDetails;
import org.apache.nifi.registry.bundle.model.ExtensionInfo;
import org.apache.nifi.registry.bundle.model.ExtensionType;
import org.apache.nifi.registry.bundle.model.ProvidedServiceAPI;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class TestSAXExtensionDocsParser {

    private ExtensionDocsParser parser;

    @Before
    public void setup() {
        parser = new SAXExtensionDocsParser();
    }

    @Test
    public void testDocsWithProcessors() throws IOException {
        final ExtensionInfo extensionInfo = parse("src/test/resources/descriptors/extension-docs-hadoop-nar.xml");
        assertNotNull(extensionInfo);
        assertEquals("1.9.0-SNAPSHOT", extensionInfo.getSystemApiVersion());

        final Set<ExtensionDetails> extensionDetails = extensionInfo.getExtensionDetails();
        assertEquals(10, extensionDetails.size());

        final ExtensionDetails putHdfsDetails = extensionDetails.stream()
                .filter(e -> e.getName().equals("org.apache.nifi.processors.hadoop.PutHDFS"))
                .findFirst()
                .orElse(null);

        assertNotNull(putHdfsDetails);
        assertEquals(ExtensionType.PROCESSOR, putHdfsDetails.getType());
        assertEquals("Write FlowFile data to Hadoop Distributed File System (HDFS)", putHdfsDetails.getDescription());
        assertEquals(5, putHdfsDetails.getTags().size());
        assertTrue(putHdfsDetails.getTags().contains("hadoop"));
        assertTrue(putHdfsDetails.getTags().contains("HDFS"));
        assertTrue(putHdfsDetails.getTags().contains("put"));
        assertTrue(putHdfsDetails.getTags().contains("copy"));
        assertTrue(putHdfsDetails.getTags().contains("filesystem"));
        assertEquals(0, putHdfsDetails.getProvidedServiceApis().size());
        assertTrue(putHdfsDetails.isRestricted());
    }

    @Test
    public void testDocsWithControllerService() throws IOException {
        final ExtensionInfo extensionInfo = parse("src/test/resources/descriptors/extension-docs-dbcp-service-nar.xml");
        assertNotNull(extensionInfo);
        assertEquals("1.9.0-SNAPSHOT", extensionInfo.getSystemApiVersion());

        final Set<ExtensionDetails> extensionDetails = extensionInfo.getExtensionDetails();
        assertEquals(2, extensionDetails.size());

        final ExtensionDetails dbcpPoolDetails = extensionDetails.stream()
                .filter(e -> e.getName().equals("org.apache.nifi.dbcp.DBCPConnectionPool"))
                .findFirst()
                .orElse(null);

        assertNotNull(dbcpPoolDetails);
        assertEquals(ExtensionType.CONTROLLER_SERVICE, dbcpPoolDetails.getType());
        assertEquals("Provides Database Connection Pooling Service. Connections can be asked from pool and returned after\n" +
                "            usage.", dbcpPoolDetails.getDescription());
        assertEquals(6, dbcpPoolDetails.getTags().size());
        assertEquals(1, dbcpPoolDetails.getProvidedServiceApis().size());

        final ProvidedServiceAPI providedServiceAPI = dbcpPoolDetails.getProvidedServiceApis().iterator().next();
        assertNotNull(providedServiceAPI);
        assertEquals("org.apache.nifi.dbcp.DBCPService", providedServiceAPI.getClassName());
        assertEquals("org.apache.nifi", providedServiceAPI.getBundleCoordinate().getGroupId());
        assertEquals("nifi-standard-services-api-nar", providedServiceAPI.getBundleCoordinate().getArtifactId());
        assertEquals("1.9.0-SNAPSHOT", providedServiceAPI.getBundleCoordinate().getVersion());

        assertFalse(dbcpPoolDetails.isRestricted());
    }

    @Test
    public void testDocsWithReportingTask() throws IOException {
        final ExtensionInfo extensionInfo = parse("src/test/resources/descriptors/extension-docs-ambari-nar.xml");
        assertNotNull(extensionInfo);
        assertEquals("1.9.0-SNAPSHOT", extensionInfo.getSystemApiVersion());

        final Set<ExtensionDetails> extensionDetails = extensionInfo.getExtensionDetails();
        assertEquals(1, extensionDetails.size());

        final ExtensionDetails reportingTask = extensionDetails.stream()
                .filter(e -> e.getName().equals("org.apache.nifi.reporting.ambari.AmbariReportingTask"))
                .findFirst()
                .orElse(null);

        assertNotNull(reportingTask);
        assertEquals(ExtensionType.REPORTING_TASK, reportingTask.getType());
        assertNotNull(reportingTask.getDescription());
        assertEquals(3, reportingTask.getTags().size());
        assertTrue(reportingTask.getTags().contains("reporting"));
        assertTrue(reportingTask.getTags().contains("metrics"));
        assertTrue(reportingTask.getTags().contains("ambari"));
        assertEquals(0, reportingTask.getProvidedServiceApis().size());
        assertFalse(reportingTask.isRestricted());
    }

    @Test
    public void testDocsForTestComponents() throws IOException {
        final ExtensionInfo extensionInfo = parse("src/test/resources/descriptors/extension-docs-test-components.xml");
        assertNotNull(extensionInfo);
        assertEquals("1.8.0", extensionInfo.getSystemApiVersion());

        final Set<ExtensionDetails> extensionDetails = extensionInfo.getExtensionDetails();
        assertEquals(3, extensionDetails.size());

    }

    private ExtensionInfo parse(final String file) throws IOException {
        try (final InputStream inputStream = new FileInputStream(file)) {
            return parser.parse(inputStream);
        }
    }
}
