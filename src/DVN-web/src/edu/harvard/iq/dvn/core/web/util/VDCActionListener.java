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
 * VDCActionListener.java
 *
 * Created on January 31, 2007, 10:06 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.util;

import com.sun.faces.application.ActionListenerImpl;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 *
 * @author wbossons
 */
public class VDCActionListener extends ActionListenerImpl implements ActionListener, java.io.Serializable {  
    
    public void processAction(ActionEvent action) {
        try {
            super.processAction(action);
        } catch (Exception e) {
                FacesContext context            = FacesContext.getCurrentInstance();
                ExternalContext externalContext = context.getExternalContext();
                HttpServletRequest request      = (HttpServletRequest) externalContext.getRequest();
                HttpSession session             = request.getSession(false);
                session.setAttribute("exception", e);
                String redirectUrl              = request.getProtocol().substring(0, request.getProtocol().indexOf("/")) + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/ExceptionHandler";
                try {
                    externalContext.redirect(redirectUrl);
                 } catch (Exception ioe) {
                    System.out.println("An exception was thrown in the action listener  . . . ");
                } 
        }
    }
    
}
