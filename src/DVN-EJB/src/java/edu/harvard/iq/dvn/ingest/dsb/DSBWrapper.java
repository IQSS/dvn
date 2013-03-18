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
/*
 * DSBWrapper.java
 *
 * Created on November 1, 2006, 3:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.ingest.dsb;

import edu.harvard.iq.dvn.unf.UNF5Util;
import edu.harvard.iq.dvn.core.study.FileMetadata;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.StudyFileEditBean;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.study.FileMetadataField;
import edu.harvard.iq.dvn.core.study.FileMetadataFieldValue; 
import edu.harvard.iq.dvn.core.study.StudyFieldServiceLocal; 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.httpclient.HttpClient;


import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata.*;
import edu.harvard.iq.dvn.ingest.specialother.*;


import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;

import java.util.logging.Logger;

/**
 *
 * @author gdurand
 */
//@Stateless
@EJB(name="studyField", beanInterface=edu.harvard.iq.dvn.core.study.StudyFieldServiceLocal.class)
public class DSBWrapper implements java.io.Serializable  {
    //@EJB 
    StudyFieldServiceLocal studyFieldService;
    
    private static Logger dbgLog = Logger.getLogger(DSBWrapper.class.getPackage().getName());
    private HttpClient client = null;
    
    private static final String METADATA_SUMMARY = "FILE_METADATA_SUMMARY_INFO";
    
    /** Creates a new instance of DSBWrapper */
    public DSBWrapper() {
    }

    /*
    public static boolean useNew(String verb) {
        String useNewProperty = System.getProperty("vdc.dsb.useNew");
        if (useNewProperty != null && verb != null) {
            StringTokenizer st = new StringTokenizer(useNewProperty, "|");
            while ( st.hasMoreTokens() ) {
                if ( st.nextToken().toUpperCase().equals( verb.toUpperCase() ) ) {
                    return true;
                }
            }
        }

        return false;    
    }
    */
    /*
    private String generateUrl(String verb) throws IOException{
        String dsbHost = System.getProperty("vdc.dsb.host");
        String dsbPort = System.getProperty("vdc.dsb.port");
        
        if (dsbHost != null) {
            if ( dsbPort != null ) {
                return "http://" + dsbHost + ":" + dsbPort + "/VDC/DSB/0.1/" + verb;
            } else {
                return "http://" + dsbHost + "/VDC/DSB/0.1/" + verb;
            }
        } else {
            // fallback to the old-style option:
            dsbHost = System.getProperty("vdc.dsb.url");
            
            if (dsbHost != null) {
                return "http://" + dsbHost + "/VDC/DSB/0.1/" + verb;
            } else {
                throw new IOException("System property \"vdc.dsb.host\" has not been set.");
            }
        }
        
    }
    */
    /*
    private HttpClient getClient() {
        if (client == null) {
            client = new HttpClient( new MultiThreadedHttpConnectionManager() );
        }
        return client;
    }
    */
    /*
    private void executeMethod(PostMethod method) throws IOException {
        int state = getClient().executeMethod(method);
        
        if (state != 200) {
            throw new IOException(
                    (method.getStatusLine() != null)
                    ? method.getStatusLine().toString()
                    : "DSB Error");
        }
    }
    * */
    
    
    
    public String ingest(StudyFileEditBean file)throws IOException{
        dbgLog.fine("***** DSBWrapper: ingest(): start *****\n");

        String ddi = null;

        BufferedInputStream infile = null;
   
        // ingest-source file
        File tempFile = new File(file.getTempSystemFileLocation()); 
        SDIOData sd = null;

        if (file.getControlCardSystemFileLocation() == null) {
            // A "classic", 1 file ingest:        

            String mime_type = file.getStudyFile().getFileType();

            infile = new BufferedInputStream(new FileInputStream(tempFile));

            dbgLog.info("\nfile mimeType="+mime_type+"\n\n");

            // get available FileReaders for this MIME-type
           Iterator<StatDataFileReader> itr =
                StatDataIO.getStatDataFileReadersByMIMEType(mime_type);

            if (itr.hasNext()){                 
                // use the first Subsettable data reader
                StatDataFileReader sdioReader = itr.next();

                dbgLog.info("reader class name="+sdioReader.getClass().getName());

                if (mime_type != null){
                    String requestedCharacterEncoding = file.getDataLanguageEncoding();
                    if (requestedCharacterEncoding != null) {
                        dbgLog.fine("Will try to process the file assuming that the character strings are "
                                + "encoded in "+requestedCharacterEncoding);
                        sdioReader.setDataLanguageEncoding(requestedCharacterEncoding);
                    }
                    sd = sdioReader.read(infile, null);
                } else {
                    // fail-safe block if mime_type is null
                    // check the format type again and then read the file
                    dbgLog.info("mime-type was null: use the back-up method");
                    sd = StatDataIO.read(infile, null);
                }
            } else {
                
                throw new IllegalArgumentException("No FileReader Class found" +
                    " for this mime type="+ mime_type);
            }
        } else {
            // This is a 2-file ingest.
            // As of now, there are 2 supported methods: 
            // 1. CSV raw data file + SPSS control card;
            // 2. TAB raw data file + DDI control card;
            // NOTE, that "POR file with the Extended Labels" is NOT a 2-file.
            // control card-based ingest! Rather, we ingest the file as a regular
            // SPSS/POR dataset, then modify the variable labels in the resulting
            // TabularFile. 
            
            
            File rawDataFile = tempFile;
            
            infile = new BufferedInputStream(new FileInputStream(file.getControlCardSystemFileLocation()));

            String controlCardType = file.getControlCardType();

            if (controlCardType == null || controlCardType.equals("")) {
                dbgLog.info("No Control Card Type supplied.");

                throw new IllegalArgumentException("No Control Card Type supplied.");
            }

            Iterator<StatDataFileReader> itr =
                StatDataIO.getStatDataFileReadersByFormatName(controlCardType);

            if (!itr.hasNext()){
                dbgLog.info("No FileReader class found for "+controlCardType+".");

                throw new IllegalArgumentException("No FileReader Class found for " +
                    controlCardType+".");
            }

            StatDataFileReader sdioReader = itr.next();

            dbgLog.info("reader class name="+sdioReader.getClass().getName());

            sd = sdioReader.read(infile, rawDataFile);
            
        }
        
        if (sd != null) {
        SDIOMetadata smd = sd.getMetadata();

        // tab-file: source file
        String tabDelimitedDataFileLocation =
        smd.getFileInformation().get("tabDelimitedDataFileLocation").toString();

        dbgLog.fine("tabDelimitedDataFileLocation="+tabDelimitedDataFileLocation);

        dbgLog.fine("data file(tempFile): abs path:\n"+file.getTempSystemFileLocation());
        dbgLog.fine("mimeType :\n"+file.getStudyFile().getFileType());


        if (infile != null){
            infile.close();
        }

        // parse the response
        StudyFile f = file.getStudyFile();

        // first, check dir
        // create a sub-directory "ingested"
        File newDir = new File(tempFile.getParentFile(), "ingested");

        if (!newDir.exists()) {
            newDir.mkdirs();
        }
        dbgLog.fine("newDir: abs path:\n"+newDir.getAbsolutePath());

        // tab-file case: destination
        File newFile = new File( newDir, tempFile.getName() );

        // nio-based file-copying idiom
        FileInputStream fis = new FileInputStream(tabDelimitedDataFileLocation);
        FileOutputStream fos = new FileOutputStream(newFile);
        FileChannel fcin = fis.getChannel();
        FileChannel fcout = fos.getChannel();
        fcin.transferTo(0, fcin.size(), fcout);
        fcin.close();
        fcout.close();
        fis.close();
        fos.close();

        dbgLog.fine("newFile: abs path:\n"+newFile.getAbsolutePath());

        // store the tab-file location
        file.setIngestedSystemFileLocation(newFile.getAbsolutePath());
        
        // finally, if we have an extended variable map, let's replace the 
        // labels that have been found in the data file: 
        
        if (file.getExtendedVariableLabelMap() != null) {
            for (String varName : file.getExtendedVariableLabelMap().keySet()) {
                if (smd.getVariableLabel().containsKey(varName)) {
                    smd.getVariableLabel().put(varName,file.getExtendedVariableLabelMap().get(varName));
                }
            }
        }

        // return xmlToParse;
        DDIWriter dw = new DDIWriter(smd);
        ddi = dw.generateDDI();


        return ddi;
        }
        return null; 
    }

    
    public void ingestSpecialOther(StudyFileEditBean file) throws IOException {
        dbgLog.fine("***** DSBWrapper: ingestSpecialOther(): start *****\n");

        File tempFile = new File(file.getTempSystemFileLocation());

        BufferedInputStream infile = null;

        String mime_type = file.getStudyFile().getFileType();

        infile = new BufferedInputStream(new FileInputStream(tempFile));

        dbgLog.info("\nfile mimeType=" + mime_type + "\n\n");

        if (mime_type == null || mime_type.equals("")) {
            throw new IllegalArgumentException("No mime type provided!");
        }

        // get available FileIngesters for this MIME-type
        FileIngester fileIngester = null; 
        Iterator<FileIngester> itr = OtherFileIngestSP.getFileIngestersByMIMEType(mime_type);
        Map<String, Set<String>> fileLevelMetadata = null;

        if (itr.hasNext()) {
            // use the first Subsettable data reader
            fileIngester = itr.next();

            dbgLog.info("reader class name=" + fileIngester.getClass().getName());
            dbgLog.info("format name=" + fileIngester.getFormatName()); 


            fileLevelMetadata = fileIngester.ingest(infile);

        } else {

            throw new IllegalArgumentException("No FileReader Class found"
                    + " for this mime type=" + mime_type);
        }
        
        // attempt to ingest the extracted metadata into the database; 
        // TODO: this should throw an exception if anything goes wrong.
        StudyFile studyFile = file.getStudyFile();
        FileMetadata fileMetadata = file.getFileMetadata();

        
        if (fileLevelMetadata != null) {
            //ingestFileLevelMetadata(fileLevelMetadata, file.getFileMetadata(), fileIngester.getFormatName());
            ingestFileLevelMetadata(fileLevelMetadata, studyFile, fileMetadata, fileIngester.getFormatName());
        }
    }
    
    public static void validateControlCard(File controlCardFile, String controlCardType)throws IOException{
        dbgLog.fine("***** DSBWrapper: validateControlCard(): start *****\n");

        boolean cardIsValid = false;

        if (controlCardType != null) {
            // As of now, there are 2 supported control card-based ingests:
            // 1. CSV raw data file + SPSS control card;
            // 2. TAB raw data file + DDI control card;
            // plus
            // 3. SPSS/Por data file + extended variable labels in a text file,
            // which is not a control card ingest, strictly speaking, but 
            // we are using the same "2 file ingest" framework for it.  

            if ("porextra".equals(controlCardType)) {
                // Should be validated for simple consistency - each line 
                // must contain at least one TAB character, separating 2 
                // non-empty character strings.
                if (parseLabelFile(controlCardFile)) {
                    return; 
                }
                throw new IllegalArgumentException("Ingest: Bad Extended Labels File.");
            }
            
            // We are going to check if we have Ingest readers for the
            // control card supplied:

            Iterator<StatDataFileReader> itr =
                StatDataIO.getStatDataFileReadersByFormatName(controlCardType);

            if (!itr.hasNext()){
                dbgLog.info("No FileReader class found for "+controlCardType+".");

                throw new IllegalArgumentException("No FileReader Class found for " +
                    controlCardType+".");
            }

            StatDataFileReader sdioReader = itr.next();

            dbgLog.fine("Validate: reader class name="+sdioReader.getClass().getName()+"; validating card.");

            cardIsValid = sdioReader.isValid(controlCardFile);

        } else {
            dbgLog.info("No Control Card Type supplied.");

            throw new IllegalArgumentException("No Control Card Type supplied.");

        }

        if ( !cardIsValid ) {
            dbgLog.info("Control card is NOT valid! No further diagnostics is available");
            throw new IllegalArgumentException("Control card is NOT valid! No further diagnostics is available");
        }


        dbgLog.fine("Validate: control card file is valid.");
    }

    private static Boolean parseLabelFile (File extendedLabelsFile) {
        
        // open the text file supplied, check that they are at least 
        // parseable.
        
        BufferedReader labelsFileReader = null;
        
        try {
            labelsFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(extendedLabelsFile)));
            
            String inLine = null; 
            String[] valueTokens = new String[2];
            
            while ((inLine = labelsFileReader.readLine() ) != null) {
                valueTokens = inLine.split("\t", 2);
                
                if (!(valueTokens[0] != null && !"".equals(valueTokens[0]) &&
                    valueTokens[1] != null && !"".equals(valueTokens[1]))) {
                    
                    return false;
                }
            }
            
        } catch (java.io.FileNotFoundException fnfex) {
            dbgLog.warning("Ingest: could not open Extended Labels file");
            dbgLog.warning(fnfex.getMessage());
            return false;
        } catch (IOException ioex) {
            dbgLog.warning("Ingest: caught exception trying to process Labels File");
            dbgLog.warning(ioex.getMessage());
            return false;
        } finally {
            if (labelsFileReader != null) {
                try {labelsFileReader.close();}catch(Exception x){};
            }
        }
        
        return true;
        
    }

    public String calculateUNF(StudyVersion sv) throws IOException {
        List unfs = new ArrayList();
        for (FileMetadata fmd : sv.getFileMetadatas()) {
            StudyFile temp = fmd.getStudyFile();
            if (temp.isUNFable()) {
                unfs.add(temp.getUnf());
            }
        }
        
        if (unfs.size() == 0) {
            // no subsettable files
            return null;
        } else {
            String fileUNF=null;
          
            fileUNF = UNF5Util.calculateUNF(unfs);
            //return calculateUNF(unfs);
          
            return fileUNF;
        }
    }
    
    private void ingestFileLevelMetadata (Map<String, Set<String>> fileLevelMetadata, StudyFile studyFile, FileMetadata fileMetadata, String fileFormatName) {
        // First, add the "metadata summary" generated by the file reader/ingester
        // to the fileMetadata object, as the "description":
        
        Set<String> metadataSummarySet = fileLevelMetadata.get(METADATA_SUMMARY); 
        if (metadataSummarySet != null && metadataSummarySet.size() > 0) {
            String metadataSummary = ""; 
            for (String s : metadataSummarySet) {
                metadataSummary = metadataSummary.concat(s);
            }
            if (!metadataSummary.equals("")) {
                // The AddFiles page allows a user to enter file description 
                // on ingest. We don't want to overwrite whatever they may 
                // have entered. Rather, we'll append our metadata summary 
                // to the existing value. 
                String userEnteredFileDescription = fileMetadata.getDescription();
                if (userEnteredFileDescription != null 
                        && !(userEnteredFileDescription.equals(""))) {
                    
                    metadataSummary = 
                            userEnteredFileDescription.concat("\n"+metadataSummary);
                }
                fileMetadata.setDescription(metadataSummary);
            }
            
            fileLevelMetadata.remove(METADATA_SUMMARY);
        }
        
        // And now we can go through the remaining key/value pairs in the 
        // metadata maps and process the metadata elements found in the 
        // file: 
        
        for (String mKey : fileLevelMetadata.keySet()) {
            
            Set<String> mValues = fileLevelMetadata.get(mKey); 
            
            // Check if the field doesn't exist yet:
            
            try {
                Context ctx = new InitialContext();
                studyFieldService = (StudyFieldServiceLocal) ctx.lookup("java:comp/env/studyField"); 
            } catch (Exception ex) {
                dbgLog.info("Caught an exception looking up StudyField Service; "+ex.getMessage());
            }
            if (studyFieldService == null) {
                dbgLog.warning("No StudyField Service; exiting file-level metadata ingest.");
                return; 
            }
            
            dbgLog.fine("Looking up file meta field "+mKey+", file format "+fileFormatName);
            FileMetadataField fileMetaField = studyFieldService.findFileMetadataFieldByNameAndFormat(mKey, fileFormatName);
            
            if (fileMetaField == null) {
                //fileMetaField = studyFieldService.createFileMetadataField(mKey, fileFormatName); 
                fileMetaField = new FileMetadataField(); 
                
                if (fileMetaField == null) {
                    dbgLog.warning("Failed to create a new File Metadata Field; skipping.");
                    continue; 
                }
                
                fileMetaField.setName(mKey);
                fileMetaField.setFileFormatName(fileFormatName);               
                // TODO: provide meaningful descriptions and labels:
                fileMetaField.setDescription(mKey);
                fileMetaField.setTitle(mKey); 
                
                try {
                    studyFieldService.saveFileMetadataField(fileMetaField);
                } catch (Exception ex) {
                    dbgLog.warning("Failed to save new file metadata field ("+mKey+"); skipping values.");
                    continue; 
                }
                
                dbgLog.fine("Created file meta field "+mKey); 
            }
            
            String fieldValueText = null;
            
            if (mValues != null) {
                for (String mValue : mValues) {
                    if (mValue != null) {
                        if (fieldValueText == null) {
                            fieldValueText = mValue;
                        } else {
                            fieldValueText = fieldValueText.concat(" ".concat(mValue)); 
                        }
                    } 
                }   
            }
            
            FileMetadataFieldValue fileMetaFieldValue = null; 
            
            if (!"".equals(fieldValueText)) {
                dbgLog.fine("Attempting to create a file meta value for study file "+studyFile.getId()+", value "+fieldValueText);
                if (studyFile != null) {
                    fileMetaFieldValue =
                            new FileMetadataFieldValue(fileMetaField, studyFile, fieldValueText);
                }
            }
            if (fileMetaFieldValue == null) {
                dbgLog.warning ("Failed to create a new File Metadata Field value; skipping");
                continue;
            } else {
                if (studyFile.getFileMetadataFieldValues() == null) {
                    studyFile.setFileMetadataFieldValues(new ArrayList<FileMetadataFieldValue>());
                }
                studyFile.getFileMetadataFieldValues().add(fileMetaFieldValue);
            }
        }
    }
    
    /*
    public String calculateUNF(List unfs) throws IOException{
        PostMethod method = null;
        
        try {
            // create method
            method = new PostMethod(generateUrl(DSB_CALCULATE_UNF));
            Iterator iter = unfs.iterator();
            while (iter.hasNext()) {
                String unf = (String) iter.next();
                method.addParameter("unf", unf);
            }
            
            // execute
            executeMethod(method);
            
            // parse the response
            String unf = method.getResponseBodyAsString();
            if (unf != null) {
                unf = unf.trim();
            }
            return unf;
            
        } finally {
            if (method != null) { method.releaseConnection(); }
        }
    }
    * */
    
    
    /*
    public void disseminate(HttpServletResponse res, TabularDataFile tdf, String serverPrefix, String formatType) throws IOException{
        Map parameters = new HashMap();
        List variables = tdf.getDataTable().getDataVariables();
        disseminate(res, parameters, tdf, serverPrefix, variables, formatType );
    }
    
    public void disseminate(HttpServletResponse res, Map parameters, StudyFile sf, String serverPrefix, List variables, String formatType) throws IOException{
        if (parameters == null) {
            parameters = new HashMap();
        }
        parameters.put("dtdwnld", formatType);
        disseminate(res, parameters, sf, serverPrefix, variables);
    }
    
    public void disseminate(HttpServletResponse res, Map parameters, StudyFile sf, String serverPrefix, List variables) throws IOException{
        // add paramters for the file
        if (parameters == null) {
            parameters = new HashMap();
        }
        
        parameters.put("uri", generateUrlForDDI(serverPrefix, sf.getId()));
        parameters.put("URLdata", generateUrlForFile(serverPrefix, sf.getId()));
        parameters.put("fileid", "f" + sf.getId().toString());
        parameters.put("varbl", generateVariableListForDisseminate( variables ) );
        
        disseminate(res, parameters);
    }
    
    public void disseminate(HttpServletResponse res, Map parameters) throws IOException{
        PostMethod method = null;
        InputStream in = null;
        OutputStream out = null;
        
        try {
            // create method
            method = new PostMethod(generateUrl(DSB_DISSEMINATE));
            
            // generate parameters
            Iterator iter = parameters.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                Object value = parameters.get(key);
                
                if (value instanceof String) {
                    method.addParameter(key, (String) value);
                } else if (value instanceof List) {
                    Iterator valueIter = ((List) value).iterator();
                    while (valueIter.hasNext()) {
                        String item = (String) valueIter.next();
                        method.addParameter(key, (String) item);
                    }
                }
            }
            
            
            String debug = "Disseminate - Method Parameters:\n";
            for (int i = 0; i < method.getParameters().length; i++) {
                debug += "\n" + method.getParameters()[i].getName() + " = '";
                debug += method.getParameters()[i].getValue() + "'";
            }
            System.out.println(debug);
            HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
            if (request.getHeader("X-Forwarded-For") != null)
                method.addRequestHeader("X-Forwarded-For", request.getHeader("X-Forwarded-For"));
            else
                method.addRequestHeader("X-Forwarded-For", "NULL-HEADER-X-FORWARDED-FOR");
            // execute
            executeMethod(method);
            
            debug = "Disseminate - Recycled response headers:\n";
            // set headers on the response
            for (int i = 0; i < method.getResponseHeaders().length; i++) {
                String headerName = method.getResponseHeaders()[i].getName();
                if (headerName.startsWith("Content")) {
                    String headerValue = method.getResponseHeaders()[i].getValue();
                    debug += "\n" + headerName + " = '" + headerValue + "'";
                    res.setHeader(headerName, headerValue);
                }
            }
            System.out.println(debug);
            
            in = method.getResponseBodyAsStream();
            out = res.getOutputStream();
            
            
            byte[] dataBuffer = new byte[8192];
            
            int i = 0;
            while ( ( i = in.read(dataBuffer) ) > 0 ) {
                out.write(dataBuffer,0,i);
                out.flush();
            }
            
            
            
        } finally {
            if (method != null) { method.releaseConnection(); }
            if (in != null) { in.close(); }
            if (out != null) { out.close(); }
        }
    }
    */
    /*
    public List generateVariableListForDisseminate(List dvs) {
        List variableList = new ArrayList();
        if (dvs != null) {
            Iterator iter = dvs.iterator();
            while (iter.hasNext()) {
                DataVariable dv = (DataVariable) iter.next();
                variableList.add("v" + dv.getId());
            }
        }
        return variableList;
    }
    * */
    
    /*
    private String generateUrlForDDI(String serverPrefix, Long fileId) {
        String studyDDI = serverPrefix + "/ddi/?fileId=" + fileId;
        System.out.println(studyDDI);
        return studyDDI;
    }
    
    private String generateUrlForFile(String serverPrefix, Long fileId) {
        //get the xFF arg -- used by the web stats txt report
        WebStatisticsSupport webstatistics = new WebStatisticsSupport();
        int headerValue = webstatistics.getParameterFromHeader("X-Forwarded-For");
        String xff = webstatistics.getQSArgument("xff", headerValue);
        String file = serverPrefix + "/FileDownload/?fileId=" + fileId + "&isSSR=1" + xff;
        System.out.println(file);
        return file;
    }
    * */

    
    /* the method below, isDSBRequest() is still being used by some servlets, 
     * so it can't be removed from here just yet. however, there's really no 
     * need for the method to be used anywhere in the app - since we don't have 
     * a DSB server that may call the app back anymore... -- L.A. 
     */
    public static boolean isDSBRequest(HttpServletRequest req) {
        boolean dsbRequest = false;
        
        String dsbHost = System.getProperty("vdc.dsb.host");

        if ( dsbHost == null ) {
            // vdc.dsb.host isn't set; 
            // fall back to the old-style option: 
            dsbHost = System.getProperty("vdc.dsb.url");
        }          


        if ( dsbHost.equals(req.getRemoteHost()) ) {
            dsbRequest = true; 
        } else { 
            try {
                String dsbHostIPAddress = InetAddress.getByName(dsbHost).getHostAddress(); 
                if ( dsbHostIPAddress.equals(req.getRemoteHost()) ) {
                    dsbRequest = true;
                }
            } catch ( UnknownHostException ex ) {
                // do nothing; 
                // the "vdc.dsb.host" setting is clearly misconfigured,
                // so we just keep assuming this is NOT a DSB call
            }
        }

        return dsbRequest;
       
    }
}
