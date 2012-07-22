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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.admin;

import edu.harvard.iq.dvn.core.study.ControlledVocabulary;
import edu.harvard.iq.dvn.core.study.ControlledVocabularyValue;
import edu.harvard.iq.dvn.core.study.TemplateServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;

/**
 *
 * @author gdurand
 */

@Named
@ViewScoped
public class EditControlledVocabularyPage extends VDCBaseBean implements java.io.Serializable  {

    @EJB TemplateServiceLocal templateService;
       
    private Long cvId;
    private ControlledVocabulary controlledVocabulary;   
    private String newControlledVocabularyValue;
    private List<String> selectedControlledVocabularyValues;
    private boolean showPopup = false;
    
    public void init(){
        if (cvId != null) {
            controlledVocabulary = templateService.getControlledVocabulary(cvId);
        } else {
           controlledVocabulary = new ControlledVocabulary();
           controlledVocabulary.setControlledVocabularyValues(new ArrayList());
        }  
    }

    public ControlledVocabulary getControlledVocabulary() {
        return controlledVocabulary;
    }

    public void setControlledVocabulary(ControlledVocabulary controlledVocabulary) {
        this.controlledVocabulary = controlledVocabulary;
    }

    public Long getCvId() {
        return cvId;
    }

    public void setCvId(Long cvId) {
        this.cvId = cvId;
    }

    public String getNewControlledVocabularyValue() {
        return newControlledVocabularyValue;
    }

    public void setNewControlledVocabularyValue(String newControlledVocabularyValue) {
        this.newControlledVocabularyValue = newControlledVocabularyValue;
    }
    
    public boolean isShowPopup() {
        return showPopup;
    }

    public void setShowPopup(boolean showPopup) {
        this.showPopup = showPopup;
    }
    
    public List<String> getSelectedControlledVocabularyValues() {
        return selectedControlledVocabularyValues;
    }

    public void setSelectedControlledVocabularyValues(List<String> selectedControlledVocabularyValues) {
        this.selectedControlledVocabularyValues = selectedControlledVocabularyValues;
    }


    
    public void exit() {
    }
    public List<SelectItem> getControlledVocabularySelectItems() {
        List selectItems = new ArrayList();
            for (ControlledVocabularyValue cvv : controlledVocabulary.getControlledVocabularyValues()) {
                SelectItem si = new SelectItem(cvv.getValue());
                selectItems.add(si);
            }
        return selectItems;
    }
    
    public void addControlledVocabularyValue() {
        if(newControlledVocabularyValue.isEmpty()){
            return;
        }
        for (ControlledVocabularyValue cvv: controlledVocabulary.getControlledVocabularyValues() ){
            if (newControlledVocabularyValue.equals(cvv.getValue())) {
                return;
            }
        }
               
        ControlledVocabularyValue cvv = new ControlledVocabularyValue();
        cvv.setControlledVocabulary(controlledVocabulary);
        cvv.setValue(newControlledVocabularyValue);

        controlledVocabulary.getControlledVocabularyValues().add(cvv);
        Collections.sort(controlledVocabulary.getControlledVocabularyValues());
    }
            
    public void removeControlledVocabularyValues() {              
        for (String selectedCVV: selectedControlledVocabularyValues ){
            for (Iterator<ControlledVocabularyValue> it = controlledVocabulary.getControlledVocabularyValues().iterator(); it.hasNext();) {
                if (selectedCVV.equals(it.next().getValue())) {
                    it.remove();
                }
            }
        }       
    }

    public String save_action() {
            boolean isNewControlledVocabulary = controlledVocabulary.getId() == null;
            templateService.saveControlledVocabulary(controlledVocabulary);
            
            if (isNewControlledVocabulary) {
                getVDCRenderBean().getFlash().put("successMessage", "Successfully added new Controlled Vocabulary.");
            } else {
                getVDCRenderBean().getFlash().put("successMessage", "Successfully updated Controlled Vocabulary.");
            }
            return "/admin/ManageControlledVocabularyPage?faces-redirect=true";
    }

}