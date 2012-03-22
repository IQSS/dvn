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

