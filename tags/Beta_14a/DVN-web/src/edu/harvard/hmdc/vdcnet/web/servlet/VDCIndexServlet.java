/*
 * VDCIndexServlet.java
 *
 * Created on March 7, 2007, 3:16 PM
 */

package edu.harvard.hmdc.vdcnet.web.servlet;

import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import java.io.*;
import javax.ejb.EJB;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author gdurand
 * @version
 */
public class VDCIndexServlet extends HttpServlet {
    
    @EJB IndexServiceLocal indexService;
    
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
            out.println("<h3>Reindex confirmation</h3>");
            out.println("To reindex, click on the button below.<body>");
            out.print("<form method=POST><input value=Reindex type=submit /></form>");
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
            //first delete files
            boolean deleteFailed = false;
            File indexDir = new File("index-dir");
            File[] files = indexDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                if ( !files[i].delete() ) {
                    deleteFailed = true;
                }
                
            }
            
            beginPage(out);
            if (!deleteFailed) {
                indexService.indexAll();
                 out.println("<h3>Reindexing succeeded.</h3>");
            } else {
                out.println("<h3>Reindexing failed.</h3>");
                out.println("There was a problem deleting the files. Please fix this manually, then try again.");
            }
            endPage(out);
            
        } else {
            displayUnauthorizedPage(out);
        }
    }
    
    private void beginPage(PrintWriter out) {
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Index Servlet</title>");
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
        return "VDC Index Servlet";
    }
    
}
