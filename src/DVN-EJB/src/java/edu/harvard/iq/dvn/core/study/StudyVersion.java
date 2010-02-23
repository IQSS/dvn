/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.admin.VDCUser;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author gdurand
 */
@Entity
public class StudyVersion implements Serializable {
    private static final long serialVersionUID = 1L;

  public enum VersionState { DRAFT, IN_REVIEW, RELEASED, ARCHIVED};


   // public static final String VERSION_STATE_DRAFT = "Draft";
   // public static final String VERSION_STATE_IN_REVIEW = "In Review";
   // public static final String VERSION_STATE_RELEASED = "Released";

    public StudyVersion () {
        metadata = new Metadata();
        metadata.setStudyVersion(this);
    }

    private Long versionNumber;
    private String versionNote;
    
    @Enumerated(EnumType.STRING)
    private VersionState versionState;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(nullable=false)
    private Study study;
    @OneToOne(cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(nullable=false)
    private Metadata metadata;
    @OneToMany(mappedBy="studyVersion", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private List<FileMetadata> fileMetadatas;
    @OneToMany(mappedBy="studyVersion", cascade={CascadeType.REMOVE, CascadeType.PERSIST})
    private List<StudyComment> studyComments;
    @OneToMany(mappedBy="studyVersion", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private List<VersionContributor> versionContributors;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createTime;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastUpdateTime;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date releaseTime;

    public List<VersionContributor> getVersionContributors() {
        return versionContributors;
    }

    public void setVersionContributors(List<VersionContributor> versionContributors) {
        this.versionContributors = versionContributors;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getVersionNote() {
        return versionNote;
    }

    public void setVersionNote(String versionNote) {
        this.versionNote = versionNote;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

   
    public VersionState getVersionState() {
        return versionState;
    }

    public void setVersionState(VersionState versionState) {
        
        this.versionState = versionState;
    }

    public boolean isReleased() {
        return versionState.equals(VersionState.RELEASED);
    }

    public boolean isInReview() {
        return versionState.equals(VersionState.IN_REVIEW);
    }

    public boolean isDraft() {
         return versionState.equals(VersionState.DRAFT);
    }

    public boolean isWorkingCopy() {
        return (versionState.equals(VersionState.DRAFT) ||  versionState.equals(VersionState.IN_REVIEW)) ;
    }

    public boolean isArchived() {
         return versionState.equals(VersionState.ARCHIVED);
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
        metadata.setStudyVersion(this);
    }

    public List<FileMetadata> getFileMetadatas() {
        return fileMetadatas;
    }

    public void setFileMetadatas(List<FileMetadata> fileMetadatas) {
        this.fileMetadatas = fileMetadatas;
    }

    public List<StudyComment> getStudyComments() {
        return studyComments;
    }

    public void setStudyComments(List<StudyComment> studyComments) {
        this.studyComments = studyComments;
    }

    /**
     * Update versionContributors List:
     * If the user who made the edit has already contributed to this version,
     * update his lastUpdated time.  Else, add him as a new contributor
     */
    public void updateVersionContributors(VDCUser user) {
            boolean foundUser = false;
            for (VersionContributor vc : versionContributors) {
                if (vc.getContributor().equals(user)) {
                  vc.setLastUpdateTime(new Date());
                  foundUser=true;
                }
            }
            if (!foundUser) {
                VersionContributor newContrib = new VersionContributor();
                newContrib.setContributor(user);
                newContrib.setLastUpdateTime(new Date());
                newContrib.setStudyVersion(this);
                versionContributors.add(newContrib);

            }
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
        if (!(object instanceof StudyVersion)) {
            return false;
        }
        StudyVersion other = (StudyVersion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.harvard.iq.dvn.core.study.StudyVersion[id=" + id + "]";
    }

}
