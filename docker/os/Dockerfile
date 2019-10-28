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
RUN sudo apt-get install build-essential
RUN sudo apt-get install autoconf automake autotools-dev m4
RUN sudo apt-get install git
RUN sudo apt-get install linux-headers
RUN sudo apt-get install libaio-dev libattr1-dev libcap-dev
RUN mkdir -p ~/src/ltp
WORKDIR ~/src/ltp
RUN git clone https://github.com/linux-test-project/ltp.git ltp_git
WORKDIR ltp_git
RUN make autotools
RUN ./configure --prefix=/opt/akraino/ltp 
RUN make -j (getconf _NPROCESSORS_ONLN) 2>&1 | tee ../build-log.txt
RUN sudo make install 2>&1 | tee ../install-log.txt

# Copy binaries in the final contaier and install robot framework
FROM python:3.6-alpine3.9
COPY --from=build /wheels /wheels
COPY --from=build /opt/akraino/validation /opt/akraino/validation

RUN pip3 install -r /wheels/requirements/pip-requirements.txt \
                 -f /wheels && \
     rm -rf /wheels && \
     rm -rf /root/.cache/pip/*

# Install blueval dependencies
RUN pip install -r /opt/akraino/validation/bluval/requirements.txt

