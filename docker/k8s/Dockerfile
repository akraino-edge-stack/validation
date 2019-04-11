FROM golang:alpine3.9

ARG HOST_ARCH=amd64
ARG K8S_TAG=master

COPY pip-requirements.txt /tmp/

RUN apk --no-cache add --update \
        openssl \
        python3 && \
    apk --no-cache add --virtual .build-deps --update \
        bash \
        findutils \
        gcc \
        git \
        grep \
        libc-dev \
        libffi \
        libffi-dev \
        make \
        openssl-dev \
        python3-dev \
        py3-pip \
        rsync && \
     (git clone https://github.com/kubernetes/kubernetes /src/k8s.io/kubernetes && \
     cd /src/k8s.io/kubernetes && \
     git checkout $K8S_TAG && \
     make kubectl ginkgo && \
     make WHAT=test/e2e/e2e.test ARCH=$HOST_ARCH && \
     mv _output/bin/* /usr/local/bin && \
     rm -rf /src/k8s.io) && \
     (go get -u -v github.com/heptio/sonobuoy) && \
     (pip3 install -r /tmp/pip-requirements.txt) && \
      apk del .build-deps
