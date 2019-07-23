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
package org.akraino.validation.ui.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.akraino.validation.ui.client.nexus.resources.TimestampRobotTestResult;
import org.akraino.validation.ui.client.nexus.resources.WrapperTimestampRobotTestResult;
import org.akraino.validation.ui.data.Lab;
import org.akraino.validation.ui.entity.LabInfo;
import org.akraino.validation.ui.service.LabService;
import org.akraino.validation.ui.service.ResultService;
import org.akraino.validation.ui.service.TimestampRobotTestResultService;
import org.akraino.validation.ui.service.utils.PrioritySupplier;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WrapperTimestampRobotTestResultsGetter implements ApplicationListener<ContextRefreshedEvent> {

    private static final EELFLoggerDelegate LOGGER = EELFLoggerDelegate
            .getLogger(WrapperTimestampRobotTestResultsGetter.class);

    @Autowired
    ResultService resultService;

    @Autowired
    TimestampRobotTestResultService tsService;

    @Autowired
    LabService labService;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ExecutorServiceInitializer.class);
        ExecutorService service = (ExecutorService) context.getBean("executorService");
        WrapperTimestampRobotTestResultsGetterExecution task = new WrapperTimestampRobotTestResultsGetterExecution();
        CompletableFuture<List<WrapperTimestampRobotTestResult>> completableFuture = CompletableFuture
                .supplyAsync(new PrioritySupplier<>(1, task::execute), service);
        completableFuture.thenAcceptAsync(result -> this.callbackNotify(result));
    }

    private void callbackNotify(List<WrapperTimestampRobotTestResult> results) {
        LOGGER.debug(EELFLoggerDelegate.debugLogger,
                "Wrapper timestamp tobot test results retrieved from nexus with size : " + results.size());
        deleteOldEntries(results);
        for (WrapperTimestampRobotTestResult wrapperResult : results) {
            LabInfo actualLabInfo = labService.getLab(wrapperResult.getLab());
            if (actualLabInfo == null) {
                continue;
            }
            String blueprintName = wrapperResult.getBlueprintName();
            String version = wrapperResult.getVersion();
            for (TimestampRobotTestResult tsTestResult : wrapperResult.getTimestampRobotTestResults()) {
                String dateOfStorage = tsTestResult.getDateOfStorage();
                Boolean bResult = tsTestResult.getResult();
                String timestamp = tsTestResult.getTimestamp();
                org.akraino.validation.ui.entity.TimestampRobotTestResult dbResult = tsService
                        .getTimestampRobotTestResult(blueprintName, version, actualLabInfo, timestamp);
                if (dbResult == null) {
                    dbResult = new org.akraino.validation.ui.entity.TimestampRobotTestResult();
                    dbResult.setBlueprintName(blueprintName);
                    dbResult.setLab(actualLabInfo);
                    dbResult.setTimestamp(timestamp);
                    dbResult.setVersion(version);
                }
                dbResult.setResult(bResult);
                dbResult.setDateStorage(dateOfStorage);
                ObjectMapper mapper = new ObjectMapper();
                try {
                    dbResult.setWResults(mapper.writeValueAsString(tsTestResult.getWrapperRobotTestResults()));
                } catch (JsonProcessingException e) {
                    LOGGER.error(EELFLoggerDelegate.errorLogger,
                            "Error while converting POJO to string. " + UserUtils.getStackTrace(e));
                    continue;
                }
                LOGGER.debug(EELFLoggerDelegate.debugLogger,
                        "Storing timestamp robot test result with keys: blueprint name: " + blueprintName
                        + ", version: " + version + ", lab: " + actualLabInfo.getLab().name() + ", timestamp: "
                        + timestamp);
                tsService.saveOrUpdate(dbResult);
            }
        }
        try {
            Thread.sleep(Integer.valueOf(PortalApiProperties.getProperty("thread_sleep")));
        } catch (InterruptedException e) {
            LOGGER.error(EELFLoggerDelegate.errorLogger,
                    "Error while putting current thread in sleep state. " + UserUtils.getStackTrace(e));
        }
        ApplicationContext context = new AnnotationConfigApplicationContext(ExecutorServiceInitializer.class);
        ExecutorService service = (ExecutorService) context.getBean("executorService");
        WrapperTimestampRobotTestResultsGetterExecution task = new WrapperTimestampRobotTestResultsGetterExecution();
        CompletableFuture<List<WrapperTimestampRobotTestResult>> completableFuture = CompletableFuture
                .supplyAsync(new PrioritySupplier<>(1, task::execute), service);
        completableFuture.thenAcceptAsync(result -> this.callbackNotify(result));
    }

    private void deleteOldEntries(List<WrapperTimestampRobotTestResult> results) {
        for (WrapperTimestampRobotTestResult wrapperResult : results) {
            LabInfo actualLabInfo = labService.getLab(wrapperResult.getLab());
            if (actualLabInfo == null) {
                continue;
            }
            String blueprintName = wrapperResult.getBlueprintName();
            String version = wrapperResult.getVersion();
            List<org.akraino.validation.ui.entity.TimestampRobotTestResult> tsTestResults = tsService
                    .getTimestampRobotTestResults(blueprintName, version, actualLabInfo);
            if (tsTestResults == null) {
                continue;
            }
            for (org.akraino.validation.ui.entity.TimestampRobotTestResult dbEntry : tsTestResults) {
                boolean old = true;
                for (TimestampRobotTestResult tsTestResult : wrapperResult.getTimestampRobotTestResults()) {
                    if (tsTestResult.getTimestamp().equals(dbEntry.getTimestamp())) {
                        old = false;
                    }
                }
                if (old) {
                    LOGGER.debug(EELFLoggerDelegate.debugLogger,
                            "Deleting old timestamp robot test result with id: " + dbEntry.getResultId());
                    tsService.deleteTimestampRobotTestResult(dbEntry);
                }
            }
        }
    }

    private class WrapperTimestampRobotTestResultsGetterExecution {

        public WrapperTimestampRobotTestResultsGetterExecution() {
        }

        public List<WrapperTimestampRobotTestResult> execute() {
            List<WrapperTimestampRobotTestResult> result = new ArrayList<WrapperTimestampRobotTestResult>();
            try {
                for (Lab lab : resultService.getLabsFromNexus()) {
                    for (String blueprintName : resultService.getBlueprintNamesOfLabFromNexus(lab)) {
                        for (String version : resultService.getBlueprintVersionsFromNexus(blueprintName, lab)) {
                            LOGGER.debug(EELFLoggerDelegate.debugLogger,
                                    "Trying to retrieve wrapper timestamp robot test results from nexus for: blueprint name: "
                                            + blueprintName + ", version: " + version + ", lab: " + lab.name());
                            result.add(resultService.getRobotTestResultsByBlueprintFromNexus(blueprintName, version,
                                    lab, Integer.valueOf(PortalApiProperties.getProperty("no_last_timestamps"))));
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error(EELFLoggerDelegate.errorLogger,
                        "Error when retrieving Nexus results. " + UserUtils.getStackTrace(e));
            }
            return result;
        }
    }

}
