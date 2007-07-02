/*
 * StudyAccessRequest.java
 *
 * Created on October 19, 2006, 1:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class StudyAccessRequest implements Serializable {

    @SequenceGenerator(name="studyaccessrequest_gen", sequenceName="studyaccessrequest_id_seq") 
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="studyaccessrequest_gen")       
    private Long id;
    
    /** Creates a new instance of RoleRequest */
    public StudyAccessRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

   

 

    public String toString() {
        return "edu.harvard.hmdc.vdcnet.vdc.RoleRequest[id=" + id + "]";
    }

    /**
     * Holds value of property study.
     */
    @ManyToOne
    private Study study;

    /**
     * Getter for property study.
     * @return Value of property study.
     */
    public Study getStudy() {
        return this.study;
    }

    /**
     * Setter for property study.
     * @param role New value of property study.
     */
    public void setStudy(Study study) {
        this.study = study;
    }

 
    /**
     * Holds value of property vdcUser.
     */
    @ManyToOne
    private VDCUser vdcUser;

    /**
     * Getter for property vdcUser.
     * @return Value of property vdcUser.
     */
    public VDCUser getVdcUser() {
        return this.vdcUser;
    }

    /**
     * Setter for property vdcUser.
     * @param vdcUser New value of property vdcUser.
     */
    public void setVdcUser(VDCUser vdcUser) {
        this.vdcUser = vdcUser;
    }
  public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyAccessRequest)) {
            return false;
        }
        StudyAccessRequest other = (StudyAccessRequest)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }   
}
