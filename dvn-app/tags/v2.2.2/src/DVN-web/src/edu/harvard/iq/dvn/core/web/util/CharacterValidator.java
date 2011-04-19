/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
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
    
    private static boolean validateChars (String characterString) {
       boolean isInvalidChars = false;
        try {
                String regexp = "['\\@\\#\\$%\\^&\\*\\(\\)_\\+\\:\\<\\>\\/\\[\\]\\\\{\\}\\|\\p{Punct}\\p{Space}]";
                Pattern pattern = Pattern.compile(regexp);
                Matcher matcher          = pattern.matcher(characterString);
                isInvalidChars             = matcher.find();
                if (isInvalidChars)
                    msg = "Found an illegal character(s) starting with:  " + characterString.charAt(matcher.start()) + "The match was found at position " + (matcher.start()+1) + ".";
        } catch (Exception e) {
            throw new FacesException(e);
        } finally {
          return isInvalidChars;
        }
    }
}
