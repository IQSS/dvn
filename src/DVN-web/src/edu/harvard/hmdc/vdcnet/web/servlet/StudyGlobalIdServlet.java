/*
 * StudyGlobalIdServlet.java
 *
 * Created on June 14, 2007, 2:10 PM
 */

package edu.harvard.hmdc.vdcnet.web.servlet;

import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import java.io.*;
import javax.ejb.EJB;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author gdurand
 * @version
 */
public class StudyGlobalIdServlet extends HttpServlet {
    
    @EJB StudyServiceLocal studyService;
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
    
        String globalId = req.getParameter("globalId");
        
        if (globalId != null) {
            Study study = studyService.getStudyByGlobalId( globalId );
            if (study != null) {
                String locationPrefix = req.getScheme() +"://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();        
                res.sendRedirect(locationPrefix + "/faces/study/StudyPage.jsp?studyId=" + study.getId() );
            } else {
                createErrorResponse(res, "No study exists for the specified Global Id.");
            }
        } else {
            createErrorResponse(res, "Global Id not specified.");
        }
    }

    private void createErrorResponse(HttpServletResponse res, String message) {
        res.setContentType("text/html");
        try {
            PrintWriter out = res.getWriter();
            out.println("<HTML>");
            out.println("<HEAD><TITLE>Study</TITLE></HEAD>");
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
