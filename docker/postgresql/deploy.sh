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
IMAGE="akraino/validation_postgresql:postgresql-amd64-latest"
CONTAINER_NAME="validation_postgresql"
POSTGRES_HOST_PORT=6432
docker run --detach --name $CONTAINER_NAME --restart=always --publish $POSTGRES_HOST_PORT:5432 --volume $DROOT/postgres:/var/lib/postgresql/data --env POSTGRES_USER=admin --env POSTGRES_PASSWORD="$PW" $IMAGE
sleep 10

