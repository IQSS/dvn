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
 * DateValidator.java
 *
 * Created on February 23, 2007, 3:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;

/**
 *
 * @author ekraffmiller
 */
public class DateValidator implements Validator, java.io.Serializable  {
    
    /** Creates a new instance of DateValidator */
    public DateValidator() {
    }
    
    public void validate(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String dateString = ((String) value).trim();
        boolean valid = validate(dateString);
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage("Invalid Date Format.  Valid formats are YYYY-MM-DD, YYYY-MM, or YYYY. Optionally, 'BC' can be appended to the year. (By default, AD is assumed.)");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
    
    private boolean validate(String dateString) {
        boolean valid=false;
        
        valid = isValid(dateString,"yyyy-MM-dd");
        if (!valid ) {
            valid = isValid(dateString, "yyyy-MM");
        }     
        if (!valid) {
            valid = isValid(dateString,"yyyy");
        }
        if (!valid ) {
            valid = isValid(dateString, "yyyyyyyyy-MM-dd GG");
        }   
        if (!valid ) {
            valid = isValid(dateString, "yyyyyyyyy-MM GG");
        }     
        if (!valid) {
            valid = isValid(dateString,"yyyyyyyyy GG");
        }      
        return valid;
    }
    
    private boolean isValid(String dateString, String pattern) {
        boolean valid=true;
        Date date;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        try {
            dateString = dateString.trim();
            date = sdf.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int era = calendar.get(Calendar.ERA);
            if (era == GregorianCalendar.AD ) {
                if ( year > 9999) {
                    valid=false;
                }

            }
         //   System.out.println("pattern is "+ pattern);
         //   System.out.println("Year is "+year);
         //   System.out.println("Calendar date is "+date.toString());
         //   System.out.println("Era is "+calendar.get(Calendar.ERA));
        }catch (ParseException e) {
            valid=false;
        }
        if (dateString.length()>pattern.length()) {
            valid=false;
        }
        return valid;
    }
    public static void main(String args[]) {
        DateValidator validator = new DateValidator();     
        System.out.println("Result is "+validator.validate("-20080-02 "));
        
    }
}

