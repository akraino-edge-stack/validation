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
Documentation     Validation, robustness and stability of Linux
Library           SSHLibrary
Library           OperatingSystem
Library           BuiltIn
Library           Process
Suite Setup       Run Keywords
...               Open Connection And Log In
...               Install LTP
Suite Teardown    Run Keywords
...               Uninstall LTP
...               Close All Connections

*** Variables ***


*** Test Cases ***
Run whole ltp test suite
    [Documentation]         Wait ~5hrs to complete 2536 tests
    ${log}                  ${LOG_PATH}${/}${SUITE_NAME.replace(' ','_')}_whole.log
    ${result}=              Execute Command  sudo /opt/ltp/runltp
    Append To File          ${log}  ${result}${\n}
    Sleep                   2s
    Should Contain          ${result}    INFO: ltp-pan reported all tests PASS

Run ltp syscalls test suite
    [Documentation]         Wait ~45m for syscalls to complete
    ${log}                  ${LOG_PATH}${/}${SUITE_NAME.replace(' ','_')}_syscalls.log
    ${result}=              Execute Command  sudo /opt/ltp/runltp -f syscalls
    Append To File          ${log}  ${result}${\n}
    Sleep                   2s
    Should Contain          ${result}    INFO: ltp-pan reported all tests PASS

Run ltp syscalls madvise
    [Documentation]         Wait ~1m for madvise01-10 to complete
    ${log}                  ${LOG_PATH}${/}${SUITE_NAME.replace(' ','_')}_syscalls_madvise.log
    ${result}=              Execute Command  sudo /opt/ltp/runltp -f syscalls -s madvise
    Append To File          ${log}  ${result}${\n}
    Sleep                   2s
    Should Contain          ${result}    INFO: ltp-pan reported all tests PASS

*** Keywords ***
Open Connection And Log In
    Open Connection        ${HOST}
    Login With Public Key  ${USERNAME}  ${SSH_KEYFILE}


Install LTP
    Put File  /opt/akraino/ltp.tar.gz  /tmp/ltp.tar.gz
    Execute Command  tar -xf /tmp/ltp.tar.gz -C /


Uninstall LTP
    Execute Command  rm -rf /opt/ltp
    Execute Command  rm /temp/ltp.tar.gz