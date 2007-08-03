/*
 * VDC.java
 *
 * Created on July 28, 2006, 2:22 PM
 *
 */

package edu.harvard.hmdc.vdcnet.vdc;

import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.study.Study;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class HarvestingDataverse {
    
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastHarvestTime;
    
    public Collection<Study> search(String query) {
        //TODO: complete implementation
        return null;
    }
    /** Creates a new instance of VDC */
    public HarvestingDataverse() {
        
    }
    
    /**
     * Holds value of property id.
     */
    @SequenceGenerator(name="harvestdataverse_gen", sequenceName="harvestdataverse_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="harvestdataverse_gen")
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
     * If restricted = true, then only allowedGroups and allowedUsers can view
     * the VDC.  If false, then everyone can (it is public).
     */
    private boolean filesRestricted;
    
    /**
     * Getter for property restricted.
     * @return Value of property restricted.
     */
    public boolean isFilesRestricted() {
        return this.filesRestricted;
    }
    
    /**
     * Setter for property restricted.
     * @param restricted New value of property restricted.
     */
    public void setFilesRestricted(boolean filesRestricted) {
        this.filesRestricted = filesRestricted;
    }

    private boolean subsetRestricted;

    public boolean isSubsetRestricted() {
        return subsetRestricted;
    }

    public void setSubsetRestricted(boolean subsetRestricted) {
        this.subsetRestricted = subsetRestricted;
    }


    
    /**
     * Holds value of property allowedFileGroups.
     */
    @ManyToMany(cascade={CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name="harvestingdataverse_usergroup",
            joinColumns=@JoinColumn(name="harvestingdataverse_id"),
            inverseJoinColumns=@JoinColumn(name="allowedfilegroups_id"))
    private Collection<UserGroup> allowedFileGroups;
    
    /**
     * Getter for property allowedGroups.
     * @return Value of property allowedGroups.
     */
    public Collection<UserGroup> getAllowedFileGroups() {
        return this.allowedFileGroups;
    }
    
    /**
     * Setter for property allowedGroups.
     * @param allowedGroups New value of property allowedGroups.
     */
    public void setAllowedFileGroups(Collection<UserGroup> allowedFileGroups) {
        this.allowedFileGroups = allowedFileGroups;
    }
    
    public boolean areFilesRestrictedForUser(VDCUser user, UserGroup ipUserGroup) {
        if (this.filesRestricted) {
            if (user!=null && user.getNetworkRole()!=null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN)) {
                return false;
            }
            if (!allowedFileUsers.contains(user) ) {
                boolean foundUserInGroup=false;
                for (Iterator it = allowedFileGroups.iterator(); it.hasNext();) {
                    UserGroup elem = (UserGroup) it.next();
                    if (elem.equals(ipUserGroup)) {
                        foundUserInGroup=true;
                        break;
                    }
                    if (elem.getUsers().contains(user)) {
                        foundUserInGroup=true;
                        break;
                    }
                }
                if (!foundUserInGroup) {
                    return true;
                }
                     
            }
        }
        return false;
    }
    
    public boolean isSubsetRestrictedForUser(VDCUser user, UserGroup ipUserGroup) {
        if (areFilesRestrictedForUser(user, ipUserGroup)) {
            return this.subsetRestricted;
        } else {
            return false;
        }
    }
    
     /**
     * Holds value of property allowedFileUsers.
     */
    @ManyToMany(cascade={ CascadeType.MERGE, CascadeType.PERSIST })
    private Collection<VDCUser> allowedFileUsers;
    
    /**
     * Getter for property allowedFileUsers.
     * @return Value of property allowedGroups.
     */
    public Collection<VDCUser> getAllowedFileUsers() {
        return this.allowedFileUsers;
    }
    
    /**
     * Setter for property allowedFileUsers.
     * @param allowedGroups New value of property allowedGroups.
     */
    public void setAllowedFileUsers(Collection<VDCUser> allowedFileUsers) {
        this.allowedFileUsers = allowedFileUsers;
    }
    
    
    public boolean userInAllowedFileGroups(VDCUser user) {
        boolean foundUser=false;
        for (Iterator it = allowedFileGroups.iterator(); it.hasNext();) {
            UserGroup elem = (UserGroup) it.next();
            if (elem.getUsers().contains(user)) {
                foundUser=true;
                break;
            }
        }
        return foundUser;
    }
    
    public boolean isAllowedGroup(UserGroup usergroup) {
        boolean foundGroup=false;
        for (Iterator it = allowedFileGroups.iterator(); it.hasNext();) {
            UserGroup elem = (UserGroup) it.next();
            if (elem.getId().equals(usergroup.getId())) {
                foundGroup=true;
                break;
            }
        }
        return foundGroup;
    }

    /**
     * Holds value of property oaiServer.
     */
    private String oaiServer;

    /**
     * Getter for property oaiServer.
     * @return Value of property oaiServer.
     */
    public String getOaiServer() {
        return this.oaiServer;
    }

    /**
     * Setter for property oaiServer.
     * @param oaiServer New value of property oaiServer.
     */
    public void setOaiServer(String oaiServer) {
        this.oaiServer = oaiServer.trim();
    }

    /**
     * Holds value of property harvestingSet.
     */
    private String harvestingSet;

    /**
     * Getter for property harvestingSet.
     * @return Value of property harvestingSet.
     */
    public String getHarvestingSet() {
        return this.harvestingSet;
    }

    /**
     * Setter for property harvestingSet.
     * @param harvestingSet New value of property harvestingSet.
     */
    public void setHarvestingSet(String harvestingSet) {
        this.harvestingSet = harvestingSet;
    }

    /**
     * Holds value of property format.
     */
    private String format;

    /**
     * Getter for property format.
     * @return Value of property format.
     */
    public String getFormat() {
        return this.format;
    }

    /**
     * Setter for property format.
     * @param format New value of property format.
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Holds value of property vdc.
     */
    @OneToOne (mappedBy="harvestingDataverse",cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST })
    private VDC vdc;

    /**
     * Getter for property vdc.
     * @return Value of property vdc.
     */
    public VDC getVdc() {
        return this.vdc;
    }

    /**
     * Setter for property vdc.
     * @param vdc New value of property vdc.
     */
    public void setVdc(VDC vdc) {
        this.vdc = vdc;
    }

    /**
     * Holds value of property scheduled.
     */
    private boolean scheduled;

    /**
     * Getter for property scheduled.
     * @return Value of property scheduled.
     */
    public boolean isScheduled() {
        return this.scheduled;
    }

    /**
     * Setter for property scheduled.
     * @param scheduled New value of property scheduled.
     */
    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

     public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HarvestingDataverse)) {
            return false;
        }
        HarvestingDataverse other = (HarvestingDataverse)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }    

    public Date getLastHarvestTime() {
        return lastHarvestTime;
    }

    public void setLastHarvestTime(Date lastHarvestTime) {
        this.lastHarvestTime = lastHarvestTime;
    }

    /**
     * Holds value of property harvestingNow.
     */
    private boolean harvestingNow;

    /**
     * Getter for property harvestingNow.
     * @return Value of property harvestingNow.
     */
    public boolean isHarvestingNow() {
        return this.harvestingNow;
    }

    /**
     * Setter for property harvestingNow.
     * @param harvestingNow New value of property harvestingNow.
     */
    public void setHarvestingNow(boolean harvestingNow) {
        this.harvestingNow = harvestingNow;
    }

    /**
     * Holds value of property handlePrefix.
     */
    @ManyToOne
    private HandlePrefix handlePrefix;

    /**
     * Getter for property handlePrefix.
     * @return Value of property handlePrefix.
     */
    public HandlePrefix getHandlePrefix() {
        return this.handlePrefix;
    }

    /**
     * Setter for property handlePrefix.
     * @param handlePrefix New value of property handlePrefix.
     */
    public void setHandlePrefix(HandlePrefix handlePrefix) {
        this.handlePrefix = handlePrefix;
    }

   


}
