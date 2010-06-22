/*
 * DVNVersionServiceLocal.java
 * 
 * Created on Sep 20, 2007, 10:24:41 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.admin;

import javax.ejb.Local;

/**
 *
 * @author roberttreacy
 */
@Local
public interface DVNVersionServiceLocal extends java.io.Serializable{

    public edu.harvard.iq.dvn.core.admin.DVNVersion getLatestVersion();
    
}
