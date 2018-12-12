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

/**
 * The elements from the extension docs xml needed to create ExtensionInfo and ExtensionDetails.
 */
public enum ExtensionDocsElement {

    NIFI_API_VERSION("nifiApiVersion"),
    EXTENSION("extension"),
    NAME("name"),
    DESCRIPTION("description"),
    TYPE("type"),
    TAGS("tags"),
    TAG("tag"),
    RESTRICTION("restriction"),
    REQUIRED_PERMISSION("requiredPermission"),
    PROVIDED_SERVICE_APIS("providedServiceAPIs"),
    SERVICE("service"),
    CLASS_NAME("className"),
    GROUP_ID("groupId"),
    ARTIFACT_ID("artifactId"),
    VERSION("version"),
    OTHER("other")
    ;

    private String elementName;

    ExtensionDocsElement(final String elementName) {
        this.elementName = elementName;
    }

    public String getElementName() {
        return elementName;
    }

    public static ExtensionDocsElement fromElementName(final String elementName) {
        for (final ExtensionDocsElement element : values()) {
            if (element.getElementName().equals(elementName)) {
                return element;
            }
        }

        return OTHER;
    }
}
