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
package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.LockssServer;
import edu.harvard.iq.dvn.core.vdc.LockssConfig;
import edu.harvard.iq.dvn.core.vdc.LockssConfig.ServerAccess;
import edu.harvard.iq.dvn.core.util.DomainMatchUtil;


import java.util.*;
import java.util.logging.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author landreev
 */
@Stateless
public class LockssAuthServiceBean implements LockssAuthServiceLocal {
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;


    private static Logger dbgLog = Logger.getLogger(LockssAuthServiceBean.class.getPackage().getName());
    
    /* constructor:  */
    public LockssAuthServiceBean() {
    }

 
    /*
     * isAuthorizedLockssServer method is used to check if the remote LOCKSS
     * server is authorized to crawl this set/dv at all; meaning, at this
     * point we are not authorizing them to download any particular files,
     * just deciding whether to show them the Manifest page.
     */

    public Boolean isAuthorizedLockssServer ( VDC vdc,
                                                HttpServletRequest req ) {

        String remoteAddress = req.getRemoteHost();

        if (remoteAddress == null || remoteAddress.equals("")) {
            return false;
        }

        LockssConfig lockssConfig = null;

        if (vdc != null) {
            lockssConfig = vdc.getLockssConfig();
        } else {
            lockssConfig = vdcNetworkService.getLockssConfig();
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
                  if (DomainMatchUtil.isDomainMatch(remoteAddress, elem.getIpAddress())) {
                      return true;
                  }
            }
        }
        return false;
    }

        public Boolean isAuthorizedRestrictedFiles ( VDC vdc,
                                                HttpServletRequest req ) {

        String remoteAddress = req.getRemoteHost();

        if (remoteAddress == null || remoteAddress.equals("")) {
            return false;
        }

        LockssConfig lockssConfig = null;

        if (vdc != null) {
            lockssConfig = vdc.getLockssConfig();
        } else {
            lockssConfig = vdcNetworkService.getLockssConfig();
        }

        if (lockssConfig == null) {
            return false;
        }

        List<LockssServer> lockssServers = lockssConfig.getLockssServers();

        if (lockssServers == null || lockssServers.size() == 0) {
            return false;
        }

        // Are restricted downloads allowed at all?

        if (!lockssConfig.isAllowRestricted()) {
            return false;
        }

        // And if yes, is this server on the list? 

        for (Iterator<LockssServer> it = lockssServers.iterator(); it.hasNext();) {
            LockssServer elem =  it.next();
            if (elem.getIpAddress() != null) {
                  if (DomainMatchUtil.isDomainMatch(remoteAddress, elem.getIpAddress())) {
                      return true;
                  }
            }
        }
        return false;
    }


    /*
     * isAuthorizedLockssDownload performs authorization on individual files
     * that the crawler is trying to download. This authorization depends on
     * how this configuration is configured, whether it's open to all lockss
     * servers or an IP group, and whether the file in question is restricted. 
     */

    public Boolean isAuthorizedLockssDownload ( VDC vdc,
                                                HttpServletRequest req,
                                                Boolean fileIsRestricted) {

        String remoteAddress = req.getRemoteHost();

        if (remoteAddress == null || remoteAddress.equals("")) {
            return false;
        }

        LockssConfig lockssConfig = null;

        if (vdc != null) {
            lockssConfig = vdc.getLockssConfig();

            if (lockssConfig == null) {
                lockssConfig = vdcNetworkService.getLockssConfig();
            } 
        }

        if (lockssConfig == null) {
            return false;
        }

        // If this LOCKSS configuration is open to ALL, we allow downloads
        // of public files; so if the file is public, we can return true.
        // We *may* also allow access to restricted ones; but only to
        // select servers (we'll check for that further below).

        if (ServerAccess.ALL.equals(lockssConfig.getserverAccess())) {
            if (!fileIsRestricted) {
                return true;
            }
        }

        // This is a LOCKSS configuration open only to group of servers. 
        // 
        // Before we go through the list of the authorized IP addresses
        // and see if the remote address matches, let's first check if 
        // the file is restricted and if so, whether the LOCKSS config
        // allows downloads of restricted files; because if not, we can
        // return false right away:
        
        if (fileIsRestricted) {
            if (!lockssConfig.isAllowRestricted()) {
                return false;
            }
        }

        // Now let's get the list of the authorized servers:

        List<LockssServer> lockssServers = lockssConfig.getLockssServers();

        if (lockssServers == null || lockssServers.size() == 0) {
            return false;
        }

        for (Iterator<LockssServer> it = lockssServers.iterator(); it.hasNext();) {
            LockssServer elem =  it.next();
            if (elem.getIpAddress() != null) {
                if (DomainMatchUtil.isDomainMatch(remoteAddress, elem.getIpAddress())) {
                    return true;
                }
            }
        }

        // We've exhausted the possibilities, returning false:

        return false;
    }

}
