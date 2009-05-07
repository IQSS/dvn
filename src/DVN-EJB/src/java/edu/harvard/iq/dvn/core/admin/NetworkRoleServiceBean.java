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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 */
@Stateless
public class NetworkRoleServiceBean implements NetworkRoleServiceLocal { 

    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    
    /**
     * Creates a new instance of UserServiceBean
     */
    public NetworkRoleServiceBean() {
    }


  
    
    public NetworkRole findByName(String name) { 
       String query="SELECT r from NetworkRole r where r.name = '"+name+"'";
       NetworkRole role=null;
       role=(NetworkRole)em.createQuery(query).getSingleResult();   
       return role; 
    }
    
   public void  newCreatorRequest(Long userId) {
        NetworkRoleRequest request = new NetworkRoleRequest();
        request.setVdcUser(em.find(VDCUser.class, userId));
        request.setNetworkRole(findByName(NetworkRoleServiceLocal.CREATOR));
        em.persist(request);
    }
   
   public NetworkRoleRequest findCreatorRequest(Long userId) {
       String query="SELECT r from NetworkRoleRequest r where r.vdcUser.id = "+userId;
       NetworkRoleRequest request = null;
       try {
            request= (NetworkRoleRequest)em.createQuery(query).getSingleResult();
       } catch(NoResultException e) {
           // do nothing, just return null
       }
       return request;
   } 
  
   public NetworkRole  getCreatorRole() {
       return findByName(NetworkRoleServiceLocal.CREATOR); 
   }   
}
