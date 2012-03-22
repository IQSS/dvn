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
