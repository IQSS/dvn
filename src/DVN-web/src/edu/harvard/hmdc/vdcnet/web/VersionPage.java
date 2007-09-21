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
import javax.ejb.EJB;

/**
 *
 * @author roberttreacy
 */
public class VersionPage extends VDCBaseBean {
    @EJB DVNVersionServiceLocal dvnVersionS dvnVersionService;
    
    public void init() {
        super.init();
        DVNVersion version = getLatestVersion();
        this.versionNumber = version.getVersionNumber();
        this.buildNumber = version.getBuildNumber();
    }
    
    public Long versionNumber;
    public Long getVersionNumber(){
        return versionNumber;
    }
    
    public Long buildNumber;
    public Long getBuildNumber(){
        return buildNumber;
    }
    
    public DVNVersion getLatestVersion(){
        return dvnVersionService.getLatestVersion();
    }

}
