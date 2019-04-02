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
Documentation     HA services tests: docker and kubelet
Library           SSHLibrary
Library           OperatingSystem
Library           BuiltIn
Suite Setup       Open Connection And Log In
Suite Teardown    Close All Connections

*** Variables ***
${HOST}           localhost
${USERNAME}       localadmin
${LOG}            /opt/akraino/validation/ha/print_docker_kubelet.txt


## Container Runtime

*** Test Cases ***
Verify status of container runtime
        [Documentation]         container runtime active
        Start Command           systemctl status docker.service | grep -B4 "Active: "
        ${stdout}=              Read Command Output
        Append To File          ${LOG}  ${stdout}${\n}
        Should Contain          ${stdout}              Active: active (running)


Fail container runtime
        [Documentation]         container runtime stopped
        ${output}  ${rc}=       Execute Command         systemctl stop docker.service    return_rc=True    sudo=True
        Append To File          ${LOG}  ${output}${\n}
        Should Be Equal As Integers  ${rc}  0
        Sleep                   8s
        Start Command           systemctl status docker.service | grep -B4 "Active: "
        ${stdout}=              Read Command Output
        Append To File          ${LOG}  ${stdout}${\n}
        Should Contain          ${stdout}              Active: inactive (dead)


Start container runtime
        [Documentation]         container runtime active
        Sleep                   1 minute
        ${output}  ${rc}=       Execute Command         systemctl start docker.service    return_rc=True    sudo=True
        Append To File          ${LOG}  ${output}${\n}
        Should Be Equal As Integers  ${rc}  0
        Sleep                   8s
        Start Command           systemctl status docker.service | grep -B4 "Active: "
        ${stdout}=              Read Command Output
        Append To File          ${LOG}  ${stdout}${\n}
        Should Contain          ${stdout}              Active: active (running)

## Kubelet Service

Verify kubelet status
        [Documentation]         kubelet service active
        Start Command           systemctl status kubelet.service | grep -B4 "Active: "
        ${stdout}=              Read Command Output
        Append To File          ${LOG}  ${stdout}${\n}
        Should Contain          ${stdout}              Active: active (running)


Fail kubelet service
        [Documentation]         kubelet service stopped
        ${output}  ${rc}=       Execute Command         systemctl stop kubelet.service    return_rc=True    sudo=True
        Append To File          ${LOG}  ${output}${\n}
        Should Be Equal As Integers  ${rc}  0
        Sleep                   8s
        Start Command           systemctl status kubelet.service | grep -B4 "Active: "
        ${stdout}=              Read Command Output
        Append To File          ${LOG}  ${stdout}${\n}
        Should Contain          ${stdout}              Active: inactive (dead)


Start kubelet service
        [Documentation]         kubelet active
        Sleep                   1 minute
        ${output}  ${rc}=       Execute Command         systemctl start kubelet.service    return_rc=True    sudo=True
        Append To File          ${LOG}  ${output}${\n}
        Should Be Equal As Integers  ${rc}  0
        Sleep                   8s
        Start Command           systemctl status kubelet.service | grep -B4 "Active: "
        ${stdout}=              Read Command Output
        Append To File          ${LOG}  ${stdout}${\n}
        Should Contain          ${stdout}              Active: active (running)



*** Keywords ***
Open Connection And Log In
  Open Connection       ${HOST}
  Login With Public Key    ${USERNAME}   /root/.ssh/${USERNAME}_id_rsa

