*** Variables ***
# Manual test: robot  -v HOST:host_ip ceph_ha_mon_mgr_osd.robot

${HOST}           localhost   # cluster's master node address
${USERNAME}       localadmin  # user to log in to the host
${NODENAME}       targetnode  # cluster's master node base name
${SELECTOR}       --field-selector=spec.nodeName    # selector
${CEPHMON}        "application=ceph,component=mon"  # ceph app for monitor
${CEPHMGR}        "application=ceph,component=mgr"  # ceph app for manager
${CEPHOSD}        "application=ceph,component=osd"  # ceph app for osd
${LOG}            /opt/akraino/results/ceph_ha/print_ceph_mgr_mon_osd.txt
