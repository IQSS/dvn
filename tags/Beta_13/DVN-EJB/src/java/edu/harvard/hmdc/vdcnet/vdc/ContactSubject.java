/*
 * ReviewState.java
 *
 * Created on July 28, 2006, 2:46 PM
 *
 */

package edu.harvard.hmdc.vdcnet.vdc;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.*;

/**
 *  Pre-defined email subject text for contacting an administrator of
 *  VDC/VDCNetword
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class ContactSubject {
    private String subjectText;
    
    /** Creates a new instance of ReviewState */
    public ContactSubject() {
    }

    public String getSubjectText() {
        return subjectText;
    }

    public void setSubjectText(String subjectText) {
        this.subjectText = subjectText;
    }

    /**
     * Holds value of property id.
     */
    @SequenceGenerator(name="contactsubject_gen", sequenceName="contactsubject_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="contactsubject_gen")    
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
     * Holds value of property vdc.
     */
    private VDC vdc;

    /**
     * Getter for property vdc.
     * @return Value of property vdc.
     */
    public VDC getVdc() {
        return this.vdc;
    }

    /**
     * Setter for property vdc.
     * @param vdc New value of property vdc.
     */
    public void setVdc(VDC vdc) {
        this.vdc = vdc;
    }
     public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ContactSubject)) {
            return false;
        }
        ContactSubject other = (ContactSubject)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }      
}
