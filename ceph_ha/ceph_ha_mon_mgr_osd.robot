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
    [Documentation]     Check the ceph monitor components
    ...                 in the cluster
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHMON}
                        ...    | grep ${NODENAME}
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}     Running

Check ceph-mon health state
    [Documentation]     The health of the ceph-mon component
    ...                 should be: HEALTH OK
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHMON} --field-selector spec.nodeName=${NODENAME} | sed -n 2p | awk '{print $1}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    ceph-mon
    Start Command       kubectl exec -n ceph ${stdout} -- ceph status
    ${output}=          Read Command Output
    Append To File      ${LOG}  ${output}${\n}
    Should Contain      ${output}    HEALTH_OK

Failure of ceph monitor
    [Documentation]     Wait 15s for ceph-mon to be fully deleted
    ${output}=          Execute Command
                        ...  kubectl -n ceph delete pod --selector ${CEPHMON}
                        ...    --field-selector='spec.nodeName=${NODENAME}'
    Append To File      ${LOG}  ${output}${\n}
    Sleep               15s
    Should Contain      ${output}     deleted

After deletion verify ceph monitor
    [Documentation]     Wait 3m for ceph monitor to recover
    Sleep               3 minutes
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHMON}
                        ...    | grep ${NODENAME}
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}     Running

Check ceph-mon health
    [Documentation]     The health of the ceph-mon component
                        ...   should return to HEALTH OK after it's fully restored
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHMON} --field-selector spec.nodeName=${NODENAME} | sed -n 2p | awk '{print $1}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    ceph-mon
    Start Command       kubectl exec -n ceph ${stdout} -- ceph status
    ${output}=          Read Command Output
    Append To File      ${LOG}  ${output}${\n}
    Should Contain      ${output}    HEALTH_OK

## ceph manager
Verify ceph manager status
    [Documentation]     Check the status of the ceph manager components
    ...                 in the cluster
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHMGR}
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}     Running

Check ceph-mgr health state
    [Documentation]     The health of the ceph-mgr component
                        ...   should be HEALTH OK
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHMGR} --field-selector spec.nodeName=${NODENAME} | sed -n 2p | awk '{print $1}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    ceph-mgr
    Start Command       kubectl exec -n ceph ${stdout} -- ceph status
    ${output}=          Read Command Output
    Append To File      ${LOG}  ${output}${\n}
    Should Contain      ${output}    HEALTH_OK

Failure of ceph manager
    [Documentation]     Wait 15s for ceph-mgr component to be fully deleted
    ${output}=          Execute Command
                        ...  kubectl -n ceph delete pod --selector ${CEPHMGR}
                        ...    --field-selector='spec.nodeName=${NODENAME}'
    Append To File      ${LOG}  ${output}${\n}
    Sleep               15s
    Should Contain      ${output}     deleted

After deletion verify ceph manager
    [Documentation]     Wait 2m for ceph manager to recover
    Sleep               2 minutes
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHMGR}
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}     Running

Check ceph-mgr health
    [Documentation]     The health state of the ceph-mgr component
                        ...   should return to HEALTH OK after it's fully restored
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHMGR} --field-selector spec.nodeName=${NODENAME} | sed -n 2p | awk '{print $1}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    ceph-mgr
    Start Command       kubectl exec -n ceph ${stdout} -- ceph status
    ${output}=          Read Command Output
    Append To File      ${LOG}  ${output}${\n}
    Should Contain      ${output}    HEALTH_OK

## ceph osd
Verify ceph-osd status
    [Documentation]     Check the ceph osd components
    ...                 in the cluster
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHOSD} --field-selector spec.nodeName=${NODENAME} | sed -n 2p | awk '{print $1,$2,$3}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    1/1 Running

Check ceph-osd health state
    [Documentation]     The health of ceph-osd component
                        ...   should be HEALTH OK
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHOSD} --field-selector spec.nodeName=${NODENAME} | sed -n 2p | awk '{print $1}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    ceph-osd
    Start Command       kubectl exec -n ceph ${stdout} -- ceph status
    ${output}=          Read Command Output
    Append To File      ${LOG}  ${output}${\n}
    Should Contain      ${output}    HEALTH_OK

Failure of ceph-osd pod
    [Documentation]     Wait 5s for ceph-osd element to be
    ...                 fully deleted
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHOSD} --field-selector spec.nodeName=${NODENAME} | sed -n 2p | awk '{print $1}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    ceph-osd
    Start Command       kubectl -n ceph delete pod ${stdout}
    ${output}=          Read Command Output
    Sleep               5s
    Append To File      ${LOG}  ${output}${\n}
    Should Contain      ${output}    deleted

After deletion verify ceph osd
    [Documentation]     Ceph-osd componenets should recover in 3m
    Sleep               2 minutes 40 seconds
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHOSD} --field-selector spec.nodeName=${NODENAME} | sed -n 2p | awk '{print $1,$2,$3}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    1/1 Running

Check ceph-osd health state
    [Documentation]     The health state of the ceph-osd component
                        ...   should return to HEALTH OK after it's fully restored
    Start Command       kubectl -n ceph get pod -o wide --selector ${CEPHOSD} --field-selector spec.nodeName=${NODENAME} | sed -n 2p | awk '{print $1}'
    ${stdout}=          Read Command Output
    Append To File      ${LOG}  ${stdout}${\n}
    Should Contain      ${stdout}    ceph-osd
    Start Command       kubectl exec -n ceph ${stdout} -- ceph status
    ${output}=          Read Command Output
    Append To File      ${LOG}  ${output}${\n}
    Should Contain      ${output}    HEALTH_OK

*** Keywords ***
Open Connection And Log In
  Open Connection       ${HOST}
  Login With Public Key    ${USERNAME}  /root/.ssh/${USERNAME}_id_rsa
