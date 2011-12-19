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
