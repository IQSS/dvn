/*
 * VDCAdminServlet.java
 *
 * Created on August 30, 2007, 3:05 PM
 */

package edu.harvard.hmdc.vdcnet.web.servlet;

import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import java.io.*;
import java.net.*;
import javax.ejb.EJB;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author Gustavo
 * @version
 */
public class VDCAdminServlet extends HttpServlet {
    
    @EJB StudyServiceLocal studyService;
    
    private boolean isNetworkAdmin(HttpServletRequest req) {
        VDCUser user = null;
        if ( LoginFilter.getLoginBean(req) != null ) {
            user= LoginFilter.getLoginBean(req).getUser();
            if (user.getNetworkRole()!=null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN) ) {
                return true;
            }
        }
        
        return false;
    }
    
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        
        if (isNetworkAdmin(req)) {
            beginPage(out);
            out.println("<h3>Admin</h3>");
            out.println("<form method=POST>");

            out.println("To remove a study lock, input the study id and click on the button below.<br/>");
            out.print("<input name=\"studyId\" size=4>");
            out.print("<input name=removeLock value=\"Remove Lock\" type=submit />");
            out.print("<hr>");
            out.println("</form>");
            endPage(out);
            
        } else {
            displayMessage(out, "You are not authorized for this action.");
        }
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();

        if (isNetworkAdmin(req)) {        
            if ( req.getParameter("removeLock") != null) {
                Long studyId = null;
                try {
                    studyId = new Long( req.getParameter("studyId") );
                    studyService.removeStudyLock(studyId);
                    displayMessage (out,"Study lock removed.", "(for study id = " + studyId + ")");
                } catch (NumberFormatException nfe) {
                    displayMessage (out, "Action failed.", "The study id must be of type Long.");
                } catch (Exception e) {
                    e.printStackTrace();
                    displayMessage (out, "Action failed.", "An unknown error occurred trying to remove lock for study id = " + studyId);
                }
            } else {
                displayMessage (out, "You have selected an action that is not allowed.");
            }
        } else {
            displayMessage(out, "You are not authorized for this action.");
        }        
    }
    
    private void beginPage(PrintWriter out) {
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Admin Servlet</title>");
        out.println("</head>");
        out.println("<body><center>");
    }
    private void endPage(PrintWriter out) {
        out.println("</center></body>");
        out.println("</html>");
        out.close();
    }

    private void displayMessage(PrintWriter out, String title) {
        displayMessage(out, title, null);
    }
    
    private void displayMessage(PrintWriter out, String title, String message) {
        beginPage(out);
        out.println("<h3>" + title + "</h3>");
        if (message != null && !message.trim().equals("") ) {
            out.println(message);
        }
        endPage(out);
    }
    
    /**
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "VDC Admin Servlet";
    }
    // </editor-fold>
}
