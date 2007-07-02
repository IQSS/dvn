/*
 * DDIExportServlet.java
 *
 * Created on December 18, 2006, 6:09 PM
 */

package edu.harvard.hmdc.vdcnet.web.servlet;

import edu.harvard.hmdc.vdcnet.ddi.DDI20ServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.util.FileUtil;
import java.io.*;
import javax.ejb.EJB;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author gdurand
 * @version
 */
public class DDIExportServlet extends HttpServlet {
    
    @EJB DDI20ServiceLocal ddiService;
    @EJB StudyServiceLocal studyService;
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        String studyId = req.getParameter("studyId");
        String fetchOriginal = req.getParameter("originalImport");
        String exportToLegacyVDC = req.getParameter("legacy");
        
        if (studyId != null) {
            try {
                Study s = studyService.getStudy( new Long( studyId ) );
                
                InputStream in = null;
                PrintWriter out= null;
                
                try {
                    if (fetchOriginal != null) {
                        // get the original xml from the file System
                        File studyDir = new File(FileUtil.getStudyFileDir(), s.getAuthority() + File.separator + s.getStudyId());
                        File originalImport = new File(studyDir, "original_imported_study.xml");
                        
                        if (originalImport.exists()) {
                            res.setContentType("text/xml");
                            out = res.getWriter();   
                            in = new FileInputStream(originalImport);
                        
                            int i = in.read();
                            while (i != -1 ) {
                                out.write(i);
                                i = in.read();
                            }
                        } else {
                            createErrorResponse(res, "There is no original import DDI for this study.");
                        }
                        
                    } else {
                        // otherwise create ddi from data
                        res.setContentType("text/xml");
                        out = res.getWriter();   
                        ddiService.exportStudy(s, out, (exportToLegacyVDC != null) );
                    }
                } finally {
                    if (out!=null) { out.close(); }
                    if (in!=null) { in.close(); }
                }
                
            } catch (Exception ex) {
                ex.printStackTrace();
                createErrorResponse(res, "An exception ocurred while fetching the DDI.");
            }
        } else {
            createErrorResponse(res, "No studyId was specifed for this request.");
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
