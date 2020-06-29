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
Library           String
Suite Setup       Run Keywords
...               Open Connection And Log In
...               Install Lynis
Test Teardown     Download Logs
Suite Teardown    Run Keywords
...               Uninstall Lynis
...               Close All Connections

*** Variables ***
${FULL_SUITE}  ${SUITE_NAME.replace(' ','_')}

*** Test Cases ***
Run Lynis Audit System
    [Documentation]  Run Lynis
    ${log} =  Set Variable  ${OUTPUT DIR}${/}${FULL_SUITE}.${TEST NAME.replace(' ','_')}.log
    ${stdout}    ${rc} =  Execute Command  cd lynis && sudo ./lynis audit system --quick  return_rc=True
    Append To File  ${log}  ${stdout}${\n}
    Should Be Equal As Integers  ${rc}	0

    ${File} =  OperatingSystem.Get File  ${CURDIR}/tests_list.txt
    ${list} =  Split To Lines  ${File}
    FOR  ${line}  IN  @{list}
       ${value} =  Replace String  ${line}  /  \\/
       Set Global Variable  ${VALUE}  ${value}
       Verify Logs
    END

*** Keywords ***
Open Connection And Log In
    Open Connection  ${HOST}
    Run Keyword IF  '${SSH_KEYFILE}' != 'None'  Login With Public Key  ${USERNAME}  ${SSH_KEYFILE}  ELSE IF  '${PASSWORD}' != 'None'  Login  ${USERNAME}  ${PASSWORD}  ELSE  FAIL

Install Lynis
    [Documentation]  Install Lynis
    Put File  /opt/akraino/lynis-remote.tar.gz
    Execute Command  tar xzf lynis-remote.tar.gz && sudo chown -R 0:0 lynis

Uninstall Lynis
    [Documentation]  Uninstall Lynis
    Execute Command  rm lynis-remote.tar.gz
    Execute Command  rm -rf ~/lynis /var/log/lynis.log /var/log/lynis-report.dat  sudo=True

Download Logs
    [Documentation]  Downloading logs and removing them
    Execute Command  chmod +r /var/log/lynis.log  sudo=True
    SSHLibrary.Get File  /var/log/lynis.log  ${OUTPUT DIR}/lynis.log
    Execute Command  rm /var/log/lynis.log  sudo=True
    Execute Command  chmod +r /var/log/lynis-report.dat  sudo=True
    SSHLibrary.Get File  /var/log/lynis-report.dat  ${OUTPUT DIR}/lynis-report.dat
    Execute Command  rm /var/log/lynis-report.dat  sudo=True

Verify Logs
    ${rc}  ${test_result} =  Run And Return Rc And Output  sed -n '/${VALUE}/,/===/p' ${CURDIR}/lynis.log
    Should Be Equal As Integers  ${rc}  0
    ${status1} =  Evaluate  "Hardening: assigned maximum number of hardening points" in """${test_result}"""
    ${status2} =  Evaluate  "Hardening: assigned partial number of hardening points" in """${test_result}"""
    Append To File  ${CURDIR}/status.log  ${status1}${\n}
    Append To File  ${CURDIR}/status.log  ${status2}${\n}
    Run Keyword If  '${status1}' == 'False' and '${status2}' == 'False'  FAIL  Warnings discovered

