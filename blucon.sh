#!/bin/bash

##############################################################################
# Copyright (c) 2019 AT&T, ENEA Nokia and others                             #
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

if [ -z "$AKRAINO_HOME" ]
then
    echo "AKRAINO_HOME not available. Setting..."
    export AKRAINO_HOME=$(dirname $(dirname "$(readlink -f $0)"))
fi
echo "AKRAINO_HOME=$AKRAINO_HOME"

if [ "$#" -eq 0 ]
then
    echo "No arguments passed, assuming --help"
    set -- "--help"
fi

echo "Building docker image"
docker build -t akraino/validation:blucon-local $AKRAINO_HOME/validation/bluval

set -x

docker run --rm \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -v /usr/bin/docker:/usr/bin/docker \
    -v $AKRAINO_HOME/results:/opt/akraino/results \
    -v $AKRAINO_HOME/validation:/opt/akraino/validation \
    akraino/validation:blucon-local "$@"
