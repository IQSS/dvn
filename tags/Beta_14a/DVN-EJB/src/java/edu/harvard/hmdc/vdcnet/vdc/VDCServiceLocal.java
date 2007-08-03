/*
 * VDCServiceLocal.java
 *
 * Created on September 21, 2006, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import java.util.List;
import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author roberttreacy
 */
@Local
public interface VDCServiceLocal {
    public void create(VDC vDC);
    
    public void create(Long userId, String name, String alias);

    public void edit(VDC vDC);

    public void destroy(VDC vDC);

    public VDC find(Object pk);
    
    public VDC findById(Long id);

    public List findAll();

    public VDC findByAlias(String alias);

    public VDC findByName(String name);

    public VDC getVDCFromRequest(HttpServletRequest request);
    
    public void addContributorRequest(Long vdcId, Long userId);

    java.util.List getLinkedCollections(VDC vdc);

    java.util.List getLinkedCollections(VDC vdc, boolean getHiddenCollections);
     
    public void delete (Long vdcId);
    
    public List findAllNonHarvesting();

    public List findVdcsNotInGroups();
    
}
