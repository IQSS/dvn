
package edu.harvard.iq.dvn.core.harvest;

import java.io.IOException;
import java.io.FileNotFoundException;

import java.io.InputStream;
import java.io.StringReader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;

import java.io.FileOutputStream;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipInputStream;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

import org.xml.sax.InputSource;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;



public class DvnFastGetRecord {
   
    /**
     * Client-side GetRecord verb constructor
     *
     * @param baseURL the baseURL of the server to be queried
     * @exception MalformedURLException the baseURL is bad
     * @exception SAXException the xml response is bad
     * @exception IOException an I/O error occurred
     */

    public DvnFastGetRecord(String baseURL, String identifier, String metadataPrefix)
    throws IOException, ParserConfigurationException, SAXException,
    TransformerException {
        harvestRecord (baseURL, identifier, metadataPrefix);

    }
    
    private String errorMessage = null;
    private File savedMetadataFile = null; 
    private XMLInputFactory xmlInputFactory = null; 
    private boolean recordDeleted = false;

    // TODO: logging

    public String getErrorMessage () {
        return errorMessage;
    }

    public File getMetadataFile () {
        return savedMetadataFile;
    }

    public boolean isDeleted () {
        return this.recordDeleted;
    }


    public void harvestRecord(String baseURL, String identifier, String metadataPrefix) throws IOException,
        ParserConfigurationException, SAXException, TransformerException {

        xmlInputFactory = javax.xml.stream.XMLInputFactory.newInstance();

        String requestURL = getRequestURL(baseURL, identifier, metadataPrefix);

        InputStream in = null;
        URL url = new URL(requestURL);
        HttpURLConnection con = null;
        int responseCode = 0;

        con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "OAIHarvester/2.0");
        con.setRequestProperty("Accept-Encoding",
                                   "compress, gzip, identify");
        try {
            responseCode = con.getResponseCode();
            //logger.debug("responseCode=" + responseCode);
        } catch (FileNotFoundException e) {
            //logger.info(requestURL, e);
            responseCode = HttpURLConnection.HTTP_UNAVAILABLE;
        }

        // TODO: -- L.A.
        //
        // support for cookies;
        // support for limited retry attempts -- ?
        // implement reading of the stream as filterinputstream -- ?
        // -- that could make it a little faster still. -- L.A. 



        if (responseCode == 200) {

            String contentEncoding = con.getHeaderField("Content-Encoding");
            //logger.debug("contentEncoding=" + contentEncoding);

            // support for the standard compress/gzip/deflate compression
            // schemes:

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

            // We are going to read the OAI header and SAX-parse it for the
            // error messages and other protocol information;
            // The metadata section we're going to simply save in a temporary
            // file, unparsed.

            BufferedReader rd = new BufferedReader(new InputStreamReader(in));

            String line = null;
            String oaiResponseHeader = "";
            boolean metadataFlag = false;

            savedMetadataFile = File.createTempFile("meta", ".tmp");
            FileOutputStream tempFileStream = new FileOutputStream(savedMetadataFile);
            PrintWriter metadataOut = new PrintWriter (tempFileStream, true);

            metadataOut.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
 
            while ( ( line = rd.readLine () ) != null) {
                if (metadataFlag) {
                    if (line.matches("</metadata>")) {
                        line = line.replaceAll("</metadata>.*", "");
                        metadataOut.println(line);
                        metadataOut.close();
                        rd.close();

                        return;
                    }
                    metadataOut.println(line);
                } else {
                    if (line.matches("<metadata>")) {
                        String lineCopy = line;

                        metadataOut.println(line.replaceAll("^.*<metadata>", ""));

                        oaiResponseHeader = oaiResponseHeader.concat(line.replaceAll("<metadata>.*", "<metadata></metadata></record></GetRecord></OAI-PMH>"));

                        // parse the OAI Record header:

                        InputSource data = new InputSource(new StringReader(oaiResponseHeader));

                        XMLStreamReader xmlr = null;

                        try {
                            StringReader reader = new StringReader(oaiResponseHeader);
                            xmlr =  xmlInputFactory.createXMLStreamReader(reader);
                            processOAIheader( xmlr);

                        } catch (XMLStreamException ex) {
                            //Logger.getLogger("global").log(Level.SEVERE, null, ex);
                            if (this.errorMessage == null) {
                                this.errorMessage = "Failed to parse GetRecord response: " + ex.getMessage();
                            }

                            if (rd != null) {
                                rd.close();
                            }
                            if (metadataOut != null) {
                                metadataOut.close();
                            }
                            if (savedMetadataFile != null) {
                                savedMetadataFile.delete();
                            }

                            try {
                                if (xmlr != null) {
                                    xmlr.close();
                                }
                            } catch (Exception ex2) {}

                            return;
                        }

                        metadataFlag = true;
                        try {
                            if (xmlr != null) {
                                xmlr.close();
                            }
                        } catch (Exception ed) {}


                    } else {
                        oaiResponseHeader = oaiResponseHeader.concat(line);
                    }
                }
            }

            // shouldn't get here!

            if (rd != null) {
                rd.close();
            }
            if (metadataOut != null) {
                metadataOut.close();
            }
            if (savedMetadataFile != null) {
                savedMetadataFile.delete();
            }
            this.errorMessage = "Malformed GetRecord response";
            throw new IOException (this.errorMessage);

        } else {
            this.errorMessage = "GetRecord request failed. HTTP error code "+responseCode;
        }
   }

    /**
     * Construct the query portion of the http request
     * (borrowed from OCLC implementation)
     *
     * @return a String containing the query portion of the http request
     */
    private static String getRequestURL(String baseURL,
            String identifier,
            String metadataPrefix) {

        StringBuffer requestURL =  new StringBuffer(baseURL);
        requestURL.append("?verb=GetRecord");
        requestURL.append("&identifier=").append(identifier);
        requestURL.append("&metadataPrefix=").append(metadataPrefix);

        return requestURL.toString();
    }

    private void processOAIheader (XMLStreamReader xmlr) throws XMLStreamException {

        // is this really a GetRecord response?
        xmlr.nextTag();
        xmlr.require(XMLStreamConstants.START_ELEMENT, null, "OAI-PMH");
        processOAIPMH(xmlr);

    }

    private void processOAIPMH (XMLStreamReader xmlr) throws XMLStreamException {

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                // TODO:
                // process all the fields currently skipped -- ? -- L.A.
                if (xmlr.getLocalName().equals("responseDate")) {}
                else if (xmlr.getLocalName().equals("request")) {}
                else if (xmlr.getLocalName().equals("error")) {
                    String errorCode = xmlr.getAttributeValue(null, "code");
                    String errorMessageText = getElementText(xmlr);

                    if (errorCode != null) {
                        this.errorMessage = "GetRecord error code: "+errorCode+"; ";
                    }

                    if (errorCode != null) {
                        this.errorMessage = this.errorMessage + "GetRecord error message: "+errorMessageText+"; ";
                    }
                    throw new XMLStreamException(this.errorMessage);

                }
                else if (xmlr.getLocalName().equals("GetRecord")) processGetRecordSection(xmlr);
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("OAI-PMH")) return;
            }
        }
    }

    private void processGetRecordSection (XMLStreamReader xmlr) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                 if (xmlr.getLocalName().equals("record")) {processRecord(xmlr);}
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("GetRecord")) return;
            }
        }

    }

    private void processRecord (XMLStreamReader xmlr) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                 if (xmlr.getLocalName().equals("header")) {
                     if ("deleted".equals( xmlr.getAttributeValue(null, "status"))) {
                        this.recordDeleted = true;
                     }
                     processHeader(xmlr);
                 } else if (xmlr.getLocalName().equals("metadata")) {/*do nothing;*/}
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("record")) return;
            }
        }
    }

    private void processHeader (XMLStreamReader xmlr) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                 if (xmlr.getLocalName().equals("identifier")) {/*do nothing*/}
                 else if (xmlr.getLocalName().equals("datestamp")) {/*do nothing -- ?*/}
                 else if (xmlr.getLocalName().equals("setSpec")) {/*do nothing*/}


            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("header")) return;
            }
        }
    }

    
    // (from Gustavo's ddiServiceBean -- L.A.)
    //
    /* We had to add this method because the ref getElementText has a bug where it
     * would append a null before the text, if there was an escaped apostrophe; it appears
     * that the code finds an null ENTITY_REFERENCE in this case which seems like a bug;
     * the workaround for the moment is to comment or handling ENTITY_REFERENCE in this case
     */
    private String getElementText(XMLStreamReader xmlr) throws XMLStreamException {
        if(xmlr.getEventType() != XMLStreamConstants.START_ELEMENT) {
            throw new XMLStreamException("parser must be on START_ELEMENT to read next text", xmlr.getLocation());
        }
        int eventType = xmlr.next();
        StringBuffer content = new StringBuffer();
        while(eventType != XMLStreamConstants.END_ELEMENT ) {
            if(eventType == XMLStreamConstants.CHARACTERS
            || eventType == XMLStreamConstants.CDATA
            || eventType == XMLStreamConstants.SPACE
            /* || eventType == XMLStreamConstants.ENTITY_REFERENCE*/) {
                content.append(xmlr.getText());
            } else if(eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
                || eventType == XMLStreamConstants.COMMENT
                || eventType == XMLStreamConstants.ENTITY_REFERENCE) {
                // skipping
            } else if(eventType == XMLStreamConstants.END_DOCUMENT) {
                throw new XMLStreamException("unexpected end of document when reading element text content");
            } else if(eventType == XMLStreamConstants.START_ELEMENT) {
                throw new XMLStreamException("element text content may not contain START_ELEMENT", xmlr.getLocation());
            } else {
                throw new XMLStreamException("Unexpected event type "+eventType, xmlr.getLocation());
            }
            eventType = xmlr.next();
        }
        return content.toString();
    }


}
