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
import java.net.URL;

import org.akraino.validation.ui.client.jenkins.JenkinsExecutorClient;
import org.akraino.validation.ui.client.jenkins.resources.QueueJobItem;
import org.akraino.validation.ui.client.jenkins.resources.QueueJobItem.Executable;
import org.akraino.validation.ui.common.PropertyUtil;
import org.akraino.validation.ui.entity.OperationalSubmission;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ResultService {

    private static final Logger logger = Logger.getLogger(ResultService.class);

    public URL getResult(OperationalSubmission operationalSubmission) {
        
        String jenkinsUrl = PropertyUtil.getInstance().getProperty("jenkins.url");
        String jenkinsUserName = PropertyUtil.getInstance().getProperty("jenkins.user.name");
        String jenkinUserPassword = PropertyUtil.getInstance().getProperty("jenkins.user.pwd");

        Executable executable = null;
        while (executable == null) {
            JenkinsExecutorClient jenkinsExecutorClient;
            try {
                jenkinsExecutorClient =
                        JenkinsExecutorClient.getInstance(jenkinsUserName, jenkinUserPassword, jenkinsUrl);
                QueueJobItem queueJobItem = jenkinsExecutorClient
                        .getQueueJobItem(new URL(operationalSubmission.getJenkinsQueueJobItemUrl()));
                executable = queueJobItem.getExecutable();
                Thread.sleep(2000);
            } catch (MalformedURLException | InterruptedException e1) {
                logger.error(e1);
            }
        }
        try {
            return new URL(PropertyUtil.getInstance().getProperty("nexus.results.url") + "/job/validation/"
                    + String.valueOf(executable.getNumber()));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            logger.error(e);
        }
        return null;
    }

}
