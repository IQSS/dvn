/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.study.StudyVersion.VersionState;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;


/**
 *
 * @author ekraffmiller
 */
@Entity
public class VersionContributor implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(nullable=false)
    private StudyVersion studyVersion;

    @ManyToOne
    @JoinColumn(nullable=false)
    private VDCUser contributor;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastUpdateTime;

 
   public VDCUser getContributor() {
        return contributor;
    }

    public void setContributor(VDCUser contributor) {
        this.contributor = contributor;
    }

    public StudyVersion getStudyVersion() {
        return studyVersion;
    }

    public void setStudyVersion(StudyVersion studyVersion) {
        this.studyVersion = studyVersion;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }



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
        if (!(object instanceof VersionContributor)) {
            return false;
        }
        VersionContributor other = (VersionContributor) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.harvard.iq.dvn.core.study.VersionContributor[id=" + id + "]";
    }

}
