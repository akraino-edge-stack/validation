package org.akraino.validation.ui.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.akraino.validation.ui.client.nexus.resources.RobotTestResult;
import org.akraino.validation.ui.client.nexus.resources.TimestampWRobotTestResult;
import org.akraino.validation.ui.client.nexus.resources.WRobotTestResult;
import org.akraino.validation.ui.client.nexus.resources.WTimestampWRobotTestResult;
import org.akraino.validation.ui.dao.ValidationTestResultDAO;
import org.akraino.validation.ui.dao.WRobotTestResultDAO;
import org.akraino.validation.ui.data.BlueprintLayer;
import org.akraino.validation.ui.data.Lab;
import org.akraino.validation.ui.entity.LabInfo;
import org.akraino.validation.ui.entity.ValidationTestResult;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class DbResultAdapter {

    private static final EELFLoggerDelegate LOGGER = EELFLoggerDelegate.getLogger(DbResultAdapter.class);
    private static final Object LOCK = new Object();

    @Autowired
    LabService labService;

    @Autowired
    private ValidationTestResultDAO vTestResultDAO;

    @Autowired
    private WRobotTestResultDAO wRobotDAO;

    public void storeResultInDb(List<WTimestampWRobotTestResult> results) {
        synchronized (LOCK) {
            if (results == null || results.size() < 1) {
                return;
            }
            for (WTimestampWRobotTestResult wrapperResult : results) {
                LabInfo actualLabInfo = labService.getLab(wrapperResult.getLab());
                if (actualLabInfo == null) {
                    continue;
                }
                String blueprintName = wrapperResult.getBlueprintName();
                String version = wrapperResult.getVersion();
                storeTimestampResults(blueprintName, version, actualLabInfo,
                        wrapperResult.getTimestampWRobotTestResults());
            }
        }
    }

    public WTimestampWRobotTestResult readResultFromDb(@Nonnull String blueprintName, @Nonnull String version,
            String layerValidation, @Nonnull Lab lab) throws JsonParseException, JsonMappingException, IOException {
        synchronized (LOCK) {
            LabInfo actualLabInfo = labService.getLab(lab);
            if (actualLabInfo == null) {
                return null;
            }
            List<ValidationTestResult> vResults = vTestResultDAO.getValidationTestResults(blueprintName, version,
                    layerValidation, actualLabInfo);
            if (vResults == null || vResults.size() < 1) {
                return null;
            }
            WTimestampWRobotTestResult wrapperResult = new WTimestampWRobotTestResult();
            wrapperResult.setBlueprintName(blueprintName);
            wrapperResult.setVersion(version);
            wrapperResult.setLab(actualLabInfo.getLab());
            List<TimestampWRobotTestResult> results = new ArrayList<TimestampWRobotTestResult>();
            for (ValidationTestResult vResult : vResults) {
                TimestampWRobotTestResult tsResult = new TimestampWRobotTestResult();
                tsResult.setDateOfStorage(vResult.getDateStorage());
                tsResult.setResult(vResult.getResult());
                tsResult.setTimestamp(vResult.getTimestamp());
                List<org.akraino.validation.ui.entity.WRobotTestResult> wRobotDbResults = wRobotDAO
                        .getWRobotTestResult(vResult);
                if (wRobotDbResults == null || wRobotDbResults.size() < 1) {
                    continue;
                }
                List<WRobotTestResult> wRobotTestResults = new ArrayList<WRobotTestResult>();
                for (org.akraino.validation.ui.entity.WRobotTestResult wRobotDbResult : wRobotDbResults) {
                    WRobotTestResult wRobotTestResult = new WRobotTestResult();
                    wRobotTestResult.setBlueprintLayer(BlueprintLayer.valueOf(wRobotDbResult.getLayer()));
                    ObjectMapper mapper = new ObjectMapper();
                    wRobotTestResult.setRobotTestResults(mapper.readValue(wRobotDbResult.getRobotTestResults(),
                            new TypeReference<List<RobotTestResult>>() {
                    }));
                    wRobotTestResults.add(wRobotTestResult);
                }
                tsResult.setWRobotTestResult(wRobotTestResults);
                results.add(tsResult);
            }
            wrapperResult.setTimestampWRobotTestResult(results);
            return wrapperResult;
        }
    }

    public List<WRobotTestResult> readResultFromDb(Lab lab, String timestamp)
            throws JsonParseException, JsonMappingException, IOException {
        synchronized (LOCK) {
            LabInfo actualLabInfo = labService.getLab(lab);
            if (actualLabInfo == null) {
                return null;
            }
            ValidationTestResult vResult = vTestResultDAO.getValidationTestResult(actualLabInfo, timestamp);
            if (vResult == null) {
                return null;
            }
            List<org.akraino.validation.ui.entity.WRobotTestResult> wRobotDbResults = wRobotDAO
                    .getWRobotTestResult(vResult);
            if (wRobotDbResults == null || wRobotDbResults.size() < 1) {
                return null;
            }
            List<WRobotTestResult> wRobotTestResults = new ArrayList<WRobotTestResult>();
            for (org.akraino.validation.ui.entity.WRobotTestResult wRobotDbResult : wRobotDbResults) {
                WRobotTestResult wRobotTestResult = new WRobotTestResult();
                wRobotTestResult.setBlueprintLayer(BlueprintLayer.valueOf(wRobotDbResult.getLayer()));
                ObjectMapper mapper = new ObjectMapper();
                wRobotTestResult.setRobotTestResults(mapper.readValue(wRobotDbResult.getRobotTestResults(),
                        new TypeReference<List<RobotTestResult>>() {
                }));
                wRobotTestResults.add(wRobotTestResult);
            }
            return wRobotTestResults;
        }
    }

    public void deleteUnreferencedEntries(List<WTimestampWRobotTestResult> results) {
        synchronized (LOCK) {
            for (WTimestampWRobotTestResult wrapperResult : results) {
                LabInfo actualLabInfo = labService.getLab(wrapperResult.getLab());
                if (actualLabInfo == null) {
                    continue;
                }
                String blueprintName = wrapperResult.getBlueprintName();
                String version = wrapperResult.getVersion();
                List<ValidationTestResult> vResults = vTestResultDAO.getValidationTestResults(blueprintName, version,
                        null, actualLabInfo);
                if (vResults == null) {
                    continue;
                }
                for (ValidationTestResult vResult : vResults) {
                    boolean deletion = true;
                    for (TimestampWRobotTestResult tsResult : wrapperResult.getTimestampWRobotTestResults()) {
                        if (tsResult.getTimestamp().equals(vResult.getTimestamp())) {
                            deletion = false;
                        }
                    }
                    if (deletion) {
                        LOGGER.debug(EELFLoggerDelegate.debugLogger,
                                "Deleting unreferenced validation result timestamp robot test result with id: "
                                        + vResult.getResultId());
                        // Delete old associated wrapper robot rest results from db
                        for (org.akraino.validation.ui.entity.WRobotTestResult wRobotDbResult : wRobotDAO
                                .getWRobotTestResult(vResult)) {
                            wRobotDAO.deleteWRobotTestResult(wRobotDbResult.getWRobotResultId());
                        }
                        vTestResultDAO.deleteValidationTestResult(vResult);
                    }
                }
            }
        }
    }

    public List<ValidationTestResult> getValidationTestResults() {
        synchronized (LOCK) {
            return vTestResultDAO.getValidationTestResults();
        }
    }

    public ValidationTestResult getValidationTestResult(Integer resultId) {
        synchronized (LOCK) {
            return vTestResultDAO.getValidationTestResult(resultId);
        }
    }

    public List<ValidationTestResult> getValidationTestResults(String blueprintName, String version,
            String layerValidation, LabInfo labInfo) {
        synchronized (LOCK) {
            return vTestResultDAO.getValidationTestResults(blueprintName, version, layerValidation, labInfo);
        }
    }

    public ValidationTestResult getValidationTestResult(LabInfo labInfo, String timestamp) {
        synchronized (LOCK) {
            return vTestResultDAO.getValidationTestResult(labInfo, timestamp);
        }
    }

    public List<org.akraino.validation.ui.entity.WRobotTestResult> getWRobotTestResults() {
        synchronized (LOCK) {
            return wRobotDAO.getWRobotTestResults();
        }
    }

    public org.akraino.validation.ui.entity.WRobotTestResult getWRobotTestResult(Integer wRobotResultId) {
        synchronized (LOCK) {
            return wRobotDAO.getWRobotTestResult(wRobotResultId);
        }
    }

    public List<org.akraino.validation.ui.entity.WRobotTestResult> getWRobotTestResult(ValidationTestResult vResult) {
        synchronized (LOCK) {
            return wRobotDAO.getWRobotTestResult(vResult);
        }
    }

    private void storeTimestampResults(String blueprintName, String version, LabInfo labInfo,
            List<TimestampWRobotTestResult> tsTestResults) {
        if (tsTestResults == null || tsTestResults.size() < 1) {
            return;
        }
        for (TimestampWRobotTestResult tsTestResult : tsTestResults) {
            String dateOfStorage = tsTestResult.getDateOfStorage();
            Boolean bResult = tsTestResult.getResult();
            String timestamp = tsTestResult.getTimestamp();
            ValidationTestResult vResult = vTestResultDAO.getValidationTestResult(labInfo, timestamp);
            if (vResult == null) {
                vResult = new ValidationTestResult();
                vResult.setLab(labInfo);
                vResult.setTimestamp(timestamp);
            }
            vResult.setBlueprintName(blueprintName);
            vResult.setVersion(version);
            vResult.setResult(bResult);
            vResult.setDateStorage(dateOfStorage);
            LOGGER.debug(EELFLoggerDelegate.debugLogger,
                    "Storing validation test result with keys: blueprint name: " + blueprintName + ", version: "
                            + version + ", lab: " + labInfo.getLab().name() + ", timestamp: " + timestamp);
            vTestResultDAO.saveOrUpdate(vResult);
            // Delete old associated wrapper robot rest results from db
            List<org.akraino.validation.ui.entity.WRobotTestResult> wRobotDbResults = wRobotDAO
                    .getWRobotTestResult(vResult);
            /*
             * if (wRobotDbResults != null && wRobotDbResults.size() > 0) { for
             * (org.akraino.validation.ui.entity.WRobotTestResult wRobotDbResult :
             * wRobotDbResults) { LOGGER.debug(EELFLoggerDelegate.debugLogger,
             * "Deleting wrobot test result with keys: layer: " + wRobotDbResult.getLayer()
             * + " and validation test result id: " +
             * wRobotDbResult.getValidationTestResult().getResultId());
             * wRobotDAO.deleteWRobotTestResult(wRobotDbResult.getWRobotResultId()); } }
             */
            if (wRobotDbResults != null) {
                // It is assumed that old results in nexus are not modified
                return;
            }
            // Store the new wrapper robot rest results in db
            for (WRobotTestResult wRobotTestResult : tsTestResult.getWRobotTestResults()) {
                org.akraino.validation.ui.entity.WRobotTestResult wRobotDbResult = new org.akraino.validation.ui.entity.WRobotTestResult();
                wRobotDbResult.setLayer(wRobotTestResult.getBlueprintLayer().name());
                wRobotDbResult.setValidationTestResult(vResult);
                ObjectMapper mapper = new ObjectMapper();
                try {
                    wRobotDbResult
                    .setRobotTestResults(mapper.writeValueAsString(wRobotTestResult.getRobotTestResults()));
                } catch (JsonProcessingException e) {
                    LOGGER.error(EELFLoggerDelegate.errorLogger,
                            "Error while converting POJO to string. " + UserUtils.getStackTrace(e));
                    continue;
                }
                wRobotDAO.saveOrUpdate(wRobotDbResult);
            }
        }
    }

}
