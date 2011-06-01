
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
            boolean metadataWritten = false;
            boolean schemaChecked = false;

            savedMetadataFile = File.createTempFile("meta", ".tmp");
            FileOutputStream tempFileStream = new FileOutputStream(savedMetadataFile);
            PrintWriter metadataOut = new PrintWriter (tempFileStream, true);

            metadataOut.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

            int mopen = 0;
            int mclose = 0;
 
            while ( ( line = rd.readLine () ) != null) {
                if (!metadataFlag) {
                    if (line.matches(".*<metadata>.*")) {
                        String lineCopy = line;

                        int i = line.indexOf("<metadata>");
                        line = line.substring(i+10);

                        oaiResponseHeader = oaiResponseHeader.concat(lineCopy.replaceAll("<metadata>.*", "<metadata></metadata></record></GetRecord></OAI-PMH>"));

                        metadataFlag = true;
                    }
                }

                if (metadataFlag) {
                    if (!metadataWritten) {
                        // Inside an OAI-PMH GetRecord response, the metadata
                        // record returned is enclosed in <metadata> ... </metadata>
                        // tags, after the OAI service sections that provide the
                        // date, identifier and other protocol-level information.
                        // However, it is possible for the metadata record itself
                        // to have <metadata> tags of its own. So we have no
                        // choice but to count the opening and closing tags in
                        // order to recognize the one terminating the metadata
                        // section.
                        // This code isn't pretty, but on seriously large records
                        // the savings from not fully parsing the XML are
                        // significant.
                        //  -- L.A. 

                        if (line.matches("<metadata")) {
                           int i = 0;
                           while ((i = line.indexOf("<metadata", i)) > -1) {
                               if (!line.substring(i).matches("^<metadata[^>]*/")) {
                                   // don't count if it's a closed, empty tag:
                                   // <metadata />
                                   mopen++;
                               }
                               i+=10;
                           }
                        }
                        if (line.matches(".*</metadata>.*")) {
                            int i = 0;
                            while ((i = line.indexOf("</metadata>", i)) > -1) {
                                i+=11;
                                mclose++;
                            }

                            if ( mclose > mopen ) {
                                line = line.substring(0, line.lastIndexOf("</metadata>"));
                                metadataWritten = true;
                            }
                        }

                        if (!schemaChecked) {
                            // if the top-level XML element lacks the schema definition,
                            // insert the generic xmlns and xmlns:xsi attributes; these
                            // may be needed by the transform stylesheets.
                            // this mimicks the behaviour of the OCLC GetRecord
                            // client implementation.
                            //      -L.A. 
                            if (line.indexOf('<') > -1) {
                                if (!line.matches("^[^<]*<[^>]*xmlns")) {
                                    line = line.replaceFirst(">", " xmlns=\"http://www.openarchives.org/OAI/2.0/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
                                }
                                schemaChecked = true; 
                            }
                        }

                        metadataOut.println(line);
                    }
                } else {
                    oaiResponseHeader = oaiResponseHeader.concat(line);
                }
            }

            // parse the OAI Record header:

            XMLStreamReader xmlr = null;

            try {
                StringReader reader = new StringReader(oaiResponseHeader);
                xmlr = xmlInputFactory.createXMLStreamReader(reader);
                processOAIheader(xmlr);

            } catch (XMLStreamException ex) {
                //Logger.getLogger("global").log(Level.SEVERE, null, ex);
                if (this.errorMessage == null) {
                    this.errorMessage = "Malformed GetRecord response: " + oaiResponseHeader;
                }

                // delete the temp metadata file; we won't need it:
                if (savedMetadataFile != null) {
                    //savedMetadataFile.delete();
                }

            }

            try {
                if (xmlr != null) {
                    xmlr.close();
                }
            } catch (Exception ed) {
                // seems OK to ignore;
            }


            if (rd != null) {
                rd.close();
            }

            if (metadataOut != null) {
                metadataOut.close();
            }

            if (!(metadataWritten) && !(this.isDeleted())) {
                this.errorMessage = "Failed to parse GetRecord response; "+oaiResponseHeader;
                //savedMetadataFile.delete();
            }

            if (this.isDeleted()) {
                //savedMetadataFile.delete();
            }


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
