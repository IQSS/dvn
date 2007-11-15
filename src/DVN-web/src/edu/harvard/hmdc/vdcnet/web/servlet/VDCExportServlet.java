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
 * VDCTestServlet.java
 *
 * Created on December 14, 2006, 4:41 PM
 */

package edu.harvard.hmdc.vdcnet.web.servlet;

import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.ddi.DDI20ServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.study.SyncVDCServiceLocal;
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
@EJB(name="editStudy", beanInterface=edu.harvard.hmdc.vdcnet.study.EditStudyService.class)
public class VDCExportServlet extends HttpServlet {
    @EJB StudyServiceLocal studyService;    
    @EJB SyncVDCServiceLocal syncVDCService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    
    @EJB DDI20ServiceLocal ddiService;    
    
    
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // Optional parameter: authority
        // If parameter is null, then authority of this dataverse network will be used
        String authority = req.getParameter("authority");
        
        // Optional parameter: lastUpdateTime
        // Should be in the form of yyyy-MM-dd
        // If no parameter is passed, then schedule the daily update
        if (req.getParameter("lastUpdateTime")!=null) {
            // Do the export immediately -
            // Export all studies in the authority where lastUpdateTime = "lastUpdateTime" param
            syncVDCService.scheduleNow(req.getParameter("lastUpdateTime"), authority);
        } else {
            // Schedule first export for 1:00 AM of next day
            // repeat every 24 hours
            syncVDCService.scheduleDaily();
        }
        
        
    }
    
    
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        
        
        beginPage(out);
        out.println("<h3>Admin</h3>");
        out.println("<form method=POST>");
        if (isNetworkAdmin(req)) {
            out.println("To get DDI XML for a study, input the study id and click 'GetDDI'.<br/>");
            out.print("<input name=\"studyId\" size=8>");
            out.print("<input name=getDDI value=\"Get DDI\" type=submit />");
            out.print("<hr>");
        
        out.println("To export all studies updated after a given date, enter the info below and click 'Export Studies'<br/>");
        out.print("<input name=exportStudies value=\"Export Studies\" type=submit />");
        out.print("<hr>");
        out.print("<input name=\"studyId\" size=8>");
        
        out.println("</form>");
        } else {
               displayMessage(out, "You are not authorized for this action.");
         
        }
        endPage(out);
        
        
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        OutputStream os = response.getOutputStream();
        PrintWriter out = new PrintWriter(os);
        if (isNetworkAdmin(request)) {
            if (request.getParameter("getDDI")!=null) {
                if (request.getParameter("studyId")!=null) {
                    Long studyId = Long.parseLong(request.getParameter("studyId"));
                    try {
                        Study study = studyService.getStudy(studyId);
                        ddiService.exportStudy(study, os);
                    } catch(Exception e) {
                        e.printStackTrace();
                        displayMessage(out,"Exception exporting study to DDI, see server.log for details.");
                    }
                }
            } else {
                processRequest(request, response);
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
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
