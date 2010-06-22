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
 * StudyKeyword.java
 *
 * Created on August 7, 2006, 9:54 AM
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
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class StudyTopicClass implements java.io.Serializable, MetadataFieldGroup {
    
    /** Creates a new instance of StudyKeyword */
    public StudyTopicClass() {
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
     * Holds value of property value.
     */
     @Column(columnDefinition="TEXT")
    private String value;

    /**
     * Getter for property value.
     * @return Value of property value.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Setter for property value.
     * @param value New value of property value.
     */
    public void setValue(String value) {
        this.value = value;
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
     * Holds value of property vocab.
     */
    private String vocab;

    /**
     * Getter for property vocab.
     * @return Value of property vocab.
     */
    public String getVocab() {
        return this.vocab;
    }

    /**
     * Setter for property vocab.
     * @param vocab New value of property vocab.
     */
    public void setVocab(String vocab) {
        this.vocab = vocab;
    }

    /**
     * Holds value of property vocabURI.
     */
    private String vocabURI;

    /**
     * Getter for property vocabURI.
     * @return Value of property vocabURI.
     */
    public String getVocabURI() {
        return this.vocabURI;
    }

    /**
     * Setter for property vocabURI.
     * @param vocabURI New value of property vocabURI.
     */
    public void setVocabURI(String vocabURI) {
        this.vocabURI = vocabURI;
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
    
    
    public boolean isEmpty() {
        return ((value==null || value.trim().equals(""))
            && (vocab==null || vocab.trim().equals(""))
            && (vocabURI==null || vocabURI.trim().equals("")));
    }
  public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyTopicClass)) {
            return false;
        }
        StudyTopicClass other = (StudyTopicClass)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }    
}
