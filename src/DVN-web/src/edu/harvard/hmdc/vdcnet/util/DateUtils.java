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

package edu.harvard.hmdc.vdcnet.util;

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
    
    
}
