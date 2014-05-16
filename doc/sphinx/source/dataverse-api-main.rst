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
++++++++++++++++

As of version 3.6, a new API for programmatic deposit of data and metadata to the Dataverse Network has been added. The API allows a remote, non-Dataverse Network archive/application to deposit files and metadata to a Dataverse Network installation.

Overview of Data Deposit API
============================

"v1" of the DVN Data Deposit API is a partial implementation of the SWORDv2 protocol, the specification for which available at http://swordapp.github.io/SWORDv2-Profile/SWORDProfile.html

Please reference the SWORDv2 specification for expected HTTP status codes (i.e. 201, 204, 404, etc.), headers (i.e. "Location"), etc.

Data Deposit API v1 `curl` examples
-----------------------------------

The following `curl` commands demonstrate supported operations:

Retrieve SWORD service document
*******************************

The service document enumerates the dataverses ("collections" from a SWORD perspective) the user can deposit data into. The "collectionPolicy" element for each dataverse contains the deposit terms of use for the network and dataverse.

``curl https://$USERNAME:$PASSWORD@$DVN_SERVER/dvn/api/data-deposit/v1/swordv2/service-document``

Create a study with an Atom entry (XML file)
********************************************

``curl --data-binary "@atom-entry-study.xml" -H "Content-Type: application/atom+xml" https://$USERNAME:$PASSWORD@$DVN_SERVER/dvn/api/data-deposit/v1/swordv2/collection/dataverse/$DATAVERSE_ALIAS``

.. code-block:: guess

        <?xml version="1.0"?>
        <!--
        modified from http://swordapp.github.io/SWORDv2-Profile/SWORDProfile.html#protocoloperations_editingcontent_metadata
        -->
        <entry xmlns="http://www.w3.org/2005/Atom"
               xmlns:dcterms="http://purl.org/dc/terms/">
           <!-- some embedded metadata -->
           <dcterms:title>Roasting at Home</dcterms:title>
           <dcterms:creator>Peets, John</dcterms:creator>
           <dcterms:creator>Stumptown, Jane</dcterms:creator>
           <!-- Producer with financial or admin responsibility of the data -->
           <dcterms:publisher>Coffee Bean State University</dcterms:publisher>
           <!-- related publications --> 
           <dcterms:isReferencedBy holdingsURI="http://dx.doi.org/10.1038/dvn333" agency="DOI"
               IDNo="10.1038/dvn333">Peets, J., &amp; Stumptown, J. (2013). Roasting at Home. New England Journal of Coffee, 3(1), 22-34.</dcterms:isReferencedBy>
           <!-- production date -->
           <dcterms:date>2013-07-11</dcterms:date>
           <!-- Other Identifier for the data in this study (or potentially global id if unused) -->
           <!--
           <dcterms:identifier>hdl:1XXZY.1/XYXZ</dcterms:identifier>
           -->
           <dcterms:description>Considerations before you start roasting your own coffee at home.</dcterms:description>
           <!-- keywords -->
           <dcterms:subject>coffee</dcterms:subject>
           <dcterms:subject>beverage</dcterms:subject>
           <dcterms:subject>caffeine</dcterms:subject>
           <!-- geographic coverage -->
           <dcterms:coverage>United States</dcterms:coverage>
           <dcterms:coverage>Canada</dcterms:coverage>
           <!-- kind of data -->
           <dcterms:type>aggregate data</dcterms:type>
           <!-- List of sources of the data collection-->
           <dcterms:source>Stumptown, Jane. 2011. Home Roasting. Coffeemill Press.</dcterms:source>
           <!-- restrictions -->
           <dcterms:rights>Creative Commons CC-BY 3.0 (unported) http://creativecommons.org/licenses/by/3.0/</dcterms:rights>
           <!-- related materials -->
           <dcterms:relation>Peets, John. 2010. Roasting Coffee at the Coffee Shop. Coffeemill Press</dcterms:relation>
        </entry>
        
Dublin Core (DC) Qualified Mapping - DDI - Dataverse Network DB Element Crosswalk
***********************************************************************************

+-----------------------------+----------------------------------------------+--------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------+
|DC (terms: namespace)        |                DVN DB Element                |        DDI Element 2.x         |                                                                    Note                                                                    |
+=============================+==============================================+================================+============================================================================================================================================+
|dcterms:title                |                    title                     |         2.1.1.1 title          |                                                                                                                                            |
+-----------------------------+----------------------------------------------+--------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------+
|dcterms:creator              |         author (LastName, FirstName)         |        2.1.2.1 AuthEnty        |                                                                                                                                            |
+-----------------------------+----------------------------------------------+--------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------+
|dcterms:subject              |                   keyword                    |        2.2.1.1. keyword        |                                                                                                                                            |
+-----------------------------+----------------------------------------------+--------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------+
|dcterms:description          |                   abstract                   |         2.2.2 abstract         |                                     Describing the purpose, scope or nature of the data collection...                                      |
+-----------------------------+----------------------------------------------+--------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------+
|dcterms:publisher            |                   producer                   |        2.1.3.1 producer        |                                person or agency financially or administratively responsible for the dataset                                |
+-----------------------------+----------------------------------------------+--------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------+
|dcterms:contributor          |                     n/a                      |              n/a               |                                                         see dcterms:creator above                                                          |
+-----------------------------+----------------------------------------------+--------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------+
|dcterms:date                 |productionDate (YYYY-MM-DD or YYYY-MM or YYYY)|        2.1.3.3 prodDate        |                                                  production or published date of dataset                                                   |
+-----------------------------+----------------------------------------------+--------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------+
|dcterms:type                 |                  kindOfData                  |       2.2.3.10 dataKind        |                     Type of data included in the file: survey data, census/enumeration data, aggregate data, clinical                      |
+-----------------------------+----------------------------------------------+--------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------+
|dcterms:format               |                     n/a                      |              n/a               |                                                                                                                                            |
+-----------------------------+----------------------------------------------+--------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------+
|dcterms:identifier           |                   otherID                    |          2.1.1.5 IDNo          |                        Don't use this field to map a journal article ID. Only ID's that directly belong to dataset                         |
+-----------------------------+----------------------------------------------+--------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------+
|dcterms:source               |                 dataSources                  |       2.3.1.8.1 dataSrc        |                       List of books, articles, data files if any that served as the sources for the data collection                        |
+-----------------------------+----------------------------------------------+--------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------+
|dcterms:language             |                     n/a                      |              n/a               |                                                                                                                                            |
+-----------------------------+----------------------------------------------+--------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------+
|dcterms:relation             |               relatedMaterial                |          2.5.1 relMat          |                      any related material (journal article is not included here - see: dcterms:isReferencedBy below)                       |
+-----------------------------+----------------------------------------------+--------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------+
|dcterms:coverage             |              geographicCoverage              |       2.2.3.4 geogCover        |                                                Info on the geographic coverage of the data                                                 |
+-----------------------------+----------------------------------------------+--------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------+
|dcterms:rights               |                 restrictions                 |        2.4.2.3 restrctn        |                                            any restrictions on the access or use of the dataset                                            |
+-----------------------------+----------------------------------------------+--------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------+
|dcterms:bibliographicCitation|                 dataCitation                 |       ? (2.1.7 biblCit)        |                                            data citation for the study in the Dataverse Network                                            |
+-----------------------------+----------------------------------------------+--------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------+
|dcterms:isReferencedBy       |             studyRelPublications             |? (not set by DDI community yet)|the publication (journal article, book, other work) that uses this dataset (include citation, permanent identifier (DOI), and permanent URL)|
+-----------------------------+----------------------------------------------+--------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------+

        

Add files to a study with a zip file
************************************

``curl --data-binary @example.zip -H "Content-Disposition: filename=example.zip" -H "Content-Type: application/zip" -H "Packaging: http://purl.org/net/sword/package/SimpleZip" https://$USERNAME:$PASSWORD@$DVN_SERVER/dvn/api/data-deposit/v1/swordv2/edit-media/study/hdl:TEST/12345``

Display a study atom entry
**************************

Contains data citation (bibliographicCitation), alternate URI [persistent URI of study], edit URI, edit media URI, statement URI.

``curl https://$USERNAME:$PASSWORD@$DVN_SERVER/dvn/api/data-deposit/v1/swordv2/edit/study/hdl:TEST/12345``

Display a study statement
*************************

Contains feed of file entries, latestVersionState, locked boolean

``curl https://$USERNAME:$PASSWORD@$DVN_SERVER/dvn/api/data-deposit/v1/swordv2/statement/study/hdl:TEST/12345``

Delete a file by database id
****************************

``curl -i -X DELETE https://$USERNAME:$PASSWORD@$DVN_SERVER/dvn/api/data-deposit/v1/swordv2/edit-media/file/2325541``

Replacing cataloging information (title, author, etc.) for a study
******************************************************************

Please note that all cataloging information will be replaced, including fields that can not be expressed with "dcterms" fields.

``curl --upload-file "atom-entry-study2.xml" -H "Content-Type: application/atom+xml" https://$USERNAME:$PASSWORD@$DVN_SERVER/dvn/api/data-deposit/v1/swordv2/edit/study/hdl:TEST/12345``

.. code-block:: guess

        <?xml version="1.0"?>
        <!--
        for modifying a study created with atom-entry-study.xml
        -->
        <entry xmlns="http://www.w3.org/2005/Atom"
               xmlns:dcterms="http://purl.org/dc/terms/">
           <!-- some embedded metadata -->
           <dcterms:title>The Levels of Caffeine in Cold Brew Coffee</dcterms:title>
           <dcterms:creator>Peets, John L.</dcterms:creator>
           <dcterms:creator>Stumptown Research Institute</dcterms:creator>
           <dcterms:isReferencedBy holdingsURI="http://dx.doi.org/10.1038/dvn333" agency="DOI"
               IDNo="10.1038/dvn333">Peets, J., &amp; Stumptown, J. (2013). Roasting at Home. New England Journal of Coffee, 3(1), 22-34.</dcterms:isReferencedBy>
           <dcterms:date>2013-08-11</dcterms:date>
           <dcterms:description>This study evaluates the caffeine levels of a cold brewed coffee.</dcterms:description>
           <dcterms:subject>coffee bean</dcterms:subject>
           <dcterms:subject>caffeine</dcterms:subject>
           <dcterms:subject>cold brew process</dcterms:subject>
           <dcterms:subject>Stumptown Coffee Company</dcterms:subject>
           <dcterms:rights>Creative Commons CC-BY 3.0 (unported) http://creativecommons.org/licenses/by/3.0/</dcterms:rights>
        </entry>

List studies in a dataverse
***************************

``curl https://$USERNAME:$PASSWORD@$DVN_SERVER/dvn/api/data-deposit/v1/swordv2/collection/dataverse/$DATAVERSE_ALIAS``

Delete a study (non-released studies only)
******************************************

``curl -i -X DELETE https://$USERNAME:$PASSWORD@$DVN_SERVER/dvn/api/data-deposit/v1/swordv2/edit/study/hdl:TEST/12345``

Deaccession a study (released studies only)
****************************************************

``curl -i -X DELETE https://$USERNAME:$PASSWORD@$DVN_SERVER/dvn/api/data-deposit/v1/swordv2/edit/study/hdl:TEST/12345``

Release a study
***************

``curl -X POST -H "In-Progress: false" --upload-file zero-length-file.txt https://$USERNAME:$PASSWORD@$DVN_SERVER/dvn/api/data-deposit/v1/swordv2/edit/study/hdl:TEST/12345``

Determine if a dataverse has been released 
******************************************

Look for a `dataverseHasBeenReleased` boolean.

``curl https://$USERNAME:$PASSWORD@$DVN_SERVER/dvn/api/data-deposit/v1/swordv2/collection/dataverse/$DATAVERSE_ALIAS``

`curl` reference
----------------

Per http://curl.haxx.se/docs/manpage.html 

* `--upload-file` is an HTTP `PUT`
* `--data-binary` is an HTTP `POST`

dvn package on CRAN
===================
The `dvn package on CRAN
<http://cran.r-project.org/web/packages/dvn/index.html>`__ provides
access to the Dataverse Network Data Deposit API to integrate data (and
metadata) archiving directly into the R workflow with just a few simple
functions. Developed by: `Thomas Leeper <http://thomasleeper.com>`__.
Follow its development at https://github.com/ropensci/dvn

DVN Data Deposit API v1 client sample code (Python)
===================================================

https://github.com/dvn/swordpoc/tree/master/dvn_client contains sample Python code for writing a DVN Data Deposit API v1 client. It makes use of a Python client library which conforms to the SWORDv2 specification: https://github.com/swordapp/python-client-sword2

SWORDv2 client libraries
========================

* Python: https://github.com/swordapp/python-client-sword2
* Java: https://github.com/swordapp/JavaClient2.0
* Ruby: https://github.com/swordapp/sword2ruby
* PHP: https://github.com/swordapp/swordappv2-php-library
