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
 * UserGroup.java
 *
 * Created on July 28, 2006, 2:04 PM
 *
 */
package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.vdc.HarvestingDataverse;
import edu.harvard.iq.dvn.core.vdc.VDC;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class UserGroup implements java.io.Serializable  {
    private String name;
    private String description;
    @OneToMany(mappedBy="userGroup", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<LoginDomain> loginDomains;
   
    
    /**
     * Creates a new instance of UserGroup
     */
    public UserGroup() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<LoginDomain> getLoginDomains() {
        return loginDomains;
    }

    public void setLoginDomains(Collection<LoginDomain> loginDomains) {
        this.loginDomains = loginDomains;
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
     * Holds value of property users.
     */
    @ManyToMany (mappedBy="userGroups",cascade={CascadeType.PERSIST } )
    private Collection<VDCUser> users;

    

    /**
     * Getter for property users.
     * @return Value of property users.
     */
    public Collection<VDCUser> getUsers() {
        return this.users;
    }

    /**
     * Setter for property users.
     * @param users New value of property users.
     */
    public void setUsers(Collection<VDCUser> users) {
        this.users = users;
    }

    /**
     * Holds value of property studies.
     */
    @ManyToMany (mappedBy="allowedGroups", cascade={CascadeType.REMOVE})
    private Collection<Study> studies;

    /**
     * Getter for property studies.
     * @return Value of property studies.
     */
    public Collection<Study> getStudies() {
        return this.studies;
    }

    /**
     * Setter for property studies.
     * @param studies New value of property studies.
     */
    public void setStudies(Collection<Study> studies) {
        this.studies = studies;
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
     * Holds value of property studyFiles.
     */
    @ManyToMany(mappedBy="allowedGroups")
    private Collection<StudyFile> studyFiles;

    /**
     * Getter for property studyFiles.
     * @return Value of property studyFiles.
     */
    public Collection<StudyFile> getStudyFiles() {
        return this.studyFiles;
    }

    /**
     * Setter for property studyFiles.
     * @param studyFiles New value of property studyFiles.
     */
    public void setStudyFiles(Collection<StudyFile> studyFiles) {
        this.studyFiles = studyFiles;
    }

    /**
     * Holds value of property vdcs.
     */
    @ManyToMany(mappedBy="allowedGroups")    
    private Collection<VDC> vdcs;

    /**
     * Getter for property vdcs.
     * @return Value of property vdcs.
     */
    public Collection<VDC> getVdcs() {
        return this.vdcs;
    }

    /**
     * Setter for property vdcs.
     * @param vdcs New value of property vdcs.
     */
    public void setVdcs(Collection<VDC> vdcs) {
        this.vdcs = vdcs;
    }

    /**
     * Holds value of property pinService.
     */
    private String pinService;

    /**
     * Getter for property pinService.
     * @return Value of property pinService.
     */
    public String getPinService() {
        return this.pinService;
    }

    /**
     * Setter for property pinService.
     * @param pinService New value of property pinService.
     */
    public void setPinService(String pinService) {
        this.pinService = pinService;
    }

    /**
     * Holds value of property friendlyName.
     */
    private String friendlyName;

    /**
     * Getter for property friendlyName.
     * @return Value of property friendlyName.
     */
    public String getFriendlyName() {
        return this.friendlyName;
    }

    /**
     * Setter for property friendlyName.
     * @param friendlyName New value of property friendlyName.
     */
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    /**
     * Holds value of property loginAffiliates.
     */
    @OneToMany(mappedBy="userGroup", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<LoginAffiliate> loginAffiliates;

    /**
     * Getter for property loginAffiliates.
     * @return Value of property loginAffiliates.
     */
    public Collection<LoginAffiliate> getLoginAffiliates() {
        return this.loginAffiliates;
    }

    /**
     * Setter for property loginAffiliates.
     * @param loginAffiliates New value of property loginAffiliates.
     */
    public void setLoginAffiliates(Collection<LoginAffiliate> loginAffiliates) {
        this.loginAffiliates = loginAffiliates;
    }

       /**
     * Returns a hash code value for the object.  This implementation computes 
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    /**
     * Determines whether another object is equal to this Dummy.  The result is 
     * <code>true</code> if and only if the argument is not null and is a Dummy object that 
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserGroup)) {
            return false;
        }
        UserGroup other = (UserGroup)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs 
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "edu.harvard.iq.dvn.core.admin.UserGroup[id=" + id + "]";
    }

    
    @ManyToMany(mappedBy="allowedFileGroups")
    @OrderBy("name ASC")
    private java.util.List<VDC> allowedFileVdcs;

    /**
     * Getter for property memberVdcs.
     * @return Value of property memberVdcs.
     */
    public java.util.List<VDC> getAllowedFileVdcs() {
        return this.allowedFileVdcs;
    }

    /**
     * Setter for property memberVdcs.
     * @param memberVdcs New value of property memberVdcs.
     */
    public void setAllowedFileVdcs(java.util.List<VDC> memberVdcs) {
        this.allowedFileVdcs = memberVdcs;
    }     
}
