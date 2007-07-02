/*
 * VDCFacesServlet.java
 *
 * Created on February 1, 2007, 11:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
         System.out.print("VDC Info:  Initing faces servlet....");
         delegate.service(request,response);
     } 
     catch(Throwable e) {
         redirectToErrorPage((HttpServletRequest) request, (HttpServletResponse) response, (Exception) e);
     }
}

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
            
}
