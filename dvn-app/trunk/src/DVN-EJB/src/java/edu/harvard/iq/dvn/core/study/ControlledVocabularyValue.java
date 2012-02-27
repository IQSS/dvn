/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

/**
 *
 * @author gdurand
 */
@Entity
public class ControlledVocabularyValue implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)    
    private Long id;
    
    @Column(columnDefinition="TEXT") 
    private String value;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    
    @ManyToOne
    @JoinColumn(nullable=false)
    private ControlledVocabulary controlledVocabulary;

    public ControlledVocabulary getControlledVocabulary() { return controlledVocabulary; }
    public void setControlledVocabulary(ControlledVocabulary controlledVocabulary) { this.controlledVocabulary=controlledVocabulary; }    
    
    
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ControlledVocabularyValue)) {
            return false;
        }
        ControlledVocabularyValue other = (ControlledVocabularyValue) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.harvard.iq.dvn.core.study.ControlledVocabularyValue[ id=" + id + " ]";
    }
    
}
