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
Documentation     Baremetal details
Resource          resource_baremetal.txt
Library           OperatingSystem
Library           BuiltIn
Library           Process

*** Test Cases ***
MAC Data
    ${output}=        Run    curl ${BASE_URI} -k | python -m json.tool
    Append To File    ${LOG}  ${output}${\n}
    Should Contain    ${output}    MACAddress

Chassis
    ${output}=        Run    curl --user ${USERNAME}:${PASSWORD} ${BASE_URI}/Chassis/System.Embedded.1 -k | python -m json.tool
    Append To File    ${LOG}  ${output}${\n}
    Should Contain    ${output}    SerialNumber

iDRAC settings
    ${output}=        Run    curl --user ${USERNAME}:${PASSWORD} ${BASE_URI}/Managers/iDRAC.Embedded.1/Attributes -k | python -m json.tool
    Append To File    ${LOG}  ${output}${\n}
    Should Contain    ${output}    CurrentIPv4.1.Address

Boot Registry
    ${output}=        Run    curl --user ${USERNAME}:${PASSWORD} ${BASE_URI}/Systems/System.Embedded.1/BootSources/BootSourcesRegistry -k | python -m json.tool
    Append To File    ${LOG}  ${output}${\n}
    Should Contain    ${output}    Boot Sequence

Bios Settings
    ${output}=        Run    curl --user ${USERNAME}:${PASSWORD} ${BASE_URI}/Systems/System.Embedded.1/Bios -k | python -m json.tool
    Append To File    ${LOG}  ${output}${\n}
    Should Contain    ${output}    SystemBiosVersion

Firmware version
    ${output}=        Run    curl --user ${USERNAME}:${PASSWORD} ${BASE_URI}/UpdateService/FirmwareInventory -k | python -m json.tool
    Append To File    ${LOG}  ${output}${\n}
    Should Contain    ${output}   Collection of Firmware


*** Keywords ***
