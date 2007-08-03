/*
 * SessionCounter.java
 *
 * Created on March 19, 2007, 4:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Ellen Kraffmiller
 */
public class SessionCounter {
    
    /** Creates a new instance of SessionCounter */
    public SessionCounter() {
      
    }
   public final static synchronized Long getNext() {
            HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            Long currentVal = (Long)session.getAttribute("sessionCounter");
            if (currentVal==null) {
                currentVal = new Long(-1);
            } else {
                currentVal = new Long(currentVal.intValue()-1);
            }
            session.setAttribute("sessionCounter",currentVal);
            return currentVal;
            
        }    
}