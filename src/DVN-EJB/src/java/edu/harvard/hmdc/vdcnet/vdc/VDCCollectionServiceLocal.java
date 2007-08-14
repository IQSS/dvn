/*
 * VDCCollectionServiceLocal.java
 *
 * Created on September 22, 2006, 11:02 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import edu.harvard.hmdc.vdcnet.study.Study;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author roberttreacy
 */
@Local
public interface VDCCollectionServiceLocal {
    void create(VDCCollection vDCCollection);

    void edit(VDCCollection vDCCollection);

    void destroy(VDCCollection vDCCollection);

    VDCCollection find(Object pk);

    List findAll();
    
    public List findSubCollections(Long id);

    java.util.List findSubCollections(Long id, boolean getAllCollections);
    
    public java.util.List<Study> getOrderedStudiesByCollection(Long collectionId);
    
    public java.util.List<Long> getOrderedStudyIdsByCollection(Long collectionId);
    
}
