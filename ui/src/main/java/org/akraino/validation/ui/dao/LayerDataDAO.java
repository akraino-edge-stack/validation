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
package org.akraino.validation.ui.dao;

import java.util.List;

import org.akraino.validation.ui.entity.LayerData;

public interface LayerDataDAO {

    public void saveOrUpdate(LayerData layerData);

    public void merge(LayerData layerData);

    public List<LayerData> getLayerDatas();

    public LayerData getLayerData(LayerData layerDataId);

    public void deleteLayerData(LayerData layerData);

    public void deleteAll();

}
