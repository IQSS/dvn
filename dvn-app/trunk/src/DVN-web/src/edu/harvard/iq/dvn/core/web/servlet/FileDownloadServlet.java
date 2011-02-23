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
package edu.harvard.iq.dvn.core.web.servlet;

import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.VDCUser;

import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.RemoteAccessAuth;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.StudyFileServiceLocal;
import edu.harvard.iq.dvn.core.study.TabularDataFile;
import edu.harvard.iq.dvn.core.study.VariableCategory;

import edu.harvard.iq.dvn.core.util.FileUtil;

import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.admin.LockssAuthServiceLocal;

//import edu.harvard.iq.dvn.core.vdc.LockssServer;
//import edu.harvard.iq.dvn.core.vdc.LockssConfig;


import edu.harvard.iq.dvn.core.web.dvnremote.DvnTermsOfUseAccess;
import edu.harvard.iq.dvn.core.web.dvnremote.ICPSRauth;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;
import java.util.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.ejb.EJB;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;


import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.lang.StringUtils;

import edu.harvard.iq.dvn.ingest.dsb.impl.*;
import java.io.BufferedOutputStream;
import java.util.logging.Logger;

/**
 *
 * @author gdurand
 * @author landreev
 */
public class FileDownloadServlet extends HttpServlet {

    /** Creates a new instance of FileDownloadServlet */
    public FileDownloadServlet() {
    }

    private HttpClient getClient() {
        return new HttpClient();
    }

    // TODO: figure out what this is being used for, and whether these variable
    // is used properly (I'm seeing variables with the same name defined in
    // some local methods, with potentially overriding each other). -- L.A. 
    
    private List<DataVariable> dataVariables = new ArrayList<DataVariable>();

    /** Sets the logger (use the package name) */
    private static Logger dbgLog = Logger.getLogger(FileDownloadServlet.class.getPackage().getName());
    

    @EJB
    StudyServiceLocal studyService;
    @EJB
    VDCServiceLocal vdcService;
    @EJB
    StudyFileServiceLocal studyFileService;
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
    @EJB
    LockssAuthServiceLocal lockssAuthService;


    public void service(HttpServletRequest req, HttpServletResponse res) {

        // Parameters:

        // 1. Extracted from the session:

        VDCUser user = null;
        if (LoginFilter.getLoginBean(req) != null) {
            user = LoginFilter.getLoginBean(req).getUser();
        }

        VDC vdc = vdcService.getVDCFromRequest(req);

        UserGroup ipUserGroup = null;
        if (req.getSession(true).getAttribute("ipUserGroup") != null) {
            ipUserGroup = (UserGroup) req.getSession().getAttribute("ipUserGroup");
        }

        // 2. HTTP parameters:

        String fileId = req.getParameter("fileId");
        String formatRequested = req.getParameter("format");
        String downloadOriginalFormat = req.getParameter("downloadOriginalFormat");
        String imageThumb = req.getParameter("imageThumb");
        String noVarHeader = req.getParameter("noVarHeader");


        // Single file download request:

        if (fileId != null && (!fileId.contains(","))) {

            // This is done in a few easy steps:

            // step 1. look up the studyfile by id:

            StudyFile file = lookupStudyFile(fileId);

            if (file == null) {
                createErrorResponse404(res);
                return;
            }

            // step 2. perform access authorization check:

            if (!isAccessAuthorized(file, req, user, vdc, ipUserGroup)) {
                // generate a response with a correct 403/FORBIDDEN code
                createErrorResponse403(res);
                return;
            }

            // step 3. initiate download object/subsystem:

            FileDownloadObject fileDownloadObject = initiateDownloadObject (file, req);

            if (fileDownloadObject == null || (fileDownloadObject.getStatus() != 200)) {

                if (fileDownloadObject != null) {
                    if (fileDownloadObject.getStatus() == 403) {
                        createErrorResponse403Remote(res);
                    } else {
                        // generic "not found" message:
                        createErrorResponse404(res);
                    }
                    fileDownloadObject.releaseConnection();
                } else {
                    createErrorResponse404(res);
                }
                return;

            }

            // step 4a. perform format conversion, if requested:

            if (formatRequested != null) {
                fileDownloadObject = performFormatConversion (file, fileDownloadObject, formatRequested);

            }

            // step 4b. see if this is a "download as original" request:
            
            else if (downloadOriginalFormat != null) {
                fileDownloadObject = lookupOriginalFormat (file, fileDownloadObject);
            }
            
            // step 4c. or if it's a request for a thumbnail of an image:
            
            else if (imageThumb != null) {
                fileDownloadObject = getImageThumb(file, fileDownloadObject);
            }

            // step 5. create error response if any of the above has failed:

            if (fileDownloadObject == null) {
                // generate error response:
                createErrorResponse404(res);

                //fileDownloadObject.releaseConnection();
                return;
            }

            // step 6. set the headers in the HTTP response and stream the
            // data:

            deliverContent (file, fileDownloadObject, res);

            // step 7. increment the appropriate download counters:
            // (but only if it's not a LOCKSS crawl!)

            if (!isLockssCrawlRequest(req)) {
                incrementDownloadCounts(file, vdc);
            }

            // done!
            // End of single file download.

        } else { // a request for a zip-packaged multiple file archive.
            zipMultipleFiles ( req, res, user, vdc, ipUserGroup);
        }

        return;
    } // end of the main service() method.



    public class FileDownloadObject {
        public FileDownloadObject () {

        }

        private int status;
        private long size;

        private InputStream in;

        private String mimeType;
        private String fileName;
        private String varHeader;
        private String errorMessage;

        // For remote downloads:

        private String remoteUrl;
        private GetMethod method = null;

        private Header[] responseHeaders;

        private Boolean isFile = false;
        private Boolean isZippedStream = false;
        private Boolean noVarHeader = false;

        // getters:

        public int getStatus () {
            return status;
        }

        private long getSize () {
            return size;
        }

        public InputStream getInputStream () {
            return in;
        }

        public String getMimeType () {
            return mimeType;
        }

        public String getFileName () {
            return fileName;
        }

        public String getVarHeader () {
            return varHeader;
        }

        public String getErrorMessage () {
            return errorMessage;
        }

        public String getRemoteUrl () {
            return remoteUrl;
        }

        public GetMethod getHTTPMethod () {
            return method;
        }

        public Header[] getResponseHeaders () {
            return responseHeaders;
        }

        public Boolean isFile () {
            return isFile;
        }

        public Boolean isZippedStream () {
            return isZippedStream;
        }

        public Boolean noVarHeader () {
            return noVarHeader;
        }

        // setters:

        public void setStatus (int s) {
            status = s;
        }

        public void setSize (long s) {
            size = s;
        }

        public void setInputStream (InputStream is) {
            in = is;
        }

        public void setMimeType (String mt) {
            mimeType = mt;
        }

        public void setFileName (String fn) {
            fileName = fn;
        }

        public void setVarHeader (String vh) {
            varHeader = vh;
        }

        public void setErrorMessage (String em) {
            errorMessage = em;
        }

        public void setRemoteUrl (String u) {
            remoteUrl = u;
        }

        public void setHTTPMethod (GetMethod hm) {
            method = hm;
        }

        public void setResponseHeaders (Header[] headers) {
            responseHeaders = headers;
        }

        public void setIsFile (Boolean f) {
            isFile = f;
        }

        public void setIsZippedStream (Boolean zs) {
            isZippedStream = zs;
        }

        public void setNoVarHeader (Boolean nvh) {
            noVarHeader = nvh;
        }

        // connection management methods:

        public void releaseConnection () {
            if (method != null) {
                method.releaseConnection();
            }
        }

        public void closeInputStream () {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    // we really don't care.
                    String eMsg = "Warning: IO exception closing input stream.\n";
                    if (errorMessage == null) {
                        errorMessage = eMsg;
                    } else {
                        errorMessage = errorMessage + eMsg;
                    }
                }
            }
        }
    }

    // HELPER METHODS
    private StudyFile lookupStudyFile (String fileId) {
        StudyFile file = null;

        try {
            file = studyFileService.getStudyFile(new Long(fileId));
        } catch (Exception ex) {
            return null;
        }

        return file;
    }

    private Boolean isAccessAuthorized (StudyFile file,
                                        HttpServletRequest req,
                                        VDCUser user,
                                        VDC vdc,
                                        UserGroup ipUserGroup) {
        Boolean authorized = false;

        // first check is to see if this request is from localhost.
        // localhost is authorized to get anything it wants,
        // no questions asked.

        String localHostByName = "localhost";
        String localHostNumeric = "127.0.0.1";

        if (!isLockssCrawlRequest(req)) {
            // This is a non-LOCKSS download request.

            if (localHostByName.equals(req.getRemoteHost()) ||
                localHostNumeric.equals(req.getRemoteHost())) {
                return true;
            }

            // then check to see if this request is from our dedicated
            // DSB host. DSB host is authorized to get any file without
            // restrictions.
            // (Chances are this is no longer required -- L.A. -- ?)

            String dsbHost = System.getProperty("vdc.dsb.host");

            if (dsbHost == null) {
                // vdc.dsb.host isn't set;
                // fall back to the old-style option:
                dsbHost = System.getProperty("vdc.dsb.url");
            }

            boolean isDSBrequest = false;

            if (dsbHost != null) {
                if (dsbHost.equals(req.getRemoteHost())) {
                    return true;
                } else {
                    try {
                        String dsbHostIPAddress = InetAddress.getByName(dsbHost).getHostAddress();
                        if (dsbHostIPAddress.equals(req.getRemoteHost())) {
                            return true;
                        }
                    } catch (UnknownHostException ex) {
                        // no need to do anything;
                        // this probably means the "vdc.dsb.host" setting is
                        // misconfigured. in any event, safe to assume this is NOT
                        // a DSB call
                    }
                }
            }

            // Now, let's check if the file is authorized for this specific user:
            if (!file.isFileRestrictedForUser(user, vdc, ipUserGroup)) {
                    return true;
            }
        } else {
            // this is a LOCKSS crawler:

            if (isAuthorizedLockssCrawler(file, req)) {
                return true;
            }
        }

        // We've exhausted the possibilities:
        return false;
    }

    private Boolean isAuthorizedLockssCrawler (StudyFile file,
                                        HttpServletRequest req) {
        // is this a LOCKSS request?

        if (!isLockssCrawlRequest(req)) {
            return false;
        }

        // OK, it is.
        // Let's check if this address is authorized to download this file.

        String remoteAddress = req.getRemoteHost();

        if (remoteAddress == null || remoteAddress.equals("")) {
            return false;
        }
        Study study = null;
        VDC vdc = null;

        study = file.getStudy();

        if (study != null) {
            vdc = study.getOwner();
        }

        //return lockssAuthService.isAuthorizedLockssServer(vdc, req);
        return lockssAuthService.isAuthorizedLockssDownload(vdc, req, isRestrictedFile(file, study, vdc));
    }

    private Boolean isRestrictedFile (StudyFile file, Study study, VDC vdc) {
        if (vdc.isFilesRestricted()) {
            return true;
        }

        if (study.isRestricted()) {
            // This restriction check is only performed for LOCKSS crawlers;
            // Studies that are restricted don't get exported. So this should
            // not normally happen -- i.e. files from restricted studies are
            // not supposed to be crawled at all. But better safe than sorry,
            // you know.
            return true;
        }

        if (file.isRestricted()) {
            return true;
        }
        
        return false;
    }

    private Boolean isLockssCrawlRequest (HttpServletRequest req) {

        String remoteAgent = req.getHeader("user-agent");
        
        //dbgLog.info ("remote browser detected: "+remoteAgent);
        if (remoteAgent != null && remoteAgent.matches(".*LOCKSS cache.*")) {
            return true;
        }
        
        return false;
    }


    private FileDownloadObject initiateDownloadObject (StudyFile file, HttpServletRequest req) {
        if (file != null) {
            if (file.isRemote()) {
                return initiateRemoteDownload (file, req);
            } else {
                return initiateLocalDownload (file, req);
            }
        }
        return null;
    }

    private FileDownloadObject initiateLocalDownload (StudyFile file, HttpServletRequest req) {
        FileDownloadObject localDownload = new FileDownloadObject ();

        if (req.getParameter("noVarHeader") != null) {
            localDownload.setNoVarHeader(true);
        }

        localDownload.setSize(getLocalFileSize(file));

        InputStream in = getLocalFileAsStream(file);

        if (in == null) {
            return null;
        }

        localDownload.setInputStream(in);
        localDownload.setIsFile(true);

        localDownload.setMimeType(file.getFileType());
        localDownload.setFileName(file.getFileName());


        if (file.getFileType() != null &&
            file.getFileType().equals("text/tab-separated-values")  &&
            file.isSubsettable() && (!localDownload.noVarHeader())) {

            List datavariables = ((TabularDataFile) file).getDataTable().getDataVariables();
            String varHeaderLine = generateVariableHeader(datavariables);
            localDownload.setVarHeader(varHeaderLine);
        }

        setDownloadContentHeaders (localDownload);


        localDownload.setStatus(200);
        return localDownload;
    } // End of initiateLocalDownload;


    private void setDownloadContentHeaders (FileDownloadObject fileDownloadObject) {
        List<Header> headerList = new ArrayList();
        Header contentHeader = null;
        int headerCounter = 0;

        if (fileDownloadObject.getFileName() != null) {
            if (fileDownloadObject.getMimeType() != null) {

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
                //          "inline; filename=\"" + dbFileName + "\"" );
                //
                //} else {

                //respHeaders = new Header[2];
                contentHeader = new Header ();

                contentHeader.setName("Content-disposition");
                contentHeader.setValue("attachment; filename=\"" + fileDownloadObject.getFileName() + "\"");

                headerList.add(contentHeader);
                headerCounter++;

                //}

                // And this one is for MS Explorer:

                contentHeader = new Header ();

                contentHeader.setName("Content-Type");
                contentHeader.setValue(fileDownloadObject.getMimeType() + "; name=\"" + fileDownloadObject.getFileName() + "\"; charset=ISO-8859-1");

                headerList.add(contentHeader);
                headerCounter++;

            } else {
                // Have filename, but no content-type;
                // All we can do is provide a Mozilla-friendly
                // header:

                //respHeaders = new Header[1];

                contentHeader = new Header ();

                contentHeader.setName("Content-disposition");
                contentHeader.setValue("attachment; filename=\"" + fileDownloadObject.getFileName() + "\"");

                headerList.add(contentHeader);
                headerCounter++;
            }
        } else if (fileDownloadObject.getMimeType() != null) {
            // no filename available;
            // but if we have content-type in the database
            // we'll just set that:

            //respHeaders = new Header[1];

            contentHeader = new Header ();

            contentHeader.setName("Content-Type");
            contentHeader.setValue(fileDownloadObject.getMimeType());

            headerList.add(contentHeader);
            headerCounter++;

        }

        // Add size header, if size is available
        // (as of now -- for local files only).

        if (fileDownloadObject.isFile()) {
            long fileSize = fileDownloadObject.getSize();

            if ( fileSize > 0 ) {
                // Don't forget about the variable header we are adding to the
                // TAB files. This will change the size of the content!
                
                if (fileDownloadObject.getVarHeader() != null && (!fileDownloadObject.noVarHeader())) {
                    fileSize += fileDownloadObject.getVarHeader().length();
                }
                Header contentLengthHeader = new Header();
                contentLengthHeader.setName("Content-Length");
                contentLengthHeader.setValue((new Long(fileSize)).toString());

                headerList.add(contentLengthHeader);
                headerCounter++;
            }
        }

        if ( headerCounter > 0 ) {
            Header[] respHeaders = new Header[headerCounter];
            for (int i=0; i<headerCounter; i++) {
                respHeaders[i] = headerList.get(i); 
            }
            fileDownloadObject.setResponseHeaders(respHeaders);
        }

    }

    private FileDownloadObject initiateRemoteDownload (StudyFile file, HttpServletRequest req) {

        FileDownloadObject remoteDownload = new FileDownloadObject ();

        if (req.getParameter("noVarHeader") != null) {
            remoteDownload.setNoVarHeader(true);
        }


        String remoteFileUrl = file.getFileSystemLocation();
        if (remoteFileUrl != null) {
            remoteFileUrl = remoteFileUrl.replaceAll(" ", "+");
            remoteDownload.setRemoteUrl(remoteFileUrl);
        }

        Boolean zippedStream = false;


        GetMethod method = null;
        int status = 200;

        try {

            if (remoteFileUrl != null) {
                remoteFileUrl = remoteFileUrl.replaceAll(" ", "+");
            }

            // If it's another DVN from which we are getting
            // the file, we need to pass the "noVarHeader"
            // argument along:

            if (remoteFileUrl.matches(".*FileDownload.*")) {
                if (remoteDownload.noVarHeader()) {
                    remoteFileUrl = remoteFileUrl + "&noVarHeader=1";
                } else {
                    // and if we are retreiving this tab file in order
                    // to convert it to another format locally, we also
                    // have to add the noVarHeader flag, otherwise the
                    // header will be treated as a line of data!

                    if (req.getParameter("format") != null) {
                        remoteFileUrl = remoteFileUrl + "&noVarHeader=1";
                        // TODO -- ? -- do we need to check if this is
                        // a tab-delimited file? in theory, this could
                        // be a fixed field from another DVN... or could it?
                    }
                }
            }


            // See if remote authentication is required;

            String remoteHost = null;
            String regexRemoteHost = "https*://([^/]*)/";
            Pattern patternRemoteHost = Pattern.compile(regexRemoteHost);
            Matcher hostMatcher = patternRemoteHost.matcher(remoteFileUrl);

            if (hostMatcher.find()) {
                remoteHost = hostMatcher.group(1);
            }


            method = new GetMethod(remoteFileUrl);

            String jsessionid = null;
            String remoteAuthHeader = null;

            String remoteAuthType = remoteAuthRequired(remoteHost);


            if (remoteAuthType != null) {
                if (remoteAuthType.equals("httpbasic")) {
                    // get the basic HTTP auth credentials
                    // (password and username) from the database:

                    remoteAuthHeader = getRemoteAuthCredentials(remoteHost);

                    if (remoteAuthHeader != null) {
                        method.addRequestHeader("Authorization", remoteAuthHeader);
                    }
                } else if (remoteAuthType.equals("dvn")) {
                    // Authenticate with the remote DVN:

                    jsessionid = dvnRemoteAuth(remoteHost);
                } else if (remoteAuthType.equals("icpsr")) {
                    method = null;
                    //remoteHost = "www.icpsr.umich.edu";
                    dbgLog.fine("ICPSR download: Stored URL: "+remoteFileUrl);
                    remoteFileUrl = remoteFileUrl.replace("staging", "www");
                    remoteFileUrl = remoteFileUrl.replace("/cgi-bin/file", "/cgi-bin/bob/file");
                    dbgLog.fine("ICPSR download: Edited URL: "+remoteFileUrl);
                    method = new GetMethod(remoteFileUrl);
                    
                    String icpsrCookie = getICPSRcookie(remoteHost, remoteFileUrl);

                    dbgLog.fine("ICPSR download: obtained ICPSR cookie: "+icpsrCookie);

                    if (icpsrCookie != null) {
                        method.addRequestHeader("Cookie", icpsrCookie);
                    }

                    if ( remoteFileUrl.matches(".*gzip.*") ) {
                        zippedStream = true;
                    }
                }
            }

            if (jsessionid != null) {
                method.addRequestHeader("Cookie", "JSESSIONID=" + jsessionid);
            }

            // normally, the HTTP client follows redirects
            // automatically, so we need to explicitely tell it
            // not to:

            method.setFollowRedirects(false);
            status = getClient().executeMethod(method);

            // The code below is to enable the click through
            // Terms-of-Use Agreement.
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

            if (status == 302 || status == 301) {
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
                String extraCookies = null;

                for (int i = 0; i < method.getResponseHeaders().length; i++) {
                    String headerName = method.getResponseHeaders()[i].getName();
                    if (headerName.equals("Location")) {
                        redirectLocation = method.getResponseHeaders()[i].getValue();
                    }

                    String regexCookie = "^([^;]*;)";
                    Pattern patternCookie = Pattern.compile (regexCookie);

                    if (headerName.equals("Set-Cookie") ||
                        headerName.equals("Set-cookie")) {
                        String cookieHeader = method.getResponseHeaders()[i].getValue();
                        Matcher cookieMatcher = patternCookie.matcher(cookieHeader);
                        if ( cookieMatcher.find() ) {
                            extraCookies = cookieMatcher.group(1);
                        }
                    }
                }

                if (redirectLocation.matches(".*TermsOfUsePage.*")) {

                    // Accept the TOU agreement:

                    method = remoteAccessTOU(redirectLocation, jsessionid, remoteFileUrl, extraCookies);

                    // If everything has worked right
                    // we should be redirected to the final
                    // download URL, and the method returned is
                    // an established download connection;

                    if (method != null) {
                        status = method.getStatusCode();
                    } else {
                        // but if something went wrong in the progress,
                        // we just report that we couldn't find
                        // the file:
                        status = 404;
                    }

                } else {
                    // just try again (and hope for the best!)
                    method = new GetMethod(redirectLocation);
                    status = getClient().executeMethod(method);
                }
            }
        } catch (IOException ex) {
            //if (method != null) {
            //    method.releaseConnection();
            //}
            status = 404;
        }

        remoteDownload.setStatus(status);

        if (status != 200) {
            if (method != null) {
                method.releaseConnection();
            }

            return remoteDownload;
        }

        InputStream in = null;

        try {
            if ( zippedStream ) {
                InputStream zipInputStream = method.getResponseBodyAsStream();
                ZipInputStream zin = new ZipInputStream(zipInputStream);
                zin.getNextEntry();

                remoteDownload.setIsZippedStream(true);

                in = zin;

            } else {
                in = method.getResponseBodyAsStream();
            }

            remoteDownload.setInputStream(in);

        } catch (IOException ex) {
            remoteDownload.setStatus(404);
            String errorMessage = "An unknown I/O error has occured while attempting to retreive a remote data file (i.e., a file that belongs to a harvested study). It is possible that it is temporarily unavailable from that location or perhaps a temporary network error has occured. Please try again later and if the problem persists, report it to your DVN technical support contact.";
            remoteDownload.setErrorMessage(errorMessage);
        }

        remoteDownload.setResponseHeaders(method.getResponseHeaders());
        remoteDownload.setHTTPMethod(method);
        return remoteDownload;

    } // End of initiateRemoteDownload;


    public File saveRemoteFile (StudyFile file, FileDownloadObject fileDownload) {

        File tabFile = null;
        File dtFile = null;

        try {
            // save the incoming stream as a temp file

            // temp data file that stores incoming data
            // (meaning, the entire file, containing all variables;
            // tab-delimited or fixed-field.)


            dtFile = File.createTempFile("tempDataFile.", ".dat");

            InputStream in = fileDownload.getInputStream();
            OutputStream out = new BufferedOutputStream(new FileOutputStream(dtFile));

            int bufsize;
            byte [] bffr = new byte[4*8192];

            while ((bufsize = in.read(bffr))!=-1) {
                out.write(bffr, 0, bufsize);
            }


            in.close();
            out.close();

            // Check the resulting file

            if (dtFile.exists()){

                // if it exists, and the file is tab-delimited, we can
                // proceed sending it to the R batch server for conversion;
                // if however, it is fixed-field, it has to be converted
                // to the tab-delimited format first. That we do by
                // requesting a "subsetting" of the file for all of its
                // variables:

                if ( file.getFileType() != null && file.getFileType().equals("text/tab-separated-values") ) {
                    tabFile = dtFile;
                } else {
                    // must be a fixed-field file.

                    // create yet another temporary file to store the
                    // results of jcut subsetting:

                    tabFile = File.createTempFile("tempTabFile.", ".tab");

                    // subsetting:

                    Long noRecords = ((TabularDataFile) file).getDataTable().getRecordsPerCase();
                    Map<Long, List<List<Integer>>> varMetaSet = getSubsettingMetaData(noRecords);
                    DvnNewJavaFieldCutter fc = new DvnNewJavaFieldCutter(varMetaSet);

                    try {
                        fc.cutColumns(dtFile, noRecords.intValue(), 0, "\t", tabFile.getAbsolutePath());
                    } catch (Exception e){
                        // TODO: actually, it would be nice to be able to
                        // tell if we've failed to download the remote file
                        // or failed to convert it.
                        // (otherwise there's not even any point in catching
                        // this exception separately here!)
                        return null;
                    }
                }
            }
        } catch (Exception ex) {
            // TODO: should probably create and save an error message here.
            // "failed to download (as opposed to failed to convert").
        }

        if (tabFile != null && tabFile.exists()) {
            return tabFile;
        }

        return null;
    }

    public InputStream getLocalFileAsStream (StudyFile file) {
        InputStream in;

        try {
            in = new FileInputStream(new File(file.getFileSystemLocation()));
        } catch (Exception ex) {
            // We don't particularly care what the reason why we have
            // failed to access the file was.
            // From the point of view of the download subsystem, it's a
            // binary operation -- it's either successfull or not.
            // If we can't access it for whatever reason, we are saying
            // it's 404 NOT FOUND in our HTTP response.
            return null;
        }

        return in;
    }

        public long getLocalFileSize (StudyFile file) {
        long fileSize = 0;
        File testFile = null;

        try {
            testFile = new File(file.getFileSystemLocation());
            if (testFile != null) {
                fileSize = testFile.length();
            }
        } catch (Exception ex) {
            return 0;
        }

        return fileSize;
    }


    public void incrementDownloadCounts (StudyFile file, VDC vdc) {
        if ( vdc != null ) {
            studyService.incrementNumberOfDownloads(file.getId(), vdc.getId());
        } else {
            studyService.incrementNumberOfDownloads(file.getId(), (Long)null);
        }
    }

    public void streamData (InputStream in, OutputStream out, String varHeader) {

        try {
            byte[] dataReadBuffer = new byte[4 * 8192];

            // If we are streaming a TAB-delimited file, we will need to add the
            // variable header line:

            if (varHeader != null) {
                byte[] varHeaderBuffer = null;
                varHeaderBuffer = varHeader.getBytes();
                out.write(varHeaderBuffer);
                out.flush();
            }


            int i = 0;

            while ((i = in.read(dataReadBuffer)) > 0) {
                out.write(dataReadBuffer, 0, i);
                out.flush();
            }

            in.close();
            out.close();
        } catch (IOException ex) {
            // whatever. we don't care.
        }
    }


    public void streamData (FileChannel in, WritableByteChannel out, String varHeader) {

        long position = 0;
        long howMany = 32 * 1024;

        try {
            // If we are streaming a TAB-delimited file, we will need to add the
            // variable header line:

            if (varHeader != null) {
                ByteBuffer varHeaderByteBuffer = ByteBuffer.wrap(varHeader.getBytes());
                    out.write(varHeaderByteBuffer);
            }

            while (position < in.size()) {
                in.transferTo(position, howMany, out);
                position += howMany;
            }

            in.close();
            out.close();
        } catch (IOException ex) {
            // whatever. we don't care at this point.
        }

    }

 
    public void deliverContent (StudyFile file, FileDownloadObject fileDownload, HttpServletResponse res) {
        OutputStream out = null; 
        
        try {
            out = res.getOutputStream();
        } catch (IOException ex) {
            // TODO: try to generate error response.
            return; 
        }
        InputStream in = fileDownload.getInputStream();
        
        if (in == null) {
            // TODO: generate error response.
            fileDownload.releaseConnection();
            return;
        }

        // If we are streaming a TAB-delimited file, we will need to add the
        // variable header line:


        String varHeaderLine = null;

        if (!fileDownload.noVarHeader()) {
            varHeaderLine = fileDownload.getVarHeader();
        }


        for (int i = 0; i < fileDownload.getResponseHeaders().length; i++) {
            String headerName = fileDownload.getResponseHeaders()[i].getName();
			// The goal is to (re)use all the Content-* headers.
            // (if this is a remote file, we may be recycling the headers
            // we have received from the remote repository):

            if (headerName.startsWith("Content")) {

                // Special treatment case for remote
                // HTML pages:
                // if it looks like HTML, we redirect to
                // that page, instead of trying to display it:
                // (this is for cases like the harvested HGL
                // documents which contain URLs pointing to
                // dynamic content pages, not to static files.

                if (headerName.equals("Content-Type") &&
                    file.isRemote() &&
                    fileDownload.getResponseHeaders()[i].getValue() != null &&
                    fileDownload.getResponseHeaders()[i].getValue().startsWith("text/html")) {

                    createRedirectResponse(res, fileDownload.getRemoteUrl());

                    fileDownload.releaseConnection();
                    return;
                }
                
                String headerValue = fileDownload.getResponseHeaders()[i].getValue();

                if ( fileDownload.isZippedStream() ) {
                    headerValue = headerValue.replace (".zip", "");
                }

                res.setHeader(headerName, headerValue);
            }
        }
		
        // TODO: should probably do explicit res.setContent (), if mimetype
        // is available. 


        
        // and now send the incoming HTTP stream as the response body       

        if (fileDownload.isFile()) {
            // for files that we are reading off disk (as opposed to remote
            // streams we are reading through network sockets) it is more
            // efficient to use NIO channels.
            FileInputStream fis = (FileInputStream)in;
            FileChannel inChannel = fis.getChannel();

            WritableByteChannel outChannel = Channels.newChannel(out);

            streamData(inChannel, outChannel, varHeaderLine);

        } else {

            streamData(in, out, varHeaderLine);
        }

        fileDownload.releaseConnection();

    }
    
    public FileDownloadObject getImageThumb (StudyFile file, FileDownloadObject fileDownload) {
        if (file != null && file.getFileType().substring(0, 6).equalsIgnoreCase("image/")) {
            if (generateImageThumb(file.getFileSystemLocation())) {
                File imgThumbFile = new File(file.getFileSystemLocation() + ".thumb");

                if (imgThumbFile != null && imgThumbFile.exists()) {

                    fileDownload.closeInputStream();
                    fileDownload.setSize(imgThumbFile.length());

                    
                    InputStream imgThumbInputStream = null; 
                    
                    try {

                        imgThumbInputStream = new FileInputStream(imgThumbFile);
                    } catch (IOException ex) {
                        return null; 
                    }
                    
                    if (imgThumbInputStream != null) {
                        fileDownload.setInputStream(imgThumbInputStream);
                        fileDownload.setIsFile(true);
                               
                        fileDownload.setMimeType("image/png");
                        setDownloadContentHeaders (fileDownload);

                    } else {
                        return null; 
                    }
                }
            }
        } 
        
        
        return fileDownload;
    }
    
    public FileDownloadObject lookupOriginalFormat (StudyFile file, FileDownloadObject fileDownload) {
        File inFile = new File(file.getFileSystemLocation());

        if (inFile != null) {
            File origFile = new File(inFile.getParent(), "_" + file.getFileSystemName());
            
            if (origFile != null && origFile.exists()) {
                
                fileDownload.closeInputStream();
                fileDownload.setSize(origFile.length());
                
                try {
                    fileDownload.setInputStream(new FileInputStream(origFile));
                } catch (IOException ex) {
                    return null; 
                }
                fileDownload.setIsFile(true);

                String originalMimeType = file.getOriginalFileType();

                if (originalMimeType != null && !originalMimeType.equals("")) {
                    if (originalMimeType.matches("application/x-dvn-.*-zip")) {
                        fileDownload.setMimeType("application/zip");
                    }
                    fileDownload.setMimeType(originalMimeType);
                } else {
                    fileDownload.setMimeType("application/x-unknown");
                }

                if (file.getFileName() != null) {
                    if ( file.getOriginalFileType() != null) {
                        String origFileExtension = generateOriginalExtension(file.getOriginalFileType());
                        fileDownload.setFileName(file.getFileName().replaceAll(".tab$", origFileExtension));
                    } else {
                        fileDownload.setFileName(file.getFileName().replaceAll(".tab$", ""));
                    }
                }


                // The fact that we have the "original format" file for this data
                // set, means it's a subsettable, tab-delimited file. Which means
                // we've already prepared a variable header to be added to the
                // stream. We don't want to add it to the stream that's no longer
                // tab-delimited -- that would screw it up! -- so let's remove
                // those headers:

                fileDownload.setNoVarHeader(true);
                fileDownload.setVarHeader(null);

                setDownloadContentHeaders (fileDownload);

                return fileDownload;
            }
        }
        
        return null;
    }
    
    public FileDownloadObject performFormatConversion (StudyFile file, FileDownloadObject fileDownload, String formatRequested) {

        File tabFile = null; 
        File formatConvertedFile = null;

        String cachedFileSystemLocation = null;

         // initialize the data variables list:

        dataVariables = ((TabularDataFile) file).getDataTable().getDataVariables();

        // if the format requested is "D00", and it's already a TAB file,
        // we don't need to do anything:
        if (formatRequested.equals("D00") &&
            file.getFileType().equals("text/tab-separated-values")) {
                
            return fileDownload;
        }

        if (file.isRemote()) {
            tabFile = saveRemoteFile (file, fileDownload);
        } else {
            // If it's a local file we may already have a cached copy of this
            // format.

            cachedFileSystemLocation = file.getFileSystemLocation()
                + "."
                + formatRequested;


            if (new File(cachedFileSystemLocation).exists()) {
                formatConvertedFile = new File(cachedFileSystemLocation);
            } else {
                // OK, we don't have a cached copy. So we'll have to run
                // conversion again (below). Let's have the
                // tab-delimited file handy:

                tabFile = new File(file.getFileSystemLocation());
            }
        }
                        
        
        // Check the tab file:

        if (tabFile != null && (tabFile.length() > 0)) {   
            formatConvertedFile = runFormatConversion (file, tabFile, formatRequested);

            // for local files, cache the result:

            if (!file.isRemote() &&
                    formatConvertedFile != null &&
                    formatConvertedFile.exists()) {

                try {
                    File cachedConvertedFile = new File (cachedFileSystemLocation);
                    FileUtil.copyFile(formatConvertedFile,cachedConvertedFile);
                } catch (IOException ex) {
                    // Whatever. For whatever reason we have failed to cache
                    // the format-converted copy of the file we just produced.
                    // But it's not fatal. So we just carry on.
                }
            }

        }

        // Now check the converted file: 
              
        if (formatConvertedFile != null && formatConvertedFile.exists()) {

            fileDownload.closeInputStream();
            fileDownload.setSize(formatConvertedFile.length());

            try {
                fileDownload.setInputStream(new FileInputStream(formatConvertedFile));
            } catch (IOException ex) {
                return null; 
            }

            fileDownload.releaseConnection();
            fileDownload.setHTTPMethod(null);
            fileDownload.setIsFile(true);

            fileDownload.setMimeType(generateAltFormat(formatRequested));
            String dbFileName = file.getFileName();

            if (dbFileName == null || dbFileName.equals("")) {
                dbFileName = "f" + file.getId().toString();
            }

            fileDownload.setFileName(generateAltFileName(formatRequested, dbFileName));

            if (formatRequested.equals("D00") && (!fileDownload.noVarHeader())) {

                String varHeaderLine = null;
                List dataVariablesList = ((TabularDataFile) file).getDataTable().getDataVariables();
                varHeaderLine = generateVariableHeader(dataVariablesList);
                fileDownload.setVarHeader(varHeaderLine);
            } else {
                fileDownload.setNoVarHeader(true);
                fileDownload.setVarHeader(null);
                // (otherwise, since this is a subsettable file, the variable header
                //  will be added to this R/Stata/etc. file -- which would
                //  totally screw things up!)
            }

            setDownloadContentHeaders (fileDownload);

           
            return fileDownload; 
        }
        
        return null; 
    } // end of performformatconversion();

    // Method for (subsettable) file format conversion.
    // The method needs the subsettable file saved on disk as in the
    // TAB-delimited format.
    // Meaning, if this is a remote subsettable file, it needs to be downloaded
    // and stored locally as a temporary file; and if it's a fixed-field file, it
    // needs to be converted to TAB-delimited, before you can feed the file
    // to this method. (See performFormatConversion() method)
    // The method below takes the tab file and sends it to the R server
    // (possibly running on a remote host) and gets back the transformed copy,
    // providing error-checking and diagnostics in the process.
    // This is mostly Akio Sone's code.

    public File runFormatConversion (StudyFile file, File tabFile, String formatRequested) {

        if ( formatRequested.equals ("D00") ) {
            // if the *requested* format is TAB-delimited, we don't
            // need to call R to do any conversions, we can just
            // send back the TAB file we have just produced.

            return tabFile;
        }

        DvnRJobRequest sro = null;
        Map<String, List<String>> paramListToR = null;
        Map<String, Map<String, String>> vls = null;

        dbgLog.fine(" ***** remote: set-up block for format conversion cases *****");

        paramListToR = new HashMap<String, List<String>>();

        paramListToR.put("dtdwnld", Arrays.asList(formatRequested));
        paramListToR.put("requestType", Arrays.asList("Download"));

        //vls = getValueTablesForAllRequestedVariables();
        vls = getValueTableForRequestedVariables(dataVariables);
        dbgLog.fine("format conversion: variables(getDataVariableForRequest())="+getDataVariableForRequest()+"\n");
        dbgLog.fine("format conversion: variables(dataVariables)="+dataVariables+"\n");
        dbgLog.fine("format conversion: value table(vls)="+vls+"\n");

        Long tabFileSize = tabFile.length();
        paramListToR.put("subsetFileName", Arrays.asList(tabFile.getAbsolutePath()));
        paramListToR.put("subsetDataFileName",Arrays.asList(tabFile.getName()));

        File frmtCnvrtdFile = null;
        Map<String, String> resultInfo = new HashMap<String, String>();

        dbgLog.fine("local: paramListToR="+paramListToR);

        sro = new DvnRJobRequest(getDataVariableForRequest(), paramListToR, vls);

        // create the service instance
        DvnRforeignFileConversionServiceImpl dfcs = new DvnRforeignFileConversionServiceImpl();

        // execute the service
        resultInfo = dfcs.execute(sro);

        //resultInfo.put("offlineCitation", citation);
        dbgLog.fine("resultInfo="+resultInfo+"\n");

        // check whether a requested file is actually created

        if (resultInfo.get("RexecError").equals("true")){
            dbgLog.fine("R-runtime error trying to convert a file.");
            return  null;
        } else {
            String wbDataFileName = resultInfo.get("wbDataFileName");
            dbgLog.fine("wbDataFileName="+wbDataFileName);

            frmtCnvrtdFile = new File(wbDataFileName);

            if (frmtCnvrtdFile.exists()){
                dbgLog.fine("frmtCnvrtdFile:length="+frmtCnvrtdFile.length());
            } else {
                dbgLog.warning("Format-converted file was not properly created.");
                return null;
            }
        }

        return frmtCnvrtdFile;
    }

    private void createErrorResponseGeneric(HttpServletResponse res, int status, String statusLine) {
        res.setContentType("text/html");

        if (status == 0) {
            status = 200;
        }

        res.setStatus(status);
        PrintWriter out = null;
        try {
            out = res.getWriter();
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
            out.flush();
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    private void createRedirectResponse(HttpServletResponse res, String remoteUrl) {
        try {
            res.sendRedirect(remoteUrl);
        } catch (IOException ex) {
            //ex.printStackTrace();
            String errorMessage = "An unknown I/O error has occured while the Application was attempting to issue a redirect to a remote resource (i.e., a URL pointing to a page on a remote server). Please try again later and if the problem persists, report it to your DVN technical support contact.";
            createErrorResponseGeneric(res, 0, errorMessage);

        }

    }

    private void createErrorResponse403(HttpServletResponse res) {
        createErrorResponseGeneric(res, res.SC_FORBIDDEN, "You do not have permission to download this file.");
        //createRedirectResponse(res, "/dvn/faces/ErrorPage.xhtml?errorMsg=You do not have permission to download this file.&errorCode=403");
    }

    private void createErrorResponse403Remote(HttpServletResponse res) {
        createErrorResponseGeneric(res, res.SC_FORBIDDEN, "You do not have permission to download this remote file.");
        //createRedirectResponse(res, "/dvn/faces/ErrorPage.xhtml?errorMsg=You do not have permission to download this remote file.&errorCode=403");
    }

    private void createErrorResponse404(HttpServletResponse res) {
        createErrorResponseGeneric(res, res.SC_NOT_FOUND, "Sorry. The file you are looking for could not be found.");
        //createRedirectResponse(res, "/dvn/faces/ErrorPage.xhtml?errorMsg=Sorry. The file you are looking for could not be found.&errorCode=404");
    }

    // private methods for generating parameters for the DSB 
    // conversion call;

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

            varHeader = varHeader + "\n";
        }

        return varHeader;
    }

    private String generateAltFormat(String formatRequested) {
        String altFormat;

        //  

        if (formatRequested.equals("D00")) {
            altFormat = "text/tab-separated-values";
        } else if ( formatRequested.equals("D02") ) {
            altFormat = "application/x-rlang-transport";
        } else if ( formatRequested.equals("D03") ) {
            altFormat = "application/x-stata-6";
        } else {
            altFormat = "application/x-R-2";
        }
        return altFormat;
    }

    private String generateOriginalExtension(String fileType) {

        if (fileType.equalsIgnoreCase("application/x-spss-sav")) {
            return ".sav";
        } else if (fileType.equalsIgnoreCase("application/x-spss-por")) {
            return ".por";
        } else if (fileType.equalsIgnoreCase("application/x-stata")) {
            return ".dta";
        } else if (fileType.equalsIgnoreCase("application/x-dvn-csvspss-zip")) {
            return ".zip";
        } else if (fileType.equalsIgnoreCase("application/x-dvn-tabddi-zip")) {
            return ".zip";
        }

        return "";
    }

    private boolean generateImageThumb(String fileLocation) {

        String thumbFileLocation = fileLocation + ".thumb";

        // see if the thumb is already generated and saved:

        if (new File(thumbFileLocation).exists()) {
            return true;
        }

        // let's attempt to generate the thumb:

        // (I'm scaling all the images down to 64 pixels horizontally;
        // I picked the number 64 totally arbitrarily;
        // TODO: (?) make the default thumb size configurable
        // through a JVM option??


        if (new File("/usr/bin/convert").exists()) {

            String ImageMagick = "/usr/bin/convert -size 64x64 " + fileLocation + " -resize 64 -flatten png:" + thumbFileLocation;
            int exitValue = 1;

            try {
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec(ImageMagick);
                exitValue = process.waitFor();
            } catch (Exception e) {
                exitValue = 1;
            }

            if (exitValue == 0) {
                return true;
            }
        }

        // For whatever reason, creating the thumbnail with ImageMagick
        // has failed.
        // Let's try again, this time with Java's standard Image
        // library:

        try {
            BufferedImage fullSizeImage = ImageIO.read(new File(fileLocation));

	    if ( fullSizeImage == null ) {
		return false; 
	    }

            double scaleFactor = ((double) 64) / (double) fullSizeImage.getWidth(null);
            int thumbHeight = (int) (fullSizeImage.getHeight(null) * scaleFactor);

	    // We are willing to spend a few extra CPU cycles to generate
	    // better-looking thumbnails, hence the SCALE_SMOOTH flag. 
	    // SCALE_FAST would trade quality for speed. 

	    java.awt.Image thumbImage = fullSizeImage.getScaledInstance(64, thumbHeight, java.awt.Image.SCALE_SMOOTH);

            ImageWriter writer = null;
            Iterator iter = ImageIO.getImageWritersByFormatName("png");
            if (iter.hasNext()) {
                writer = (ImageWriter) iter.next();
            } else {
                return false;
            }

            BufferedImage lowRes = new BufferedImage(64, thumbHeight, BufferedImage.TYPE_INT_RGB);
            lowRes.getGraphics().drawImage(thumbImage, 0, 0, null);

            ImageOutputStream ios = ImageIO.createImageOutputStream(new File(thumbFileLocation));
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
	    dbgLog.info("ImageIO: caught an exception while trying to generate a thumbnail for "+fileLocation);

            return false;
        }
    }

    private String generateAltFileName(String formatRequested, String xfileId) {
        String altFileName = xfileId;

        if ( altFileName == null || altFileName.equals("")) {
            altFileName = "Converted";
        }

        if ( formatRequested != null ) {
            if (formatRequested.equals("D00")) {
                altFileName = FileUtil.replaceExtension(altFileName, "tab");
            } else if ( formatRequested.equals("D02") ) {
                altFileName = FileUtil.replaceExtension(altFileName, "ssc");
            } else if ( formatRequested.equals("D03") ) {
                altFileName = FileUtil.replaceExtension(altFileName, "dta");
            } else if ( formatRequested.equals("D04") ) {
                altFileName = FileUtil.replaceExtension(altFileName, "RData");
            } else {
                altFileName = FileUtil.replaceExtension(altFileName, formatRequested);
            }
        }

        return altFileName;
    }

    private String checkZipEntryName(String originalName, List nameList) {
        String name = originalName;
        int fileSuffix = 1;
        int extensionIndex = originalName.lastIndexOf(".");

        while (nameList.contains(name)) {
            if (extensionIndex != -1) {
                name = originalName.substring(0, extensionIndex) + "_" + fileSuffix++ + originalName.substring(extensionIndex);
            } else {
                name = originalName + "_" + fileSuffix++;
            }
        }
        nameList.add(name);
        return name;
    }

    private void zipMultipleFiles ( HttpServletRequest req,
                                    HttpServletResponse res,
                                    VDCUser user,
                                    VDC vdc,
                                    UserGroup ipUserGroup) {
        // a request for a zip-packaged multiple file archive.

        String fileId = req.getParameter("fileId");
        String studyId = req.getParameter("studyId");

        Study study = null;
        Collection files = new ArrayList();
        boolean createDirectoriesForCategories = false;

        String fileManifest = "";


        if (fileId != null) {
            String[] idTokens = fileId.split(",");

            for (String tok : idTokens) {
                StudyFile sf;
                try {
                    sf = studyFileService.getStudyFile(new Long(tok));
                    files.add(sf);
                } catch (Exception ex) {
                    fileManifest = fileManifest + tok + " DOES NOT APPEAR TO BE A VALID FILE ID;\r\n";
                }
            }
        } else if (studyId != null) {
            try {
                study = studyService.getStudy(new Long(studyId));
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
            if (file.isFileRestrictedForUser(user, vdc, ipUserGroup)) {
                fileManifest = fileManifest + file.getFileName() + " IS RESTRICTED AND CANNOT BE DOWNLOADED\r\n";
                iter.remove();
            }
        }

        if (files.size() == 0) {
            createErrorResponse403(res);
            return;
        }

        Long sizeLimit = Long.valueOf(104857600);
        // that's the default of 100 MB.

        Long sizeTotal = Long.valueOf(0);

        // this is the total limit of the size of all the files we
        // are packaging. if exceeded, we stop packaging files and add
        // a note to the manifest explaining what happened.
        // the value above is the default. a different value can
        // be set with a JVM option.

        String sizeLimitOption = System.getProperty("dvn.batchdownload.limit");

        if ( sizeLimitOption != null ) {
            Long sizeOptionValue = new Long(sizeLimitOption);
            if ( sizeOptionValue > 0 ) {
                sizeLimit = sizeOptionValue;
            }
        }

        FileDownloadObject remoteDownload = null;

        // now create zip stream
        try {
            // set content type:
            res.setContentType("application/zip");

            // create zipped output stream:

            OutputStream out = res.getOutputStream();
            ZipOutputStream zout = new ZipOutputStream(out);

            List nameList = new ArrayList(); // used to check for duplicates
            List successList = new ArrayList();

            iter = files.iterator();

            while (iter.hasNext()) {
                int fileSize = 0;
                StudyFile file = (StudyFile) iter.next();

                if ( sizeTotal < sizeLimit ) {
                    InputStream in = null;

                    String varHeaderLine = null;
                    String dbContentType = file.getFileType();

                    if (dbContentType != null && dbContentType.equals("text/tab-separated-values") && file.isSubsettable()) {
                        List datavariables = ((TabularDataFile) file).getDataTable().getDataVariables();
                        varHeaderLine = generateVariableHeader(datavariables);
                    }

                    if (dbContentType == null) {
                        dbContentType = "unknown filetype;";
                    }

                    Boolean Success = true;

                    if (file.isRemote()) {

                        // do the http magic;
                        // remote files may be subject to complex authentication and
                        // authorization.
                        // And for that we have a special method...

                        remoteDownload = initiateRemoteDownload (file, req);

                        if (remoteDownload.getStatus() != 200) {
                            fileManifest = fileManifest + file.getFileName() + " (" + dbContentType + ") COULD NOT be downloaded because an I/O error has occured. \r\n";

                            if (remoteDownload.getInputStream() != null) {
                                remoteDownload.getInputStream().close();
                            }

                            remoteDownload.releaseConnection();

                            Success = false;
                        } else {
                            in = remoteDownload.getInputStream();
                        }
                    } else {
                        in = getLocalFileAsStream(file);
                        if (in == null) {
                            fileManifest = fileManifest + file.getFileName()
                                + " (" + dbContentType
                                + ") COULD NOT be downloaded because an I/O error has occured. \r\n";

                            Success = false;
                        }
                    }

                    if (Success) {
                        String zipEntryName = file.getFileName();
                        zipEntryName = checkZipEntryName(zipEntryName, nameList);
                        ZipEntry e = new ZipEntry(zipEntryName);

                        zout.putNextEntry(e);

                        if (varHeaderLine != null) {
                            byte[] headerBuffer = varHeaderLine.getBytes();
                            zout.write(headerBuffer);
                            fileSize += (headerBuffer.length);
                        }

                        byte[] dataBuffer = new byte[8192];

                        int i = 0;
                        while ((i = in.read(dataBuffer)) > 0) {
                            zout.write(dataBuffer, 0, i);
                            fileSize += i;
                            out.flush();
                        }
                        in.close();
                        zout.closeEntry();

                        if (dbContentType == null) {
                            dbContentType = "unknown filetype;";
                        }

                        fileManifest = fileManifest + file.getFileName() + " (" + dbContentType + ") " + fileSize + " bytes.\r\n";

                        if (fileSize > 0) {
                            successList.add ( file.getId() );
                            sizeTotal += Long.valueOf(fileSize);
                        }

                        // if this was a remote stream, let's close
                        // the connection properly:

                        if (remoteDownload != null) {
                            remoteDownload.releaseConnection();
                        }
                    }
                } else {
                    fileManifest = fileManifest + file.getFileName() + " skipped because the total size of the download bundle exceeded the limit of " + sizeLimit + " bytes.\r\n";
                }
            }

            // finally, let's create the manifest entry:

            ZipEntry e = new ZipEntry("MANIFEST.TXT");

            zout.putNextEntry(e);
            zout.write(fileManifest.getBytes());
            zout.closeEntry();

            zout.close();

            // and finally finally, we can now increment the download
            // counts on all the files successfully zipped:

            Iterator it = successList.iterator();
            while (it.hasNext()) {
                Long fid = (Long) it.next();
                if ( vdc != null ) {
                    studyService.incrementNumberOfDownloads(fid, vdc.getId());
                } else {
                    studyService.incrementNumberOfDownloads(fid, (Long)null);
                }
            }

        } catch (IOException ex) {
            // if we caught an exception *here*, it means something
            // catastrophic has happened while packaging the zip archive
            // itself (I/O errors on individual files would be caught
            // above); so there's not much we can do except print a
            // generic error message:

            String errorMessage = "An unknown I/O error has occured while generating a Zip archive of multiple data files. Unfortunately, no further diagnostic information on the nature of the problem is avaiable to the Application at this point. It is possible that the problem was caused by a temporary network error. Please try again later and if the problem persists, report it to your DVN technical support contact.";
            createErrorResponse403(res);

            if (remoteDownload != null) {
                remoteDownload.releaseConnection();
            }
        }
    }


    private String remoteAuthRequired(String remoteHost) {
        String remoteAuthType = null;

        if (remoteHost == null) {
            return null;
        }

        RemoteAccessAuth remoteAuth = studyService.lookupRemoteAuthByHost(remoteHost);
        if (remoteAuth != null) {
            remoteAuthType = remoteAuth.getType();
        }

        return remoteAuthType;
    }

    private String getRemoteAuthCredentials(String remoteHost) {
        String remoteAuthCreds = null;

        if (remoteHost == null) {
            return null;
        }

        remoteAuthCreds = studyService.lookupRemoteAuthByHost(remoteHost).getAuthCred1();

        if (remoteAuthCreds != null) {
            return "Basic " + remoteAuthCreds;
        }

        return null;
    }

    private String dvnRemoteAuth(String remoteHost) {
        // if successful, this method will return the JSESSION string
        // for the authenticated session on the remote DVN.

        String remoteJsessionid = null;
        String remoteDvnUser = null;
        String remoteDvnPw = null;

        GetMethod loginGetMethod = null;
        PostMethod loginPostMethod = null;


        RemoteAccessAuth remoteAuth = studyService.lookupRemoteAuthByHost(remoteHost);

        if (remoteAuth == null) {
            return null;
        }

        remoteDvnUser = remoteAuth.getAuthCred1();
        remoteDvnPw = remoteAuth.getAuthCred2();

        if (remoteDvnUser == null || remoteDvnPw == null) {
            return null;
        }

        int status = 0;

        try {

            String remoteAuthUrl = "http://" + remoteHost + "/dvn/faces/login/LoginPage.xhtml";
            loginGetMethod = new GetMethod(remoteAuthUrl);
            loginGetMethod.setFollowRedirects(false);
            status = getClient().executeMethod(loginGetMethod);

            InputStream in = loginGetMethod.getResponseBodyAsStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in));

            String line = null;

            String viewstate = null;


            String regexpJsession = "jsessionid=([^\"?&]*)";
            String regexpViewState = "ViewState\" value=\"([^\"]*)\"";

            Pattern patternJsession = Pattern.compile(regexpJsession);
            Pattern patternViewState = Pattern.compile(regexpViewState);

            Matcher matcher = null;

            while ((line = rd.readLine()) != null) {
                matcher = patternJsession.matcher(line);
                if (matcher.find()) {
                    remoteJsessionid = matcher.group(1);
                }
                matcher = patternViewState.matcher(line);
                if (matcher.find()) {
                    viewstate = matcher.group(1);
                }
            }

            rd.close();
            loginGetMethod.releaseConnection();

            if (remoteJsessionid != null) {

                // We have found Jsession;
                // now we can log in,
                // has to be a POST method:

                loginPostMethod = new PostMethod(remoteAuthUrl + ";jsessionid=" + remoteJsessionid);
                loginPostMethod.setFollowRedirects(false);

                Part[] parts = {
                    new StringPart("vanillaLoginForm:vdcId", ""),
                    new StringPart("vanillaLoginForm:username", remoteDvnUser),
                    new StringPart("vanillaLoginForm:password", remoteDvnPw),
                    new StringPart("vanillaLoginForm_hidden", "vanillaLoginForm_hidden"),
                    new StringPart("vanillaLoginForm:button1", "Log in"),
                    new StringPart("javax.faces.ViewState", viewstate)
                };


                loginPostMethod.setRequestEntity(new MultipartRequestEntity(parts, loginPostMethod.getParams()));
                loginPostMethod.addRequestHeader("Cookie", "JSESSIONID=" + remoteJsessionid);
                status = getClient().executeMethod(loginPostMethod);

                String redirectLocation = null;

                if (status == 302) {
                    for (int i = 0; i < loginPostMethod.getResponseHeaders().length; i++) {
                        String headerName = loginPostMethod.getResponseHeaders()[i].getName();
                        if (headerName.equals("Location")) {
                            redirectLocation = loginPostMethod.getResponseHeaders()[i].getValue();
                        }
                    }
                }

                loginPostMethod.releaseConnection();
                int counter = 0;

                int redirectLimit = 20; // number of redirects we are willing to follow before we give up.

                while (status == 302 && counter < redirectLimit) {

                    if (counter > 0) {
                        for (int i = 0; i < loginGetMethod.getResponseHeaders().length; i++) {
                            String headerName = loginGetMethod.getResponseHeaders()[i].getName();
                            if (headerName.equals("Location")) {
                                redirectLocation = loginGetMethod.getResponseHeaders()[i].getValue();
                            }
                        }
                    }

                    // try following redirects until we get a static page,
                    // or until we exceed the hoop limit.
                    if (redirectLocation.matches(".*TermsOfUsePage.*")) {
                        loginGetMethod = remoteAccessTOU(redirectLocation + "&clicker=downloadServlet", remoteJsessionid, null, null);
                        if (loginGetMethod != null) {
                            status = loginGetMethod.getStatusCode();
                        }
                    } else {
                        loginGetMethod = new GetMethod(redirectLocation);
                        loginGetMethod.setFollowRedirects(false);
                        loginGetMethod.addRequestHeader("Cookie", "JSESSIONID=" + remoteJsessionid);
                        status = getClient().executeMethod(loginGetMethod);

                        //InputStream in = loginGetMethod.getResponseBodyAsStream();
                        //BufferedReader rd = new BufferedReader(new InputStreamReader(in));
                        //rd.close();
                        loginGetMethod.releaseConnection();
                        counter++;
                    }
                }
            }
        } catch (IOException ex) {
            if (loginGetMethod != null) {
                loginGetMethod.releaseConnection();
            }
            if (loginPostMethod != null) {
                loginPostMethod.releaseConnection();
            }
            return null;
        }

        return remoteJsessionid;
    }

    private String getICPSRcookie (String remoteHost, String fileDownloadUrl) {
        String icpsrSubscribtionUser = null;
        String icpsrSubscribtionPassword = null;

        RemoteAccessAuth remoteAuth = studyService.lookupRemoteAuthByHost(remoteHost);

        if (remoteAuth == null) {
            return null;
        }

        icpsrSubscribtionUser = remoteAuth.getAuthCred1();
        icpsrSubscribtionPassword = remoteAuth.getAuthCred2();

        if ( icpsrSubscribtionUser == null || icpsrSubscribtionPassword == null) {
            return null;
        }

        ICPSRauth icpsrAuth = new ICPSRauth();

        return icpsrAuth.obtainAuthCookie (icpsrSubscribtionUser, icpsrSubscribtionPassword, fileDownloadUrl);
    }

    private GetMethod remoteAccessTOU(String TOUurl, String jsessionid, String downloadURL, String extraCookies) {
        DvnTermsOfUseAccess dvnTOU = new DvnTermsOfUseAccess();
    
        jsessionid = dvnTOU.dvnAcceptRemoteTOU ( TOUurl, jsessionid, downloadURL, extraCookies );

        GetMethod finalGetMethod = null;
        int status = 0;

        try {
            finalGetMethod = new GetMethod ( downloadURL );
            finalGetMethod.setFollowRedirects(false);
	    
            finalGetMethod.addRequestHeader("Cookie", "JSESSIONID=" + jsessionid);

            if ( extraCookies != null ) {
                finalGetMethod.addRequestHeader("Cookie", extraCookies);
            }
            status = getClient().executeMethod(finalGetMethod);
        
        } catch (IOException ex) {
            if (finalGetMethod != null) {
                finalGetMethod.releaseConnection();
            }
        }

        if (status != 200) {
            if (finalGetMethod != null) {
                finalGetMethod.releaseConnection();
            }
            //return null;
        }
	
        return finalGetMethod;
    }

    // OLD, grandfathered-in methods:
    // ------------------------------

    // removed a bunch of them that had to do something with variable
    // labels since they were not being used. however, we need to double-check
    // if those were indeed needed for something. i recall there was a bug
    // report about labels missing in some format-converted files. so maybe
    // those methods were generating label lists that were supposed to be sent
    // to R. in other words, we'll revisit this. -- L.A.

    /**
     * Returns a List object that stores major metadata for all variables 
     * selected by an end-user
     *
     * @return    List of DataVariable objects that stores metadata
     */
    public List<DataVariable> getDataVariableForRequest() {
        List<DataVariable> dvs = new ArrayList<DataVariable>();
        for (Iterator el = dataVariables.iterator(); el.hasNext();) {
            DataVariable dv = (DataVariable) el.next();
            String keyS = dv.getId().toString();
            //if (varCart.containsKey(keyS)) {
                dvs.add(dv);
            //}
        }
        return dvs;
    }
        
    /**
     * 
     *
     * @param dvs   
     * @return      
     */
    public List<String> generateVariableIdList(List<String> dvs) {
        List<String> variableIdList = new ArrayList<String>();
        if (dvs != null) {
            for (String el : dvs){
                variableIdList.add("v" + el);
            }
        }
        return variableIdList;
    }

    /**
     * 
     *
     * @return    
     */
    public Map<Long, List<List<Integer>>> getSubsettingMetaData(Long noRecords){

        Map<Long, List<List<Integer>>> varMetaSet = new LinkedHashMap<Long, List<List<Integer>>>();

        List<DataVariable> dvs = getDataVariableForRequest();

    //populate the initial, empty varMetaSet: 

    for (Long count = new Long((long)0); count < noRecords; count++){
        List<List<Integer>> cardVarMetaSet = new LinkedList<List<Integer>>();
        varMetaSet.put((count+1), cardVarMetaSet); 
    }

        if (dvs != null) {
            for (int i = 0 ; i < dvs.size();i++  ){

                DataVariable dv = dvs.get(i);

                List<Integer> varMeta = new ArrayList<Integer>();

                varMeta.add( Integer.valueOf(dv.getFileStartPosition().toString()) );
                varMeta.add( Integer.valueOf(dv.getFileEndPosition().toString()) );
                // raw data: 1: numeric 2: character=> 0: numeric; 1 character
                varMeta.add( Integer.valueOf( (int)(dv.getVariableFormatType().getId()-1)  ) ); 
                
                if ( dv.getNumberOfDecimalPoints() == null ) {
                    varMeta.add ( 0 ); 
                } else if ( dv.getNumberOfDecimalPoints().toString().equals ("") ) {
                    varMeta.add ( 0 ); 
                } else {
                    varMeta.add( Integer.valueOf(dv.getNumberOfDecimalPoints().toString()) ); 
                }

                Long recordSegmentNumber = dv.getRecordSegmentNumber(); 

                //if ( varMetaSet.get(recordSegmentNumber) == null ) {
                //    List<List<Integer>> cardVarMetaSet = new LinkedList<List<Integer>>();
                //    varMetaSet.put(recordSegmentNumber, cardVarMetaSet); 
                //}

                varMetaSet.get(recordSegmentNumber).add(varMeta); 
            
            }
        }

        return varMetaSet; 
    }    
    
    public Map<String, Map<String, String>> getValueTableForRequestedVariables(List<DataVariable> dvs){
        Map<String, Map<String, String>> vls = new LinkedHashMap<String, Map<String, String>>();
        for (DataVariable dv : dvs){
            List<VariableCategory> varCat = new ArrayList<VariableCategory>();
            varCat.addAll(dv.getCategories());
            Map<String, String> vl = new HashMap<String, String>();
            for (VariableCategory vc : varCat){
                if (vc.getLabel() != null){
                    vl.put(vc.getValue(), vc.getLabel());
                }
            }
            if (vl.size() > 0){
                vls.put("v"+dv.getId(), vl);
            }
        }
        return vls;
    }

    public Map<String, Map<String, String>> getValueTablesForAllRequestedVariables(){
        Map<String, Map<String, String>> vls = getValueTableForRequestedVariables(getDataVariableForRequest());
        //Map<String, Map<String, String>> vln = getValueTablesOfRecodedVariables();
        //vls.putAll(vln);
        return vls;
    }
    
}
