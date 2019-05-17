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

CONTAINER_NAME="blueprint-validation-ui"
REGISTRY=akraino
NAME=validation
TAG_PRE=`echo "${PWD##*/}"`
TAG_VER=latest
# get the architecture of the host
HOST_ARCH=amd64
postgres_db_user_pwd=""
jenkins_url=""
jenkins_user_name=""
jenkins_user_pwd=""
jenkins_job_name=""
nexus_results_url=""

if [ `uname -m` = "aarch64" ]
  then
    HOST_ARCH=arm64
fi

if [ ! -z "$1" ]
  then
    REGISTRY=$1
  else
    echo "ERROR: You must specify the registry"
    exit 1
fi

if [ ! -z "$2" ]
  then
    NAME=$2
  else
    echo "ERROR: You must specify the docker image name"
    exit 1
fi

if [ ! -z "$3" ]
  then
    TAG_VER=$3
  else
    echo "ERROR: You must specify the number part of the docker image label"
    exit 1
fi

if [ ! -z "$4" ]
  then
    postgres_db_user_pwd=$4
  else
    echo "ERROR: You must specify the postgresql root user password"
    exit 1
fi

if [ ! -z "$5" ]
  then
    jenkins_url=$5
  else
    echo "ERROR: You must specify the Jenkins Url"
    exit 1
fi

if [ ! -z "$6" ]
  then
    jenkins_user_name=$6
  else
    echo "ERROR: You must specify the Jenkins username"
    exit 1
fi

if [ ! -z "$7" ]
  then
    jenkins_user_pwd=$7
  else
    echo "ERROR: You must specify the Jenkins user password"
    exit 1
fi

if [ ! -z "$8" ]
  then
    jenkins_job_name=$8
  else
    echo "ERROR: You must specify the Jenkins job name"
    exit 1
fi

if [ ! -z "$9" ]
  then
    nexus_results_url=$9
  else
    echo "ERROR: You must specify the Nexus Url"
    exit 1
fi

proxy_ip=${10}
proxy_port=${11}


IMAGE="$REGISTRY"/"$NAME":"$TAG_PRE"-"$HOST_ARCH"-"$TAG_VER"
docker run --name $CONTAINER_NAME --network="host" -it --rm -e postgres_db_user_pwd="$postgres_db_user_pwd" -e jenkins_url="$jenkins_url" -e jenkins_user_name="$jenkins_user_name" -e jenkins_user_pwd="$jenkins_user_pwd" -e jenkins_job_name="$jenkins_job_name" -e nexus_results_url="$nexus_results_url" -e proxy_ip="$proxy_ip" -e proxy_port="$proxy_port" $IMAGE
sleep 10
