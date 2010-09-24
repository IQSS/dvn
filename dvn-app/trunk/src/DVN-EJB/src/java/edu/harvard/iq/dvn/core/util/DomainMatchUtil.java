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
 * LockssServerAuth.java
 *
 * Created on Mar 23, 2007, 3:13 PM
 *
 */

package edu.harvard.iq.dvn.core.util;

import java.util.logging.*;
import java.net.InetAddress;


/**
 *
 * @author landreev
 */

public class DomainMatchUtil implements java.io.Serializable {
    private static Logger dbgLog = Logger.getLogger(DomainMatchUtil.class.getPackage().getName());
    
    /* constructor:  */
    public DomainMatchUtil() {
    }

 
    public static Boolean isDomainMatch (String remoteAddress, String domainName) {
        // We assume that the string representation of the remote address can be
        // in both the numeric and domain form;
        // Same for the string defining the domain. It can be in either of the
        // 2 forms above; plus we support wild cards in domain definitions:
        // as in, both 140.247.116.* and *.hmdc.harvard.edu

        // if they didn't supply any IP address, bad for them:

        if (remoteAddress == null) {
            return false;
        }

        // let's check if the remoteAddress supplied is in the numeric or
        // domain name form:

        String remoteAddressNumeric = null;
        String remoteAddressDomainName = null;



        if (remoteAddress.matches("^[0-9][0-9]*\\.[0-9][0-9]*\\.[0-9][0-9]*\\.[0-9][0-9]*$")) {
            // numeric:
            remoteAddressNumeric = remoteAddress;
            try {
                byte[] rawAddress = InetAddress.getByName(remoteAddress).getAddress();
                remoteAddressDomainName = InetAddress.getByAddress(rawAddress).getHostName();
            } catch (Exception ex) {
                // not fatal;
                // we haven't been able to look up the domain name; but it's ok,
                // we'll just have to work with the numeric address.
            }

        } else if (remoteAddress.matches("^$")) {
            // domain name:
            remoteAddressDomainName = remoteAddress;
            try {
                remoteAddressNumeric = InetAddress.getByName(remoteAddress).getHostAddress();
            } catch (Exception ex) {
            }
        } else {
            // this actually means that this is not a valid IP address.
            return false;
        }

        if (remoteAddressNumeric != null && isDomainMatchNumeric (remoteAddressNumeric, domainName)) {
            return true;
        }

        if (remoteAddressDomainName != null && isDomainMatchByName (remoteAddressDomainName, domainName)) {
            return true;
        }

        // no match found.

        return false;
    }

    public static Boolean isDomainMatchNumeric (String remoteAddressNumeric, String domainName) {

        if (domainName == null) {
            return false;
        }

        // exact match:

        if (domainName.equals(remoteAddressNumeric)) {
            return true;
        }

         // 140.247.116.* -> 140.247.116.220 match:

        int wildCartIndex = domainName.indexOf(".*",0);

        if ( wildCartIndex != -1 ) {
            String pattern = domainName.replace(".*", "\\.");

            if (pattern.equals(remoteAddressNumeric.substring(0,wildCartIndex+1))) {
                return true;
            }
        }
        return false;
    }

    public static Boolean isDomainMatchByName (String remoteAddressDomainName, String domainName) {

     if (domainName == null) {
            return false;
        }

        // exact match:

        if (domainName.equals(remoteAddressDomainName)) {
            return true;
        }

        // *.hmdc.harvard.edu -> porkchop.hmdc.harvard.edu match:

        if (domainName.substring(0,1).equals("*")) {
            if (remoteAddressDomainName.indexOf(domainName.substring(1)) != -1) {
                return true;
            }
        }

        return false;
    }
}
