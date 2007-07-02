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
public interface UserServiceLocal {
   

    public VDCUser find(Long id);

    public List findAll();
    
    public void remove(Long id);
    
    public VDCUser findByUserName(String userName);
    
    public VDCUser findByUserName(String userName, boolean activeOnly);

    public void addVdcRole(Long userId, Long vdcId, String roleName);
    
    public void addCreatorRequest(Long userId);

    public void addContributorRequest(Long userId, Long vdcId);
    
    public void setActiveStatus(Long userId, boolean active);
    
}
