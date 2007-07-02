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
public class PageDefServiceBean implements PageDefServiceLocal  { 

    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    
    /**
     * Creates a new instance of UserServiceBean
     */
    public PageDefServiceBean() {
    }


   public PageDef findByPath(String path) {
       String query="SELECT r from PageDef r where r.path = '"+path+"'";
       PageDef pageDef=null;
       try {
        pageDef=(PageDef)em.createQuery(query).getSingleResult();
       } catch(NoResultException e) {
           // Do nothing, just return null;
       }
       return pageDef;
    }   
    
    public PageDef findByName(String name) {
       String query="SELECT r from PageDef r where r.name = '"+name+"'";
       PageDef pageDef=null;
       try {
            pageDef=(PageDef)em.createQuery(query).getSingleResult();   
       } catch(NoResultException e) {
           // Do nothing, just return null;
       }
       return pageDef;
    }
    public Role findById(Long id) {
        return em.find(Role.class,id);
    }
   
}
