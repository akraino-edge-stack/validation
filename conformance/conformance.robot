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
Library           Process
Library           BuiltIn

*** Variables ***
${LOG}            /opt/akraino/results/conformance/print_conformance.txt

*** Test Cases ***
Check that k8s cluster is reachable
        # Log the version of robot framework
        ${output}=              Run Process     robot --version
        Append To File          ${LOG}  ${output}${\n}
        Should Contain          ${output}       Robot Framework
        # Check that the config file is mounted in the container
        File Should Not Be Empty  /root/.kube/config
        # Make sure the pod is reachable with the local k8s client
        ${output}=              Run Process     kubectl get pods --all-namespaces
        Append To File          ${LOG}  ${output}${\n}
        Should Contain          ${output}      kube-system

Run Sonobuoy Conformance Test
        # Start the test and check it is running
        Run Process             kubectl apply -f /opt/akraino/repo/conformance/sonobuoy.yaml
        Sleep                   5s
        ${output}=              Run Process     kubectl describe pod/sonobuoy -n heptio-sonobuoy
        Append To File          ${LOG}  ${output}${\n}
        ${output}=              Run Process     sonobuoy status
        Append To File          ${LOG}  ${output}${\n}
        Sleep                   3s
        Should Contain          ${output}              Sonobuoy is still running
        # Wait until the test finishes execution
        Run Process             until sonobuoy status | grep "Sonobuoy has completed"; do sleep 120; done
        Sleep                   15s
        ${output}=              Run Process     results=$(sonobuoy retrieve) &&
                                ...             cp $results /opt/akraino/results/conformance/ &&
                                ...             sonobuoy e2e $results
        Append To File          ${LOG}  ${output}${\n}
        Sleep                   3s
        Should Contain          ${output}       failed tests: 0


Cleanup Sonobuoy
        ${output}=              Run Process    kubectl delete -f /opt/akraino/repo/conformance/sonobuoy.yaml
        Append To File          ${LOG}  ${output}${\n}
        Sleep                   3s
        Should Contain          ${output}      service "sonobuoy-master" deleted
