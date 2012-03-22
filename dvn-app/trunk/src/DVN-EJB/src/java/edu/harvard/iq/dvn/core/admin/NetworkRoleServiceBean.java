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
