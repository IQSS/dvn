/*
 * UserServiceBean.java
 *
 * Created on September 21, 2006, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import java.util.Iterator;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 */
@Stateless
public class UserGroupServiceBean implements UserGroupServiceLocal {
   
    
    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    
    /**
     * Creates a new instance of UserServiceBean
     */
    public UserGroupServiceBean() {
    }
    
 
    
    public  UserGroup findById(Long id) {
        UserGroup userGroup = em.find(UserGroup.class, id);
        for (Iterator it = userGroup.getUsers().iterator(); it.hasNext();) {
            VDCUser elem = (VDCUser) it.next();
            Long userId = elem.getId();
        }
        return userGroup;
    }
    
  
}
