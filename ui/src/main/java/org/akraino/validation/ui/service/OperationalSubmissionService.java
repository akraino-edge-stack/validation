/* 
 * Copyright (c) 2019 AT&T Intellectual Property. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.akraino.validation.ui.service;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.akraino.validation.ui.client.jenkins.JenkinsExecutorClient;
import org.akraino.validation.ui.client.jenkins.resources.Parameters;
import org.akraino.validation.ui.client.jenkins.resources.Parameters.Parameter;
import org.akraino.validation.ui.common.PropertyUtil;
import org.akraino.validation.ui.config.AppInitializer;
import org.akraino.validation.ui.dao.OperationalSubmissionDAO;
import org.akraino.validation.ui.dao.SubmissionBaseInfoDAO;
import org.akraino.validation.ui.data.OperationalSubmissionWithJenkinsQueueJobItemUrl;
import org.akraino.validation.ui.data.SubmissionStatus;
import org.akraino.validation.ui.entity.OperationalSubmission;
import org.akraino.validation.ui.entity.SubmissionBaseInfo;
import org.akraino.validation.ui.service.utils.OperationalSubmissionServiceHelper;
import org.akraino.validation.ui.service.utils.PrioritySupplier;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OperationalSubmissionService {

    private static final Logger logger = Logger.getLogger(OperationalSubmissionService.class);

    @Autowired
    private OperationalSubmissionDAO operationalSubmissionDAO;

    @Autowired
    private SubmissionBaseInfoDAO submissionBaseInfoDAO;

    @Autowired
    private OperationalSubmissionServiceHelper operationalSubmissionServiceHelper;

    @Autowired
    private ResultService resultService;

    public OperationalSubmission saveOperationalSubmission(SubmissionBaseInfo submissionBaseInfo) {
        submissionBaseInfoDAO.saveOrUpdate(submissionBaseInfo);
        OperationalSubmission operationalSubmission = new OperationalSubmission();
        operationalSubmission.setSubmissionStatus(SubmissionStatus.Submitted);
        operationalSubmission.setsubmissionBaseInfo(submissionBaseInfo);
        operationalSubmissionDAO.saveOrUpdate(operationalSubmission);

        JenkinsTriggerSubmissionJob task = new JenkinsTriggerSubmissionJob(operationalSubmission);
        CompletableFuture<OperationalSubmissionWithJenkinsQueueJobItemUrl> completableFuture =
                CompletableFuture.supplyAsync(new PrioritySupplier<>(1, task::execute), AppInitializer.executorService);
        completableFuture.thenAcceptAsync(t -> this.callbackNotify(t));

        return operationalSubmission;
    }

    public List<OperationalSubmission> getOperationalSubmissions() {

        return operationalSubmissionDAO.getOperationalSubmissions();

    }

    public void deleteOperationalSubmission(Integer id) {

        operationalSubmissionDAO.deleteOperationalSubmission(id);

    }

    public void deleteAll() {
        operationalSubmissionDAO.deleteAll();
    }

    public void callbackNotify(OperationalSubmissionWithJenkinsQueueJobItemUrl operationalSubmissionWithBuildQueueUrl) {
        if (operationalSubmissionWithBuildQueueUrl == null) {
            return;
        }
        OperationalSubmission operationalSubmission = operationalSubmissionWithBuildQueueUrl.getOperationalSubmission();
        operationalSubmission.setJenkinsQueueJobItemUrl(
                operationalSubmissionWithBuildQueueUrl.getJenkinsQueueJobItemUrl().toString());
        operationalSubmission.setSubmissionStatus(SubmissionStatus.Running);
        operationalSubmissionServiceHelper.saveOperationalSubmission(operationalSubmission);
        operationalSubmission.setNexusResultUrl(resultService.getResult(operationalSubmission).toString());
        operationalSubmissionServiceHelper.saveOperationalSubmission(operationalSubmission);
    }

    private class JenkinsTriggerSubmissionJob {

        private OperationalSubmission operationalSubmission;

        public JenkinsTriggerSubmissionJob(OperationalSubmission operationalSubmission) {
            this.operationalSubmission = operationalSubmission;
        }

        public OperationalSubmissionWithJenkinsQueueJobItemUrl execute() {
            String jenkinsUrl = PropertyUtil.getInstance().getProperty("jenkins.url");
            String jenkinsUserName = PropertyUtil.getInstance().getProperty("jenkins.user.name");
            String jenkinUserPassword = PropertyUtil.getInstance().getProperty("jenkins.user.pwd");
            String jenkinsJobName = PropertyUtil.getInstance().getProperty("jenkins.job.name");
            List<Parameter> listOfParameters = new ArrayList<Parameter>();
            Parameters parameters = new Parameters();
            Parameter parameter = parameters.new Parameter();
            parameter.setName("BLUEPRINT");
            parameter.setValue(operationalSubmission.getSubmissionBaseInfo().getBlueprintData().getBlueprint());
            parameter.setName("LAYER");
            parameter.setValue(
                    operationalSubmission.getSubmissionBaseInfo().getBlueprintData().getLayerData().getLayer().name());
            listOfParameters.add(parameter);
            parameters.setParameter(listOfParameters);
            try {
                JenkinsExecutorClient jenkinsExecutorClient =
                        JenkinsExecutorClient.getInstance(jenkinsUserName, jenkinUserPassword, jenkinsUrl);
                OperationalSubmissionWithJenkinsQueueJobItemUrl result =
                        new OperationalSubmissionWithJenkinsQueueJobItemUrl();
                result.setJenkinsQueueJobItemUrl(
                        jenkinsExecutorClient.postJobWithQueryParams(jenkinsJobName, parameters));
                result.setOperationalSubmission(operationalSubmission);
                return result;
            } catch (MalformedURLException ex) {
                logger.error("Malformed Url value. ", ex);
            }
            return null;
        }
    }

}
