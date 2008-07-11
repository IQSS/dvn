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

package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.RoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.VDCRole;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverse;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Persistence;
import javax.persistence.SequenceGenerator;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class StudyFile implements Serializable{
    private String globalId;
    private String fileName;
    private String fileType;
    private boolean subsettable;
    private String fileSystemLocation;
    private String label;
    
    private String originalFileType;

    
    @Column(columnDefinition="TEXT")
    private String description;
    
    /**
     * Creates a new instance of StudyFile
     */
    public StudyFile() {
    }
    
    public String getGlobalId() {
        return globalId;
    }
    
    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileType() {
        return fileType;
    }
    
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    public boolean isSubsettable() {
        return subsettable;
    }
    
    public void setSubsettable(boolean subsettable) {
        this.subsettable = subsettable;
    }
    
    public String getFileSystemLocation() {
        return fileSystemLocation;
    }
    
    public void setFileSystemLocation(String fileSystemLocation) {
        this.fileSystemLocation = fileSystemLocation;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public String getOriginalFileType() {
        return originalFileType;
    }

    public void setOriginalFileType(String originalFileType) {
        this.originalFileType = originalFileType;
    }    
   
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
  
    @ManyToOne (cascade={CascadeType.MERGE, CascadeType.PERSIST})
    private FileCategory fileCategory;
    
    public FileCategory getFileCategory() {
        return fileCategory;
    }
    
    public void setFileCategory(FileCategory fileCategory) {
        this.fileCategory = fileCategory;
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
    @SequenceGenerator(name="studyfile_gen", sequenceName="studyfile_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="studyfile_gen")
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
    @ManyToMany(cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST })
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
    @OneToOne(mappedBy="studyFile", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private DataTable dataTable;
    
    /**
     * Getter for property studyFile.
     * @return Value of property studyFile.
     */
    public DataTable getDataTable() {
        return this.dataTable;
    }
    
    /**
     * Setter for property studyFile.
     * @param studyFile New value of property studyFile.
     */
    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    public boolean isSubsetRestrictedForUser(VDCUser user, VDC vdc, UserGroup ipUserGroup) {
        // the restrictions should be checked on the owner of the study, not the currentVDC (needs cleanup)
        VDC owner = this.getFileCategory().getStudy().getOwner();        
        if (owner.isHarvestingDataverse()) {
            HarvestingDataverse hd = owner.getHarvestingDataverse();
            return hd.isSubsetRestrictedForUser(user, ipUserGroup);
        } else {
            return isFileRestrictedForUser(user, owner, ipUserGroup);
        }
    }    
    
    public boolean isFileRestrictedForUser( VDCUser user, VDC vdc, UserGroup ipUserGroup ) {

        // the restrictions should be checked on the owner of the study, not the currentVDC (needs cleanup)
        vdc = this.getFileCategory().getStudy().getOwner();

        
        // first check if study is restricted, regardless of file permissions
        Study study = getFileCategory().getStudy();
        if (study.isStudyRestrictedForUser(vdc,user)){
            return true;
        }

        //  then check dataverse level file permisssions
         if (vdc.areFilesRestrictedForUser(user, ipUserGroup)) {
             return true;
         }
        
        // otherwise check for restriction on file itself
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
          
            // 1. check if study is restricted (this part was moved a little higher; still need more cleanup)    
            // 2. check network role
            if (user.getNetworkRole()!=null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN) ) {
                // If you are network admin, you can do anything!
                return false;
            }
            
            // 3. check vdc role
            VDCRole userRole = user.getVDCRole(vdc);
            if (userRole != null) {
                String userRoleName = userRole.getRole().getName();
                if ( userRoleName.equals(RoleServiceLocal.ADMIN) || userRoleName.equals(RoleServiceLocal.CURATOR) ) {
                    return false;
                }
            }
            
            //4. check if creator
            if (user.getId().equals(this.getFileCategory().getStudy().getCreator().getId())) {
                return false;
            }
            
            // 5. check user
            Iterator iter = this.getAllowedUsers().iterator();
            while (iter.hasNext()) {
                VDCUser allowedUser = (VDCUser) iter.next();
                if ( allowedUser.getId().equals(user.getId()) ) {
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
        }      
        else {
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
        return 
            fileSystemLocation != null && 
            (fileSystemLocation.startsWith("http:") || fileSystemLocation.startsWith("https:") || fileSystemLocation.startsWith("ftp:") );
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
        StudyFile other = (StudyFile)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }       
    
}
