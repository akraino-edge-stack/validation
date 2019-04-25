*** Variables ***
# Manual test: robot baremetal.robot

${LOG}         /opt/akraino/results/baremetal/print_baremetal.txt
${OOB_IP}      192.168.XX.XX                   # OOB IP
${BASE_URI}    https://${OOB_IP}/redfish/v1    # Redfish api link address
${USERNAME}    ipmi_user                       # root user
${PASSWORD}    ipmi_passwd                     # root passwd
${MTOOL_PY}    -k | python -m json.tool        # json tool for better readable output
${HOST_MR}     master host                     # cluster's master host address
${HOST_WR1}    worker1 host                    # cluster's worker host address
${HOST_WR2}    worker2 host                    # cluster's worker host address
${MACADDR}     MACAddress                      # Host MAC Address
${CHASSIS}     SerialNumber                    # IDRAC address
${IDRACIP}     CurrentIPv4.1.Address           # Current idrac IP
${BOOTSEQ}     Uefi Boot Sequence              # Boot Settings
${BIOSVER}     SystemBiosVersion               # Bios version
${FIRMWARE}    Collection of Firmware          # Firmaware
