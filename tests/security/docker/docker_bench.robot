##############################################################################
# Copyright (c) 2019 AT&T Intellectual Property.                             #
# Copyright (c) 2019 Nokia.                                                  #
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


*** Settings ***

Documentation     Runs the Docker Bench for Security script which checks for
...               dozens of common best-practices around deploying Docker
...               containers in production.
Resource          docker_bench.resource
Suite Setup       Open Connection And Log In
Suite Teardown    Close All Connections
Test Teardown     Remove Test Software


*** Test Cases ***
Security Check By Docker Bench
    Download Docker Bench Test Software
    Upload Test Software To Nodes
    Run Test Software On Nodes
