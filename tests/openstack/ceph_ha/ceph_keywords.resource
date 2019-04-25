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
Documentation     Ceph HA test cases for monitor, manager, osd
Library           SSHLibrary
Library           OperatingSystem
Library           BuiltIn
Resource          variables.resource

*** Variables ***
${LOG}        ${LOG_PATH}${/}${SUITE_NAME.replace(' ','_')}.log


*** Keywords ***
Open Connection And Log In
  Open Connection       ${HOST}
  Login With Public Key    ${USERNAME}  ${HOME}/.ssh/id_rsa

Ceph Should Be Healthy
    Start Command
    ...   kubectl -n ceph get pod -o wide --selector "application=ceph,component=mon" --field-selector=spec.nodeName=${NODENAME} | sed -n 2p | awk '{print $1}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    ceph-mon
    Start Command       kubectl exec -n ceph ${stdout} -- ceph status
    ${output}=          Read Command Output
    Append To File      ${LOG}  ${output}${\n}
    Should Contain      ${output}    HEALTH_OK

Verify Ceph Monitor Status
    Start Command       kubectl -n ceph get pod -o wide --selector "application=ceph,component=mon" | grep ${NODENAME}
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}     1/1       Running

Failure Of Ceph Monitor
    ${output}=          Execute Command
    ...  kubectl -n ceph delete pod --selector "application=ceph,component=mon" --field-selector='spec.nodeName=${NODENAME}'
    Append To File      ${LOG}  ${output}${\n}
    Sleep               15s
    Should Contain      ${output}     deleted

After Deletion Verify Ceph Monitor
    Sleep               3 minutes
    Start Command       kubectl -n ceph get pod -o wide --selector "application=ceph,component=mon" | grep ${NODENAME}
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}     1/1       Running

Verify Ceph Manager Status
    Start Command       kubectl -n ceph get pod -o wide --selector "application=ceph,component=mgr"
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}     1/1       Running

Failure Of Ceph Manager
    ${output}=          Execute Command
    ...  kubectl -n ceph delete pod --selector "application=ceph,component=mgr" --field-selector='spec.nodeName=${NODENAME}'
    Append To File      ${LOG}  ${output}${\n}
    Sleep               15s
    Should Contain      ${output}     deleted

After Deletion Verify Ceph Manager
    [Documentation]     Wait 3m for ceph manager to recover
    Sleep               3 minutes
    Start Command       kubectl -n ceph get pod -o wide --selector "application=ceph,component=mgr"
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}     1/1       Running

Verify Ceph Osd Status
    Start Command
    ...   kubectl -n ceph get pod -o wide --selector "application=ceph,component=osd" --field-selector=spec.nodeName=${NODENAME} | sed -n 2p | awk '{print $1,$2,$3}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    1/1 Running

Failure Of Ceph Osd Component
    Start Command
    ...   kubectl -n ceph get pod -o wide --selector "application=ceph,component=osd" --field-selector=spec.nodeName=${NODENAME} | sed -n 2p | awk '{print $1}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    ceph-osd
    Start Command       kubectl -n ceph delete pod ${stdout}
    ${output}=          Read Command Output
    Sleep               5s
    Append To File      ${LOG}  ${output}${\n}
    Should Contain      ${output}    deleted

After Deletion Verify Ceph Osd
    Sleep               4 minutes
    Start Command
    ...   kubectl -n ceph get pod -o wide --selector "application=ceph,component=osd" --field-selector=spec.nodeName=${NODENAME} | sed -n 2p | awk '{print $1,$2,$3}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    1/1 Running

