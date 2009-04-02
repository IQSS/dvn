/*
 * VersionPage.java
 * 
 * Created on Sep 20, 2007, 12:12:12 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web;

import edu.harvard.hmdc.vdcnet.admin.DVNVersion;
import edu.harvard.hmdc.vdcnet.admin.DVNVersionServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import javax.ejb.EJB;

/**
 *
 * @author roberttreacy
 */
public class VersionPage extends VDCBaseBean  implements java.io.Serializable {
    @EJB DVNVersionServiceLocal dvnVersionService;
    
    public void init() {
        super.init();
        DVNVersion version = getLatestVersion();
        this.versionNumber = version.getVersionNumber();
        this.buildNumber = version.getBuildNumber();
    }
    
    public Long versionNumber;

    public Long getVersionNumber() {
        DVNVersion version = getLatestVersion();
        return version.getVersionNumber();
    }
    
    public Long buildNumber;
    public Long getBuildNumber(){
        DVNVersion version = getLatestVersion();
        return version.getBuildNumber();
    }
    
    public DVNVersion getLatestVersion(){
        return dvnVersionService.getLatestVersion();
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
