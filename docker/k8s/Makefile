# get the architecture of the host
UNAME := $(shell uname -m)
ARCH = amd64
ifeq ($(UNAME), aarch64)
    ARCH = arm64
endif

# declare the variables
HOST_ARCH ?= $(ARCH)
REGISTRY = # TBD
TARGET =
IMAGE = $(REGISTRY)/$(TARGET)
IMAGE_TAG =
DOCKERFILE :=

# define targets
pre:
	wget https://github.com/estesp/manifest-tool/releases/download/v0.9.0/manifest-tool-linux-$(HOST_ARCH) \
		-O manifest-tool; \
	chmod +x ./manifest-tool

build_container:
	docker build \
	-t $(REGISTRY)/$(TARGET):$(IMAGE_TAG) \
	--build-arg HOST_ARCH=$(HOST_ARCH) \
	-f $(DOCKERFILE) \
	.

container:
	make build_container DOCKERFILE="Dockerfile" TARGET="akraino_validation" IMAGE_TAG="k8s-$(HOST_ARCH)-latest";

push_images:
	docker push $(REGISTRY)/$(TARGET):$(IMAGE_TAG)

push_manifest:
	./manifest-tool push from-args --platforms linux/amd64,linux/arm64 --template $(TEMPLATE) --target $(TAG)

push: pre container
	make push_images TARGET="akraino_validation" IMAGE_TAG="k8s-$(HOST_ARCH)-latest";
	make push_manifest TARGET="akraino_validation" \
		TEMPLATE="$(REGISTRY)/akraino_validation:k8s-ARCH-latest" \
		TAG=$(REGISTRY)/$(TARGET):k8s-latest;
