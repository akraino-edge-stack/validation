*** Variables ***
# Manual test: robot -v HOST:host_ip -v USERNAME:root -v PASSWORD:root_passwd ltp.robot

${HOST}        localhost    # cluster's master node address
${USERNAME}    root         # root credentials to log in to the host
${PASSWORD}    root_passwd  # password  to log in to the host
${LOG}         /opt/akraino/results/os/print_ltp.txt
