*** Settings ***
Documentation     Run K8s Conformance Test
Library           SSHLibrary
Suite Setup       Open Connection And Log In
Suite Teardown    Close All Connections

*** Variables ***
${HOST}           172.28.14.109
${USERNAME}       root
${PASSWORD}       smil3yfc
${LOG}            /opt/akraino/validation/conformance/heptio_result.txt


*** Test Cases ***
Get Processes
    [Documentation]      Execute pre-requisites for Sonoboy test
    ${output}=    Execute Command   cd ~ > ${LOG}
    ${outout}=    Execute Command   robot --version >> ${LOG}
    ${outout}=    Execute Command   hello >> ${LOG}
    ${output}=    Execute Command   kubectl get pods --all-namespaces -o wide >> ${LOG}
    ${output}=    Execute Command   cat /root/mm747b/sonobuoy/sonobuoy.yaml | kubectl apply -f - >> ${LOG}
    ${output}=    Execute Command   kubectl get pods --all-namespaces | grep heptio >> ${LOG}
    ${output}=    Execute Command   kubectl get pods --all-namespaces | grep heptio >> ${LOG}
    ${output}=    Execute Command   kubectl describe pod/sonobuoy -n heptio-sonobuoy >> ${LOG}
    # ${output}=    Execute Command   kubectl logs -f -n heptio-sonobuoy sonobuoy kube-sonobuoy >> ${LOG}


*** Keywords ***
Open Connection And Log In
  Open Connection       ${HOST}
  Login                 ${USERNAME}     ${PASSWORD}

