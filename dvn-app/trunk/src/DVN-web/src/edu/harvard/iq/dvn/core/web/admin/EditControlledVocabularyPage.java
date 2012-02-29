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

    public List<String> getSelectedControlledVocabularyValues() {
        return selectedControlledVocabularyValues;
    }

    public void setSelectedControlledVocabularyValues(List<String> selectedControlledVocabularyValues) {
        this.selectedControlledVocabularyValues = selectedControlledVocabularyValues;
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