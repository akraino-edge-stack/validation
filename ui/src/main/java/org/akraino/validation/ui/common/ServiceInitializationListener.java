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
package org.akraino.validation.ui.common;

import java.sql.SQLException;

import org.akraino.validation.ui.service.BlueprintDataService;
import org.akraino.validation.ui.service.LayerDataService;
import org.akraino.validation.ui.service.OperationalSubmissionService;
import org.akraino.validation.ui.service.SubmissionBaseInfoService;
import org.akraino.validation.ui.service.TimeslotService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ServiceInitializationListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = Logger.getLogger(ServiceInitializationListener.class);

    @Autowired
    private TimeslotService timeslotService;

    @Autowired
    private BlueprintDataService blueprintDataService;

    @Autowired
    private LayerDataService layerDataService;

    @Autowired
    private SubmissionBaseInfoService submissionBaseInfoService;

    @Autowired
    private OperationalSubmissionService operationalSubmissionService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        logger.info("Deleting static timeslots, blueprint and layer data from db on start up");
        operationalSubmissionService.deleteAll();
        submissionBaseInfoService.deleteAll();
        blueprintDataService.deleteAll();
        layerDataService.deleteAll();
        timeslotService.deleteAll();

        logger.info("Loading static timeslots, blueprint and layer data into db on start up");
        timeslotService.loadInitialData();
        layerDataService.loadInitialData();
        try {
            blueprintDataService.loadInitialData();
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("Failed to load blueprint data. ", e);
        }

    }

}
