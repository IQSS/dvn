/*
 * DateUtils.java
 *
 * Created on Aug 1, 2007, 11:41:58 AM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;

/**
 *
 * @author wbossons
 */
public class DateUtils {

    public DateUtils() {
    }

    public static void main(String[] args) {
    }
    
    private static String timestamp = null;

    public static String getTimeStamp() {
        Date date = new Date();
        timestamp = DateFormat.getDateTimeInstance().format(date);
        return timestamp;
    }
    
    
}
