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
