-- Licensed to the Apache Software Foundation (ASF) under one or more
-- contributor license agreements.  See the NOTICE file distributed with
-- this work for additional information regarding copyright ownership.
-- The ASF licenses this file to You under the Apache License, Version 2.0
-- (the "License"); you may not use this file except in compliance with
-- the License.  You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.

-- UserGroupProvider tables

CREATE TABLE UGP_USER (
    IDENTIFIER VARCHAR(50) NOT NULL,
    IDENTITY VARCHAR(4096) NOT NULL,
    VERSION BIGINT NOT NULL DEFAULT(0),
    CONSTRAINT PK__UGP_USER_IDENTIFIER PRIMARY KEY (IDENTIFIER),
    CONSTRAINT UNIQUE__UGP_USER_IDENTITY UNIQUE (IDENTITY)
);

CREATE TABLE UGP_GROUP (
    IDENTIFIER VARCHAR(50) NOT NULL,
    IDENTITY VARCHAR(4096) NOT NULL,
    VERSION BIGINT NOT NULL DEFAULT(0),
    CONSTRAINT PK__UGP_GROUP_IDENTIFIER PRIMARY KEY (IDENTIFIER),
    CONSTRAINT UNIQUE__UGP_GROUP_IDENTITY UNIQUE (IDENTITY)
);

CREATE TABLE UGP_USER_GROUP (
    USER_IDENTIFIER VARCHAR(50) NOT NULL,
    GROUP_IDENTIFIER VARCHAR(50) NOT NULL,
    CONSTRAINT PK__UGP_USER_GROUP PRIMARY KEY (USER_IDENTIFIER, GROUP_IDENTIFIER),
    CONSTRAINT FK__UGP_USER_GROUP_USER_IDENTIFIER FOREIGN KEY (USER_IDENTIFIER) REFERENCES UGP_USER(IDENTIFIER) ON DELETE CASCADE,
    CONSTRAINT FK__UGP_USER_GROUP_GROUP_IDENTIFIER FOREIGN KEY (GROUP_IDENTIFIER) REFERENCES UGP_GROUP(IDENTIFIER) ON DELETE CASCADE
);

-- AccessPolicyProvider tables

CREATE TABLE APP_POLICY (
    IDENTIFIER VARCHAR(50) NOT NULL,
    RESOURCE VARCHAR(1000) NOT NULL,
    ACTION VARCHAR(50) NOT NULL,
    USER_IDENTIFIERS TEXT NOT NULL,
    GROUP_IDENTIFIERS TEXT NOT NULL,
    VERSION BIGINT NOT NULL DEFAULT(0),
    CONSTRAINT PK__APP_POLICY_IDENTIFIER PRIMARY KEY (IDENTIFIER),
    CONSTRAINT UNIQUE__APP_POLICY_RESOURCE_ACTION UNIQUE (RESOURCE, ACTION)
);
