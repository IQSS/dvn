/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
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
