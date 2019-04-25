*** Variables ***
# Manual test: robot -v HOST:host_ip -v USERNAME:root -v PASSWORD:root_passwd ltp.robot

${HOST}           localhost    # host from cluster to perform the test
${USERNAME}       root         # root credentials
${PASSWORD}       root_passwd  # root password
${LOG}            /opt/akraino/validation/os/print_ltp.txt
