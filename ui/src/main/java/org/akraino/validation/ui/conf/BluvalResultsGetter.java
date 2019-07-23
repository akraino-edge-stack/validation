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

import org.akraino.validation.ui.client.nexus.resources.WrapperTimestampRobotTestResult;
import org.akraino.validation.ui.data.Lab;
import org.akraino.validation.ui.entity.BluvalResult;
import org.akraino.validation.ui.entity.LabInfo;
import org.akraino.validation.ui.service.BluvalResultService;
import org.akraino.validation.ui.service.LabService;
import org.akraino.validation.ui.service.ResultService;
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
public class BluvalResultsGetter implements ApplicationListener<ContextRefreshedEvent> {

    private static final EELFLoggerDelegate LOGGER = EELFLoggerDelegate.getLogger(BluvalResultsGetter.class);

    @Autowired
    ResultService resultsService;

    @Autowired
    BluvalResultService bluvalService;

    @Autowired
    LabService labService;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ExecutorServiceInitializer.class);
        ExecutorService service = (ExecutorService) context.getBean("executorService");
        BluevalResultsGetterExecution task = new BluevalResultsGetterExecution();
        CompletableFuture<List<WrapperTimestampRobotTestResult>> completableFuture = CompletableFuture
                .supplyAsync(new PrioritySupplier<>(1, task::execute), service);
        completableFuture.thenAcceptAsync(result -> this.callbackNotify(result));
    }

    private void callbackNotify(List<WrapperTimestampRobotTestResult> results) {
        for (WrapperTimestampRobotTestResult wrapperResult : results) {
            LabInfo actualLabInfo = null;
            for (LabInfo labInfo : labService.getLabs()) {
                if (labInfo.getLab().equals(wrapperResult.getLab())) {
                    actualLabInfo = labInfo;
                }
            }
            if (actualLabInfo == null) {
                continue;
            }
            BluvalResult bluvalResults = bluvalService.getBluvalResult(wrapperResult.getBlueprintName(),
                    wrapperResult.getVersion(), actualLabInfo);
            if (bluvalResults == null) {
                bluvalResults = new BluvalResult();
                bluvalResults.setBlueprintName(wrapperResult.getBlueprintName());
                bluvalResults.setLab(actualLabInfo);
                bluvalResults.setVersion(wrapperResult.getVersion());
            }
            ObjectMapper mapper = new ObjectMapper();
            try {
                bluvalResults.setResults(mapper.writeValueAsString(wrapperResult.getTimestampRobotTestResults()));
            } catch (JsonProcessingException e) {
                LOGGER.error(EELFLoggerDelegate.errorLogger,
                        "Error when converting Pojo to string. " + UserUtils.getStackTrace(e));
                continue;
            }
            bluvalService.saveOrUpdate(bluvalResults);
        }
        try {
            Thread.sleep(Integer.valueOf(PortalApiProperties.getProperty("thread_sleep")));
        } catch (InterruptedException e) {
            LOGGER.error(EELFLoggerDelegate.errorLogger,
                    "Error while putting current thread in sleep state. " + UserUtils.getStackTrace(e));
        }
        ApplicationContext context = new AnnotationConfigApplicationContext(ExecutorServiceInitializer.class);
        ExecutorService service = (ExecutorService) context.getBean("executorService");
        BluevalResultsGetterExecution task = new BluevalResultsGetterExecution();
        CompletableFuture<List<WrapperTimestampRobotTestResult>> completableFuture = CompletableFuture
                .supplyAsync(new PrioritySupplier<>(1, task::execute), service);
        completableFuture.thenAcceptAsync(result -> this.callbackNotify(result));
    }

    private class BluevalResultsGetterExecution {

        public BluevalResultsGetterExecution() {
        }

        public List<WrapperTimestampRobotTestResult> execute() {
            List<WrapperTimestampRobotTestResult> result = new ArrayList<WrapperTimestampRobotTestResult>();
            try {
                for (Lab lab : resultsService.getLabsOnTheFly()) {
                    for (String blueprintName : resultsService.getBlueprintNamesOfLabOnTheFly(lab)) {
                        for (String version : resultsService.getBlueprintVersionsOnTheFly(blueprintName, lab)) {
                            result.add(resultsService.getRobotTestResultsByBlueprintOnTheFly(blueprintName, version,
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
