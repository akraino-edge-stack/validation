/*
 * Copyright (c) 2019 AT&T Intellectual Property. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.akraino.validation.ui.client.nexus.resources;

import java.util.List;

import org.akraino.validation.ui.data.Lab;

public class WTimestampWRobotTestResult {

    private String blueprintName;

    private String version;

    private Lab lab;

    private List<TimestampWRobotTestResult> timestampWRobotTestResults;

    public WTimestampWRobotTestResult() {

    }

    public String getBlueprintName() {
        return this.blueprintName;
    }

    public void setBlueprintName(String blueprintName) {
        this.blueprintName = blueprintName;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Lab getLab() {
        return this.lab;
    }

    public void setLab(Lab lab) {
        this.lab = lab;
    }

    public List<TimestampWRobotTestResult> getTimestampWRobotTestResults() {
        return this.timestampWRobotTestResults;
    }

    public void setTimestampWRobotTestResult(List<TimestampWRobotTestResult> timestampWRobotTestResults) {
        this.timestampWRobotTestResults = timestampWRobotTestResults;
    }

}
