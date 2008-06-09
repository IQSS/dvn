1. US Census OAI:  
   http://pronghorn.dsd.census.gov/oaicat/OAIHandler
   The OAI contents haven't changed since Aug 2006
   [CONFIRMED June 05]

2. Census studies metadata are distributed in MIF XML
   format; it's a proprietory format that we convert to DDI
   to ingest the data using our own MIF-to-DDI crosswalk. 

3. Downloads: 
   
   Actual census data can be downloaded using the download 
   servlet they created for us. Only select columns
   (variables) can be downloaded; i.e., you cannot download 
   the whole file. Sample download URL:

  http://leopard.dsd.census.gov/TheDataWeb_Tabulation/VDCRepositoryServlet/AHS//Metropolitan Sample/1998/4505?vars= 


   Download URLs are served by 4 servers: 

   impala.dsd.census.gov
   jaguar.dsd.census.gov
   kinkajou.dsd.census.gov
   leopard.dsd.census.gov


4. Testing: 

   There are few tests I used to run automatically on these
   Census studies: 

   a. Validate the downloaded MIFs against the MIF schema; 

   b. Convert the valid MIFs into DDIs, using our crosswalk, 
   and validate the resulting DDI against the DDI schema;

   c. download test: from each valid MIF I extract the
   download URL and one random variable; then I try to
   request this variable column from their server and look
   at the results. (the list of test URLs is in this 
   directory as urls.list)


