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
