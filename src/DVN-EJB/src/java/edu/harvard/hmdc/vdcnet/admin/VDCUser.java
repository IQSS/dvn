/*
 * VDCUser.java
 *
 * Created on July 28, 2006, 1:36 PM
 */

package edu.harvard.hmdc.vdcnet.admin;

import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.StudyLock;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetwork;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class VDCUser {

    @OneToMany(mappedBy = "user", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private List<StudyLock> studyLocks;

    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String password; 
    @ManyToMany (cascade={ CascadeType.PERSIST })
    private Collection<UserGroup> userGroups;
    
    @ManyToOne (cascade={CascadeType.PERSIST })
    private edu.harvard.hmdc.vdcnet.admin.NetworkRole networkRole;
    
    @OneToMany(mappedBy="vdcUser", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    private Collection<RoleRequest> roleRequests;
    
      @OneToMany(mappedBy="vdcUser", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    private Collection<NetworkRoleRequest> networkRoleRequests;
  
    
    @OneToMany(mappedBy="vdcUser", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    private Collection<VDCRole> vdcRoles;
    
    /**
     * Creates a new instance of VDCUser
     */
    public VDCUser() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter for property vdcRoles.
     * @return Value of property vdcRoles.
     */
    public Collection<VDCRole> getVdcRoles() {
        return this.vdcRoles;
    }
    
    public VDCRole getVDCRole(VDC vdc) {
        if (vdc != null) {
            for (Iterator it = vdcRoles.iterator(); it.hasNext();) {
                VDCRole elem = (VDCRole) it.next();
                if (elem.getVdc().getId().equals(vdc.getId())) {
                    return elem;
                }
            }
        }
        return null;
    }

    /**
     * Setter for property vdcRoles.
     * @param vdcRoles New value of property vdcRoles.
     */
    public void setVdcRoles(Collection<VDCRole> vdcRoles) {
        this.vdcRoles = vdcRoles;
    }

    public Collection<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(Collection<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }


    /**
     * Holds value of property id.
     */
    @SequenceGenerator(name="vdcuser_gen", sequenceName="vdcuser_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="vdcuser_gen")       
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
     * @param long New value of property id.
     */
    public void setLong(Long id) {
        this.id = id;
    }

    private int test;

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
     * Holds value of property studies.
     */
      @ManyToMany(mappedBy="allowedUsers", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST })
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
     * Holds value of property studyFiles.
     */
    @ManyToMany(mappedBy="allowedUsers", cascade={CascadeType.REMOVE }) 
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
     * Holds value of property institution.
     */
    private String institution;

    /**
     * Getter for property institution.
     * @return Value of property institution.
     */
    public String getInstitution() {
        return this.institution;
    }

    /**
     * Setter for property institution.
     * @param institution New value of property institution.
     */
    public void setInstitution(String institution) {
        this.institution = institution;
    }

    /**
     * Holds value of property position.
     */
    private String position;

    /**
     * Getter for property position.
     * @return Value of property position.
     */
    public String getPosition() {
        return this.position;
    }

    /**
     * Setter for property position.
     * @param position New value of property position.
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * Holds value of property phoneNumber.
     */
    private String phoneNumber;

    /**
     * Getter for property phoneNumber.
     * @return Value of property phoneNumber.
     */
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    /**
     * Setter for property phoneNumber.
     * @param phoneNumber New value of property phoneNumber.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public edu.harvard.hmdc.vdcnet.admin.NetworkRole getNetworkRole() {
        return networkRole;
    }

    public void setNetworkRole(edu.harvard.hmdc.vdcnet.admin.NetworkRole networkRole) {
        this.networkRole = networkRole;
    }

    /**
     * Holds value of property vdcNetwork.
     */
    @OneToOne(mappedBy="defaultNetworkAdmin")
    private VDCNetwork vdcNetwork;

    /**
     * Getter for property vdcNetwork.
     * @return Value of property vdcNetwork.
     */
    public VDCNetwork getVdcNetwork() {
        return this.vdcNetwork;
    }

    /**
     * Setter for property vdcNetwork.
     * @param vdcNetwork New value of property vdcNetwork.
     */
    public void setVdcNetwork(VDCNetwork vdcNetwork) {
        this.vdcNetwork = vdcNetwork;
    }

    public Collection<NetworkRoleRequest> getNetworkRoleRequests() {
        return networkRoleRequests;
    }

    public void setNetworkRoleRequests(Collection<NetworkRoleRequest> networkRoleRequests) {
        this.networkRoleRequests = networkRoleRequests;
    }

    /**
     * Holds value of property active.
     */
    private boolean active;

    /**
     * Getter for property active.
     * @return Value of property active.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Setter for property active.
     * @param active New value of property active.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    public List<StudyLock> getStudyLocks() {
        return studyLocks;
    }

    public void setStudyLocks(List<StudyLock> studyLocks) {
        this.studyLocks = studyLocks;
    }
     public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VDCUser)) {
            return false;
        }
        VDCUser other = (VDCUser)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }  
        
}
