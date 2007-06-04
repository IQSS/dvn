/*
 * UserServiceLocal.java
 *
 * Created on September 21, 2006, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author roberttreacy
 */
@Local
public interface RoleServiceLocal {
   
    // Define constants here to use in FindBy Name
    public static final String CONTRIBUTOR = "contributor";
    public static final String CURATOR = "curator";
    public static final String ADMIN = "admin";
    public static final String PRIVILEGED_VIEWER = "privileged viewer";
    

    public VDCRole findByUserVDC(Long userId, Long vdcId);

    public Role findByName(String roleName);
    
    public Role findById(Long id);
  
    
    
    
}
