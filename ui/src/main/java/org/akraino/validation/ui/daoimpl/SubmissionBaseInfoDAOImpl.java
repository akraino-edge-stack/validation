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

import org.akraino.validation.ui.dao.SubmissionBaseInfoDAO;
import org.akraino.validation.ui.entity.SubmissionBaseInfo;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SubmissionBaseInfoDAOImpl implements SubmissionBaseInfoDAO {

    private static final Logger logger = Logger.getLogger(SubmissionBaseInfoDAOImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<SubmissionBaseInfo> getSubmissionBaseInfos() {

        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<SubmissionBaseInfo> criteria = builder.createQuery(SubmissionBaseInfo.class);

        Root<SubmissionBaseInfo> root = criteria.from(SubmissionBaseInfo.class);
        criteria.select(root);

        Query<SubmissionBaseInfo> query = getSession().createQuery(criteria);

        return query.getResultList();

    }

    @Override
    public SubmissionBaseInfo getSubmissionBaseInfo(Integer submissionBaseInfoId) {

        EntityManager em = getSession().getEntityManagerFactory().createEntityManager();

        return em.find(SubmissionBaseInfo.class, submissionBaseInfoId);
    }

    @Override
    public void saveOrUpdate(SubmissionBaseInfo submissionBaseInfo) {
        getSession().saveOrUpdate(submissionBaseInfo);

    }

    @Override
    public void merge(SubmissionBaseInfo submissionBaseInfo) {
        getSession().merge(submissionBaseInfo);

    }

    @Override
    public void deleteSubmissionBaseInfo(SubmissionBaseInfo submissionBaseInfo) {
        getSession().delete(submissionBaseInfo);

    }

    @Override
    public void deleteAll() {

        Query<?> query = getSession().createQuery("delete from SubmissionBaseInfo");

        int result = query.executeUpdate();

        if (result > 0) {
            logger.info("All submission base info entries are cleaned up");
        }
    }

}
