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
 * AllPhaseListener.java
 *
 * Created on March 29, 2007, 1:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.util;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author wbossons
 */
public class AllPhaseListener implements PhaseListener, java.io.Serializable  {
  
    
     public PhaseId getPhaseId() {
            return PhaseId.ANY_PHASE;
     } 
     
     public void beforePhase(PhaseEvent pe){
        //code any needed beforephase actions here
     }
     
     public void afterPhase(PhaseEvent pe) {
      
         if (pe.getPhaseId() == PhaseId.UPDATE_MODEL_VALUES) {
             // code any needed afterphase actions here (render response)
             // can work for other phases by referencing the correct phaseid
         }
         if (pe.getPhaseId() == PhaseId.RENDER_RESPONSE) {
             // code any needed afterphase actions here (render response)
             // can work for other phases by referencing the correct phaseid
         }
     }
}
