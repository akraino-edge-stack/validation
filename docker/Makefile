SUBDIRS := $(wildcard */.)
SUBDIRS_NAME := $(SUBDIRS:/.=)
SUBDIRS_BUILD := $(SUBDIRS:/.=-build)
SUBDIRS_PUSH := $(SUBDIRS:/.=-push)

.PHONY: all
all: $(SUBDIRS_NAME)

.PHONY: build-all
build-all: $(SUBDIRS_BUILD)

.PHONY: $(SUBDIRS_NAME)
$(SUBDIRS_NAME):
	$(MAKE) -C $@

.PHONY: $(SUBDIRS_BUILD)
$(SUBDIRS_BUILD): $(SUBDIRS)
	$(MAKE) -C $< build

include build.mk
