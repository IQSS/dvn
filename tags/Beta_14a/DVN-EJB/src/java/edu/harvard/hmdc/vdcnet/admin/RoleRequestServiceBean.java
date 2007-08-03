/*
 * UserServiceBean.java
 *
 * Created on September 21, 2006, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import edu.harvard.hmdc.vdcnet.vdc.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 */
@Stateless
public class RoleRequestServiceBean implements RoleRequestServiceLocal {
    
    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    
    /**
     * Creates a new instance of UserServiceBean
     */
    public RoleRequestServiceBean() {
    }
    
    
    public RoleRequest find(Long roleRequestId) {
        return (RoleRequest)em.find(RoleRequest.class,roleRequestId);
    }
    
    public void create(Long vdcUserId, Long roleId, Long vdcId) { 
         doCreate(vdcUserId, roleId, vdcId);
    }
    
    public void create(Long vdcUserId, Long roleId) {
        doCreate(vdcUserId, roleId, null);
    }
    
    private void doCreate(Long vdcUserId, Long roleId, Long vdcId) {
        RoleRequest roleRequest = new RoleRequest();
        
        VDCUser user = (VDCUser)em.find(VDCUser.class,vdcUserId);
        roleRequest.setVdcUser(user);
        
        Role role = (Role)em.find(Role.class,roleId);
        roleRequest.setRole(role);
        
        if (vdcId!=null) {
            VDC vdc = (VDC)em.find(VDC.class,vdcId);
            roleRequest.setVdc(vdc);
        }
        em.persist(roleRequest);
    }
  
    public RoleRequest findByUserVDCRole(Long vdcUserId, Long vdcId, String roleName) {
          String query="SELECT r from RoleRequest r where r.vdcUser.id = "+vdcUserId
                  + " and r.vdc.id = "+vdcId
                  + " and r.role.name ='"+roleName+"'";
          RoleRequest roleRequest = null;
          try {
              roleRequest = (RoleRequest)em.createQuery(query).getSingleResult();
          } catch (javax.persistence.NoResultException e) {
            // Just return null
          }
          return roleRequest;
        
    }
    public RoleRequest findContributorRequest(Long vdcUserId, Long vdcId) {
        return findByUserVDCRole(vdcUserId,vdcId, RoleServiceLocal.CONTRIBUTOR);
        
    }
    
}
