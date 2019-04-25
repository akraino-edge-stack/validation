*** Variables ***
# Manual test: robot -v HOST:host_ip -v USERNAME:root -v PASSWORD:root_passwd ltp.robot

${HOST}        localhost    # cluster's master node address
${USERNAME}    root         # root user to log in to the host
${PASSWORD}    root_passwd  # root passwd to log in to the host
${LOG}         /opt/akraino/result/os/print_ltp.txt
