#!/bin/bash
#
# Copyright 2018 AT&T Intellectual Property.  All other rights reserved.
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

set -e -u
echo "==> send_results.sh"

# Deploying logs to LFNexus log server ##

NEXUS_URL=https://nexus.akraino.org
SILO=att-blu-val
BLUEPRNT=rec
VERSION=master
TIMESTAMP=$(date "+%Y%m%d-%H%M%S")
NEXUS_PATH="${SILO}/${BLUEPRNT}/${VERSION}/${TIMESTAMP}"

zip -r results.zip results
echo "executing lftools deploy nexus-zip $NEXUS_URL logs $NEXUS_PATH results.zip"
lftools deploy nexus-zip $NEXUS_URL logs $NEXUS_PATH results.zip
rm results.zip
