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
 * TemplateField.java
 *
 * Created on August 2, 2006, 4:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.web.study.TemplateFieldControlledVocabulary;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class TemplateField implements java.io.Serializable {


    /**
     * Creates a new instance of TemplateField
     */
    public TemplateField() {
    }

    
    private String defaultValue;

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Holds value of property selectValues.
     */
    @OneToMany (mappedBy="templateField", cascade=CascadeType.PERSIST)
    private java.util.Collection<TemplateFieldSelectValue> selectValues;

    /**
     * Getter for property selectValues.
     * @return Value of property selectValues.
     */
    public java.util.Collection<TemplateFieldSelectValue> getSelectValues() {
        return this.selectValues;
    }

    /**
     * Setter for property selectValues.
     * @param selectValues New value of property selectValues.
     */
    public void setSelectValues(java.util.Collection<TemplateFieldSelectValue> selectValues) {
        this.selectValues = selectValues;
    }

    
    private String fieldInputLevelString;

    public String getFieldInputLevelString() {
        return fieldInputLevelString;
    }

    public void setFieldInputLevelString(String fieldInputLevelString) {
        this.fieldInputLevelString = fieldInputLevelString;
    }

    /**
     * Holds value of property template.
     */
    @ManyToOne
    @JoinColumn(nullable=false)
    private Template template;

    /**
     * Getter for property template.
     * @return Value of property template.
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * Setter for property template.
     * @param template New value of property template.
     */
    public void setTemplate(Template template) {
        this.template=template;
    }

    /**
     * Holds value of property id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Getter for property id.
     * @return Value of property id.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Setter for property id.
     * @param id New value of property id.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Holds value of property studyField.
     */
    @ManyToOne
    @JoinColumn(nullable=false, insertable = true)
    private StudyField studyField;

    /**
     * Getter for property studyField.
     * @return Value of property studyField.
     */
    public StudyField getStudyField() {
        return this.studyField;
    }

    /**
     * Setter for property studyField.
     * @param studyField New value of property studyField.
     */
    public void setStudyField(StudyField studyField) {
        this.studyField = studyField;
    }

    /*
    @OneToMany (mappedBy="templateField",  cascade={ CascadeType.REMOVE, CascadeType.MERGE,CascadeType.PERSIST})
    @OrderBy ("strValue")
    private List<TemplateFieldValue> templateFieldValues;

    public List<TemplateFieldValue> getTemplateFieldValues() {
        return templateFieldValues;
    }

    public void setTemplateFieldValues(List<TemplateFieldValue> templateFieldValues) {
        this.templateFieldValues = templateFieldValues;
    }
    
    public List<String> getTemplateFieldValueStrings(){
        List <String> retList = new ArrayList();
        for (TemplateFieldValue tfv: this.getTemplateFieldValues()){
            retList.add(tfv.getStrValue());
        }        
        return retList;       
    }

    public String getTemplateFieldValueSingleString(){
        List <String> retList = new ArrayList();
        for (TemplateFieldValue tfv: this.getTemplateFieldValues()){
            retList.add(tfv.getStrValue());
        }
        if (!retList.isEmpty()){
           return retList.get(0);  
        } else {
           return "";
        }              
    }
    
    public void initValues (){
        if (this.getTemplateFieldValues() == null || this.getTemplateFieldValues().isEmpty()){
            TemplateFieldValue elem = new TemplateFieldValue();
            elem.setTemplateField(this);
            elem.setMetadata(this.getTemplate().getMetadata());
            elem.setDisplayOrder(0);
            List values = new ArrayList();
            values.add(elem);
            this.setTemplateFieldValues(values);
        }

    }
    */

    @OneToMany (mappedBy="templateField", cascade={ CascadeType.REMOVE, CascadeType.MERGE,CascadeType.PERSIST})
    @OrderBy ("strValue")
    private List<TemplateFieldControlledVocabulary> templateFieldControlledVocabulary;

    public List<TemplateFieldControlledVocabulary> getTemplateFieldControlledVocabulary() {
        return templateFieldControlledVocabulary;
    }

    public void setTemplateFieldControlledVocabulary(List<TemplateFieldControlledVocabulary> templateFieldControlledVocabulary) {
        this.templateFieldControlledVocabulary = templateFieldControlledVocabulary;
    }

    public void initControlledVocabulary (){
        if (this.getTemplateFieldControlledVocabulary() == null || this.getTemplateFieldControlledVocabulary().isEmpty()){
            TemplateFieldControlledVocabulary elem = new TemplateFieldControlledVocabulary();
            elem.setTemplateField(this);
            elem.setMetadata(this.getTemplate().getMetadata());
            List vocab = new ArrayList();
            vocab.add(elem);
            this.setTemplateFieldControlledVocabulary(vocab);
        }
    }
    
    public List<String> getControlledVocabularyStrings(){
        List <String> retList = new ArrayList();
        if(!this.isAllowMultiples()){
             retList.add("--No Value--");
        }

        for (TemplateFieldControlledVocabulary tfcv: this.templateFieldControlledVocabulary){
            retList.add(tfcv.getStrValue());
        }
        return retList;
    }

  /**
     * Holds value of property version.
     */
    @Version
    private Long version; 

    /**
     * Getter for property version.
     * @return Value of property version.
     */
    public Long getVersion() {
        return this.version;
    }

    /**
     * Setter for property version.
     * @param version New value of property version.
     */
    public void setVersion(Long version) {
        this.version = version;
    }
    

    public boolean isAllowMultiples() {
        return this.studyField.isAllowMultiples();
    }


    
        /**
     * Holds value of property allow multiples.
     */
    private Long dcmSortOrder; 

    /**
     * Getter for property allow multiples.
     * @return Value of property allow multiples.
     */
    public Long getDcmSortOrder() {
        return this.dcmSortOrder;
    }

    /**
     * Setter for property allow multiples.
     * @param version New value of property allow multiples.
     */
    public void setdcmSortOrder(Long dcmSortOrder) {
        this.dcmSortOrder = dcmSortOrder;
    }
    
    public boolean isRequired() {
        return fieldInputLevelString.equals("required"); 
    }

    public boolean isRecommended() {
        return fieldInputLevelString.equals("recommended"); 
    }
    
    public boolean isOptional() {
        return fieldInputLevelString.equals("optional"); 
    }
    
    public boolean isHidden() {
        return fieldInputLevelString.equals("hidden"); 
    }
    
    public boolean isDisabled() {
        return fieldInputLevelString.equals("disabled"); 
    }
    
   public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TemplateField)) {
            return false;
        }
        TemplateField other = (TemplateField)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }            

}
