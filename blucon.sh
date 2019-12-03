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

set -x

if [ -z "$AKRAINO_HOME" ]; then
    echo "AKRAINO_HOME variable not set"
    exit 1
else
    echo "AKRAINO_HOME=$AKRAINO_HOME"
fi

docker run --rm \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -v /usr/bin/docker:/usr/bin/docker \
    -v $AKRAINO_HOME/results:/opt/akraino/results \
    -v $AKRAINO_HOME/validation:/opt/akraino/validation \
    akraino/validation:blucon-latest "$@"
