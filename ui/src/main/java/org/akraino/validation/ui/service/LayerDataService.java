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

import org.akraino.validation.ui.dao.LayerDataDAO;
import org.akraino.validation.ui.data.BlueprintLayer;
import org.akraino.validation.ui.entity.LayerData;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LayerDataService {

    private static final Logger logger = Logger.getLogger(LayerDataService.class);

    @Autowired
    private LayerDataDAO layerDataDAO;

    public void saveLayerData(LayerData layerData) throws ClassNotFoundException, SQLException {

        layerDataDAO.saveOrUpdate(layerData);

    }

    public List<LayerData> getLayerDatas() throws ClassNotFoundException, SQLException {

        return layerDataDAO.getLayerDatas();

    }

    public void loadInitialData() {
        // TODO: This data should be stored in a file. Then, this function should retrieve data from this file and store
        // in the db.
        LayerData layerData = new LayerData();
        layerData.setDescription("Description #1");
        layerData.setLayer(BlueprintLayer.Hardware);
        layerDataDAO.saveOrUpdate(layerData);

        layerData = new LayerData();
        layerData.setDescription("Description #2");
        layerData.setLayer(BlueprintLayer.K8s);
        layerDataDAO.saveOrUpdate(layerData);

        layerData = new LayerData();
        layerData.setDescription("Description #3");
        layerData.setLayer(BlueprintLayer.Kubeless);
        layerDataDAO.saveOrUpdate(layerData);

    }

    public void deleteAll() {
        layerDataDAO.deleteAll();
    }

}
