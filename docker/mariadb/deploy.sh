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
CONTAINER_NAME="akraino_validation_mariadb"
# Container input variables
MARIADB_PASSWORD=""
admin_password=""
akraino_password=""
# Image data
REGISTRY=akraino
NAME=validation
TAG_PRE=`echo "${PWD##*/}"`
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
            MARIADB_PASSWORD)    MARIADB_PASSWORD=${VALUE} ;;
            CONTAINER_NAME)    CONTAINER_NAME=${VALUE} ;;
            MARIADB_HOST_PORT)    MARIADB_HOST_PORT=${VALUE} ;;
            admin_password)    admin_password=${VALUE} ;;
            akraino_password)    akraino_password=${VALUE} ;;
            *)
    esac
done

if [ -z "$MARIADB_PASSWORD" ]
  then
    echo "ERROR: You must specify at least the mariadb database password"
    exit 1
fi

if [ -z "$admin_password" ]
  then
    echo "ERROR: You must specify the password of the admin user"
    exit 1
fi

if [ -z "$akraino_password" ]
  then
    echo "ERROR: You must specify the password for the akraino user"
    exit 1
fi

IMAGE="$REGISTRY"/"$NAME":"$TAG_PRE"-"$HOST_ARCH"-"$TAG_VER"
docker run --detach --name $CONTAINER_NAME --publish $MARIADB_HOST_PORT:3306 --volume $HOST_STORAGE_DIR:/var/lib/mysql -v "/$(pwd)/mariadb.conf:/etc/mysql/conf.d/my.cnf" -e MYSQL_ROOT_PASSWORD="$MARIADB_PASSWORD" -e admin_password="$admin_password" -e akraino_password="$akraino_password" $IMAGE
sleep 15
docker exec $CONTAINER_NAME /bin/bash -c '/etc/init.d/mysql start ; sleep 10 ; sed -i 's/admin_password/'"$admin_password"'/g' /EcompSdkDMLMySql_2_4_OS.sql ; sed -i 's/akraino_password/'"$akraino_password"'/g' /akraino-blueprint_validation_db.sql ; mysql -p`echo "$MYSQL_ROOT_PASSWORD"` -uroot -h 127.0.0.1 < /EcompSdkDDLMySql_2_4_Common.sql ; mysql -p`echo "$MYSQL_ROOT_PASSWORD"` -uroot -h 127.0.0.1 < /EcompSdkDDLMySql_2_4_OS.sql ; mysql -p`echo "$MYSQL_ROOT_PASSWORD"` -uroot -h 127.0.0.1 < /EcompSdkDMLMySql_2_4_Common.sql ; mysql -p`echo "$MYSQL_ROOT_PASSWORD"` -uroot -h 127.0.0.1 < /EcompSdkDMLMySql_2_4_OS.sql ; mysql -p`echo "$MYSQL_ROOT_PASSWORD"` -uroot -h 127.0.0.1 < /akraino-blueprint_validation_db.sql'