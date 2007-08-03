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
public interface NetworkRoleServiceLocal {
    public static final String CREATOR = "Creator";
    public static final String ADMIN = "Admin";
   

  

    public NetworkRole findByName(String roleName);
    
    public void newCreatorRequest(Long userId); 
    
    public NetworkRoleRequest findCreatorRequest(Long userId);
    
    
}
