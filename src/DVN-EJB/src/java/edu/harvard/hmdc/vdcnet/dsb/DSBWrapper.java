/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/*
 * DSBWrapper.java
 *
 * Created on November 1, 2006, 3:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.dsb;

import edu.harvard.hmdc.vdcnet.study.DataVariable;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.StudyFileEditBean;
import edu.harvard.hmdc.vdcnet.util.WebStatisticsSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

/**
 *
 * @author gdurand
 */
public class DSBWrapper {
    
    private HttpClient client = null;
    
    private static final String DSB_ANALYZE = "Analyze";
    private static final String DSB_INGEST = "Ingest";
    private static final String DSB_CALCULATE_UNF = "CalculateUNF";
    private static final String DSB_GET_ZELIG_CONFIG = "GetZeligConfig";
    private static final String DSB_DISSEMINATE = "Disseminate";
    
    private static final String FORMAT_TYPE_TAB = "D01";
    private static final String FORMAT_TYPE_SPLUS = "D02";
    private static final String FORMAT_TYPE_STATA = "D03";
    private static final String FORMAT_TYPE_R = "D03";
    
    
    /** Creates a new instance of DSBWrapper */
    public DSBWrapper() {
    }
    
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
    
    private HttpClient getClient() {
        if (client == null) {
            client = new HttpClient( new MultiThreadedHttpConnectionManager() );
        }
        return client;
    }
    
    private void executeMethod(PostMethod method) throws IOException {
        int state = getClient().executeMethod(method);
        
        if (state != 200) {
            throw new IOException(
                    (method.getStatusLine() != null)
                    ? method.getStatusLine().toString()
                    : "DSB Error");
        }
    }
    
    
    public String analyze(File f) throws IOException{
        BufferedReader rd = null;
        PostMethod method = null;
        
        try {
            String fileType = null;
            
            // create method
            method = new PostMethod(generateUrl(DSB_ANALYZE));
            method.addParameter("file_name", f.getName());
            method.addParameter("file_header", new String(Base64.encodeBase64(getHeaderFromFile(f))) );
            
            // execute
            executeMethod(method);
            
            // parse the response
            rd = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()  ));
            String line;
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
                int startIndex = line.indexOf("<mime>");
                if (startIndex != -1) {
                    int endIndex = line.indexOf("</mime>");
                    fileType = line.substring( startIndex+6,endIndex );
                    //break;
                }
            }
            
            return fileType;
            
        } finally {
            if (method != null) { method.releaseConnection(); }
            try {
                if (rd != null) { rd.close(); }
            } catch (IOException ex) {
            }
        }
    }
    
    private static byte[] getHeaderFromFile(File f) throws IOException{
        FileInputStream fin = null;
        try {
            byte[] header = new byte[1024];
            fin = new FileInputStream(f);
            
            // Get the first 'headerLength' bytes from the file
            if (f.length() > 1024) {
                fin.read(header, 0, 1024);
            } else {
                //byte[] b = new byte[(int) f.length()];
                header = new byte[(int) f.length()];
                //fin.read(b);
                fin.read(header);
            }
            
            return header;
            
        } finally {
            try {
                if (fin != null) { fin.close(); }
            } catch (IOException ex) {
            }
        }
    }
    
    public String ingest(StudyFileEditBean file) throws IOException{
        BufferedReader rd = null;
        PostMethod method = null;
        
        try {
            // create method
            method = new PostMethod(generateUrl(DSB_INGEST));
            File tempFile = new File(file.getTempSystemFileLocation());
            
            if (file.getControlCardSystemFileLocation() == null) {
                Part[] parts = {
                    new FilePart( "dataFile0", tempFile ),
                    new StringPart( "dataFileMime0", file.getStudyFile().getFileType() )
                };
                
                method.setRequestEntity(
                        new MultipartRequestEntity(parts, method.getParams())
                        );
            } else {
                File controlCardFile = new File(file.getControlCardSystemFileLocation() );
                
                Part[] parts = {
                    new FilePart( "dataFile0", tempFile ),
                    new StringPart( "dataFileMime0", file.getStudyFile().getFileType() ),
                    new FilePart( "controlCard0", controlCardFile )
                };
                
                method.setRequestEntity(
                        new MultipartRequestEntity(parts, method.getParams())
                        );
            }
            
            // execute
            executeMethod(method);
            
            // parse the response
            StudyFile f = file.getStudyFile();
            
            // first, check dir
            File newDir = new File(tempFile.getParentFile(), "ingested");
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            
            rd = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()  ));
            String boundary = "=vdc-ingest-multipart";
            String xmlToParse = "<codeBook xmlns=\"http://www.icpsr.umich.edu/DDI\">\n";
            String line = rd.readLine();
            while (line != null) {
                if (line.equals("--" + boundary + "--")) {
                    break; // this is the last line of the file
                    
                } else if (line.equals("--" + boundary)) {
                    // we are at the beginning of a part
                    line = rd.readLine(); // second line should be content type
                    String contentType = line.substring( line.indexOf("Content-Type: ") + 14 );
                    line = rd.readLine(); // next line is blank
                    line = rd.readLine(); // now begin reading this part
                    
                    if ( contentType.equals("text/tab-separated-values") ) {
                        File newFile = new File( newDir, tempFile.getName() );
                        BufferedWriter out = new BufferedWriter(new FileWriter(newFile));
                        while ( !line.startsWith("--" + boundary) ) {
                            out.write(line + "\n");
                            line = rd.readLine();
                        }
                        file.setIngestedSystemFileLocation(newFile.getAbsolutePath());
                        out.close();
                    } else if ( contentType.equals("text/xml") ) {
                        while ( !line.startsWith("--" + boundary) ) {
                            xmlToParse += line + "\n";
                            line = rd.readLine();
                        }
                    }
                    
                } else {
                    line = rd.readLine();
                }
            }
            xmlToParse += "</codeBook>";
            return xmlToParse;
            
        } finally {
            if (method != null) { method.releaseConnection(); }
            try {
                if (rd != null) { rd.close(); }
            } catch (IOException ex) {
            }
        }
    }
    
    public String calculateUNF(Study s) throws IOException {
        List unfs = new ArrayList();
        Iterator iter = s.getStudyFiles().iterator();
        while (iter.hasNext()) {
            StudyFile temp = (StudyFile) iter.next();
            if (temp.isSubsettable()) {
                unfs.add(temp.getDataTable().getUnf());
            }
        }
        
        if (unfs.size() == 0) {
            // no subsettable files
            return null;
        } else {
            return calculateUNF(unfs);
        }
    }
    
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
    
    public String getZeligConfig() throws IOException{
        PostMethod method = null;
        
        try {
            // create method
            method = new PostMethod(generateUrl(DSB_GET_ZELIG_CONFIG));
            
            // execute
            executeMethod(method);
            
            // parse the response
            String zeligConfig = method.getResponseBodyAsString();
            return zeligConfig;
            
        } finally {
            if (method != null) { method.releaseConnection(); }
        }
    }
    
    public void disseminate(HttpServletResponse res, StudyFile sf, String serverPrefix, String formatType) throws IOException{
        Map parameters = new HashMap();
        List variables = sf.getDataTable().getDataVariables();
        disseminate(res, parameters, sf, serverPrefix, variables, formatType );
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
}
