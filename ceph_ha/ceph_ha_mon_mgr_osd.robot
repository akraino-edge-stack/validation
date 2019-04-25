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
Documentation     Ceph HA test cases for monitor and manager
Library           SSHLibrary
Library           OperatingSystem
Library           BuiltIn
Resource          ceph_ha_mon_mgr_osd.resource
Suite Setup       Open Connection And Log In
Suite Teardown    Close All Connections

*** Test Cases ***

## ceph monitor
Verify ceph monitor status
    [Documentation]     ceph mon
    Start Command       kubectl -n ceph get pod -o wide --selector "application=ceph,component=mon" | grep ${NODENAME}
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}     Running

Failure of ceph monitor
    [Documentation]     ceph-mon pods deleted
    ${output}=          Execute Command    kubectl -n ceph delete pod --selector 'application=ceph,component=mon' --field-selector='spec.nodeName=${NODENAME}'
    Append To File      ${LOG}  ${output}${\n}
    Sleep               15s
    Should Contain      ${output}     deleted

Verify ceph monitor state
    [Documentation]     ceph-mon recovers in 3m
    Sleep               15s
    Start Command       kubectl -n ceph get pod -o wide --selector "application=ceph,component=mon" | grep ${NODENAME}
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}     Running

## ceph manager
Verify ceph manager status
    [Documentation]     ceph-mgr components active
    Start Command       kubectl -n ceph get pod -o wide --selector "application=ceph,component=mgr"
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}     Running

Failure of ceph manager
    [Documentation]     ceph pods deleted
    ${output}=          Execute Command    kubectl -n ceph delete pod --selector 'application=ceph,component=mgr' --field-selector='spec.nodeName=${NODENAME}'
    Append To File      ${LOG}  ${output}${\n}
    Sleep               15s
    Should Contain      ${output}     deleted

Verify ceph manager state
    [Documentation]     ceph-mgr recovers in 2m
    Sleep               15s
    Start Command       kubectl -n ceph get pod -o wide --selector "application=ceph,component=mgr"
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}     Running

## ceph osd
Verify ceph-osd status
    Start Command       kubectl -n ceph get pod -o wide --selector "application=ceph,component=osd" --field-selector spec.nodeName=${NODENAME} | sed -n 2p | awk '{print $1,$2,$3}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    1/1 Running

Delete ceph-osd pod
    Start Command       kubectl -n ceph get pod -o wide --selector "application=ceph,component=osd" --field-selector spec.nodeName=${NODENAME} | sed -n 2p | awk '{print $1}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    ceph-osd
    Start Command       kubectl -n ceph delete pod ${stdout}
    ${output}=          Read Command Output
    Append To File      ${LOG}  ${output}${\n}
    Should Contain      ${output}    deleted

Ceph-osd component is re-created and running
    Sleep               2 minutes 40 seconds
    Start Command       kubectl -n ceph get pod -o wide --selector "application=ceph,component=osd" --field-selector spec.nodeName=${NODENAME} | sed -n 2p | awk '{print $1,$2,$3}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    1/1 Running

*** Keywords ***
Open Connection And Log In
  Open Connection       ${HOST}
  Login With Public Key    ${USERNAME}   /root/.ssh/${USERNAME}_id_rsa
