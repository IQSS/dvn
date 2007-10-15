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
 * FileDownloadServlet.java
 *
 * Created on October 12, 2006, 6:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.servlet;


import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.study.FileCategory;
import edu.harvard.hmdc.vdcnet.study.DataVariable;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.study.FileCategoryUI;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map; 
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.ejb.EJB;
import javax.ejb.EJBException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod; 
import org.apache.commons.httpclient.methods.PostMethod; 

/**
 *
 * @author gdurand
 */
public class FileDownloadServlet extends HttpServlet{

    private HttpClient client = null;
    
    /** Creates a new instance of FileDownloadServlet */
    public FileDownloadServlet() {
    }

    private HttpClient getClient() {
        if (client == null) {
            client = new HttpClient( new MultiThreadedHttpConnectionManager() );
        }
        return client;
    }

    private String generateDisseminateUrl() throws IOException{
        String dsbHost = System.getProperty("vdc.dsb.host");
	String dsbPort = System.getProperty("vdc.dsb.port");
        
        if (dsbHost != null) {
	    if (dsbPort != null ) {
		return "http://" + dsbHost + ":" + dsbPort + "/VDC/DSB/0.1/Disseminate";
	    } else {
		return "http://" + dsbHost + "/VDC/DSB/0.1/Disseminate";
	    }
        } else {
	    // fall back to the old "vdc.dsb.url" option 
	    dsbHost = System.getProperty("vdc.dsb.url");

	    if (dsbHost != null) {
		return "http://" + dsbHost + "/VDC/DSB/0.1/Disseminate";
	    } else {	    
		throw new IOException("System property \"vdc.dsb.host\" has not been set.");
	    }
        }
        
    }

    @EJB StudyServiceLocal studyService;
    @EJB VDCServiceLocal vdcService;
  
    public void service(HttpServletRequest req, HttpServletResponse res)  {
        VDCUser user = null;
        if ( LoginFilter.getLoginBean(req) != null ) {
            user= LoginFilter.getLoginBean(req).getUser();
        }
        VDC vdc = vdcService.getVDCFromRequest(req);
        UserGroup ipUserGroup= null;
        if (req.getSession(true).getAttribute("ipUserGroup") != null) {
            ipUserGroup= (UserGroup)req.getSession().getAttribute("ipUserGroup");
        }
        String fileId = req.getParameter("fileId");
	String formatRequested = req.getParameter("format");
	String downloadOriginalFormat = req.getParameter("downloadOriginalFormat");


        if (fileId != null) {

	    StudyFile file = null; 

	    try {
		file = studyService.getStudyFile( new Long(fileId));

	    } catch (Exception ex) {
		if (ex.getCause() instanceof IllegalArgumentException) {
		    createErrorResponse404(res);
		    return;
		}
	     }
	    
	    if ( file == null ) {
	        // this check is probably unnecessary, as a non-existing file
		// would already have produced the exception above (??)
		// -- not sure, gotta ask Gustavo 

		createErrorResponse404(res);
		return;
	     }		    

	    // determine if the fileId represents a local object 
	    // or a remote URL

	    if ( file.isRemote() ) {

		// do the http magic

		if (formatRequested != null) {
		    // user requested the file in a non-default (i.e.,
		    // not tab-delimited) format.

		    // we are going to send a format conversion request to 
		    // the DSB via HTTP.
		    
		    Map parameters = new HashMap();		    

		    parameters.put("dtdwnld", formatRequested); 

		    String serverPrefix = req.getScheme() +"://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();

		    parameters.put("uri", generateUrlForDDI(serverPrefix, file.getFileCategory().getStudy().getId()));
		    parameters.put("URLdata", generateUrlForFile(serverPrefix, file.getId()));
		    parameters.put("fileid", "f" + file.getId().toString());
			
		    // We are requesting a conversion of the whole datafile. 
		    // I was positive there was a simple way to ask univar (Disseminate)
		    // for "all" variables; but I'm not so sure anymore. So 
		    // far I've only been able to do this by listing all the 
		    // variables in the datafile and adding them to the request.
		    // :( -- L.A.

		    List variables = file.getDataTable().getDataVariables();
		    parameters.put("varbl", generateVariableListForDisseminate( variables ) );
		    parameters.put("wholefile", "true");

		    PostMethod method = null; 
		    int status = 200;		    
			
		    try { 
			method = new PostMethod (generateDisseminateUrl());

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

			status = getClient().executeMethod(method);
			
		    } catch (IOException ex) {
			// return 404 and
			// generate generic error messag:
			
			status = 404; 
			createErrorResponseGeneric( res, status, (method.getStatusLine() != null)
						    ? method.getStatusLine().toString()
						    : "DSB conversion failure");
			if (method != null) { method.releaseConnection(); }
			return;
		    }
		    
		    if ( status != 200 ) {
			createErrorResponseGeneric( res, status, (method.getStatusLine() != null)
						    ? method.getStatusLine().toString()
						    : "DSB conversion failure");
			if (method != null) { method.releaseConnection(); }
			return;
		    }

		    try {
			// recycle all the incoming headers 
			for (int i = 0; i < method.getResponseHeaders().length; i++) {
			    String headerName = method.getResponseHeaders()[i].getName();
			    if (headerName.startsWith("Content")) {
				res.setHeader(method.getResponseHeaders()[i].getName(), method.getResponseHeaders()[i].getValue());
			    }
			}
		    
			// send the incoming HTTP stream as the response body

			InputStream in = method.getResponseBodyAsStream(); 
			OutputStream out = res.getOutputStream();
			    
			byte[] dataBuffer = new byte[8192]; 

			int i = 0;
			while ( ( i = in.read (dataBuffer) ) > 0 ) {
			    out.write(dataBuffer,0,i);
			    out.flush(); 
			}

			in.close();
			out.close();
                
                
		    } catch (IOException ex) {
			ex.printStackTrace();
		    }
		    
		    method.releaseConnection();
		} else {
		    GetMethod method = null; 
		    int status = 200;

		    try { 
			method = new GetMethod ( file.getFileSystemLocation() );
			status = getClient().executeMethod(method);
		    } catch (IOException ex) {
			// return 404 
			// and generate a FILE NOT FOUND message

			createErrorResponse404(res);
			if (method != null) { method.releaseConnection(); }
			return;
		    }


		    if ( status == 403 ) {
			// generate an HTML-ized response with a correct 
			// 403/FORBIDDEN code   

			createErrorResponse403(res);
			if (method != null) { method.releaseConnection(); }
			return;
		    } 

		    if ( status == 404 ) {
			// generate an HTML-ized response with a correct 
			// 404/FILE NOT FOUND code   

			createErrorResponse404(res);
			if (method != null) { method.releaseConnection(); }
			return;
		    } 

		    // a generic response for all other failure cases:
		    
		    if ( status != 200 ) {
			createErrorResponseGeneric( res, status, (method.getStatusLine() != null)
						    ? method.getStatusLine().toString()
						    : "Unknown HTTP Error");
			if (method != null) { method.releaseConnection(); }
			return;
		    }

		    try {
			// recycle all the incoming headers 
			for (int i = 0; i < method.getResponseHeaders().length; i++) {
			    res.setHeader(method.getResponseHeaders()[i].getName(), method.getResponseHeaders()[i].getValue());
			}

		    
			// send the incoming HTTP stream as the response body

			InputStream in = method.getResponseBodyAsStream(); 
			OutputStream out = res.getOutputStream();
			
			byte[] dataBuffer = new byte[8192]; 

			int i = 0;
			while ( ( i = in.read (dataBuffer) ) > 0 ) {
			    out.write(dataBuffer,0,i);
			    out.flush(); 
			}

			in.close();
			out.close();
                
			
		    } catch (IOException ex) {
			ex.printStackTrace();
		    }

		    method.releaseConnection();
		}
	    } else {
		// local object
		
		String dsbHost = System.getProperty("vdc.dsb.host");

		// fall back to the old-style option: 

		if ( dsbHost == null ) {
		    dsbHost = System.getProperty("vdc.dsb.url");
		}		   

		boolean NOTaDSBrequest = true;
  
		if ( dsbHost.equals(req.getRemoteHost()) ) {
		    NOTaDSBrequest = false; 
		} else { 
		    try {
			String dsbHostIPAddress = InetAddress.getByName(dsbHost).getHostAddress(); 
			if ( dsbHostIPAddress.equals(req.getRemoteHost()) ) {
			    NOTaDSBrequest = false;
			}
		    } catch ( UnknownHostException ex ) {
			// do nothing; 
			// the "vdc.dsb.host" setting is clearly misconfigured,
			// so we just keep assuming this is NOT a DSB call
		    }
		}
		
		if ( NOTaDSBrequest && file.isRestricted() && (user == null || file.isFileRestrictedForUser(user, vdc,ipUserGroup)) ) {
		    // generate a response with a correct 403/FORBIDDEN code   

		    createErrorResponse403(res);
		    return;
		}

		studyService.incrementNumberOfDownloads(file.getFileCategory().getStudy().getId());

		if (formatRequested != null) {

		    // user requested the file in a non-default (i.e.,
		    // not tab-delimited) format.

		    // First, let's check if we have this file cached.

		    String cachedFileSystemLocation = file.getFileSystemLocation() + "." + formatRequested; 

		    if ( new File(cachedFileSystemLocation).exists() ) {
			try {
			    String cachedAltFormatType = generateAltFormat ( formatRequested ); 
			    String cachedAltFormatFileName = generateAltFileName ( formatRequested, "f" + file.getId().toString() ); 

			    res.setHeader ( "Content-disposition",
					    "attachment; filename=\"" + cachedAltFormatFileName + "\"" ); 
			    res.setHeader ( "Content-Type",
						 cachedAltFormatType + "; name=\"" + cachedAltFormatFileName + "\"; charset=ISO-8859-1" ); 
				
			
			    // send the file as the response
			    InputStream in = new FileInputStream(new File(cachedFileSystemLocation));
			    OutputStream out = res.getOutputStream();
			
			    byte[] dataBuffer = new byte[8192]; 

			    int i = 0;
			    while ( ( i = in.read (dataBuffer) ) > 0 ) {
				out.write(dataBuffer,0,i);
				out.flush(); 
			    }
			    in.close();
			    out.close();
                
			
			} catch (IOException ex) {
			    ex.printStackTrace();
			}

		    } else {

			// we are going to send a format conversion request to 
			// the DSB via HTTP.
		    
			Map parameters = new HashMap();		    

			parameters.put("dtdwnld", formatRequested); 

			String serverPrefix = req.getScheme() +"://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();

			parameters.put("uri", generateUrlForDDI(serverPrefix, file.getFileCategory().getStudy().getId()));
			parameters.put("URLdata", generateUrlForFile(serverPrefix, file.getId()));
			parameters.put("fileid", "f" + file.getId().toString());

			// We are requesting a conversion of the whole datafile. 
			// I was positive there was a simple way to ask univar (Disseminate)
			// for "all" variables; but I'm not so sure anymore. So 
			// far I've only been able to do this by listing all the 
			// variables in the datafile and adding them to the request.
			// :( -- L.A.

			List variables = file.getDataTable().getDataVariables();
			parameters.put("varbl", generateVariableListForDisseminate( variables ) );
			parameters.put("wholefile", "true");


			PostMethod method = null; 
			int status = 200;		    

			try { 
			    method = new PostMethod (generateDisseminateUrl());

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

			    status = getClient().executeMethod(method);
			
			} catch (IOException ex) {
			    // return 404 and
			    // generate generic error messag:

			    status = 404; 
			    createErrorResponseGeneric( res, status, (method.getStatusLine() != null)
							? method.getStatusLine().toString()
							: "DSB conversion failure");
			    if (method != null) { method.releaseConnection(); }
			    return;
			}

			if ( status != 200 ) {
			    createErrorResponseGeneric( res, status, (method.getStatusLine() != null)
							? method.getStatusLine().toString()
							: "DSB conversion failure");
			    if (method != null) { method.releaseConnection(); }
			    return;
			}

			try {
			    // recycle all the incoming headers 
			    for (int i = 0; i < method.getResponseHeaders().length; i++) {
				String headerName = method.getResponseHeaders()[i].getName();
				if (headerName.startsWith("Content")) {
				    res.setHeader(method.getResponseHeaders()[i].getName(), method.getResponseHeaders()[i].getValue());
				}
			    }


		    
			    // send the incoming HTTP stream as the response body
			    InputStream in = method.getResponseBodyAsStream(); 
			    OutputStream out = res.getOutputStream();

			    // Also, we want to cache this file for future use:

			    FileOutputStream fileCachingStream = new FileOutputStream(cachedFileSystemLocation);

			    byte[] dataBuffer = new byte[8192]; 

			    int i = 0;
			    while ( ( i = in.read (dataBuffer) ) > 0 ) {
				out.write(dataBuffer,0,i);
				fileCachingStream.write(dataBuffer,0,i); 
				out.flush(); 
			    }
			    in.close();
			    out.close();
			    fileCachingStream.flush(); 
			    fileCachingStream.close(); 
                
			} catch (IOException ex) {
			    ex.printStackTrace();
			}
		    
			method.releaseConnection();
		    }
		    
		} else {

		    // finally, the *true* local case, where we just 
		    // read the file off disk and send the stream back.
		   
		    try {
			// set content type to that stored in the db,
			// if available.
			
			String dbContentType = null; 

			if ( downloadOriginalFormat != null ) {
			    dbContentType = file.getOriginalFileType();
			} else {
			    dbContentType = file.getFileType();
			}

			// specify the file name, if available:
			String dbFileName = null; 

			if ( downloadOriginalFormat == null ) {
			    file.getFileName();
			}

			if ( dbFileName != null ) {
			    if ( dbContentType != null ) {

				// The "content-disposition" header is for the
				// Mozilla family of browsers;

				// about the commented out code below:
				//
				// the idea used to be that we should prompt 
				// for the content to open in the browser 
				// whenever possible; but lately it's been 
				// suggested by our users that all downloads
				// should behave the same, i.e. prompt the 
				// user to "save as" the file. 

				//if ( dbContentType.equalsIgnoreCase("application/pdf") || 
				//     dbContentType.equalsIgnoreCase("text/xml") || 
				//     dbContentType.equalsIgnoreCase("text/plain"))  {
				//    res.setHeader ( "Content-disposition",
				//		    "inline; filename=\"" + dbFileName + "\"" ); 
				//
				//} else {

				res.setHeader ( "Content-disposition",
						    "attachment; filename=\"" + dbFileName + "\"" ); 
			        //}

				// And this one is for MS Explorer: 
				res.setHeader ( "Content-Type",
						dbContentType + "; name=\"" + dbFileName + "\"; charset=ISO-8859-1" ); 
				
			    } else {
				// Have filename, but no content-type; 
				// All we can do is provide a Mozilla-friendly
				// header: 

				res.setHeader ( "Content-disposition",
						"attachment; filename=\"" + dbFileName + "\"" ); 
			    }
			} else {
			    // no filename available; 
			    // but if we have content-type in the database
			    // we'll just set that: 

			    if ( dbContentType != null ) {
				res.setContentType( dbContentType );
			    }
			}

			// open the appropriate physical file

			File inFile = new File(file.getFileSystemLocation());  

			if ( downloadOriginalFormat != null ) {
			    inFile = new File ( inFile.getParent(), "_" + file.getFileSystemName()); 
			} 

			// send the file as the response


			InputStream in = new FileInputStream(inFile);
			OutputStream out = res.getOutputStream();
			
			byte[] dataBuffer = new byte[8192]; 

			int i = 0;
			while ( ( i = in.read (dataBuffer) ) > 0 ) {
			    out.write(dataBuffer,0,i);
			    out.flush(); 
			}
			in.close();
			out.close();
                
			
		    } catch (IOException ex) {
			ex.printStackTrace();
		    }
		}
	    }
	} else {
	    // a request for a zip-packaged multiple 
	    // file archive.

            // first determine which files to archive.

            Study study = null;
            Collection files = new ArrayList();
            boolean createDirectoriesForCategories = false;
            
            String catId = req.getParameter("catId");
            String studyId = req.getParameter("studyId");
            
            if (catId != null) {
                try {
                    FileCategory cat = studyService.getFileCategory( new Long(catId));                 
                    study = cat.getStudy();
                    files = cat.getStudyFiles();
                } catch (Exception ex) {
                    if (ex.getCause() instanceof IllegalArgumentException) {
                        createErrorResponse404(res);
                        return;
                    }
                }                
            } else if (studyId != null) {
                try {
                    study = studyService.getStudy( new Long(studyId));
                    files = study.getStudyFiles();
                    createDirectoriesForCategories = true;
                } catch (Exception ex) {
                    if (ex.getCause() instanceof IllegalArgumentException) {
                        createErrorResponse404(res);
                        return;
                    }
                }                    
            } else {
                createErrorResponse404(res);    
                return;
            }
 
            // check for restricted files
            Iterator iter = files.iterator();
            while (iter.hasNext()) {
                StudyFile file = (StudyFile) iter.next();
                if (file.isRestricted() && (user == null || file.isFileRestrictedForUser(user, vdc,ipUserGroup)) ) {
                    iter.remove();
                }  
            }
            if (files.size() == 0) {
                createErrorResponse403(res); 
                return;
            }
            studyService.incrementNumberOfDownloads(study.getId());            

	    // an HTTP GET method for remote files; 
	    // we want it defined here so that it can be closed 
	    // properly if an exception is caught. 

	    GetMethod method = null; 
            
            // now create zip
            try {
		// set content type: 
                res.setContentType("application/zip");

		// create zipped output stream: 

		OutputStream out = res.getOutputStream();
                ZipOutputStream zout = new ZipOutputStream(out);


		// send the file as the response

                List nameList = new ArrayList(); // used to check for duplicates
                iter = files.iterator();
		


                while (iter.hasNext()) {
                    StudyFile file = (StudyFile) iter.next();

                    InputStream in = null;

		    if ( file.isRemote() ) {

			// do the http magic

			int status = 200;
			
			method = new GetMethod ( file.getFileSystemLocation() );
			status = getClient().executeMethod(method);

			if ( status != 200 ) {

			    if (method != null) { 
				method.releaseConnection(); 
			    }

			} else {

			    // the incoming HTTP stream is the source of 
			    // the current chunk of the zip stream we are
			    // creating.

			    in = method.getResponseBodyAsStream(); 
			}
			
			// well, yes, the logic above will result in 
			// adding an empty file to the zip archive in 
			// case the remote object is not accessible. 

			// I can't think of a better solution right now, 
			// but it should work for now.

		    } else {
			// local file.		       
			in = new FileInputStream(new File(file.getFileSystemLocation()));
                    }

                    String zipEntryName = file.getFileName();
                    if (createDirectoriesForCategories) {
                        String catName = new FileCategoryUI( file.getFileCategory() ).getDownloadName();    
                        zipEntryName = catName + "/" + zipEntryName;
                    }
                    zipEntryName = checkZipEntryName( zipEntryName, nameList );
                    ZipEntry e = new ZipEntry(zipEntryName);
                    
                    zout.putNextEntry(e);

		    byte[] dataBuffer = new byte[8192]; 

		    int i = 0;
		    while ( ( i = in.read (dataBuffer) ) > 0 ) {
			zout.write(dataBuffer,0,i);
			out.flush(); 
		    }
                    in.close();
                    zout.closeEntry();

		    // if this was a remote stream, let's close
		    // the connection properly:

		    if ( file.isRemote() ) {
			if (method != null) { 
			    method.releaseConnection(); 
			}
		    }
		    
		}
                zout.close();
            } catch (IOException ex) {
		// if the exception was caught while downloading 
		// a remote object, let's make sure the network 
		// connection is closed properly.

		if (method != null) { 
		    method.releaseConnection(); 
		}

                ex.printStackTrace();
            }
        }
    }

    private void createErrorResponseGeneric(HttpServletResponse res, int status, String statusLine) {
        res.setContentType("text/html");
	res.setStatus ( status ); 
        try {
            PrintWriter out = res.getWriter();
            out.println("<HTML>");
            out.println("<HEAD><TITLE>File Download</TITLE></HEAD>");
            out.println("<BODY>");
            out.println("<BIG>" + statusLine + "</BIG>");
            out.println("</BODY></HTML>");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    
    private void createErrorResponse403(HttpServletResponse res) {
        res.setContentType("text/html");
	res.setStatus ( res.SC_FORBIDDEN ); 
        try {
            PrintWriter out = res.getWriter();
            out.println("<HTML>");
            out.println("<HEAD><TITLE>File Download</TITLE></HEAD>");
            out.println("<BODY>");
            out.println("<BIG>Sorry. You do not have permission to download this file.</BIG>");
            out.println("</BODY></HTML>");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void createErrorResponse404(HttpServletResponse res) {
        res.setContentType("text/html");
	res.setStatus ( res.SC_NOT_FOUND ); 
        try {
            PrintWriter out = res.getWriter();
            out.println("<HTML>");
            out.println("<HEAD><TITLE>File Download</TITLE></HEAD>");
            out.println("<BODY>");
            out.println("<BIG>Sorry. The file you are looking for could not be found.</BIG>");
            out.println("</BODY></HTML>");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // private methods for generating parameters for the DSB 
    // conversion call;
    // borrowed from Gustavo's code in DSBWrapper (for now)


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

    

    private String generateUrlForDDI(String serverPrefix, Long studyId) {
        String studyDDI = serverPrefix + "/ddi/?studyId=" + studyId;
        return studyDDI;
    }
    
    private String generateUrlForFile(String serverPrefix, Long fileId) {
        String file = serverPrefix + "/FileDownload/?fileId=" + fileId + "&isSSR=1";
        System.out.println(file);
        return file;
    }

    private String generateAltFormat(String formatRequested) {
        String altFormat; 
	
	//	if ( formatRequested.equals("D02") ) {
	//    altFormat = "application/x-rlang-transport"; 
	// } else if ( formatRequested.equals("DO3") ) {
	//     altFormat = "application/x-stata-6"; 
	// } else {
	//     altFormat = "application/x-R-2"; 
	// }	    

	altFormat = "application/x-gzip-tar";
        return altFormat;
    }

    private String generateAltFileName(String formatRequested, String xfileId) {
        String altFileName; 
	
	//	if ( formatRequested.equals("D02") ) {
	//    altFileName = "data_" + xfileId + ".ssc"; 
	// } else if ( formatRequested.equals("DO3") ) {
	//    altFileName = "data_" + xfileId + ".dta"; 
	// } else {
	//     altFileName = "data_" + xfileId + ".RData"; 
	// }	    

	altFileName = "data_" + xfileId + ".zip"; 

        return altFileName;
    }

    private String checkZipEntryName(String originalName, List nameList) {
        String name = originalName;
        int fileSuffix = 1;
        int extensionIndex = originalName.lastIndexOf(".");
        
        while (nameList.contains(name)) {
            if (extensionIndex != -1 ) {
                name = originalName.substring(0, extensionIndex) + "_" + fileSuffix++ + originalName.substring(extensionIndex);
            }  else {
                name = originalName + "_" + fileSuffix++;
            }    
        }
        nameList.add(name);
        return name;            
    }
}

