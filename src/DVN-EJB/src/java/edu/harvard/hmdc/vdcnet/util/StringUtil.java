/*
 * StringUtil.java
 *
 * Created on October 4, 2006, 11:07 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Ellen Kraffmiller
 */
public final class StringUtil {
    
   
    public static final boolean isEmpty(String str) {
        if (str==null || str.trim().equals("")) {
            return true;
        } else {
            return false;
        }        
    }

    public static String truncateString(String originalString, int maxLength) {
        maxLength = Math.max( 0, maxLength);
        String finalString = originalString;
        if (finalString != null && finalString.length() > maxLength ) {
             try {
                String regexp = "[A-Za-z0-9][\\p{Punct}][\\p{Space}]";
                Pattern pattern = Pattern.compile(regexp);
                String startParsedString = finalString.substring(0, maxLength);
                String endParsedString   = finalString.substring(maxLength, finalString.length());
                Matcher matcher          = pattern.matcher(endParsedString);
                boolean found            = matcher.find();
                endParsedString          = endParsedString.substring(0, matcher.end());
                finalString              = "<div>" + startParsedString + endParsedString + "</div>";

             } catch (Exception e) {
                System.out.println("An issue occurred truncating the following String: " + originalString);
            }
        } 
        
        return finalString;             
    }    
    
}
