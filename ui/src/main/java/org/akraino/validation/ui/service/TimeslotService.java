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
import java.time.LocalDateTime;
import java.util.List;

import org.akraino.validation.ui.dao.TimeslotDAO;
import org.akraino.validation.ui.entity.Timeslot;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TimeslotService {

    private static final Logger logger = Logger.getLogger(TimeslotService.class);

    @Autowired
    private TimeslotDAO timeslotDAO;

    public void saveTimeslot(Timeslot timeslot) throws ClassNotFoundException, SQLException {

        timeslotDAO.saveOrUpdate(timeslot);

    }

    public List<Timeslot> getTimeslots() throws ClassNotFoundException, SQLException {

        return timeslotDAO.getTimeslots();

    }

    public void loadInitialData() {
        // TODO: This data should be stored in a file. Then, this function should retrieve data from this file and store
        // in the db.
        Timeslot timeslot = new Timeslot();
        timeslot.setStartDateTime(LocalDateTime.now().toString());
        timeslot.setDuration(1234);
        timeslotDAO.saveOrUpdate(timeslot);

        timeslot = new Timeslot();
        timeslot.setStartDateTime(LocalDateTime.now().toString());
        timeslot.setDuration(12);
        timeslotDAO.saveOrUpdate(timeslot);

        timeslot = new Timeslot();
        timeslot.setStartDateTime(LocalDateTime.now().toString());
        timeslot.setDuration(7);
        timeslotDAO.saveOrUpdate(timeslot);

    }

    public void deleteAll() {
        timeslotDAO.deleteAll();
    }

}
