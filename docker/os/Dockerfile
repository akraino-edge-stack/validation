##############################################################################
# Copyright (c) 2019 AT&T, ENEA AB, Nokia and others                         #
#                                                                            #
# Licensed under the Apache License, Version 2.0 (the "License");            #
# you maynot use this file except in compliance with the License.            #
#                                                                            #
# You may obtain a copy of the License at                                    #
#       http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                            #
# Unless required by applicable law or agreed to in writing, software        #
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  #
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.           #
# See the License for the specific language governing permissions and        #
# limitations under the License.                                             #
##############################################################################

# ref: https://docs.docker.com/develop/develop-images/dockerfile_best-practices/#use-multi-stage-builds
FROM ubuntu as build

# Install dependencies
COPY pip-requirements.txt /wheels/requirements/pip-requirements.txt
RUN apt-get update && apt-get -y install \
    gcc \
    git \
    make \
    wget \
    linux-headers-generic \
    build-essential \
    libssl-dev \
    libffi-dev \
    python-dev \
    python3-pip \
    automake \
    autoconf \
    pkgconf

# Build binaries
WORKDIR /wheels
RUN pip3 install wheel
RUN pip3 wheel -r /wheels/requirements/pip-requirements.txt
RUN git clone https://gerrit.akraino.org/r/validation /opt/akraino/validation

# Build ltp tests
RUN mkdir -p /opt/akraino /root/src
WORKDIR  /root/src
RUN git clone https://github.com/linux-test-project/ltp.git
WORKDIR /root/src/ltp
RUN make autotools && \
    ./configure --prefix=/opt/ltp && \
    make -j $(getconf _NPROCESSORS_ONLN) 2>&1 | tee ../build-log.txt && \
    make install 2>&1 | tee ../install-log.txt
RUN tar czvf /opt/akraino/ltp.tar.gz /opt/ltp
WORKDIR /root/src
RUN git clone https://github.com/CISOfy/lynis && tar czvf /opt/akraino/lynis-remote.tar.gz ./lynis

# Fetches vuls databases (invalidate cache using unique timestamp)
SHELL ["/bin/bash", "-c"]
RUN if [ $(uname -m) == 'aarch64' ]; then HOST_ARCH=arm64; else HOST_ARCH=amd64; fi && \
    wget https://dl.google.com/go/go1.14.linux-$HOST_ARCH.tar.gz -P /root/ --progress=dot:giga && \
    cd /root/ && \
    tar -xzf go1.14.linux-$HOST_ARCH.tar.gz -C /root/ && \
    rm go1.14.linux-$HOST_ARCH.tar.gz && \
    export GOROOT=/root/go && \
    export GOPATH=/root/go/src/github.com && \
    export PATH=$PATH:/root/go/bin:/root/go/src/github.com/bin && \
    mkdir -p /root/go/src/github.com/future-architect && \
    cd /root/go/src/github.com/future-architect && \
    git clone https://github.com/future-architect/vuls -b v0.10.0 && \
    cd vuls && \
    make install && \
    mkdir -p /root/go/src/github.com/kotakanbe && \
    git -C /root/go/src/github.com/kotakanbe clone https://github.com/kotakanbe/go-cve-dictionary.git -b v0.5.0 && \
    cd /root/go/src/github.com/kotakanbe/go-cve-dictionary/ && \
    make install && \
    git -C /root/go/src/github.com/kotakanbe clone https://github.com/kotakanbe/goval-dictionary.git -b v0.2.8 && \
    cd /root/go/src/github.com/kotakanbe/goval-dictionary && \
    make install && \
    mkdir -p /root/go/src/github.com/knqyf263 && \
    git -C /root/go/src/github.com/knqyf263 clone https://github.com/knqyf263/gost.git -b v0.1.3 && \
    cd /root/go/src/github.com/knqyf263/gost && \
    make install

ADD db.tar.gz /opt/akraino/validation/tests/os/vuls/
ADD db.tar.gz.timestamp /root/
RUN \
    export GOROOT=/root/go && \
    export GOPATH=/root/go/src/github.com && \
    export PATH=$PATH:/root/go/bin:/root/go/src/github.com/bin && \
    for i in $(seq 2002 "$(date +"%Y")"); do go-cve-dictionary fetchnvd -quiet -http-proxy=${HTTP_PROXY} -dbpath /opt/akraino/validation/tests/os/vuls/cve.sqlite3 -years "$i"; done && \
    goval-dictionary fetch-ubuntu -http-proxy=${HTTP_PROXY} -dbpath=/opt/akraino/validation/tests/os/vuls/oval_ubuntu_16.sqlite3 16 && \
    goval-dictionary fetch-ubuntu -http-proxy=${HTTP_PROXY} -dbpath=/opt/akraino/validation/tests/os/vuls/oval_ubuntu_18.sqlite3 18 && \
    goval-dictionary fetch-redhat -http-proxy=${HTTP_PROXY} -dbpath=/opt/akraino/validation/tests/os/vuls/oval_centos_7.sqlite3 7 && \
    goval-dictionary fetch-redhat -http-proxy=${HTTP_PROXY} -dbpath=/opt/akraino/validation/tests/os/vuls/oval_centos_8.sqlite3 8 && \
    gost fetch redhat --http-proxy=${HTTP_PROXY} --dbpath=/opt/akraino/validation/tests/os/vuls/gost_centos.sqlite3 --threads=$(nproc) && \
    cd /opt/akraino/validation/tests/os/vuls && \
    tar cvzf db.tar.gz *.sqlite3 && \
    rm *.sqlite3

# Copy binaries into the final container and install robot framework, bluval dependencies
FROM ubuntu
COPY --from=build /wheels /wheels
COPY --from=build /opt/akraino/validation /opt/akraino/validation
COPY --from=build /opt/akraino/ltp.tar.gz /opt/akraino/ltp.tar.gz
COPY --from=build /opt/akraino/lynis-remote.tar.gz /opt/akraino/lynis-remote.tar.gz
COPY --from=build /root/go/bin /root/go/bin
COPY --from=build /root/go/src/github.com/bin /root/go/src/github.com/bin

RUN apt-get update && apt-get -y install \
    openssh-client \
    sshpass \
    python3-pip python3.6 && \
    cd /usr/bin && ln -s python3 python && \
    pip3 install -r /wheels/requirements/pip-requirements.txt \
                 -f /wheels && \
    pip3 install -r /opt/akraino/validation/bluval/requirements.txt && \
    rm -rf /wheels && \
    rm -rf /root/.cache/pip/* && \
    rm -rf /var/cache/apt/* && \
    rm -rf /var/lib/apt/lists/*

ENV LC_ALL=C.UTF-8
ENV LANG=C.UTF-8
