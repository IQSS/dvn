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
 * VDCHandleRegistrationServlet.java
 *
 * Created on March 7, 2007, 3:16 PM
 */
package edu.harvard.iq.dvn.core.web.servlet;

import edu.harvard.iq.dvn.core.admin.NetworkRoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.gnrs.GNRSServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCSessionBean;
import java.io.*;
import javax.ejb.EJB;

import javax.inject.Inject;
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
    
    @Inject VDCSessionBean vdcSession;
    
    private boolean isNetworkAdmin(HttpServletRequest req) {
        VDCUser user = null;
        if ( vdcSession.getLoginBean() != null ) {
            user= vdcSession.getLoginBean().getUser();
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
