/*
 * EncodingFilter.java
 *
 * Created on December 7, 2006, 12:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 *
 * @author wbossons
 */
public class EncodingFilter implements Filter {
    private String encoding;
    private FilterConfig filterConfig = null;
    private static final boolean debug = true;
    
    /** Creates a new instance of EncodingFilter */
    public EncodingFilter() {
    }
    
    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
    throws IOException, ServletException {
       //doNothing
    }
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
            req.setCharacterEncoding(encoding);
            resp.setContentType("text/html; UTF-8");
	    chain.doFilter(req, resp);
    }
        
    /**
     * Destroy method for this filter
     *
     */
    public void destroy() {
    }
    
    /**
     * Init method for this filter
     *
     */
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        this.encoding = filterConfig.getInitParameter("encoding");
        if (filterConfig != null) {
            if (debug) {
                log("EncodingFilter:Initializing filter");
            }
        }
    }
    
    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

}
