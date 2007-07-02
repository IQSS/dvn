/*
 * UserServiceBean.java
 *
 * Created on September 21, 2006, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

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
  
   
}
