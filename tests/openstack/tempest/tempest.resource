##############################################################################
# Copyright (c) 2019 AT&T Intellectual Property.                             #
# Copyright (c) 2019 Nokia.                                                  #
#                                                                            #
# Licensed under the Apache License, Version 2.0 (the "License");            #
# you maynot use this file except in compliance with the License.            #
#                                                                            #
# You may obtain a copy of the License at                                    #
#       http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                            #
# Unless required by applicable law or agreed to in writing, software        #
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  #
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.           #
# See the License for the specific language governing permissions and        #
# limitations under the License.                                             #
##############################################################################


*** Settings ***
Library            BuiltIn
Library            OperatingSystem
Library            Process


*** Variables ***
${REPORTDIR}       ${LOG_PATH}${/}${SUITE_NAME.replace(' ','_')}
${WORKDIR}         /tmp/tempest-run
${WORKSPACE}       sut
${BLACKLIST_FILE}  ${CURDIR}/blacklist.txt


*** Keywords ***
Tempest Suite Setup
    #Create OpenStack Resources
    Setup Workspace
    #Create Tempest Configuration File

Tempest Suite Teardown
    #Delete OpenStack Resources
    No Operation

Setup Workspace
    Run Process            tempest  workspace  remove  --name  ${WORKSPACE}
    Run Process            rm  -rf  ${WORKDIR}
    Create Directory       ${WORKDIR}/${WORKSPACE}
    Run Process            tempest  init  --name  ${WORKSPACE}  ${WORKDIR}/${WORKSPACE}
    Copy File              ${CURDIR}/tempest.conf  ${WORKDIR}/${WORKSPACE}/etc/

Run Tempest Test
    ${result}=             Run Process  tempest  run  --workspace  ${WORKSPACE}
     ...                                              --load-list  ${CURDIR}/test_list.txt
     ...                                              --blacklist-file  ${BLACKLIST_FILE}
     ...                                              --concurrency  4
     ...                                cwd=${WORKDIR}/${WORKSPACE}/logs  stdout=run.log
    Should Be Equal As Integers  ${result.rc}  0
    Should Contain         ${result.stdout}  - Failed: 0
    Copy Files             ${WORKDIR}/${WORKSPACE}/logs/*  ${REPORTDIR}/
