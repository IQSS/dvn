********************* Awstats for Dataverse Network(DVN) Readme **********************************

Awstats is the application that DVN uses for producing web statistics. To deploy awstats for the DVN,
first deploy this WAR on the glassfish server where DVN is housed.

After Awstats has been deployed, it must be configured for the targetted context. Follow the steps below.

The files contained in the etc directory have been configured for their respective servers so that 
the file titled awstats.mit.conf.dvn-qa1 is setup for use on the domain dvn-qa1.hmdc.harvard.edu. 
The path example below points to the DVN install location as of August 2007. In the future,
this may need to be edited to reflect any change in the default installation location of the
dvn web tier.

To use the file(s):

1.  Edit the last extension from the file name ...

    awstats.mit.conf.dvn-qa1 becomes awstats.mit.conf
    awstats.iqss.conf.dvn-qa1 becomes awstats.iqss.conf

2.  Drop the conf file in the awstats directory for the DVN... 

<dvn server> /usr/local/glassfish/domains/domain1/applications/j2ee-modules/DvnAwstats/cgi/awstats

*******************************************************************************************