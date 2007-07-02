/*
 * RestoreViewPhaseListener.java
 *
 * Created on March 29, 2007, 1:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

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
public class RestoreViewPhaseListener implements PhaseListener {
  
    
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
