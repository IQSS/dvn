/*
 * ErrorPageServlet.java
 *
 * Created on January 29, 2007, 10:59 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import javax.persistence.OptimisticLockException;

public class ErrorPageServlet extends HttpServlet  {

    private static String timestamp = new String("");

    /** Creates a new instance of ErrorPageServlet */
    public ErrorPageServlet() {
    }
        
    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException  {
        FacesContext facescontext = FacesContext.getCurrentInstance();
        String cause = new String();
        Exception exception = (Exception) req.getAttribute("exception");
        String virtualPath = (String) req.getAttribute("virtualPath"); // set in the VDCFacesServlet wjb
        boolean optimisticLock=false;
        if (exception != null) {
            if (exception.getCause()!=null 
                    && exception.getCause().getCause()!=null 
                    && exception.getCause().getCause().getCause()!=null 
                    && (exception.getCause() instanceof OptimisticLockException
                        || exception.getCause().getCause() instanceof OptimisticLockException
                        || exception.getCause().getCause().getCause() instanceof OptimisticLockException)) {
                 optimisticLock=true;
                 cause = "%20%20The form could not be saved because it contains stale data (due to concurrent editing by another user).  Please reload the form and try again.";
            } else {
                cause = "%20%20" + exception.getCause().toString();
            }
        }
        else if (req.getQueryString() != null)  {
            cause = req.getQueryString();
        }
        else {
            cause = "We are sorry. An unspecified error occurred.";
        }
        String time="";
        if (!optimisticLock) {
            cause += "%20%20If this continues to occur, please <a href='/dvn/faces/ContactUsPage.jsp'>Contact</a> the Dataverse Network admin with this message.";
            time = "&time=" + URLEncoder.encode(this.getTimeStamp().toString(), "UTF-8");
        }
        if (virtualPath!=null) {
            res.sendRedirect(req.getContextPath() + virtualPath + "/faces" + "/ErrorPage.jsp?errorMsg=" + cause + time);
        } else {
           res.sendRedirect(req.getContextPath() + "/faces" + "/ErrorPage.jsp?errorMsg=" + cause + time);  
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
        Date date = new Date(); 
        timestamp = DateFormat.getDateTimeInstance().format(date);
        return timestamp;
    }
        
}
