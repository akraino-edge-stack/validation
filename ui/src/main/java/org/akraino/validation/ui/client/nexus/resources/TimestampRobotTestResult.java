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

public class TimestampRobotTestResult {

    private boolean result;

    private String dateOfStorage;

    private String timestamp;

    private List<WrapperRobotTestResult> wrapperRobotTestResults;

    public TimestampRobotTestResult() {

    }

    public boolean getResult() {
        return this.result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getDateOfStorage() {
        return this.dateOfStorage;
    }

    public void setDateOfStorage(String dateOfStorage) {
        this.dateOfStorage = dateOfStorage;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<WrapperRobotTestResult> getWrapperRobotTestResults() {
        return this.wrapperRobotTestResults;
    }

    public void setWrapperRobotTestResults(List<WrapperRobotTestResult> wrapperRobotTestResults) {
        this.wrapperRobotTestResults = wrapperRobotTestResults;
    }

}
