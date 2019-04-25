*** Variables ***
# Manual test: robot baremetal.robot

${USERNAME}    root    # root user
${PASSWORD}    passwd  # root passwd
${BASE_URI}    https://<idrac_ip>/redfish/v1 # idrac ip
${HOST_MR}     master__ip  # master host ip from cluster
${HOST_WR1}    worker1_ip  # worker host ip from cluster
${HOST_WR2}    worker2_ip  # worker host ip from cluster
${LOG}         /opt/akraino/validation/baremetal/print_baremetal.txt
