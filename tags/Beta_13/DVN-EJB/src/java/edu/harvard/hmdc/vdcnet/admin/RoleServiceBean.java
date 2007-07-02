/*
 * UserServiceBean.java
 *
 * Created on September 21, 2006, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 */
@Stateless
public class RoleServiceBean implements RoleServiceLocal { 

    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    
    /**
     * Creates a new instance of UserServiceBean
     */
    public RoleServiceBean() {
    }


    public VDCRole findByUserVDC(Long userId, Long vdcId) {
     String query="SELECT r from VDCRole r where r.vdcUserId = "+userId+" and r.vdcId = "+vdcId;
       VDCRole role=null;
       try {
           role=(VDCRole)em.createQuery(query).getSingleResult();
       } catch (javax.persistence.NoResultException e) {
           // Do nothing, just return null. 
       }
       return role;
    }    
    
    public Role findByName(String name) {
       String query="SELECT r from Role r where r.name = '"+name+"'";
       Role role=null;
       role=(Role)em.createQuery(query).getSingleResult();   
       return role;
    }
    public Role findById(Long id) {
        return em.find(Role.class,id);
    }
   
}
