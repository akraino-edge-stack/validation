# Akraino Blueprint Validation UI

This project contains the source code of the Akraino Bluepint Validation UI. 

This UI consists of the frontend and backend parts. 
The frontend part is based on HTML, CSS, and AngularJS technologies.
The backend part is based on Spring MVC and Apache Tomcat technologies.  

## Getting Started

Based on these instructions, a user can provide the prerequisites, compile the source code and deploy the UI.

### Prerequisites

A PostgreSQL database instance is needed with the appropriate relations in order for the backend system to store and use data.
Replace root_password_of_database with the appropriate user root password and execute the following commands:

```
cd validation/docker/postgresql
make build
./deploy.sh root_password_of_database
```

### Compiling

```
cd validation/ui
mvn clean install 
```

### Deploying
In the context of deploying, the following data is needed: 

- Name of the docker registry (examine the result of make build command)
- Name of the docker image (examine the result of make build command)
- Tag version of the image (examine the result of make build command)
- The postgres root user password
- The Jenkins url
- The Jenkins username and password
- The name of Jenkins Job
- The Url of the Nexus results
- The host system's proxy ip and port

So, replace all the required data with the appropriate one and execute the following commands:

```
cd validation/docker/ui
make build
./deploy.sh registry image ver postgres_root_user_password jenkins_url jenkins_user jenkins_password jenkins_job_name nexus_url proxy_ip proxy_port
```

Example of registry: akraino
Example of image name: validation
Example of version: latest
Example of Nexus Url is : https://nexus.akraino.org/content/sites/logs


The UI should be available in the following url:

http://localhost:8080/AECBlueprintValidationUI
