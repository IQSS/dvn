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
public class EncodingFilter implements Filter, java.io.Serializable  {
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
