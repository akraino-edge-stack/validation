.. ############################################################################
.. Copyright (c) 2019 AT&T, ENEA AB, Nokia and others                         #
..                                                                            #
.. Licensed under the Apache License, Version 2.0 (the "License");            #
.. you maynot use this file except in compliance with the License.            #
..                                                                            #
.. You may obtain a copy of the License at                                    #
..       http://www.apache.org/licenses/LICENSE-2.0                           #
..                                                                            #
.. Unless required by applicable law or agreed to in writing, software        #
.. distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  #
.. WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.           #
.. See the License for the specific language governing permissions and        #
.. limitations under the License.                                             #
.. ############################################################################


Overview
========

The Makefile in this directory is used to build and push all
the validation containers. The default registry is **akraino** on
dockerhub, but only CI jenkins slaves are authorized to push
images to that registry. If you want to push to your own test registry, set
the REGISTRY variables as in the commands below.

To build and push the images:

.. code-block:: console

    make all [ REGISTRY=<dockerhub_registry> ]

To just build the containers, use the command:

.. code-block:: console

    make build-all [ REGISTRY=<dockerhub_registry> ]

The k8s container
=================

Building and pushing the container
----------------------------------

To build just the k8s container, use the command:

.. code-block:: console

    make k8s-build [ REGISTRY=<dockerhub_registry> ]

To both build and push the container, use the command:

.. code-block:: console

    make k8s [ REGISTRY=<dockerhub_registry> ]

Using the container
-------------------

The k8s image is meant to be ran from a server that has access to the
kubernetes cluster (jenkins slave, jumpserver, etc).

Before running the image, copy the folder ~/.kube from your kubernetes
master node to a local folder (e.g. /home/jenkins/k8s_access).

Container needs to be started with the kubernetes access folder mounted.
Optionally, the results folder can be mounted as well; this way the logs are
stored on the local server.

.. code-block:: console

    docker run -ti -v /home/jenkins/k8s_access:/root/.kube/ \
    -v /home/jenkins/k8s_results:/opt/akraino/validation/results/ \
    akraino/validation:k8s-latest

By default, the container will run the k8s conformance test. If you want to
enter the container, add */bin/sh* at the end of the command above


The mariadb container
=================

Building and pushing the container
----------------------------------

To build just the postgresql container, use the command:

.. code-block:: console

   make mariadb-build [ REGISTRY=<dockerhub_registry> NAME=<image_name>]

To both build and push the container, use the command:

.. code-block:: console

   make mariadb [ REGISTRY=<dockerhub_registry> NAME=<image_name>]

Using the container
-------------------
In order for the container to be easily created, the deploy.sh script has been developed. This script accepts the following as input parameters:

CONTAINER_NAME, name of the container, default value is akraino_validation_mariadb
MARIADB_PASSWORD, mariadb root user password, this variable is required
admin_password, the Blueprint Validation UI password for the admin user, this variable is required
akraino_password, the Blueprint Validation UI password for the akraino user, this variable is required
REGISTRY, registry of the mariadb image, default value is akraino
NAME, name of the mariadb image, default value is validation
TAG_VER, last part of the image version, default value is latest
MARIADB_HOST_PORT, port on which mariadb is exposed on host, default value is 3307

If you want to deploy the container, you can run this script with the appropriate parameters.

Example (assuming you have used the default variables for building the image using the make command):

.. code-block:: console
    ./deploy.sh MARIADB_PASSWORD=password admin_password=admin akraino_password=akraino


The ui container
=================

Building and pushing the container
----------------------------------

To build just the UI container, use the command:

.. code-block:: console

   make ui-build [ REGISTRY=<dockerhub_registry> NAME=<image_name>]

To both build and push the container, use the command:

.. code-block:: console

   make ui [ REGISTRY=<dockerhub_registry> NAME=<image_name>]

Using the container
-------------------
In order for the container to be easily created, the deploy.sh script has been developed under the UI source code directory. This script accepts the following as input parameters:

CONTAINER_NAME, name of the contaner, default value is akraino-validation-ui-dev
mariadb_user_pwd, mariadb root user password, this variable is required
REGISTRY, registry of the mariadb image, default value is akraino
NAME, name of the mariadb image, default value is validation
TAG_PRE, first part of the image version, default value is dev-ui
TAG_VER, last part of the image version, default value is latest
jenkins_url, the URL of the Jenkins instance, this variable is required
jenkins_user_name, the Jenkins user name, this variable is required
jenkins_user_pwd, the Jenkins user password, this variable is required
jenkins_job_name, the name of Jenkins job capable of executing the blueprint validation tests, this variable is required
db_connection_url, the URL connection with the akraino databse of the maridb instance, this variable is required
nexus_proxy, the proxy needed in order for the Nexus server to be reachable, default value is none
jenkins_proxy, the proxy needed in order for the Jenkins server to be reachable, default value is none

Note that, for a functional UI, the following prerequisites are needed:

- The mariadb container in up and running state
- A Jenkins instance capable of running the blueprint validation test
- A Nexus repo in which all the test results are stored.

Look at the UI README file for more info.

If you want to deploy the container, you can run the aforementioned script with the appropriate parameters.

Example (assuming you have used the default variables for building the image using the make command):

.. code-block:: console
    ./deploy.sh CONTAINER_NAME=akraino-validation-ui TAG_PRE=ui db_connection_url=172.17.0.3:3306/akraino mariadb_user_pwd=password jenkins_url=http://192.168.2.2:8080 jenkins_user_name=name jenkins_user_pwd=jenkins_pwd jenkins_job_name=job1
