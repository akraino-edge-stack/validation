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

import java.util.List;

import org.akraino.validation.ui.dao.BluvalResultDAO;
import org.akraino.validation.ui.entity.BluvalResult;
import org.akraino.validation.ui.entity.LabInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BluvalResultService {

    @Autowired
    private BluvalResultDAO bluvalResultsDAO;

    public List<BluvalResult> getBluvalResults() {
        return bluvalResultsDAO.getBluvalResults();
    }

    public BluvalResult getBluvalResult(Integer bluvalId) {
        return bluvalResultsDAO.getBluvalResult(bluvalId);
    }

    public BluvalResult getBluvalResult(String blueprintName, String version, LabInfo labInfo) {
        return bluvalResultsDAO.getBluvalResult(blueprintName, version, labInfo);
    }

    public void saveOrUpdate(BluvalResult bluvalResults) {
        bluvalResultsDAO.saveOrUpdate(bluvalResults);
    }

    public void deleteBluvalResult(BluvalResult bluvalResult) {
        bluvalResultsDAO.deleteBluvalResult(bluvalResult);
    }

}
