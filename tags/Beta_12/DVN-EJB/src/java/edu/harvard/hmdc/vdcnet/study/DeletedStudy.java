/*
 * DeletedStudy.java
 *
 * Created on March 5, 2007, 2:53 PM
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

/**
 * Entity class DeletedStudy
 * 
 * 
 * @author Ellen Kraffmiller
 */
@Entity
public class DeletedStudy implements Serializable {

    @Id
    
    private Long id;
    
    /**
     * Creates a new instance of DeletedStudy
     */
    public DeletedStudy() {
    }

    /**
     * Gets the id of this DeletedStudy.
     * 
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the id of this DeletedStudy to the specified value.
     * 
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
     * Determines whether another object is equal to this DeletedStudy.  The result is 
     * <code>true</code> if and only if the argument is not null and is a DeletedStudy object that 
     * has the same id field values as this object.
     * 
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DeletedStudy)) {
            return false;
        }
        DeletedStudy other = (DeletedStudy)object;
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
        return "edu.harvard.hmdc.vdcnet.study.ExportDeletedStudy[id=" + id + "]";
    }

    /**
     * Holds value of property authority.
     */
    private String authority;

    /**
     * Getter for property authority.
     * @return Value of property authority.
     */
    public String getAuthority() {
        return this.authority;
    }

    /**
     * Setter for property authority.
     * @param authority New value of property authority.
     */
    public void setAuthority(String authority) {
        this.authority = authority;
    }

    /**
     * Holds value of property studyId.
     */
    private String studyId;

    /**
     * Getter for property studyId.
     * @return Value of property studyId.
     */
    public String getStudyId() {
        return this.studyId;
    }

    /**
     * Setter for property studyId.
     * @param studyId New value of property studyId.
     */
    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }
    
    
    
}
