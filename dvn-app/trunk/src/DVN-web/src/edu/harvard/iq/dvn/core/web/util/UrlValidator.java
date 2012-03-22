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
 * UrlValidator.java
 *
 * Created on January 4, 2007, 1:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.util;

import java.net.URL;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author wbossons
 */
public class UrlValidator implements Validator, java.io.Serializable  {
    
    private static String msg = new String();
    
    /** Creates a new instance of UrlValidator */
    public UrlValidator() {
    }
    
    public void validate(FacesContext context, UIComponent component, 
                        Object value) {
        if (value == null) return;
        String url = (String) value;
        try {
            if (!validateUrl(url) ) {
                msg = "Please enter a valid url";
                FacesMessage message = new FacesMessage(msg);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                ((UIInput)component).setValid(false);
                context.addMessage(component.getClientId(context), message);
                throw new ValidatorException(message);
            }
        } catch (java.net.MalformedURLException mue) {
            System.out.println("An exception was thrown ... " + mue.toString());
        }
    }
    
    

    public boolean validateUrl (String aUrl) 
        throws java.net.MalformedURLException {
        boolean isValid = false;
        try {
            URL url = new URL(aUrl);
            if (url.getProtocol().indexOf("http") != -1 && url.getHost().length() > 0) {
                isValid = true;
            }
        } catch (java.net.MalformedURLException mue) {
            System.out.println( "MalformedURLException . . . " + mue.getCause().toString());
            msg = "Malformed Url Exception . . . " + mue.getCause().toString();
        } finally {
            return isValid;
        }
    }
    
    /** buildInContextUrl
     *
     * @description utility method
     * to build in-context urls.
     *
     * @param request 
     * @param alias String dataverse alias, in the form /dv/<alias>If a dvn, pass this as "".
     *
     * @return urlString
     *
     *@author wbossons
     *
     */
    public String buildInContextUrl(HttpServletRequest request, String alias) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String urlString    = new String("");
        String serverName   = new String("");
        String contextPath  = new String("");
        String servletPath  = new String("");
        String protocol     = new String("");
        String serverPort   = new String("");
        if (request != null) {
            serverName   = request.getServerName();
            contextPath  = request.getContextPath();
            servletPath  = request.getServletPath();
            protocol     = request.getProtocol().substring(0, request.getProtocol().indexOf("/")).toLowerCase() + "://";
            serverPort   = new String((request.getServerPort() != 80 ? ":" + request.getServerPort():""));
        }
        if (alias != null)
            urlString = protocol + serverName + serverPort + contextPath + alias +  servletPath;
        else
            urlString = protocol + serverName + serverPort + contextPath + servletPath;
        return urlString;
    }

    /** buildUrl
     *
     * @description utility method
     * to build urls.
     *
     * @param contextPath String - self explanatory. Ex: /dvn
     * @param scriptName String - everything after the contextPath,
     * but before the pageName and preceded by a slash
     *
     * @return urlString
     *
     */
    public String buildCrossContextUrl(String contextPath, String scriptName) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String urlString    = new String("");
        String serverName   = new String("");
        String protocol     = new String("");
        String servletPath  = new String("");
        String serverPort   = new String("");
        if (facesContext != null) {
            HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
            serverName   = request.getServerName();
            protocol     = request.getProtocol().substring(0, request.getProtocol().indexOf("/")).toLowerCase() + "://";
            serverPort   = new String((request.getServerPort() != 80 ? ":" + request.getServerPort():""));
        }
        urlString = protocol + serverName + serverPort + contextPath + scriptName;
        return urlString;
    }
}
