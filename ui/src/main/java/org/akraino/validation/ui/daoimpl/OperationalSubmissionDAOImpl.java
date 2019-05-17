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
package org.akraino.validation.ui.daoimpl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.akraino.validation.ui.dao.OperationalSubmissionDAO;
import org.akraino.validation.ui.entity.OperationalSubmission;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OperationalSubmissionDAOImpl implements OperationalSubmissionDAO {

    private static final Logger logger = Logger.getLogger(OperationalSubmissionDAOImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<OperationalSubmission> getOperationalSubmissions() {

        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<OperationalSubmission> criteria = builder.createQuery(OperationalSubmission.class);

        Root<OperationalSubmission> root = criteria.from(OperationalSubmission.class);
        criteria.select(root);

        Query<OperationalSubmission> query = getSession().createQuery(criteria);

        return query.getResultList();

    }

    @Override
    public OperationalSubmission getOperationalSubmission(Integer operationalSubmissionId) {

        EntityManager em = getSession().getEntityManagerFactory().createEntityManager();

        return em.find(OperationalSubmission.class, operationalSubmissionId);
    }

    @Override
    public void saveOrUpdate(OperationalSubmission operationalSubmission) {
        getSession().saveOrUpdate(operationalSubmission);

    }

    @Override
    public void merge(OperationalSubmission operationalSubmission) {
        getSession().merge(operationalSubmission);

    }

    @Override
    public void deleteOperationalSubmission(OperationalSubmission operationalSubmission) {
        getSession().delete(operationalSubmission);

    }

    @Override
    public void deleteOperationalSubmission(Integer id) {
        getSession().delete(this.getOperationalSubmission(id));
    }

    @Override
    public void deleteAll() {

        Query<?> query = getSession().createQuery("delete from OperationalSubmission");

        int result = query.executeUpdate();

        if (result > 0) {
            logger.info("All Operational Submission entries are cleaned up");
        }
    }

}
