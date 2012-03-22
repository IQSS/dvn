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
 * EditVDCPrivilegesServiceBean.java
 *
 * Created on September 29, 2006, 1:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.harvest.HarvestFormatType;
import edu.harvard.iq.dvn.core.harvest.HarvesterServiceLocal;
import edu.harvard.iq.dvn.core.harvest.SetDetailBean;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.vdc.HandlePrefix;
import edu.harvard.iq.dvn.core.vdc.HarvestingDataverse;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.util.ArrayList;
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
    @EJB HarvesterServiceLocal harvesterService;
 
    @PersistenceContext(type = PersistenceContextType.EXTENDED,unitName="VDCNet-ejbPU")
    //  @EJB RoleServiceLocal roleService;
    EntityManager em;
    private HarvestingDataverse harvestingDataverse;
    private Long selectedHandlePrefixId;
    private Long selectedMetadataPrefixId;
    private String editMode;
   
    public String getEditMode() {
        return editMode;
    }

    public void setEditMode(String editMode) {
        this.editMode = editMode;
    }
    
    public Long getSelectedHandlePrefixId() {
        return selectedHandlePrefixId;
    }

    public void setSelectedHandlePrefixId(Long selectedHandlePrefixId) {
        this.selectedHandlePrefixId = selectedHandlePrefixId;
    }

    public Long getSelectedMetadataPrefixId() {
        return selectedMetadataPrefixId;
    }

    public void setSelectedMetadataPrefixId(Long selectedMetadataPrefixId) {
        this.selectedMetadataPrefixId = selectedMetadataPrefixId;
    }

  
    
    /**
     *  Initialize the bean with a Study for editing
     */
    public void setHarvestingDataverse(Long id ) {
        editMode = EDIT_MODE_UPDATE;
        harvestingDataverse = em.find(HarvestingDataverse.class,id);
        em.refresh(harvestingDataverse);
        if (harvestingDataverse==null) {
            throw new IllegalArgumentException("Unknown harvestingDataverse id: "+id);
        }
        if (harvestingDataverse.getHandlePrefix()!=null) {
            selectedHandlePrefixId = harvestingDataverse.getHandlePrefix().getId();
        }
        if (harvestingDataverse.getHarvestFormatType()!=null) {
            selectedMetadataPrefixId = harvestingDataverse.getHarvestFormatType().getId();
        }
        
        allowedFileGroups.addAll( harvestingDataverse.getVdc().getAllowedFileGroups() );
        allowedFileUsers.addAll( harvestingDataverse.getVdc().getAllowedFileUsers() );
    }
    
    public void newHarvestingDataverse( ) {
        editMode = EDIT_MODE_CREATE;
        harvestingDataverse = new HarvestingDataverse();
     
        em.persist(harvestingDataverse);
    }
    
    public  HarvestingDataverse getHarvestingDataverse() {
        return harvestingDataverse;
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save(Long userId, String name, String alias, boolean filesRestricted, String dtype, String affiliation) {

        // set these values first, so that reqired columns are not null!
        if (selectedHandlePrefixId==null) {
            harvestingDataverse.setHandlePrefix(null);
        } else {
            HandlePrefix handlePrefix = em.find(HandlePrefix.class, selectedHandlePrefixId);
            harvestingDataverse.setHandlePrefix(handlePrefix);
        }
        
        if (selectedMetadataPrefixId==null) {
            harvestingDataverse.setHarvestFormatType(null);
        } else {
            HarvestFormatType hft = em.find(HarvestFormatType.class, selectedMetadataPrefixId);
            harvestingDataverse.setHarvestFormatType(hft);
        }
   

        VDC vdc = null;
        if (harvestingDataverse.getVdc()==null) {
            vdcService.create(userId, name, alias, dtype);
            vdc = vdcService.findByAlias(alias);
            // Get managed entity so we can update it with harvesting dataverse reference
            VDC managedVdc = em.find(VDC.class, vdc.getId());  
            harvestingDataverse.setVdc(managedVdc);
            managedVdc.setHarvestingDataverse(harvestingDataverse);
        } else {
            vdc = harvestingDataverse.getVdc();
            vdc.setName(name);
            vdc.setAlias(alias);
        }

        vdc.setAffiliation(affiliation);

        vdc.setFilesRestricted(filesRestricted);
        vdc.setAllowedFileGroups(allowedFileGroups);
        vdc.setAllowedFileUsers(allowedFileUsers);
             
//        harvesterService.updateHarvestTimer(harvestingDataverse);
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
        getAllowedFileGroups().remove(group);
        
    }
    
    public void  addAllowedFileGroup(Long groupId) {
        UserGroup group = em.find(UserGroup.class,groupId);
        getAllowedFileGroups().add(group);
       
        
    }
    
     public void removeAllowedFileUser(Long userId) {
        VDCUser user = em.find(VDCUser.class,userId);
        getAllowedFileUsers().remove(user);
      
    }
    
    public void  addAllowedFileUser(Long userId) {
        VDCUser user = em.find(VDCUser.class,userId);
        getAllowedFileUsers().add(user);
     
        
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
    
    List allowedFileGroups = new ArrayList();
    List allowedFileUsers = new ArrayList();

    public List getAllowedFileGroups() {
        return allowedFileGroups;
    }



    public List getAllowedFileUsers() {
        return allowedFileUsers;
    }


    
    

}
