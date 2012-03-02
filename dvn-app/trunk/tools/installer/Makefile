installer:	dvninstall.zip
	@touch $@

DVN_WAR_FILE=dvninstall/appdeploy/dist/DVN-web.war

DVN_CONFIG_FILES=dvninstall/config/error.xsl dvninstall/config/fgdc2ddi.xsl dvninstall/config/graphml.props dvninstall/config/header.xsl dvninstall/config/jhove.conf dvninstall/config/logging.properties dvninstall/config/metadata.xsl dvninstall/config/mif2ddi.xsl dvninstall/config/neodb.props dvninstall/config/oai_dc2ddi.xsl dvninstall/config/oaicat.properties

DVN_SQL_FILES=dvninstall/referenceData.sql dvninstall/ejbtimer.sql dvninstall/buildupdate_v2_2_5_v3_0.sql

DVN_NETWORKDATA_LIBS=dvninstall/config/networkData/lib/collections-generic-4.01.jar dvninstall/config/networkData/lib/colt-1.2.0.jar dvninstall/config/networkData/lib/concurrent-1.3.4.jar dvninstall/config/networkData/lib/geronimo-jta_1.1_spec-1.1.1.jar dvninstall/config/networkData/lib/jung-algorithms-2.0.jar dvninstall/config/networkData/lib/jung-api-2.0.jar dvninstall/config/networkData/lib/jung-visualization-2.0.jar dvninstall/config/networkData/lib/junit-3.8.1.jar dvninstall/config/networkData/lib/lucene-core-2.9.2.jar dvninstall/config/networkData/lib/neo4j-index-1.1.jar dvninstall/config/networkData/lib/neo4j-kernel-1.1.jar dvninstall/config/networkData/lib/neo4j-utils-1.1.jar dvninstall/config/networkData/lib/nestedvm-1.0.jar dvninstall/config/networkData/lib/network_utils-1.0-SNAPSHOT.jar dvninstall/config/networkData/lib/sqlite-jdbc-3.6.16.jar

DVN_INSTALLER_SCRIPT=dvninstall/install

DVN_CONFIG_TEMPLATES=dvninstall/domain.xml.TEMPLATE

DVN_DEPLOYMENT_FRAMEWORK=dvninstall/appdeploy/ant-deploy.xml dvninstall/appdeploy/build-impl.xml dvninstall/appdeploy/build.xml dvninstall/appdeploy/glassfish.properties.TEMPLATE dvninstall/appdeploy/private.properties dvninstall/appdeploy/project.properties dvninstall/appdeploy/AS.properties.TEMPLATE 

DVN_POSTGRES_DRIVER=dvninstall/pgdriver/postgresql-8.3-603.jdbc4.jar

${DVN_WAR_FILE}:	../../src/DVN-web/dist/DVN-web.war
	@/bin/cp ../../src/DVN-web/dist/DVN-web.war dvninstall/appdeploy/dist


../../src/DVN-web/dist/DVN-web.war:
	@echo
	@echo "ERROR: can't find application .war file (DVN-web.war)!"
	@echo "Build the DVN war file, then try again."
	@echo
	@exit 1

#@zip -r dvninstall.zip dvninstall
dvninstall.zip: ${DVN_CONFIG_TEMPLATES} ${DVN_INSTALLER_SCRIPT} ${DVN_POSTGRES_DRIVER} ${DVN_WAR_FILE} ${DVN_CONFIG_FILES} ${DVN_SQL_FILES} ${DVN_NETWORKDATA_LIBS} ${DVN_DEPLOYMENT_FRAMEWORK}
	@echo making installer...
	@zip dvninstall.zip ${DVN_CONFIG_TEMPLATES} ${DVN_INSTALLER_SCRIPT} ${DVN_POSTGRES_DRIVER} ${DVN_WAR_FILE} ${DVN_CONFIG_FILES} ${DVN_SQL_FILES} ${DVN_NETWORKDATA_LIBS} ${DVN_DEPLOYMENT_FRAMEWORK}
	@echo 
	@echo "Done!"

${DVN_SQL_FILES}:	../../src/DVN-EJB/src/conf/referenceData.sql ../../src/DVN-EJB/src/conf/ejbtimer.sql ../../src/DVN-EJB/src/conf/buildupdate_v2_2_5_v3_0.sql
	@/bin/cp ../../src/DVN-EJB/src/conf/referenceData.sql dvninstall
	@/bin/cp ../../src/DVN-EJB/src/conf/ejbtimer.sql dvninstall
	@/bin/cp ../../src/DVN-EJB/src/conf/buildupdate_v2_2_5_v3_0.sql dvninstall

${DVN_CONFIG_FILES}: 	../../working_directory/error.xsl ../../working_directory/fgdc2ddi.xsl ../../working_directory/graphml.props ../../working_directory/header.xsl ../../working_directory/jhove.conf ../../working_directory/logging.properties ../../working_directory/metadata.xsl ../../working_directory/mif2ddi.xsl ../../working_directory/neodb.props ../../working_directory/oai_dc2ddi.xsl ../../working_directory/oaicat.properties
		@/bin/cp ../../working_directory/error.xsl ../../working_directory/fgdc2ddi.xsl ../../working_directory/graphml.props ../../working_directory/header.xsl ../../working_directory/jhove.conf ../../working_directory/logging.properties ../../working_directory/metadata.xsl ../../working_directory/mif2ddi.xsl ../../working_directory/neodb.props ../../working_directory/oai_dc2ddi.xsl ../../working_directory/oaicat.properties dvninstall/config

${DVN_NETWORKDATA_LIBS}: ../../lib/dvn-lib-NetworkData/collections-generic-4.01.jar ../../lib/dvn-lib-NetworkData/colt-1.2.0.jar ../../lib/dvn-lib-NetworkData/concurrent-1.3.4.jar ../../lib/dvn-lib-NetworkData/geronimo-jta_1.1_spec-1.1.1.jar ../../lib/dvn-lib-NetworkData/jung-algorithms-2.0.jar ../../lib/dvn-lib-NetworkData/jung-api-2.0.jar ../../lib/dvn-lib-NetworkData/jung-visualization-2.0.jar ../../lib/dvn-lib-NetworkData/junit-3.8.1.jar ../../lib/dvn-lib-NetworkData/neo4j-index-1.1.jar ../../lib/dvn-lib-NetworkData/neo4j-kernel-1.1.jar ../../lib/dvn-lib-NetworkData/neo4j-utils-1.1.jar ../../lib/dvn-lib-NetworkData/nestedvm-1.0.jar ../../lib/dvn-lib-NetworkData/network_utils-1.0-SNAPSHOT.jar ../../lib/dvn-lib-NetworkData/sqlite-jdbc-3.6.16.jar ../../lib/dvn-lib-NetworkData-LUCENE/lucene-core-2.9.2.jar
	@mkdir -p dvninstall/config/networkData/lib
	@/bin/cp ../../lib/dvn-lib-NetworkData/collections-generic-4.01.jar ../../lib/dvn-lib-NetworkData/colt-1.2.0.jar ../../lib/dvn-lib-NetworkData/concurrent-1.3.4.jar ../../lib/dvn-lib-NetworkData/geronimo-jta_1.1_spec-1.1.1.jar ../../lib/dvn-lib-NetworkData/jung-algorithms-2.0.jar ../../lib/dvn-lib-NetworkData/jung-api-2.0.jar ../../lib/dvn-lib-NetworkData/jung-visualization-2.0.jar ../../lib/dvn-lib-NetworkData/junit-3.8.1.jar ../../lib/dvn-lib-NetworkData/neo4j-index-1.1.jar ../../lib/dvn-lib-NetworkData/neo4j-kernel-1.1.jar ../../lib/dvn-lib-NetworkData/neo4j-utils-1.1.jar ../../lib/dvn-lib-NetworkData/nestedvm-1.0.jar ../../lib/dvn-lib-NetworkData/network_utils-1.0-SNAPSHOT.jar ../../lib/dvn-lib-NetworkData/sqlite-jdbc-3.6.16.jar ../../lib/dvn-lib-NetworkData-LUCENE/lucene-core-2.9.2.jar dvninstall/config/networkData/lib

