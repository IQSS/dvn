# Insert copyright statement here

# installation Makefile for the logger module 

include ../Make.rules

meDIR	= rcut
meRPM	= VDC-RAP
meSPC	= vdc-rap.spec
CC	= gcc
CFLAGS	= -O2 -Wall -pedantic -lm

setup: 

build:: all

all:: Make.rules rcut

clean::
	rm -f rcut

install:: rcut
	$(INSTALL) -m 0775 -d $(RPM_PREFIX)/$(VDC_DIR)/bin
	$(INSTALL) -m 0777 rcut $(RPM_PREFIX)/$(VDC_DIR)/bin

Make.rules:
	@if [ -f ../Make.rules ]; then \
		cp ../Make.rules $@; \
	else \include Make.rules \
		echo using a cached copy of $@; \
	fi

rcut: rcut.c
	$(CC) $(CFLAGS) rcut.c -o rcut
