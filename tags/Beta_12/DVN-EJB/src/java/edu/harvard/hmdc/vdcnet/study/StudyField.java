/*
 * StudyField.java
 *
 * Created on July 28, 2006, 3:13 PM
 *
 */

package edu.harvard.hmdc.vdcnet.study;

import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollection;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToMany;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class StudyField {
    private String name;
    private String title;
    private String description;
    
   
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

    /**
     * Holds value of property id.
     */
   @SequenceGenerator(name="studyfield_gen", sequenceName="studyfield_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="studyfield_gen") 
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
    
}
