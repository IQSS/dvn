(this is an addendum for the main DVN installation manual;
please make sure you read the whole thing first). 

This README contains some extra installation tips:

I. Configuring both the main application (DVN) and the DSB
component on the same server.

The DSB (Data Services Broker) performs some of the
more CPU-intensive tasks of our system, so separating the 2
components to run on 2 different servers is a natural
scaling choice when overall performance becomes critical. 

It is however hardly necessary unless the DVN is indeed
being used in a busy, production setting. So for trial-like
installations you will likely choose to run both things on
the same machine. Below are some tips for this type of use
case. 

1. Choice of ports

The DVN runs under Glassfish and DSB under httpd/apache. So
the 2 services need to be configured to run on different
ports. Since the DVN is the front end of the system, you
will likely want to run it on the default HTTP port (80) and
choose a custom port for httpd. 
Here's how to proceed: 

a. If you already have httpd running on port 80, stop
it;
b. Follow the steps from the DVN installation manual to
configure glassfish to run the application on port 80;
c. Make sure the JVM option -Dvdc.dsb.port is set to the
custom port you chose for the DSB;
d. Edit the apache configuration file
/etc/httpd/conf/httpd.conf and modify the line 

Listen 80

to specify the port you chose (for ex., "Listen 8080"). 
e. Follow the instructions to install the DSB rpm. The
installer will try to detect if httpd is configured to 
run on a non-default port and configure the component
appropriately (it will print a message informing you of 
the settings detected). However, if after the installation
you are having problems connecting to the DSB, please check
that the DSB's configuration file
(/usr/local/VDC/etc/vdc.conf) has the VirtualHost line that
specifies the same port as the one on the "Listen" line in
the main httpd.conf, above. For example: 

<VirtualHost *:8080>


II. Activating the variable metadata access optimization.

This a new feature in the DSB: instead of receiving the
variable-level metadata from the application in the
XML-ized, DDI format, the component can now access it
directly in the application databases. Because this
mechanism is fairly new, it is disabled by default. It
offers a substantial performance increase though, so we
recommend activating it. To do so, please edit the file
/usr/local/VDC/etc/glv03 and set $USE_SQL_DIRECT to 1; then
modify the following lines:

our($SQLHOST) = "dvn.example.edu"; 
our($SQLPORT) = 5432;
our($SQLDB)   = "dvnDb";
our($SQLUSER) = "dvnApp";
our($SQLPW)   = "xxxxxx";

(use the same SQL access settings that you already have in
your Glassfish domain.xml file). 

Note that if you are running the DVN and the DSB on the same
server (as described above in I.), you will most likely want
to set $SQLHOST to blank string: 

our($SQLHOST) = ""; 
