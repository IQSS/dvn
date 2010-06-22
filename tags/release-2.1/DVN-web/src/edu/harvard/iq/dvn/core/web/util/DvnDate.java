/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.util;

import edu.harvard.iq.dvn.core.util.StringUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Ellen Kraffmiller
 */
public class DvnDate {
    String year;
    String month;
    String day;
    String era;
    String errMessage;
    
    DvnDate( String formattedDate ) {
        setDateParts(formattedDate);
                
        
    }
    private boolean validate() {
      
        errMessage=null;
        
        // If day is set, then month and year must be set
        if (!StringUtil.isEmpty(day)) {
            if (StringUtil.isEmpty(month) || StringUtil.isEmpty(year)) {
                errMessage="If day is entered, then month and year must be entered.";
                return false;
            }
        }
        // If month is set, then year must be set
        if (!StringUtil.isEmpty(month)) {
            if ( StringUtil.isEmpty(year)) {
                errMessage="If month is entered, then year must be entered.";
                return false;
            }
        }
      //  if (convertStringToDate(getDateString())==null) {
      //      errMessage="Invalid date.";
      //  }
        return true;
    }
    
    private void setDateParts(String formattedDate) {
        Date date = convertStringToDate(formattedDate);
        if (date!=null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            year = new Integer(calendar.get(Calendar.YEAR)).toString();
            month = new Integer(calendar.get(Calendar.MONTH)+1).toString();
            day = new Integer(calendar.get(Calendar.DAY_OF_MONTH)).toString();
            int eraInt = calendar.get(Calendar.ERA);
            if (eraInt==0) {
                era="BC";
            }       
        }
        else {
            // we couldn't separate date into it's component parts, 
            // so put it all in year.
            year= formattedDate;
        }
    }
    
    public static Date convertStringToDate(String dateString) {
          Date date = convertFromPattern(dateString,"yyyy-MM-dd");
        if (date==null ) {
            date = convertFromPattern(dateString, "yyyy-MM");
        }
         if (date==null) {
            date = convertFromPattern(dateString,"yyyy GG");
        }
        if (date==null) {
            date = convertFromPattern(dateString,"yyyyGG");
        }
        if (date==null) {
            date = convertFromPattern(dateString,"yyyy");
        }
        return date;
    }
    public static Date convertFromPattern(String dateString, String pattern) {
       
        Date date=null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        
        
        try {
            dateString = dateString.trim();
            date = sdf.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
          //  System.out.println("pattern is "+ pattern);
          //  System.out.println("Year is "+year);
          //  System.out.println("Calendar date is "+date.toString());
          //  System.out.println("Era is "+calendar.get(Calendar.ERA));
        } catch(ParseException e) {
            date=null;
        }
        if (dateString.length()>pattern.length()) {
          date=null;
        }
        return date;
    }
     
     public String toString() {
         String str = new String();
         str = "Year: "+year+"; Month: "+month+", Day: "+day+"; Era: "+era;
         return str;
     }
     
     public static void main(String args[]) {
         String testString ="2008 BC";
         System.out.println("Testing: "+testString);
         DvnDate dvnDate = new DvnDate(testString);
         System.out.println(dvnDate);
     }

}
