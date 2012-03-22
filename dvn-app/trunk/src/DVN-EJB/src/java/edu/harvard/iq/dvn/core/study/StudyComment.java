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
package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.admin.VDCUser;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author roberttreacy
 */
@Entity
public class StudyComment implements Serializable {

   
    public enum Status { OK, FLAGGED, DELETED };
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String comment;
    @ManyToOne
    @JoinColumn(nullable=false)
    private VDCUser commentCreator;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createTime;

    @ManyToOne
    @JoinColumn(nullable=false)
    private StudyVersion studyVersion;
    @ManyToMany
    @JoinTable(name = "FLAGGED_STUDY_COMMENTS",
    joinColumns = @JoinColumn(name = "study_comment_id"),
    inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Collection<VDCUser> flaggedByUsers;

    public StudyComment(){
        this.createTime = new Date();
    }

    public StudyComment(String comment, VDCUser commenter, StudyVersion studyVersion){
        this.commentCreator = commenter;
        this.comment = comment;
        this.studyVersion = studyVersion;
        this.createTime = new Date();
    }

     /**
     * @return the flaggedByUsers
     */
    public Collection<VDCUser> getFlaggedByUsers() {
        return flaggedByUsers;
    }

    /**
     * @param flaggedByUsers the flaggedByUsers to set
     */
    public void setFlaggedByUsers(Collection<VDCUser> flaggedByUsers) {
        this.flaggedByUsers = flaggedByUsers;
    }

    public VDCUser getFlaggedByUser() {
        VDCUser flaggedByUser = ((VDCUser)flaggedByUsers.toArray()[0]);
        return flaggedByUser;
    }

    /**
     * @return the studyVersion
     */
    public StudyVersion getStudyVersion() {
        return studyVersion;
    }

    /**
     * @param studyVersion the studyVersion to set
     */
    public void setStudyVersion(StudyVersion studyVersion) {
        this.studyVersion = studyVersion;
    }
    
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
        if (!(object instanceof StudyComment)) {
            return false;
        }
        StudyComment other = (StudyComment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.harvard.hmdc.vdcnet.study.StudyComment[id=" + id + "]";
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }
    private Status status;

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the commentCreator
     */
    public VDCUser getCommentCreator() {
        return commentCreator;
    }

    /**
     * @param commentCreator the commentCreator to set
     */
    public void setCommentCreator(VDCUser commentCreator) {
        this.commentCreator = commentCreator;
    }
}
