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
        VDCGroup o = (VDCGroup) em.find(VDCGroup.class, id);
        return o;
    }
    
    public List<VDCGroup> findAll() {
        List<VDCGroup> vdcgroups = (List<VDCGroup>)em.createQuery("select object(o) from VDCGroup as o order by o.displayOrder").getResultList() ;
        return vdcgroups;
    }
    
    public void updateVdcGroup(VDCGroup vdcgroup) {
        if (findById(vdcgroup.getId()) != null) {
            em.merge(vdcgroup);
        }
    }
}
