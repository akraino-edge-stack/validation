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
package org.akraino.validation.ui.daoimpl;

import java.util.List;

import org.akraino.validation.ui.dao.TimestampRobotTestResultDAO;
import org.akraino.validation.ui.entity.LabInfo;
import org.akraino.validation.ui.entity.TimestampRobotTestResult;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TimestampRobotTestResultDAOImpl implements TimestampRobotTestResultDAO {

    private static final EELFLoggerDelegate LOGGER = EELFLoggerDelegate
            .getLogger(TimestampRobotTestResultDAOImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<TimestampRobotTestResult> getTimestampRobotTestResults() {
        Criteria criteria = getSession().createCriteria(TimestampRobotTestResult.class);
        return criteria.list();
    }

    @Override
    public TimestampRobotTestResult getTimestampRobotTestResult(Integer resultId) {
        Criteria criteria = getSession().createCriteria(TimestampRobotTestResult.class);
        criteria.add(Restrictions.eq("id", String.valueOf(resultId)));
        return criteria.list() == null ? null : (TimestampRobotTestResult) criteria.list().get(0);
    }

    @Override
    public List<TimestampRobotTestResult> getTimestampRobotTestResults(String blueprintName, String version,
            LabInfo labInfo) {
        Criteria criteria = getSession().createCriteria(TimestampRobotTestResult.class);
        criteria.add(Restrictions.eq("blueprintName", String.valueOf(blueprintName)));
        criteria.add(Restrictions.eq("version", String.valueOf(version)));
        criteria.add(Restrictions.eq("lab", labInfo));
        return criteria.list() == null || criteria.list().size() == 0 ? null
                : (List<TimestampRobotTestResult>) criteria.list();
    }

    @Override
    public TimestampRobotTestResult getTimestampRobotTestResult(String blueprintName, String version, LabInfo labInfo,
            String timestamp) {
        Criteria criteria = getSession().createCriteria(TimestampRobotTestResult.class);
        criteria.add(Restrictions.eq("blueprintName", String.valueOf(blueprintName)));
        criteria.add(Restrictions.eq("version", String.valueOf(version)));
        criteria.add(Restrictions.eq("lab", labInfo));
        criteria.add(Restrictions.eq("timestamp", timestamp));
        return criteria.list() == null || criteria.list().size() == 0 ? null
                : (TimestampRobotTestResult) criteria.list().get(0);
    }

    @Override
    public void saveOrUpdate(TimestampRobotTestResult tsResult) {
        getSession().saveOrUpdate(tsResult);
    }

    @Override
    public void merge(TimestampRobotTestResult tsResult) {
        getSession().merge(tsResult);
    }

    @Override
    public void deleteTimestampRobotTestResult(TimestampRobotTestResult tsResult) {
        getSession().delete(tsResult);
    }

    @Override
    public void deleteTimestampRobotTestResult(String blueprintName, String version, LabInfo labInfo,
            String timestamp) {
        Criteria criteria = getSession().createCriteria(TimestampRobotTestResult.class);
        criteria.add(Restrictions.eq("blueprintName", String.valueOf(blueprintName)));
        criteria.add(Restrictions.eq("version", String.valueOf(version)));
        criteria.add(Restrictions.eq("lab", labInfo));
        criteria.add(Restrictions.eq("timestamp", timestamp));
        if (criteria.list() == null || criteria.list().size() == 0) {
            return;
        }
        getSession().delete(criteria.list().get(0));
    }

    @Override
    public void deleteAll() {
        if (getSession().createQuery("delete from BluvalResults").executeUpdate() > 0) {
            LOGGER.info(EELFLoggerDelegate.applicationLogger, "All bluval results are cleaned up");
        }
    }

}
