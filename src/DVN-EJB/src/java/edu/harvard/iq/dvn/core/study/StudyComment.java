/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
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
