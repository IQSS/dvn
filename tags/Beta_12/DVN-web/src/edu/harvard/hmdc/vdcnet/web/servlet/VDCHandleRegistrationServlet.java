/*
 * VDCHandleRegistrationServlet.java
 *
 * Created on March 7, 2007, 3:16 PM
 */

package edu.harvard.hmdc.vdcnet.web.servlet;

import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.gnrs.GNRSServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetwork;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import java.io.*;
import javax.ejb.EJB;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author gdurand
 * @version
 */
public class VDCHandleRegistrationServlet extends HttpServlet {
    
    @EJB GNRSServiceLocal registrationService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    
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
        VDCNetwork network = vdcNetworkService.find();
        
        if (isNetworkAdmin(req) && network.isHandleRegistration()) {
            beginPage(out);
            out.println("<h3>Registration confirmation</h3>");
            out.println("To register all studies, click on the button below.<body>");
            out.print("<form method=POST><input value=Register type=submit /></form>");
            endPage(out);
            
        } else {
            displayUnauthorizedPage(out);
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
            beginPage(out);
                registrationService.registerAll();
                 out.println("<h3>Registration complete.</h3>");
            endPage(out);
            
        } else {
            displayUnauthorizedPage(out);
        }
    }
    
    private void beginPage(PrintWriter out) {
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Registration Servlet</title>");
        out.println("</head>");
        out.println("<body><center>");
    }
    private void endPage(PrintWriter out) {
        out.println("</center></body>");
        out.println("</html>");
        out.close();
    }
    
    private void displayUnauthorizedPage(PrintWriter out) {
        beginPage(out);
        out.println("<h3>You are not authorized for this action.</h3>");
        endPage(out);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "DVN Registration Servlet";
    }
    
}
