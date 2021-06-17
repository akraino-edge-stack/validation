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
Library           String
Library           SSHLibrary
Library           Process
Library           JSONLibrary
Test Setup        Run Keywords
...               Check that k8s cluster is reachable
...               Define Images
...               Onboard Images
...               Create Manifest File
Test Teardown     Run Keywords
...               Cleanup Sonobuoy
...               Close All Connections

*** Variables ***
${LOG}            ${LOG_PATH}${/}${SUITE_NAME.replace(' ','_')}.log

&{SONOBUOY}         path=sonobuoy
...                 name=sonobuoy:v0.18.2
&{E2E}              path=k8s.gcr.io
...                 name=Actual value set dynamically
&{SONOBUOY_IMGS}    sonobuoy=&{SONOBUOY}
...                 e2e=&{E2E}

# Following tests assume DNS domain is "cluster.local"
${DNS_DOMAIN_TESTS}  SEPARATOR=
...                 DNS should provide /etc/hosts entries for the cluster|
...                 DNS should provide DNS for services|
...                 DNS should provide DNS for ExternalName services|
...                 DNS should provide DNS for the cluster|
...                 DNS should provide DNS for pods for Subdomain|
...                 DNS should provide DNS for pods for Hostname

# Images listed by Sonobuoy but not available for downloading
@{SKIP_IMGS}        gcr.io/kubernetes-e2e-test-images/windows-nanoserver:v1
...                 gcr.io/authenticated-image-pulling/windows-nanoserver:v1
...                 gcr.io/authenticated-image-pulling/alpine:3.7
...                 k8s.gcr.io/invalid-image:invalid-tag
...                 invalid.com/invalid/alpine:3.1

*** Test Cases ***
Run Sonobuoy Conformance Test
        # Start the test
        Run                     kubectl apply -f ${CURDIR}${/}sonobuoy.yaml
        Sleep                   20s
        ${rc}  ${output}=       Run And Return Rc And Output
                                ...  kubectl describe pod/sonobuoy -n sonobuoy
        Append To File          ${LOG}  ${output}${\n}

        # Wait until the test finishes execution
        Wait Until Keyword Succeeds    3x    20 sec    Check that sonobuoy is running
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
                                ...  kubectl version
        Append To File          ${LOG}  ${output}${\n}
        Should Contain          ${output}      Server Version: version.Info

Check that sonobuoy is running
       ${output}=              Run    kubectl get pod sonobuoy --namespace sonobuoy
       Should Contain          ${output}     Running

Cleanup Sonobuoy
        ${rc}  ${output}=       Run And Return Rc And Output
                                ...  kubectl delete -f ${CURDIR}${/}sonobuoy.yaml
        Append To File          ${LOG}  ${output}${\n}
        Sleep                   3s
        Should Contain          ${output}      service "sonobuoy-master" deleted

Open Connection And Log In
        Open Connection         ${HOST}
        Login With Public Key   ${USERNAME}  ${SSH_KEYFILE}

Upload To Internal Registry
         [Arguments]            ${path}  ${name}
         ${rc}=  Execute Command
         ...     docker pull ${path}/${name}
         ...       return_stdout=False  return_rc=True
         Should Be Equal As Integers  ${rc}  0
         ${rc}=  Execute Command
         ...     docker tag ${path}/${name} ${INT_REG}/bluval/${name}
         ...       return_stdout=False  return_rc=True
         Should Be Equal As Integers  ${rc}  0
         ${rc}=  Execute Command
         ...     docker push ${INT_REG}/bluval/${name}
         ...       return_stdout=False  return_rc=True
         Should Be Equal As Integers  ${rc}  0

Onboard Sonobuoy Images
        FOR  ${img}  IN  @{SONOBUOY_IMGS}
            ${path}=            Get From Dictionary  ${SONOBUOY_IMGS['${img}']}  path
            ${name}=            Get From Dictionary  ${SONOBUOY_IMGS['${img}']}  name
            Upload To Internal Registry  ${path}  ${name}
            Set To Dictionary  ${SONOBUOY_IMGS['${img}']}  path=${INT_REG}/bluval
        END

Onboard Kubernetes e2e Test Images
        ${result}=              Run Process  sonobuoy  images
        Should Be Equal As Integers  ${result.rc}  0
        @{images}=              Split String  ${result.stdout}
        FOR  ${img}  IN  @{images}
            Continue For Loop If  $img in $SKIP_IMGS
            ${path}  ${name}  Split String From Right  ${img}  /  1
            Upload To Internal Registry  ${path}  ${name}
        END

Define Images
        ${result}=              Run Process  kubectl  version  -o  json
        Should Be Equal As Integers  ${result.rc}  0
        ${versions}=            Convert String To JSON  ${result.stdout}
        ${gitVersion}=          Get Value From Json  ${versions}  $.serverVersion.gitVersion
        Set To Dictionary       ${SONOBUOY_IMGS['e2e']}  name=conformance:${gitVersion[0]}

Onboard Images
        ${INT_REG}=             Get Variable Value  ${INTERNAL_REGISTRY}  ${EMPTY}
        Set Test Variable       ${INT_REG}
        Return From Keyword If  $INT_REG == '${EMPTY}'
        Open Connection And Log In
        Onboard Sonobuoy Images
        Onboard Kubernetes e2e Test Images

Get Tests To Skip
        ${flag}=                Set Variable  Aggregator|Alpha|\\[(Disruptive|Feature:[^\\]]+|Flaky)\\]
        ${flag}=                Run Keyword If  '${DNS_DOMAIN}' != 'cluster.local'
        ...                         Catenate  SEPARATOR=|  ${flag}  ${DNS_DOMAIN_TESTS}
        ...                     ELSE
        ...                         Set Variable  ${flag}
        [Return]                ${flag}

Create Manifest File
        ${skip}=                Get Tests To Skip
        @{flags}=               Set Variable
        ...                         --e2e-focus  \\[Conformance\\\]
        ...                         --e2e-skip  ${skip}
        ...                         --kube-conformance-image  ${SONOBUOY_IMGS.e2e.path}/${SONOBUOY_IMGS.e2e.name}
        ...                         --sonobuoy-image  ${SONOBUOY_IMGS.sonobuoy.path}/${SONOBUOY_IMGS.sonobuoy.name}
        ...                         --image-pull-policy  Always
        ...                         --timeout  14400
        Run Keyword If          $INT_REG != '${EMPTY}'  Run Keywords
        ...                     Append To List  ${flags}
        ...                         --e2e-repo-config  ${CURDIR}${/}custom_repos.yaml
        ...                     AND
        ...                     Run Process  sed  -i  s|{{ registry }}|${INT_REG}/bluval|g
        ...                         ${CURDIR}${/}custom_repos.yaml
        ${result}=              Run Process  sonobuoy  gen  @{flags}
        Should Be Equal As Integers  ${result.rc}  0
        Create File             ${CURDIR}${/}sonobuoy.yaml  ${result.stdout}
