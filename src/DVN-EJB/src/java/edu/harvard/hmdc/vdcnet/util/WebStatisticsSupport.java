/*
 * WebStatisticsSupport.java
 *
 * Created on August 23, 2007, 10:35 AM
 *
 * This class provides helper methods
 * that are used to provide querystring
 * and/or header information needed for
 * certain requests that interest the web
 * site statistics application.
 */

package edu.harvard.hmdc.vdcnet.util;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author wbossons
 */
public class WebStatisticsSupport {
    
    /** Creates a new instance of WebStatisticsSupport */
    public WebStatisticsSupport() {
    }
    
    public int getParameterFromHeader(String headername){
        int headerValue = 0;
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        if (request.getHeader(headername) != null)
            headerValue = 1;
        else
            headerValue = 0;
        return headerValue;
    }
    
    public String getQSArgument(String arg, int val) {
        String keyValuePair = "&" + arg + "=" + val;
        return keyValuePair;
    }
    
}
