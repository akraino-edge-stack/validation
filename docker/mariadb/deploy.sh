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

# Directory on host in where database data will be stored
HOST_STORAGE_DIR="/var/lib/mariadb"
# Container name
CONTAINER_NAME="akraino-validation-mariadb"
# Container input variables
MARIADB_ROOT_PASSWORD=""
UI_ADMIN_PASSWORD=""
UI_AKRAINO_PASSWORD=""
# Image data
REGISTRY=akraino
NAME=validation
TAG_PRE=mariadb
TAG_VER=latest
HOST_ARCH=amd64
MARIADB_HOST_PORT=3307

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
            TAG_PRE)    TAG_PRE=${VALUE} ;;
            MARIADB_ROOT_PASSWORD)    MARIADB_ROOT_PASSWORD=${VALUE} ;;
            CONTAINER_NAME)    CONTAINER_NAME=${VALUE} ;;
            MARIADB_HOST_PORT)    MARIADB_HOST_PORT=${VALUE} ;;
            UI_ADMIN_PASSWORD)    UI_ADMIN_PASSWORD=${VALUE} ;;
            UI_AKRAINO_PASSWORD)    UI_AKRAINO_PASSWORD=${VALUE} ;;
            *)
    esac
done

if [ -z "$MARIADB_ROOT_PASSWORD" ]
  then
    echo "ERROR: You must specify the mariadb database root password"
    exit 1
fi

if [ -z "$UI_ADMIN_PASSWORD" ]
  then
    echo "ERROR: You must specify the password of the UI admin user"
    exit 1
fi

if [ -z "$UI_AKRAINO_PASSWORD" ]
  then
    echo "ERROR: You must specify the password for the UI_akraino user"
    exit 1
fi

IMAGE="$REGISTRY"/"$NAME":"$TAG_PRE"-"$HOST_ARCH"-"$TAG_VER"
docker run --detach --name $CONTAINER_NAME --publish $MARIADB_HOST_PORT:3306 --volume $HOST_STORAGE_DIR:/var/lib/mysql -v "/$(pwd)/mariadb.conf:/etc/mysql/conf.d/my.cnf" -e MYSQL_ROOT_PASSWORD="$MARIADB_ROOT_PASSWORD" -e UI_ADMIN_PASSWORD="$UI_ADMIN_PASSWORD" -e UI_AKRAINO_PASSWORD="$UI_AKRAINO_PASSWORD" $IMAGE
sleep 10
docker exec $CONTAINER_NAME /bin/bash -c 'continue=`ps aux | grep mysql` ; while [ -z "$continue" ]; do continue=`ps aux | grep mysql`; sleep 5; done ; sed -i 's/admin_password/'"$UI_ADMIN_PASSWORD"'/g' /EcompSdkDMLMySql_2_4_OS.sql ; sed -i 's/akraino_password/'"$UI_AKRAINO_PASSWORD"'/g' /akraino-blueprint_validation_db.sql; mysql < /EcompSdkDDLMySql_2_4_Common.sql ; mysql < /EcompSdkDDLMySql_2_4_OS.sql ; mysql < /EcompSdkDMLMySql_2_4_Common.sql ; mysql < /EcompSdkDMLMySql_2_4_OS.sql ; mysql < /akraino-blueprint_validation_db.sql'
