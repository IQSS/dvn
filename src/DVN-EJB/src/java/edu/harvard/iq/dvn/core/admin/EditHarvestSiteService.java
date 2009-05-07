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
