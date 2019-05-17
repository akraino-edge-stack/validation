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

import org.akraino.validation.ui.dao.LayerDataDAO;
import org.akraino.validation.ui.entity.LayerData;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LayerDataDAOImpl implements LayerDataDAO {

    private static final Logger logger = Logger.getLogger(LayerDataDAOImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<LayerData> getLayerDatas() {

        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<LayerData> criteria = builder.createQuery(LayerData.class);

        Root<LayerData> root = criteria.from(LayerData.class);
        criteria.select(root);

        Query<LayerData> query = getSession().createQuery(criteria);

        return query.getResultList();

    }

    @Override
    public LayerData getLayerData(LayerData layerDataId) {

        EntityManager em = getSession().getEntityManagerFactory().createEntityManager();

        return em.find(LayerData.class, layerDataId);
    }

    @Override
    public void saveOrUpdate(LayerData layerData) {
        getSession().saveOrUpdate(layerData);

    }

    @Override
    public void merge(LayerData layerData) {
        getSession().merge(layerData);

    }

    @Override
    public void deleteLayerData(LayerData layerData) {
        getSession().delete(layerData);

    }

    @Override
    public void deleteAll() {

        Query<?> query = getSession().createQuery("delete from LayerData");

        int result = query.executeUpdate();

        if (result > 0) {
            logger.info("All layerData entries are cleaned up");
        }
    }

}
