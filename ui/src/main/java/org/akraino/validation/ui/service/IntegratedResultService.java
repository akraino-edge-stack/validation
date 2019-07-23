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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.akraino.validation.ui.client.nexus.NexusExecutorClient;
import org.akraino.validation.ui.client.nexus.resources.TimestampWRobotTestResult;
import org.akraino.validation.ui.client.nexus.resources.WRobotTestResult;
import org.akraino.validation.ui.client.nexus.resources.WTimestampWRobotTestResult;
import org.akraino.validation.ui.data.Lab;
import org.akraino.validation.ui.entity.LabInfo;
import org.akraino.validation.ui.entity.LabSilo;
import org.akraino.validation.ui.entity.Submission;
import org.akraino.validation.ui.entity.ValidationTestResult;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

@Service
@Transactional
public class IntegratedResultService {

    private static final EELFLoggerDelegate LOGGER = EELFLoggerDelegate.getLogger(IntegratedResultService.class);

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private SiloService siloService;

    @Autowired
    NexusExecutorClient nexusService;

    @Autowired
    LabService labService;

    @Autowired
    DbResultAdapter dbAdapter;

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
            throw new IllegalArgumentException("Could not retrieve blueprint names of lab : " + lab.toString());
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
            throw new IllegalArgumentException("Could not retrieve silo of the lab : " + lab.toString());
        }
        return nexusService.getResource(siloText, name);
    }

    public WTimestampWRobotTestResult getWTimestampWRobotTestResultFromNexus(String name, String version, Lab lab,
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
        List<TimestampWRobotTestResult> results = nexusService.getTimestampWRobotTestResults(name, version, siloText,
                noTimestamps);
        WTimestampWRobotTestResult wrapperResult = new WTimestampWRobotTestResult();
        wrapperResult.setBlueprintName(name);
        wrapperResult.setVersion(version);
        wrapperResult.setLab(lab);
        wrapperResult.setTimestampWRobotTestResult(results);
        return wrapperResult;
    }

    public TimestampWRobotTestResult getTimestampWRobotTestResultFromNexus(String name, String version, Lab lab,
            String timestamp) throws JsonParseException, JsonMappingException, IOException, KeyManagementException,
    ClientHandlerException, UniformInterfaceException, NoSuchAlgorithmException, NullPointerException,
    ParseException {
        String siloText = null;
        for (LabSilo silo : siloService.getSilos()) {
            if (silo.getLab().getLab().equals(lab)) {
                siloText = silo.getSilo();
            }
        }
        if (siloText == null) {
            throw new IllegalArgumentException("Could not retrieve silo of the lab : " + lab.toString());
        }
        return nexusService.getTimestampWRobotTestResult(name, version, siloText, timestamp);
    }

    public TimestampWRobotTestResult getLastResultBasedOnOutcomeFromNexus(String name, String version, Lab lab,
            String layerValidation, boolean outcome)
                    throws JsonParseException, JsonMappingException, KeyManagementException, ClientHandlerException,
                    UniformInterfaceException, NoSuchAlgorithmException, NullPointerException, IOException, ParseException {
        String siloText = null;
        for (LabSilo silo : siloService.getSilos()) {
            if (silo.getLab().getLab().equals(lab)) {
                siloText = silo.getSilo();
            }
        }
        if (siloText == null) {
            throw new IllegalArgumentException("Lab does not exist: " + lab.toString());
        }
        return nexusService.getLastResultBasedOnOutcome(name, version, siloText, outcome);
    }

    public WTimestampWRobotTestResult getBasedOnDateFromNexus(String blueprintName, String version, Lab lab, Date date)
            throws JsonParseException, JsonMappingException, IOException, ParseException, KeyManagementException,
            ClientHandlerException, UniformInterfaceException, NoSuchAlgorithmException, NullPointerException {
        String siloText = null;
        for (LabSilo silo : siloService.getSilos()) {
            if (silo.getLab().getLab().equals(lab)) {
                siloText = silo.getSilo();
            }
        }
        if (siloText == null) {
            throw new IllegalArgumentException("Lab does not exist: " + lab.toString());
        }
        WTimestampWRobotTestResult wrapperResult = new WTimestampWRobotTestResult();
        wrapperResult.setBlueprintName(blueprintName);
        wrapperResult.setVersion(version);
        wrapperResult.setLab(lab);
        wrapperResult.setTimestampWRobotTestResult(
                nexusService.getTimestampWRobotTestResult(blueprintName, version, siloText, date));
        return wrapperResult;
    }

    public Set<Lab> getLabsFromDb() {
        Set<Lab> labs = new HashSet<Lab>();
        for (ValidationTestResult result : dbAdapter.getValidationTestResults()) {
            labs.add(result.getLab().getLab());
        }
        return labs;
    }

    public Set<String> getBlueprintNamesOfLabFromDb(Lab lab) {
        Set<String> blueprintNames = new HashSet<String>();
        for (ValidationTestResult result : dbAdapter.getValidationTestResults()) {
            if (result.getLab().getLab().equals(lab)) {
                blueprintNames.add(result.getBlueprintName());
            }
        }
        return blueprintNames;
    }

    public Set<String> getBlueprintVersionsFromDb(String name, Lab lab) {
        Set<String> blueprintVersions = new HashSet<String>();
        for (ValidationTestResult result : dbAdapter.getValidationTestResults()) {
            if (result.getLab().getLab().equals(lab) && result.getBlueprintName().equals(name)) {
                blueprintVersions.add(result.getVersion());
            }
        }
        return blueprintVersions;
    }

    public List<WRobotTestResult> getWRobotTestResultsFromDb(String submissionId)
            throws JsonParseException, JsonMappingException, KeyManagementException, ClientHandlerException,
            UniformInterfaceException, NoSuchAlgorithmException, IOException, NullPointerException, ParseException {
        Submission submission = submissionService.getSubmission(submissionId);
        if (submission == null) {
            LOGGER.info(EELFLoggerDelegate.applicationLogger, "Requested submission does not exist");
            return null;
        }
        Lab submissionLab = submission.getTimeslot().getLab().getLab();
        String timestamp = submission.getNexusResultUrl()
                .substring(submission.getNexusResultUrl().lastIndexOf('/') + 1);
        List<WRobotTestResult> wRobotResults = dbAdapter.readResultFromDb(submissionLab, timestamp);
        if (wRobotResults == null) {
            wRobotResults = this
                    .getTimestampWRobotTestResultFromNexus(
                            submission.getBlueprintInstanceForValidation().getBlueprint().getBlueprintName(),
                            submission.getBlueprintInstanceForValidation().getVersion(), submissionLab, timestamp)
                    .getWRobotTestResults();
        }
        return wRobotResults;
    }

    public List<WRobotTestResult> getWRobotTestResultsFromDb(String blueprintName, String version, Lab lab,
            String timestamp)
                    throws JsonParseException, JsonMappingException, KeyManagementException, ClientHandlerException,
                    UniformInterfaceException, NoSuchAlgorithmException, NullPointerException, IOException, ParseException {
        LabInfo actualLabInfo = labService.getLab(lab);
        if (actualLabInfo == null) {
            return null;
        }
        List<WRobotTestResult> wRobotResults = dbAdapter.readResultFromDb(lab, timestamp);
        if (wRobotResults == null) {
            wRobotResults = this.getTimestampWRobotTestResultFromNexus(blueprintName, version, lab, timestamp)
                    .getWRobotTestResults();
        }
        return wRobotResults;
    }

    public List<WRobotTestResult> getLastResultBasedOnOutcomeFromDb(String blueprintName, String version, Lab lab,
            String layerValidation, boolean outcome)
                    throws JsonParseException, JsonMappingException, KeyManagementException, ClientHandlerException,
                    UniformInterfaceException, NoSuchAlgorithmException, IOException, NullPointerException, ParseException {
        LabInfo actualLabInfo = labService.getLab(lab);
        if (actualLabInfo == null) {
            return null;
        }
        WTimestampWRobotTestResult wTsResult = null;
        if (layerValidation.equals("*")) {
            wTsResult = dbAdapter.readResultFromDb(blueprintName, version, null, lab);
        } else {
            wTsResult = dbAdapter.readResultFromDb(blueprintName, version, layerValidation, lab);
        }
        List<TimestampWRobotTestResult> tsResults = null;
        if (wTsResult != null) {
            tsResults = findTsResultsBasedOnOutcome(wTsResult.getTimestampWRobotTestResults(), outcome);
            if (tsResults.size() > 0) {
                DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                Collections.sort(tsResults, new Comparator<TimestampWRobotTestResult>() {
                    @Override
                    public int compare(TimestampWRobotTestResult tsResult1, TimestampWRobotTestResult tsResult2) {
                        try {
                            return dateFormat.parse(tsResult2.getDateOfStorage())
                                    .compareTo(dateFormat.parse(tsResult1.getDateOfStorage()));
                        } catch (ParseException e) {
                            LOGGER.error(EELFLoggerDelegate.errorLogger,
                                    "Error when parsing date. " + UserUtils.getStackTrace(e));
                            return 0;
                        }
                    }
                });
                return tsResults.get(0).getWRobotTestResults();
            }
        }
        TimestampWRobotTestResult tsResult = getLastResultBasedOnOutcomeFromNexus(blueprintName, version, lab,
                layerValidation, outcome);
        if (tsResult != null) {
            return tsResult.getWRobotTestResults();
        }
        return null;
    }

    private List<TimestampWRobotTestResult> findTsResultsBasedOnOutcome(List<TimestampWRobotTestResult> tsResults,
            boolean outcome) {
        List<TimestampWRobotTestResult> results = new ArrayList<TimestampWRobotTestResult>();
        for (TimestampWRobotTestResult tsResult : tsResults) {
            if (tsResult.getResult() == outcome) {
                results.add(tsResult);
            }
        }
        return results;
    }

}
