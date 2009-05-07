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
 * Access.java
 *
 * Created on September 15, 2008, 6:05 PM
 *
 */

package edu.harvard.iq.dvn.core.web.dvnremote; 

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern; 

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod; 
import org.apache.commons.httpclient.methods.PostMethod; 
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;



/**
 *
 * @author landreev
 */
public class DvnTermsOfUseAccess {

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

	    int status = getClient().executeMethod(TOUgetMethod);
	    
	    InputStream in = TOUgetMethod.getResponseBodyAsStream(); 
	    BufferedReader rd = new BufferedReader(new InputStreamReader(in)); 
	    
	    String line = null;
	    
	    String viewstate      = null; 
	    String studyid        = null; 
	    String remotefileid   = null; 

	    String regexpJsession = "jsessionid=([^\"?&]*)"; 
	    String regexpViewState = "ViewState\" value=\"([^\"]*)\""; 
	    String regexpStudyId = "studyId\" value=\"([0-9]*)\""; 
	    //String regexpRemoteFileId = "(/FileDownload.*fileId=[0-9]*)";
	    String regexpRemoteFileId = "(/FileDownload.*)";
	    String regexpOldStyleForm = "content:termsOfUsePageView:";


	    Pattern patternJsession = Pattern.compile(regexpJsession); 
	    Pattern patternViewState= Pattern.compile(regexpViewState); 
	    Pattern patternStudyId = Pattern.compile(regexpStudyId); 
	    Pattern patternRemoteFileId = Pattern.compile(regexpRemoteFileId); 
	    Pattern patternOldStyleForm = Pattern.compile(regexpOldStyleForm); 
			    

	    Matcher matcher = null; 

	    if ( downloadURL != null ) {
		matcher = patternRemoteFileId.matcher(downloadURL);
		if ( matcher.find() ) {
		    remotefileid = matcher.group(1); 
		}
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
		if ( !compatibilityMode ) {
		    matcher = patternOldStyleForm.matcher(line);
		    if ( matcher.find() ) {
			compatibilityMode = true; 
		    }
		}		    
	    }

	    if ( compatibilityMode ) {
		compatibilityPrefix = "content:termsOfUsePageView:";
	    }



	    rd.close();
	    TOUgetMethod.releaseConnection(); 

	    if ( jsessionid != null ) {
		
		// We seem to have been issued a new JSESSIONID; 
		// or perhaps already had a JSESSIONID issued
		// to us when we logged in.
		// Now we can make the call agreeing to
		// to the Terms of Use;
		// it has to be a POST method: 

		TOUurl = TOUurl.substring(0, TOUurl.indexOf( "?" )); 
		TOUpostMethod = new PostMethod( TOUurl + ";jsessionid=" + jsessionid ); 
		TOUpostMethod.setFollowRedirects(false);

				
		Part[] parts = {
		    new StringPart( compatibilityPrefix + "form1:vdcId", "" ),
		    new StringPart( "pageName", "TermsOfUsePage" ),
		    new StringPart( compatibilityPrefix + "form1:studyId", studyid ),
		    new StringPart( compatibilityPrefix + "form1:redirectPage", remotefileid ),
		    new StringPart( compatibilityPrefix + "form1:tou", "download" ),
		    new StringPart( compatibilityPrefix + "form1:termsAccepted", "on" ),
		    new StringPart( compatibilityPrefix + "form1:termsButton", "Continue" ),
		    new StringPart( compatibilityPrefix + "form1_hidden", compatibilityPrefix + "form1_hidden'" ),
		    new StringPart( "javax.faces.ViewState", viewstate )
		};

		// TODO: 

		// no need to set the redirectPage parameter, if there's 
		// no filedownload url. 

		TOUpostMethod.setRequestEntity(new MultipartRequestEntity(parts, TOUpostMethod.getParams()));
		TOUpostMethod.addRequestHeader("Cookie", "JSESSIONID=" + jsessionid ); 
		status = getClient().executeMethod(TOUpostMethod);

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
		}

		TOUpostMethod.releaseConnection();
	    
		int counter = 0;

		while ( status == 302 && counter < 10 
			&& (!(redirectLocation.matches( ".*FileDownload.*" )))) {
			  
		    redirectGetMethod = new GetMethod ( redirectLocation );
		    redirectGetMethod.setFollowRedirects(false);
		    redirectGetMethod.addRequestHeader("Cookie", "JSESSIONID=" + jsessionid ); 
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

	return jsessionid; 
    }

}

