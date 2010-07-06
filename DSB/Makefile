INSTALL_ROOT = /usr/local/VDC
TMPDIR = /tmp/VDC

all: 	build

clean:
	for dir in rcut; \
	do (cd $$dir; make clean); done
	/bin/rm -f `find . -name '*~'`

install: 

uninstall: 
	/bin/rm -rf ${RPM_PREFIX}/${INSTALL_ROOT}
	/bin/rm -rf ${RPM_PREFIX}/${TMPDIR}

build: inst_rcut inst_cgi-bin inst_etc inst_perl inst_R inst_sbin inst_xslt inst_tmp

build-mini: inst_etc_mini inst_perl_mini inst_R_mini inst_sbin_mini

inst_etc_mini: 
	install -m 0775 -d ${RPM_PREFIX}/${INSTALL_ROOT}/etc
	install -m 0775 etc/Rmodules.txt ${RPM_PREFIX}/${INSTALL_ROOT}/etc

inst_perl_mini:	inst_etc_mini
	install -m 0775 -d ${RPM_PREFIX}/${INSTALL_ROOT}/perl
	install -m 0775 -d ${RPM_PREFIX}/${INSTALL_ROOT}/perl/lib
	install -m 0775 -d ${RPM_PREFIX}/${INSTALL_ROOT}/perl/lib/VDC
	install -m 0775 -d ${RPM_PREFIX}/${INSTALL_ROOT}/perl/lib/Parse
	install -m 0775 -d ${RPM_PREFIX}/${INSTALL_ROOT}/perl/lib/VDC/Ingest
	install -m 0775  perl/lib/VDC/DSB/Ingest/SPS.pm ${RPM_PREFIX}/${INSTALL_ROOT}/perl/lib/VDC/DSB/Ingest
	install -m 0775  perl/lib/VDC/DSB/Ingest/StatDataFileReaderFactory.pm ${RPM_PREFIX}/${INSTALL_ROOT}/perl/lib/VDC/DSB/Ingest
	install -m 0775  perl/lib/VDC/DSB/Ingest/StatData.pm ${RPM_PREFIX}/${INSTALL_ROOT}/perl/lib/VDC/DSB/Ingest
	install -m 0775  perl/lib/Parse/RecDescent.pm ${RPM_PREFIX}/${INSTALL_ROOT}/perl/lib/Parse
	install -m 0775  perl/lib/Parse/RecDescent.pod ${RPM_PREFIX}/${INSTALL_ROOT}/perl/lib/Parse

inst_R_mini:
	install -m 0775 -d ${RPM_PREFIX}/${INSTALL_ROOT}
	install -m 0775 -d ${RPM_PREFIX}/${INSTALL_ROOT}/R
	install -m 0775 -d ${RPM_PREFIX}/${INSTALL_ROOT}/R/library
	install -m 0775 R/installR.sh ${RPM_PREFIX}/${INSTALL_ROOT}/R
	install -m 0775 R/library/vdc_startup.R ${RPM_PREFIX}/${INSTALL_ROOT}/R/library

inst_sbin_mini: 	
	install -m 0775 -d ${RPM_PREFIX}/${INSTALL_ROOT}/sbin
	install -m 0775 perl/sbin/DDIsec4sps.pl ${RPM_PREFIX}/${INSTALL_ROOT}/sbin

inst_rcut: 
	(cd rcut; make VDC_DIR=${RPM_PREFIX}/${INSTALL_ROOT})

inst_cgi-bin:
	install -m 0775 -d ${RPM_PREFIX}/${INSTALL_ROOT}/cgi-bin
	install -m 0775 perl/bind.cgi ${RPM_PREFIX}/${INSTALL_ROOT}/cgi-bin
	install -m 0775 perl/dispatch.pl ${RPM_PREFIX}/${INSTALL_ROOT}/cgi-bin
	install -m 0775 perl/dmpConfigZelig.pl ${RPM_PREFIX}/${INSTALL_ROOT}/cgi-bin
	install -m 0775 perl/nph-dmpData.pl ${RPM_PREFIX}/${INSTALL_ROOT}/cgi-bin
	install -m 0775 perl/nph-dmpJpg.pl ${RPM_PREFIX}/${INSTALL_ROOT}/cgi-bin
	install -m 0775 perl/nph-error.cgi ${RPM_PREFIX}/${INSTALL_ROOT}/cgi-bin
	install -m 0775 perl/univar03.cgi ${RPM_PREFIX}/${INSTALL_ROOT}/cgi-bin

inst_etc: 
	install -m 0775 -d ${RPM_PREFIX}/${INSTALL_ROOT}/etc
	install -m 0775 etc/Rmodules.txt ${RPM_PREFIX}/${INSTALL_ROOT}/etc
	install -m 0775 etc/*.tmpl ${RPM_PREFIX}/${INSTALL_ROOT}/etc
	install -m 0775 etc/README.* ${RPM_PREFIX}/${INSTALL_ROOT}/etc
	install -m 0775 etc/iptables.sample ${RPM_PREFIX}/${INSTALL_ROOT}/etc
	install -m 0775 etc/configure.perl ${RPM_PREFIX}/${INSTALL_ROOT}/etc

inst_perl:	inst_etc
	install -m 0775 -d ${RPM_PREFIX}/${INSTALL_ROOT}/perl
	install -m 0775 perl/dispatch.pl ${RPM_PREFIX}/${INSTALL_ROOT}/perl
	install -m 0775 perl/startup.pl ${RPM_PREFIX}/${INSTALL_ROOT}/perl
	install -m 0775 perl/vdcCGI.pm ${RPM_PREFIX}/${INSTALL_ROOT}/perl
	install -m 0775 perl/VDCdiag.pm ${RPM_PREFIX}/${INSTALL_ROOT}/perl
	install -m 0775 perl/vdcLOG.pm ${RPM_PREFIX}/${INSTALL_ROOT}/perl
	install -m 0775 perl/vdcLWP.pm ${RPM_PREFIX}/${INSTALL_ROOT}/perl
	cp -R perl/lib ${RPM_PREFIX}/${INSTALL_ROOT}/perl
	cp -R perl/vdcRAP ${RPM_PREFIX}/${INSTALL_ROOT}/perl
	(cd ${RPM_PREFIX}/${INSTALL_ROOT}/perl; ln -s ../etc/glv03)

inst_R:
	install -m 0775 -d ${RPM_PREFIX}/${INSTALL_ROOT}
	cp -R R ${RPM_PREFIX}/${INSTALL_ROOT}
	install -m 0775 installR.sh ${RPM_PREFIX}/${INSTALL_ROOT}/R

inst_sbin: 	
	install -m 0775 -d ${RPM_PREFIX}/${INSTALL_ROOT}/sbin
	install -m 0775 perl/sbin/readdta.pl ${RPM_PREFIX}/${INSTALL_ROOT}/sbin
	install -m 0775 perl/sbin/readpor.pl ${RPM_PREFIX}/${INSTALL_ROOT}/sbin
	install -m 0775 perl/sbin/readsav.pl ${RPM_PREFIX}/${INSTALL_ROOT}/sbin
	install -m 0775 perl/sbin/DDIsec4sps.pl ${RPM_PREFIX}/${INSTALL_ROOT}/sbin
	install -m 0775 perl/sbin/recreatevlm.pl ${RPM_PREFIX}/${INSTALL_ROOT}/sbin

inst_xslt: 
	install -m 0775 -d ${RPM_PREFIX}/${INSTALL_ROOT}/xslt
	install -m 0775 xslt/rcrtcl_tab.xsl ${RPM_PREFIX}/${INSTALL_ROOT}/xslt
	install -m 0775 xslt/rcrtcl.xsl ${RPM_PREFIX}/${INSTALL_ROOT}/xslt

inst_tmp: 
	install -m 0777 -d ${RPM_PREFIX}/${TMPDIR}
	install -m 0777 -d ${RPM_PREFIX}/${TMPDIR}/DSB
	install -m 0777 -d ${RPM_PREFIX}/${TMPDIR}/webtemp

#rpm:	clean update-spec
rpm:	clean
	/bin/rm -rf `find . -type d -name '.svn'`
	chown -R root.root . 
	(cd ..; ln -s DSB DVN-DSB-`cat DSB/VERSION`; \
	tar chzf /usr/src/redhat/SOURCES/DVN-DSB-`cat DSB/VERSION`.tar.gz DVN-DSB-`cat DSB/VERSION`/; /bin/rm DVN-DSB-`cat DSB/VERSION`)
	rpmbuild -ba dvn-dsb.spec
	#rpmbuild -bb --target i686 dvn-dsb.spec
	#rpmbuild -bb --target x86_64 dvn-dsb.spec


rpm-mini:	clean
	/bin/rm -rf `find . -type d -name '.svn'`
	chown -R root.root . 
	(cd ..; ln -s DSB DVN-DSB-`cat DSB/VERSION`; \
	tar chzf /usr/src/redhat/SOURCES/DVN-DSB-`cat DSB/VERSION`.tar.gz DVN-DSB-`cat DSB/VERSION`/; /bin/rm DVN-DSB-`cat DSB/VERSION`)
	rpmbuild -ba dvn-dsb-mini.spec

update-spec:	dvn-dsb.spec
	./update-spec.pl dvn-dsb.spec
