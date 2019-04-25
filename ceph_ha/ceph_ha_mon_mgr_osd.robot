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
Suite Setup       Open Connection And Log In
Suite Teardown    Close All Connections

*** Test Cases ***
## ceph heath
Check ceph health state
    [Documentation]     The health of the ceph components
    ...                 should be: HEALTH OK
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHMON} ${SELECTOR}=${NODENAME} | sed -n 2p | awk '{print $1}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    ceph-mon
    Start Command       kubectl exec -n ceph ${stdout} -- ceph status
    ${output}=          Read Command Output
    Append To File      ${LOG}  ${output}${\n}
    Should Contain      ${output}    HEALTH_OK

## ceph monitor
Verify ceph monitor status
    [Documentation]     Check the ceph monitor components
    ...                 in the cluster
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHMON} | grep ${NODENAME}
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}     1/1       Running

Failure of ceph monitor
    [Documentation]     Wait 15s for ceph-mon to be fully deleted
    ${output}=          Execute Command
                        ...  kubectl -n ceph delete pod --selector ${CEPHMON} --field-selector='spec.nodeName=${NODENAME}'
    Append To File      ${LOG}  ${output}${\n}
    Sleep               15s
    Should Contain      ${output}     deleted

After deletion verify ceph monitor
    [Documentation]     Wait 3m for ceph monitor to recover
    Sleep               3 minutes
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHMON} | grep ${NODENAME}
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}     1/1       Running

## ceph manager
Verify ceph manager status
    [Documentation]     Check the status of the ceph manager components
    ...                 in the cluster
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHMGR}
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}     1/1       Running

Failure of ceph manager
    [Documentation]     Wait 15s for ceph-mgr component to be fully deleted
    ${output}=          Execute Command
                        ...  kubectl -n ceph delete pod --selector ${CEPHMGR} --field-selector='spec.nodeName=${NODENAME}'
    Append To File      ${LOG}  ${output}${\n}
    Sleep               15s
    Should Contain      ${output}     deleted

After deletion verify ceph manager
    [Documentation]     Wait 3m for ceph manager to recover
    Sleep               3 minutes
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHMGR}
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}     1/1       Running

## ceph osd
Verify ceph-osd status
    [Documentation]     Check the ceph osd components
    ...                 in the cluster
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHOSD} ${SELECTOR}=${NODENAME} | sed -n 2p | awk '{print $1,$2,$3}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    1/1 Running

Failure of ceph-osd pod
    [Documentation]     Wait 5s for ceph-osd element to be
    ...                 fully deleted
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHOSD} ${SELECTOR}=${NODENAME} | sed -n 2p | awk '{print $1}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    ceph-osd
    Start Command       kubectl -n ceph delete pod ${stdout}
    ${output}=          Read Command Output
    Sleep               5s
    Append To File      ${LOG}  ${output}${\n}
    Should Contain      ${output}    deleted

After deletion verify ceph osd
    [Documentation]     Wait 4m for ceph-osd to recover
    Sleep               4 minutes
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHOSD} ${SELECTOR}=${NODENAME} | sed -n 2p | awk '{print $1,$2,$3}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    1/1 Running

## ceph heath
After ceph purge check health state
    [Documentation]     The health of the ceph components
    ...                 should be: HEALTH OK
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHMON} ${SELECTOR}=${NODENAME} | sed -n 2p | awk '{print $1}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    ceph-mon
    Start Command       kubectl exec -n ceph ${stdout} -- ceph status
    ${output}=          Read Command Output
    Append To File      ${LOG}  ${output}${\n}
    Should Contain      ${output}    HEALTH_OK

*** Keywords ***
Open Connection And Log In
  Open Connection       ${HOST}
  Login With Public Key    ${USERNAME}  /root/.ssh/${USERNAME}_id_rsa

