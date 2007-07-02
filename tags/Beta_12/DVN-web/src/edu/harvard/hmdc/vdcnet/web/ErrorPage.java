/*
 * ErrorPage.java
 *
 * Created on March 29, 2007, 3:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author Ellen Kraffmiller
 */
public class ErrorPage {
    
    /** Creates a new instance of ErrorPage */
    public ErrorPage() {
    }
    
    public List getMessages() {
        List msgs = new ArrayList();
        FacesContext context = FacesContext.getCurrentInstance();
        for (Iterator it = context.getMessages(); it.hasNext();) {
            FacesMessage elem = (FacesMessage) it.next();
            msgs.add(elem.getSummary());
        }
        return msgs;
    }
}
