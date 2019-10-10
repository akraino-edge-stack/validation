##############################################################################
# Copyright (c) 2019 AT&T Intellectual Property.                             #
#                                                                            #
# Licensed under the Apache License, Version 2.0 (the "License"); you may    #
# not use this file except in compliance with the License.                   #
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
Library             SSHLibrary
Resource            ../../resources/common.robot
Suite Setup       common.Open Connection And Log In
Suite Teardown    Close All Connections

*** Variables ***


*** Keywords ***
Testing Prometheus logs
  ${command} =  set variable  kubectl get pods -n kube-system --field-selector=status.phase==Running | grep prometheus- | awk '{print $1}'
  ${result} =  Execute Command    ${command}
  ${prometheus_pod_name} =  set variable  ${result}
  Log  prometheus_pod_name = ${prometheus_pod_name}
  ${command} =  set variable  kubectl logs -n kube-system ${prometheus_pod_name} | grep 'WAL checkpoint complete'
  ${result} =  Execute Command  ${command}
  Should Contain  ${result}  WAL checkpoint complete


*** Test Cases ***
Verify Prometheus logs
  Testing Prometheus logs