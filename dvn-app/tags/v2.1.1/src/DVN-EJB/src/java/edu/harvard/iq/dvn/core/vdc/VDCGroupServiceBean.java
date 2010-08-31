/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
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
