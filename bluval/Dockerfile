# ref: https://docs.docker.com/develop/develop-images/dockerfile_best-practices/#use-multi-stage-builds
FROM ubuntu:18.04 as build

RUN apt-get update && apt-get -y install git python3-pip python3.7

# Build binaries
RUN git clone https://gerrit.akraino.org/r/validation /opt/akraino/validation 

# Install blueval dependencies
COPY requirements.txt /opt/akraino/validation/bluval/requirements.txt

RUN pip3 install -r /opt/akraino/validation/bluval/requirements.txt  

ENV LC_ALL=C.UTF-8
ENV LANG=C.UTF-8

WORKDIR /opt/akraino/validation
ENTRYPOINT  ["python3", "bluval/blucon.py"]