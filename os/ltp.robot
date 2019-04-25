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
Documentation     OS LTP
Library           SSHLibrary
Library           OperatingSystem
Library           BuiltIn
Library           Process
Suite Setup       Open Connection And Log In
Suite Teardown    Close All Connections

*** Variables ***
${HOST}           localhost
${USERNAME}       root
${PASSWORD}       root_password
${LOG}            /opt/akraino/validation/os/print_ltp.txt


*** Test Cases ***
#Run whole ltp suite test
#    [Documentation]         2536 tcs, takes ~5hrs
#    ${result}=              Run Process     ./runltp     shell=yes     cwd=/opt/ltp     stdout=${LOG}
#    Append To File          ${LOG}  ${result}${\n}
#    Sleep                   2s


#Run ltp syscalls test suite
#    [Documentation]         Takes about 45 mins
#    ${result}=              Run Process     ./runltp -f syscalls     shell=yes     cwd=/opt/ltp    stdout=${LOG}
#    Append To File          ${LOG}  ${result}${\n}
#    Sleep                   2s


Run ltp syscalls madvise
    [Documentation]         madvise01-10
    ${result}=              Run Process     ./runltp -f syscalls -s madvise     shell=yes     cwd=/opt/ltp     stdout=${LOG}
    Append To File          ${LOG}  ${result}${\n}
    Sleep                   2s


*** Keywords ***
Open Connection And Log In
  Open Connection       ${HOST}
  Login                 ${USERNAME}     ${PASSWORD}

