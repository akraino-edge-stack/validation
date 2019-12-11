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
Documentation     Validation, Auditing Hardening Compliance
Library           SSHLibrary
Library           OperatingSystem
Library           BuiltIn
Library           Process
Suite Setup       Run Keywords
...               Open Connection And Log In
...               Install Lynis
Test Teardown     Download Logs
Suite Teardown    Run Keywords
...               Uninstall Lynis
...               Close All Connections

*** Variables ***
${FULL_SUITE}            ${SUITE_NAME.replace(' ','_')}

*** Test Cases ***
Run Lynis Audit System
    [Documentation]         Run Lynis
    ${log} =  Set Variable  ${OUTPUT DIR}${/}${FULL_SUITE}.${TEST NAME.replace(' ','_')}.log
    ${result}=              Execute Command  lynis audit system --quick  sudo=true
    Append To File          ${log}  ${result}${\n}


*** Keywords ***
Open Connection And Log In
    Open Connection        ${HOST}
    Login With Public Key  ${USERNAME}  ${SSH_KEYFILE}

Install Lynis
    [Documentation]  Install Lynis
    Execute Command  yum install -y lynis  sudo=true

Uninstall Lynis
    [Documentation]  Uninstall Lynis
    Execute Command  yum erase -y lynis  sudo=true

Download Logs
    [Documentation]  Downloading logs and removing them
    SSHLibrary.Get File  /var/log/lynis.log  ${OUTPUT DIR}/lynis.log
    Execute Command  rm /var/log/lynis.log  sudo=True
    SSHLibrary.Get File  /var/log/lynis-report.dat  ${OUTPUT DIR}/lynis-report.dat
    Execute Command  rm /var/log/lynis-report.dat  sudo=True