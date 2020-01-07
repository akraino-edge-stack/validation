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
    ${rc}  ${output} =  Run And Return Rc And Output  sed -i 's/HOST/${HOST}/g' config.toml
    Should Be Equal As Integers  ${rc}  0

    ${rc}  ${output} =  Run And Return Rc And Output  sed -i 's/USERNAME/${USERNAME}/g' config.toml
    Should Be Equal As Integers  ${rc}  0

    ${rc} =  Run And Return Rc  tar xvzf db.tar.gz -C /opt/akraino/validation/tests/os/vuls/
    Should Be Equal As Integers  ${rc}  0

    ${rc} =  Run And Return Rc  cp ${SSH_KEYFILE} /opt/akraino/validation/tests/os/vuls/
    Should Be Equal As Integers  ${rc}  0

    ${rc}  ${output} =  Run And Return Rc And Output  docker build -t vuls/vuls:v2 -f /opt/akraino/validation/tests/os/vuls/Dockerfile .
    Should Be Equal As Integers  ${rc}  0

    ${rc}  ${output} =  Run And Return Rc And Output  docker run -tid --entrypoint /bin/sh vuls/vuls:v2 -c /bin/sh
    Append To File  /tmp/images.txt  ${output}${\n}
    Should Not Be Empty  ${output}
    Should Be Equal As Integers  ${rc}  0
    ${cid} =     Set Variable  ${output}

    Should Be Equal As Integers  ${rc}  0
    ${rc} =  Run And Return Rc  docker exec ${cid} apk add make gcc libc-dev sqlite
    Should Be Equal As Integers  ${rc}  0

    ${rc}  ${output} =  Run And Return Rc And Output  docker exec ${cid} vuls scan -config /vuls/config.toml -ssh-config
    Should Be Equal As Integers  ${rc}  0

    ${rc}  ${output} =  Run And Return Rc And Output  docker exec ${cid} vuls report
    Append To File  /opt/akraino/validation/tests/os/vuls/vuls.log  ${output}${\n}
    Should Be Equal As Integers  ${rc}  0

    ${rc} =  Run And Return Rc  docker rm -vf ${cid}
    Should Be Equal As Integers  ${rc}  0
