/*
 * VDCTestServlet.java
 *
 * Created on December 14, 2006, 4:41 PM
 */

package edu.harvard.hmdc.vdcnet.web.servlet;

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
    
    @EJB SyncVDCServiceLocal syncVDCService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
   
    
    
    
    
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
      }
      else {
            // Schedule first export for 1:00 AM of next day 
            // repeat every 24 hours          
           syncVDCService.scheduleDaily();
       }
  
        
    }
   
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
