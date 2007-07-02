/*
 * UserServiceLocal.java
 *
 * Created on September 21, 2006, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import edu.harvard.hmdc.vdcnet.vdc.*;
import javax.ejb.Local;

/**
 *
 * 
 */
@Local
public interface RoleRequestServiceLocal {
    public RoleRequest find(Long roleRequestId);
    public RoleRequest findByUserVDCRole(Long vdcUserId, Long vdcId, String roleName);
    public RoleRequest findContributorRequest(Long vdcUserId, Long vdcId);
    public void create(Long vdcUserId, Long roleId, Long vdcId);
    public void create(Long vdcUserId, Long roleId); 
}
