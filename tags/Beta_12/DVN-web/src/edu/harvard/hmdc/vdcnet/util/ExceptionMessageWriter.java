/*
 * ExceptionMessageWriter.java
 *
 * Created on November 28, 2006, 3:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author wbossons
 */
public class ExceptionMessageWriter {
    
    /** Creates a new instance of ExceptionMessageWriter */
    public ExceptionMessageWriter() {
    }
    
    public static void addGlobalMessage(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg));
    }
    
    public static void removeGlobalMessage(String compareAgainst) {
        Iterator iterator = FacesContext.getCurrentInstance().getMessages(null);
            while (iterator.hasNext()){
                if (iterator.next().equals(compareAgainst)) iterator.remove();
            }
    }
    
    public static void logException(Exception e) {
        String msg = "An error occurred: " + e.getCause().toString();
        System.out.println(msg);
    }
            
    
}
