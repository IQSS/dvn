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
 * StudyField.java
 *
 * Created on July 28, 2006, 3:13 PM
 *
 */
package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.util.StringUtil;
import java.util.Collection;

import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCCollection;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class StudyField implements Serializable {
    private String name;    // This is the internal, DDI-like name, no spaces, etc.
    private String title;   // A longer, human-friendlier name - punctuation allowed
    private String description; // A user-friendly Description; will be used for 
                                // mouse-overs, etc. 
    private boolean customField;
    private String fieldType;
    
   
    /** Creates a new instance of StudyField */
    public StudyField() {
    }

    /**
     * Holds value of property displayOrder.
     */
    private int displayOrder;

    /**
     * Getter for property order.
     * @return Value of property order.
     */
    public int getDisplayOrder() {
        return this.displayOrder;
    }

    /**
     * Setter for property order.
     * @param order New value of property order.
     */
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isCustomField() {
        return customField;
    }

    public void setCustomField(boolean customField) {
        this.customField = customField;
    }
    
        
    /**
     * Holds value of property allow multiples.
     */
    private boolean allowMultiples; 

    /**
     * Getter for property allow multiples.
     * @return Value of property allow multiples.
     */
    public boolean isAllowMultiples() {
        return this.allowMultiples;
    }

    /**
     * Setter for property allow multiples.
     * @param version New value of property allow multiples.
     */
    public void setAllowMultiples(boolean allowMultiples) {
        this.allowMultiples = allowMultiples;
    }

     public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
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


    
    @OneToMany(mappedBy = "parentStudyField", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("name ASC")
    private Collection<StudyField> childStudyFields;

    public Collection<StudyField> getChildStudyFields() {
        return this.childStudyFields;
    }

    public void setChildStudyFields(Collection<StudyField> childStudyFields) {
        this.childStudyFields = childStudyFields;
    }
    @ManyToOne(cascade = CascadeType.MERGE)
    private StudyField parentStudyField;

    public StudyField getParentStudyField() {
        return parentStudyField;
    }

    public void setParentStudyField(StudyField parentStudyField) {
        this.parentStudyField = parentStudyField;
    }
    
    
    /**
     * Holds value of property studies. 
     */
    @ManyToMany(mappedBy="summaryFields",cascade={CascadeType.REMOVE })
    private Collection<Study> studies;

    @ManyToMany(mappedBy="advSearchFields",cascade={CascadeType.REMOVE })
    private Collection<VDC> advSearchFieldVDCs;

   @ManyToMany(mappedBy="searchResultFields",cascade={CascadeType.REMOVE })
    private Collection<VDC> searchResultFieldVDCs;
   
   @ManyToMany(mappedBy="anySearchFields",cascade={CascadeType.REMOVE })
    private Collection<VDC> anySearchFieldVDCs;
    
   @ManyToMany(mappedBy="summaryFields",cascade={CascadeType.REMOVE })
    private Collection<VDC> summaryFieldVDCs;

   @ManyToMany(mappedBy="advSearchFields",cascade={CascadeType.REMOVE })
    private Collection<VDCCollection> advSearchFieldColls;

   @ManyToMany(mappedBy="searchResultFields",cascade={CascadeType.REMOVE })
    private Collection<VDCCollection> searchResultFieldColls;
   
   @ManyToMany(mappedBy="anySearchFields",cascade={CascadeType.REMOVE })
    private Collection<VDCCollection> anySearchFieldColls;

    @OneToMany(mappedBy="studyField")
    private Collection <TemplateField> templateFields;

    /**
     * Holds value of property basicSearchField.
     */
    private boolean basicSearchField;

    /**
     * Getter for property basicSearchField.
     * @return Value of property basicSearchField.
     */
    public boolean isBasicSearchField() {
        return this.basicSearchField;
    }

    /**
     * Setter for property basicSearchField.
     * @param basicSearchField New value of property basicSearchField.
     */
    public void setBasicSearchField(boolean basicSearchField) {
        this.basicSearchField = basicSearchField;
    }

    /**
     * Holds value of property advancedSearchField.
     */
    private boolean advancedSearchField;

    /**
     * Getter for property advancedSearchField.
     * @return Value of property advancedSearchField.
     */
    public boolean isAdvancedSearchField() {
        return this.advancedSearchField;
    }

    /**
     * Setter for property advancedSearchField.
     * @param advancedSearchField New value of property advancedSearchField.
     */
    public void setAdvancedSearchField(boolean advancedSearchField) {
        this.advancedSearchField = advancedSearchField;
    }

    /**
     * Holds value of property searchResultField.
     */
    private boolean searchResultField;

    /**
     * Getter for property searchResultField.
     * @return Value of property searchResultField.
     */
    public boolean isSearchResultField() {
        return this.searchResultField;
    }

    /**
     * Setter for property searchResultField.
     * @param searchResultField New value of property searchResultField.
     */
    public void setSearchResultField(boolean searchResultField) {
        this.searchResultField = searchResultField;
    }

 public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyField)) {
            return false;
        }
        StudyField other = (StudyField)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }      
    

    @OneToMany (mappedBy="studyField",  cascade={ CascadeType.REMOVE, CascadeType.MERGE,CascadeType.PERSIST})
    private List<StudyFieldValue> studyFieldValues;

    public List<StudyFieldValue> getStudyFieldValues() {
        return studyFieldValues;
    }  
       
    public void setStudyFieldValues(List<StudyFieldValue> studyFieldValues) {
        this.studyFieldValues = studyFieldValues;
    }      
    
    // helper methods for getting the internal string values
    public List<String> getStudyFieldValueStrings() {
        List <String> retString = new ArrayList();
        for (StudyFieldValue sfv:studyFieldValues){
            if ( !StringUtil.isEmpty(sfv.getStrValue()) ) {
                retString.add(sfv.getStrValue());
            }
        }
        return retString;
    }
    
    public String getStudyFieldValueSingleString() {
        return studyFieldValues.size() > 0 ? studyFieldValues.get(0).getStrValue() : "";
    }
    
    public void setStudyFieldValueStrings(List<String> newValList) {}

    public void setStudyFieldValueSingleString(String newVal) {}
    
      
}
