/*
 * VDCActionListener.java
 *
 * Created on January 31, 2007, 10:06 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

import com.sun.faces.application.ActionListenerImpl;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author wbossons
 */
public class VDCActionListener extends ActionListenerImpl implements ActionListener{  
    
    public void processAction(ActionEvent action){
        try {
            super.processAction(action);
        } catch (Exception e) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
            request.setAttribute("exception", e);
            NavigationHandler navigationhandler = facesContext.getApplication().getNavigationHandler();
            navigationhandler.handleNavigation(facesContext, null, "exceptionNavigation");
            facesContext.renderResponse();
        }
    }
    
}
