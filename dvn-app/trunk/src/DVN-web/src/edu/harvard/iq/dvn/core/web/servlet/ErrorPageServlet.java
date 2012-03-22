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
 * ErrorPageServlet.java
 *
 * Created on January 29, 2007, 10:59 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.servlet;

import edu.harvard.iq.dvn.core.web.util.DateUtils;

import java.io.IOException;
import java.net.URLEncoder;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpSession;

public class ErrorPageServlet extends HttpServlet  {

    private static String timestamp = new String("");

    /** Creates a new instance of ErrorPageServlet */
    public ErrorPageServlet() {
    }
        
    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException  {
        FacesContext facescontext = FacesContext.getCurrentInstance();
        String cause = new String();
        Exception exception = null;
        exception = (Exception) req.getAttribute("exception");
        String virtualPath = null;
        if (req.getAttribute("virtualPath") != null) {
            virtualPath = (String) req.getAttribute("virtualPath"); // set in the VDCFacesServlet wjb
        }
        boolean optimisticLock=false;
        if (exception != null) {
            if (checkForOptimistLockException(exception) ) {
                 optimisticLock=true;
                 cause = "%20%20The form could not be saved because it contains stale data (due to concurrent editing by another user).  Please reload the form and try again.";
            } else {
                cause = "%20%20" + exception.getCause().toString();
            }
        }
        else if (req.getQueryString() != null)  {
            cause = req.getQueryString();
        }
        else if (req.getSession().getAttribute("exception") != null) {
            if (checkForOptimistLockException(((Exception)req.getSession().getAttribute("exception"))) ) {
                 optimisticLock=true;
                 cause = "%20%20The form could not be saved because it contains stale data (due to concurrent editing by another user).  Please reload the form and try again.";
            } else {
                cause = ((Exception)req.getSession().getAttribute("exception")).getCause().toString();
            }
            req.getSession().removeAttribute("exception");
        }
        else {
            cause = "We are sorry. An unspecified error occurred.";
        }
        String time="";
        if (!optimisticLock) {
            cause += "%20%20If this continues to occur%2C please <a href=\"/dvn/faces/ContactUsPage.xhtml\">Contact</a> the Dataverse Network admin with this message.";
            time = "&time=" + URLEncoder.encode(this.getTimeStamp().toString(), "UTF-8");
        }

        if (virtualPath != null) {
            res.sendRedirect(virtualPath + "/ErrorPage.xhtml?errorMsg=" + cause + time);
        } else {
           res.sendRedirect(req.getContextPath() + "/faces" + "/ErrorPage.xhtml?errorMsg=" + cause + time);  
        }
    }
    
    /**
     *
     * A utility to look at the request parameters
     *
     * @author wbossons
     */
    
    private void printRequestAttributes(HttpServletRequest request){
        Enumeration attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
             String attributeName = attributeNames.nextElement().toString();
             System.out.println("ERROR SERVLET: the header name/value pair is: " + attributeName + ":" + request.getAttribute(attributeName).toString());
        }
    }
    
    /** 
     *
     * get the timestamp
     *
     * @author wbossons
     *
     */
    private String getTimeStamp() {
        timestamp = DateUtils.getTimeStampString();
        return timestamp;
    }
    
    private boolean checkForOptimistLockException (Throwable t) {
        if (t == null) {
            return false;
        } else if (t instanceof OptimisticLockException) {
            return true;
        } else {
            return checkForOptimistLockException( t.getCause() );
        }
    }
        
}
