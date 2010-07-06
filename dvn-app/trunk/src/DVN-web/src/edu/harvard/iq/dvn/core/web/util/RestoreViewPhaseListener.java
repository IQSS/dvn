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
 * RestoreViewPhaseListener.java
 *
 * Created on March 29, 2007, 1:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.util;

import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 *
 * @author Ellen Kraffmiller
 */
public class RestoreViewPhaseListener implements PhaseListener, java.io.Serializable  {
  
    
     public PhaseId getPhaseId() {
            return PhaseId.RESTORE_VIEW;
     } 
     public void beforePhase(PhaseEvent pe){}
     
     public void afterPhase(PhaseEvent pe) {
         
         FacesContext context = pe.getFacesContext();
         for (Iterator it = context.getMessages(); it.hasNext();) {
                 FacesMessage elem = (FacesMessage) it.next();
                 if (elem.getSeverity().equals(FacesMessage.SEVERITY_ERROR)) {
                       context.getApplication().getNavigationHandler().handleNavigation(context,null,"error");
                       context.renderResponse();   
                 }
                 
             }
        
             
                
         }
     
    
}
