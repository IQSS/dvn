====================================
APIs Guide
====================================

.. _api:

**Introduction**

We strongly encourage anyone interested in building tools to
interoperate with the Dataverse Network to utilize our open source
APIs. Please visit our `website <http://thedata.org/book/apps>`__  for
examples of external apps that have been built to work with our APIs.

.. _data-sharing-api:

Data Sharing API
++++++++++++++++++++++++++

As of version 3.0, a new API for programmatic access to the DVN data and
metadata has been added. The API allows a remote, non-DVN
archive/application to search the holdings and download files from a
Dataverse Network.

The Data Sharing API documentation is available below:

API URLs
====================

The URLs for the Data Sharing API resources are of the form:

``/dvn/api/{/arg}{?{{arg}&...}}``

Generally, mandatory arguments are embedded in the URL and optional
arguments are supplied as query parameters, in the ``?param=...`` notation.
See the documentation for the individual resources below for details.

The API supports basic HTTP Authentication. So that the access
credentials are not transmitted in the clear, the API verbs (methods)
below are **only accessible over HTTPS**.

Metadata API
==========================

The API for accessing Dataverse Network metadata is implemented in 4 verbs
(resources):

| ``metadataSearchFields`` 
| ``metadataSearch`` 
| ``metadataFormatsAvailable`` 
| ``metadata``

metadataSearchFields
----------------------------------

**Arguments:** 

``none``

**URL example:** 

``/dvn/api/metadataSearchFields/``

**Output:** 

XML record in the format below: 

.. code-block:: guess

	<MetadataSearchFields>
	<SearchableField>
	<fieldName>title</fieldName>
	<fieldDescription>title</fieldDescription>
	</SearchableField>
	<SearchableField>
	<fieldName>authorName</fieldName>
	<fieldDescription>authorName</fieldDescription>
	</SearchableField>
	<SearchableField>
	<fieldName>otherId</fieldName>
	<fieldDescription>otherId</fieldDescription>
	</SearchableField>
	...
	</MetadataSearchableFields>

metadataSearch
------------------------------------

**Arguments:**

| ``queryString: mandatory, embedded.``
| *Standard Lucene-style search queries are supported; (same query format currently used to define OAI sets, etc.)*

**URLs examples:**

| ``/dvn/api/metadataSearch/title:test``
| ``/dvn/api/metadataSearch/title:test AND authorName:leonid``

**Output:**

XML record in the format below:

.. code-block:: guess

	<MetadataSearchResults>
	<searchQuery>title:test</searchQuery>
	<searchHits>
	<study ID="hdl:TEST/10007"/>
	...
	</searchHits>
	</MetadataSearchResults>

**Error Conditions:**

Note that when the query does not produce any results, the resource returns an XML record
with an empty ``<searchHits>`` list, NOT a 404.

metadataFormatsAvailable
--------------------------------------

**Arguments:**

| ``objectId: mandatory, embedded.``
| *Both global and local (database) IDs are supported.*

**URLs examples:**
 
| ``/dvn/api/metadataFormatsAvailable/hdl:1902.1/6635``
| ``/dvn/api/metadataFormatsAvailable/9956``

**Output:** 

XML record in the format below:

.. code-block:: guess

	<MetadataFormatsAvailable studyId="hdl:TEST/10007">
	<formatAvailable selectSupported="true" excludeSupported="true">
	<formatName>ddi</formatName>
	<formatSchema>http://www.icpsr.umich.edu/DDI/Version2-0.xsd</formatSchema>
	<formatMime>application/xml</formatMime>
	</formatAvailable>
	<formatAvailable>
	<formatName>oai_dc</formatName>
	<formatSchema>http://www.openarchives.org/OAI/2.0/oai_dc.xsd</formatSchema>
	<formatMime>application/xml</formatMime>
	</formatAvailable>
	</MetadataFormatsAvailable> 

(**Note** the ``selectSupported`` and ``excludeSupported`` attributes above!)

**Error Conditions:**

``404 NOT FOUND`` if study does not exist

metadata
-------------------------

**Arguments:**

| ``objectId: mandatory, embedded.``
| *Both global and local (database) IDs are supported.*

| ``formatType: optional, query.`` 
| *Defaults to DDI if not supplied.*

**URLs examples:**

| ``/dvn/api/metadata/hdl:1902.1/6635 /dvn/api/metadata/9956``
| ``/dvn/api/metadata/hdl:1902.1/6635?formatType=ddi``

**Output:**

Metadata record in the format requested, if available. No extra
headers, etc.

**Partial selection of metadata sections:**

When requesting partial records is supported (see
``metadataFormatsAvailable``, above for more info), these additional parameters can be supplied:

| ``partialExclude: optional, query.``
| *Xpath query representing metadata section to drop, where supported.*

| ``partialInclude: optional, query.`` 
| *Xpath query representing metadata section to include, where supported.*

**Examples:**

| ``/dvn/api/metadata/hdl:1902.1/6635?formatType=ddi&partialExclude=codeBook/dataDscr``
| will produce a DDI without the dataDscr section. 
| *[I’m expecting this to be the single most useful and common real-life application of thisfeature - L.A.]*

| ``/dvn/api/metadata/hdl:1902.1/6635?formatType=ddi&partialInclude=codeBook/stdyDscr``
| will produce a DDI with the stdyDscr section only. 

(**Note**: for now, only simple top-level Xpath queries like the above are supported).

One other limitation of the current implementation: it does not validate the supplied ``partialExclude`` and ``partialInclude`` arguments; no error messages/diagnostics will be given if the Xpath queries are not part of the metadata schema. For example, if you request partialInclude=foobar, it will quietly produce an empty DDI, and ``partialExclude=foobar`` will not exclude anything (and you will get a complete DDI).

**Error Conditions:**

| ``404 NOT FOUND``
| if study does not exist

| ``503 SERVICE UNAVAILABLE``
| if study exists, but the format requested is not available; 
| also, when partial exclude or include is requested, if it’s not supported by the service (see the documenation for metadataFormatsAvailable above).

**Notes:**

A real-life workflow scenario may go as follows: 

a. Find the searchable index fields on this DVN (meatadataSearchFields)
b. Run a search (metadataSearch) 
c. For [select] studies returned, find what metadata formats are available (metadataFormatsAvailable) 
d. Retrieve the metadata in the desired format (metadata)

File Access API
=====================

The Dataverse Network API for downloading digital objects (files) is implemented in 2
verbs (resources): 

| ``downloadInfo`` 
| ``download``

downloadInfo
-----------------------------

**Arguments:**

| ``objectId: mandatory, embedded.``
| Database ID of the Dataverse Network Study File.

**URLs example:**

``/dvn/api/downloadInfo/9956``

**Output:**

XML record in the format below: 

*(Note: the record below is only an example; we will provide full schema/documentation of theFileDownloadInfo record format below)*

.. code-block:: guess

	<FileDownloadInfo>
	<studyFile fileId="9956">

	<fileName>prettypicture.jpg</fileName>
	<fileMimeType>image/jpeg</fileMimeType>
	<fileSize>52825</fileSize>

	<Authentication>
		<authUser>testUser</authUser>
		<authMethod>password</authMethod>
	</Authentication>

	<Authorization directAccess="true"/>

	<accessPermissions accessGranted="true">Authorized Access only</accessPermissions>

	<accessRestrictions accessGranted="true">Terms of Use</accessRestrictions>

	<accessServicesSupported>

		<accessService>
			<serviceName>thumbnail</serviceName>
			<serviceArgs>imageThumb=true</serviceArgs>
			<contentType>image/png</contentType>
			<serviceDesc>Image Thumbnail</serviceDesc>
		</accessService>

	</accessServicesSupported>
	</studyFile>
	</FileDownloadInfo>

**Error Conditions:**

| ``404 NOT FOUND`` 
| Study file does not exist.

download
---------------------------------

**Arguments:**

| ``objectId: mandatory, embedded.`` 
| Database ID of the DVN Study File.

| ``Optional Query args:``
| As specified in the output of downloadInfo, above.

**URLs examples:**
 
| ``/dvn/api/download/9956``
| ``/dvn/api/download/9956?imageThumb=true``
| ``/dvn/api/download/9957?fileFormat=stata``

**Output:**

Byte Stream (with proper HTTP headers specifying the content
type, file name and such)

**Error Conditions:**

| ``404 NOT FOUND`` 
| Study file does not exist.

| ``401 AUTHORIZATION REQUIRED``
| Access to restricted object attempted without HTTP Authorization header supplied.

| ``403 PERMISSION DENIED HTTP``
| Authorization header supplied, but the authenticated user is not
| authorized to directly access the object protected by Access
| Permissions and/or Access Restrictions (“Terms of Use”).

.. _data-deposit-api:

Data Deposit API
++++++++++++++++++++++++++++

As of version 3.6, a new API for programmatic deposit of data and metadata to the Dataverse Network will be added. The API will allow a remote, non-Dataverse Network archive/application to deposit files and metadata to a Dataverse Network installation.

The latest information on this plugin is available `here <https://redmine.hmdc.harvard.edu/issues/3108>`__.

Overview of Data Deposit API
================================

Project Sites
-----------------

`https://redmine.hmdc.harvard.edu/issues/3108 <https://redmine.hmdc.harvard.edu/issues/3108>`__

`http://projects.iq.harvard.edu/ojs-dvn/book/project-documentation <http://projects.iq.harvard.edu/ojs-dvn/book/project-documentation>`__

`http://projects.iq.harvard.edu/ojs-dvn/book/faq-ojs-dataverse-integration-project <http://projects.iq.harvard.edu/ojs-dvn/book/faq-ojs-dataverse-integration-project>`__

`http://pkp.sfu.ca/wiki/index.php?title=PKP/Dataverse\_Network\_Integration <http://pkp.sfu.ca/wiki/index.php?title=PKP/Dataverse_Network_Integration>`__

`http://projects.iq.harvard.edu/files/styles/os\_files\_xxlarge/public/ojs-dvn/files/whiteboardofswordv2api.jpg <http://projects.iq.harvard.edu/files/styles/os_files_xxlarge/public/ojs-dvn/files/whiteboardofswordv2api.jpg>`__
via
`http://projects.iq.harvard.edu/ojs-dvn/blog/update-deep-trenches-swordv2-api-development <http://projects.iq.harvard.edu/ojs-dvn/blog/update-deep-trenches-swordv2-api-development>`__

What is SWORD? - Simple Web-service Offering Repository Deposit
---------------------------------------------------------------

`http://swordapp.org <http://swordapp.org/>`__

`http://en.wikipedia.org/wiki/SWORD\_%28protocol%29 <http://en.wikipedia.org/wiki/SWORD_%28protocol%29>`__

Intro to SWORD v2 video by Cottage Labs
***********************************************************

`http://cottagelabs.com/news/intro-to-sword-2 <http://cottagelabs.com/news/intro-to-sword-2>`__

+----------+------------------------+------------------------+-------------------------------+-------------------------------------+---------------------+
|          | service-document-uri   | collection-uri         | edit-uri                      | edit-media-uri                      | statement-uri       |
+==========+========================+========================+===============================+=====================================+=====================+
| GET      | list of collections    | ?                      | representation of container   | download package                    | describes objects   |
+----------+------------------------+------------------------+-------------------------------+-------------------------------------+---------------------+
| POST     | ?                      | create new container   | ?                             | add content                         | ?                   |
+----------+------------------------+------------------------+-------------------------------+-------------------------------------+---------------------+
| PUT      | ?                      | ?                      | ?                             | replace content                     | ?                   |
+----------+------------------------+------------------------+-------------------------------+-------------------------------------+---------------------+
| DELETE   | ?                      | ?                      | delete container              | delete all content from container   | ?                   |
+----------+------------------------+------------------------+-------------------------------+-------------------------------------+---------------------+

`Direct data
download <http://devguide.thedata.org/features/api/data-deposit/swordv2-verbs.tsv>`__

-  GET the service-document-uri to retrieve a list of collections
-  POST a package of content to the server which will

   -  unpack it
   -  create an object (a container?) and put into it

      -  metadata
      -  files

   -  return a deposit receipt, which is an Atom entry, which contains
      three URIs:

      -  edit-uri
      -  edit-media-uri
      -  statement-uri

-  GET the edit-uri to retrieve the same deposit receipt from the
   original POST (a representation of the container)
-  GET the edit-media-uri to get a package of content, possibly in a zip
   file
-  GET the statement-uri to retrieve a document (OAI-ORE or Atom feed)
   that describes the structure of the objects on the server so we can
   build a representation of the object on the client end
-  PUT a new package on the edit-media URI to *replace* content
-  POST a package to the edit-uri *adds* new content
-  DELETE to the edit-media-uri deletes all the content from the
   container
-  DELETE to the edit-uri deletes the entire object


See also: `screenshots at Intro to SWORDv2 Cottage Labs video - Google
Drive <https://docs.google.com/a/g.harvard.edu/document/d/1oyz3ZTfZA_7FFNpZNaxR6cDJzLXgm3X1ybwUkMQwL1Q/edit>`__

SWORD v2 spec
----------------------------

| `http://swordapp.github.io/SWORDv2-Profile/SWORDProfile.html <http://swordapp.github.io/SWORDv2 Profile/SWORDProfile.html>`__
| via
| `http://swordapp.org/sword-v2/sword-v2-specifications/ <http://swordapp.org/sword-v2/sword-v2-specifications/>`__

DVN SWORD v2 implementation
------------------------------------

SWORD as it related to DVN and OJS
*************************************************


- Important DVN-OJS Google docs**

 -  `DVN Collaborations - OJS folder <https://drive.google.com/a/g.harvard.edu/?tab=mo#folders/0B5hBU9MLa_0KOFZDWFdrbVIydDA>`__

 -  `Use Cases / Workflows folder <https://drive.google.com/a/g.harvard.edu/?tab=mo#folders/0BzeLxEN77UZoSTUxVkxnemwyM0k>`__

    -  `Working Copy (updated workflow): PKP-Dataverse plugin Use Cases According to Article/Data Lifecycle v.2 (For Internal Use) <https://docs.google.com/document/d/1COP7Qg9XjPnaxTqG-dSgB8tq3X504QOCm9A7lk4Iqaw/edit?usp=sharing>`__

- `SWORD terminology as it related to DVN and OJS <https://docs.google.com/spreadsheet/ccc?key=0AvqMYwdHFZghdGFjenpFOXppQUg4djFOQlRJbW5EWmc&usp=sharing>`__

Status
***********

-  Status: Still lots of work to do... so far it allows you to...

   -  retrieve a SWORD service document after authenticating
   -  create a study based on metadata (title, author, etc.) in an XML
      file, i.e.
      `https://github.com/IQSS/dvn/blob/10ece42ec9236ccd2e58eea2e69c7b54fc783133/tools/scripts/data-deposit-api/atom-entry-study.xml <https://github.com/IQSS/dvn/blob/10ece42ec9236ccd2e58eea2e69c7b54fc783133/tools/scripts/data-deposit-api/atom-entry-study.xml>`__
   -  upload a file to a study (treated as binary for now)

-  Communication with OJS developers:
   `http://projects.iq.harvard.edu/ojs-dvn/people <http://projects.iq.harvard.edu/ojs-dvn/people>`__

   -  `DVN SWORDv2 implementation design document  <https://docs.google.com/document/d/1Sw8ZTjelFtWIi1etWqgQ0gPh6ltJuh8voX00WUi7Sng/edit?usp=sharing>`__
   - `Sample Atom Entry (SWORDv2) BTW OJS & DVN (with notes) <https://docs.google.com/document/d/1Rn70XItfA6_mJ4JA3I7m4em0spfdXIIHhwkrgCl40uE/edit?usp=sharing>`__
   -  `http://irclog.iq.harvard.edu/dvn/2013-07-02 <http://irclog.iq.harvard.edu/dvn/2013-07-02>`__
   -  `http://irclog.iq.harvard.edu/dvn/2013-07-10 <http://irclog.iq.harvard.edu/dvn/2013-07-10>`__

-  Next steps

   -  experiment with different files (RData, CSV, Stata, etc.) and
      ingest them
   -  improve mapping between dcterms (Dublic Core) and DDI
   -  deal with known bugs in the official SWORDv2 Java server library:
      `https://github.com/swordapp/JavaServer2.0 <https://github.com/swordapp/JavaServer2.0>`__
      (sword2-server-1.0-classes.jar) by committing to
      `https://github.com/IQSS/swordv2-java-server-library <https://github.com/IQSS/swordv2-java-server-library>`__
      and build sword2-server-1.0-classes.jar at
      `https://build.hmdc.harvard.edu:8443/job/swordv2-java-server-library-iqss/ <https://build.hmdc.harvard.edu:8443/job/swordv2-java-server-library-iqss/>`__

      -  first character truncated from filename:
         `https://github.com/swordapp/JavaServer2.0/pull/2 <https://github.com/swordapp/JavaServer2.0/pull/2>`__
      -  multipart deposit results in MalformedStreamException:
         `https://github.com/dvn/swordpoc/issues/2 <https://github.com/dvn/swordpoc/issues/2>`__

   -  force https
   -  merge
      `https://github.com/IQSS/dvn/tree/3108-data-deposit-api <https://github.com/IQSS/dvn/tree/3108-data-deposit-api>`__
      into "develop" branch

      -  
         `URL patterns in servlet mappings  <https://github.com/IQSS/dvn/blob/de603773c779a45f67b85908e8746b90c6e04661/src/DVN-web/web/WEB-INF/web.xml>`__

         -  ``/api/data-deposit/swordv2/service-document/*``
         -  ``/api/data-deposit/swordv2/collection/*``
         -  ``/api/data-deposit/swordv2/edit/*``
         -  ``/api/data-deposit/swordv2/edit-media/*``

      -  Many jars required (and more?):

         -  abdera-core-1.1.1.jar
         -  abdera-i18n-1.1.1.jar
         -  abdera-parser-1.1.1.jar
         -  axiom-api-1.2.10.jar
         -  axiom-impl-1.2.10.jar
         -  commons-fileupload-1.2.1.jar
         -  sword2-server-1.0-classes.jar
         -  xom-1.1.jar

-  Questions for the DVN team

   -  when should studies be released?
   -  when should a journal dataverse be released?
   -  how are we going to test the DVN SWORDv2 implemention?

      -  the SWORDv2 client at
         `https://github.com/swordapp/JavaClient2.0 <https://github.com/swordapp/JavaClient2.0>`__
         seems promising. Some trouble getting it to communicate over
         `HTTPS: <https:>`__ how to tell Abdera to ignore invalid cert
         on localhost -
         `http://mail-archives.apache.org/mod\_mbox/abdera-user/201307.mbox/%3CCAHxGWy7kwpTqg6M9m4-SetATeLn9uBu2%2BXwOaPwXZmLME1m3rg%40mail.gmail.com%3E <http://mail-archives.apache.org/mod_mbox/abdera-user/201307.mbox/%3CCAHxGWy7kwpTqg6M9m4-SetATeLn9uBu2%2BXwOaPwXZmLME1m3rg%40mail.gmail.com%3E>`__

   -  how are we going to test interoperability with the OJS plugin?

-  Questions for the SWORD community

   -  To upload a file, it is ok to continue to use the
      SWORDv2CollectionServlet (POST) or should we switch to the
      SWORDv2MediaResourceServlet (PUT)? A thread has been started on
      this: [sword-app-tech] POST atom entry, then PUT media resource -
      `http://www.mail-archive.com/sword-app-tech@lists.sourceforge.net/msg00331.html <http://www.mail-archive.com/sword-app-tech@lists.sourceforge.net/msg00331.html>`__

SWORD v2 server implementations
-----------------------------------

`http://swordapp.org/sword-v2/sword-v2-implementations/ <http://swordapp.org/sword-v2/sword-v2-implementations/>`__

Python
***********

- Simple Sword Server (reference implementation)


 - `https://github.com/swordapp/Simple-Sword-Server <https://github.com/swordapp/Simple-Sword-Server>`__


 - `https://github.com/dvn/swordpoc <https://github.com/dvn/swordpoc>`__ (Vagrant environment)


- `Databank <https://github.com/dataflow/RDFDatabank/wiki/SWORD-overview-for-developers>`__
- `OERPUB <https://github.com/oerpub>`__

Java
*********

- DSpace
  
  The `DSpace implementation of SWORD v2 <https://github.com/DSpace/DSpace/tree/master/dspace-swordv2/src/main/java/org/dspace/sword2>`__ was written by the spec lead.

- DASH (Digital Access to Scholarship at Harvard)
  
  `DASH <http://dash.harvard.edu/>`__ is built on top of DSpace and one of the DASH developers has been submitting pull requests to the common Java server library for SWORD at: `https://github.com/bmckinney/JavaServer2.0 <https://github.com/bmckinney/JavaServer2.0>`__

  **Service Document example from DASH**

  The DASH Service Document looks something like this:

 ::

    <?xml version="1.0"?>
    <service xmlns="http://www.w3.org/2007/app" xmlns:atom="http://www.w3.org/2005/Atom">
      <workspace>
        <atom:title type="text">Digital Access to Scholarship at Harvard</atom:title>
        <collection href="http://dash.harvard.edu/swordv2/collection/1/2">
          <atom:title type="text">FAS Scholarly Articles</atom:title>
          <accept alternate="multipart-related">*/*</accept>
          <collectionPolicy xmlns="http://purl.org/net/sword/terms/">NOTE: PLACE YOUR OWN LICENSE HERE</collectionPolicy>
          <mediation xmlns="http://purl.org/net/sword/terms/">true</mediation>
          <acceptPackaging xmlns="http://purl.org/net/sword/terms/">http://purl.org/net/sword/package/SimpleZip</acceptPackaging>
          <acceptPackaging xmlns="http://purl.org/net/sword/terms/">http://purl.org/net/sword/package/METSDSpaceSIP</acceptPackaging>
          <acceptPackaging xmlns="http://purl.org/net/sword/terms/">http://purl.org/net/sword/package/Binary</acceptPackaging>
          <abstract xmlns="http://purl.org/dc/terms/">Peer reviewed scholarly articles from the Faculty of Arts and Sciences of Harvard University</abstract>
        </collection>
        <collection href="http://dash.harvard.edu/swordv2/collection/1/10527970">
          <atom:title type="text">FAS Student Papers</atom:title>
          <accept alternate="multipart-related">*/*</accept>
          <collectionPolicy xmlns="http://purl.org/net/sword/terms/">NOTE: PLACE YOUR OWN LICENSE HERE </collectionPolicy>
          <mediation xmlns="http://purl.org/net/sword/terms/">true</mediation>
          <acceptPackaging xmlns="http://purl.org/net/sword/terms/">http://purl.org/net/sword/package/SimpleZip</acceptPackaging>
          <acceptPackaging xmlns="http://purl.org/net/sword/terms/">http://purl.org/net/sword/package/METSDSpaceSIP</acceptPackaging>
          <acceptPackaging xmlns="http://purl.org/net/sword/terms/">http://purl.org/net/sword/package/Binary</acceptPackaging>
          <abstract xmlns="http://purl.org/dc/terms/">FAS Student Papers</abstract>
        </collection>
        <collection href="http://dash.harvard.edu/swordv2/collection/1/4927603">
          <atom:title type="text">FAS Theses and Dissertations</atom:title>
          <accept alternate="multipart-related">*/*</accept>
          <collectionPolicy xmlns="http://purl.org/net/sword/terms/">NOTE: PLACE YOUR OWN LICENSE HERE</collectionPolicy>
          <mediation xmlns="http://purl.org/net/sword/terms/">true</mediation>
          <acceptPackaging xmlns="http://purl.org/net/sword/terms/">http://purl.org/net/sword/package/SimpleZip</acceptPackaging>
          <acceptPackaging xmlns="http://purl.org/net/sword/terms/">http://purl.org/net/sword/package/METSDSpaceSIP</acceptPackaging>
          <acceptPackaging xmlns="http://purl.org/net/sword/terms/">http://purl.org/net/sword/package/Binary</acceptPackaging>
          <abstract xmlns="http://purl.org/dc/terms/">FAS Theses and Dissertations</abstract>
        </collection>
      </workspace>
      <generator xmlns="http://www.w3.org/2005/Atom" uri="http://www.dspace.org/ns/sword/2.0/" version="2.0">dash-webadmin@hulmail.harvard.edu</generator>
      <version xmlns="http://purl.org/net/sword/terms/">2.0</version>
    </service>

- Fedora (Fedora Commons Repository Software)

  | `https://github.com/mediashelf/sword2-fedora <https://github.com/mediashelf/sword2-fedora>`__
  | and
  | `https://github.com/mediashelf/sword2-server <https://github.com/mediashelf/sword2-server>`__
  | via
  | `http://www.mail-archive.com/sword-app-tech@lists.sourceforge.net/msg00317.html <http://www.mail-archive.com/sword-app-tech@lists.sourceforge.net/msg00317.html>`__

- Carolina Digital Repository (based on Fedora)

 - `https://github.com/UNC-Libraries/Carolina-Digital-Repository/tree/master/sword-server <https://github.com/UNC-Libraries/Carolina-Digital-Repository/tree/master/sword-server>`__

 - `http://blogs.lib.unc.edu/cdr/index.php/2012/11/06/biomed-central/ <http://blogs.lib.unc.edu/cdr/index.php/2012/11/06/biomed-central/>`__

 - `http://blogs.lib.unc.edu/cdr/index.php/2012/10/30/curators-workbench-4-0-released/ <http://blogs.lib.unc.edu/cdr/index.php/2012/10/30/curators-workbench-4-0-released/>`__

 - `https://github.com/UNC-Libraries/Curators-Workbench/blob/master/workbench\_help/html/concepts/forms.html <https://github.com/UNC-Libraries/Curators-Workbench/blob/master/workbench_help/html/concepts/forms.html>`__

Perl
*********

- EPrints

 - `http://wiki.eprints.org/w/SWORD <http://wiki.eprints.org/w/SWORD>`__

 - `https://github.com/eprints/eprints/blob/master/perl\_lib/EPrints/Apache/CRUD.pm <https://github.com/eprints/eprints/blob/master/perl_lib/EPrints/Apache/CRUD.pm>`__

Community
----------------

- `http://swordapp.org/contact/ <http://swordapp.org/contact/>`__

- `sword-app-tech@lists.sourceforge.net <mailto:sword-app-tech@lists.sourceforge.net>`__

 - `https://lists.sourceforge.net/lists/listinfo/sword-app-tech <https://lists.sourceforge.net/lists/listinfo/sword-app-tech>`__

 - `http://www.mail-archive.com/sword-app-tech@lists.sourceforge.net/ <http://www.mail-archive.com/sword-app-tech@lists.sourceforge.net/>`__

- `https://twitter.com/swordapp <https://twitter.com/swordapp>`__


- #dspace on Freenode



v1 of Data Deposit API
================

DVN Data Deposit API v1 examples using curl
-------------------------------------------

Please note that this is all subject to change!

|
  `https://github.com/IQSS/dvn/tree/develop/tools/scripts/data-deposit-api <https://github.com/IQSS/dvn/tree/develop/tools/scripts/data-deposit-api>`__ contains the latest working example code.

Retrieve SWORD service document
************************************************

``curl --insecure -s https://sword:sword@localhost:8181/dvn/api/data-deposit/v1/swordv2/service-document``

Create a study with an Atom entry (XML file)
****************************************************

``curl --insecure -s --data-binary "@atom-entry-study.xml" -H "Content-Type: application/atom+xml" https://sword:sword@localhost:8181/dvn/api/data-deposit/v1/swordv2/collection/dataverse/sword``

| A sample ``atom-entry-study.xml`` file is available `here <https://github.com/IQSS/dvn/blob/develop/tools/scripts/data-deposit-api/atom-entry-study.xml>`__.

Add files to a study with a zip file
****************************************************

``curl --insecure -s --data-binary @example.zip -H "Content-Disposition: filename=example.zip" -H "Content-Type: application/zip" -H "Packaging: http://purl.org/net/sword/package/SimpleZip" https://sword:sword@localhost:8181/dvn/api/data-deposit/v1/swordv2/edit-media/study/hdl:TEST/12345``

Display a study atom entry
********************************************************

 Contains data citation (bibliographicCitation), alternate URI [persistent URI of study], edit URI, edit media URI, statement URI.

``curl --insecure -s https://sword:sword@localhost:8181/dvn/api/data-deposit/v1/swordv2/edit/study/hdl:TEST/12345``

Display a study statement
***********************************************

Contains feed of file entries, latestVersionState, locked boolean

``curl --insecure -s https://sword:sword@localhost:8181/dvn/api/data-deposit/v1/swordv2/statement/study/hdl:TEST/12345``

Delete a file by database id
****************************************************

``curl --include --insecure -s -X DELETE https://sword:sword@localhost:8181/dvn/api/data-deposit/v1/swordv2/edit-media/file/2325541``

Replacing cataloging information (title, author, etc.) for a study
*****************************************************************************

``curl --insecure -s --upload-file "atom-entry-study2.xml" -H "Content-Type: application/atom+xml" https://sword:sword@localhost:8181/dvn/api/data-deposit/v1/swordv2/edit/study/hdl:TEST/12345``

A sample ``atom-entry-study2.xml`` file is available `here <https://github.com/IQSS/dvn/blob/develop/tools/scripts/data-deposit-api/atom-entry-study2.xml>`__.

List studies in a dataverse
****************************************************

``curl --insecure -s https://sword:sword@localhost:8181/dvn/api/data-deposit/v1/swordv2/collection/dataverse/sword``

Delete a study (non-released studies only)
****************************************************

``curl -i --insecure -s -X DELETE https://sword:sword@localhost:8181/dvn/api/data-deposit/v1/swordv2/edit/study/hdl:TEST/12345``

Deaccession a study (released studies only)
****************************************************

``curl -i --insecure -s -X DELETE https://sword:sword@localhost:8181/dvn/api/data-deposit/v1/swordv2/edit/study/hdl:TEST/12345``

Release a study
****************************************************

``curl --insecure -s -X POST -H "In-Progress: false" --upload-file zero-length-file.txt https://sword:sword@localhost:8181/dvn/api/data-deposit/v1/swordv2/edit/study/hdl:TEST/12345``

Determine if a dataverse has been released 
************************************************************************

dataverseHasBeenReleased boolean

``curl --insecure -s https://sword:sword@localhost:8181/dvn/api/data-deposit/v1/swordv2/collection/dataverse/sword``

curl reference
-----------------

--upload-file is a PUT
****************************************************

"If this is used on an HTTP(S) server, the `PUT <http://curl.haxx.se/docs/manpage.html>`__ command will be used." 

--data-binary is a POST
****************************************************

"(HTTP) This posts data" --
`http://curl.haxx.se/docs/manpage.html <http://curl.haxx.se/docs/manpage.html>`__

Reformatting XML output with xmllint to make it readable
--------------------------------------------------------

``curl --insecure -s https://sword:sword@localhost:8181/dvn/api/data-deposit/v1/swordv2/service-document | xmllint -format -``

DVN Data Deposit API v1 client written in Python
------------------------------------------------

`https://github.com/dvn/swordpoc/tree/master/dvn\_client <https://github.com/dvn/swordpoc/tree/master/dvn_client>`__





