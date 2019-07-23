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

import org.akraino.validation.ui.dao.ValidationTestResultDAO;
import org.akraino.validation.ui.entity.LabInfo;
import org.akraino.validation.ui.entity.ValidationTestResult;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ValidationTestResultDAOImpl implements ValidationTestResultDAO {

    private static final EELFLoggerDelegate LOGGER = EELFLoggerDelegate.getLogger(ValidationTestResultDAOImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<ValidationTestResult> getValidationTestResults() {
        Criteria criteria = getSession().createCriteria(ValidationTestResult.class);
        return criteria.list();
    }

    @Override
    public ValidationTestResult getValidationTestResult(@Nonnull Integer resultId) {
        Criteria criteria = getSession().createCriteria(ValidationTestResult.class);
        criteria.add(Restrictions.eq("id", String.valueOf(resultId)));
        return criteria.list() == null ? null : (ValidationTestResult) criteria.list().get(0);
    }

    @Override
    public List<ValidationTestResult> getValidationTestResults(String blueprintName, String version,
            String layerValidation, LabInfo labInfo) {
        Criteria criteria = getSession().createCriteria(ValidationTestResult.class);
        if (blueprintName != null) {
            criteria.add(Restrictions.eq("blueprintName", String.valueOf(blueprintName)));
        }
        if (version != null) {
            criteria.add(Restrictions.eq("version", String.valueOf(version)));
        }
        if (labInfo != null) {
            criteria.add(Restrictions.eq("lab", labInfo));
        }
        if (layerValidation != null) {
            criteria.add(Restrictions.eq("layerValidation", layerValidation));
        }
        return criteria.list() == null || criteria.list().size() == 0 ? null
                : (List<ValidationTestResult>) criteria.list();
    }

    @Override
    public ValidationTestResult getValidationTestResult(@Nonnull LabInfo labInfo, @Nonnull String timestamp) {
        Criteria criteria = getSession().createCriteria(ValidationTestResult.class);
        criteria.add(Restrictions.eq("lab", labInfo));
        criteria.add(Restrictions.eq("timestamp", timestamp));
        return criteria.list() == null || criteria.list().size() == 0 ? null
                : (ValidationTestResult) criteria.list().get(0);
    }

    @Override
    public void saveOrUpdate(@Nonnull ValidationTestResult vResult) {
        getSession().saveOrUpdate(vResult);
        getSession().flush();
    }

    @Override
    public void merge(@Nonnull ValidationTestResult vResult) {
        getSession().merge(vResult);
        getSession().flush();
    }

    @Override
    public void deleteValidationTestResult(@Nonnull ValidationTestResult vResult) {
        getSession().delete(vResult);
        getSession().flush();
    }

    @Override
    public void deleteValidationTestResult(@Nonnull LabInfo labInfo, @Nonnull String timestamp) {
        Criteria criteria = getSession().createCriteria(ValidationTestResult.class);
        criteria.add(Restrictions.eq("lab", labInfo));
        criteria.add(Restrictions.eq("timestamp", timestamp));
        if (criteria.list() == null || criteria.list().size() == 0) {
            return;
        }
        getSession().delete(criteria.list().get(0));
        getSession().flush();
    }

    @Override
    public void deleteAll() {
        if (getSession().createQuery("delete from ValidationTestResult").executeUpdate() > 0) {
            LOGGER.info(EELFLoggerDelegate.applicationLogger, "All validation test results are cleaned up");
            getSession().flush();
        }
    }

}
