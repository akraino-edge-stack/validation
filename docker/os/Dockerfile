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
FROM python:3.6-alpine3.9 as build

# Install dependencies
COPY pip-requirements.txt /wheels/requirements/pip-requirements.txt
RUN apk --no-cache add --update \
        gcc \
        git \
        libc-dev \
        libffi \
        libffi-dev \
        make \
        openssl-dev

# Build binaries
WORKDIR /wheels
RUN pip3 install wheel
RUN pip3 wheel -r /wheels/requirements/pip-requirements.txt
RUN git clone https://gerrit.akraino.org/r/validation /opt/akraino/validation

#Build ltp tests
FROM ubuntu as build_ubuntu
RUN linux_headers=$(uname -r) && \
    if [ $(uname -m) == 'aarch64' ]; then linux_headers='generic'; fi && \
    apt-get update && apt-get -y install \
    build-essential \
    autoconf automake autotools-dev m4 \
    git \ 
    linux-headers-$linux_headers \
    libaio-dev libattr1-dev libcap-dev 
RUN mkdir -p /opt/akraino
RUN mkdir -p /root/src
WORkDIR  /root/src
RUN git clone https://github.com/linux-test-project/ltp.git
WORKDIR /root/src/ltp
RUN pwd && make autotools && \
    ./configure --prefix=/opt/ltp && \ 
    make -j $(getconf _NPROCESSORS_ONLN) 2>&1 | tee ../build-log.txt && \
    make install 2>&1 | tee ../install-log.txt
RUN tar -czvf ltp.tar.gz /opt/ltp && mv ltp.tar.gz /opt/akraino

# Copy binaries in the final contaier and install robot framewor
FROM python:3.6-alpine3.9
COPY --from=build /wheels /wheels
COPY --from=build /opt/akraino/validation /opt/akraino/validation
COPY --from=build_ubuntu /opt/akraino/ltp.tar.gz /opt/akraino/ltp.tar.gz

RUN pip3 install -r /wheels/requirements/pip-requirements.txt \
                 -f /wheels && \
     rm -rf /wheels && \
     rm -rf /root/.cache/pip/*

# Install blueval dependencies
RUN pip install -r /opt/akraino/validation/bluval/requirements.txt
