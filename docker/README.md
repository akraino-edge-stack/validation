# Usage for building the docker containers

Overview
========

The Makefile in this directory is used to build and push all
the validation containers. The command to do that is:
   make all REGISTRY=<dockerhub_registry>
To just build the containers, use the command:
   make build-all REGISTRY=<dockerhub_registry>

The k8s container
=================

To build just the k8s container, use the command:
   make k8s-build REGISTRY=<dockerhub_registry>
To both build and push the container, use the command:
   make k8s REGISTRY=<dockerhub_registry>

Container should be started with the admin.conf file mounted:
docker run -ti -v /home/jenkins/admin.conf:/root/.kube/config \
<dockerhub_registry>/akraino_validation:k8s-latest /bin/sh
