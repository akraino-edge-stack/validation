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

*** Variables ***
# Manual test: robot dell_baremetal.robot

${BASE_URI}    https://192.168.XX.XX/redfish/v1/    # Redfish api link address
${IPMIUSER}    ipmi_user                       # ipmi user
${IPMIPWRD}    ipmi_passwd                     # ipmi password
${HOST_MR}     master host                     # cluster's master host address
${HOST_WR1}    worker1 host                    # cluster's worker host address
${HOST_WR2}    worker2 host                    # cluster's worker host address
${MACADDR}     MACAddress                      # Host MAC Address
${CHASSIS}     SerialNumber                    # IDRAC address
${IDRACIP}     CurrentIPv4.1.Address           # Current idrac IP
${BOOTSEQ}     Uefi Boot Sequence              # Boot Settings
${BIOSVER}     SystemBiosVersion               # Bios version
${FIRMWARE}    Collection of Firmware          # Firmaware
