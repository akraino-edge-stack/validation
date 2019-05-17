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
package org.akraino.validation.ui.client.jenkins;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.ws.rs.core.MultivaluedMap;

import org.akraino.validation.ui.client.jenkins.resources.CrumbResponse;
import org.akraino.validation.ui.client.jenkins.resources.Parameters;
import org.akraino.validation.ui.client.jenkins.resources.Parameters.Parameter;
import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.json.JSONConfiguration;

public final class JenkinsExecutorClient {

    private static final Logger logger = Logger.getLogger(JenkinsExecutorClient.class);

    private static final List<JenkinsExecutorClient> JENKINS_EXECUTOR_CLIENTS = new ArrayList<>();
    private static final Object LOCK = new Object();
    private final Client client;

    private final String user;
    private final String password;
    private final String baseurl;

    private JenkinsExecutorClient(String newUser, String newPassword, String newBaseurl) {
        this.user = newUser;
        this.password = newPassword;
        this.baseurl = newBaseurl;
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        this.client = Client.create(clientConfig);
        this.client.addFilter(new HTTPBasicAuthFilter(user, password));
    }

    public static synchronized JenkinsExecutorClient getInstance(@Nonnull String newUser, @Nonnull String newPassword,
            @Nonnull String newBaseurl) throws MalformedURLException {
        new URL(newBaseurl);
        for (JenkinsExecutorClient jenkinsExecutorClient : JENKINS_EXECUTOR_CLIENTS) {
            if (jenkinsExecutorClient.getBaseUrl().equals(newBaseurl) && jenkinsExecutorClient.getUser().equals(newUser)
                    && jenkinsExecutorClient.getPassword().equals(newPassword)) {
                return jenkinsExecutorClient;
            }
        }
        JenkinsExecutorClient jenkinsExecutorClient = new JenkinsExecutorClient(newUser, newPassword, newBaseurl);
        JENKINS_EXECUTOR_CLIENTS.add(jenkinsExecutorClient);
        return jenkinsExecutorClient;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    public String getBaseUrl() {
        return this.baseurl;
    }

    public URL postJobWithQueryParams(@Nonnull String jobName, @Nonnull Parameters parameters) {
        synchronized (LOCK) {
            logger.info("Trying to trigger a job to Jenkins");
            String crumb = this.getCrumb();
            if (crumb == null) {
                logger.error("Error when trying to get the Jenkins crumb. ");
                return null;
            }
            logger.debug("Jenkins crumb is: " + crumb);
            String queryParams = "?";
            for (Parameter parameter : parameters.getParameter()) {
                queryParams = queryParams + parameter.getName() + "=" + parameter.getValue() + "&";
            }
            queryParams = queryParams.substring(0, queryParams.length() - 1);
            WebResource webResource =
                    this.client.resource(this.getBaseUrl() + "/job/" + jobName + "/buildWithParameters" + queryParams);
            logger.debug("Request URI of post: " + webResource.getURI().toString());
            WebResource.Builder builder = webResource.getRequestBuilder();
            ClientResponse response;
            try {
                builder.header("Jenkins-Crumb", crumb);
                response = builder.type("application/json").post(ClientResponse.class, String.class);
            } catch (ClientHandlerException ex) {
                logger.error("Connection error. ", ex);
                return null;
            }
            if (response.getStatus() != 200 && response.getStatus() != 201) {
                logger.error("Post of Jenkins job failed. HTTP error code : " + response.getStatus() + " and message: "
                        + response.getEntity(String.class));
                return null;
            }
            logger.info("Jenkins job has been successfully triggered");
            URL buildQueueUrl = null;
            MultivaluedMap<String, String> responseValues = response.getHeaders();
            Iterator<String> it = responseValues.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (key.equals("Location")) {
                    try {
                        buildQueueUrl = new URL(responseValues.getFirst(key));
                    } catch (MalformedURLException e) {
                        logger.error("Error during build queue URL formulation.");
                    }
                }
            }
            return buildQueueUrl;
        }
    }

    private String getCrumb() {
        logger.info("Get crumb attempt");
        String crumbUri = baseurl + "/crumbIssuer/api/json";
        WebResource webResource = this.client.resource(crumbUri);
        ClientResponse response;
        try {
            response = webResource.accept("application/json").type("application/json").get(ClientResponse.class);
        } catch (ClientHandlerException ex) {
            logger.error("Connection error. ", ex);
            return null;
        }
        if (response.getStatus() == 201 || response.getStatus() == 200) {
            CrumbResponse crumbResponse = response.getEntity(CrumbResponse.class);
            logger.info("Successful crumb retrieval.");
            return crumbResponse.getCrumb();
        }
        logger.error("Get crumb attempt towards Jenkins failed. HTTP error code: " + response.getStatus()
                + " and message: " + response.getEntity(String.class));
        return null;
    }

}
