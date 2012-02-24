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
 * StudyField.java
 *
 * Created on July 28, 2006, 3:13 PM
 *
 */

package edu.harvard.iq.dvn.core.study;

import java.util.Collection;

import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCCollection;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class StudyField implements Serializable {
    private String name;
    private String title;
    private String description;
    private boolean dcmField;
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
    
    public boolean isDcmField() {
        return dcmField;
    }

    public void setDcmField(boolean dcmField) {
        this.dcmField = dcmField;
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
}
