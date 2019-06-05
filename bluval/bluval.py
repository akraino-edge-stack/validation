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
"""This module parses yaml file, reads layers, testcases and executes each
testcase
"""

import subprocess
import sys
import traceback
import click
import yaml

class BluvalError(Exception):
    """Base class for exceptions in this module."""
    pass



class ShowStopperError(Exception):
    """Showstopper test case failed"""
    pass



def run_testcase(testcase):
    """Runs a single testcase
    """
    name = testcase.get('name')
    when = testcase.get('when', "True")
    if when.lower() == "false":
        # if not meeting when condition just skip it.
        print('Skipping {}'.format(name))
        return
    show_stopper = testcase.get('show_stopper', "False")
    what = testcase.get('what')
    variables = "variables.yaml"
    layer = testcase.get('layer')
    results = "results/"+layer+"/"+what
    test_path = "tests/"+layer+"/"+what
    args = ["robot", "-V", variables, "-d", results, test_path]

    print('Executing testcase {}'.format(name))
    print('show_stopper {}'.format(show_stopper))
    print('Invoking {}'.format(args))
    try:
        status = subprocess.call(args, shell=False)
        if status != 0 and show_stopper.lower() == "true":
            raise ShowStopperError(name)
    except OSError:
        #print('Error while executing {}'.format(args))
        raise BluvalError(OSError)

def validate_layer(blueprint, layer):
    """validates a layer by validating all testcases under that layer
    """
    print('## Layer {}'.format(layer))
    for testcase in blueprint[layer]:
        testcase['layer'] = layer
        run_testcase(testcase)


def validate_blueprint(yaml_loc, layer):
    """Parse yaml file and validates given layer. If no layer given all layers
    validated
    """
    with open(yaml_loc) as yaml_file:
        yamldoc = yaml.safe_load(yaml_file)
    blueprint = yamldoc['blueprint']
    if layer is None:
        for each_layer in blueprint['layers']:
            validate_layer(blueprint, each_layer)
    else:
        validate_layer(blueprint, layer)


@click.command()
@click.argument('blueprint')
@click.option('--layer', '-l')
def main(blueprint, layer):
    """Takes blueprint name and optional layer. Validates inputs and derives
    yaml location from blueprint name. Invokes validate on blue print.
    """
    yaml_loc = 'bluval/bluval-{}.yaml'.format(blueprint)
    if layer is not None:
        layer = layer.lower()
    try:
        validate_blueprint(yaml_loc, layer)
    except ShowStopperError as err:
        print('ShowStopperError:', err)
    except BluvalError as err:
        print('Unexpected BluvalError', err)
        raise
    except:
        print("Exception in user code:")
        print("-"*60)
        traceback.print_exc(file=sys.stdout)
        print("-"*60)
        raise

if __name__ == "__main__":
    # pylint: disable=no-value-for-parameter
    main()
