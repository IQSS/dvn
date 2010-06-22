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
 *  along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package edu.harvard.iq.dvn.core.web.networkAdmin;

import edu.harvard.iq.dvn.core.vdc.OAISet;
import edu.harvard.iq.dvn.core.vdc.OAISetServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;

/**
 *
 * @author Ellen Kraffmiller
 */
public class EditOAISetPage extends VDCBaseBean implements java.io.Serializable  {
    @EJB OAISetServiceLocal oaiSetService;
    public EditOAISetPage() {
    }
    
    public void init() {
        super.init();
        oaiSet = new OAISet();
        if ( !isFromPage("EditOAISetPage") && oaiSetId!=null) {
            oaiSet = oaiSetService.findById(oaiSetId);
        }
    }

    public OAISet getOaiSet() {
        return oaiSet;
    }

    public void setOaiSet(OAISet oaiSet) {
        this.oaiSet = oaiSet;
    }

    public Long getOaiSetId() {
        return oaiSetId;
    }

    public void setOaiSetId(Long oaiSetId) {
        this.oaiSetId = oaiSetId;
    }
    
    public String save() {
      
               
            oaiSetService.update(oaiSet);
             return "success";
     
    }    
    
    public String cancel() {
        return "cancel";
    }
    
    OAISet oaiSet;
    Long oaiSetId;
    
      private String SUCCESS_MESSAGE = new String("Update Successful!");
    /**
     * Holds value of property success.
     */
    private boolean success;

    /**
     * Getter for property success.
     * @return Value of property success.
     */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * Setter for property success.
     * @param success New value of property success.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }  
    
}
