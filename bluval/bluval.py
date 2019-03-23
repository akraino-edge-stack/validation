#!/usr/bin/python
##############################################################################
# Copyright (c) 2018 AT&T Intellectual Property. All rights reserved.        #
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
import os
import sys
import yaml


def run_testcase(testcase):
    print 'Executing testcase {}'.format(testcase['name'])
    cmd = 'robot {}'.format(testcase['what'])
    print 'Invoking {}'.format(cmd)
    try:
      os.system(cmd)
    except Exception:
      print 'Error while executing {}'.format(cmd)
      return -1;
    return os.EX_OK   



def parse_yaml(file):
  with open(file) as f:
    y = yaml.safe_load(f)
    b = y['blueprint']
    for s in b['sections']:
      print '## Section {}'.format(s)
      for t in b[s]:
        run_testcase(t)

if __name__ == "__main__":
  if len(sys.argv) != 2:
    print 'usage: bluval.py <testcase.yaml>'
    sys.exit(1)
  parse_yaml(sys.argv[1]) 
