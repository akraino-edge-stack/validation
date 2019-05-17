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

import org.akraino.validation.ui.data.SubmissionStatus;

@Entity
@Table(name = "akraino.operational_submission")
public class OperationalSubmission {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operational_submission_id_generator")
    @SequenceGenerator(name = "operational_submission_id_generator",
            sequenceName = "akraino.seq_operational_submission", allocationSize = 1)
    @Column(name = "operational_submission_id")
    private int id;

    @Column(name = "status")
    private SubmissionStatus status;

    @OneToOne
    @JoinColumn(name = "submission_base_info_id")
    private SubmissionBaseInfo submissionBaseInfo;

    public void setOperationalSubmissionId(int operationalSubmissionId) {
        this.id = operationalSubmissionId;
    }

    public int getOperationalSubmissionId() {
        return id;
    }

    public SubmissionStatus getSubmissionStatus() {
        return this.status;
    }

    public void setStatus(SubmissionStatus submissionStatus) {
        this.status = submissionStatus;
    }

    public void setsubmissionBaseInfo(SubmissionBaseInfo submissionBaseInfo) {
        this.submissionBaseInfo = submissionBaseInfo;
    }

    public SubmissionBaseInfo getSubmissionBaseInfo() {
        return this.submissionBaseInfo;
    }

}
