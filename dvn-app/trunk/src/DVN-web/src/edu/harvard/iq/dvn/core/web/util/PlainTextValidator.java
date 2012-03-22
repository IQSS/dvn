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
 * validate fields that should not allow html
 */
package edu.harvard.iq.dvn.core.web.util;

import java.net.URLEncoder;
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
public class PlainTextValidator implements Validator, java.io.Serializable  {

    private static String msg = new String();

    /** Creates a new instance of UrlValidator */
    public PlainTextValidator() {
    }

    public void validate(FacesContext context, UIComponent component,
                        Object value) {
        if (value == null) return;
        String characterString = (String) value;
        try {
            if ( validateChars(characterString) ) {
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

    public boolean isValidTextEntry(FacesContext context, UIComponent component,
                        Object value) {
        boolean isValid = true;
        if (value == null) return isValid;
        String characterString = (String) value;
        try {
            if ( validateChars(characterString) ) {
                isValid = false;
                FacesMessage message = new FacesMessage(msg);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                ((UIInput)component).setValid(false);
                context.addMessage(component.getClientId(context), message);
            } 
        } catch (Exception e) {
            System.out.println("An exception was thrown ... " + e.toString());
        }
        return isValid;
    }
    
    private static boolean validateChars (String characterString) {
       boolean isInvalidChars = false;
        try {
                String regexp = ("\\<|\\>|&lt;|&gt;");//("\\<|\\>")
                Pattern pattern = Pattern.compile(regexp);
                Matcher matcher          = pattern.matcher(characterString);
                isInvalidChars           = matcher.find();
                if (isInvalidChars) {
                    String matched = characterString.substring(matcher.start(), matcher.end());
                    if (matched.equals("<") || matched.equals("&lt;"))
                        matched = "&#60";
                    else
                        matched = "&#62;";
                    msg = "Found an html character(s) starting with:  " + matched + ".  The match was found at position " + (matcher.start()+1) + ".  ";
                    msg += "This field is only eligible for text entry, not html.";
                }
        } catch (Exception e) {
            throw new FacesException(e);
        } finally {
          return isInvalidChars;
        }
    }

}
