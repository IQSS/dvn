/*
 * UrlValidator.java
 *
 * Created on January 4, 2007, 1:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

import java.net.URL;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author wbossons
 */
public class UrlValidator implements Validator {
    
    private static String msg = new String();
    
    /** Creates a new instance of UrlValidator */
    public UrlValidator() {
    }
    
    public void validate(FacesContext context, UIComponent component, 
                        Object value) {
        if (value == null) return;
        String url = (String) value;
        try {
            if (!validateUrl(url) ) {
                msg = "Please enter a valid url";
                FacesMessage message = new FacesMessage(msg);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                ((UIInput)component).setValid(false);
                context.addMessage(component.getClientId(context), message);
                throw new ValidatorException(message);
            }
        } catch (java.net.MalformedURLException mue) {
            System.out.println("An exception was thrown ... " + mue.toString());
        }
    }
    
    

    public boolean validateUrl (String aUrl) 
        throws java.net.MalformedURLException {
        boolean isValid = false;
        try {
            URL url = new URL(aUrl);
            if (url.getProtocol().indexOf("http") != -1 && url.getHost().length() > 0) {
                isValid = true;
            }
        } catch (java.net.MalformedURLException mue) {
            System.out.println( "MalformedURLException . . . " + mue.getCause().toString());
            msg = "Malformed Url Exception . . . " + mue.getCause().toString();
        } finally {
            return isValid;
        }
    }
}
