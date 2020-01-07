#!/bin/sh

# Install depend
apk update
apk add make gcc libc-dev sqlite

# Install go
wget https://dl.google.com/go/go1.12.6.linux-amd64.tar.gz -P /root
cd /root/
tar -xzf go1.12.6.linux-amd64.tar.gz

# Set go path
echo "export GOROOOT=/root/go" >> /etc/profile
echo "export GOPATH=/root/go/src" >> /etc/profile
echo "export PATH=$PATH:/root/go/bin:/root/go/src/bin" >> /etc/profile
. /etc/profile
mkdir /lib64 && ln -s /lib/libc.musl-x86_64.so.1 /lib64/ld-linux-x86-64.so.2

# Add go-cve-dictionary
cd /root/go/src
mkdir -p /root/go/src/github.com/kotakanbe
git -C /root/go/src/github.com/kotakanbe clone https://github.com/kotakanbe/go-cve-dictionary.git
make -C /root/go/src/github.com/kotakanbe/go-cve-dictionary install
for i in `seq 2002 $(date +"%Y")`; do go-cve-dictionary fetchnvd -dbpath /vuls/cve.sqlite3 -years $i; done

# Add goval-dictionary
git -C /root/go/src/github.com/kotakanbe clone https://github.com/kotakanbe/goval-dictionary.git
make -C /root/go/src/github.com/kotakanbe/goval-dictionary install
goval-dictionary fetch-ubuntu -dbpath=/vuls/oval.sqlite3 18

# Set ssh configuration
touch /root/.ssh/config
echo "Host *" > ~/.ssh/config
echo "StrictHostKeyChecking no" >> ~/.ssh/config

# Run vuls test
cd /vuls
vuls scan -config /vuls/config.toml -ssh-config 
touch vuls.log 
vuls report | tee -a /vuls/vuls.log

