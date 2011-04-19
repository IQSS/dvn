/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

/**
 *
 * @author gdurand
 */
@Entity
public class FileMetadata implements Serializable {
    private static final long serialVersionUID = 1L;

    private String label = "";
    @Column(columnDefinition = "TEXT")
    private String description = "";
    @Column(columnDefinition="TEXT")
    private String category = "";

    @ManyToOne
    @JoinColumn(nullable=false)
    private StudyVersion studyVersion;
    @ManyToOne
    @JoinColumn(nullable=false)
    private StudyFile studyFile;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public StudyVersion getStudyVersion() {
        return studyVersion;
    }

    public void setStudyVersion(StudyVersion studyVersion) {
        this.studyVersion = studyVersion;
    }



    public StudyFile getStudyFile() {
        return studyFile;
    }

    public void setStudyFile(StudyFile studyFile) {
        this.studyFile = studyFile;
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
        this.version = version;
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
        if (!(object instanceof FileMetadata)) {
            return false;
        }
        FileMetadata other = (FileMetadata) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.harvard.iq.dvn.core.study.FileMetadata[id=" + id + "]";
    }

}
