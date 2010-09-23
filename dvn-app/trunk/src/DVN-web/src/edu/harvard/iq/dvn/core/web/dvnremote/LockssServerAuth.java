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
