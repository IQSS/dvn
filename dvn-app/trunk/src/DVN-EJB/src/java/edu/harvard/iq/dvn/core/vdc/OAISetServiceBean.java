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
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Vector;

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

     public List<OAISet> findAllOrdered() {
        //return em.createQuery("select object(o) from OAISet as o order by o.name").getResultList();

        //List<Long> returnOaiSets = new ArrayList();
        //return em.createQuery("select object(o) from OAISet  as o where  o.lockssconfig_id != null  order by o.name").getResultList();

        List<OAISet> returnOaiSets = em.createQuery("select object(o) from OAISet  as o where  o.lockssConfig is  null  order by o.name").getResultList();
/*
        String nativeQuery = "select * from OAISet   " +
            " as o where  lockssconfig_id > 0  order by o.name ";

        Query query = em.createNativeQuery(nativeQuery);

        for (Object currentResult :  query.getResultList()) {
            OAISet addResult = new OAISet();
            addResult.setId(new Long(((Integer) ((Vector) currentResult).get(0))).longValue());
            addResult.setDefinition((String) (( ((Vector) currentResult).get(1))));
            addResult.setDescription((String) (( ((Vector) currentResult).get(2))));
            addResult.setSpec((String) (( ((Vector) currentResult).get(3))));
            addResult.setVersion(new Long(((Long) ((Vector) currentResult).get(4))));
            addResult.setName((String) (( ((Vector) currentResult).get(5))));
            addResult.setLockssConfig(new LockssConfig());
            returnOaiSets.add(addResult);
        }
 */
        returnOaiSets.addAll(em.createQuery("select object(o) from OAISet  as o where  o.lockssConfig is not null  order by o.name").getResultList());

        return returnOaiSets;

    }

    public void remove(Long id) {
        OAISet oaiSet = em.find(OAISet.class, id);
        em.createQuery("delete from HarvestStudy hs where hs.setName = '" + oaiSet.getName() + "'").executeUpdate();
        em.remove(oaiSet);
    }
    
    public OAISet findById(Long id) {
       return em.find(OAISet.class,id);
    }   
    
    public void update(OAISet oaiSet) {
        em.merge(oaiSet);
    }
    
    
}
