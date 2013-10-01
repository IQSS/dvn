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
package edu.harvard.iq.dvn.core.study;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity
public class StudyFieldValue implements Serializable, MetadataFieldGroup {

    public StudyFieldValue () {
    }
    
    public StudyFieldValue(StudyField sf, Metadata m, String val) {
        setStudyField(sf);
        setMetadata(m);
        setStrValue(val);    
    }    
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
     /**
     * Holds value of property studyField.
     */
    @ManyToOne
    @JoinColumn(nullable=false)
    private StudyField studyField;

    /**
     * Getter for property template.
     * @return Value of property template.
     */
    public StudyField getStudyField() {
        return studyField;
    }

    /**
     * Setter for property template.
     * @param template New value of property template.
     */
    public void setStudyField(StudyField studyField) {
        this.studyField=studyField;
    }

    
    /**
     * Holds value of property Metadata.
     */
    @ManyToOne
    @JoinColumn(nullable=false)
    private Metadata metadata;

    /**
     * Getter for property template.
     * @return Value of property template.
     */
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * Setter for property template.
     * @param template New value of property template.
     */
    public void setMetadata(Metadata metadata) {
        this.metadata=metadata;
    }
    
    
    @Column(columnDefinition="TEXT") 
    private String strValue;

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
        
    }
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyFieldValue)) {
            return false;
        }
        StudyFieldValue other = (StudyFieldValue) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.harvard.iq.dvn.core.study.StudyFieldValue[ id=" + id + " ]";
    }
    
     public boolean isEmpty() {
        return ((strValue==null || strValue.trim().equals("")));
    }
    
    

    private int displayOrder;
    public int getDisplayOrder() { return this.displayOrder;}
    public void setDisplayOrder(int displayOrder) {this.displayOrder = displayOrder;}    
}
