*** Settings ***
Documentation     Check HW details
Library           SSHLibrary    60 seconds
Suite Setup       Open Connection And Log In
Suite Teardown    Close All Connections

*** Variables ***
${HOST}           172.28.14.109
${USERNAME}       root
${PASSWORD}       smil3yfc
${LOG}            /opt/akraino/validation/bios_version/output_bios_hw.txt

*** Test Cases ***
Get Processes
    [Documentation]     Command printouts the HW details
    ${output}=    Execute Command   dmidecode | grep -A3 '^System Information' > ${LOG}
    ${output}=    Execute Command   dmidecode | more | grep 'BIOS Revision' >> ${LOG}
    ${outout}=    Execute Command   lscpu >> ${LOG}
    ${output}=    Execute Command   lsblk >> ${LOG}


*** Keywords ***
Open Connection And Log In
  Open Connection       ${HOST}
  Login                 ${USERNAME}     ${PASSWORD}

