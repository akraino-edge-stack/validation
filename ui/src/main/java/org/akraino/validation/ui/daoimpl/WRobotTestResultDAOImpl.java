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

import javax.annotation.Nonnull;

import org.akraino.validation.ui.dao.WRobotTestResultDAO;
import org.akraino.validation.ui.entity.ValidationTestResult;
import org.akraino.validation.ui.entity.WRobotTestResult;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class WRobotTestResultDAOImpl implements WRobotTestResultDAO {

    private static final EELFLoggerDelegate LOGGER = EELFLoggerDelegate.getLogger(WRobotTestResultDAOImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<WRobotTestResult> getWRobotTestResults() {
        Criteria criteria = getSession().createCriteria(WRobotTestResult.class);
        return criteria.list();
    }

    @Override
    public WRobotTestResult getWRobotTestResult(@Nonnull Integer wRobotResultId) {
        Criteria criteria = getSession().createCriteria(WRobotTestResult.class);
        criteria.add(Restrictions.eq("id", wRobotResultId));
        return criteria.list() == null || criteria.list().size() < 1 ? null : (WRobotTestResult) criteria.list().get(0);
    }

    @Override
    public List<WRobotTestResult> getWRobotTestResult(@Nonnull ValidationTestResult vResult) {
        Criteria criteria = getSession().createCriteria(WRobotTestResult.class);
        criteria.add(Restrictions.eq("vResult", vResult));
        return criteria.list() == null || criteria.list().size() == 0 ? null : (List<WRobotTestResult>) criteria.list();
    }

    @Override
    public void saveOrUpdate(@Nonnull WRobotTestResult wResult) {
        getSession().saveOrUpdate(wResult);
        getSession().flush();
    }

    @Override
    public void merge(@Nonnull WRobotTestResult wResult) {
        getSession().merge(wResult);
        getSession().flush();
    }

    @Override
    public void deleteWRobotTestResult(@Nonnull Integer wRobotResultId) {
        getSession().delete(this.getWRobotTestResult(wRobotResultId));
        getSession().flush();
    }

    @Override
    public void deleteAll() {
        if (getSession().createQuery("delete from WRobotTestResult").executeUpdate() > 0) {
            LOGGER.info(EELFLoggerDelegate.applicationLogger, "All wrapper robot test results are cleaned up");
            getSession().flush();
        }
    }

}
