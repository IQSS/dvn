/*
 * VDCIndexServlet.java
 *
 * Created on March 7, 2007, 3:16 PM
 */

package edu.harvard.hmdc.vdcnet.web.servlet;

import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.ejb.EJB;
import javax.ejb.EJBException;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author gdurand
 * @version
 */
public class VDCIndexServlet extends HttpServlet {
    
    @EJB IndexServiceLocal indexService;
    @EJB VDCServiceLocal vdcService;
    
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
            out.println("<h3>Index Admin</h3>");
            out.println("<form method=POST>");
            out.println("To reindex all studies, click on the button below.<br/>");
            out.print("<input name=index value=All type=submit />");
            out.print("<hr>");
            out.println("To index studies owned by a specific dataverse, input the dataverse id and click on the button below.<br/>");
            out.print("<input name=\"vdcToIndex\" size=4>");
            out.print("<input name=index value=Dataverse type=submit />");
            out.print("<hr>");
            out.println("To index arbitrary studies, input the study ids and click on the button below.<br/>");
            out.print("<textarea name=\"studyIds\" rows=10></textarea><br/>");
            out.print("<input name=index value=Studies type=submit />");
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
            String indexParam = req.getParameter("index");
        
            if ("Dataverse".equals(indexParam)) {
                Long vdcToIndex = null;
                try {
                    vdcToIndex = new Long(req.getParameter("vdcToIndex"));
                    VDC vdc =  vdcService.findById(vdcToIndex);
                    if (vdc != null) { 
                        List studyIDList = new ArrayList();
                        for (Study study :  vdc.getOwnedStudies() ) {
                            studyIDList.add( study.getId() );
                        }
                        indexService.indexList( studyIDList );
                        displayMessage (out,"Indexing succeeded.", "(for dataverse id = " + vdcToIndex + ")");
                    } else {
                        displayMessage (out, "Indexing failed.", "There is no dataverse with dvId = " + vdcToIndex);                    
                    }
                } catch (NumberFormatException nfe) {
                    displayMessage (out, "Indexing failed.", "The dataverse id must be of type Long.");
                } catch (Exception e) {
                    e.printStackTrace();
                    displayMessage (out, "Indexing failed.", "An unknown error occurred trying to index dataverse with id = " + vdcToIndex);
                }
                
            } else if ("Studies".equals(indexParam)) {
                String studyIds = req.getParameter("studyIds");
                List studyIdList = new ArrayList();
                String failedTokens = "";

                StringTokenizer st = new StringTokenizer(studyIds, ",; \t\n\r\f");
                 while (st.hasMoreTokens()) {
                     String token = st.nextToken();
                     try {
                        studyIdList.add( new Long(token) );
                     } catch (NumberFormatException nfe) {
                        if (!failedTokens.equals("")) {
                            failedTokens += ", ";
                        }
                        failedTokens += "\"" +token + "\"";
                    }
                 }
                indexService.indexList( studyIdList );
                if (!failedTokens.equals("")) {
                    failedTokens = "(However, the following tokens were not of type Long: " + failedTokens + ")";
                }
                displayMessage (out,"Indexing succeeded.", failedTokens);

            } else if ("All".equals(indexParam)) {
                //first delete files
                boolean deleteFailed = false;
                File indexDir = new File("index-dir");
                File[] files = indexDir.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if ( !files[i].delete() ) {
                        deleteFailed = true;
                    }

                }

                if (!deleteFailed) {
                    indexService.indexAll();
                     displayMessage (out,"Reindexing succeeded.");
                } else {
                    displayMessage (out,"Reindexing failed.","There was a problem deleting the files. Please fix this manually, then try again.");
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
        out.println("<title>Index Servlet</title>");
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
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "VDC Index Servlet";
    }
    
}
