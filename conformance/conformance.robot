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
Documentation     Run K8s Conformance Test
Library           OperatingSystem
Library           BuiltIn

*** Variables ***
${LOG}            /opt/akraino/results/conformance/print_conformance.txt

*** Test Cases ***
Check prerequsites
        [Documentation]         Check that the tools needed are present and that
        ...                     the k8s cluster is reachable
        # Log the version of robot framework
        ${rc}  ${output}=       Run And Return Rc And Output     robot --version
        Append To File          ${LOG}  ${output}${\n}
        Should Contain          ${output}                        Robot Framework
        # Check that the config file is mounted in the container
        File Should Not Be Empty  /root/.kube/config
        # Make sure the pod is reachable with the local k8s client
        ${rc}  ${output}=       Run And Return Rc And Output
                                ...            kubectl get pods --all-namespaces
        Append To File          ${LOG}  ${output}${\n}
        Should Contain          ${output}      kube-system

Start Sonobuoy Conformance Test
        [Documentation]         Start the test and check it is running
        Run                     kubectl apply -f /opt/akraino/repo/conformance/sonobuoy.yaml
        Sleep                   5s
        ${rc}  ${output}=       Run And Return Rc And Output
                                ...  kubectl describe pod/sonobuoy -n heptio-sonobuoy
        Append To File          ${LOG}  ${output}${\n}
        ${rc}  ${output}=       Run And Return Rc And Output     sonobuoy status
        Append To File          ${LOG}  ${output}${\n}
        Sleep                   3s
        Should Contain          ${output}              Sonobuoy is still running

Verify Conformance Test Result
        [Documentation]         Test will take about 1hr and 40 mins to complete.
        ...                     Wait for the test to finish and check the number
        ...                     of failed tests
        Run                     until sonobuoy status | grep "Sonobuoy has completed"; do sleep 120; done
        Sleep                   15s
        ${rc}  ${output}=       Run And Return Rc And Output
                                ...  results=$(sonobuoy retrieve) && sonobuoy e2e $results
        Append To File          ${LOG}  ${output}${\n}
        Sleep                   3s
        Should Contain          ${output}                        failed tests: 0


Cleanup Sonobuoy Conformance Test
        [Documentation]         Remove all sonobuoy containers
        ${rc}  ${output}=       Run And Return Rc And Output
                                ...  kubectl delete -f /opt/akraino/repo/conformance/sonobuoy.yaml
        Append To File          ${LOG}  ${output}${\n}
        Sleep                   3s
        Should Contain          ${output}      service "sonobuoy-master" deleted
