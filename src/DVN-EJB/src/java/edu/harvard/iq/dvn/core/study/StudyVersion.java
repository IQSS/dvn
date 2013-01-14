/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.admin.VDCUser;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.persistence.*;

/**
 *
 * @author gdurand
 */
@Entity
public class StudyVersion implements Serializable {
    private static final long serialVersionUID = 1L;

  // IMPORTANT: If you add a new value to this enum, you will also have to modify the
  // StudyVersionsFragment.xhtml in order to display the correct value from a Resource Bundle
  public enum VersionState { DRAFT, IN_REVIEW, RELEASED, ARCHIVED, DEACCESSIONED};



    public StudyVersion () {
        metadata = new Metadata();
        metadata.setStudyVersion(this);
        
       
    }

    private Long versionNumber;
    public static final int VERSION_NOTE_MAX_LENGTH = 1000;
    @Column(length=VERSION_NOTE_MAX_LENGTH)
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
    @OrderBy("category") // this is not our preferred ordering, which is with the AlphaNumericComparator, but does allow the files to be grouped by category
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
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date archiveTime;
    public static final int ARCHIVE_NOTE_MAX_LENGTH = 1000;
    @Column(length=ARCHIVE_NOTE_MAX_LENGTH)
    private String archiveNote;
    private String deaccessionLink;

    public Date getArchiveTime() {
        return archiveTime;
    }

    public void setArchiveTime(Date archiveTime) {
        this.archiveTime = archiveTime;
    }

    public String getArchiveNote() {
        return archiveNote;
    }

    public void setArchiveNote(String note) {
        if (note.length()>ARCHIVE_NOTE_MAX_LENGTH ) {
            throw new IllegalArgumentException("Error setting archiveNote: String length is greater than maximum ("+ ARCHIVE_NOTE_MAX_LENGTH + ")."
                   +"  StudyVersion id="+id+", archiveNote="+note);
        }
        this.archiveNote = note;
    }

    public String getDeaccessionLink() {
        return deaccessionLink;
    }

    public void setDeaccessionLink(String deaccessionLink) {
        this.deaccessionLink = deaccessionLink;
    }

    public GlobalId getDeaccessionLinkAsGlobalId() {
        return new GlobalId(deaccessionLink);
    }


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
        if (createTime==null) {
            createTime = lastUpdateTime;
        }
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

    public void setVersionNote(String note) {
        if (note != null &&  note.length()>VERSION_NOTE_MAX_LENGTH ) {
            throw new IllegalArgumentException("Error setting versionNote: String length is greater than maximum ("+ VERSION_NOTE_MAX_LENGTH + ")."
                   +"  StudyVersion id="+id+", versionNote="+note);
        }
        this.versionNote = note;
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

    public boolean isDeaccessioned() {
         return versionState.equals(VersionState.DEACCESSIONED);
    }

    public boolean isRetiredCopy() {
        return (versionState.equals(VersionState.ARCHIVED) ||  versionState.equals(VersionState.DEACCESSIONED)) ;
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

    public String getNumberOfFiles() {
        if (this.fileMetadatas != null && this.fileMetadatas.size() != 0) {
            return "" + this.fileMetadatas.size();
        } else {
            return null;
        }
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
            if (versionContributors == null) {
                versionContributors = new ArrayList();
            }
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

    public List<String> getFileCategories() {
        List<String> fileCategories = new ArrayList();

        String currentCategory = null;
        for (FileMetadata fmd : this.getFileMetadatas()) { //fileMetadatas are grouped together based on @OrderBy annotation
            if (currentCategory == null || !currentCategory.equals(fmd.getCategory())) {
                currentCategory = fmd.getCategory();
                fileCategories.add(currentCategory);
            }
        }
        return fileCategories;
    }

    public boolean isLatestVersion() {
        return this.equals( this.getStudy().getLatestVersion() );
    }
}
