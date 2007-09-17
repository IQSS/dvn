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
 * VDCAdminServlet.java
 *
 * Created on August 30, 2007, 3:05 PM
 */

package edu.harvard.hmdc.vdcnet.web.servlet;

import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.PasswordEncryption;
import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;

/**
 *
 * @author Gustavo
 * @version
 */
public class VDCAdminServlet extends HttpServlet {
    @Resource(name="jdbc/VDCNetDS") DataSource dvnDatasource;
    @EJB StudyServiceLocal studyService;
    @EJB UserServiceLocal userService;
    
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
        
     
            beginPage(out);
            out.println("<h3>Admin</h3>");
            out.println("<form method=POST>");
            if (isNetworkAdmin(req)) {
                out.println("To remove a study lock, input the study id and click on the button below.<br/>");
                out.print("<input name=\"studyId\" size=8>");
                out.print("<input name=removeLock value=\"Remove Lock\" type=submit />");
                out.print("<hr>");
            }
            out.println("To encrypt all current passwords, click on the Encrypt Passwords button<br/>");
            out.print("<input name=encryptPasswords value=\"Encrypt Passwords\" type=submit />");
            out.print("<hr>");            
            out.println("</form>");
            endPage(out);
            
        
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();

        if (isNetworkAdmin(req) || req.getParameter("encryptPasswords")!=null ) {        
            if ( req.getParameter("removeLock") != null) {
                Long studyId = null;
                try {
                    studyId = new Long( req.getParameter("studyId") );
                    studyService.removeStudyLock(studyId);
                    displayMessage (out,"Study lock removed.", "(for study id = " + studyId + ")");
                } catch (NumberFormatException nfe) {
                    displayMessage (out, "Action failed.", "The study id must be of type Long.");
                } catch (Exception e) {
                    e.printStackTrace();
                    displayMessage (out, "Action failed.", "An unknown error occurred trying to remove lock for study id = " + studyId);
                }
            } else if (req.getParameter("encryptPasswords") != null) {
                try {
                    encryptPasswords();
                    displayMessage(out, "Passwords encrypted.");
                } catch(SQLException e) {
                    e.printStackTrace();
                    if (e.getSQLState().equals("42703")) {
                        displayMessage(out, "Passwords already encrypted");
                    } else {
                        displayMessage (out, "SQLException updating passwords");
                    }
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

    
    /**
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "VDC Admin Servlet";
    }
    
    private void encryptPasswords() throws SQLException {
        String selectString = "SELECT id, password from vdcuser";
        String updateString = "update vdcUser set encryptedpassword = ? where id = ?";
        
        Connection conn=null;
        PreparedStatement sth = null;
        PreparedStatement updateStatement=null;
        conn = dvnDatasource.getConnection();
        sth = conn.prepareStatement(selectString);
        ResultSet rs = sth.executeQuery();
        
        List<String> ids = new ArrayList<String>();
        List<String> passwords = new ArrayList<String>();
        
        while(rs.next()) {
            ids.add(rs.getString(1));
            passwords.add(rs.getString(2));
        }
        updateStatement = conn.prepareStatement(updateString);
        for (int i=0;i<ids.size();i++) {
            if (passwords.get(i)!=null && !passwords.get(i).trim().equals("")) {
                updateStatement.setString(1, userService.encryptPassword(passwords.get(i)));
                updateStatement.setString(2, ids.get(i));
                updateStatement.executeUpdate();
            }
     
        }
        updateStatement = conn.prepareStatement("alter table vdcuser drop column password");
       
        updateStatement.executeUpdate();
       
    }
    // </editor-fold>
}
