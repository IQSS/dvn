/*
 * OAISetServiceBean.java
 *
 * Created on Oct 2, 2007, 5:13:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.vdc;

import ORG.oclc.oai.server.verb.NoItemsMatchException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateless
public class OAISetServiceBean implements OAISetServiceLocal {
   @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em; 
    
    
    public OAISet findBySpec(String spec) throws NoItemsMatchException {
     String query="SELECT o from OAISet o where o.spec = :fieldName";
       OAISet oaiSet=null;
       try {
           oaiSet=(OAISet)em.createQuery(query).setParameter("fieldName",spec).getSingleResult();
       } catch (javax.persistence.NoResultException e) {
           throw new NoItemsMatchException();
           // Do nothing, just return null. 
       }
       return oaiSet;
    }
    public List<OAISet> findAll() {
        return em.createQuery("select object(o) from OAISet as o order by o.name").getResultList();
    }

    public void remove(Long id) {
        OAISet oaiSet = em.find(OAISet.class, id);
        em.remove(oaiSet);
    }
    
    public OAISet findById(Long id) {
       return em.find(OAISet.class,id);
    }   
    
    public void update(OAISet oaiSet) {
        em.merge(oaiSet);
    }
    
    
}
