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

import java.sql.SQLException;
import java.util.List;

import org.akraino.validation.ui.dao.OperationalSubmissionDAO;
import org.akraino.validation.ui.dao.SubmissionBaseInfoDAO;
import org.akraino.validation.ui.data.SubmissionStatus;
import org.akraino.validation.ui.entity.OperationalSubmission;
import org.akraino.validation.ui.entity.SubmissionBaseInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OperationalSubmissionService {

    private static final Logger logger = Logger.getLogger(OperationalSubmissionService.class);

    @Autowired
    private OperationalSubmissionDAO operationalSubmissionDAO;

    @Autowired
    private SubmissionBaseInfoDAO submissionBaseInfoDAO;

    public OperationalSubmission saveOperationalSubmission(SubmissionBaseInfo submissionBaseInfo)
            throws ClassNotFoundException, SQLException {
        submissionBaseInfoDAO.saveOrUpdate(submissionBaseInfo);
        OperationalSubmission operationalSubmission = new OperationalSubmission();
        operationalSubmission.setStatus(SubmissionStatus.Submitted);
        operationalSubmission.setsubmissionBaseInfo(submissionBaseInfo);
        operationalSubmissionDAO.saveOrUpdate(operationalSubmission);
        return operationalSubmission;
    }

    public List<OperationalSubmission> getOperationalSubmissions() throws ClassNotFoundException, SQLException {

        return operationalSubmissionDAO.getOperationalSubmissions();

    }

    public void deleteOperationalSubmission(Integer id) {

        operationalSubmissionDAO.deleteOperationalSubmission(id);

    }

}
