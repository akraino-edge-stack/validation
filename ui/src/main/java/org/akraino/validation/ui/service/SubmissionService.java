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

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.akraino.validation.ui.client.jenkins.JenkinsExecutorClient;
import org.akraino.validation.ui.client.jenkins.resources.Parameter;
import org.akraino.validation.ui.client.jenkins.resources.Parameters;
import org.akraino.validation.ui.common.PropertyUtil;
import org.akraino.validation.ui.config.AppInitializer;
import org.akraino.validation.ui.dao.SubmissionDAO;
import org.akraino.validation.ui.data.SubmissionStatus;
import org.akraino.validation.ui.entity.Submission;
import org.akraino.validation.ui.service.utils.SubmissionServiceHelper;
import org.akraino.validation.ui.service.utils.PrioritySupplier;
import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

@Service
@Transactional
public class SubmissionService {

    private static final Logger logger = Logger.getLogger(SubmissionService.class);

    @Autowired
    private SubmissionDAO submissionDAO;

    @Autowired
    private SubmissionServiceHelper submissionServiceHelper;

    public Submission saveSubmission(Submission submission) {
        submission.setSubmissionStatus(SubmissionStatus.Submitted);
        submissionDAO.saveOrUpdate(submission);

        JenkinsTriggerSubmissionJob task = new JenkinsTriggerSubmissionJob(submission);
        CompletableFuture<Submission> completableFuture =
                CompletableFuture.supplyAsync(new PrioritySupplier<>(1, task::execute), AppInitializer.executorService);
        completableFuture.thenAcceptAsync(t -> this.callbackNotify(t));

        return submission;
    }

    public List<Submission> getSubmissions() {
        return submissionDAO.getSubmissions();
    }

    public Submission getSubmission(String submissionId) {
        return submissionDAO.getSubmission(Integer.valueOf(submissionId));
    }

    public void deleteSubmission(Integer id) {
        submissionDAO.deleteSubmission(id);
    }

    public void deleteAll() {
        submissionDAO.deleteAll();
    }

    private void callbackNotify(Submission submission) {
        if (submission == null) {
            return;
        }
        submission.setSubmissionStatus(SubmissionStatus.Running);
        submissionServiceHelper.saveSubmission(submission);
    }

    private class JenkinsTriggerSubmissionJob {

        private Submission submission;

        public JenkinsTriggerSubmissionJob(Submission submission) {
            this.submission = submission;
        }

        public Submission execute() {
            String jenkinsUrl = PropertyUtil.getInstance().getProperty("jenkins.url");
            String jenkinsUserName = PropertyUtil.getInstance().getProperty("jenkins.user.name");
            String jenkinUserPassword = PropertyUtil.getInstance().getProperty("jenkins.user.pwd");
            String jenkinsJobName = PropertyUtil.getInstance().getProperty("jenkins.job.name");
            List<Parameter> listOfParameters = new ArrayList<Parameter>();
            Parameters parameters = new Parameters();
            Parameter parameter = new Parameter();
            parameter.setName("SUBMISSION_ID");
            parameter.setValue(String.valueOf(submission.getSubmissionId()));
            listOfParameters.add(parameter);
            parameter = new Parameter();
            parameter.setName("BLUEPRINT");
            parameter.setValue(submission.getBlueprintData().getBlueprint());
            listOfParameters.add(parameter);
            parameter = new Parameter();
            parameter.setName("LAYER");
            parameter.setValue(submission.getBlueprintData().getLayer().name());
            listOfParameters.add(parameter);
            parameter = new Parameter();
            parameter.setName("UI_IP");
            String localIP = null;
            try (final DatagramSocket socket = new DatagramSocket()) {
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                localIP = socket.getLocalAddress().getHostAddress();
            } catch (SocketException | UnknownHostException e1) {
                logger.error(e1);
                return null;
            }
            parameter.setValue(localIP);
            listOfParameters.add(parameter);
            parameters.setParameter(listOfParameters);
            JenkinsExecutorClient jenkinsExecutorClient;
            try {
                jenkinsExecutorClient =
                        JenkinsExecutorClient.getInstance(jenkinsUserName, jenkinUserPassword, jenkinsUrl);
                submission.setJenkinsQueueJobItemUrl(
                        jenkinsExecutorClient.postJobWithQueryParams(jenkinsJobName, parameters).toString());
            } catch (MalformedURLException | KeyManagementException | HttpException | ClientHandlerException
                    | UniformInterfaceException | NoSuchAlgorithmException e) {
                logger.error(e);
                return null;
            }
            return submission;
        }
    }

}
