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

drop sequence IF EXISTS akraino.seq_blueprint_data;
drop sequence IF EXISTS akraino.seq_timeslot;
drop sequence IF EXISTS akraino.seq_submission_base_info;
drop sequence IF EXISTS akraino.seq_operational_submission;
drop sequence IF EXISTS akraino.seq_layer_data;

drop table IF EXISTS akraino.operational_submission;
drop table IF EXISTS akraino.submission_base_info;
drop table IF EXISTS akraino.blueprint_data;
drop table IF EXISTS akraino.timeslot;
drop table IF EXISTS akraino.layer_data;

CREATE SCHEMA IF NOT EXISTS akraino
 AUTHORIZATION postgres;

CREATE TABLE akraino.timeslot
(
   timeslot_id bigint not NULL unique,
   start_date_time text not NULL,
   duration int not NULL
)
WITH (
  OIDS = FALSE
)
;
ALTER TABLE akraino.timeslot
  OWNER TO postgres;
  
CREATE TABLE akraino.layer_data
(
   layer_data_id bigint not NULL unique,
   layer text not NULL,
   description text not NULL
)
WITH (
  OIDS = FALSE
)
;
ALTER TABLE akraino.layer_data
  OWNER TO postgres;
  
CREATE TABLE akraino.blueprint_data
(
   blueprint_data_id bigint not NULL unique,
   blueprint text not NULL,
   version text not NULL,
   lab text not NULL,
   layer_data_id bigint not NULL,
   CONSTRAINT layer_data_id_fk FOREIGN KEY (layer_data_id)
      REFERENCES akraino.layer_data (layer_data_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS = FALSE
)
;
ALTER TABLE akraino.blueprint_data
  OWNER TO postgres;

CREATE TABLE akraino.submission_base_info
(
   submission_base_info_id bigint not NULL unique,
   blueprint_data_id bigint not NULL,
   timeslot_id bigint not NULL,
   CONSTRAINT blueprint_data_id_fk FOREIGN KEY (blueprint_data_id)
      REFERENCES akraino.blueprint_data (blueprint_data_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
   CONSTRAINT timeslot_id_fk FOREIGN KEY (timeslot_id)
      REFERENCES akraino.timeslot (timeslot_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS = FALSE
)
;
ALTER TABLE akraino.submission_base_info
  OWNER TO postgres;
  
CREATE TABLE akraino.operational_submission
(
   operational_submission_id bigint not NULL unique,
   status text not NULL,
   submission_base_info_id bigint not NULL,
   CONSTRAINT submission_base_info_id_fk FOREIGN KEY (submission_base_info_id)
      REFERENCES akraino.submission_base_info (submission_base_info_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS = FALSE
)
;
ALTER TABLE akraino.operational_submission
  OWNER TO postgres;

CREATE SEQUENCE akraino.seq_blueprint_data
  START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE akraino.seq_timeslot
  START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE akraino.seq_submission_base_info
  START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE akraino.seq_operational_submission
  START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE akraino.seq_layer_data
  START WITH 1 INCREMENT BY 1;

commit;
