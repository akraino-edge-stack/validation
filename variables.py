#!/usr/bin/python3
##############################################################################
# Copyright (c) 2019 AT&T Intellectual Property.                             #
# Copyright (c) 2019 Nokia.                                                  #
#                                                                            #
# Licensed under the Apache License, Version 2.0 (the "License"); you may    #
# not use this file except in compliance with the License.                   #
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

"""
Taking mandatory variable lab and returns a key value map.
Purpose of ths method is to create a key value map to pass as
variables to robot testcases.
"""

import sys


def get_variables(lab):
    """return variables
    """
    variables = {
        "sysinfo": "PowerEdge R740xd",
        "bios_revision": "1.3"
    }

    if lab == "att":
        variables.update({
            "host": "aknode109",
            "username": "localadmin"
        })

    return variables


if __name__ == "__main__":
    print(get_variables(sys.argv[1]))
