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
from pathlib import Path

import click
import yaml

_OPTIONAL_ALSO = False

def run_testcase(testcase):
    """Runs a single testcase
    """
    result = 1;
    name = testcase.get('name')
    skip = testcase.get('skip', "False")
    optional = testcase.get('optional', "False")
    if skip.lower() == "true":
        # skip is mentioned and true.
        print('Skipping {}'.format(name))
        return
    print("_OPTIONAL_ALSO {}".format(_OPTIONAL_ALSO))
    if  not _OPTIONAL_ALSO and optional.lower() == "true":
        # Optional Test case.
        print('Ignoring Optional {} testcase'.format(name))
        return
    show_stopper = testcase.get('show_stopper', "False")
    what = testcase.get('what')
    mypath = Path(__file__).absolute()
    results_path = mypath.parents[2].joinpath(
        "results/"+testcase.get('layer')+"/"+what)
    test_path = mypath.parents[1].joinpath(
        "tests/"+testcase.get('layer')+"/"+what)

    # add to the variables file the path to where to sotre the logs
    variables_file = mypath.parents[1].joinpath("tests/variables.yaml")
    variables_dict = yaml.safe_load(variables_file.open())
    variables_dict['log_path'] = str(results_path)
    variables_file.write_text(str(variables_dict))

    # run the test
    args = ["robot", "-V", str(variables_file), "-d",
            str(results_path), str(test_path)]

    print('Executing testcase {}'.format(name))
    print('show_stopper {}'.format(show_stopper))
    print('Invoking {}'.format(args))
    try:
        result = subprocess.call(args, shell=False)
        print('Result {}'.format(result))
    except OSError:
        result = 1
        return result
        #print('Error while executing {}'.format(args))
    return result

def validate_layer(blueprint, layer):
    """validates a layer by validating all testcases under that layer
    """
    result = 1
    print('## Layer {}'.format(layer))
    for testcase in blueprint[layer]:
        testcase['layer'] = layer
        result = run_testcase(testcase)
        if result:
            print('test '+ str(testcase) + ' failed with error' +str(result))
            #if one test fails, all tests are failed
    return result


def validate_blueprint(yaml_loc, layer):
    """Parse yaml file and validates given layer. If no layer given all layers
    validated
    """
    with open(str(yaml_loc)) as yaml_file:
        yamldoc = yaml.safe_load(yaml_file)
    blueprint = yamldoc['blueprint']
    result = validate_layer(blueprint, layer)
    return result

def write_test_info(layer):
    """writes testing info to test_info.yaml
    """
    data = dict(
        test_info=dict(
            layer=layer,
            optional=_OPTIONAL_ALSO,
        )
    )

    with open('/opt/akraino/results/test_info.yaml', 'w') as outfile:
        yaml.dump(data, outfile, default_flow_style=False)


@click.command()
@click.argument('blueprint')
@click.option('--layer', '-l')
@click.option('--optional_also', '-o', is_flag=True)
def main(blueprint, layer, optional_also):
    """Takes blueprint name and optional layer. Validates inputs and derives
    yaml location from blueprint name. Invokes validate on blue print.
    """
    global _OPTIONAL_ALSO  # pylint: disable=global-statement
    result = 1
    mypath = Path(__file__).absolute()
    yaml_loc = mypath.parents[0].joinpath('bluval-{}.yaml'.format(blueprint))
    if layer is not None:
        layer = layer.lower()
    if optional_also:
        _OPTIONAL_ALSO = True
        print("_OPTIONAL_ALSO {}".format(_OPTIONAL_ALSO))
    try:
        write_test_info(layer)
        result = validate_blueprint(yaml_loc, layer)
    except:
        print("Exception in user code:")
        print("-"*60)
        traceback.print_exc(file=sys.stdout)
        print("-"*60)
        sys.exit(1)
    if result:
        sys.exit(1)

if __name__ == "__main__":
    # pylint: disable=no-value-for-parameter
    main()
