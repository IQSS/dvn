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
 * CharacterValidator.java
 *
 * Created on February 6, 2007, 4:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;

/**
 *
 * @author wbossons
 */
public class CharacterValidator implements Validator, java.io.Serializable  {
    private static String msg = new String();
    
    /** Creates a new instance of CharacterValidator */
    public CharacterValidator() {
    }
    
    public void validate(FacesContext context, UIComponent component, 
                            Object value) {
        if (value == null) return;
        String characterString = (String) value;
        try {
            if ( !validateChars(characterString) ) {
                FacesMessage message = new FacesMessage(msg);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                ((UIInput)component).setValid(false);
                context.addMessage(component.getClientId(context), message);
                context.renderResponse();
            }
        } catch (Exception e) {
            System.out.println("An exception was thrown ... " + e.toString());
        } 
    }
    
    public void validateWEmail(FacesContext context, UIComponent component, 
                            Object value) {
        EmailValidator emailValidator = new EmailValidator();
        if (value == null) return;
        String characterString = (String) value;
        try {
            if ( !validateChars(characterString) ) {
                if (emailValidator.validateEmail(characterString)){
                    //If it passes email validation, we will accept as valid......
                } else {
                    msg += "  Note that you may also enter a valid EMail address";
                    FacesMessage message = new FacesMessage(msg);
                    message.setSeverity(FacesMessage.SEVERITY_ERROR);
                    ((UIInput)component).setValid(false);
                    context.addMessage(component.getClientId(context), message);
                    context.renderResponse();
                }

            }
        } catch (Exception e) {
            System.out.println("An exception was thrown ... " + e.toString());
        } 
    }
    
    private static boolean validateChars (String characterString) {        
        if (characterString.matches("[a-zA-z0-9\\_\\-]*")) {
            return true;
        } else {

            msg = "Found an illegal character(s). Valid characters are a-Z, 0-9, '_', and '-' ";                    
            return false;
        }
    }
}
