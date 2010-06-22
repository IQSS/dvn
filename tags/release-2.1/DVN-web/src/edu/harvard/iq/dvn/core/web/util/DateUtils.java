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
 * DateUtils.java
 *
 * Created on Aug 1, 2007, 11:41:58 AM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.util;

import java.sql.Timestamp;
import java.util.Date;
import java.text.DateFormat;

/**
 *
 * @author wbossons
 */
public class DateUtils implements java.io.Serializable {

    public DateUtils() {
    }

    public static void main(String[] args) {
    }
    
    private static String timestamp = null;

    public static String getTimeStampString() {
        Date date = new Date();
        timestamp = DateFormat.getDateTimeInstance().format(date);
        return timestamp;
    }
    
    public static Timestamp getTimestamp() {
        Timestamp timeStamp   = null;
        Date date = new Date();
        timeStamp = new Timestamp(date.getTime());
        return timeStamp;
    }
    
    public static String getTimeInterval(Long startMilliseconds) {
        Long currentMilliseconds = System.currentTimeMillis();
        Long interval            = (currentMilliseconds - startMilliseconds);
        //System.out.println("currentMillis are " + currentMilliseconds + " and startMillis are " + startMilliseconds + " Time elapsed in minutes is : " + interval.toString() + " minutes");   
        Long uBoundMinutes = new Long(60 * 60);
        Long uBoundHours   = new Long(uBoundMinutes * 24);
        Long uBoundDays    = new Long(uBoundHours * 7);
        Long uBoundWeeks   = new Long(uBoundDays * 4);
        Long uBoundMonths  = new Long(uBoundWeeks * 12);
        interval           = interval/1000;
        if (interval > 0 && interval < 60)
            return interval + ((interval > 1) ? " seconds ago" : " second ago");
        else if (interval > 60 && interval < uBoundMinutes)
            return interval/(60) + ((interval > 1) ? " minutes ago" : " minute ago");
        else if (interval >= uBoundMinutes && interval < uBoundHours)
            return interval/uBoundMinutes + ((interval/uBoundMinutes > 1) ? " hours ago" : " hour ago");
        else if (interval >= uBoundHours && interval < uBoundDays)
            return interval/uBoundHours + ((interval/uBoundHours > 1) ? " days ago" : " day ago");
        else if (interval >= uBoundDays && interval < uBoundWeeks)
            return interval/uBoundDays + ((interval/uBoundDays > 1) ? " weeks ago" : " week ago");
        else if (interval >= uBoundWeeks && interval < uBoundMonths)
            return interval/uBoundWeeks + ((interval/uBoundWeeks > 1) ? " months ago" : " month ago");
        else if (interval >= uBoundMonths)
            return "More than a year ago";
        else 
            return "--";
        }

    public static String getLoadTime(Long startMilliseconds) {
        Long currentMilliseconds = System.currentTimeMillis();
        Long interval            = (currentMilliseconds - startMilliseconds);
        //System.out.println("currentMillis are " + currentMilliseconds + " and startMillis are " + startMilliseconds + " Time elapsed in minutes is : " + interval.toString() + " minutes");
        Long uBoundMinutes = new Long(60 * 60);
        Long uBoundHours   = new Long(uBoundMinutes * 24);
        Long uBoundDays    = new Long(uBoundHours * 7);
        Long uBoundWeeks   = new Long(uBoundDays * 4);
        Long uBoundMonths  = new Long(uBoundWeeks * 12);
        interval           = interval/1000;
        if (interval < 0)
            return "under a second";
        else if (interval > 0 && interval < 60)
            return interval + ((interval > 1) ? " seconds" : " second");
        else if (interval > 60 && interval < uBoundMinutes)
            return interval/(60) + ((interval > 1) ? " minutes" : " minute");
        else if (interval >= uBoundMinutes && interval < uBoundHours)
            return interval/uBoundMinutes + ((interval/uBoundMinutes > 1) ? " hours" : " hour");
        else if (interval >= uBoundHours && interval < uBoundDays)
            return interval/uBoundHours + ((interval/uBoundHours > 1) ? " days" : " day");
        else if (interval >= uBoundDays && interval < uBoundWeeks)
            return interval/uBoundDays + ((interval/uBoundDays > 1) ? " weeks" : " week");
        else if (interval >= uBoundWeeks && interval < uBoundMonths)
            return interval/uBoundWeeks + ((interval/uBoundWeeks > 1) ? " months" : " month");
        else if (interval >= uBoundMonths)
            return "More than a year";
        else
            return "under a second";
        }
}
