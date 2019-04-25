*** Variables ***
# Manual test: robot  -v HOST:host_ip ceph_ha_mon_mgr_osd.robot

${HOST}           localhost   # host from cluster to perform the test
${USERNAME}       localadmin  # public user credentials
${NODENAME}       targetnode  # worker or master node to purge ceph
${SELECTOR}       --field-selector=spec.nodeName    # selector
${CEPHMON}        "application=ceph,component=mon"  # ceph app for monitor
${CEPHMGR}        "application=ceph,component=mgr"  # ceph app for manager
${CEPHOSD}        "application=ceph,component=osd"  # ceph app for osd
${LOG}            /opt/akraino/result/ceph_ha/print_ceph_mgr_mon_osd.txt
