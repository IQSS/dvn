/*
 * UserGroup.java
 *
 * Created on July 28, 2006, 2:04 PM
 *
 */

package edu.harvard.hmdc.vdcnet.admin;

import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverse;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
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
public class UserGroup {
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
   @SequenceGenerator(name="usergroup_gen", sequenceName="usergroup_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="usergroup_gen")        
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

    @ManyToMany (mappedBy="allowedFileGroups",cascade={CascadeType.PERSIST } )
    private Collection<HarvestingDataverse> harvestingDataverses;
   
    
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
        return "edu.harvard.hmdc.vdcnet.admin.UserGroup[id=" + id + "]";
    }

    public Collection<HarvestingDataverse> getHarvestingDataverses() {
        return harvestingDataverses;
    }

    public void setHarvestingDataverses(Collection<HarvestingDataverse> harvestingDataverses) {
        this.harvestingDataverses = harvestingDataverses;
    }
}
