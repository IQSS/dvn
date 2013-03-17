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

/**
 * DateWithFormatter is a convenience class used to return an object that's
 * usable within a file reader that will keep the string data paired off with
 * its format. This is helpful when computing UNF's via:
 * UNF5Calcultator.calculate(stringEntries, dateFormats);
 * In particular, it nicely formats both the strings and dateFormats in the end.
 * Note that date information is important considering TIMEZONE is a legitimate
 * component of a date object, and the UNF calculator dictates that it must
 * convert all times to UTC for the UNF.
 */

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.DateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 *
 * @author Matt Owen
 */
public class DateWithFormatter {
  Date mDate;
  SimpleDateFormat mFormat;
  String mPattern;
  
  public DateWithFormatter (Date date, SimpleDateFormat format) {
    mDate = date;
    mFormat = format;
  }
  
  /**
   * Get Date
   * @return Date
   */
  public Date getDate () {
    return mDate;
  }
  /**
   * Get Formatter
   * @return 
   */
  public SimpleDateFormat getFormatter () {
    return mFormat;
  }
  /**
   * Get the Date as a String in the Supposed Format
   * @return 
   */
  public String getAsString () {
    return mFormat.format(mDate);
  }
}
