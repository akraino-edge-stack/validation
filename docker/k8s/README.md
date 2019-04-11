# Usage for k8s docker container

Container can be built locally using the command: make container
Image can be pushed to a repo using the command: make push

Container should be started with the admin.conf file mounted:
docker run -ti -v /home/jenkins/admin.conf:/root/.kube/config \
<image> /bin/sh
