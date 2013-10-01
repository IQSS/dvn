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
 * DateUtil.java
 *
 * Created on February 23, 2007, 4:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author gdurand
 */
public class DateUtil implements java.io.Serializable  {
    
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
    
    public static Timestamp getTimestamp() {
        Timestamp timeStamp   = null;
        Date date = new Date();
        timeStamp = new Timestamp(date.getTime());
        return timeStamp;
    }
    
    
}
