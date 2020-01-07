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
#Suite Setup       Open Connection And Log In
#Suite Teardown    Close All Connections

*** Variables ***
${LOG_PATH}       /opt/akraino/validation/tests/os/security
${LOG}            ${LOG_PATH}${/}
${USERNAME}       ubuntu
${HOST}           172.16.10.36
${SSH_KEYFILE}    /var/lib/opnfv/mcp.rsa

*** Test Cases ***

Install images


    Run  docker run -v ${SSH_KEYFILE}:/root/.ssh/mcp.rsa -v /tmp/:/vuls/results/ -v /tmp/gost-log:/var/log/vuls -v /etc/localtime:/etc/localtime:ro -v /etc/timezone:/etc/timezone:ro --entrypoint /bin/sh vuls/vuls -c "rm -rf /vuls/environment.sh && echo '$(cat /opt/akraino/validation/tests/os/security/environment.sh)' > /vuls/environment.sh && echo '$(sed -e 's|HOST|${HOST}|g' -e 's|USERNAME|${USERNAME}|g' /opt/akraino/validation/tests/os/security/config.toml)' > /vuls/config.toml && cat /vuls/config.toml && chmod +x /vuls/environment.sh && /vuls/environment.sh "

    Run     docker cp vuls/vuls:vuls.log /tmp/vuls.log
