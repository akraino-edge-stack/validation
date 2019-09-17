##############################################################################
# Copyright (c) 2019 AT&T Intellectual Property.                             #
# Copyright (c) 2019 Nokia.                                                  #
# Copyright (c) 2019 Enea AB
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
Documentation     Run k8s conformance test using sonobuoy
Library           OperatingSystem
Library           BuiltIn
Library           Collections
Library           SSHLibrary
Library           Process
Test Setup        Run Keywords
...               Check that k8s cluster is reachable
...               Onboard Images
...               Update Resource Config
Test Teardown     Run Keywords
...               Cleanup Sonobuoy
...               Remove Images
...               Close All Connections

*** Variables ***
${LOG}            ${LOG_PATH}${/}${SUITE_NAME.replace(' ','_')}.log

&{SONOBUOY}         path=gcr.io/heptio-images
...                 name=sonobuoy:v0.15.1
...                 var=sonobuoy-img
&{SONOBUOY_LATEST}  path=gcr.io/heptio-images
...                 name=sonobuoy:latest
...                 var=sonobuoy-latest-img
&{E2E}              path=akraino
...                 name=validation:kube-conformance-v1.15
...                 var=e2e-img
&{SYSTEMD_LOGS}     path=akraino
...                 name=validation:sonobuoy-plugin-systemd-logs-latest
...                 var=systemd-logs-img
@{IMGS}           &{SONOBUOY}  &{SONOBUOY_LATEST}  &{E2E}  &{SYSTEMD_LOGS}

*** Test Cases ***
Run Sonobuoy Conformance Test
        # Start the test
        Run                     kubectl apply -f ${CURDIR}${/}sonobuoy.yaml
        Sleep                   10s
        ${rc}  ${output}=       Run And Return Rc And Output
                                ...  kubectl describe pod/sonobuoy -n heptio-sonobuoy
        Append To File          ${LOG}  ${output}${\n}

        # Wait until the test finishes execution
        Run                     while sonobuoy status | grep "Sonobuoy is still running"; do sleep 180; done
        Append To File          ${LOG}  "Sonobuoy has completed"${\n}

        # Get the result and store the sonobuoy logs
        ${rc}  ${output}=       Run And Return Rc And Output
                                ...  results=$(sonobuoy retrieve ${LOG_PATH}) && sonobuoy e2e $results
        Append To File          ${LOG}  ${output}${\n}
        Should Contain          ${output}       failed tests: 0

*** Keywords ***
Check that k8s cluster is reachable
        # Check that the config file is mounted in the container
        File Should Not Be Empty  /root/.kube/config

        # Make sure the pod is reachable with the local k8s client
        ${rc}  ${output}=       Run And Return Rc And Output
                                ...  kubectl get pods --all-namespaces
        Append To File          ${LOG}  ${output}${\n}
        Should Contain          ${output}      kube-system

Cleanup Sonobuoy
        ${rc}  ${output}=       Run And Return Rc And Output
                                ...  kubectl delete -f ${CURDIR}${/}sonobuoy.yaml
        Append To File          ${LOG}  ${output}${\n}
        Sleep                   3s
        Should Contain          ${output}      service "sonobuoy-master" deleted

Open Connection And Log In
        Open Connection         ${HOST}
        Login With Public Key   ${USERNAME}  ${SSH_KEYFILE}

Onboard Images
        ${INT_REG}=             Get Variable Value  ${INTERNAL_REGISTRY}  ${EMPTY}
        Set Test Variable       ${INT_REG}
        Return From Keyword If  $INT_REG == '${EMPTY}'
        Open Connection And Log In
        FOR  ${img}  IN  @{IMGS}
            ${rc}=  Execute Command
            ...     docker pull ${img.path}/${img.name}
            ...       return_stdout=False  return_rc=True
            Should Be Equal As Integers  ${rc}  0
            ${rc}=  Execute Command
            ...     docker tag ${img.path}/${img.name} ${INT_REG}/bluval/${img.name}
            ...       return_stdout=False  return_rc=True
            Should Be Equal As Integers  ${rc}  0
            ${rc}=  Execute Command
            ...     docker push ${INT_REG}/bluval/${img.name}
            ...       return_stdout=False  return_rc=True
            Should Be Equal As Integers  ${rc}  0
            Set To Dictionary  ${img}  path=${INT_REG}/bluval
        END

Remove Images
        Return From Keyword If  $INT_REG == '${EMPTY}'
        FOR  ${img}  IN  @{IMGS}
            Execute Command  docker rmi ${img.path}/${img.name}
        END

Update Resource Config
        FOR  ${img}  IN  @{IMGS}
            Run Process  sed  -i  s|{{ ${img.var} }}|${img.path}/${img.name}|g
            ...              /opt/akraino/validation/tests/k8s/conformance/sonobuoy.yaml
        END
