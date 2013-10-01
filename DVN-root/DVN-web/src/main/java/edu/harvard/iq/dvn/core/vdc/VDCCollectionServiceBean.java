/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
/*
 * VDCCollectionServiceBean.java
 *
 * Created on September 22, 2006, 11:02 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.vdc;

import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.ejb.EJB;
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

    @PersistenceContext(unitName = "VDCNet-ejbPU")
    private EntityManager em;
    private static final Logger logger = Logger.getLogger("edu.harvard.iq.dvn.core.vdc.VDCCollectionServiceBean");
    @EJB
    IndexServiceLocal indexService;
    @EJB
    StudyServiceLocal studyService;
    @EJB
    VDCServiceLocal vdcService;

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
        VDCCollection managedCollection = em.merge(vDCCollection);
        managedCollection.setName(vDCCollection.getName());
        managedCollection.setQuery(vDCCollection.getQuery());
        managedCollection.setDescription(vDCCollection.getDescription());
        managedCollection.setType(vDCCollection.getType());
        managedCollection.setScope(vDCCollection.getScope());
        managedCollection.setDisplayOrder(vDCCollection.getDisplayOrder());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void destroy(VDCCollection vDCCollection) {
        VDCCollection mCollection = em.find(VDCCollection.class, vDCCollection.getId());
        // Remove this Collection from all linked VDCs
        for (Iterator itc = mCollection.getLinkedVDCs().iterator(); itc.hasNext();) {
            VDC linkedVDC = (VDC) itc.next();
            linkedVDC.getLinkedCollections().remove(mCollection);
        }
        em.merge(mCollection);
        em.remove(mCollection);
    }

    public VDCCollection find(Object pk) {
        return (VDCCollection) em.find(VDCCollection.class, pk);
    }

    public VDCCollection findByNameWithinDataverse(String name, VDC dataverse) {
        String query = "SELECT v from VDCCollection v where v.name = :fieldName and v.owner = :owner";
        VDCCollection vdcCollection = null;
        try {
            vdcCollection = (VDCCollection) em.createQuery(query).setParameter("fieldName", name).setParameter("owner", dataverse).getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            logger.info("Collection " + dataverse.getName() + "/" + name + " not found");
        }
        return vdcCollection;
    }

    public List findAll() {
        return em.createQuery("select object(o) from VDCCollection as o").getResultList();
    }

    public List findSubCollections(Long id) {
        return findSubCollections(id, false);
    }

    public List findSubCollections(Long id, boolean getHiddenCollections) {
        String query = "select c " +
                " from VDCCollection c " +
                " where c.parentCollection.id = " + id;

        query += " order by c.name ";

        return em.createQuery(query).getResultList();


    }

    private java.util.List<Long> getStudyIdsByCollection(Long collectionId) {
        String queryStr = "SELECT s.id FROM VDCCollection c JOIN c.studies s where c.id = " + collectionId;
        Query query = em.createQuery(queryStr);
        return query.getResultList();
    }

    public List<Long> getStudyIds(VDCCollection coll) {
        return studyService.getOrderedStudies( getStudyIds(coll, true), "releaseTime");
    }

    private List<Long> getStudyIds(VDCCollection coll, boolean includeSubCollections) {

        Set<Long> studyIds = new LinkedHashSet();

        if (coll.isRootCollection()) {
            studyIds.addAll(vdcService.getOwnedStudyIds(coll.getOwner().getId()));
        }

        if (coll.isDynamic()) {
            String query = null;
            if (coll.isLocalScope()) {
                query = "dvOwnerId:" + coll.getOwner().getId() + " AND (" + coll.getQuery() + ")";
            } else if (coll.isSubnetworkScope()) {
                query = "ownerDvNetworkId:" + coll.getOwner().getVdcNetwork().getId() + " AND (" + coll.getQuery() + ")";
            } else {
                query = coll.getQuery();
            }
            studyIds.addAll(indexService.query( query ));
        } else {
            studyIds.addAll(getStudyIdsByCollection(coll.getId()));
        }

        if (includeSubCollections) {
            for (VDCCollection subColl : coll.getSubCollections()) {
                if (coll.isRootCollection()) {
                    // creat flat list, in order to correctly exclude localscope queries
                    for (VDCCollection flatColl : getCollectionList(subColl)) {
                        // include studies if it's assigned or if it's dynamic and global
                        if (!flatColl.isDynamic() || !flatColl.isLocalScope()) {
                            studyIds.addAll( getStudyIds(flatColl, false) );
                        }
                    }
                } else {
                    studyIds.addAll(getStudyIds(subColl));
                }
            }
        }
        
        return new ArrayList(studyIds);
    }

    public List<Study> getStudies(VDCCollection coll) {
        List<Study> studies = new ArrayList();

        for (Long sid : getStudyIds(coll)) {
            studies.add(studyService.getStudy(sid));
        }
        return studies;
    }

    public List<VDCCollection> getCollectionList(VDC vdc) {
        return getCollectionList(vdc.getRootCollection(), null);
    }

    public List<VDCCollection> getCollectionList(VDC vdc, VDCCollection collectionToExclude) {
        return getCollectionList(vdc.getRootCollection(), collectionToExclude);
    }

    public List<VDCCollection> getCollectionList(VDCCollection coll) {
        return getCollectionList(coll, null);
    }

    private List<VDCCollection> getCollectionList(VDCCollection coll, VDCCollection collectionToExclude) {
        List collections = new ArrayList<VDCCollection>();
        addCollectionFamilyToList(collections, coll, collectionToExclude);
        return collections;
    }

    private void addCollectionFamilyToList(List collections, VDCCollection coll, VDCCollection collectionToExclude) {
        if (collectionToExclude == null || !coll.getId().equals(collectionToExclude.getId())) {
            collections.add(coll);
            for (VDCCollection subColl : coll.getSubCollections()) {
                subColl.setLevel(coll.getLevel() + 1);
                addCollectionFamilyToList(collections, subColl, collectionToExclude);
            }
        }
    }
}
