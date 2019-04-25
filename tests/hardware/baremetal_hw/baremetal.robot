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
Documentation     Bare metal HW test cases to verify configuration
...               from given blueprint
Resource          variables.resource
Library           OperatingSystem
Library           BuiltIn
Library           Process

*** Test Cases ***
Verify cluster connectivity
    [Documentation]    Wait a few seconds to prove connectivity
    @{nodes}  Create List  ${HOST_MR}  ${HOST_WR1}  ${HOST_WR2}
    FOR  ${node}  IN  @{nodes}
         ${output}=        Run    ping ${node} -c 3
         Append To File    ${LOG}  ${output}${\n}
         Should Contain    ${output}    3 packets transmitted, 3 received
    END

Verify mac address
    [Documentation]   Data should match mac input
    ${output}=        Run    curl ${BASE_URI} -k | python -m json.tool
    Append To File    ${LOG}  ${output}${\n}
    Should Contain    ${output}    ${MACADDR}

Verify chassis details
    [Documentation]   Data should match chassis input
    ${output}=        Run
    ...   curl --user ${USERNAME}:${PASSWORD} ${BASE_URI}/Chassis/System.Embedded.1 -k | python -m json.tool
    Append To File    ${LOG}  ${output}${\n}
    Should Contain    ${output}    ${CHASSIS}

Verify iDRAC settings
    [Documentation]   Data should match idrac input
    ${output}=        Run
    ...   curl --user ${USERNAME}:${PASSWORD} ${BASE_URI}/Managers/iDRAC.Embedded.1/Attributes -k | python -m json.tool
    Append To File    ${LOG}  ${output}${\n}
    Should Contain    ${output}    ${IDRACIP}

Verify boot registry
    [Documentation]   Data should match boot input
    ${output}=        Run
    ...   curl --user ${USERNAME}:${PASSWORD} ${BASE_URI}/Systems/System.Embedded.1/BootSources/BootSourcesRegistry -k | python -m json.tool
    Append To File    ${LOG}  ${output}${\n}
    Should Contain    ${output}    ${BOOTSEQ}

Verify bios settings
    [Documentation]   Data should match bios input
    ${output}=        Run
    ...   curl --user ${USERNAME}:${PASSWORD} ${BASE_URI}/Systems/System.Embedded.1/Bios -k | python -m json.tool
    Append To File    ${LOG}  ${output}${\n}
    Should Contain    ${output}    ${BIOSVER}

Verify firmware version
    [Documentation]   Data should match firmware input
    ${output}=        Run
    ...   curl --user ${USERNAME}:${PASSWORD} ${BASE_URI}/UpdateService/FirmwareInventory -k | python -m json.tool
    Append To File    ${LOG}  ${output}${\n}
    Should Contain    ${output}   ${FIRMWARE}

