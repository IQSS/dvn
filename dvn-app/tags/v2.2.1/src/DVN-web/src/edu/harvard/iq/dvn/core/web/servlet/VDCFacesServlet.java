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
 * VDCFacesServlet.java
 *
 * Created on February 1, 2007, 11:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.servlet;

import edu.harvard.iq.dvn.core.web.util.UrlValidator;
import edu.harvard.iq.dvn.core.web.common.VDCRequestBean;
import java.io.IOException;
import java.util.Enumeration;
import javax.faces.FactoryFinder;
import javax.faces.webapp.FacesServlet;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author wbossons
 */
 /** Creates a new instance of VDCFacesServlet */
public class VDCFacesServlet implements Servlet { 

 private static final String INIT_PARAM_ERROR_PAGE = "errorPage";

private FacesServlet delegate;

private String errorPage;

public void init(ServletConfig servletConfig) throws ServletException {
     FactoryFinder.setFactory(FactoryFinder.FACES_CONTEXT_FACTORY, "com.sun.faces.context.FacesContextFactoryImpl");
     FactoryFinder.setFactory(FactoryFinder.LIFECYCLE_FACTORY, "com.sun.faces.lifecycle.LifecycleFactoryImpl");
     delegate = new FacesServlet();
     delegate.init(servletConfig);
     errorPage = servletConfig.getInitParameter(INIT_PARAM_ERROR_PAGE);
}


public void destroy() {
     delegate.destroy();
}

public ServletConfig getServletConfig() {
     return delegate.getServletConfig();
}

public String getServletInfo() {
     return delegate.getServletInfo();
}

public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
     try {
        delegate.service(request,response);
          
     } catch(Throwable e) {
         if (request.getAttribute("dataverseURL") != null)
            redirectToErrorPage((HttpServletRequest) request, (HttpServletResponse) response, (Exception) e, (String)request.getAttribute("dataverseURL"));
         else
             redirectToErrorPage((HttpServletRequest) request, (HttpServletResponse) response, (Exception) e, new String(""));
     } 
}

    /** 
     * redirectToErrorPage
     *
     * This is essentially deprecated
     * in the context of the VDCFacesServlet.
     * It remains to support any portions of the
     * code that may use it. 
     *
     * See overloaded redirectToErrorPage below.
     * @author wbossons
     *
     *
     */
    private void redirectToErrorPage(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
         if (!"".equals(errorPage) && !response.isCommitted())  {
                request.setAttribute("exception", e);
                String virtualPathStr = parseVirtual(request);
                request.setAttribute("virtualPath", virtualPathStr);
                try {
                    getServletConfig().getServletContext().log("VDC Info:  Found an application error ...");
                    RequestDispatcher requestdispatcher = getServletConfig().getServletContext().getRequestDispatcher(errorPage);
                    requestdispatcher.forward(request,response);
                } catch (ServletException se) {
                    getServletConfig().getServletContext().log("VDC Error:  Forwarding to error page failed, attempting redirect.");
                    response.sendRedirect(request.getContextPath() + errorPage + "?appex=" + e.getCause().toString());
                }
         }
    }
    
    /**
     * an overloaded method to capture the virtual
     * address if present in the url
     *
     * @param request
     * @param response
     * @param e the exception object
     * @param dataversePath -- a string like /dv/<alias>
     *
     *
     */
     private void redirectToErrorPage(HttpServletRequest request, HttpServletResponse response, Exception e, String dataversePath) throws IOException {
         if (!"".equals(errorPage) && !response.isCommitted())  {
                request.setAttribute("exception", e);
                String virtualPathStr = parseVirtual(request, dataversePath);
                request.setAttribute("virtualPath", virtualPathStr);
                try {
                    getServletConfig().getServletContext().log("VDC Info:  Found an application error ...");
                    RequestDispatcher requestdispatcher = getServletConfig().getServletContext().getRequestDispatcher(errorPage);
                    requestdispatcher.forward(request,response);
                } catch (ServletException se) {
                    getServletConfig().getServletContext().log("VDC Error:  Forwarding to error page failed, attempting redirect.");
                    response.sendRedirect(request.getContextPath() + errorPage + "?appex=" + e.getCause().toString());
                }
         }
    }
    
    /** A utility for looking at the request headers
     *
     *
     * @author wbossons
     *
     */
    private void printHeaders(HttpServletRequest request) {
        Enumeration enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()){
            String elementName = enumeration.nextElement().toString();
            System.out.println("VDCFacesServlet: the header name/value pair is " + elementName + ":" + request.getHeader(elementName));
        }
    }
    
    /**
     * @DEPRECATED in favor of parseVirtual(String dataversePath)
     *  See overloaded method below.
     *
     *  parse the path from the referer
     *  get everything between the contextpath end and the beginning / of the servletcontext
     *
     * @author wbossons
     */
    private String parseVirtual(HttpServletRequest request){
        String virtualPathStr = new String("");
        if (request.getHeader("referer") != null) {
            virtualPathStr = (String)request.getHeader("referer");
            virtualPathStr = virtualPathStr.substring(virtualPathStr.indexOf("/", virtualPathStr.indexOf(request.getContextPath())+1), virtualPathStr.indexOf(request.getServletPath()));
        }
        return virtualPathStr;
    }
    
    /**  overloaded method to 
     *   parse the path from the referer
     *   when the intitial request has a set virtual DV path in it
     *   such as /dv/<alias>
     *  get everything between the contextpath end and the beginning / of the servletcontext
     *
     *  @param request
     *  @param dataversePath such as /dv/<alias>
     * @author wbossons
     */
    private String parseVirtual(HttpServletRequest request, String dataversePath){
        String virtualPathStr = new String("");
        UrlValidator urlvalidator = new UrlValidator();
        virtualPathStr = urlvalidator.buildInContextUrl(request, dataversePath);
        return virtualPathStr;
    }
            
}
