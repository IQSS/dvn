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
 * StringUtil.java
 *
 * Created on October 4, 2006, 11:07 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Ellen Kraffmiller
 */
public final class StringUtil implements java.io.Serializable  {
    
   
    public static final boolean isEmpty(String str) {
        if (str==null || str.trim().equals("")) {
            return true;
        } else {
            return false;
        }        
    }

    public static final boolean isAlphaNumeric(String str) {
      final char[] chars = str.toCharArray();
      for (int x = 0; x < chars.length; x++) {      
        final char c = chars[x];
        if(isAlphaNumericChar(c)) {
            continue;
        }
        return false;
      }  
      return true;
}
    public static final boolean isAlphaNumericChar(char c) {
        if ((c >= 'a') && (c <= 'z')) return true; // lowercase
        if ((c >= 'A') && (c <= 'Z')) return true; // uppercase
        if ((c >= '0') && (c <= '9')) return true; // numeric
        return false;
        
      
}

    public static String truncateString(String originalString, int maxLength) {
        maxLength = Math.max( 0, maxLength);
        String finalString = originalString;
        if (finalString != null && finalString.length() > maxLength) {
            String regexp = "[A-Za-z0-9][\\p{Space}]";
            Pattern pattern = Pattern.compile(regexp);
            String startParsedString = finalString.substring(0, maxLength);
            String endParsedString = finalString.substring(maxLength, finalString.length());
            Matcher matcher = pattern.matcher(endParsedString);
            boolean found = matcher.find();
            if (found) {
                endParsedString = endParsedString.substring(0, matcher.end());
                finalString = startParsedString + endParsedString + "<span class='dvn_threedots'>...</span>";
            }
        }
        
        return finalString;             
    }    
    
}
