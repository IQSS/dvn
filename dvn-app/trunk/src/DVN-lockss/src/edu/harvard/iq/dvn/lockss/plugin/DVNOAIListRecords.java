/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
/**
 Copyright 2006 OCLC, Online Computer Library Center
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
/**
 * Hacked version of the standard OAI OCLC ListRecords class
 * Original author: Jeffrey A. Young, OCLC Online Computer Library Center
 * Rehacker: Leonid Andreev, HMDC 
 */
package edu.harvard.iq.dvn.lockss.plugin;

import org.lockss.util.LockssRandom; 

import ORG.oclc.oai.harvester2.verb.ListRecords;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.PrintWriter; 
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.SAXParser; 
import javax.xml.parsers.SAXParserFactory; 

/**
 * HarvesterVerb is the parent class for each of the OAI verbs.
 * 
 * @author Jefffrey A. Young, OCLC Online Computer Library Center
 */
public class DVNOAIListRecords extends ListRecords {
    private static Logger logger = Logger.getLogger(DVNOAIListRecords.class);

    static {
        BasicConfigurator.configure();
    }
    
    /* Primary OAI namespaces */
    public static final String SCHEMA_LOCATION_V2_0 = "http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd";
    public static final String SCHEMA_LOCATION_V1_1_GET_RECORD = "http://www.openarchives.org/OAI/1.1/OAI_GetRecord http://www.openarchives.org/OAI/1.1/OAI_GetRecord.xsd";
    public static final String SCHEMA_LOCATION_V1_1_IDENTIFY = "http://www.openarchives.org/OAI/1.1/OAI_Identify http://www.openarchives.org/OAI/1.1/OAI_Identify.xsd";
    public static final String SCHEMA_LOCATION_V1_1_LIST_IDENTIFIERS = "http://www.openarchives.org/OAI/1.1/OAI_ListIdentifiers http://www.openarchives.org/OAI/1.1/OAI_ListIdentifiers.xsd";
    public static final String SCHEMA_LOCATION_V1_1_LIST_METADATA_FORMATS = "http://www.openarchives.org/OAI/1.1/OAI_ListMetadataFormats http://www.openarchives.org/OAI/1.1/OAI_ListMetadataFormats.xsd";
    public static final String SCHEMA_LOCATION_V1_1_LIST_RECORDS = "http://www.openarchives.org/OAI/1.1/OAI_ListRecords http://www.openarchives.org/OAI/1.1/OAI_ListRecords.xsd";
    public static final String SCHEMA_LOCATION_V1_1_LIST_SETS = "http://www.openarchives.org/OAI/1.1/OAI_ListSets http://www.openarchives.org/OAI/1.1/OAI_ListSets.xsd";
    private Document doc = null;
    private String schemaLocation = null;
    private String requestURL = null;
    private static HashMap builderMap = new HashMap();
    private static Element namespaceElement = null;
    private static DocumentBuilderFactory factory = null;
    
    private static Transformer idTransformer = null;
    static {
        try {
            /* create transformer */
            TransformerFactory xformFactory = TransformerFactory.newInstance();
            try {
                idTransformer = xformFactory.newTransformer();
                idTransformer.setOutputProperty(
                        OutputKeys.OMIT_XML_DECLARATION, "yes");
            } catch (TransformerException e) {
                e.printStackTrace();
            }
            
            /* Load DOM Document */
            factory = DocumentBuilderFactory
            .newInstance();
            factory.setNamespaceAware(true);
            Thread t = Thread.currentThread();
            DocumentBuilder builder = factory.newDocumentBuilder();
            builderMap.put(t, builder);
            
            DOMImplementation impl = builder.getDOMImplementation();
            Document namespaceHolder = impl.createDocument(
                    "http://www.oclc.org/research/software/oai/harvester",
                    "harvester:namespaceHolder", null);
            namespaceElement = namespaceHolder.getDocumentElement();
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:harvester",
            "http://www.oclc.org/research/software/oai/harvester");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:oai20", "http://www.openarchives.org/OAI/2.0/");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:oai11_GetRecord",
            "http://www.openarchives.org/OAI/1.1/OAI_GetRecord");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:oai11_Identify",
            "http://www.openarchives.org/OAI/1.1/OAI_Identify");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:oai11_ListIdentifiers",
            "http://www.openarchives.org/OAI/1.1/OAI_ListIdentifiers");
            namespaceElement
            .setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:oai11_ListMetadataFormats",
            "http://www.openarchives.org/OAI/1.1/OAI_ListMetadataFormats");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:oai11_ListRecords",
            "http://www.openarchives.org/OAI/1.1/OAI_ListRecords");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:oai11_ListSets",
            "http://www.openarchives.org/OAI/1.1/OAI_ListSets");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get the OAI response as a DOM object
     * 
     * @return the DOM for the OAI response
     */
    public Document getDocument() {
        return doc;
    }
    
    /**
     * Get the xsi:schemaLocation for the OAI response
     * 
     * @return the xsi:schemaLocation value
     */
    public String getSchemaLocation() {
        return schemaLocation;
    }
    
    /**
     * Get the OAI errors
     * @return a NodeList of /oai:OAI-PMH/oai:error elements
     * @throws TransformerException
     */
    public NodeList getErrors() throws TransformerException {
        if (SCHEMA_LOCATION_V2_0.equals(getSchemaLocation())) {
            return getNodeList("/oai20:OAI-PMH/oai20:error");
        } else {
            return null;
        }
    }
    
    /**
     * Get the OAI request URL for this response
     * @return the OAI request URL as a String
     */
    public String getRequestURL() {
        return requestURL;
    }
    
    /**
     * Mock object creator (for unit testing purposes)
     */
    public DVNOAIListRecords() {
    }
    
    /**
     * Performs the OAI request
     * 
     * @param requestURL
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     */
    public DVNOAIListRecords(String requestURL) throws IOException,
    ParserConfigurationException, SAXException, TransformerException {
        harvest(requestURL);
    }

    public DVNOAIListRecords(String baseURL, String from, String until,
            String set, String metadataPrefix)
    throws IOException, ParserConfigurationException, SAXException,
    TransformerException {
        harvest(getRequestURL(baseURL, from, until, set, metadataPrefix));
    }
    

    public DVNOAIListRecords(String baseURL, String resumptionToken)
    throws IOException, ParserConfigurationException, SAXException,
    TransformerException {
        harvest(getRequestURL(baseURL, resumptionToken));
    }
    


    
    /**
     * Preforms the OAI request
     * 
     * @param requestURL
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     */

    public void harvest(String requestURL) throws IOException,
    ParserConfigurationException, SAXException, TransformerException {
        this.requestURL = requestURL;
        logger.debug("requestURL=" + requestURL);
        InputStream in = null;
        URL url = new URL(requestURL);
        HttpURLConnection con = null;
        int responseCode = 0;
        do {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "OAIHarvester/2.0");
            con.setRequestProperty("Accept-Encoding",
				   "compress, gzip, identify");
            try {
                responseCode = con.getResponseCode();
                logger.debug("responseCode=" + responseCode);
            } catch (FileNotFoundException e) {
                // assume it's a 503 response
                logger.info(requestURL, e);
                responseCode = HttpURLConnection.HTTP_UNAVAILABLE;
            }
            
            if (responseCode == HttpURLConnection.HTTP_UNAVAILABLE) {
                long retrySeconds = con.getHeaderFieldInt("Retry-After", -1);
                if (retrySeconds == -1) {
                    long now = (new Date()).getTime();
                    long retryDate = con.getHeaderFieldDate("Retry-After", now);
                    retrySeconds = retryDate - now;
                }
                if (retrySeconds == 0) { // Apparently, it's a bad URL
                    throw new FileNotFoundException("Bad URL?");
                }
                System.err.println("Server response: Retry-After="
                        + retrySeconds);
                if (retrySeconds <= 0) {
		    retrySeconds = 1; 
                }
		try {
		    Thread.sleep(retrySeconds * 1000);
		} catch (InterruptedException ex) {
		    ex.printStackTrace();
		}
            }
        } while (responseCode == HttpURLConnection.HTTP_UNAVAILABLE);
        String contentEncoding = con.getHeaderField("Content-Encoding");
        logger.debug("contentEncoding=" + contentEncoding);
        if ("compress".equals(contentEncoding)) {
            ZipInputStream zis = new ZipInputStream(con.getInputStream());
            zis.getNextEntry();
            in = zis;
        } else if ("gzip".equals(contentEncoding)) {
            in = new GZIPInputStream(con.getInputStream());
        } else if ("deflate".equals(contentEncoding)) {
            in = new InflaterInputStream(con.getInputStream());
        } else {
            in = con.getInputStream();
        }
        
	// (TODO - add detailed comments explaining the whole process -- L.A.)

        FileOutputStream tempOutFileStream = null; 
        FileOutputStream tempOutRecordStream = null;
        PrintWriter tempOutRecordWriter = null; 
        
        int TmpId = new LockssRandom().nextInt() & 0xffff;
        String tempExtractFile = "/tmp/ListRecords.extract." + TmpId + ".xml";
        String tempRecordFile = "/tmp/ListRecords.record." + TmpId + ".xml";
        
	try {
            // Here's the deal: 
            // We want to parse 1 record at a time, so that we know which one
            // fails to parse; then we can skip it, log it, and continue 
            // with the parsing and crawling. 
            // One way of doing this would be to use ListIdentifiers/GetRecord
            // instead of ListRecords. But that would result in a great 
            // increase of the remote server calls. So we should rather 
            // spend a little extra effort going through the ListRecords stream
            // and separating individual records (and service headers).
           	
            tempOutFileStream = new FileOutputStream(tempExtractFile);
            
	    byte[] dataBuffer = new byte[8192]; 

            int bytesread = 0;
            int headInit = 0; 
            StringBuffer lineBuffer = null;
            
            
	    while ( (bytesread = in.read (dataBuffer)) > 0 ) {
                StringBuffer dataLine = null; 
                
                if (lineBuffer == null || lineBuffer.length() == 0) {
                    dataLine = new StringBuffer (new String (dataBuffer, 0, bytesread));
                } else {
                    dataLine = lineBuffer.append(new StringBuffer(new String (dataBuffer, 0, bytesread)));
                    lineBuffer = null;
                }
                
                int x = 0; 
                
                if (headInit == 0) {
                    // Extract OAI header; 
                    x = dataLine.indexOf("<record>");
                    if ( x > 0) {
                        String oaiHeader = dataLine.substring(0, x);
                        tempOutFileStream.write(oaiHeader.getBytes());
                        tempOutFileStream.flush(); 
                         
                        dataLine = dataLine.replace(0, x, "");
                        dataLine.trimToSize();
                        
                    } else if (dataLine.indexOf("<error") > 0 
                            || dataLine.indexOf("</ListRecords>") > 0) {
                        // Error (or empty) OAI response; ok.
                        
                    } else {
                        // TODO -- may want to check the size of the content, 
                        // try to do another read() - just in case. 
                        throw new SAXException("Bad OAI ListRecords response.");
                    }
                    
                    headInit = 1;                    

                } 
                    
                if (tempOutRecordWriter != null && ((x = dataLine.indexOf("</record>")) > -1)) {
                    tempOutRecordWriter.print(dataLine.substring(0, x+9));
                    tempOutRecordWriter.close(); 
                    dataLine = dataLine.replace(0, x+9, "");
                    dataLine.trimToSize();

                    produceRecordExtract(new File(tempRecordFile), tempOutFileStream);
                    tempOutRecordWriter = null; 


                    
                }
                
                while (tempOutRecordWriter == null && ((x = dataLine.indexOf("<record>")) > -1)) {
                    tempOutRecordStream = new FileOutputStream(tempRecordFile);
                    tempOutRecordWriter = new PrintWriter(tempOutRecordStream, true);
                    
                    int y = dataLine.indexOf("</record>");
                    
                    if (y > 0) {
                        tempOutRecordWriter.print(dataLine.substring(x, y+9));
                        tempOutRecordWriter.close();
                        tempOutRecordWriter = null;

                        dataLine = dataLine.replace(0, y+9, "");
                        dataLine.trimToSize();

                        produceRecordExtract(new File(tempRecordFile), tempOutFileStream);

                         
                    } else {
                    }
                }
                
                // End of the read block:
                // If we are in the process of writing a single record, 
                // output what's left in the buffer. (but check for split 
                // XML tags in between blocks!)
                // If we don't have a record file open, just cache the 
                // contents of the buffer for the next iteration. 
                if (tempOutRecordWriter != null) {
                    
                    String outputLine = null; 
                    
                    if (dataLine.toString().matches("<[^>]*$")) {
                        int z = dataLine.lastIndexOf("<");
                        lineBuffer = new StringBuffer( dataLine.substring(z) );
                        outputLine = dataLine.substring(0, z);
                        tempOutRecordWriter.print(outputLine);
                    } else {
                        tempOutRecordWriter.print(dataLine);
                    }
                } else {
                    // Otherwise, we cache everything in the string buffer for 
                    // a future iteration, when a new opening <record> tag is
                    // found.
                    lineBuffer = dataLine; 
                    lineBuffer.trimToSize();
                }
            }
               
            // What's left in the buffer is, presumably, the tail end of the 
            // OAI Response. Dump it into the extract file: 
               
            
            if (lineBuffer != null) {
                tempOutFileStream.write(lineBuffer.toString().getBytes());    
                tempOutFileStream.flush();  
            } else {
            }
            
	    
        } catch (SAXException sx) {
            logger.info("Error Encountered. "+sx.getMessage());
            throw sx; 
	} catch (IOException ex) {
	    logger.info("error encountered; "+requestURL, ex);
	    throw ex; 
	} finally {
            in.close();
            if (tempOutFileStream != null) {
                tempOutFileStream.close(); 
            }
            if (tempOutRecordWriter != null) {
                tempOutRecordWriter.close(); 
            }
            
        }

        // And now re-open the filtered extract: 
        in = new FileInputStream(new File (tempExtractFile));
        
        InputSource data = new InputSource(in);
        
        Thread t = Thread.currentThread();
        DocumentBuilder builder = (DocumentBuilder) builderMap.get(t);
        if (builder == null) {
            builder = factory.newDocumentBuilder();
            builderMap.put(t, builder);
        }
        doc = builder.parse(data);
        
        StringTokenizer tokenizer = new StringTokenizer(
                getSingleString("/*/@xsi:schemaLocation"), " ");
        StringBuffer sb = new StringBuffer();
        while (tokenizer.hasMoreTokens()) {
            if (sb.length() > 0)
                sb.append(" ");
            sb.append(tokenizer.nextToken());
        }
        this.schemaLocation = sb.toString();

	// delete the extract file: 

	new File (tempExtractFile).delete();
    }
    
    
    void produceRecordExtract ( File recordTempFile, FileOutputStream tempExtractFileStream) throws SAXException, IOException {
        // try to parse the record and produce the extract: 
        
        FileInputStream tempInRecordStream = null;
        ByteArrayOutputStream extractByteStream = null;
        PrintWriter pout = null; 

        try {

            tempInRecordStream = new FileInputStream(recordTempFile);
            InputSource tempData = new InputSource(tempInRecordStream);

            SAXParserFactory spf = SAXParserFactory.newInstance();
            //spf.setValidating (true); 
            SAXParser parser = spf.newSAXParser();

            XMLReader reader = parser.getXMLReader();

            extractByteStream = new ByteArrayOutputStream();
            pout = new PrintWriter(extractByteStream, true);

            DVNOAIFilterHandler hd = new DVNOAIFilterHandler(pout);
            reader.setContentHandler(hd);
            reader.setErrorHandler(hd);
            reader.parse(tempData);


            // If we've made it all the way down here without 
            // catching any exceptions, it means we've got the
            // record parsed and extract produced. We can save 
            // it.

            tempExtractFileStream.write(extractByteStream.toByteArray());
            tempExtractFileStream.flush();

            recordTempFile.delete(); 
        } catch (SAXException sx) {
            logger.info("SAX error encountered. " + sx.getMessage());
        } catch (Exception ex) {
            logger.info("Unknown error encountered. " + ex.getMessage());
        } finally {
            if (tempInRecordStream != null) {
                tempInRecordStream.close();
            }
            // closing a ByteArrayOutputStream doesn't have any effect: 
            //if (extractByteStream != null) {
            //    extractByteStream.close();
            //}
            if (pout != null) {
                pout.close();
            }
        }
    }
    
    /**
     * Get the String value for the given XPath location in the response DOM
     * 
     * @param xpath
     * @return a String containing the value of the XPath location.
     * @throws TransformerException
     */
    public String getSingleString(String xpath) throws TransformerException {
        return getSingleString(getDocument(), xpath);
    }
    
    public String getSingleString(Node node, String xpath)
    throws TransformerException {
        return XPathAPI.eval(node, xpath, namespaceElement).str();
    }
    
    /**
     * Get a NodeList containing the nodes in the response DOM for the specified
     * xpath
     * @param xpath
     * @return the NodeList for the xpath into the response DOM
     * @throws TransformerException
     */
    public NodeList getNodeList(String xpath) throws TransformerException {
        return XPathAPI.selectNodeList(getDocument(), xpath, namespaceElement);
    }
    
    public String toString() {
        // Element docEl = getDocument().getDocumentElement();
        // return docEl.toString();
        Source input = new DOMSource(getDocument());
        StringWriter sw = new StringWriter();
        Result output = new StreamResult(sw);
        try {
            idTransformer.transform(input, output);
            return sw.toString();
        } catch (TransformerException e) {
            return e.getMessage();
        }
    }

    /**
     * Get the oai:resumptionToken from the response
     * 
     * @return the oai:resumptionToken value
     * @throws TransformerException
     * @throws NoSuchFieldException
     */

    public String getResumptionToken()
    throws TransformerException, NoSuchFieldException {
        String schemaLocation = getSchemaLocation();
        if (schemaLocation.indexOf(SCHEMA_LOCATION_V2_0) != -1) {
            return getSingleString("/oai20:OAI-PMH/oai20:ListRecords/oai20:resumptionToken");
        } else if (schemaLocation.indexOf(SCHEMA_LOCATION_V1_1_LIST_RECORDS) != -1) {
            return getSingleString("/oai11_ListRecords:ListRecords/oai11_ListRecords:resumptionToken");
        } else {
            throw new NoSuchFieldException(schemaLocation);
        }
    }
    
    /**
     * Construct the query portion of the http request
     *
     * @return a String containing the query portion of the http request
     */
    private static String getRequestURL(String baseURL, String from,
            String until, String set,
            String metadataPrefix) {
        StringBuffer requestURL =  new StringBuffer(baseURL);
        requestURL.append("?verb=ListRecords");
        if (from != null) requestURL.append("&from=").append(from);
        if (until != null) requestURL.append("&until=").append(until);
        if (set != null) requestURL.append("&set=").append(set);
        requestURL.append("&metadataPrefix=").append(metadataPrefix);
        return requestURL.toString();
    }
    
    /**
     * Construct the query portion of the http request (resumptionToken version)
     * @param baseURL
     * @param resumptionToken
     * @return
     */
    private static String getRequestURL(String baseURL,
            String resumptionToken) {
        StringBuffer requestURL =  new StringBuffer(baseURL);
        requestURL.append("?verb=ListRecords");

	/* clean the resumption token string from any blanks and 
	   punctuation: */ 

	resumptionToken = resumptionToken.trim(); 

        requestURL.append("&resumptionToken=").append(URLEncoder.encode(resumptionToken));
        return requestURL.toString();
    }
}
