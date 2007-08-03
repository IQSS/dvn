/*
 * FieldInputLevel.java
 *
 * Created on August 4, 2006, 3:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.GenerationType;

/**
 * Defines the type of input for a study field: 
 * Required, Recommended, Optional
 * @author Ellen Kraffmiller
 */
@Entity        
public class FieldInputLevel {
    @SequenceGenerator(name="fieldinputlevel_gen", sequenceName="fieldinputlevel_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="fieldinputlevel_gen") 
    private Long id;
    private String name;
    /** Creates a new instance of FieldInputLevel */
    public FieldInputLevel() {
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
     public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyLock)) {
            return false;
        }
        FieldInputLevel other = (FieldInputLevel)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
    
}
