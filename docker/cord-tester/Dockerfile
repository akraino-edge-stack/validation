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
FROM ubuntu:xenial

# Sonobuoy supports Kubernetes versions 1.11, 1.12 and 1.13
ARG KUBE_VERSION=1.13.0-00

# Install required packages
RUN apt update
RUN apt install -y \
    lsb-release python python-pip python-dev \
    software-properties-common apt-transport-https \
    curl git make gcc

# Install kubectl
RUN curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -
RUN echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" > /etc/apt/sources.list.d/kubernetes.list
RUN apt update
RUN apt install -y kubectl=$KUBE_VERSION
RUN apt-mark hold kubectl

# Dependency for kafka pip package; needed for aarch64 image
RUN apt install -y libffi-dev libssl-dev libpq-dev
RUN curl -L https://github.com/edenhill/librdkafka/archive/v0.11.6.tar.gz | tar xzf - \
    && cd librdkafka-0.11.6/ \
    && ./configure --prefix=/usr \
    && make -j \
    && make install \
    && cd .. \
    && rm -rf librdkafka-0.11.6/

RUN pip install virtualenv
RUN apt autoremove -y
