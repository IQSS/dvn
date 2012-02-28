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
 * HarvestSitesPage.java
 *
 * Created on April 5, 2007, 10:20 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.admin;

import edu.harvard.iq.dvn.core.study.ControlledVocabulary;
import edu.harvard.iq.dvn.core.study.TemplateServiceLocal;
import java.util.List;
import javax.ejb.EJB;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author Ellen Kraffmiller
 */
@Named("ManageControlledVocabularyPage")
@ViewScoped
public class ManageControlledVocabularyPage extends VDCBaseBean implements java.io.Serializable  {
    @EJB TemplateServiceLocal templateService;
    
    private List<ControlledVocabulary> controlledVocabularyList;
    
    /** Creates a new instance of HarvestSitesPage */
    public ManageControlledVocabularyPage() {
    }
    
    public void init(){
        controlledVocabularyList = templateService.getNetworkControlledVocabulary();
    }

    public List<ControlledVocabulary> getControlledVocabularyList() {
        return controlledVocabularyList;
    }

    public void setControlledVocabularyList(List<ControlledVocabulary> controlledVocabularyList) {
        this.controlledVocabularyList = controlledVocabularyList;
    }

    
    
}
