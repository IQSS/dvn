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
package edu.harvard.iq.dvn.core.web.dataaccess;

// java core imports:
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

import javax.naming.InitialContext;


// Apache toolkit imports:
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;



// DVN App imports:
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.RemoteAccessAuth;
import edu.harvard.iq.dvn.core.web.dvnremote.DvnTermsOfUseAccess;
import edu.harvard.iq.dvn.core.web.dvnremote.ICPSRauth;

public class HttpAccessObject extends DataAccessObject {
    private StudyServiceLocal studyService = null;

    public HttpAccessObject () throws IOException {
        this(null);
    }

    public HttpAccessObject(StudyFile file) throws IOException {
        this (file, null);
    }

    public HttpAccessObject(StudyFile file, DataAccessRequest req) throws IOException {

        super(file, req);

        if (!file.isRemote()) {
            throw new IOException ("Not a remote file!");
        }

        this.setIsRemoteAccess(true);
        this.setIsHttpAccess(true);
        this.setIsDownloadSupported(true);
        //this.setIsNIOSupported(true);
    }


    public boolean canAccess (String location) throws IOException{
        return true;
    }

    //public void open (String location) throws IOException{

    //}

    //private void open (StudyFile file, Object req) throws IOException {
    public void open () throws IOException {

        StudyFile file = this.getFile();
        DataAccessRequest req = this.getRequest(); 

        if (req.getParameter("noVarHeader") != null) {
            this.setNoVarHeader(true);
        }

        try {
            this.studyService = (StudyServiceLocal) new InitialContext().lookup("java:comp/env/studyService");
        } catch (Exception e) {
            throw new IOException ("Caught exception trying to look up studyService; "+e.getMessage());
        }


        String remoteFileUrl = file.getFileSystemLocation();

        if (remoteFileUrl != null) {
            remoteFileUrl = remoteFileUrl.replaceAll(" ", "+");
            this.setRemoteUrl(remoteFileUrl);
        }

        Boolean zippedStream = false;


        GetMethod method = null;
        int status = 200;

        try {

            // If it's another DVN from which we are getting
            // the file, we need to pass the "noVarHeader"
            // argument along:

            if (remoteFileUrl.matches(".*FileDownload.*")) {
                if (this.noVarHeader()) {
                    remoteFileUrl = remoteFileUrl + "&noVarHeader=1";
                } else {
                    // and if we are retreiving this tab file in order
                    // to convert it to another format locally, we also
                    // have to add the noVarHeader flag, otherwise the
                    // header will be treated as a line of data!

                    if (req.getParameter("format") != null) {
                        remoteFileUrl = remoteFileUrl + "&noVarHeader=1";
                        // TODO -- ? -- do we need to check if this is
                        // a tab-delimited file?
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
                    remoteFileUrl = remoteFileUrl.replace("staging", "www");
                    remoteFileUrl = remoteFileUrl.replace("/cgi-bin/file", "/cgi-bin/bob/file");
                    method = new GetMethod(remoteFileUrl);

                    String icpsrCookie = getICPSRcookie(remoteHost, remoteFileUrl);

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
            status = (new HttpClient()).executeMethod(method);

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
                    status = (new HttpClient()).executeMethod(method);
                }
            }
        } catch (IOException ex) {
            //if (method != null) {
            //    method.releaseConnection();
            //}
            status = 404;
        }

        this.setStatus(status);

        if (status != 200) {
            if (method != null) {
                method.releaseConnection();
            }

            throw new IOException ("HTTP access failed; status: "+status);
        }

        InputStream in = null;

        try {
            if ( zippedStream ) {
                InputStream zipInputStream = method.getResponseBodyAsStream();
                ZipInputStream zin = new ZipInputStream(zipInputStream);
                zin.getNextEntry();

                this.setIsZippedStream(true);

                in = zin;

            } else {
                in = method.getResponseBodyAsStream();
            }

            this.setInputStream(in);

        } catch (IOException ex) {
            this.setStatus(404);
            String errorMessage = "I/O error has occured while attempting to retreive a remote data file: "+ex.getMessage()+". Please try again later and if the problem persists, report it to your DVN technical support contact.";
            this.setErrorMessage(errorMessage);

            throw new IOException ("I/O error has occured while attempting to retreive a remote data file: "+ex.getMessage());
        }

        this.setResponseHeaders(method.getResponseHeaders());
        this.setHTTPMethod(method);

    } // End of initiateLocalDownload;

    // Auxilary helper methods, HTTP access-specific:

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
            status = (new HttpClient()).executeMethod(loginGetMethod);

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
                status = (new HttpClient()).executeMethod(loginPostMethod);

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
                        status = (new HttpClient()).executeMethod(loginGetMethod);

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
            status = (new HttpClient()).executeMethod(finalGetMethod);

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

}