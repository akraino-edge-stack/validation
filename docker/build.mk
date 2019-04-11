# declare the variables
REGISTRY ?= # TBD
NAME ?= akraino_validation
TAG_PRE ?= $(notdir $(CURDIR))
TAG_VER ?= latest
DOCKERFILE ?= Dockerfile
MTOOL ?= $(dir $(realpath $(lastword $(MAKEFILE_LIST))))/manifest-tool

# get the architecture of the host
HOST_ARCH = amd64
ifeq ($(shell uname -m), aarch64)
    HOST_ARCH = arm64
endif

$(MTOOL):
	wget https://github.com/estesp/manifest-tool/releases/download/v0.9.0/manifest-tool-linux-$(HOST_ARCH) -O $@
	sudo chmod +x $@

.PHONY: .build
.build:
	docker build \
		-t $(REGISTRY)/$(NAME):$(TAG_PRE)-$(HOST_ARCH)-$(TAG_VER) \
		-f $(DOCKERFILE) \
		.

.PHONY: .push_image
.push_image: .build
	docker push $(REGISTRY)/$(NAME):$(TAG_PRE)-$(HOST_ARCH)-$(TAG_VER)

.PHONY: .push_manifest
.push_manifest: $(MTOOL)
	../manifest-tool push from-args \
		--platforms linux/amd64,linux/arm64 \
		--template $(REGISTRY)/$(NAME):$(TAG_PRE)-ARCH-$(TAG_VER) \
		--target $(REGISTRY)/$(NAME):$(TAG_PRE)-$(TAG_VER)
