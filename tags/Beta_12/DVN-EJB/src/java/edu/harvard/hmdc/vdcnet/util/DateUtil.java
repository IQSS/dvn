/*
 * DateUtil.java
 *
 * Created on February 23, 2007, 4:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author gdurand
 */
public class DateUtil {
    
    /** Creates a new instance of DateUtil */
    public DateUtil() {
    }

    public static boolean validateDate(String dateString, String pattern) {
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
    
    public static boolean validateDate(String dateString) {
        boolean valid=false;
        
        if (dateString == null) {
            return false;
        }
        
        String monthDayYear = "yyyy-MM-dd";
        String monthYear = "yyyy-MM";
        String year = "yyyy";
        
        if (dateString.length()==4) {
            valid = validateDate(dateString,"yyyy");
        } else if (dateString.length()>4 && dateString.length()<=7) {
            valid = validateDate(dateString, "yyyy-MM");
        } else if (dateString.length()>7) {
            valid = validateDate(dateString,"yyyy-MM-dd");
        }
        
        return valid;       
    }    
    
}
