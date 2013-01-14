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
 * VersionPage.java
 * 
 * Created on Sep 20, 2007, 12:12:12 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web;

import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author roberttreacy
 */
@Named("VersionPage")
@ApplicationScoped
public class VersionPage extends VDCBaseBean  implements java.io.Serializable {
   
    private String versionNumber;
    public String getVersionNumber() {
        if (versionNumber == null) {
            versionNumber = ResourceBundle.getBundle("VersionNumber").getString("version.number");
        }
        return versionNumber;

    }
    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }
    public String getBambooBuild() {
        String buildString = null;
        String buildStr = ResourceBundle.getBundle("BuildNumber").getString("build.number");
        return buildStr != null? buildStr : "00";
    }

    public String getServerName() {
        try {
            return InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

}
