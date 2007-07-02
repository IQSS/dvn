/*
 * StudyLock.java
 *
 * Created on March 22, 2007, 4:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author gdurand
 */
@Entity
public class StudyLock implements Serializable {

    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="studylock_gen") 
    @SequenceGenerator(name="studylock_gen", sequenceName="studylock_id_seq")
    private Long id;
    
    /** Creates a new instance of StudyLock */
    public StudyLock() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        StudyLock other = (StudyLock)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    public String toString() {
        return "edu.harvard.hmdc.vdcnet.study.StudyLock[id=" + id + "]";
    }
    
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date startTime;    

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
 
    
    
    @OneToOne
    private Study study;

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }
 
    @ManyToOne
    private VDCUser user;    

    public VDCUser getUser() {
        return user;
    }

    public void setUser(VDCUser user) {
        this.user = user;
    }

    private String detail;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
    
    
  
}
