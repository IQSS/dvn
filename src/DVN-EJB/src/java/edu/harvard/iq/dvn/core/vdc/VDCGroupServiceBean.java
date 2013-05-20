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
 * VDCGroupServiceBean.java
 *
 * Created on May 23, 2007, 9:56 AM
 *
 * @author wbossons
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.vdc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author wbossons
 */
@Stateless
public class VDCGroupServiceBean implements VDCGroupServiceLocal {
    
    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    @EJB VDCServiceLocal vdcService;
    /**
     * Constructor
     */
    public VDCGroupServiceBean() {    
    }
    
    public VDCGroup findById(Long id) {
        VDCGroup o = (VDCGroup) em.find(VDCGroup.class, id);
        return o;
    }

    public VDCGroup findByName(String name) {
        String query = "SELECT object(o) FROM VDCGroup AS o WHERE o.name = :fieldName";
        VDCGroup vdcgroup = null;
        try {
            vdcgroup = (VDCGroup)em.createQuery(query).setParameter("fieldName", name).getSingleResult();
        } catch (NoResultException nre) {
            //System.out.println("no result for findByName " + name);
        }
        finally {
            return vdcgroup;
        }
    }
    
    public List<VDCGroup> findAll() {
        List<VDCGroup> vdcgroups = (List<VDCGroup>)em.createQuery("select object(o) from VDCGroup as o order by o.displayOrder").getResultList();
        return vdcgroups;
    }

    /** findByParentId
     *
     *
     * @param id
     * @return a list of VDCGroup type that are subclass'ns
     *          of this VDCGroup of param id.
     */
    public List<VDCGroup> findByParentId(Long id) {
        Query query = null;
        if (id==null) {
            query = em.createQuery("SELECT object(o) FROM VDCGroup AS o WHERE o.parent is null ORDER BY o.displayOrder");
        } else {
            query = em.createQuery("SELECT object(o) FROM VDCGroup AS o WHERE o.parent = :fieldName ORDER BY o.displayOrder");
            query.setParameter("fieldName", id);
        }
        List<VDCGroup> vdcgroups = (List<VDCGroup>)query.getResultList();
        return vdcgroups;
    }
    
    private static final String SELECT_VDC_IDS = "select vd.vdc_id from vdcgroup_vdcs vd where vd.vdcgroup_id = ?;";
    
    public List<Long> findVDCIdsByVDCGroupId(Long id) {
        Query query = null;
        List<Long> vdcids = new ArrayList();       
        query = em.createNativeQuery(SELECT_VDC_IDS).setParameter(1, id);
        for (Object currentResult : query.getResultList()) {
            vdcids.add(new Long(((Long)currentResult).longValue()));
        }
        return vdcids;
    }
    
    private static final String COUNT_VDCs_UNRESTRICTED = "select count(*) from vdcgroup_vdcs vd, VDC d where d.ID = vd.vdc_id and d.restricted = false and vd.vdcgroup_id = ?;";
    
    public Long findCountVDCsByVDCGroupId(Long id) {
        Query query = em.createNativeQuery(COUNT_VDCs_UNRESTRICTED).setParameter(1, id);        
        Long countReleased = (Long) query.getSingleResult();
        return countReleased;
    }
    private static final String COUNT_VDCs_UNRESTRICTED_W_SUBNETWORKS = "select count(*) from vdcgroup_vdcs vd, VDC d where d.ID = vd.vdc_id and d.restricted = false and vd.vdcgroup_id = ? and d.vdcnetwork_id = ?;";
    
    public Long findCountVDCsByVDCGroupIdSubnetworkId(Long id, Long netId) {
        Query query = null;
        if (netId !=null && netId > 0){
            query = em.createNativeQuery(COUNT_VDCs_UNRESTRICTED_W_SUBNETWORKS).setParameter(1, id);     
            query.setParameter(2, netId); 
        } else {
            query = em.createNativeQuery(COUNT_VDCs_UNRESTRICTED).setParameter(1, id); 
        }

        Long countReleased = (Long) query.getSingleResult();
        return countReleased;
    }
    
    private static final String COUNT_CHILD_VDCs_UNRESTRICTED = " select count(*) from vdcgroup_vdcs vd, VDC d where d.ID = vd.vdc_id and d.restricted = false and vd.vdcgroup_id in (select id from vdcgroup where parent = ?);";
    
    public Long findCountChildVDCsByVDCGroupId(Long id) {
        Query query = em.createNativeQuery(COUNT_CHILD_VDCs_UNRESTRICTED).setParameter(1, id);    
        Long countReleased = (Long) query.getSingleResult();
        return countReleased;
    }
    
    private static final String COUNT_CHILD_VDCs_UNRESTRICTED_W_SUBNETWORKS = " select count(*) from vdcgroup_vdcs vd, VDC d where d.ID = vd.vdc_id and d.restricted = false and vd.vdcgroup_id in (select id from vdcgroup where parent = ?) and d.vdcnetwork_id = ?;";
    
    public Long findCountChildVDCsByVDCGroupIdSubnetworkId(Long id, Long netId) {
        Query query = null;
        if (netId != null && netId > 0) {
            query = em.createNativeQuery(COUNT_CHILD_VDCs_UNRESTRICTED_W_SUBNETWORKS).setParameter(1, id);
            query.setParameter(2, netId);
        } else {
            query = em.createNativeQuery(COUNT_CHILD_VDCs_UNRESTRICTED).setParameter(1, id);
        }
        Long countReleased = (Long) query.getSingleResult();
        return countReleased;
    }
    
    private static final String COUNT_ALL_VDCs_UNRESTRICTED = " select count(distinct d.id) from vdcgroup_vdcs vd, VDC d where d.ID = vd.vdc_id and d.restricted = false and (vd.vdcgroup_id in (select id from vdcgroup where parent = ?) or vd.vdcgroup_id = ?);";
    
    public Long findCountParentChildVDCsByVDCGroupId(Long id) {
        Query query = em.createNativeQuery(COUNT_ALL_VDCs_UNRESTRICTED).setParameter(1, id);
        query.setParameter(2, id);
        Long countReleased = (Long) query.getSingleResult();
        return countReleased;
    }
    private static final String COUNT_ALL_VDCs_UNRESTRICTEDW_SUBNETWORKS = " select count(distinct d.id) from vdcgroup_vdcs vd, VDC d where d.ID = vd.vdc_id and d.restricted = false and (vd.vdcgroup_id in (select id from vdcgroup where parent = ?) or vd.vdcgroup_id = ?) and d.vdcnetwork_id = ?;";
    
    public Long findCountParentChildVDCsByVDCGroupIdSubnetworkId(Long id, Long netId) {
        Query query = null;
        if (netId != null && netId > 0) {
            query = em.createNativeQuery(COUNT_ALL_VDCs_UNRESTRICTEDW_SUBNETWORKS).setParameter(1, id);
            query.setParameter(2, id);
            query.setParameter(3, netId);
        } else {
            query = em.createNativeQuery(COUNT_ALL_VDCs_UNRESTRICTED).setParameter(1, id);
            query.setParameter(2, id);
        }
        Long countReleased = (Long) query.getSingleResult();
        return countReleased;
    }
    
    List<VDCGroup> allDescendants = new ArrayList();
            
    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeVdcGroup(VDCGroup vdcgroup) {
        VDCGroup group = findById(vdcgroup.getId());
        if (group != null) {
            List membervdcs = (List)group.getVdcs();
            Iterator iterator = membervdcs.iterator();
            while (iterator.hasNext()) {
                VDC vdc = (VDC)iterator.next();
                if (vdc.getVdcGroups().contains(group))
                    vdc.getVdcGroups().remove(group);
                iterator.remove();//remove the vdc from the group relationship
            } 
        }
        List<VDCGroup> nodeDescendants = findByParentId(group.getId());
        if (nodeDescendants != null) {
            allDescendants.addAll(nodeDescendants);
            System.out.println("VDCGroupServiceBean: descendants = " + allDescendants.toString());
            buildDescendantsList(nodeDescendants);
            Iterator innerIterator = allDescendants.iterator();
            while (innerIterator.hasNext()) {
                VDCGroup vdcGroup = (VDCGroup)innerIterator.next();
                List vdcs = (List)vdcGroup.getVdcs();
                Iterator vdcIterator = vdcs.iterator();
                while (vdcIterator.hasNext()) {
                    VDC vdc = (VDC)vdcIterator.next();
                    if (vdc.getVdcGroups().contains(vdcGroup))
                        vdc.getVdcGroups().remove(vdcGroup);
                    vdcIterator.remove();//remove the vdc from the group relationship
                }
                em.remove(vdcGroup);
            }
        }
        em.remove(group);
    }

    private void buildDescendantsList(List<VDCGroup> descendants) {
        Iterator iterator = descendants.iterator();
        while (iterator.hasNext()) {
            VDCGroup group = (VDCGroup)iterator.next();
            List<VDCGroup> nodeDescendants = findByParentId(group.getId());
            if (nodeDescendants != null) {
                allDescendants.addAll(nodeDescendants);
                recurseAndBuildDescendantsList(nodeDescendants);
            }
        }
    }

    private void recurseAndBuildDescendantsList(List<VDCGroup> descendants) {
          Iterator iterator = descendants.iterator();
          while (iterator.hasNext()) {
                VDCGroup group = (VDCGroup)iterator.next();
                List<VDCGroup> nodeDescendants = findByParentId(group.getId());
                if (nodeDescendants != null) {
                    allDescendants.addAll(nodeDescendants);
                    recurseAndBuildDescendantsList(nodeDescendants);
                }
          }
    }
    //END DEBUG
    
    public void create(VDCGroup vdcgroup) {
        em.persist(vdcgroup);
        em.flush();
        em.refresh(vdcgroup);
    }
    
    public void updateVdcGroup(VDCGroup vdcgroup) {
        if (findById(vdcgroup.getId()) != null) {
            em.merge(vdcgroup);
        }
    }

    /** updateWithVdcs
     *
     * This version was used prior to v1.4
     * It may be removed after it's determined that
     * it's no longer used anywhere.
     *
     * @param vdcgroup
     * @param vdcs
     */
    public void updateWithVdcs(VDCGroup vdcgroup, String[] vdcs) {
            // remove existing relationships
        vdcgroup = findById(vdcgroup.getId());
        if (vdcgroup != null) {
            List membervdcs = (List)vdcgroup.getVdcs();
            Iterator iterator = membervdcs.iterator();
            while (iterator.hasNext()) {
                VDC vdc = (VDC)iterator.next();
                if (vdc.getVdcGroups().contains(vdcgroup))
                    vdc.getVdcGroups().remove(vdcgroup);
                iterator.remove();//remove the vdc from the vdcgroup relationship
            } 
        
            //end remove existing relationships
            String selectedVdcs[] = vdcs;
            for ( int i = 0; i < selectedVdcs.length; i++ ) {
                String mystring = selectedVdcs[i].toString();
                Long vdcid = new Long(mystring);
                VDC vdc    = vdcService.findById(vdcid);
                if (!vdc.getVdcGroups().contains(vdcgroup)) {
                    vdc.getVdcGroups().add(vdcgroup);
                    vdcgroup.getVdcs().add(vdc);
                }
            }
        }
    }

    /** updateWithVdcs
     *
     * This method overrides the previously needed method
     * updateWithVdcs. The new component doesn't build a string
     * array, so there's no need to deal with strings.
     *
     * @param vdcgroup
     * @param vdcs
     */
    public void updateWithVdcs(VDCGroup vdcgroup, Long[] vdcs) {
            // remove existing relationships
        vdcgroup = findById(vdcgroup.getId());
        if (vdcgroup != null) {
            List membervdcs = (List)vdcgroup.getVdcs();
            Iterator iterator = membervdcs.iterator();
            while (iterator.hasNext()) {
                VDC vdc = (VDC)iterator.next();
                if (vdc.getVdcGroups().contains(vdcgroup))
                    vdc.getVdcGroups().remove(vdcgroup);
                iterator.remove();//remove the vdc from the vdcgroup relationship
            }

            //end remove existing relationships
            for ( int i = 0; i < vdcs.length; i++ ) {
                Long vdcid = vdcs[i];
                VDC vdc    = vdcService.findById(vdcid);
                if (!vdc.getVdcGroups().contains(vdcgroup)) {
                    vdc.getVdcGroups().add(vdcgroup);
                    vdcgroup.getVdcs().add(vdc);
                }
            }
        }
    }
    
    /** getNextInOrder
     *
     * returns the next int
     * in sequence for group 
     * ordering
     *
     * @author wbossons
     *
     */
    public int getNextInOrder() {
        Integer nextInOrder = new Integer("0");
        try {
            nextInOrder = (Integer)em.createQuery("select MAX(v.displayOrder) from VDCGroup v").getSingleResult();
            nextInOrder += 1;
        } catch (Exception e) {
           System.out.append("There was an exception in getting the next in order ... " + e.toString()); 
        }
        return nextInOrder.intValue();
    }

}
