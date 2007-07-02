/*
 * ICPSRproxyServlet.java
 *
 * Created on October 12, 2006, 6:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.servlet;

import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.study.FileCategory;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.ejb.EJB;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod; 
import org.apache.commons.codec.binary.Base64; 

/**
 *
 * @author leonid andreev
 */
public class ICPSRproxyServlet extends HttpServlet{

    private HttpClient client = null;
    
    /** Creates a new instance of ICPSRproxyServlet */
    public ICPSRproxyServlet() {
    }

    private HttpClient getClient() {
        if (client == null) {
            client = new HttpClient( new MultiThreadedHttpConnectionManager() );
        }
        return client;
    }

    @EJB StudyServiceLocal studyService;
    @EJB VDCServiceLocal vdcService;
    
    public void service(HttpServletRequest req, HttpServletResponse res)  {
        
        String icpsrId = req.getParameter("icpsrId");

        if (icpsrId != null) {
	    Base64 b64 = new Base64();

	    byte[] bytesDecoded = b64.decode ( icpsrId.getBytes() ); 

	    String icpsrURLdecoded = new String ( bytesDecoded ); 


	    if ( icpsrURLdecoded.startsWith("http") ) {

		GetMethod method = null; 
		int status = 200;

		try { 
		    method = new GetMethod ( icpsrURLdecoded );
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

		    int i = in.read();
    		    while (i != -1 ) {
			out.write(i);
			i = in.read();
		    }
		    in.close();
		    out.close();
                
                
		} catch (IOException ex) {
		    ex.printStackTrace();
		}

		method.releaseConnection();
	    } else {
		createErrorResponse404(res);
		return;
	    }

	} else {
	    createErrorResponse404(res);
	    return;
	}
    }

    private void createErrorResponseGeneric(HttpServletResponse res, int status, String statusLine) {
        res.setContentType("text/html");
	res.setStatus ( status ); 
        try {
            PrintWriter out = res.getWriter();
            out.println("<HTML>");
            out.println("<HEAD><TITLE>ICPSR File Download</TITLE></HEAD>");
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
            out.println("<HEAD><TITLE>ICPSR File Download</TITLE></HEAD>");
            out.println("<BODY>");
            out.println("<BIG>You do not have permission to download this file.</BIG>");
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
            out.println("<HEAD><TITLE>ICPSR File Download</TITLE></HEAD>");
            out.println("<BODY>");
            out.println("<BIG>No such object found for the File Id supplied.</BIG>");
            out.println("</BODY></HTML>");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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

