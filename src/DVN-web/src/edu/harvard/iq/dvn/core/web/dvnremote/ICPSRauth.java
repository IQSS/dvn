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
 * ICPSRauth.java
 *
 * Created on Mar 23, 2007, 3:13 PM
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
import org.apache.commons.httpclient.methods.PostMethod; 
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import  org.apache.commons.httpclient.protocol.Protocol;

/**
 *
 * @author landreev
 */
public class ICPSRauth {

    private static Logger dbgLog = Logger.getLogger(ICPSRauth.class.getPackage().getName());

    private String icpsrLoginUrl = "https://www.icpsr.umich.edu/ticketlogin";

    private HttpClient client = null;
    
    /* constructor:  */
    public ICPSRauth() {
	String icpsrLoginUrlJVMoption = System.getProperty("icpsr.login.url");

	// the JVM option overrides the default:

	if ( icpsrLoginUrlJVMoption != null ) {
	    icpsrLoginUrl = icpsrLoginUrlJVMoption; 
	}
    }

    private HttpClient getClient() {
        return new HttpClient();
    }

    /* 
       The method below authenticates with the ICPSR file server using
       their custom login protocol. It returns the obtained authorization 
       cookie that can later be used to authorize a download request. 
    */ 

    public String obtainAuthCookie ( String username, String password, String fileDownloadUrl ) {

        PostMethod loginPostMethod = null;

        if (username == null || password == null) {
            return null;
        }

        int status = 0;
	String icpsrAuthCookie = null;


        try {
	    dbgLog.fine ("entering ICPSR auth;"); 	    

	    HttpClient httpclient = getClient();

	    loginPostMethod = new PostMethod(icpsrLoginUrl);
	    
	    Part[] parts = {
		new StringPart("email", username),
		new StringPart("password", password),
		new StringPart("path", "ICPSR"),
		new StringPart("request_uri", fileDownloadUrl)
	    };

	    loginPostMethod.setRequestEntity(new MultipartRequestEntity(parts, loginPostMethod.getParams()));

	    status = httpclient.executeMethod(loginPostMethod);

	    dbgLog.fine ("executed POST method; status="+status); 

	    
	    if (status != 200) {
		loginPostMethod.releaseConnection();
		return null; 
	    }

	    String regexpTicketCookie = "(Ticket=[^;]*)"; 
	    Pattern patternTicketCookie = Pattern.compile(regexpTicketCookie);
	    
	    for (int i = 0; i < loginPostMethod.getResponseHeaders().length; i++) {
		String headerName = loginPostMethod.getResponseHeaders()[i].getName();
		if (headerName.equalsIgnoreCase("set-cookie")) {
		    String cookieHeader = loginPostMethod.getResponseHeaders()[i].getValue();
		    Matcher cookieMatcher = patternTicketCookie.matcher(cookieHeader);
		    if ( cookieMatcher.find() ) {
			icpsrAuthCookie = cookieMatcher.group(1); 
			dbgLog.fine ("detected ICPSR ticket cookie: "+cookieHeader);				    
		    }
		}
	    }
	    
	    loginPostMethod.releaseConnection();	    
	    return icpsrAuthCookie; 

        } catch (IOException ex) {
	    dbgLog.info ("ICPSR auth: caught IO exception."); 
	    ex.printStackTrace();

            if (loginPostMethod != null) {
                loginPostMethod.releaseConnection();
            }
            return null;
        }
    }
}
