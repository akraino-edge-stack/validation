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
Library           SSHLibrary
Library           OperatingSystem
Library           BuiltIn
Library           Process

*** Variables ***
${LOG_PATH}       /opt/akraino/validation/tests/os/vuls

*** Test Cases ***
Run vuls test
    Run  docker build -t vuls/vuls:v2 /opt/akraino/validation/tests/os/vuls

    Run  docker images |grep v2 | awk 'NR==1 {print $3}' | xargs -i docker run -v /var/lib/opnfv/mcp.rsa:/root/.ssh/mcp.rsa -v /tmp/:/vuls/results/ -v /tmp/gost-log:/var/log/vuls -v /etc/localtime:/etc/localtime:ro -v /etc/timezone:/etc/timezone:ro --entrypoint /bin/sh {} -c "chmod +x /vuls/environment.sh && /vuls/environment.sh"

Download Logs
   Run  docker ps --all |grep bin/sh | awk 'NR==1 {print $1}' | xargs -i docker cp {}:/vuls/vuls.log $LOG_PATH/vuls.log

