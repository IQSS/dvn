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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.nio.ByteBuffer; 
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map; 
import java.util.regex.Matcher;
import java.util.regex.Pattern; 
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
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;


import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/**
 *
 * @author gdurand
 * @author landreev
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
	String imageThumb = req.getParameter("imageThumb"); 
	String noVarHeader = req.getParameter("noVarHeader"); 


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

	    // perform access authorization check:

	    String dsbHost = System.getProperty("vdc.dsb.host");

	    if ( dsbHost == null ) {
		// vdc.dsb.host isn't set; 
		// fall back to the old-style option: 
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
		
	    if ( NOTaDSBrequest && file.isFileRestrictedForUser(user, vdc, ipUserGroup) ) {
		// generate a response with a correct 403/FORBIDDEN code   

		createErrorResponse403(res);
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

		    parameters.put("uri", generateUrlForDDI(serverPrefix, file.getId()));
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
			    
			byte[] dataReadBuffer = new byte[8192 * 4]; 

			int i = 0; 
			while ( ( i = in.read (dataReadBuffer)) > 0 ) {
			    out.write(dataReadBuffer, 0, i);
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

			// normally, the HTTP client follows redirects 
			// automatically, so we need to explicitely tell it
			// not to: 

			method.setFollowRedirects(false); 

			status = getClient().executeMethod(method);

			// The code below is to enable the click through
			// authentication.
			// We are assuming that if they have gotten here, 
			// they must have already clicked on all the 
			// licensing agreement forms (the terms-of-use
			// agreements are preserved in the study DDIs as 
			// they are exported and harvested between DVNs). 

			// There are obvious dangers in this approach. 
			// We have to trust the DVN harvesting from us to display 
			// the agreements in question to their users. But since
			// terms/restrictions cannot be disabled on harvested
			// content through the normal DVN interface, so they 
			// would have to go directly to the database to do so, 
			// which would constitute an obvious "hacking" of the
			// mechanism, (hopefully) making them and not us liable 
			// for it. 

			if ( status == 302 ) {
			    // this is a redirect. 

			    // let's see where it is redirecting us; if it looks like 
			    // DVN TermsOfUse page, we'll "click" and submit the form,
			    // then we'll hopefully be able to download the file.
			    // If it's no the TOU page, we are just going to try to 
			    // follow the redirect and hope for the best. 
			    // (A good real life example is the Census archive: the 
			    // URLs for their objects that they give us are actually
			    // aliases that are 302-redirected to the actual locations)

			    String redirectLocation = null;
			  
			    for (int i = 0; i < method.getResponseHeaders().length; i++) {
				String headerName = method.getResponseHeaders()[i].getName();
				if (headerName.equals("Location")) {
				    redirectLocation = method.getResponseHeaders()[i].getValue(); 
				}
			    }
			    
			    if (redirectLocation.matches ( ".*TermsOfUsePage.*" )) {

				// try again: 
				method = new GetMethod ( redirectLocation + "&clicker=downloadServlet" );
				status = getClient().executeMethod(method);

				InputStream in = method.getResponseBodyAsStream(); 
				BufferedReader rd = new BufferedReader(new InputStreamReader(in)); 

				String line = null;

				String jsessionid     = null; 
				String viewstate      = null; 
				String studyid        = null; 
				String remotefileid   = null; 

				String regexpJsession = "jsessionid=([0-9a-f]*)\""; 
				String regexpViewState = "ViewState\" value=\"([^\"]*)\""; 
				String regexpStudyId = "studyId\" value=\"([0-9]*)\""; 
				String regexpRemoteFileId = "fileId=([0-9]*)\"";

				Pattern patternJsession = Pattern.compile(regexpJsession); 
				Pattern patternViewState= Pattern.compile(regexpViewState); 
				Pattern patternStudyId = Pattern.compile(regexpStudyId); 
				Pattern patternRemoteFileId = Pattern.compile(regexpRemoteFileId); 
			    

				Matcher matcher = null; 

				matcher = patternRemoteFileId.matcher(file.getFileSystemLocation());
				if ( matcher.find() ) {
				    remotefileid = matcher.group(1); 
				}

				while ( ( line = rd.readLine () ) != null ) {
				    matcher = patternJsession.matcher(line);
				    if ( matcher.find() ) {
					jsessionid = matcher.group(1); 
				    }
				    matcher = patternViewState.matcher(line);
				    if ( matcher.find() ) {
					viewstate = matcher.group(1); 
				    }
				    matcher = patternStudyId.matcher(line);
				    if ( matcher.find() ) {
					studyid = matcher.group(1); 
				    }
				}

				rd.close();
				method.releaseConnection(); 

				if ( jsessionid != null ) {
				    
				    // we seem to have found a JSESSIONID; 
				    // looks like an authentication form. 
				    // let's make an authentication call, 
				    // which has to be a POST method: 

				    redirectLocation = redirectLocation.substring(0, redirectLocation.indexOf( "?" )); 
				    PostMethod TOUpostMethod = new PostMethod( redirectLocation + ";jsessionid=" + jsessionid ); 
				
				    Part[] parts = {
					new StringPart( "content:termsOfUsePageView:form1:vdcId", "" ),
					new StringPart( "pageName", "TermsOfUsePage" ),
					new StringPart( "content:termsOfUsePageView:form1:studyId", studyid ),
					new StringPart( "content:termsOfUsePageView:form1:redirectPage", "/FileDownload/?fileId=" + remotefileid ),
					new StringPart( "content:termsOfUsePageView:form1:tou", "download" ),
					new StringPart( "content:termsOfUsePageView:form1:termsAccepted", "on" ),
					new StringPart( "content:termsOfUsePageView:form1:termsButton", "Continue" ),
					new StringPart( "content:termsOfUsePageView:form1_hidden", "content:termsOfUsePageView:form1_hidden'" ),
					new StringPart( "javax.faces.ViewState", viewstate )
				    };


				    TOUpostMethod.setRequestEntity(new MultipartRequestEntity(parts, TOUpostMethod.getParams()));
				    TOUpostMethod.addRequestHeader("Cookie", "JSESSIONID=" + jsessionid ); 
				    status = getClient().executeMethod(TOUpostMethod);

				    // TODO -- more diagnostics needed here! 
				
				    TOUpostMethod.releaseConnection();

				    // And now, let's try and download the file
				    // again: 


				    method = new GetMethod (file.getFileSystemLocation()); 
				    method.addRequestHeader("Cookie", "JSESSIONID=" + jsessionid ); 
				    status = getClient().executeMethod(method);
				}
			    } else {
				// just try again (and hope for the best!)

				method = new GetMethod ( redirectLocation );
				status = getClient().executeMethod(method);
			    }
			}

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
			// recycle the Content-* headers from the incoming HTTP stream:

			for (int i = 0; i < method.getResponseHeaders().length; i++) {
			    String headerName = method.getResponseHeaders()[i].getName();
			    if (headerName.startsWith("Content")) {
				res.setHeader(method.getResponseHeaders()[i].getName(), method.getResponseHeaders()[i].getValue());
			    }
			}
		    
			// send the incoming HTTP stream as the response body

			InputStream in = method.getResponseBodyAsStream(); 
			OutputStream out = res.getOutputStream();
			//WritableByteChannel out = Channels.newChannel (res.getOutputStream()); 
			    
			byte[] dataReadBuffer = new byte[4 * 8192]; 
			//ByteBuffer dataWriteBuffer = ByteBuffer.allocate ( 4 * 8192 ); 

			int i = 0;
			while ( (i = in.read (dataReadBuffer)) > 0 ) {
			    //dataWriteBuffer.put ( dataReadBuffer ); 
			    //out.write(dataWriteBuffer);
			    out.write(dataReadBuffer,0,i);
			    //dataWriteBuffer.rewind (); 
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

			    FileChannel in = new FileInputStream(new File(cachedFileSystemLocation)).getChannel();
			    WritableByteChannel out = Channels.newChannel ( res.getOutputStream() ); 

			    long bytesPerIteration = 4 * 8192; 
			    long start = 0;

			    while ( start < in.size() ) {
				in.transferTo(start, bytesPerIteration, out);
				start += bytesPerIteration;
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

			parameters.put("uri", generateUrlForDDI(serverPrefix, file.getId()));

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
			String dbFileName = file.getFileName();

			if ( dbFileName != null && downloadOriginalFormat != null ) {
			    if ( dbContentType != null ) {
				String origFileExtension = generateOriginalExtension ( dbContentType ); 
				dbFileName = dbFileName.replaceAll ( ".tab$", origFileExtension ); 
			    } else {
				dbFileName = dbFileName.replaceAll ( ".tab$", "" ); 
			    }
			}

			// open the appropriate physical file

			File inFile = null; 
			String varHeaderLine = null; 

			// but first, see if they have requested a 
			// thumbnail for an image, or if it's a request
			// for the datafile in the "original format":
			
			if ( imageThumb != null && dbContentType.substring(0, 6).equalsIgnoreCase ("image/") ) {
			    if ( generateImageThumb(file.getFileSystemLocation()) ) {
				inFile = new File (file.getFileSystemLocation() + ".thumb"); 
				dbContentType = "image/png";
			    }
			} else { 
			    
			    if ( dbContentType.equals ("text/tab-separated-values") && file.isSubsettable() && noVarHeader == null ) {
				List datavariables = file.getDataTable().getDataVariables();
				varHeaderLine = generateVariableHeader ( datavariables );
			    }
			    inFile = new File(file.getFileSystemLocation());  

			    if ( downloadOriginalFormat != null ) {
				inFile = new File ( inFile.getParent(), "_" + file.getFileSystemName()); 
			    }
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
			
			// send the file as the response

			// InputStream in = new FileInputStream(inFile);
			
			FileChannel in = new FileInputStream(inFile).getChannel();
			
			// OutputStream out = res.getOutputStream();

			WritableByteChannel out = Channels.newChannel ( res.getOutputStream() ); 

			if ( varHeaderLine != null ) {
			    ByteBuffer varHeaderByteBuffer = ByteBuffer.allocate(varHeaderLine.length() * 2);
			    varHeaderByteBuffer.put (varHeaderLine.getBytes()); 
			    out.write ( varHeaderByteBuffer); 
			}

			long position = 0;
			long howMany = 32 * 1024; 

			while ( position < in.size() ) {
			    in.transferTo(position, howMany, out);
			    position += howMany;
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
                if (file.isFileRestrictedForUser(user, vdc,ipUserGroup) ) {
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
		    //ReadableByteChannel in = null; 

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
            
            if (status == res.SC_NOT_FOUND) {
                for (int i = 0; i < 10; i++) {
                    out.println("<!-- This line is filler to handle IE case for 404 errors   -->");
                }
            }
            
            out.println("</BODY></HTML>");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private void createErrorResponse403(HttpServletResponse res) {
        createErrorResponseGeneric(res, res.SC_FORBIDDEN, "You do not have permission to download this file.");
    }

    private void createErrorResponse404(HttpServletResponse res) {
        createErrorResponseGeneric(res, res.SC_NOT_FOUND, "Sorry. The file you are looking for could not be found.");
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

     public String generateVariableHeader(List dvs) {
	 String varHeader = null; 

	 if (dvs != null) {
            Iterator iter = dvs.iterator();
	    DataVariable dv; 

            if (iter.hasNext()) {
                dv = (DataVariable) iter.next();
                varHeader = dv.getName();
            }

            while (iter.hasNext()) {
                dv = (DataVariable) iter.next();
                varHeader = varHeader + "\t" + dv.getName();
            }
	 }

	 return varHeader;
     }

    private String generateUrlForDDI(String serverPrefix, Long fileId) {
        String studyDDI = serverPrefix + "/ddi/?fileId=" + fileId;
        System.out.println(studyDDI);
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

	if ( formatRequested.equals("D00") ) {
	    altFormat = "text/tab-separated-values"; 
	} else {
	    altFormat = "application/zip";
	}

        return altFormat;
    }

    private String generateOriginalExtension (String fileType) {
	
	if ( fileType.equalsIgnoreCase("application/x-spss-sav") ) {
	    return ".sav";
	} else if ( fileType.equalsIgnoreCase("application/x-spss-por") ) {
	    return ".por"; 
	} else if ( fileType.equalsIgnoreCase("application/x-stata") ) {
	    return ".dta"; 
	} 

        return "";
    }


    private boolean generateImageThumb (String fileLocation) {

	String thumbFileLocation = fileLocation + ".thumb"; 

	// see if the thumb is already generated and saved: 

	if (new File (thumbFileLocation).exists()) {
	    return true; 
	}

	// let's attempt to generate the thumb:

	// (I'm scaling all the images down to 64 pixels horizontally;
	// I picked the number 64 totally arbitrarily;
	// TODO: (?) make the default thumb size configurable
	// through a JVM option??
	
	
	if (new File ("/usr/bin/convert").exists()) {

	    String ImageMagick = "/usr/bin/convert -size 64x64 " + fileLocation + " -resize 64 -flatten png:" +  thumbFileLocation; 
	    int exitValue = 1; 
	
	    try {
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(ImageMagick);
		exitValue = process.waitFor(); 
	    } catch (Exception e) {
		exitValue = 1; 
	    }

	    if ( exitValue == 0 ) {
		return true; 
	    }
	}

	// For whatever reason, creating the thumbnail with ImageMagick
	// has failed. 
	// Let's try again, this time with Java's standard Image 
	// library:

	try {
	    BufferedImage fullSizeImage = ImageIO.read (new File(fileLocation)); 

	    double scaleFactor = ((double)64)/(double) fullSizeImage.getWidth(null); 
	    int thumbHeight = (int) (fullSizeImage.getHeight(null) * scaleFactor); 

	    java.awt.Image thumbImage = fullSizeImage.getScaledInstance(64, thumbHeight, java.awt.Image.SCALE_FAST); 

	    ImageWriter writer = null;
	    Iterator iter = ImageIO.getImageWritersByFormatName("png");
	    if (iter.hasNext()) {
		writer = (ImageWriter) iter.next();
	    } else {
		return false; 
	    }

	    BufferedImage lowRes = new BufferedImage(64, thumbHeight, BufferedImage.TYPE_INT_RGB);
	    lowRes.getGraphics().drawImage(thumbImage, 0, 0, null);
	    
	    ImageOutputStream ios = ImageIO.createImageOutputStream(new File (thumbFileLocation));
	    writer.setOutput(ios);

	    // finally, save thumbnail image: 
	    writer.write(lowRes);
	    writer.dispose();

	    ios.close();
	    thumbImage.flush();
	    fullSizeImage.flush();
	    lowRes.flush();
	    return true;
	} catch (Exception e) {
	    // something went wrong, returning "false":
	    return false; 
	}
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

	if ( formatRequested.equals("D00") ) {
	    altFileName = "data_" + xfileId + ".tab"; 
	} else {
	    altFileName = "data_" + xfileId + ".zip"; 
	}

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

