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

/*
 * Study.java
 *
 * Created on July 28, 2006, 2:44 PM
 *
 */

package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.admin.NetworkRoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.RoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.VDCRole;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.vdc.ReviewState;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJBException;
import javax.persistence.*;

import org.apache.commons.lang.builder.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"authority,protocol,studyId"}))
public class Study implements java.io.Serializable {

    @OneToOne(mappedBy = "study", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private StudyDownload studyDownload;    
    @OneToOne(mappedBy = "study", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private StudyLock studyLock;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createTime;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastUpdateTime;
    @ManyToOne
    private ReviewState reviewState;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastExportTime;
    @ManyToMany(cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST })
    private Collection<UserGroup> allowedGroups;
    @ManyToOne
    private VDCUser creator;
    @ManyToOne
    private VDCUser lastUpdater;
    private boolean isHarvested;
    @ManyToMany( cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST })
    private Collection<StudyField> summaryFields;
    @OneToMany(mappedBy="study", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<StudyAccessRequest> studyRequests;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastIndexTime;
    @OneToMany(mappedBy="study", cascade={CascadeType.REMOVE, CascadeType.PERSIST})
    private List<StudyVersion> studyVersions;

    
    public Study () {
        StudyVersion sv = new StudyVersion();
        studyVersions = new ArrayList<StudyVersion>();
        studyVersions.add( sv );
        sv.setStudy(this);
        
    }    
     public Study(VDC vdc, VDCUser creator, ReviewState reviewState) {
         this(vdc,creator,reviewState,null);
     }
        
    public Study(VDC vdc, VDCUser creator, ReviewState reviewState, Template initTemplate) {
        if (vdc==null) {
            throw new EJBException("Cannot create study with null VDC");
        }
        this.setOwner(vdc);
        if (initTemplate == null ){
            setTemplate(vdc.getDefaultTemplate());                    
        } else {
            this.setTemplate(initTemplate);
          
        }
        StudyVersion sv = new StudyVersion();
        studyVersions = new ArrayList<StudyVersion>();
        studyVersions.add( sv );
        sv.setStudy(this);

        template.getMetadata().copyMetadata(sv.getMetadata());
        if (vdc != null) {
            vdc.getOwnedStudies().add(this);
        }
        
        Date createDate = new Date();

        this.setCreator(creator);
        this.setCreateTime(createDate);

        this.setLastUpdater(creator);
        this.setLastUpdateTime(createDate);

        this.setReviewState( reviewState );
    }
       
    
    public String getGlobalId() {
        return protocol+":"+authority+"/"+getStudyId();
    }

    
    public String getHandleURL() {
         return "http://hdl.handle.net/"+authority+"/"+getStudyId();
    }

    private String studyId;
    
    public String getStudyId() {
        return studyId;
    }
    
    public void setStudyId(String studyId) {
        this.studyId = (studyId != null ? studyId.toUpperCase() : null);
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
    
    
    
    public boolean isInReview() {
        return reviewState.getName().equals(ReviewStateServiceLocal.REVIEW_STATE_IN_REVIEW);
    }
    
    public boolean isNew() {
        return reviewState.getName().equals(ReviewStateServiceLocal.REVIEW_STATE_NEW);
    }
    
    public boolean isReleased() {
        return reviewState.getName().equals(ReviewStateServiceLocal.REVIEW_STATE_RELEASED);
    }
    
    public ReviewState getReviewState() {
        return reviewState;
    }
    
    public void setReviewState(ReviewState reviewState) {
        this.reviewState = reviewState;
    }

    
    public Collection<UserGroup> getAllowedGroups() {
        return allowedGroups;
    }
    
    public void setAllowedGroups(Collection<UserGroup> allowedGroups) {
        this.allowedGroups = allowedGroups;
    }
    
    public VDCUser getCreator() {
        return creator;
    }
    
    public void setCreator(VDCUser creator) {
        this.creator = creator;
    }
    
    public VDCUser getLastUpdater() {
        return lastUpdater;
    }
    
    public void setLastUpdater(VDCUser lastUpdater) {
        this.lastUpdater = lastUpdater;
    }
    
    public boolean isIsHarvested() {
        return isHarvested;
    }
    
    public void setIsHarvested(boolean isHarvested) {
        this.isHarvested = isHarvested;
    }
    
    public Collection<StudyField> getSummaryFields() {
        return summaryFields;
    }
    
    public void setSummaryFields(Collection<StudyField> summaryFields) {
        this.summaryFields = summaryFields;
    }
 
    private long numberOfDownloads;
    
    /**
     * Getter for property numberOfDownloads.
     * @return Value of property numberOfDownloads.
     */
    public long getNumberOfDownloads() {
        return this.numberOfDownloads;
    }
    
    /**
     * Setter for property numberOfDownloads.
     * @param numberOfDownloads New value of property numberOfDownloads.
     */
    public void setNumberOfDownloads(long numberOfDownloads) {
        this.numberOfDownloads = numberOfDownloads;
    }
        
    /**
     * Holds value of property id.
     */
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
    
    
    /**
     * Holds value of property defaultFileCategory.
     */
    @OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="defaultFileCategory_id")
    private FileCategory defaultFileCategory;
    
    /**
     * Getter for property defaultFileCategory.
     * @return Value of property defaultFileCategory.
     */
    public FileCategory getDefaultFileCategory() {
        return this.defaultFileCategory;
    }
    
    /**
     * Setter for property defaultFileCategory.
     * @param defaultFileCategory New value of property defaultFileCategory.
     */
    public void setDefaultFileCategory(FileCategory defaultFileCategory) {
        this.defaultFileCategory = defaultFileCategory;
    }
    
    /**
     * Holds value of property fileCategories.
     */
    @OneToMany(mappedBy="study", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("name ASC")
    private java.util.List<edu.harvard.iq.dvn.core.study.FileCategory> fileCategories;
    
    /**
     * Getter for property fileCategories.
     * @return Value of property fileCategories.
     */
    public java.util.List<edu.harvard.iq.dvn.core.study.FileCategory> getFileCategories() {
        return this.fileCategories;
    }
    
    /**
     * Setter for property fileCategories.
     * @param fileCategories New value of property fileCategories.
     */
    public void setFileCategories(java.util.List<edu.harvard.iq.dvn.core.study.FileCategory> fileCategories) {
        this.fileCategories = fileCategories;
    }

    @OneToMany(mappedBy="study", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private java.util.List<edu.harvard.iq.dvn.core.study.StudyFile> studyFiles;

    public List<StudyFile> getStudyFiles() {
        return studyFiles;
    }

    public void setStudyFiles(List<StudyFile> studyFiles) {
        this.studyFiles = studyFiles;
    }

    
    
    /**
     * Holds value of property version.
     */
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
    @ManyToMany(mappedBy="studies")
    private Collection<VDCCollection> studyColls;
    
    
    
    public Collection<VDCCollection> getStudyColls() {
        return studyColls;
    }
    
    public void setStudyColls(Collection<VDCCollection> studyColls) {
        this.studyColls = studyColls;
    }
    
    
    /**
     * Holds value of property template.
     */
    @ManyToOne
    private Template template;
    
    /**
     * Getter for property template.
     * @return Value of property template.
     */
    public Template getTemplate() {
        return this.template;
    }
    
    /**
     * Setter for property template.
     * @param template New value of property template.
     */
    public void setTemplate(Template template) {
        this.template = template;
    }
    
    /**
     * Holds value of property restricted.
     */
    private boolean restricted;
    
    /**
     * Getter for property restricted.
     * @return Value of property restricted.
     */
    public boolean isRestricted() {
        return this.restricted;
    }
    
    /**
     * Setter for property restricted.
     * @param restricted New value of property restricted.
     */
    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }
    
    /**
     * Holds value of property allowedUsers.
     */
    @ManyToMany(cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST })
    private Collection<VDCUser> allowedUsers;
    
    /**
     * Getter for property allowedUsers.
     * @return Value of property allowedUsers.
     */
    public Collection<VDCUser> getAllowedUsers() {
        return this.allowedUsers;
    }
    
    /**
     * Setter for property allowedUsers.
     * @param allowedUsers New value of property allowedUsers.
     */
    public void setAllowedUsers(Collection<VDCUser> allowedUsers) {
        this.allowedUsers = allowedUsers;
    }
    
    /**
     * Holds value of property requestAccess.
     * If this value is true, then the user can send an email
     * to the vdc administrator requesting access to the study
     */
    private boolean requestAccess;
    
    /**
     * Getter for property requestAccess.
     * @return Value of property requestAccess.
     */
    public boolean isRequestAccess() {
        return this.requestAccess;
    }
    
    /**
     * Setter for property requestAccess.
     * @param requestAccess New value of property requestAccess.
     */
    public void setRequestAccess(boolean requestAccess) {
        this.requestAccess = requestAccess;
    }
    

    /**
     * Holds value of property owner.
     */
    @ManyToOne
    private VDC owner;
    
    /**
     * Getter for property owner.
     * @return Value of property owner.
     */
    public VDC getOwner() {
        return this.owner;
    }
    
    /**
     * Setter for property owner.
     * @param owner New value of property owner.
     */
    public void setOwner(VDC owner) {
        this.owner = owner;
    }
    
    /**
     * Holds value of property reviewer.
     */
    @ManyToOne
    private VDCUser reviewer;
    
    /**
     * Getter for property reviewer.
     * @return Value of property reviewer.
     */
    public VDCUser getReviewer() {
        return this.reviewer;
    }
    
    /**
     * Setter for property reviewer.
     * @param reviewer New value of property reviewer.
     */
    public void setReviewer(VDCUser reviewer) {
        this.reviewer = reviewer;
    }
    
    public Collection<StudyAccessRequest> getStudyRequests() {
        return studyRequests;
    }
    
    public void setStudyRequests(Collection<StudyAccessRequest> studyRequests) {
        this.studyRequests = studyRequests;
    }
    

    
    // these are wrapper methods to support old code; once old calls are
    // cleaned up and use the method directly, these should be removed
    public boolean isStudyRestrictedForUser(VDC vdc, VDCUser user) {
        return isStudyRestrictedForUser(user, null);
    }
    
    public boolean isStudyRestrictedForGroup(UserGroup usergroup) {
        return isStudyRestrictedForUser(null, usergroup);
    }    
    
    public boolean isStudyRestrictedForUser(VDCUser user, UserGroup ipUserGroup) {
        
        // the restrictions should be checked on the owner of the study, not the currentVDC (needs cleanup)
        VDC vdc = this.getOwner();
      
        // first check restrictions at the dataverse level
        if (this.getOwner().isVDCRestrictedForUser(user, null) ) {
            return true;
        }

        // otherwise check for restriction on study itself
        if ( isRestricted() ) {
            if (user == null) {
                if (ipUserGroup==null) {
                    return true;
                } else {
                    Iterator iter = this.getAllowedGroups().iterator();
                    while (iter.hasNext()) {
                        UserGroup allowedGroup = (UserGroup) iter.next();
                        if (allowedGroup.equals(ipUserGroup)) {
                            return false;
                        }    
                    } 
                    return true;
                }
            }
          
            // 1. check network role
            if (user.getNetworkRole()!=null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN) ) {
                // If you are network admin, you can do anything!
                return false;
            }
            
            // 2. check vdc role
            VDCRole userRole = user.getVDCRole(vdc);
            if (userRole != null) {
                String userRoleName = userRole.getRole().getName();
                if ( userRoleName.equals(RoleServiceLocal.ADMIN) || userRoleName.equals(RoleServiceLocal.CURATOR) ) {
                    return false;
                }
            }
            
            // 2a. check if creator
            if (user.getId().equals(this.getCreator().getId())) {
                return false;
            }
            
            // 3. check user
            Iterator iter = this.getAllowedUsers().iterator();
            while (iter.hasNext()) {
                VDCUser allowedUser = (VDCUser) iter.next();
                if ( allowedUser.getId().equals(user.getId()) ) {
                    return false;
                }
            }
            
            // 4. check groups
            iter = this.getAllowedGroups().iterator();
            while (iter.hasNext()) {
                UserGroup allowedGroup = (UserGroup) iter.next();
                if (user.getUserGroups().contains(allowedGroup)) {
                    return false;
                }
            }
            return true;
        }      
        else {
            return false;
        }
     }


/*********************************************************************
The methods in this section are the old way and need to be removed, once they are no longer called;
I left in for now because I want to make sure that using new code doesn't break anything
*********************************************************************/
    public  boolean isUserRestricted( VDC vdc, VDCUser user) {

        // the restrictions should be checked on the owner of the study, not the currentVDC (needs cleanup)
        vdc = this.getOwner();
        
        if (user == null) {
            return true;
        }
        if (user.getNetworkRole()!=null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN)) {
            return false;
        }
        VDCRole userRole = user.getVDCRole(vdc);
        String userRoleName=null;
        if (userRole!=null) {
            userRoleName = userRole.getRole().getName();
        }
        
        if (RoleServiceLocal.ADMIN.equals(userRoleName)
        || RoleServiceLocal.CURATOR.equals(userRoleName)
        || userInAllowedGroups(user)
        || userInAllowedUsers(user)
        || getCreator().getId().equals(user.getId())) {
            return false;
        } else {
            return true;
        }
        
    }
    
    private boolean userInAllowedUsers(VDCUser user) {
        for (Iterator it = allowedUsers.iterator(); it.hasNext();) {
            VDCUser allowedUser = (VDCUser) it.next();
            if (allowedUser.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean userInAllowedGroups(VDCUser user) {
        boolean foundUser=false;
        for (Iterator it = allowedGroups.iterator(); it.hasNext();) {
            UserGroup userGroup = (UserGroup) it.next();
            for (Iterator it2 = userGroup.getUsers().iterator(); it2.hasNext();) {
                VDCUser allowedUser = (VDCUser) it2.next();
                if (allowedUser.getId().equals(user.getId())) {
                    foundUser=true;
                    break;
                }
                
            }
            
            
        }
        return foundUser;
    }
/*********************************************************************
End of deprecated methods section
*********************************************************************/

    
    /**
     * Holds value of property protocol.
     */
    @Column(columnDefinition="TEXT")
    private String protocol;
    
    /**
     * Getter for property protocol.
     * @return Value of property protocol.
     */
    public String getProtocol() {
        return this.protocol;
    }
    
    /**
     * Setter for property protocol.
     * @param protocol New value of property protocol.
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    /**
     * Holds value of property authority.
     */
    @Column(columnDefinition="TEXT")
    private String authority;
    
    /**
     * Getter for property authority.
     * @return Value of property authority.
     */
    public String getAuthority() {
        return this.authority;
    }
    
    /**
     * Setter for property authority.
     * @param authority New value of property authority.
     */
    public void setAuthority(String authority) {
        this.authority = authority;
    }
    
    private String harvestIdentifier;
    
    public String getHarvestIdentifier() {
        return harvestIdentifier;
    }

    public void setHarvestIdentifier(String harvestIdentifier) {
        this.harvestIdentifier = harvestIdentifier;
    }     
    

    
    public boolean isUserAuthorizedToEdit(VDCUser user) {   
        String  studyVDCRoleName =null;
        // No users are allowed to edit a Harvested Study
        if (owner.isHarvestingDv()) {
            return false;
        }
        if (user.getNetworkRole()!=null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN)) {
            return true;
        }

        if (user.getVDCRole(owner)!=null) {
            studyVDCRoleName= user.getVDCRole(owner).getRole().getName();
        }
        if ((creator.getId().equals(user.getId()) && reviewState.getName().equals(ReviewStateServiceLocal.REVIEW_STATE_NEW))
        || RoleServiceLocal.ADMIN.equals(studyVDCRoleName)
        || RoleServiceLocal.CURATOR.equals(studyVDCRoleName)) {
            return true;
        }
        return false;
    }

    public boolean isUserAuthorizedToRelease(VDCUser user) {   
        String  studyVDCRoleName =null;
        // No users are allowed to edit a Harvested Study
        if (owner.isHarvestingDv()) {
            return false;
        }
        if (user.getNetworkRole()!=null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN)) {
            return true;
        }
        if (user.getVDCRole(owner)!=null) {
            studyVDCRoleName= user.getVDCRole(owner).getRole().getName();
        }
        if (RoleServiceLocal.ADMIN.equals(studyVDCRoleName)
        || RoleServiceLocal.CURATOR.equals(studyVDCRoleName)) {
            return true;
        }
        return false;
    }
    
    public StudyLock getStudyLock() {
        return studyLock;
    }

    public void setStudyLock(StudyLock studyLock) {
        this.studyLock = studyLock;
    }

     public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Study)) {
            return false;
        }
        Study other = (Study)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    public StudyDownload getStudyDownload() {
        return studyDownload;
    }

    public void setStudyDownload(StudyDownload studyDownload) {
        this.studyDownload = studyDownload;
    }

    
    
    public Date getLastExportTime() {
        return lastExportTime;
    }

    public void setLastExportTime(Date lastExportTime) {
        this.lastExportTime = lastExportTime;
    }


    public Date getLastIndexTime() {
        return lastIndexTime;
    }

    public void setLastIndexTime(Date lastIndexTime) {
        this.lastIndexTime = lastIndexTime;
    }

    @OneToMany(mappedBy = "study", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private List<StudyFileActivity> studyFileActivity;

    public List<StudyFileActivity> getStudyFileActivity() {
        return studyFileActivity;
    }

    public void setStudyFileActivity(List<StudyFileActivity> studyFileActivity) {
        this.studyFileActivity = studyFileActivity;
    }


    public List<StudyVersion> getStudyVersions() {
        return studyVersions;
    }

    public void setStudyVersions(List<StudyVersion> studyVersions) {
        this.studyVersions = studyVersions;
    }

    
    public StudyVersion getReleasedVersion() {
        for (StudyVersion studyVersion : getStudyVersions()) {
            if ("Released".equals(studyVersion.getVersionState()) ) {
                return studyVersion;
            }
        }

        return null;
    }

//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this,
//            ToStringStyle.MULTI_LINE_STYLE);
//    }

}
