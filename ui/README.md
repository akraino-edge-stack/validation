
# Akraino Blueprint Validation UI

This project contains the source code of the Akraino Bluepint Validation UI. 

This UI consists of the frontend and backend parts. 
The frontend part is based on HTML, CSS, and AngularJS technologies.
The backend part is based on Spring MVC and Apache Tomcat technologies.  

## Getting Started

Based on these instructions, a user can provide the prerequisites, compile the source code and deploy the UI.

### Prerequisites

A PostgreSQL database instance is needed with the appropriate relations in order for the backend system to store and use data.
Configure the postgreSQL root password in the variable POSTGRES_PASSWORD and execute the following commands:

```
cd validation/docker/postgresql
make build
./deploy.sh POSTGRES_PASSWORD=password
```

### Compiling

```
cd validation/ui
mvn clean install 
```

### Deploying
In the context of deploying, the following data is needed: 

- The postgres root user password
- The Jenkins url
- The Jenkins username and password
- The name of Jenkins Job
- The Url of the Nexus results
- The host system's proxy ip and port

These variables must be configured as content of the deploy script input parameters. Execute the following commands:

```
cd validation/docker/ui
make build
./deploy.sh postgres_db_user_pwd=password jenkins_url=http://192.168.2.2:8080 jenkins_user_name=name jenkins_user_pwd=jenkins_pwd jenkins_job_name=job1 nexus_results_url=https://nexus.akraino.org/content/sites/logs proxy_ip=172.28.40.9 proxy_port=3128    
```

If no proxy exists, just do not define proxy ip and port variables.

The UI should be available in the following url:

http://localhost:8080/AECBlueprintValidationUI
