/*
 * VDCCollectionServiceBean.java
 *
 * Created on September 22, 2006, 11:02 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class VDCCollectionServiceBean implements VDCCollectionServiceLocal {
    
    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    
    /**
     * Creates a new instance of VDCCollectionServiceBean
     */
    public VDCCollectionServiceBean() {
    }
    
    public void create(VDCCollection vDCCollection) {

        em.persist(vDCCollection);
        for (Iterator it = vDCCollection.getStudies().iterator(); it.hasNext();) {
            Study elem = (Study) it.next();
            em.merge(elem);
        }
    }
    
    public void edit(VDCCollection vDCCollection) {
        em.merge(vDCCollection);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void destroy(VDCCollection vDCCollection) {
        VDCCollection mCollection = em.find(VDCCollection.class, vDCCollection.getId());
        em.merge(mCollection);
        em.remove(mCollection);
    }
    
    public VDCCollection find(Object pk) {
        return (VDCCollection) em.find(VDCCollection.class, pk);
    }
    
    public List findAll() {
        return em.createQuery("select object(o) from VDCCollection as o").getResultList();
    }
    
    public List findSubCollections(Long id) {
        return findSubCollections(id, false);
    }
    
    public List findSubCollections(Long id, boolean getHiddenCollections) {
        String query = "select c "+
                " from VDCCollection c "+
                " where c.parentCollection.id = "+id;
        
        if (!getHiddenCollections) {
            query += " and c.visible=true";
        }
        
        query += " order by c.name ";
        
        return em.createQuery(query).getResultList();
        
        
    }
    
     public java.util.List<Study> getOrderedStudiesByCollection(Long collectionId){
        String queryStr = "SELECT s FROM VDCCollection c JOIN c.studies s where c.id = " + collectionId +" ORDER BY s.title";
        Query query =em.createQuery(queryStr);
        List <Study> studies = query.getResultList();
        
        return studies;
        
     }
    
}
