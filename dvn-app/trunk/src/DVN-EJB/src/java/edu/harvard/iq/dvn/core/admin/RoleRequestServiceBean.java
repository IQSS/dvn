/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
/*
 * UserServiceBean.java
 *
 * Created on September 21, 2006, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.vdc.*;
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
