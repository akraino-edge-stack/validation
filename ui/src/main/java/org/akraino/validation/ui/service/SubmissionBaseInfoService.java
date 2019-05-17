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

import org.akraino.validation.ui.dao.SubmissionBaseInfoDAO;
import org.akraino.validation.ui.entity.SubmissionBaseInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SubmissionBaseInfoService {

    private static final Logger logger = Logger.getLogger(SubmissionBaseInfoService.class);

    @Autowired
    private SubmissionBaseInfoDAO submissionBaseInfoDAO;

    public void saveSubmissionBaseInfo(SubmissionBaseInfo submissionBaseInfo)
            throws ClassNotFoundException, SQLException {

        submissionBaseInfoDAO.saveOrUpdate(submissionBaseInfo);

    }

    public List<SubmissionBaseInfo> getSubmissionBaseInfos() throws ClassNotFoundException, SQLException {

        return submissionBaseInfoDAO.getSubmissionBaseInfos();

    }
    
    public void deleteAll() {
        submissionBaseInfoDAO.deleteAll();
    }

}
