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

package edu.harvard.hmdc.vdcnet.vdc;

import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    
    public List<VDCGroup> findAll() {
        List<VDCGroup> vdcgroups = (List<VDCGroup>)em.createQuery("select object(o) from VDCGroup as o order by o.displayOrder").getResultList() ;
        return vdcgroups;
    }
    
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
        em.remove(group);
    }
    
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
}
