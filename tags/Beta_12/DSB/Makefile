
#
# Global rule.  Build the Makefiles if needed and then build the
# libraries and agents.
#

SUBDIRS = Ingest VERBS cgi XSLT R

all: Make.rules
	@sh -c 'for i in $(SUBDIRS); do \
	(cd $$i; echo  "making all in" "(`pwd`)"; \
	$(MAKE) all ) done '

meDIR           = DSB
meRPM		= VDC-DSB
meSPC	        = vdc-dsb.spec

#
# general routines - to be implemented in Makefiles in each
# subdirectory
#


clean:
	@sh -c 'for i in $(SUBDIRS); do \
	(cd $$i; echo  "making clean in" "(`pwd`)"; \
	$(MAKE) clean ) done '

install: all
	@sh -c 'for i in $(SUBDIRS); do \
	(cd $$i; echo  "making install in" "(`pwd`)"; \
	$(MAKE) RPM_PREFIX=${RPM_PREFIX} install ) done '

Make.rules:
	@if -f ../Make.rules \
	then; \
		cp ../Make.rules $@; \
	else \
		echo using a cached copy of $@; \
	fi

rpm: all clean update-spec
	(cd ..; ln -s DSB VDC-DSB-`cat VERSION`; \
	tar chzf /usr/src/redhat/SOURCES/VDC-DSB-`cat VERSION`.tar.gz VDC-DSB-`cat VERSION`/; /bin/rm VDC-DSB-`cat VERSION`)
	rpmbuild -ba --target i586 $(meSPC).daily
	#rpmbuild -bb --target i686 $(meSPC).daily
	#rpmbuild -bb --target x86_64 $(meSPC).daily
	@echo "don't forget to run cvs commit to properly update the build version!"

rpm-daily: all clean
	/bin/cp $(meSPC) $(meSPC).daily
	../update-spec.pl --build=`date +%Y%m%d` $(meSPC).daily
	(cd ..; ln -s $(meDIR) $(meRPM)-`cat VERSION`; \
	tar chzf /usr/src/redhat/SOURCES/$(meRPM)-`cat VERSION`.tar.gz $(meRPM)-`cat VERSION`/; /bin/rm $(meRPM)-`cat VERSION`)
	rpmbuild -ba --target i586 $(meSPC).daily
	#rpmbuild -bb --target i686 $(meSPC).daily
	#rpmbuild -bb --target x86_64 $(meSPC).daily

update-spec:	vdc-dsb.spec
	../update-spec.pl vdc-dsb.spec


