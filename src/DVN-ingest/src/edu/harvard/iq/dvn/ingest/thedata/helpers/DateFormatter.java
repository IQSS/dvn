/*
   Copyright (C) 2005-2013, by the President and Fellows of Harvard College.

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
package edu.harvard.iq.dvn.ingest.thedata.helpers;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.DateFormat;
import java.util.*;
import java.util.logging.Logger;
import edu.harvard.iq.dvn.ingest.thedata.helpers.DateWithFormatter;

/**
 * Date Formatter for Generalizing Issues with Date-Time
 * @author Matt Owen
 */
public class DateFormatter {
  
  private static final Logger LOG = Logger.getLogger(DateFormatter.class.getPackage().getName());
  
  private List <SimpleDateFormat> mDateFormats = new ArrayList <SimpleDateFormat>();
  private List <SimpleDateFormat> mTimeFormats = new ArrayList <SimpleDateFormat>();
  
  /**
   * Construct a Date Formatter Object
   */
  public DateFormatter () {
    
  }
  /**
   * 
   * @param formats
   * @return 
   */
  public DateFormatter setDateFormats (String[] formats) {
    for (String format : formats) {
      mDateFormats.add(new SimpleDateFormat(format));
    }
    
    return this;
  }
  /**
   * 
   * @param formats
   * @return 
   */
  public DateFormatter setDateFormats (SimpleDateFormat [] formats) {
    mDateFormats = Arrays.asList(formats);
    return this;
  }
  /**
   * 
   * @param formats
   * @return 
   */
  public DateFormatter setTimeFormats (String[] formats) {
    
    for (String format : formats) {
      mTimeFormats.add(new SimpleDateFormat(format));
    }
    
    return this;
  }
  /**
   * 
   * @param formats
   * @return 
   */
  public DateFormatter setTimeFormats (SimpleDateFormat [] formats) {
    mTimeFormats = Arrays.asList(formats);
    return this;
  }
  /**
   * Checks whether a string is a date
   * @param value
   * @return 
   */
  public boolean isDate (String value) {
    return matchesDateTimeInList(value, mDateFormats);
  }
  /**
   * Checks whether a string is a time
   * @param value
   * @return 
   */
  public boolean isTime (String value) {
    return matchesDateTimeInList(value, mTimeFormats);
  }
  /**
   * 
   * @param needle
   * @param haystack
   * @return 
   */
  private static boolean matchesDateTimeInList (String needle, List <SimpleDateFormat> haystack) {
    // Iterate through each Date format. If one works, then return true.
    for (SimpleDateFormat format : haystack) {
      try {
        // Attempt to parse the string as a date
        format.parse(needle);
        // If successful, then we return true
        return true;
      }
      catch (ParseException ex) {
        // Do nothing
      }
    }
    // If we went through the entire list, and nothing worked, then we return false
    return false;
  }
  /**
   * 
   * @param value
   * @return 
   */
  public DateWithFormatter getDateWithFormat (String value) {
    Date dateResult;
    String stringResult;
    DateFormat utcFormatter = DateFormat.getDateTimeInstance(
            DateFormat.DEFAULT,
            DateFormat.DEFAULT,
            Locale.getDefault()
            );
    
    SimpleDateFormat utcSimpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat utcSimpleTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    // Format strings as UTC
    utcFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    
    int k = 0;
    
    // Iterate through all available date-time formats
    for (SimpleDateFormat format : mTimeFormats) {
      try {
        LOG.info("format[" + k + "] = " + format.toPattern());
        LOG.info("value[" + k + "] = " + value);
        
        k++;
        
        // Parse the date from the format
        dateResult = format.parse(value);
        // Format in 
        stringResult = utcSimpleTimeFormatter.format(dateResult);
        LOG.info("RESULT (time) = " + stringResult);
        // If parse and format were successful, return the formatted result
        //return stringResult;
        return new DateWithFormatter(dateResult, format);
      }
      catch (ParseException ex) {
        // Do nothing
      }
    }
    // Iterate through all available date formats
    for (SimpleDateFormat format : mDateFormats) {
      try {
        // Strict parsing
        format.setLenient(false);
        // Parse the date from the format
        dateResult = format.parse(value);
        // String result
        stringResult = utcSimpleDateFormatter.format(dateResult);
        
        LOG.info("RESULT (date) = " + stringResult);
        // If parse and format were successful, return the formatted result
        return new DateWithFormatter(dateResult, format);
      }
      catch (ParseException ex) {
        // Do nothing
      }
    }
    // On failure, return NULL
    return null;
  }
  /**
   * Whether a String Matches a DateTime Format
   * @param value
   * @return 
   */
  public String formattedDate (String value) {
    return "";
  }
}
