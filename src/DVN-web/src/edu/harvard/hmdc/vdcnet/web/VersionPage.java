/*
 * VersionPage.java
 * 
 * Created on Sep 20, 2007, 12:12:12 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web;

/**
 *
 * @author roberttreacy
 */
public class VersionPage {
    
    public Long versionNumber;
    public Long getVersionNumber(){
        return 1L;
    }
    
    public Long buildNumber;
    public Long getBuildNumber(){
        return 15L;
    }

}
