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
package edu.harvard.iq.dvn.core.util;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author wbossons
 */
public class WebStatisticsSupport implements java.io.Serializable  {
    
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
