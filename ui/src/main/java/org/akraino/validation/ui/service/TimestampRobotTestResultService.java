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
package org.akraino.validation.ui.service;

import java.util.List;

import org.akraino.validation.ui.dao.TimestampRobotTestResultDAO;
import org.akraino.validation.ui.entity.LabInfo;
import org.akraino.validation.ui.entity.TimestampRobotTestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TimestampRobotTestResultService {

    @Autowired
    private TimestampRobotTestResultDAO tsTestResultDAO;

    public List<TimestampRobotTestResult> getTimestampRobotTestResults() {
        return tsTestResultDAO.getTimestampRobotTestResults();
    }

    public TimestampRobotTestResult getTimestampRobotTestResult(Integer resultId) {
        return tsTestResultDAO.getTimestampRobotTestResult(resultId);
    }

    public List<TimestampRobotTestResult> getTimestampRobotTestResults(String blueprintName, String version,
            LabInfo labInfo) {
        return tsTestResultDAO.getTimestampRobotTestResults(blueprintName, version, labInfo);
    }

    public TimestampRobotTestResult getTimestampRobotTestResult(String blueprintName, String version, LabInfo labInfo,
            String timestamp) {
        return tsTestResultDAO.getTimestampRobotTestResult(blueprintName, version, labInfo, timestamp);
    }

    public void saveOrUpdate(TimestampRobotTestResult tsTestResult) {
        tsTestResultDAO.saveOrUpdate(tsTestResult);
    }

    public void deleteTimestampRobotTestResult(String blueprintName, String version, LabInfo labInfo,
            String timestamp) {
        tsTestResultDAO.deleteTimestampRobotTestResult(blueprintName, version, labInfo, timestamp);
    }

    public void deleteTimestampRobotTestResult(TimestampRobotTestResult tsTestResult) {
        tsTestResultDAO.deleteTimestampRobotTestResult(tsTestResult);
    }

}
