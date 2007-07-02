/*
 * StudyDownload.java
 *
 * Created on April 26, 2007, 6:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

/**
 * Entity class StudyDownload
 * 
 * @author gdurand
 */
@Entity
public class StudyDownload implements Serializable {

    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="studydownload_gen") 
    @SequenceGenerator(name="studydownload_gen", sequenceName="studydownload_id_seq")
    private Long id;

    
    
    /** Creates a new instance of StudyDownload */
    public StudyDownload() {
    }

    /**
     * Gets the id of this StudyDownload.
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the id of this StudyDownload to the specified value.
     * @param id the new id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns a hash code value for the object.  This implementation computes 
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    /**
     * Determines whether another object is equal to this StudyDownload.  The result is 
     * <code>true</code> if and only if the argument is not null and is a StudyDownload object that 
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyDownload)) {
            return false;
        }
        StudyDownload other = (StudyDownload)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs 
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "edu.harvard.hmdc.vdcnet.study.StudyDownload[id=" + id + "]";
    }
  
    
    @OneToOne
    private Study study;

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }    
    
    private int numberOfDownloads;

    public int getNumberOfDownloads() {
        return numberOfDownloads;
    }

    public void setNumberOfDownloads(int numberOfDownloads) {
        this.numberOfDownloads = numberOfDownloads;
    }
}
