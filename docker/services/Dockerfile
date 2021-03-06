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
FROM python:3.10.0b1-alpine3.13 as build

# Install dependencies
COPY pip-requirements.txt /wheels/requirements/pip-requirements.txt

RUN apk --no-cache add --update \
        build-base \
        cairo \
        cairo-dev \
        cargo \
        curl \
        freetype-dev \
        gcc \
        gdk-pixbuf-dev \
        gettext \
        git \
        jpeg-dev \
        lcms2-dev \
        libffi-dev \
        musl-dev \
        openjpeg-dev \
        openssl-dev \
        pango-dev \
        poppler-utils \
        postgresql-client \
        postgresql-dev \
        py-cffi \
        python3-dev \
        rust \
        tcl-dev \
        tiff-dev \
        tk-dev \
        zlib-dev

# Build binaries
WORKDIR /wheels
RUN pip3 install wheel
RUN pip3 wheel -r /wheels/requirements/pip-requirements.txt
RUN git clone https://gerrit.akraino.org/r/validation /opt/akraino/validation

# Copy binaries in the final contaier and install robot framework
FROM python:3.10.0b1-alpine3.13
COPY --from=build /wheels /wheels
COPY --from=build /opt/akraino/validation /opt/akraino/validation

RUN apk --no-cache add --update \
        build-base \
        cairo \
        cairo-dev \
        cargo \
        curl \
        freetype-dev \
        gcc \
        gdk-pixbuf-dev \
        gettext \
        git \
        jpeg-dev \
        lcms2-dev \
        libffi-dev \
        musl-dev \
        openjpeg-dev \
        openssl-dev \
        pango-dev \
        poppler-utils \
        postgresql-client \
        postgresql-dev \
        py-cffi \
        python3-dev \
        rust \
        tcl-dev \
        tiff-dev \
        tk-dev \
        zlib-dev

RUN pip3 install -r /wheels/requirements/pip-requirements.txt \
                 -f /wheels && \
     rm -rf /wheels && \
     rm -rf /root/.cache/pip/*

# Install blueval dependencies
RUN pip install -r /opt/akraino/validation/bluval/requirements.txt

# Install curl
RUN apk --no-cache add --update \
        curl
