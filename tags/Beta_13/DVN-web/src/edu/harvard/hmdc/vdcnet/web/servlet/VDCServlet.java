/*
 * VDCServlet.java
 *
 * Created on October 6, 2006, 3:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.servlet;

import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import java.io.IOException;
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
            // todo: forward to an error page (this alias does not exist)
        }
    }
}
