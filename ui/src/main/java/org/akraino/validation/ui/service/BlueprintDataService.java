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

import org.akraino.validation.ui.dao.BlueprintDataDAO;
import org.akraino.validation.ui.data.Lab;
import org.akraino.validation.ui.entity.BlueprintData;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BlueprintDataService {

    private static final Logger logger = Logger.getLogger(BlueprintDataService.class);

    @Autowired
    private BlueprintDataDAO blueprintDataDAO;

    @Autowired
    private LayerDataService layerDataService;

    public void saveBlueprintData(BlueprintData blueprintData) throws ClassNotFoundException, SQLException {

        blueprintDataDAO.saveOrUpdate(blueprintData);

    }

    public List<BlueprintData> getBlueprintDatas() throws ClassNotFoundException, SQLException {

        return blueprintDataDAO.getBlueprintDatas();

    }

    public void loadInitialData() throws ClassNotFoundException, SQLException {
        // TODO: This data should be stored in a file. Then, this function should retrieve data from this file and store
        // in the db.
        BlueprintData blueprintData = new BlueprintData();
        blueprintData.setLayerData(layerDataService.getLayerDatas().get(0));
        blueprintData.setBlueprint("Unicycle Blueprint");
        blueprintData.setLab(Lab.ATT);
        blueprintData.setVersion("0.0.1-SNAPSHOT");
        blueprintDataDAO.saveOrUpdate(blueprintData);

        blueprintData = new BlueprintData();
        blueprintData.setLayerData(layerDataService.getLayerDatas().get(1));
        blueprintData.setBlueprint("Unicycle Blueprint");
        blueprintData.setLab(Lab.Ericsson);
        blueprintData.setVersion("0.0.2-SNAPSHOT");
        blueprintDataDAO.saveOrUpdate(blueprintData);

        blueprintData = new BlueprintData();
        blueprintData.setLayerData(layerDataService.getLayerDatas().get(2));
        blueprintData.setBlueprint("REC");
        blueprintData.setLab(Lab.Ericsson);
        blueprintData.setVersion("0.0.3-SNAPSHOT");
        blueprintDataDAO.saveOrUpdate(blueprintData);

        blueprintData = new BlueprintData();
        blueprintData.setLayerData(layerDataService.getLayerDatas().get(1));
        blueprintData.setBlueprint("REC");
        blueprintData.setLab(Lab.ATT);
        blueprintData.setVersion("0.0.3-SNAPSHOT");
        blueprintDataDAO.saveOrUpdate(blueprintData);

        blueprintData = new BlueprintData();
        blueprintData.setLayerData(layerDataService.getLayerDatas().get(0));
        blueprintData.setBlueprint("dummy");
        blueprintData.setLab(Lab.ATT);
        blueprintData.setVersion("0.0.7-SNAPSHOT");
        blueprintDataDAO.saveOrUpdate(blueprintData);

    }

    public void deleteAll() {
        blueprintDataDAO.deleteAll();
    }

}
