/*
 * EditVDCPrivilegesService.java
 *
 * Created on October 23, 2006, 4:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import edu.harvard.hmdc.vdcnet.harvest.SetDetailBean;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverse;
import java.util.List;
import javax.ejb.Remove;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Ellen Kraffmiller
 */
public interface EditHarvestSiteService {
    /**
     * Remove this Stateful Session bean from the EJB Container without 
     * saving updates to the database.
     */
    @Remove
    void cancel();

    
    HarvestingDataverse getHarvestingDataverse();

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save(String name, String alias, Long userId);

   
    public void removeAllowedFileGroup(Long groupId);

    public void addAllowedFileGroup(Long groupId);
    
    public void removeAllowedFileUser(Long userId);

    public void addAllowedFileUser(Long userId);
    
    void setHarvestingDataverse(Long id);
    
    
    void newHarvestingDataverse();
    
    public List<SetDetailBean> getHarvestingSets();
  
    public void setHarvestingSets(List<SetDetailBean> harvestingSets);
    
    public List<String> getMetadataFormats();
  
    public void setMetadataFormats(List<String> metadataFormats);
    
}
