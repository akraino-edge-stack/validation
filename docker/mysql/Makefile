##############################################################################
# Copyright (c) 2019 AT&T Intellectual Property. All rights reserved.        #
#                                                                            #
# Licensed under the Apache License, Version 2.0 (the "License");            #
# you maynot use this file except in compliance with the License.            #
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

.PHONY: all
all: push-image .push_manifest

# MySQL 5.6 is not supported on arm64 yet, skip building it for now
.PHONY: build push-image
ifneq ($(shell uname -m), aarch64)
build: .build
push-image: .push_image
else
build:
	echo "Skipping build on aarch64 platform!"
	exit 0
push-image:
	echo "Skipping push on aarch64 platform!"
	exit 0
endif

include ../build.mk
