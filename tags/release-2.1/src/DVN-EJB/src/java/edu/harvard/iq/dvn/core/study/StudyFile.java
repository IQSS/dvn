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
 * StudyFile.java
 *
 * Created on July 28, 2006, 2:59 PM
 *
 */
package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.admin.NetworkRoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.RoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.VDCRole;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.vdc.HarvestingDataverse;
import edu.harvard.iq.dvn.core.vdc.VDC;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.persistence.*;



/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="fileClass")
public abstract class StudyFile implements Serializable {

    private String fileType;
    @Column(columnDefinition="TEXT")
    private String fileSystemLocation;
    private String originalFileType;
    private String unf;


    @OneToMany(mappedBy="studyFile", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private List<FileMetadata> fileMetadatas;


    /**
     * Creates a new instance of StudyFile
     */
    public StudyFile() {
        fileMetadatas = new ArrayList<FileMetadata>();
    }

    public StudyFile(Study study) {
        this.setStudy( study );
        study.getStudyFiles().add(this);
        
        StudyFileActivity sfActivity = new StudyFileActivity();
        this.setStudyFileActivity(sfActivity);
        sfActivity.setStudyFile(this);
        sfActivity.setStudy(this.getStudy());
        //TODO: add both sides of relationship here
        //this.getStudy().getStudyFileActivity().add(sfActivity);
        fileMetadatas = new ArrayList<FileMetadata>();
    }






    public List<FileMetadata> getFileMetadatas() {
        return fileMetadatas;
    }

    public void setFileMetadatas(List<FileMetadata> fileMetadatas) {
        this.fileMetadatas = fileMetadatas;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileSystemLocation() {
        return fileSystemLocation;
    }

    public void setFileSystemLocation(String fileSystemLocation) {
        this.fileSystemLocation = fileSystemLocation;
    }



    public String getOriginalFileType() {
        return originalFileType;
    }

    public void setOriginalFileType(String originalFileType) {
        this.originalFileType = originalFileType;
    }



    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(nullable=false)
    private Study study;

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }


    /**
     * Holds value of property displayOrder.
     */
    private int displayOrder;

    /**
     * Getter for property displayOrder.
     * @return Value of property displayOrder.
     */
    public int getDisplayOrder() {
        return this.displayOrder;
    }

    /**
     * Setter for property displayOrder.
     * @param displayOrder New value of property displayOrder.
     */
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
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
     * Holds value of property allowedGroups.
     */
    @ManyToMany(cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<UserGroup> allowedGroups;

    /**
     * Getter for property allowedGroups.
     * @return Value of property allowedGroups.
     */
    public Collection<UserGroup> getAllowedGroups() {
        return this.allowedGroups;
    }

    /**
     * Setter for property allowedGroups.
     * @param allowedGroups New value of property allowedGroups.
     */
    public void setAllowedGroups(Collection<UserGroup> allowedGroups) {
        this.allowedGroups = allowedGroups;
    }
    /**
     * Holds value of property allowedUsers.
     */
    @ManyToMany(cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
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
     * If this is true, then the user can send a request to the vdc administrator
     * to be allowed to view the file.
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
     * Holds value of property allowAccessRequest.
     */
    private boolean allowAccessRequest;

    /**
     * Getter for property allowAccessRequest.
     * @return Value of property allowAccessRequest.
     */
    public boolean isAllowAccessRequest() {
        return this.allowAccessRequest;
    }

    /**
     * Setter for property allowAccessRequest.
     * @param allowAccessRequest New value of property allowAccessRequest.
     */
    public void setAllowAccessRequest(boolean allowAccessRequest) {
        this.allowAccessRequest = allowAccessRequest;
    }
    /**
     * Holds value of property dataTable.
     */
    @OneToMany(mappedBy = "studyFile", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private List<DataTable> dataTables;

    public List<DataTable> getDataTables() {
        return dataTables;
    }

    public void setDataTables(List<DataTable> dataTables) {
        this.dataTables = dataTables;
    }

    public boolean isSubsetRestrictedForUser(VDCUser user, VDC vdc, UserGroup ipUserGroup) {
        // the restrictions should be checked on the owner of the study, not the currentVDC (needs cleanup)
        VDC owner = this.getStudy().getOwner();
        if (owner.isHarvestingDv()) {
            HarvestingDataverse hd = owner.getHarvestingDataverse();
            return hd.isSubsetRestrictedForUser(user, ipUserGroup);
        } else {
            return isFileRestrictedForUser(user, owner, ipUserGroup);
        }
    }

    public boolean isFileRestrictedForUser(VDCUser user, VDC vdc, UserGroup ipUserGroup) {

        // the restrictions should be checked on the owner of the study, not the currentVDC (needs cleanup)
        vdc = this.getStudy().getOwner();


        // first check if study is restricted, regardless of file permissions
        Study study = getStudy();
        if (study.isStudyRestrictedForUser( user, ipUserGroup)) {
            return true;
        }

        //  then check dataverse level file permisssions
        if (vdc.areFilesRestrictedForUser(user, ipUserGroup)) {
            return true;
        }

        // otherwise check for restriction on file itself
        if (isRestricted()) {
            if (user == null) {
                if (ipUserGroup == null) {
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

            // 1. check if study is restricted (this part was moved a little higher; still need more cleanup)    
            // 2. check network role
            if (user.getNetworkRole() != null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN)) {
                // If you are network admin, you can do anything!
                return false;
            }

            // 3. check vdc role
            VDCRole userRole = user.getVDCRole(vdc);
            if (userRole != null) {
                String userRoleName = userRole.getRole().getName();
                if (userRoleName.equals(RoleServiceLocal.ADMIN) || userRoleName.equals(RoleServiceLocal.CURATOR)) {
                    return false;
                }
            }

            //4. check if creator
            if (user.getId().equals(this.getStudy().getCreator().getId())) {
                return false;
            }

            // 5. check user
            Iterator iter = this.getAllowedUsers().iterator();
            while (iter.hasNext()) {
                VDCUser allowedUser = (VDCUser) iter.next();
                if (allowedUser.getId().equals(user.getId())) {
                    return false;
                }
            }

            // 6. check groups

            iter = this.getAllowedGroups().iterator();
            while (iter.hasNext()) {
                UserGroup allowedGroup = (UserGroup) iter.next();
                if (user.getUserGroups().contains(allowedGroup)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean isUserInAllowedUsers(Long userId) {
        for (Iterator it = allowedUsers.iterator(); it.hasNext();) {
            VDCUser elem = (VDCUser) it.next();
            if (elem.getId().equals(userId)) {
                return true;
            }

        }
        return false;
    }

    public boolean isGroupInAllowedGroups(Long groupId) {
        for (Iterator it = allowedGroups.iterator(); it.hasNext();) {
            UserGroup elem = (UserGroup) it.next();
            if (elem.getId().equals(groupId)) {
                return true;
            }

        }
        return false;
    }
    /**
     * Holds value of property fileSystemName.
     */
    private String fileSystemName;

    /**
     * Getter for property fileSystemName.
     * @return Value of property fileSystemName.
     */
    public String getFileSystemName() {
        return this.fileSystemName;
    }

    /**
     * Setter for property fileSystemName.
     * @param fileSystemName New value of property fileSystemName.
     */
    public void setFileSystemName(String fileSystemName) {
        this.fileSystemName = fileSystemName;
    }

    public boolean isRemote() {
        return fileSystemLocation != null &&
                (fileSystemLocation.startsWith("http:") || fileSystemLocation.startsWith("https:") || fileSystemLocation.startsWith("ftp:"));
    }

    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyFile)) {
            return false;
        }
        StudyFile other = (StudyFile) object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
    @OneToOne(mappedBy = "studyFile", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private StudyFileActivity studyFileActivity;

    public StudyFileActivity getStudyFileActivity() {
        return studyFileActivity;
    }

    public void setStudyFileActivity(StudyFileActivity studyFileActivity) {
        this.studyFileActivity = studyFileActivity;
    }

     public String getUnf() {
        return this.unf;
    }

    public void setUnf(String unf) {
        this.unf = unf;
    }

    public abstract boolean isSubsettable();

    public abstract boolean isUNFable();

    public void clearData() {
         for (DataTable elem : this.getDataTables()) {
            if (elem != null && elem.getDataVariables() != null) {
                elem.getDataVariables().clear();
            }
        }
    }


    // TODO: review how these methods are used
    // I believe it is still needed. At least the DSB ingest framework still
    // uses this method. (And I'm assuming we'll always want to be able to
    // get the filename of a given studyFile -- unless I'm missing something).
    //  -- L.A.
    public String getFileName() {
        if (getLatestFileMetadata() == null) {
            return null;
        }
        return getLatestFileMetadata().getLabel();
    }


    public String getLatestCategory() {
        return getLatestFileMetadata().getCategory();
    }


    public String getFileName(Long versionNumber) {
        if (getFileMetadata(versionNumber) == null) {
            return null;
        }
        return getFileMetadata(versionNumber).getLabel();
    }

    private FileMetadata getLatestFileMetadata() {
        FileMetadata fmd = null;

        for (FileMetadata fileMetadata : fileMetadatas) {
            if (fmd == null || fileMetadata.getStudyVersion().getVersionNumber().compareTo( fmd.getStudyVersion().getVersionNumber() ) > 0 ) {
                fmd = fileMetadata;
            }                       
        }
        return fmd;
    }

    private FileMetadata getFileMetadata (Long versionNumber) {
        if (versionNumber == null) {
                return getLatestFileMetadata();
        }

        for (FileMetadata fileMetadata : fileMetadatas) {
            if (fileMetadata != null && fileMetadata.getStudyVersion() != null &&
                versionNumber.equals( fileMetadata.getStudyVersion().getVersionNumber() ) ) {
                return fileMetadata;
            }
        }

        return null;
    }

    public FileMetadata getReleasedFileMetadata() {
        for (FileMetadata fileMetadata : fileMetadatas) {
            if (fileMetadata.getStudyVersion().isReleased() ) {
                return fileMetadata;
            }
        }
        return null;
    }

//    @Override
//    public String toString() {
//            return ToStringBuilder.reflectionToString(this,
//                ToStringStyle.MULTI_LINE_STYLE);
//    }
}
