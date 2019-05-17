#!/bin/bash
#
# Copyright (c) 2019 AT&T Intellectual Property.  All other rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

export DROOT=/var/lib
export PW=abc123
CONTAINER_NAME="validation_postgresql"
POSTGRES_HOST_PORT=6432
REGISTRY=akraino
NAME=validation
TAG_PRE=`echo "${PWD##*/}"`
TAG_VER=latest
# get the architecture of the host
HOST_ARCH=amd64

if [ `uname -m` = "aarch64" ]; then
    HOST_ARCH=arm64
fi
if [ ! -z "$1" ]
  then
    REGISTRY=$1
fi
if [ ! -z "$2" ]
  then
    NAME=$2
fi
if [ ! -z "$3" ]
  then
    TAG_VER=$3
fi

IMAGE="$REGISTRY"/"$NAME":"$TAG_PRE"-"$HOST_ARCH"-"$TAG_VER"
docker run --detach --name $CONTAINER_NAME --restart=always --publish $POSTGRES_HOST_PORT:5432 --volume $DROOT/postgres:/var/lib/postgresql/data --env POSTGRES_USER=admin --env POSTGRES_PASSWORD="$PW" $IMAGE
sleep 10
docker exec $CONTAINER_NAME /bin/bash -c "psql -h localhost -p 5432 -U admin -f /akraino-blueprint_validation_db.sql"