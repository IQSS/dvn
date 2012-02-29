installer:	dvninstall.zip
	@touch $@

dvninstall.zip: dvninstall/domain.xml.TEMPLATE dvninstall/install dvninstall/pgdriver/postgresql-8.3-603.jdbc4.jar dvninstall/appdeploy/dist/DVN-EAR.ear ../../working_directory/error.xsl ../../working_directory/fgdc2ddi.xsl ../../working_directory/graphml.props ../../working_directory/header.xsl ../../working_directory/jhove.conf ../../working_directory/logging.properties ../../working_directory/metadata.xsl ../../working_directory/mif2ddi.xsl ../../working_directory/neodb.props ../../working_directory/oai_dc2ddi.xsl ../../working_directory/oaicat.properties dvninstall/referenceData.sql networkDataLIBS
	@echo making installer...
	@/bin/cp ../../working_directory/error.xsl ../../working_directory/fgdc2ddi.xsl ../../working_directory/graphml.props ../../working_directory/header.xsl ../../working_directory/jhove.conf ../../working_directory/logging.properties ../../working_directory/metadata.xsl ../../working_directory/mif2ddi.xsl ../../working_directory/neodb.props ../../working_directory/oai_dc2ddi.xsl ../../working_directory/oaicat.properties dvninstall/config
	@zip -r dvninstall.zip dvninstall
	@echo 
	@echo "Done!"


dvninstall/referenceData.sql:	../../src/DVN-EJB/src/conf/referenceData.sql
	@/bin/cp ../../src/DVN-EJB//src/conf/referenceData.sql dvninstall

dvninstall/appdeploy/dist/DVN-web.war:	../../src/DVN-web/dist/DVN-web.war
	@/bin/cp ../../src/DVN-web/dist/DVN-web.war dvninstall/appdeploy/dist


../../src/DVN-web/dist/DVN-web.war:
	@echo
	@echo "ERROR: can't find application .war file (DVN-web.war)!"
	@echo "Build the DVN war file, then try again."
	@echo
	@exit 1
 
networkDataLIBS: ../../lib/dvn-lib-NetworkData/collections-generic-4.01.jar ../../lib/dvn-lib-NetworkData/colt-1.2.0.jar ../../lib/dvn-lib-NetworkData/concurrent-1.3.4.jar ../../lib/dvn-lib-NetworkData/geronimo-jta_1.1_spec-1.1.1.jar ../../lib/dvn-lib-NetworkData/jung-algorithms-2.0.jar ../../lib/dvn-lib-NetworkData/jung-api-2.0.jar ../../lib/dvn-lib-NetworkData/jung-visualization-2.0.jar ../../lib/dvn-lib-NetworkData/junit-3.8.1.jar ../../lib/dvn-lib-NetworkData/neo4j-index-1.1.jar ../../lib/dvn-lib-NetworkData/neo4j-kernel-1.1.jar ../../lib/dvn-lib-NetworkData/neo4j-utils-1.1.jar ../../lib/dvn-lib-NetworkData/nestedvm-1.0.jar ../../lib/dvn-lib-NetworkData/network_utils-1.0-SNAPSHOT.jar ../../lib/dvn-lib-NetworkData/sqlite-jdbc-3.6.16.jar ../../lib/dvn-lib-NetworkData-LUCENE/lucene-core-2.9.2.jar
	@mkdir -p dvninstall/config/networkData/lib
	@/bin/cp ../../lib/dvn-lib-NetworkData/collections-generic-4.01.jar ../../lib/dvn-lib-NetworkData/colt-1.2.0.jar ../../lib/dvn-lib-NetworkData/concurrent-1.3.4.jar ../../lib/dvn-lib-NetworkData/geronimo-jta_1.1_spec-1.1.1.jar ../../lib/dvn-lib-NetworkData/jung-algorithms-2.0.jar ../../lib/dvn-lib-NetworkData/jung-api-2.0.jar ../../lib/dvn-lib-NetworkData/jung-visualization-2.0.jar ../../lib/dvn-lib-NetworkData/junit-3.8.1.jar ../../lib/dvn-lib-NetworkData/neo4j-index-1.1.jar ../../lib/dvn-lib-NetworkData/neo4j-kernel-1.1.jar ../../lib/dvn-lib-NetworkData/neo4j-utils-1.1.jar ../../lib/dvn-lib-NetworkData/nestedvm-1.0.jar ../../lib/dvn-lib-NetworkData/network_utils-1.0-SNAPSHOT.jar ../../lib/dvn-lib-NetworkData/sqlite-jdbc-3.6.16.jar ../../lib/dvn-lib-NetworkData-LUCENE/lucene-core-2.9.2.jar dvninstall/config/networkData/lib
