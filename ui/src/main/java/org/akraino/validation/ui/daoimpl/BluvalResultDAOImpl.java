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

import org.akraino.validation.ui.dao.BluvalResultDAO;
import org.akraino.validation.ui.entity.BluvalResult;
import org.akraino.validation.ui.entity.LabInfo;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BluvalResultDAOImpl implements BluvalResultDAO {

    private static final EELFLoggerDelegate LOGGER = EELFLoggerDelegate.getLogger(BluvalResultDAOImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<BluvalResult> getBluvalResults() {
        Criteria criteria = getSession().createCriteria(BluvalResult.class);
        return criteria.list();
    }

    @Override
    public BluvalResult getBluvalResult(Integer bluvalId) {
        Criteria criteria = getSession().createCriteria(BluvalResult.class);
        criteria.add(Restrictions.eq("id", String.valueOf(bluvalId)));
        return criteria.list() == null ? null : (BluvalResult) criteria.list().get(0);
    }

    @Override
    public BluvalResult getBluvalResult(String blueprintName, String version, LabInfo labInfo) {
        Criteria criteria = getSession().createCriteria(BluvalResult.class);
        criteria.add(Restrictions.eq("blueprintName", String.valueOf(blueprintName)));
        criteria.add(Restrictions.eq("version", String.valueOf(version)));
        criteria.add(Restrictions.eq("lab", labInfo));
        return criteria.list() == null || criteria.list().size() == 0 ? null : (BluvalResult) criteria.list().get(0);
    }

    @Override
    public void saveOrUpdate(BluvalResult bluvalResults) {
        getSession().saveOrUpdate(bluvalResults);
    }

    @Override
    public void merge(BluvalResult bluvalResults) {
        getSession().merge(bluvalResults);
    }

    @Override
    public void deleteBluvalResult(BluvalResult bluvalResults) {
        getSession().delete(bluvalResults);
    }

    @Override
    public void deleteAll() {
        if (getSession().createQuery("delete from BluvalResults").executeUpdate() > 0) {
            LOGGER.info(EELFLoggerDelegate.applicationLogger, "All bluval results are cleaned up");
        }
    }

}
