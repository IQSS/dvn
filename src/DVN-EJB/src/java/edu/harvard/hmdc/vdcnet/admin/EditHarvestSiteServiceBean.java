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
 * EditVDCPrivilegesServiceBean.java
 *
 * Created on September 29, 2006, 1:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import edu.harvard.hmdc.vdcnet.harvest.SetDetailBean;
import edu.harvard.hmdc.vdcnet.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.HandlePrefix;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverse;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EditHarvestSiteServiceBean implements EditHarvestSiteService  {
    @EJB MailServiceLocal mailService;
    @EJB RoleServiceLocal roleService;
    @EJB UserServiceLocal userService;
    @EJB VDCServiceLocal vdcService;

    @PersistenceContext(type = PersistenceContextType.EXTENDED,unitName="VDCNet-ejbPU")
    //  @EJB RoleServiceLocal roleService;
    EntityManager em;
    private HarvestingDataverse harvestingDataverse;
    private Long selectedHandlePrefixId;

    public Long getSelectedHandlePrefixId() {
        return selectedHandlePrefixId;
    }

    public void setSelectedHandlePrefixId(Long selectedHandlePrefixId) {
        this.selectedHandlePrefixId = selectedHandlePrefixId;
    }
   
  
    
    /**
     *  Initialize the bean with a Study for editing
     */
    public void setHarvestingDataverse(Long id ) {
        harvestingDataverse = em.find(HarvestingDataverse.class,id);
        if (harvestingDataverse==null) {
            throw new IllegalArgumentException("Unknown harvestingDataverse id: "+id);
        }
        if (harvestingDataverse.getHandlePrefix()!=null) {
            selectedHandlePrefixId = harvestingDataverse.getHandlePrefix().getId();
        }
    }
    
    public void newHarvestingDataverse( ) {
        harvestingDataverse = new HarvestingDataverse();
     
        em.persist(harvestingDataverse);
    }
    
    public  HarvestingDataverse getHarvestingDataverse() {
        return harvestingDataverse;
    }
    
    
  
 
        
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save(String name, String alias, Long userId ) {
        if (harvestingDataverse.getVdc()==null) {
            vdcService.create(userId,name,alias);
            VDC vdc = vdcService.findByAlias(alias);
            // Get managed entity so we can update it with harvesting dataverse reference
            VDC managedVdc = em.find(VDC.class, vdc.getId());  
            harvestingDataverse.setVdc(managedVdc);
            managedVdc.setHarvestingDataverse(harvestingDataverse);
        } else {
            harvestingDataverse.getVdc().setName(name);
            harvestingDataverse.getVdc().setAlias(alias);
        }
        if (selectedHandlePrefixId==null) {
            harvestingDataverse.setHandlePrefix(null);
        } else {
            HandlePrefix handlePrefix = em.find(HandlePrefix.class, selectedHandlePrefixId);
            harvestingDataverse.setHandlePrefix(handlePrefix);
        }
        em.flush();
    }
    
  
    
    /**
     * Remove this Stateful Session bean from the EJB Container without
     * saving updates to the database.
     */
    @Remove
    public void cancel() {
        
    }
    /**
     * Creates a new instance of EditVDCPrivilegesServiceBean
     */
    public EditHarvestSiteServiceBean() {
    }
    
    /**
     * Holds value of property contributorRequests.
     */
 
    
    public void removeAllowedFileGroup(Long groupId) {
        UserGroup group = em.find(UserGroup.class,groupId);
        harvestingDataverse.getAllowedFileGroups().remove(group);
        
    }
    
    public void  addAllowedFileGroup(Long groupId) {
        UserGroup group = em.find(UserGroup.class,groupId);
        harvestingDataverse.getAllowedFileGroups().add(group);
       
        
    }
    
     public void removeAllowedFileUser(Long userId) {
        VDCUser user = em.find(VDCUser.class,userId);
        harvestingDataverse.getAllowedFileUsers().remove(user);
      
    }
    
    public void  addAllowedFileUser(Long userId) {
        VDCUser user = em.find(VDCUser.class,userId);
        harvestingDataverse.getAllowedFileUsers().add(user);
     
        
    }

    /**
     * Holds value of property harvestingSets.
     */
    private java.util.List<SetDetailBean> harvestingSets;

    /**
     * Getter for property harvestingSets.
     * @return Value of property harvestingSets.
     */
    public java.util.List<SetDetailBean> getHarvestingSets() {
        return this.harvestingSets;
    }

    /**
     * Setter for property harvestingSets.
     * @param harvestingSets New value of property harvestingSets.
     */
    public void setHarvestingSets(java.util.List<SetDetailBean> harvestingSets) {
        this.harvestingSets = harvestingSets;
    }
    
    private List<String> metadataFormats;
     
    public List<String> getMetadataFormats(){
        return metadataFormats;
    }
  
    public void setMetadataFormats(List<String> metadataFormats){
        this.metadataFormats=metadataFormats;
    }
}
