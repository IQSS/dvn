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
 * DDIExportServlet.java
 *
 * Created on December 18, 2006, 6:09 PM
 */

package edu.harvard.iq.dvn.core.web.servlet;

import edu.harvard.iq.dvn.core.admin.NetworkRoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.ddi.DDIServiceLocal;
import edu.harvard.iq.dvn.ingest.dsb.DSBWrapper;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.StudyFileServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.TabularDataFile;
import edu.harvard.iq.dvn.core.util.FileUtil;
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
public class DDIExportServlet extends HttpServlet {
    
    @EJB DDIServiceLocal ddiService;
    @EJB StudyServiceLocal studyService;
    @EJB StudyFileServiceLocal studyFileService;
    
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
   
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {


       if (DSBWrapper.isDSBRequest(req) || isNetworkAdmin(req)) {
            String studyId = req.getParameter("studyId");
            String fileId = req.getParameter("fileId");
            String fetchOriginal = req.getParameter("originalImport");
            String exportToLegacyVDC = req.getParameter("legacy");

            InputStream in = null; // used if getting originalImport
            OutputStream out = res.getOutputStream();

            try {
                if (fileId != null) {
                    try {
                        StudyFile sf = studyFileService.getStudyFile( new Long( fileId ) );

                        if (!(sf instanceof TabularDataFile)) {
                            createErrorResponse(res, "The file you requested is NOT a tabular data file.");
                        } else {
                            res.setContentType("text/xml");
                            ddiService.exportDataFile( (TabularDataFile) sf, out );
                        }
                    } catch (Exception ex) {
                        if (ex.getCause() instanceof IllegalArgumentException) {
                            createErrorResponse(res, "There is no file with fileId: " + fileId);    
                        } else {
                            ex.printStackTrace();
                            createErrorResponse(res, "An exception ocurred while fetching the DDI for this data file.");
                        }
                    }
                } else if (studyId != null) {
                    try {
                        Study s = studyService.getStudy( new Long( studyId ) );

                        if (fetchOriginal != null) {
                            // get the original xml from the file System
                            File studyDir = new File(FileUtil.getStudyFileDir(), s.getAuthority() + File.separator + s.getStudyId());
                            File originalImport = new File(studyDir, "original_imported_study.xml");

                            if (originalImport.exists()) {
                                res.setContentType("text/xml");
                                in = new FileInputStream(originalImport);

                                byte[] dataBuffer = new byte[8192]; 

                                int i = 0;
                                while ( ( i = in.read (dataBuffer) ) > 0 ) {
                                    out.write(dataBuffer,0,i);
                                    out.flush(); 
                                }
                            } else {
                                createErrorResponse(res, "There is no original import DDI for this study.");
                            }

                        } else {
                            // otherwise create ddi from data
                            res.setContentType("text/xml");
                            ddiService.exportStudy(s, out);
                        }
                    } catch (Exception ex) {
                        if (ex.getCause() instanceof IllegalArgumentException) {
                            createErrorResponse(res, "There is no study with studyId: " + studyId);    
                        } else {                    
                            ex.printStackTrace();
                            createErrorResponse(res, "An exception ocurred while fetching the DDI for this study.");
                        }
                    }
                } else {
                    createErrorResponse(res, "No studyId or fileId was specifed for this request.");
                }
            } finally {
                if (out!=null) { out.close(); }
                if (in!=null) { in.close(); }
            }
        } else {
            createErrorResponse(res, "You are not authorized for this action.");
        }
    }
    
    private void createErrorResponse(HttpServletResponse res, String message) {
        res.setContentType("text/html");
        try {
            PrintWriter out = res.getWriter();
            out.println("<HTML>");
            out.println("<HEAD><TITLE>DDI Export</TITLE></HEAD>");
            out.println("<BODY>");
            out.println("<BIG>" + message + "</BIG>");
            out.println("</BODY></HTML>");
        } catch (IOException ex) {
            ex.printStackTrace();
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
