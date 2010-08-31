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
