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
package edu.harvard.iq.dvn.core.web.dvnremote;

import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.LockssServer;
import edu.harvard.iq.dvn.core.vdc.LockssConfig;
import edu.harvard.iq.dvn.core.vdc.LockssConfig.ServerAccess;


import java.util.*;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern; 

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author landreev
 */
public class LockssServerAuth {
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;


    private static Logger dbgLog = Logger.getLogger(LockssServerAuth.class.getPackage().getName());
    
    /* constructor:  */
    public LockssServerAuth() {
    }

 
    /* 
    */ 

    public Boolean isAuthorizedLockssServer (  VDC vdc,
                                                HttpServletRequest req) {

        String remoteAddress = req.getRemoteHost();

        if (remoteAddress == null || remoteAddress.equals("")) {
            return false;
        }

        LockssConfig lockssConfig = null;

        if (vdc != null) {
            if (vdc.getLockssConfig()!=null) {
                lockssConfig = vdc.getLockssConfig();
            } else {
                lockssConfig = vdcNetworkService.getLockssConfig();
            }
        }

        if (lockssConfig == null) {
            return false;
        }

        if (ServerAccess.ALL.equals(lockssConfig.getserverAccess())) {
            return true; 
        }

        List<LockssServer> lockssServers = lockssConfig.getLockssServers();

        if (lockssServers == null || lockssServers.size() == 0) {
            return false;
        }

        for (Iterator<LockssServer> it = lockssServers.iterator(); it.hasNext();) {
            LockssServer elem =  it.next();
            if (elem.getIpAddress() != null) {
                  if (isLockssServerMatch(remoteAddress, elem.getIpAddress())) {
                      return true;
                  }
            }
        }
        return false;
    }

    private Boolean isLockssServerMatch (String remoteAddress, String lockssServerAddress) {

        // exact match:

        if (lockssServerAddress.equals(remoteAddress)) {
            return true;
        }

        // *.bar.edu -> foo.bar.edu match:

        if (lockssServerAddress.substring(0,1).equals("*")) {
            if (remoteAddress.indexOf(lockssServerAddress.substring(1)) != -1) {
                return true;
            }
        }

        // 140.247.116.* -> 140.247.116.220 match:

        if (lockssServerAddress.indexOf(".*",0) != -1) {
            String regexp = "^" + lockssServerAddress.replace(".*", "\\.");
            Pattern pattern = Pattern.compile(regexp);
            Matcher matcher = pattern.matcher(remoteAddress);
            if (matcher.find()) {
                return true;
            }
        }

        // the above is the equivalent of what we do in GroupServiceBean;
        // we should probably assume that the remoteAddress() can return 
        // the address in both the numeric and domain name form.
        //
        //if (remoteAddress.matches("^[0-9][0-9]*\\.[0-9][0-9]*\\.[0-9][0-9]*\\.[0-9][0-9]*$")) {
        //
        //}

        return false;
    }

}
