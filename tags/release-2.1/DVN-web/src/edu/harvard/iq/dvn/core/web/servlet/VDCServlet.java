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
 * VDCServlet.java
 *
 * Created on October 6, 2006, 3:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.servlet;

import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author gdurand
 */
public class VDCServlet extends HttpServlet{
    
    /** Creates a new instance of VDCServlet */
    public VDCServlet() {
    }

    @EJB VDCServiceLocal vdcService;
    
    public void service(HttpServletRequest req, HttpServletResponse res)  {
        String vdcAlias = null;
        String destination = "/";
        
        String uri= req.getPathInfo();
        int endIndex = uri.substring( 1 ).indexOf("/") + 1;

        if (endIndex > 0) {
             vdcAlias = uri.substring( 1, endIndex );  
             destination =  uri.substring(endIndex); 
        } else {
             vdcAlias = uri.substring( 1 );            
        }    
        
        VDC vdc = vdcService.findByAlias(vdcAlias);
        
        if (vdc != null) {
            if (destination.equals("/")) {
                // set to search page
                destination="/faces/StudyListingPage.xhtml";
            }

            req.setAttribute("vdc",vdc);
            RequestDispatcher rd = req.getRequestDispatcher(destination);
            try {
                rd.forward(req,res);
            } catch (ServletException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else {
            // todo: should this forward to a better error page?
            createErrorResponse404(res);
        }
    }

    private void createErrorResponse404(HttpServletResponse res) {
        res.setContentType("text/html");
	res.setStatus ( res.SC_NOT_FOUND ); 
        try {
            PrintWriter out = res.getWriter();
            out.println("<HTML>");
            out.println("<HEAD><TITLE>Dataverse not found!</TITLE></HEAD>");
            out.println("<BODY>");
            out.println("<BIG>Sorry. The dataverse URL you entered does not exist.</BIG>");
            for (int i = 0; i < 10; i++) {
                out.println("<!-- This line is filler to handle IE case for 404 errors   -->");
            }
            out.println("</BODY></HTML>");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }    
    
}
