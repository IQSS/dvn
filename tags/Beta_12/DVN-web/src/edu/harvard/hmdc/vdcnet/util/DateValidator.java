/*
 * DateValidator.java
 *
 * Created on February 23, 2007, 3:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;

/**
 *
 * @author gdurand
 */
public class DateValidator implements Validator {
    
    /** Creates a new instance of DateValidator */
    public DateValidator() {
    }
    
    public void validate(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String dateString = (String) value;
        boolean valid=false;
        
        String monthDayYear = "yyyy-MM-dd";
        String monthYear = "yyyy-MM";
        String year = "yyyy";
        
        if (dateString.length()==4) {
            valid = isValid(dateString,"yyyy");
        } else if (dateString.length()>4 && dateString.length()<=7) {
            valid = isValid(dateString, "yyyy-MM");
        } else if (dateString.length()>7) {
            valid = isValid(dateString,"yyyy-MM-dd");
        }
        
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage("Invalid Date Format.  Valid formats are YYYY-MM-DD, YYYY-MM, or YYYY.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
    
    

    private boolean isValid(String dateString, String pattern) {
        boolean valid=false;
        Date date;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        try {
            date = sdf.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            if (year>999) {
                valid=true;
            }
        }catch (ParseException e) {
            valid=false;
        }
        return valid;
    }
}

