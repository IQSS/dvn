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

import edu.harvard.hmdc.vdcnet.admin.LoginDomain;
import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import java.util.Iterator;
import java.util.List;
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
    
    /**
     * Constructor
     */
    public VDCGroupServiceBean() {
        
    }
    
    public VDCGroup findById(Long id) {
        VDCGroup vdcGroup = em.find(VDCGroup.class, id);
        for (Iterator iterator = vdcGroup.getVdcs().iterator(); iterator.hasNext();) {
            VDC vdc = (VDC) iterator.next();
            Long vdcId = vdc.getId();
        }
        return vdcGroup;
    }
    
    public List<VDCGroup> findAll() {
        List<VDCGroup> vdcgroups = (List<VDCGroup>)em.createQuery("select object(o) from VDCGroup as o").getResultList();
        // Trigger loading of dependent objects
        for (Iterator iterator = vdcgroups.iterator(); iterator.hasNext();) {
            VDCGroup vdcgroup = (VDCGroup) iterator.next();
            if (vdcgroup.getVdcs().size() > 0) {
                for (Iterator innerIterator = vdcgroup.getVdcs().iterator(); innerIterator.hasNext();) {
                    VDC vdc = (VDC) innerIterator.next();
                    Long id = vdc.getId();
                }
           }
        }
        return vdcgroups;
    }
}
