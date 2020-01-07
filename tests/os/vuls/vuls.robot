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
Run Vuls test
   Run  tar xvzf /opt/akraino/validation/tests/os/vuls/db.tar.gz

   ${image_id} =  Run  docker build -t vuls/vuls:v2 /opt/akraino/validation/tests/os/vuls

   Run  docker run -v ${SSH_KEYFILE}:/root/.ssh/id_rsa -v /tmp/:/vuls/results/ -v /tmp/gost-log:/var/log/vuls -v /etc/localtime:/etc/localtime:ro -v /etc/timezone:/etc/timezone:ro --entrypoint /bin/sh ${image_id} -c "chmod +x /vuls/environment.sh && /vuls/environment.sh"

   Run  docker ps --all |grep ${image_id} | awk 'NR==1 {print $1}' | xargs -i docker cp {}:/vuls/vuls.log /opt/akraino/validation/tests/os/vuls/vuls.log
