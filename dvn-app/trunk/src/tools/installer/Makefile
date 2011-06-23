installer:	dvninstall.zip
	@touch $@

dvninstall.zip: dvninstall/domain.xml.TEMPLATE dvninstall/install dvninstall/pgdriver/postgresql-8.3-603.jdbc4.jar dvninstall/appdeploy/dist/DVN-EAR.ear ../../../working_directory/error.xsl ../../../working_directory/fgdc2ddi.xsl ../../../working_directory/graphml.props ../../../working_directory/header.xsl ../../../working_directory/jhove.conf ../../../working_directory/logging.properties ../../../working_directory/metadata.xsl ../../../working_directory/mif2ddi.xsl ../../../working_directory/neodb.props ../../../working_directory/oai_dc2ddi.xsl ../../../working_directory/oaicat.properties dvninstall/referenceData.sql
	@echo making installer...
	@/bin/cp ../../../working_directory/error.xsl ../../../working_directory/fgdc2ddi.xsl ../../../working_directory/graphml.props ../../../working_directory/header.xsl ../../../working_directory/jhove.conf ../../../working_directory/logging.properties ../../../working_directory/metadata.xsl ../../../working_directory/mif2ddi.xsl ../../../working_directory/neodb.props ../../../working_directory/oai_dc2ddi.xsl ../../../working_directory/oaicat.properties dvninstall/config
	@zip -r dvninstall.zip dvninstall
	@echo 
	@echo "Done!"


dvninstall/referenceData.sql:	../../DVN-EJB//src/conf/referenceData.sql
	@/bin/cp ../../DVN-EJB//src/conf/referenceData.sql dvninstall

dvninstall/appdeploy/dist/DVN-EAR.ear:	../../DVN-EAR/dist/DVN-EAR.ear
	@/bin/cp ../../DVN-EAR/dist/DVN-EAR.ear dvninstall/appdeploy/dist


../../DVN-EAR/dist/DVN-EAR.ear:
	@echo
	@echo "ERROR: can't find ear file (DVN-EAR.ear)!"
	@echo "Build the DVN ear file, then try again."
	@echo
	@exit 1
 
