/*
 * HandlePrefixServiceBean.java
 *
 * Created on June 14, 2007, 11:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateless
public class HandlePrefixServiceBean implements edu.harvard.hmdc.vdcnet.vdc.HandlePrefixServiceLocal {
      @PersistenceContext(unitName="VDCNet-ejbPU")
      private EntityManager em;
 
    
      public List<HandlePrefix> findAll() {
         return  (List <HandlePrefix>) em.createQuery("SELECT hp from HandlePrefix hp").getResultList();
      }
    
}
