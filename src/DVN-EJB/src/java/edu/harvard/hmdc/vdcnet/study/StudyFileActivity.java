/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

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
 * @author Gustavo
 */
@Entity
public class StudyFileActivity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="studyfileactivity_gen") 
    @SequenceGenerator(name="studyfileactivity_gen", sequenceName="studyfileactivity_id_seq")    
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof StudyFileActivity)) {
            return false;
        }
        StudyFileActivity other = (StudyFileActivity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.harvard.hmdc.vdcnet.study.StudyFileActivity[id=" + id + "]";
    }
    
    @OneToOne StudyFile studyFile;
    @ManyToOne Study study;
    private int downloadCount;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastDownloadTime;

    public StudyFile getStudyFile() {
        return studyFile;
    }

    public void setStudyFile(StudyFile studyFile) {
        this.studyFile = studyFile;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Date getLastDownloadTime() {
        return lastDownloadTime;
    }

    public void setLastDownloadTime(Date lastDownloadTime) {
        this.lastDownloadTime = lastDownloadTime;
    }
    

}
