********************* Awstats Configuration Files Readme **********************************

The files contained in this folder have been configured for their respective servers so that 
the file titled awstats.mit.conf.dvn-qa1 is setup for use on the domain dvn-qa1.hmdc.harvard.edu. 
The path example below points to the DVN install location as of August 2007. In the future,
this may need to be edited to reflect any change in the default installation location of the
dvn web tier.

To use the file(s):

1.  Edit the last extension from the file name ...

    awstats.mit.conf.dvn-qa1 becomes awstats.mit.conf

2.  Drop the conf file in the awstats directory for the DVN... 

<dvn server> /usr/local/glassfish/domains/domain1/applications/j2ee-modules/DvnAwstats/cgi/awstats

*******************************************************************************************