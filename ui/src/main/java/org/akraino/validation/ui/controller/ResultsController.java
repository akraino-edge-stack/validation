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
package org.akraino.validation.ui.controller;

import java.net.URL;

import org.akraino.validation.ui.entity.OperationalSubmission;
import org.akraino.validation.ui.service.ResultService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/results")
public class ResultsController {

    private static final Logger logger = Logger.getLogger(ResultsController.class);

    @Autowired
    ResultService resultService;

    @GetMapping("/findByBlueprint")
    public ResponseEntity<URL> findByBlueprint(@RequestBody OperationalSubmission operationalSubmission) {
        try {
            return new ResponseEntity<>(resultService.getResult(operationalSubmission), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

}
