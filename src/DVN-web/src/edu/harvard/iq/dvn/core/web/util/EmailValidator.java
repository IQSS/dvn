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
 * EmailValidator.java
 *
 * Created on February 2, 2007, 5:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.util;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class EmailValidator implements Validator, java.io.Serializable  {
    
    private String msg = new String("Please enter a valid email.");
    
    Logger logger = Logger.getLogger("validationLogger");
    
        /** Creates a new instance of EmailValidator */
    public EmailValidator() {
    }
    
    public void validate(FacesContext context, UIComponent component, 
                        Object value) {
        if (value == null) return;
        String email        = (String)value;
        if (!validateEmail(email) ) {
            logger.info("Validation Error:  An invalid email was found.");
            FacesMessage message = new FacesMessage(msg);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            ((UIInput)component).setValid(false);
            context.addMessage(component.getClientId(context), message);
            throw new ValidatorException(message);
        }
    }
    
    

    public boolean validateEmail (String email) {
        boolean isValid = true;
        email = email.replace(" ", "");
        String[] input = email.split(",");
        int i = 0;
        while (i < input.length) {
          if (input[i].indexOf('@') == -1) {
              isValid = false;
              msg += "  A valid email address must contain \"@\".";
              return isValid;
          } 
          if (input[i].indexOf('@') > 1) {
              int firstAt = input[i].indexOf('@');
              System.out.print("firstAt "+ firstAt);
              System.out.print("input[i].indexOf(firstAt, '@') "+ input[i].indexOf( '@', firstAt +1));
              if (input[i].indexOf( '@', firstAt + 1) != -1) {
                  isValid = false;
                  msg += "  A valid email address must contain only one \"@\".";
                  return isValid;
              }
          }
          //Checks for email addresses starting with
          //inappropriate symbols like dots or @ signs.
          Pattern p = Pattern.compile("^\\.|^\\@");
          Matcher m = p.matcher(input[i]);
          if (m.find()) {
              isValid = false;
              msg += "  An email address cannot start" +
                                " with \".\" or \"@\".";
              return isValid;
          }
          //check to see if there is something after the @
          p = Pattern.compile("\\@$");
          m = p.matcher(input[i]);
          if (m.find()) {
              isValid = false;
              msg += " An email address cannot end with @.";
              return isValid;
          }

          //check to see if the domain part of the email address matches the rfc for email addresses
          p = Pattern.compile("\\@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+");
          m = p.matcher(input[i]);
          if (!m.find()) {
              isValid = false;
              msg += "Please check the email address domain. It appears to be incorrect.";
              return isValid;
          }
          //Checks for email addresses that start with
          //www. and prints a message if it does.
          p = Pattern.compile("^www\\.");
          m = p.matcher(input[i]);
          if (m.find()) {
              isValid = false;
              msg += "  An email address cannot start with " +
                    " with \"www.\".";
              return isValid;
          }
          //now check for invalid chars in the local part of the email address
          p = Pattern.compile("[^A-Za-z0-9\\.\\@_\\-~#]+");
          m = p.matcher(input[i]);
          StringBuffer sb = new StringBuffer();
          boolean result = m.find();
          boolean deletedIllegalChars = false;

          while(result) {
             deletedIllegalChars = true;
             m.appendReplacement(sb, "");
             result = m.find();
          }
          // Add the last segment of input to the new String
          m.appendTail(sb);
          String appendInput = sb.toString();

          if (deletedIllegalChars) {
              isValid = false;
              msg += "  Invalid characters were found" +
                               ".  Check for invalid characters or spaces at the end of the email address.";
          }
          i++;
        }
      return isValid;
    }
}
