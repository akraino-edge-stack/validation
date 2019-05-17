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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.akraino.validation.ui.data.Lab;

@Entity
@Table(name = "akraino.blueprint_data")
public class BlueprintData implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "blueprint_data_id_generator")
    @SequenceGenerator(name = "blueprint_data_id_generator", sequenceName = "akraino.seq_blueprint_data",
            allocationSize = 1)
    @Column(name = "blueprint_data_id")
    private int blueprintDataId;

    @Column(name = "blueprint")
    private String blueprint;

    @Column(name = "version")
    private String version;

    @OneToOne
    @JoinColumn(name = "layer_data_id")
    private LayerData layerData;

    @Column(name = "lab")
    private Lab lab;

    public int getBlueprintDataId() {
        return blueprintDataId;
    }

    public void setBlueprintDataId(int blueprintDataId) {
        this.blueprintDataId = blueprintDataId;
    }

    public String getBlueprint() {
        return blueprint;
    }

    public void setBlueprint(String blueprint) {
        this.blueprint = blueprint;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setLayerData(LayerData layerData) {
        this.layerData = layerData;
    }

    public LayerData getLayerData() {
        return layerData;
    }

    public void setLab(Lab lab) {
        this.lab = lab;
    }

    public Lab getLab() {
        return lab;
    }
}
