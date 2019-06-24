
Akraino Blueprint Validation UI
========

This project contains the source code of the Akraino Blueprint Validation UI. It is based on the ONAP portal SDK, version 2.4.0.

This UI consists of the front-end and back-end parts.
The front-end part is based on HTML, CSS, and AngularJS technologies.
The back-end part is based on Spring MVC and Apache Tomcat technologies.

Based on these instructions, a user can provide the prerequisites, compile the source code and deploy the UI.

The UI purpose of the UI is twofold:

1) Support full control loop of producing results. In this mode, the UI must be connected with a Jenkins instance capable of running blueprint validation tests.
   Also, the UI must be connected with a mariadb instance and the Nexus server where the results are stored. Then, it will be able to trigger the appropriate job in Jenkins and recieve the corresponisng results from Nexus.
   Note that it makes no difference whether the Jenkins instance is the community one or a private one.
2) Partial control of producing results. In this mode, the UI must be connected with a mariadb instance and the Nexus server where the results are stored. 
   Every blueprint owner is responsible of executing tests and storing results in Nexus using his/her own Jenkins instance. The UI only retrieves results from Nexus and displays them.

Currently, the partial control loop is not supported.

Developer's guide
-----------------

Download the project
~~~~~~~~~~~~~~~~~~~~

.. code-block:: console

    git clone "https://gerrit.akraino.org/r/validation"

Prerequisites
~~~~~~~~~~~~~

- Database

A mariadb database instance is needed with the appropriate databases and tables in order for the back-end system to store and retrieve data.

This database is located in validation/docker/mariadb. A script has been developed, namely validation/docker/mariadb/deploy.sh which easily deploys the container. 

This script accepts the following as input parameters:

CONTAINER_NAME, name of the container, default value is akraino_validation_mariadb
MARIADB_PASSWORD, mariadb root user password, this variable is required
admin_password, the Blueprint Validation UI password for the admin user, this variable is required
akraino_password, the Blueprint Validation UI password for the akraino user, this variable is required
REGISTRY, registry of the mariadb image, default value is akraino
NAME, name of the mariadb image, default value is validation
TAG_VER, last part of the image version, default value is latest
MARIADB_HOST_PORT, port on which mariadb is exposed on host, default value is 3307

Currently, two users are supported namely admin (full privileges) and akraino (limited privileges).

Let's build the image using only the required parameters. Configure the mariadb root password, the admin password and the akraino password in the appropriate variables and execute the following commands in order to build and deploy this database container:

.. code-block:: console

    cd validation/docker/mariadb
    make build
    ./deploy.sh MARIADB_PASSWORD=<root user password> admin_password=<UI admin user password> akraino_password=<UI akraino user password>

Below, some data that is initialized in the aforementioned database is illustrated (note that this data is used mainly for testing purposes):

.. code-block:: console

    Timeslots:
    id:1 , start date and time: now() (i.e. the time of the postgreSQL container deployment), duration: 10 (sec), lab: 0 (i.e. AT&T)
    id:2 , start date and time: now() (i.e. the time of the postgreSQL container deployment), duration: 1000 (sec), lab: 0 (i.e. AT&T)

    Blueprints:
    id: 1 , name : 'dummy'
    id: 2 , name : 'Unicycle'

    Blueprint Instances:
    id: 1, blueprint_id: 1 (i.e. dummy), version: "0.0.2-SNAPSHOT", layer: 0 (i.e. Hardware), layer_description: "Dell Hardware", timeslot id: 1
    id: 2, blueprint_id: 2 (i.e. Unicycle), version: "0.0.1-SNAPSHOT", layer: 0 (i.e. Hardware), layer_description: "Dell Hardware", timeslot id: 2

For more information about this data, please refer to the file:

    validation/ui/db-scripts/akraino-blueprint_validation_db.sql

Based on this data, the UI enables the user to select an appropriate blueprint instance for validation.

Currently, this data cannot be retrieved dynamically by the UI (see limitations subsection).

For this reason, in cases of new blueprint data, a user should define new entries in this database.

For example, if a user wants to define a new timeslot with the following data:

    start date and time:now, duration: 123 in secs, lab: Community

the following file should be created:

name: dbscript
content:
    SET FOREIGN_KEY_CHECKS=1;
    use akraino;
    insert into timeslot values(5, now(), 123, 2);

Then, the following command should be executed:

.. code-block:: console

    mysql -p<MARIADB_PASSWORD> -uroot -h <IP of the mariadb container> < ./dbscript.sql

Furthermore, if a user wants to define a new blueprint, namely "newBlueprint" and a new instance of this blueprint with the following data:

    version: "0.0.1-SNAPSHOT", layer: 2 (i.e. K8s), layer_description: "K8s with High Availability Ingress controller", timeslot id: 5 (i.e. the new timeslot)

the following file should be created:

name: dbscript
content:
    SET FOREIGN_KEY_CHECKS=1;
    use akraino;
    insert into akraino.blueprint (blueprint_id, blueprint_name) values(4, 'newBlueprint');
    insert into akraino.blueprint_instance (blueprint_instance_id, blueprint_id, version, layer, layer_description, timeslot_id) values(6, 4, '0.0.1-SNAPSHOT', 2, 'K8s with High Availability Ingress controller', 5);

Then, the following command should be executed:

.. code-block:: console

    mysql -p<MARIADB_PASSWORD> -uroot -h <IP of the mariadb container> < ./dbscript.sql

The UI will automatically retrieve this new data and display it to the user.

- Jenkins Configuration

Recall that for full control loop, a Jenkins instance is needed capable of executing blueprint validation tests. The Blueprint validation UI will trigger job executions in that instance.

This instance must have the following option enabled: "Manage Jenkins -> Configure Global Security -> Prevent Cross Site Request Forgery exploits".

Also, currently, the corresponding Jenkins job should accept the following as input parameters: "SUBMISSION_ID", "BLUEPRINT", "LAYER" and "UI_IP".
The "SUBMISSION_ID" and "UI_IP" parameters (i.e. IP address of the UI host machine-this is needed by the Jenkins instance in order to send back Job completion notification) are created and provided by the back-end part of the UI.
The "BLUEPRINT" and "LAYER" parameters are configured by the UI user.

Moreover, as the Jenkins notification plugin (https://wiki.jenkins.io/display/JENKINS/Notification+Plugin) seems to ignore proxy settings, the corresponding Jenkins job must be configured to execute the following commands at the end (Post-build Actions)

.. code-block:: console

    cookie=`curl -v -H "Content-Type: application/x-www-form-urlencoded" -X POST --insecure --silent http://$UI_IP:8080/AECBlueprintValidationUI/login_external -d "loginId=akraino&password=akraino" 2>&1 | grep "Set-Cookie: " | awk -F ':' '{print $2}'` 
    curl -v --cookie $cookie -H "Content-Type: application/json" -X POST --insecure --silent http://$UI_IP:8080/AECBlueprintValidationUI/api/jenkinsJobNotification/ --data '{"submissionId": "'"$SUBMISSION_ID"'" , "name":"'"$JOB_NAME"'", "buildNumber":"'"$BUILD_NUMBER"'"}'

- Nexus server

All the blueprint validation results are stored in Nexus server.

In the context of the full control loop, these results must be available in the following url:

    https://nexus.akraino.org/content/sites/logs/<lab_silo>/job/validation/<Jenkins job number>/results/<layer>/<name_of_the_test_suite>.

where "lab_silo" is the name of the silo given to a lab where the results have been produced (for example 'att-blu-val'), "Jenkins job number" is the number of the Jenkins job that produced this result, "layer" is the blueprint layer and "name_of_the_test_suite" is the name of the test suite.

Moreover, the results should be stored in the 'output.xml' file using the following format:

TBD

In the context of partial control, the results must be available in the following url:

TBD


Compiling
~~~~~~~~~

.. code-block:: console

    cd validation/ui
    mvn clean package

Deploying
~~~~~~~~~

The pom.xml file supports the building of an appropriate container using the produced war file. Also, a script has been developed, namely validation/ui/deploy.sh which easily deploys the container. 

This script accepts the following as input parameters:

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
db_connection_url, the URL connection with the akraino database of the maridb instance, this variable is required
nexus_proxy, the proxy needed in order for the Nexus server to be reachable, default value is none
jenkins_proxy, the proxy needed in order for the Jenkins server to be reachable, default value is none

Let's build the image using only the required parameters. To this end, the following data is needed:

- The mariadb root user password
- The URL for connecting to the akraino database of the mariadb
- The Jenkins url
- The Jenkins username and password
- The name of Jenkins Job

Execute the following commands in order to build and deploy the UI container:

.. code-block:: console

    cd validation/docker/ui
    mvn clean package docker:build 
    ./deploy.sh mariadb_user_pwd=<mariadb root password> jenkins_url=<http://jenkinsIP:port> jenkins_user_name=<Jenkins user> jenkins_user_pwd=<Jenkins password> jenkins_job_name=<Jenkins job name> db_connection_url=<Url in order to connect to akraino database of the mariadb>
    
An example for the db_connection_url is 172.17.0.3:3306/akraino.

If no proxy exists, just do not define proxy ip and port variables.

The UI should be available in the following url:

    http://localhost:8080/AECBlueprintValidationUI

Note that the deployment uses the network host mode, so the 8080 must be available on the host.

User's guide
-----------------
TBD

Limitations
-----------

- The UI has been tested using Chrome and Firefox browsers.
- The back-end part of the UI does not take into account the configured timeslot. It immediately triggers the corresponding Jenkins Job.
- Results data manipulation (filtering, graphical representation, indexing in time order, etc) is not supported.
- Only the following labs are supported: AT&T, Ericsson, Community and Arm.
- Only the following tabs are functional: 'Committed Submissions', 'Blueprint Validation Results -> Get by submission id'.
- The UI configures only the "BLUEPRINT", "LAYER", "SUBMISSION_ID" and "UI_IP" input parameters of the Jenkins job.
- The available blueprints and timeslots must be manually configured in the mariadb database.
- Logout action is not currently supported.