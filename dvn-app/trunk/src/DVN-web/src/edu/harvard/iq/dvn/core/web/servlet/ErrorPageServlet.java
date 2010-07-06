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
