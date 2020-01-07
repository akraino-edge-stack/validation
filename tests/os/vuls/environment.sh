#!/bin/sh

# Install depend
apk update
apk add make gcc libc-dev sqlite

# Set ssh configuration
touch /root/.ssh/config
echo "Host *" > ~/.ssh/config
echo "StrictHostKeyChecking no" >> ~/.ssh/config

# Run vuls test
cd /vuls || exit
vuls scan -config /vuls/config.toml -ssh-config
touch vuls.log
vuls report | tee -a /vuls/vuls.log

