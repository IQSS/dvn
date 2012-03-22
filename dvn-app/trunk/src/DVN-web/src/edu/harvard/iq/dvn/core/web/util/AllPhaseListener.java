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
