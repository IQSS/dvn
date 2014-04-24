This README is to document the ungoing process of upgrading to Java EE7/GF4. 

1. Installing jdk 1.7 and Glassfish 4: 

Install jdk 1.7/netbeans 7.4 bundle (get it here:
http://www.oracle.com/technetwork/java/javase/downloads/index.html)
and Glassfish 4 (https://glassfish.java.net/download.html). Glassfish
installation got simpler - there's no installer to run, just download
the zip file and unpack it where you want it, like /usr/local - that
will create /usr/local/glassfish4, with a pre-configured domain ready
to start with asadmin domain-start.

Start the domain. Go to https://localhost:4848 and set the admin password. 

When you start Netbeans 7.4, answer 'no' when asked whether to import 
your existing settings and projects. 


2. Building the application: 

Use the pom.xml file in this directory. 

You don't need one for DVN-root; just put it into DVN-web, and open
DVN-web maven project from Netbeans, without touching DVN-root.

You may want to erase everything in your ~/.m2/repository/* - that's
where maven keeps the downloaded jars - to ensure a clean
experiment. The project will open with errors ("some files are not in
the local repository"), go ahead and do clean and build - and that
will make it repopulate your ~/.m2/repository.

Before you try to deploy, do the following: 

Stop glassfish with asadmin; 

a. Install the postgres driver: 
   Just use the one you've been using with GF3: 
   (for example)
   cp /Applications/NetBeans/glassfish-3.1.2/glassfish/lib/postgresql-9.1-902.jdbc4.jar /usr/local/glassfish4/glassfish/lib

b. Install javassist jar: (needed by Weld)

   Download javassist-3.1.jar from here: 

   http://repo1.maven.org/maven2/jboss/javassist/3.1/javassist-3.1.jar

   put the jar into <GLASSFISH LOCATION>/glassfish/lib 

c. Upgrade Weld to 2.0.2 (you really want 2.0.2; do not upgrade to 2.1.0, etc.) 

   Download weld-osgi-bundle-2.0.2.Final.jar from here: 

   http://repo1.maven.org/maven2/org/jboss/weld/weld-osgi-bundle/2.0.2.Final/weld-osgi-bundle-2.0.2.Final.jar

   rename it weld-osgi-bundle.jar and place it into <GLASSFISH LOCATION>/glassfish/modules. 

c. Install domain.xml from this directory.

   (it has the pools and connections pre-configured; assumes the default dvnDb database, user dvnApp, etc.)

   save your domain.xml somewhere (as domain.xml.PRESERVED, for example); then put mine in its place: 

   cd <DOMAIN_DIR>/config
   cp domain.xml domain.xml.PRESERVED
   cp <THIS DIRECTORY>/domain.xml . 

   *Before starting glassfish again, edit this domain.xml*, and replace the line:

   <secure-admin special-admin-indicator="4de31578-ac8c-49cf-884e-77faada83599">

   with the one from your original domain.xml. Start glassfish.


3. Deployment - encountered problems and solutions: 

<to be filled with problems already solved>

Latest problem: 

Error occurred during deployment: Exception while loading the app : java.lang.IllegalStateException: ContainerBase.addChild: start: org.apache.catalina.LifecycleException: java.lang.RuntimeException: com.sun.faces.config.ConfigurationException: Factory 'javax.faces.lifecycle.ClientWindowFactory' was not configured properly..
