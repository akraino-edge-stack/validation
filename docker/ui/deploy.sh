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

# Container name
CONTAINER_NAME="akraino-validation-ui"
# Image data
REGISTRY=akraino
NAME=validation
TAG_PRE=ui
TAG_VER=latest
HOST_ARCH=amd64
# Container input parameters
mariadb_user_pwd=""
jenkins_url=""
jenkins_user_name=""
jenkins_user_pwd=""
jenkins_job_name=""
db_connection_url=""
nexus_proxy=""
jenkins_proxy=""

# get the architecture of the host
if [ "`uname -m`" = "aarch64" ]
  then
    HOST_ARCH=arm64
fi

for ARGUMENT in "$@"
do
    KEY=$(echo $ARGUMENT | cut -f1 -d=)
    VALUE=$(echo $ARGUMENT | cut -f2 -d=)
    case "$KEY" in
            REGISTRY)              REGISTRY=${VALUE} ;;
            NAME)    NAME=${VALUE} ;;
            TAG_PRE)    TAG_PRE=${VALUE} ;;
            TAG_VER)    TAG_VER=${VALUE} ;;
            mariadb_user_pwd)    mariadb_user_pwd=${VALUE} ;;
            jenkins_url)    jenkins_url=${VALUE} ;;
            jenkins_user_name)    jenkins_user_name=${VALUE} ;;
            jenkins_user_pwd)    jenkins_user_pwd=${VALUE} ;;
            jenkins_job_name)    jenkins_job_name=${VALUE} ;;
            db_connection_url)    db_connection_url=${VALUE} ;;
            CONTAINER_NAME)    CONTAINER_NAME=${VALUE} ;;
            nexus_proxy) nexus_proxy=${VALUE} ;;
            jenkins_proxy) jenkins_proxy=${VALUE} ;;
            *)
    esac
done

if [ -z "$db_connection_url" ]
  then
    echo "ERROR: You must specify the database connection url"
    exit 1
fi

if [ -z "$mariadb_user_pwd" ]
  then
    echo "ERROR: You must specify the mariadb root user password"
    exit 1
fi

if [ -z "$jenkins_url" ]
  then
    echo "ERROR: You must specify the Jenkins Url"
    exit 1
fi

if [ -z "$jenkins_user_name" ]
  then
    echo "ERROR: You must specify the Jenkins username"
    exit 1
fi

if [ -z "$jenkins_user_pwd" ]
  then
    echo "ERROR: You must specify the Jenkins user password"
    exit 1
fi

if [ -z "$jenkins_job_name" ]
  then
    echo "ERROR: You must specify the Jenkins job name"
    exit 1
fi

IMAGE="$REGISTRY"/"$NAME":"$TAG_PRE"-"$HOST_ARCH"-"$TAG_VER"
docker run --name $CONTAINER_NAME --network="host" -it --rm -e db_connection_url="$db_connection_url" -e maria_db_user_pwd="$mariadb_user_pwd" -e jenkins_url="$jenkins_url" -e jenkins_user_name="$jenkins_user_name" -e jenkins_user_pwd="$jenkins_user_pwd" -e jenkins_job_name="$jenkins_job_name" -e nexus_proxy="$nexus_proxy" -e jenkins_proxy="$jenkins_proxy" $IMAGE
sleep 10
