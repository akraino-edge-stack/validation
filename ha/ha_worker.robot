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
Documentation     Run HA Test - Fail Control Plane 3
Library           SSHLibrary
Library           OperatingSystem
Suite Setup       Open Connection And Log In
Suite Teardown    Close All Connections

*** Variables ***
${HOST}           localhost
${USERNAME}       ipmi_admin_username
${PASSWORD}       ipmi_admin_password
${LOG}            /opt/akraino/validation/ha/print_ha.txt

*** Test Cases ***
Power Status of Host
        [Documentation]         Get Power Status on iDRAC
        ${output}=              Execute Command         racadm serveraction powerstatus
        Append To File          ${LOG}  ${output}${\n}
        Should Contain          ${output}               ON


Fail Control Plane
        [Documentation]         Perform a reboot operation via iDRAC
        ${output}               Execute Command         racadm serveraction hardreset
        Append To File          ${LOG}  ${output}${\n}
        Should Contain          ${output}               successful


*** Keywords ***
Open Connection And Log In
  Open Connection       ${HOST}
  Login                 ${USERNAME}     ${PASSWORD}

