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

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.akraino.validation.ui.client.nexus.NexusExecutorClient;
import org.akraino.validation.ui.client.nexus.resources.WrapperRobotTestResult;
import org.akraino.validation.ui.client.nexus.resources.WrapperTimestampRobotTestResult;
import org.akraino.validation.ui.data.Lab;
import org.akraino.validation.ui.entity.LabInfo;
import org.akraino.validation.ui.entity.LabSilo;
import org.akraino.validation.ui.entity.Submission;
import org.akraino.validation.ui.entity.TimestampRobotTestResult;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

@Service
@Transactional
public class ResultService {

    private static final EELFLoggerDelegate LOGGER = EELFLoggerDelegate.getLogger(ResultService.class);

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private SiloService siloService;

    @Autowired
    NexusExecutorClient nexusService;

    @Autowired
    TimestampRobotTestResultService tsService;

    @Autowired
    LabService labService;

    public List<Lab> getLabsFromNexus()
            throws JsonParseException, JsonMappingException, KeyManagementException, ClientHandlerException,
            UniformInterfaceException, NoSuchAlgorithmException, IOException, IllegalArgumentException, ParseException {
        List<Lab> labs = new ArrayList<Lab>();
        for (String cLabSilo : nexusService.getResource(null)) {
            for (LabSilo silo : siloService.getSilos()) {
                if (silo.getSilo().equals(cLabSilo)) {
                    labs.add(silo.getLab().getLab());
                }
            }
        }
        return labs;
    }

    public Set<Lab> getLabsFromDb() {
        Set<Lab> labs = new HashSet<Lab>();
        for (TimestampRobotTestResult result : tsService.getTimestampRobotTestResults()) {
            labs.add(result.getLab().getLab());
        }
        return labs;
    }

    public List<String> getBlueprintNamesOfLabFromNexus(Lab lab)
            throws JsonParseException, JsonMappingException, KeyManagementException, ClientHandlerException,
            UniformInterfaceException, NoSuchAlgorithmException, IOException, IllegalArgumentException, ParseException {
        String siloText = null;
        for (LabSilo silo : siloService.getSilos()) {
            if (silo.getLab().getLab().equals(lab)) {
                siloText = silo.getSilo();
            }
        }
        if (siloText == null) {
            throw new IllegalArgumentException("Could not retrieve blueprint names the lab : " + lab.toString());
        }
        List<String> blueprintNames = new ArrayList<String>();
        List<String> cBlueprintNames = nexusService.getResource(siloText);
        for (String cBlueprintName : cBlueprintNames) {
            if (!cBlueprintName.equals("job")) {
                blueprintNames.add(cBlueprintName);
            }
        }
        return blueprintNames;
    }

    public Set<String> getBlueprintNamesOfLabFromDb(Lab lab) {
        Set<String> blueprintNames = new HashSet<String>();
        for (TimestampRobotTestResult result : tsService.getTimestampRobotTestResults()) {
            if (result.getLab().getLab().equals(lab)) {
                blueprintNames.add(result.getBlueprintName());
            }
        }
        return blueprintNames;
    }

    public List<String> getBlueprintVersionsFromNexus(String name, Lab lab)
            throws JsonParseException, JsonMappingException, KeyManagementException, ClientHandlerException,
            UniformInterfaceException, NoSuchAlgorithmException, IOException, IllegalArgumentException, ParseException {
        String siloText = null;
        for (LabSilo silo : siloService.getSilos()) {
            if (silo.getLab().getLab().equals(lab)) {
                siloText = silo.getSilo();
            }
        }
        if (siloText == null) {
            throw new IllegalArgumentException("Could not retrieve blueprint names the lab : " + lab.toString());
        }
        return nexusService.getResource(siloText, name);
    }

    public Set<String> getBlueprintVersionsFromDb(String name, Lab lab) {
        Set<String> blueprintVersions = new HashSet<String>();
        for (TimestampRobotTestResult result : tsService.getTimestampRobotTestResults()) {
            if (result.getLab().getLab().equals(lab) && result.getBlueprintName().equals(name)) {
                blueprintVersions.add(result.getVersion());
            }
        }
        return blueprintVersions;
    }

    public WrapperTimestampRobotTestResult getRobotTestResultsByBlueprintFromNexus(String name, String version, Lab lab,
            int noTimestamps)
                    throws JsonParseException, JsonMappingException, KeyManagementException, ClientHandlerException,
                    UniformInterfaceException, NoSuchAlgorithmException, IOException, IllegalArgumentException, ParseException {
        String siloText = null;
        for (LabSilo silo : siloService.getSilos()) {
            if (silo.getLab().getLab().equals(lab)) {
                siloText = silo.getSilo();
            }
        }
        if (siloText == null) {
            throw new IllegalArgumentException("Could not retrieve silo of the lab : " + lab.toString());
        }
        List<org.akraino.validation.ui.client.nexus.resources.TimestampRobotTestResult> results = nexusService
                .getRobotTestResultsByBlueprint(name, version, siloText, noTimestamps);
        WrapperTimestampRobotTestResult wrapperResult = new WrapperTimestampRobotTestResult();
        wrapperResult.setBlueprintName(name);
        wrapperResult.setVersion(version);
        wrapperResult.setLab(lab);
        wrapperResult.setTimestampRobotTestResult(results);
        return wrapperResult;
    }

    public WrapperTimestampRobotTestResult getRobotTestResultsByBlueprintFromDb(String name, String version, Lab lab)
            throws JsonParseException, JsonMappingException, IOException {
        LabInfo actualLabInfo = labService.getLab(lab);
        if (actualLabInfo == null) {
            return null;
        }
        List<TimestampRobotTestResult> tsResults = tsService.getTimestampRobotTestResults(name, version, actualLabInfo);
        if (tsResults == null) {
            return null;
        }
        WrapperTimestampRobotTestResult wrapperResult = new WrapperTimestampRobotTestResult();
        wrapperResult.setBlueprintName(name);
        wrapperResult.setVersion(version);
        wrapperResult.setLab(lab);
        List<org.akraino.validation.ui.client.nexus.resources.TimestampRobotTestResult> results = new ArrayList<org.akraino.validation.ui.client.nexus.resources.TimestampRobotTestResult>();
        for (TimestampRobotTestResult tsResult : tsResults) {
            org.akraino.validation.ui.client.nexus.resources.TimestampRobotTestResult result = new org.akraino.validation.ui.client.nexus.resources.TimestampRobotTestResult();
            result.setDateOfStorage(tsResult.getDateStorage());
            result.setResult(tsResult.getResult());
            result.setTimestamp(tsResult.getTimestamp());
            ObjectMapper mapper = new ObjectMapper();
            result.setWrapperRobotTestResults(
                    mapper.readValue(tsResult.getWResults(), new TypeReference<List<WrapperRobotTestResult>>() {
                    }));
            results.add(result);
        }
        wrapperResult.setTimestampRobotTestResult(results);
        return wrapperResult;
    }

    public org.akraino.validation.ui.client.nexus.resources.TimestampRobotTestResult getRobotTestResultsByBlueprintFromDb(
            String name, String version, Lab lab, String timestamp)
                    throws JsonParseException, JsonMappingException, IOException {
        LabInfo actualLabInfo = labService.getLab(lab);
        if (actualLabInfo == null) {
            return null;
        }
        TimestampRobotTestResult tsResult = tsService.getTimestampRobotTestResult(name, version, actualLabInfo,
                timestamp);
        if (tsResult == null) {
            return null;
        }
        org.akraino.validation.ui.client.nexus.resources.TimestampRobotTestResult result = new org.akraino.validation.ui.client.nexus.resources.TimestampRobotTestResult();
        result.setDateOfStorage(tsResult.getDateStorage());
        result.setResult(tsResult.getResult());
        result.setTimestamp(tsResult.getTimestamp());
        ObjectMapper mapper = new ObjectMapper();
        result.setWrapperRobotTestResults(
                mapper.readValue(tsResult.getWResults(), new TypeReference<List<WrapperRobotTestResult>>() {
                }));
        return result;
    }

    public List<WrapperRobotTestResult> getRobotTestResultsBySubmissionId(String submissionId)
            throws JsonParseException, JsonMappingException, KeyManagementException, ClientHandlerException,
            UniformInterfaceException, NoSuchAlgorithmException, IOException {
        Submission submission = submissionService.getSubmission(submissionId);
        if (submission == null) {
            LOGGER.info(EELFLoggerDelegate.applicationLogger, "Requested submission does not exist");
            return null;
        }
        Lab submissionLab = submission.getTimeslot().getLab().getLab();
        String siloText = null;
        for (LabSilo silo : siloService.getSilos()) {
            if (silo.getLab().getLab().equals(submissionLab)) {
                siloText = silo.getSilo();
            }
        }
        if (siloText == null) {
            throw new IllegalArgumentException("Could not retrieve silo of the lab : " + submissionLab.toString());
        }
        String timestamp = submission.getNexusResultUrl()
                .substring(submission.getNexusResultUrl().lastIndexOf('/') + 1);
        org.akraino.validation.ui.client.nexus.resources.TimestampRobotTestResult tsResult = getRobotTestResultsByBlueprintFromDb(
                submission.getBlueprintInstanceForValidation().getBlueprint().getBlueprintName(),
                submission.getBlueprintInstanceForValidation().getVersion(), submissionLab, timestamp);
        return tsResult != null ? tsResult.getWrapperRobotTestResults()
                : nexusService.getRobotTestResultsByTimestamp(
                        submission.getBlueprintInstanceForValidation().getBlueprint().getBlueprintName(),
                        submission.getBlueprintInstanceForValidation().getVersion(), siloText, timestamp);
    }

}
