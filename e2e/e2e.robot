##############################################################################
# Copyright (c) 2019 AT&T Intellectual Property.                             #
# Copyright (c) 2019 Nokia.                                                  #
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
Documentation      End-to-end (e2e) tests for Kubernetes provide a mechanism
...                to test end-to-end behavior of the system, and is the last
...                signal to ensure end user operations match developer
...                specifications.
Library            Process
Library            String


*** Variables ***
${KUBECONFIG}      /root/.kube/config_k3s
${REPORTDIR}       ${LOG_PATH}${/}${SUITE_NAME.replace(' ','_')}


*** Test Cases ***
Run Secrets Tests
    ${output}=     Run Process  e2e.test
    ...                         -ginkgo.focus  Secrets
    ...                         -minStartupPods  3
    ...                         -ginkgo.noColor
    ...                         -provider  local
    ...                         -kubeconfig  ${KUBECONFIG}
    ...                         -report-dir  ${REPORTDIR}
    ...                         -report-prefix  secrets_
    ${result}=     Get Line  ${output.stdout}  -1
    Should Be Equal As Strings  ${result}  PASS
