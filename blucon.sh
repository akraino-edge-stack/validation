#!/bin/bash

docker run --rm \
-v /var/run/docker.sock:/var/run/docker.sock \
-v /usr/bin/docker:/usr/bin/docker \
-v $AKRAINO_HOME/results:/opt/akraino/results \
-v $AKRAINO_HOME/validation:/opt/akraino/validation \
akraino/validation:blucon-latest $@