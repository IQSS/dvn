/*
 * ReviewStateServiceBean.java
 *
 * Created on November 17, 2006, 2:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.vdc.ReviewState;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateless
public class ReviewStateServiceBean implements ReviewStateServiceLocal{
      @PersistenceContext(unitName="VDCNet-ejbPU") EntityManager em;
  
    public ReviewState findByName(String name) {
        String query="SELECT r from ReviewState r where r.name = '"+name+"'";
        ReviewState reviewState=null;
        try {
            reviewState=(ReviewState)em.createQuery(query).getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            // DO nothing, just return null.
        }
        return reviewState;
    }
    
}
