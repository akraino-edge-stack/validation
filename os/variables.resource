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

*** Variables ***
# Manual test: robot -v HOST:host_ip -v USERNAME:root -v PASSWORD:root_passwd ltp.robot

${HOST}        localhost    # cluster's master node address
${USERNAME}    root         # root user to log in to the host
${PASSWORD}    root_passwd  # root passwd to log in to the host
${LOG}         /opt/akraino/validation/results/os/print_ltp.txt
