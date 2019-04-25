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
Resource          ceph_keywords.resource
Suite Setup       Open Connection And Log In
Suite Teardown    Close All Connections
Test Setup        Ceph Should Be Healthy
Test Teardown     Global Ceph Returns Healthy

*** Test Cases ***
Ceph Monitor Test Case
    [Documentation]     Wait 4m for fail and recover
    Verify Ceph Monitor Status
    Failure Of Ceph Monitor
    After Deletion Verify Ceph Monitor

Ceph Manager Test Case
    [Documentation]     Wait 4m for fail and recover
    Verify Ceph Manager Status
    Failure Of Ceph Manager
    After Deletion Verify Ceph Manager

Ceph Osd Test Case
    [Documentation]     Wait 4.5m for fail and recover
    Verify Ceph Osd Status
    Failure Of Ceph Osd Component
    After Deletion Verify Ceph Osd
