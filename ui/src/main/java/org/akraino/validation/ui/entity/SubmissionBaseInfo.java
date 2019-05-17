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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "akraino.submission_base_info")
public class SubmissionBaseInfo {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "submission_base_info_id_generator")
    @SequenceGenerator(name = "submission_base_info_id_generator", sequenceName = "akraino.seq_submission_base_info",
            allocationSize = 1)
    @Column(name = "submission_base_info_id")
    private int id;

    @OneToOne
    @JoinColumn(name = "blueprint_data_id")
    private BlueprintData blueprintData;

    @OneToOne
    @JoinColumn(name = "timeslot_id")
    private Timeslot timeslot;

    public void setSubmissionBaseInfoId(int submissionBaseInfoId) {
        this.id = submissionBaseInfoId;
    }

    public int getSubmissionBaseInfoId() {
        return id;
    }

    public void setBlueprintData(BlueprintData blueprintData) {
        this.blueprintData = blueprintData;
    }

    public BlueprintData getBlueprintData() {
        return this.blueprintData;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public Timeslot getTimeslot() {
        return this.timeslot;
    }

}
