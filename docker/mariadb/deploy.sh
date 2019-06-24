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

HOST_STORAGE_DIR="/var/lib/mariadb"
CONTAINER_NAME="validation_mariadb"
MARIADB_HOST_PORT=3307
REGISTRY=akraino
NAME=validation
TAG_PRE=`echo "${PWD##*/}"`
TAG_VER=latest
MARIADB_PASSWORD=""
HOST_ARCH=amd64

# get the architecture of the host
if [ "`uname -m`" = "aarch64" ]; then
    HOST_ARCH=arm64
fi

for ARGUMENT in "$@"
do
    KEY=$(echo $ARGUMENT | cut -f1 -d=)
    VALUE=$(echo $ARGUMENT | cut -f2 -d=)
    case "$KEY" in
            REGISTRY)              REGISTRY=${VALUE} ;;
            NAME)    NAME=${VALUE} ;;
            TAG_VER)    TAG_VER=${VALUE} ;;
            MARIADB_PASSWORD)    MARIADB_PASSWORD=${VALUE} ;;
            *)
    esac
done

if [ -z "$MARIADB_PASSWORD" ]
  then
    echo "ERROR: You must specify at least the mariadb database password"
    exit 1
fi

IMAGE="$REGISTRY"/"$NAME":"$TAG_PRE"-"$HOST_ARCH"-"$TAG_VER"
docker run --detach --name $CONTAINER_NAME --publish $MARIADB_HOST_PORT:3306 --volume $HOST_STORAGE_DIR:/var/lib/mysql -v "/$(pwd)/mariadb.conf:/etc/mysql/conf.d/my.cnf" -e MYSQL_ROOT_PASSWORD="$MARIADB_PASSWORD" $IMAGE
sleep 10
docker exec $CONTAINER_NAME /bin/bash -c "mysql < /EcompSdkDDLMySql_2_4_Common.sql ; mysql < /EcompSdkDDLMySql_2_4_OS.sql ; mysql < /EcompSdkDMLMySql_2_4_Common.sql ; mysql < /EcompSdkDMLMySql_2_4_OS.sql ; mysql < /akraino-blueprint_validation_db.sql"