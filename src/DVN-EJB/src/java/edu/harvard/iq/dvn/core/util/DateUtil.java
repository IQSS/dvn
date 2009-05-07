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
