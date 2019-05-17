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
package org.akraino.validation.ui.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.akraino.validation.ui.data.BlueprintLayer;

@Entity
@Table(name = "akraino.layer_data")
public class LayerData implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "layer_data_id_generator")
    @SequenceGenerator(name = "layer_data_id_generator", sequenceName = "akraino.seq_layer_data", allocationSize = 1)
    @Column(name = "layer_data_id")
    private int layerDataId;

    @Column(name = "layer")
    private BlueprintLayer layer;

    @Column(name = "description")
    private String description;

    public int getLayerDataId() {
        return layerDataId;
    }

    public void setLayerDataId(int layerDataId) {
        this.layerDataId = layerDataId;
    }

    public BlueprintLayer getLayer() {
        return layer;
    }

    public void setLayer(BlueprintLayer layer) {
        this.layer = layer;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
