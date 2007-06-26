/*
 * VDCGroupServiceLocal.java
 *
 * Created on May 23, 2007, 9:54 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import javax.ejb.Local;

/**
 *
 * @author wbossons
 */
@Local
public interface VDCGroupServiceLocal {
    
    public VDCGroup findById(Long id);
    
    public java.util.Collection<VDCGroup> findAll();
    
    public void updateVdcGroup(VDCGroup vdcgroup);
}
