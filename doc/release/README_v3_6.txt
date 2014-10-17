Welcome to the Dataverse Network application v. 3.6! 
==============================================================

For a list and description of new features in this release, please consult

http://thedata.org/pages/latest-release

Please note that Digital Object Identifiers are supported in this release.
Please consult the Intallers' Guide for instructions on how to implement this feature in your application.

Installing the DVN. 
==================

If this is a new installation:
-----------------------------

A scripted installer is provided. Please consult the Installers Guide at

http://thedata.harvard.edu/guides/dataverse-installer-main.html

or

https://github.com/IQSS/dvn/blob/develop/doc/sphinx/source/dataverse-installer-main.rst

The installer package itself can be found here:

https://sourceforge.net/projects/dvn/files/dvn/3.6/dvninstall_v3_6.zip.

If you are upgrading an existing DVN v3.5  installation:
---------------------------------------------------------

1. [IMPORTANT!] Download the dvn_domain_upgrade.zip and unpack its 
contents in your Glassfish domain directory. This will install the DVN 
documentation (starting with this release it will be embedded with the 
application) and the new R libraries used for the data analysis.

2. We recommend the following change to the Glassfish logging 
configuration, to suppress the unnecessarily large amount of debugging 
messages on startup (a side effect of one of the 3rd party components added
to the DVN in 3.6). Add this line: 

javax.enterprise.system.std.com.sun.enterprise.server.logging.level=WARNING

to the file logging.properties in your <DOMAIN>/config directory. 

3. Download the .war file and SQL database update script
(DVN-web_v3_6.war and buildupdate_v3_5_1_v3_6.sql).

4. Deploy the application .war file.

5. Run the database update script. For the database update
we recommend using pgAdmin application.

You may also run the script on the command line using the psql utility
(as root):
su - postgres
psql -d <YOUR DATABASE NAME> -f buildupdate_v3_5_1_v3_6.sql

(And if you are upgrading from a version of the DVN earlier than 3.5.1,
please also consult the README files for all the versions in between!)

6. By default, the new SWORDv2-based Data Deposit API does not limit the
size up files uploaded by clients. To limit file upload to 2 GB, for example
use the new "dvn.dataDeposit.maxUploadInBytes" JVM option in your domain.xml
like this:

<jvm-options>-Ddvn.dataDeposit.maxUploadInBytes=2147483648</jvm-options>

7. Installations of DVN 3.5.1 and earlier may have incorrect case for the
part of the "vdc.temp.file.dir" JVM option. In domain.xml, if you see
"instanceroot" rather than "instanceRoot" for this option, please use
camelCase version such that your final line in domain.xml looks something
like this:

<jvm-options>-Dvdc.temp.file.dir=${com.sun.aas.instanceRoot}/config/files/temp</jvm-options>

As always, if you have any questions and/or run into problems, please
contact us at dvn_support@help.hmdc.harvard.edu.
