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
Documentation     Tempest is the OpenStack integration Test Suite with
...               batteries of tests for OpenStack API validation, scenarios
...               and other specific tests useful in validating an OpenStack
...               deployment.
Resource          tempest.resource
Suite Setup       Tempest Suite Setup
Suite Teardown    Tempest Suite Teardown

*** Test Cases ***
Tempest Refstack Test Run
    Run Tempest Test
