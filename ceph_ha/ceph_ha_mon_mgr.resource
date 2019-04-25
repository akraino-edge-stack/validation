*** Variables ***
# Manual test: robot  -v HOST:host_ip cyclic.robot

${HOST}           localhost   # host from cluster to perform the test
${USERNAME}       localadmin  # public user credentials
${NODENAME}       aknode      # worker or master node to delete the pods
${LOG}            /opt/akraino/validation/ceph_ha/print_ceph_mgr_mon.txt
