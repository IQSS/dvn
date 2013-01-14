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
                remoteAddressDomainName = InetAddress.getByAddress(rawAddress).getCanonicalHostName();
            } catch (Exception ex) {
                // not fatal;
                // we haven't been able to look up the domain name; but it's ok,
                // we'll just have to work with the numeric address.

                // but let's make sure it hasn't been set:
                remoteAddressDomainName = null;
            }
            // A bit of an unexpected behavior here:
            // If you try to do the above with an IP address that does not
            // resolve to a DNS name (for example, "1.1.1.1"), instead of
            // throwing a "host not found" exception, it will simply set the
            // remoteAddressDomainName to this same numeric IP string
            // (i.e., "1.1.1.1"). So, let's make sure this isn't the case:

            if (remoteAddressDomainName != null &&
                    remoteAddressNumeric.equals(remoteAddressDomainName)) {
                remoteAddressDomainName = null;
            }


        } else if (validDomainName(remoteAddress)) {
            // domain name:
            remoteAddressDomainName = remoteAddress;
            try {
                remoteAddressNumeric = InetAddress.getByName(remoteAddress).getHostAddress();
            } catch (Exception ex) {
                remoteAddressNumeric = null;
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

        int wildCardIndex = domainName.indexOf(".*",0);

        if ( wildCardIndex != -1 ) {
            String pattern = domainName.replace(".*", ".");
 
            if (pattern.equals(remoteAddressNumeric.substring(0,wildCardIndex+1))) {
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

    /**
     * Cribbed from: http://pappul.blogspot.com/2006/07/validation-of-host-name-in-java.html
     * @param domainName
     * @return
     */
    private static boolean validDomainName(String domainName) {
        if ((domainName == null)) {
            return false;
        }

        String domainIdentifier = "((\\p{Alnum})([-]|(\\p{Alnum}))*(\\p{Alnum}))|(\\p{Alnum})";
        String domainNameRule = "(" + domainIdentifier + ")((\\.)(" + domainIdentifier + "))*";
        String oneAlpha = "(.)*((\\p{Alpha})|[-])(.)*";

        return domainName.matches(domainNameRule) && domainName.matches(oneAlpha);
    }

    public static void main(String[] args) {
        System.out.println("The 4 main lookup cases:");

        System.out.println("DomainMatchUtil.isDomainMatch(\"dvn.iq.harvard.edu\", \"*.iq.harvard.edu\") =" +DomainMatchUtil.isDomainMatch("dvn.iq.harvard.edu", "*.iq.harvard.edu"));
        System.out.println("DomainMatchUtil.isDomainMatch(\"dvn.iq.harvard.edu\", \"140.247.115.*\") =" +DomainMatchUtil.isDomainMatch("dvn.iq.harvard.edu", "140.247.115.*"));
        System.out.println("DomainMatchUtil.isDomainMatch(\"140.247.116.220\", \"140.247.116.*\")="+DomainMatchUtil.isDomainMatch("140.247.116.220", "140.247.116.*"));
        System.out.println("DomainMatchUtil.isDomainMatch(\"140.247.116.220\", \"*.hmdc.harvard.edu\")="+DomainMatchUtil.isDomainMatch("140.247.116.220", "*.hmdc.harvard.edu"));


        System.out.println("An incorrectly defined numeric domain:");

        System.out.println("DomainMatchUtil.isDomainMatch(\"140.247.116.220\", \"*.247.116.220\")="+DomainMatchUtil.isDomainMatch("140.247.116.220", "*.247.116.220"));

        System.out.println("Unresolvable IP addresses:");
        System.out.println("(i.e., these numeric IPs don't have registered DNS ");
        System.out.println("names; but they may still be valid addresses)");

        System.out.println("DomainMatchUtil.isDomainMatch(\"12.12.12.12\", \"*.12.12.12\")="+DomainMatchUtil.isDomainMatch("12.12.12.12", "*.12.12.12"));
        System.out.println("DomainMatchUtil.isDomainMatch(\"1.1.1.1\", \"1.1.1.*\")="+DomainMatchUtil.isDomainMatch("1.1.1.1", "1.1.1.*"));
        System.out.println("DomainMatchUtil.isDomainMatch(\"1.1.1.1\", \"1.*\")="+DomainMatchUtil.isDomainMatch("1.1.1.1", "1.*"));


    }
}
