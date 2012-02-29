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
 * UserGroupsPage.java
 *
 * Created on October 20, 2006, 4:24 PM
 * 
 */
package edu.harvard.iq.dvn.core.web.networkAdmin;

import edu.harvard.iq.dvn.core.vdc.OAISet;
import edu.harvard.iq.dvn.core.vdc.OAISetServiceLocal;
import edu.harvard.iq.dvn.core.web.common.StatusMessage;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.List;
import javax.ejb.EJB;
import com.icesoft.faces.component.ext.HtmlDataTable;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
@ViewScoped
@Named("OAISetsPage")
public class OAISetsPage extends VDCBaseBean implements java.io.Serializable  {
    @EJB OAISetServiceLocal oaiSetService;
 
    
    public void init() {
        super.init();
       
        initSetData();
    }
    
    private void initSetData() {
       
        oaiSets = oaiSetService.findAllOrderedSorted();
       
    }


    /**
     * Holds value of property goups.
     */
    List<OAISet> oaiSets; 
 
    /**
     * Getter for property goups.
     * @return Value of property goups.
     */
    public  List<OAISet> getOaiSets() { 
        return oaiSets;
    }

    public void deleteSet(ActionEvent ae) {
        OAISet oaiSet=(OAISet)dataTable.getRowData();
        
        oaiSetService.remove(oaiSet.getId());
        initSetData();  // Re-fetch list to reflect Delete action
        
        getVDCRenderBean().getFlash().put("successMessage", "Successfully deleted OAI set.");

    }

  
    /**
     * Holds value of property dataTable.
     */
    private HtmlDataTable dataTable;

    /**
     * Getter for property dataTable.
     * @return Value of property dataTable.
     */
    public HtmlDataTable getDataTable() {
        return this.dataTable;
    }

    /**
     * Setter for property dataTable.
     * @param dataTable New value of property dataTable.
     */
    public void setDataTable(HtmlDataTable dataTable) {
        this.dataTable = dataTable;
    }
   
}

