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
 * VariablePage.java
 *
 * Created on March 14, 2007, 5:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.study;

import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author gdurand
 */
@Named("VariablePage")
@ViewScoped
public class VariablePage extends VDCBaseBean implements java.io.Serializable  {
    
    @EJB VariableServiceLocal varService;
    
    /** Creates a new instance of VariablePage */
    public VariablePage() {
    }

    private Long dvId;
    private DataVariable variable;
    private StudyUI studyUI;
    
    public void init() {
        super.init();
        
        // we need to create the studyServiceBean
        if (dvId != null) {
            setVariable(varService.getDataVariable(getDvId()));
            
        } else {
            // WE SHOULD HAVE A DataVariable ID, throw an error
            System.out.println("ERROR: in variablePage, without a dvId");
        }
        studyUI = new StudyUI(variable.getDataTable().getStudyFile().getStudy());
        
    }

    public Long getDvId() {
        return dvId;
    }

    public void setDvId(Long dvId) {
        this.dvId = dvId;
    }

    public DataVariable getVariable() {
        return variable;
    }

    public void setVariable(DataVariable variable) {
        this.variable = variable;
    }

    public StudyUI getStudyUI() {
        return studyUI;
    }

    public void setStudyUI(StudyUI studyUI) {
        this.studyUI = studyUI;
    }
    
}
