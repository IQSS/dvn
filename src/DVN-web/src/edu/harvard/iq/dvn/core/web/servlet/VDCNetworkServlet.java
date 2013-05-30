/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
package edu.harvard.iq.dvn.core.web.servlet;

import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
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
 * @author skraffmiller
 */
public class VDCNetworkServlet extends HttpServlet {
        /** Creates a new instance of VDCNetworkServlet */
    public VDCNetworkServlet() {
    }

    @EJB VDCNetworkServiceLocal vdcNetworkService;
    
    public void service(HttpServletRequest req, HttpServletResponse res)  {
        String vdcNetworkAlias = null;
        String destination = "/";
        System.out.print("in service");
        
        
        String uri= req.getPathInfo();
        int endIndex = uri.substring( 1 ).indexOf("/") + 1;

        if (endIndex > 0) {
             vdcNetworkAlias = uri.substring( 1, endIndex );  
             destination =  uri.substring(endIndex); 
        } else {
             vdcNetworkAlias = uri.substring( 1 );            
        }    

        VDCNetwork vdcNetwork = vdcNetworkService.findByAlias(vdcNetworkAlias);
        
        if (vdcNetwork != null) {
            if (destination.equals("/")) {
                // set to search page
                destination="/faces/HomePage.xhtml";
            }

            req.setAttribute("vdcNetwork",vdcNetwork);
            
            System.out.print("vdcNetwork " + vdcNetwork.getName());
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
            System.out.print("404 error ");
            createErrorResponse404(res);
        }
    }

    private void createErrorResponse404(HttpServletResponse res) {
        res.setContentType("text/html");
	res.setStatus ( res.SC_NOT_FOUND ); 
        try {
            PrintWriter out = res.getWriter();
            out.println("<HTML>");
            out.println("<HEAD><TITLE>Dataverses Network not found!</TITLE></HEAD>");
            out.println("<BODY>");
            out.println("<BIG>Sorry. The dataverses network URL you entered does not exist.</BIG>");
            for (int i = 0; i < 10; i++) {
                out.println("<!-- This line is filler to handle IE case for 404 errors   -->");
            }
            out.println("</BODY></HTML>");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    } 
    
}
