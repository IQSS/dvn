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
 * StudyAuthor.java
 *
 * Created on August 7, 2006, 9:49 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class StudyAuthor implements java.io.Serializable, MetadataFieldGroup {
    
    /** Creates a new instance of StudyAuthor */
    public StudyAuthor() {
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
     * Holds value of property name.
     */
    private String name;

    /**
     * Getter for property value.
     * @return Value of property value.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for property value.
     * @param value New value of property value.
     */
    public void setName(String name) {
        this.name = name;
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

    /**
     * Holds value of property metadata.
     */
    @ManyToOne 
    @JoinColumn(nullable=false)
    private Metadata metadata;

     public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
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

    /**
     * Holds value of property affiliation.
     */
    private String affiliation;

    /**
     * Getter for property affiliation.
     * @return Value of property affiliation.
     */
    public String getAffiliation() {
        return this.affiliation;
    }

    /**
     * Setter for property affiliation.
     * @param affiliation New value of property affiliation.
     */
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }
    
    private String idType;

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }
    
    private String idValue;
    
    
    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
        if (!this.idValue.isEmpty()){
            setIdType("ORCID");
        } else {
            setIdType("");
        }
    }
       
    public boolean isEmpty() {
        return ( (affiliation==null || affiliation.trim().equals(""))
            && (name==null || name.trim().equals(""))
           );
    }
    
     public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyAuthor)) {
            return false;
        }
        StudyAuthor other = (StudyAuthor)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }


}
