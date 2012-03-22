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
 * EditVDCPrivilegesService.java
 *
 * Created on October 23, 2006, 4:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.harvest.SetDetailBean;
import edu.harvard.iq.dvn.core.vdc.HarvestingDataverse;

import java.util.List;
import javax.ejb.Remove;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/** 
 *
 * @author Ellen Kraffmiller
 */
public interface EditHarvestSiteService extends java.io.Serializable  {
    public static final String EDIT_MODE_CREATE = "create";
    public static final String EDIT_MODE_UPDATE = "update";

    /**
     * Remove this Stateful Session bean from the EJB Container without 
     * saving updates to the database.
     */
    @Remove
    void cancel();

    
    HarvestingDataverse getHarvestingDataverse();
    
    public Long getSelectedHandlePrefixId();
    public void setSelectedHandlePrefixId(Long selectedHandlePrefixId);
    
    public Long getSelectedMetadataPrefixId();
    public void setSelectedMetadataPrefixId(Long selectedMetadataPrefixId);
   
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save(Long userId, String name, String alias, boolean filesRestricted, String dtype, String affiliation);

   
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
    
    public String getEditMode();
    
    public List getAllowedFileGroups();
    public List getAllowedFileUsers();
    
}
