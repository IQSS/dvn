/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;


/**
 *
 * @author gdurand
 */
@Entity
public class StudyVersion implements Serializable {
    private static final long serialVersionUID = 1L;

    public StudyVersion () {
        metadata = new Metadata();
        metadata.setStudyVersion(this);
    }

    private Long versionNumber;
    private String versionNote;
    private String versionState;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(nullable=false)
    private Study study;
    @OneToOne(cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(nullable=false)
    private Metadata metadata;
    @OneToMany(mappedBy="studyVersion", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private List<FileMetadata> fileMetadatas;
    @OneToMany(mappedBy="studyVersion", cascade={CascadeType.REMOVE, CascadeType.PERSIST})
    private List<StudyComment> studyComments;

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

    public String getVersionState() {
        return versionState;
    }

    public void setVersionState(String versionState) {
        this.versionState = versionState;
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
