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
 * Access.java
 *
 * Created on September 15, 2008, 6:05 PM
 *
 */
package edu.harvard.iq.dvn.core.web.dvnremote; 

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import java.text.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern; 

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod; 
import org.apache.commons.httpclient.methods.PostMethod; 



/**
 *
 * @author landreev
 */
public class DvnTermsOfUseAccess {

    private static Logger dbgLog = Logger.getLogger(DvnTermsOfUseAccess.class.getPackage().getName());

    private HttpClient client = null;


    
    /* Simple constructor:  */
    public DvnTermsOfUseAccess() {
    }

    /* No recycling of HttpClients, for now; 
       It's seriously inefficient, but helps eliminate
       more potential problems during testing. */

    private HttpClient getClient() {
	//if (client == null) {
        //    client = new HttpClient( new MultiThreadedHttpConnectionManager() );
        //}
        //return client;
        return new HttpClient();
    }

    /* 
       The method below accepts the Terms Of Use agreement presented by a 
       remote DVN, follows all the redirects then returns the cookie
       corresponding to the session. 
    */

    public String dvnAcceptRemoteTOU ( String TOUurl, String jsessionid, String downloadURL ) {
	return dvnAcceptRemoteTOU ( TOUurl, jsessionid, downloadURL, null ); 
    }

    public String dvnAcceptRemoteTOU ( String TOUurl, String jsessionid, String downloadURL, String extraCookies ) {
	
	GetMethod TOUgetMethod = null; 
	PostMethod TOUpostMethod = null; 
	GetMethod redirectGetMethod = null; 

	String compatibilityPrefix = ""; 
	Boolean compatibilityMode = false; 

	try { 


	    TOUgetMethod = new GetMethod ( TOUurl );
	    if ( jsessionid != null ) {
		TOUgetMethod.addRequestHeader("Cookie", "JSESSIONID=" + jsessionid ); 
	    }
	    if ( extraCookies != null ) {
		TOUgetMethod.addRequestHeader("Cookie", extraCookies); 
	    }

	    String icesession     = null; 
	    String iceview        = null;
            String facesviewstate = null;
	    String studyid        = null; 
	    String remotefileid   = null; 
	    String remotehost     = null; 

	    String iceFacesUpdate = null; 
            
            /* 
             * As of DVN 3.0 (and IceFaces 3.*), the following has changed: 
             *  icesession - no longer used;
             *  ice.window - appears to have replaced the above;
             *  the old regex pattern for ViewState no longer works (see below);
             *  in fact, javax.faces.ViewState and ice.view must be 
             *  treated as separate parameters! (again, see below)
             *  (the last one needs to be verified; there's a chance that 
             *  javax.faces.ViewState is still not necessary... -- L.A.)
             * 
             * TODO: add re-test with DVN 2.* and make sure backward compatibility
             * mechanism is in place. -- L.A.
             *  
             */

	    String regexpRemoteFileId = "(/FileDownload.*)";
	    String regexpJsession = "JSESSIONID=([^;]*);"; 
	    String regexpRemoteHost = "(http://[^/]*/)";

            /* old ice.session; no longer used in 3.0:
	    String regexpIceSession = "session: \'([^\']*)\'";
             */
            
            /* ice.window, appears to have replaced ice.session above:
             */
            String regexpIceSession = "input [^>]*ice.window\"[^>]*value=\"([^\"]*)\"";
            
            /* ice.view pattern, changed:
	    String regexpIceViewState = "view: ([^,]*),"; 
             */
            String regexpIceViewState = "<input [^>]*ice.view\"[^>]*value=\"([^\"]*)\"";
            
            /* New in DVN/IceFaces 3.*: separate pattern of javax.faces.ViewState:
             */
            String regexpFacesViewState = "<input [^>]*ViewState\"[^>]*value=\"([^\"]*)\"";
           
            
	    String regexpStudyId = "studyId\"[^>]*value=\"([0-9]*)\""; 
	    //String regexpRemoteFileId = "(/FileDownload.*fileId=[0-9]*)";
	    String regexpOldStyleForm = "content:termsOfUsePageView:";


	    Pattern patternJsession = Pattern.compile(regexpJsession); 
	    Pattern patternRemoteFileId = Pattern.compile(regexpRemoteFileId); 
	    Pattern patternRemoteHost = Pattern.compile(regexpRemoteHost); 

	    Pattern patternIceSession = Pattern.compile(regexpIceSession); 
	    Pattern patternIceViewState = Pattern.compile(regexpIceViewState);
            Pattern patternFacesViewState = Pattern.compile(regexpFacesViewState);
	    Pattern patternStudyId = Pattern.compile(regexpStudyId); 
	    Pattern patternOldStyleForm = Pattern.compile(regexpOldStyleForm); 
			    

	    Matcher matcher = null; 


	    int status = getClient().executeMethod(TOUgetMethod);

	    for (int i = 0; i < TOUgetMethod.getResponseHeaders().length; i++) {
		String headerName = TOUgetMethod.getResponseHeaders()[i].getName();
		//dbgLog.info("TOU found header: "+headerName); 
		    
		if (headerName.equals("Set-Cookie")) {
		    dbgLog.fine("TOU found cookie header;"); 

		    String cookieHeader = TOUgetMethod.getResponseHeaders()[i].getValue();
		    matcher = patternJsession.matcher(cookieHeader);
		    if ( matcher.find() ) {
			jsessionid = matcher.group(1); 
			dbgLog.fine("TOU found jsessionid: "+jsessionid); 
		    }
		}
	    }
	    

	    
	    InputStream in = TOUgetMethod.getResponseBodyAsStream(); 
	    BufferedReader rd = new BufferedReader(new InputStreamReader(in)); 
	    
	    String line = null;
	    

	    if ( downloadURL != null ) {
		matcher = patternRemoteFileId.matcher(downloadURL);
		if ( matcher.find() ) {
		    remotefileid = matcher.group(1); 
		    dbgLog.fine("TOU found remotefileid: "+remotefileid); 
		}
		matcher = patternRemoteHost.matcher(downloadURL);
		if ( matcher.find() ) {
		    remotehost = matcher.group(1); 
                    // The update URL, below, only works with IceFaces < 2.0, 
                    // i.e., only if the remote DVN is v2.*:
		    //iceFacesUpdate = remotehost + "dvn/block/send-receive-updates";
                    // For IceFaces and DVN 3.*, the POST must be submitted to 
                    // the terms of use page itself:
                    // (needs to be verified -- L.A.)
                    iceFacesUpdate = remotehost + "dvn/faces/study/TermsOfUsePage.xhtml";
		    dbgLog.fine("TOU found remotehost: "+remotehost); 

		}
	    }


	    while ( ( line = rd.readLine () ) != null ) {
		matcher = patternIceSession.matcher(line);
		if ( matcher.find() ) {
		    icesession = matcher.group(1); 
		    dbgLog.fine("TOU found icesession: "+icesession); 
		}
		matcher = patternIceViewState.matcher(line);
		if ( matcher.find() ) {
		    iceview = matcher.group(1); 
		    dbgLog.fine("TOU found ice view: "+iceview);
		}
                matcher = patternFacesViewState.matcher(line);
		if ( matcher.find() ) {
		    facesviewstate = matcher.group(1); 
		    dbgLog.fine("TOU found faces view state: "+facesviewstate);
		}
		matcher = patternStudyId.matcher(line);
		if ( matcher.find() ) {
		    studyid = matcher.group(1); 
		    dbgLog.fine("TOU found study id: "+studyid); 
		}
		if ( !compatibilityMode ) {
		    matcher = patternOldStyleForm.matcher(line);
		    if ( matcher.find() ) {
			compatibilityMode = true; 
		    }
		}		    
	    }

	    //if ( compatibilityMode ) {
	    //	compatibilityPrefix = "content:termsOfUsePageView:";
	    //}



	    rd.close();
	    TOUgetMethod.releaseConnection(); 

	    if ( jsessionid != null ) {
		
		// We seem to have been issued a new JSESSIONID; 
		// or perhaps already had a JSESSIONID issued
		// to us when we logged in.
		// Now we can make the call agreeing to
		// to the Terms of Use;
		// it has to be a POST method: 

		TOUpostMethod = new PostMethod( iceFacesUpdate ); 
		//TOUurl = TOUurl.substring(0, TOUurl.indexOf( "?" )); 
		//TOUpostMethod = new PostMethod( TOUurl + ";jsessionid=" + jsessionid ); 
		dbgLog.fine("icefaces url: "+iceFacesUpdate); 

		TOUpostMethod.addRequestHeader("Cookie", "JSESSIONID=" + jsessionid ); 
		if ( extraCookies != null ) {
		    TOUpostMethod.addRequestHeader("Cookie", extraCookies); 
		}

		TOUpostMethod.setFollowRedirects(false);

				
		NameValuePair[] postParameters = {
                    /* new in 3.0: ViewState different from ice.view: */
		    new NameValuePair( "javax.faces.ViewState", facesviewstate ),
                    /* new in 3.0: no longer necessary: 
		    new NameValuePair( "javax.faces.RenderKitId", "ICEfacesRenderKit" ),
                     */
                    /* new in 3.0: no longer necessary: 
		    new NameValuePair( "ice.submit.partial", "false" ),
                     */
		    new NameValuePair( "icefacesCssUpdates", "" ),
                    /* new in 3.0: ice.window instead of icesession: */
		    new NameValuePair( "ice.window", icesession ),
                    /* new in 3.0: ice.view different from viewstate: */
		    new NameValuePair( "ice.view", iceview ),
		    new NameValuePair( "ice.focus", "form1:termsButton" ), 

		    new NameValuePair( "pageName", "TermsOfUsePage" ),

		    new NameValuePair( "form1", "form1" ),

		    new NameValuePair( "form1:vdcId", "" ),
		    new NameValuePair( "form1:studyId", studyid ),
		    new NameValuePair( "form1:redirectPage", remotefileid ),
		    new NameValuePair( "form1:tou", "download" ),
		    new NameValuePair( "form1:termsAccepted", "on" ),
		    new NameValuePair( "form1:termsButton", "Continue" )
		};


		// TODO: 

		// no need to set the redirectPage parameter, if there's 
		// no filedownload url. 

		TOUpostMethod.setRequestBody(postParameters); 

		status = getClient().executeMethod(TOUpostMethod);

		dbgLog.fine ( "TOU Post status: "+status ); 

		// Now the TOU system is going to redirect
		// us to the actual download URL. 
		// Note that it can be MORE THAN ONE 
		// redirects (first to the homepage, then
		// eventually to the file download url); 
		// So we want to just keep following the
		// redirect until we get the file. 
		// But just in case, we'll be counting the
		// redirect hoops to make sure we're not
		// stuck in a loop. 

		String redirectLocation = null;
		
		if ( status == 302 ) {
		    for (int i = 0; i < TOUpostMethod.getResponseHeaders().length; i++) {
			String headerName = TOUpostMethod.getResponseHeaders()[i].getName();
			if (headerName.equals("Location")) {
			    redirectLocation = TOUpostMethod.getResponseHeaders()[i].getValue(); 
			}
		    }
		} else if ( status == 200 ) {
		    for (int i = 0; i < TOUpostMethod.getResponseHeaders().length; i++) {
			String headerName = TOUpostMethod.getResponseHeaders()[i].getName();
			dbgLog.fine("TOU post header: "+headerName+"="+TOUpostMethod.getResponseHeaders()[i].getValue()); 
		    }
		    dbgLog.fine ( "TOU trying to read output of the post method;" ); 
		    InputStream pin = TOUpostMethod.getResponseBodyAsStream(); 
		    BufferedReader prd = new BufferedReader(new InputStreamReader(pin)); 
	    
		    String pline = null;
		    
		    while ( ( pline = prd.readLine () ) != null ) {
			dbgLog.fine ("TOU read line: "+pline); 
		    }
		    prd.close();
		}

		TOUpostMethod.releaseConnection();
	    
		int counter = 0;

		while ( status == 302 && counter < 10 
			&& (!(redirectLocation.matches( ".*FileDownload.*" )))) {
			  
		    redirectGetMethod = new GetMethod ( redirectLocation );
		    redirectGetMethod.setFollowRedirects(false);
		    redirectGetMethod.addRequestHeader("Cookie", "JSESSIONID=" + jsessionid ); 
		    if ( extraCookies != null ) {
			redirectGetMethod.addRequestHeader("Cookie", extraCookies); 
		    }

		    status = getClient().executeMethod(redirectGetMethod);
		    
		    if ( status == 302 ) {
			for (int i = 0; i < redirectGetMethod.getResponseHeaders().length; i++) {
			    String headerName = redirectGetMethod.getResponseHeaders()[i].getName();
			    if (headerName.equals("Location")) {
				redirectLocation = redirectGetMethod.getResponseHeaders()[i].getValue(); 
			    }
			}
		    }
		    redirectGetMethod.releaseConnection(); 
		    counter++; 
		
		}
				
	    }

	} catch (IOException ex) {
	    if (redirectGetMethod != null) { redirectGetMethod.releaseConnection(); }
	    if (TOUgetMethod != null) { TOUgetMethod.releaseConnection(); }
	    if (TOUpostMethod != null) { TOUpostMethod.releaseConnection(); }
	    return null; 
	}

	dbgLog.fine("TOU: returning jsessionid="+jsessionid);

	return jsessionid; 
    }

}

