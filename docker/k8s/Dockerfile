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
FROM golang:1.16.5-alpine3.13 as build

# Sonobuoy supports 3 Kubernetes minor versions: the current release and 2
# minor versions before.
ARG SB_TAG=v0.18.2
# Determine the latest stable git tag at build time based on stable major version
ARG K8S_VER=1.18

# Install dependencies
# COPY pip-requirements.txt /wheels/requirements/pip-requirements.txt
COPY validation/docker/k8s/pip-requirements.txt /wheels/requirements/pip-requirements.txt

RUN apk --no-cache add --update \
        bash \
        build-base \
        cairo \
        cairo-dev \
        cargo \
        curl \
        findutils \
        freetype-dev \
        gcc \
        gdk-pixbuf-dev \
        gettext \
        git \
        grep \
        jpeg-dev \
        lcms2-dev \
        libc-dev \
        libffi-dev \
        make \
        musl-dev \
        openjpeg-dev \
        openssl-dev \
        pango-dev \
        poppler-utils \
        postgresql-client \
        postgresql-dev \
        py-cffi \
        python3-dev \
        py3-pip \
        rsync \
        rust \
        tcl-dev \
        tiff-dev \
        tk-dev \
        zlib-dev

# Build binaries; detect the architecture automatically (default is amd64)
RUN git clone https://github.com/kubernetes/kubernetes /src/k8s.io/kubernetes
RUN if [ $(uname -m) == 'aarch64' ]; then HOST_ARCH=arm64; else HOST_ARCH=amd64; fi && \
    echo "Building docker on $HOST_ARCH" && \
    cd /src/k8s.io/kubernetes && \
    git checkout $(curl \
        https://storage.googleapis.com/kubernetes-release/release/stable-$K8S_VER.txt) && \
    make kubectl ginkgo && \
    make WHAT=test/e2e/e2e.test ARCH=$HOST_ARCH
RUN git clone https://github.com/heptio/sonobuoy /go/src/github.com/heptio/sonobuoy && \
    cd /go/src/github.com/heptio/sonobuoy && \
    git checkout $SB_TAG && \
    go install
#RUN git clone https://gerrit.akraino.org/r/validation /opt/akraino/validation
COPY validation /opt/akraino/validation
RUN cat /opt/akraino/validation/bluval/requirements.txt >> \
    /wheels/requirements/pip-requirements.txt

WORKDIR /wheels
RUN pip3 install wheel
RUN pip3 wheel -r /wheels/requirements/pip-requirements.txt

# Copy binaries in the final container and install robot framework
FROM python:3.10.0b1-alpine3.13
COPY --from=build /src/k8s.io/kubernetes/_output/bin /usr/local/bin
COPY --from=build /go/bin/sonobuoy /bin/sonobuoy
COPY --from=build /wheels /wheels
COPY --from=build /opt/akraino/validation/ /opt/akraino/validation/

RUN apk --no-cache add --update \
        bash \
        build-base \
        cairo \
        cairo-dev \
        cargo \
        curl \
        findutils \
        freetype-dev \
        gcc \
        gdk-pixbuf-dev \
        gettext \
        git \
        grep \
        jpeg-dev \
        lcms2-dev \
        libc-dev \
        libffi-dev \
        make \
        musl-dev \
        openjpeg-dev \
        openssl-dev \
        pango-dev \
        poppler-utils \
        postgresql-client \
        postgresql-dev \
        py-cffi \
        python3-dev \
        py3-pip \
        rsync \
        rust \
        tcl-dev \
        tiff-dev \
        tk-dev \
        zlib-dev

RUN pip3 install -r /wheels/requirements/pip-requirements.txt \
                 -f /wheels && \
     rm -rf /wheels && \
     rm -rf /root/.cache/pip/*
