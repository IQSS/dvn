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
