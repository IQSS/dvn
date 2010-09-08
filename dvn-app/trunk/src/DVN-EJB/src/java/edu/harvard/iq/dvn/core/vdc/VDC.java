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
 * VDC.java
 *
 * Created on July 28, 2006, 2:22 PM
 *
 */

package edu.harvard.iq.dvn.core.vdc;

import edu.harvard.iq.dvn.core.admin.NetworkRoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.RoleRequest;
import edu.harvard.iq.dvn.core.admin.RoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.VDCRole;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyField;
import edu.harvard.iq.dvn.core.study.Template;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.naming.InitialContext;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class VDC implements java.io.Serializable  {

    

    public static final String ORDER_BY_ACTIVITY = "activity";
    public static final String ORDER_BY_OWNED_STUDIES = "ownedStudies";
    public static final String ORDER_BY_LAST_STUDY_UPDATE_TIME = "lastStudyUpdateTime";
    public static final String ORDER_BY_NAME = "name";
    public static final String ORDER_BY_CREATOR = "username";
    public static final String ORDER_BY_CREATE_DATE = "createddate";
    public static final String ORDER_BY_RELEASE_DATE = "releasedate";
    public static final String ORDER_BY_AFFILIATION = "affiliation";
    public static final String ORDER_BY_TYPE = "dtype";

    private String name;
    private String description;
    private String visibility;
    private String affiliation;
    private String dtype;
    
    
    /**
     * Make the text the default db type for header.
     */
    @Column(name="header", columnDefinition="TEXT")
    private String header;
    /**
     * Make the text the default db type for footer.
     */
    @Column(name="footer", columnDefinition="TEXT")
    private String footer;
    /**
     * Make the text the default db type for announcements.
     */
    @Column(name="announcements", columnDefinition="TEXT")
    private String announcements;
    
    @Column(name="downloadTermsOfUse", columnDefinition="TEXT")
    private String downloadTermsOfUse;
     @Column(name="downloadTermsOfUseEnabled")
    private boolean downloadTermsOfUseEnabled;
    
     
    @Column(name="termsOfUse", columnDefinition="TEXT")
    private String depositTermsOfUse;

    public String getDepositTermsOfUse() {
        return depositTermsOfUse;
    }

    public void setDepositTermsOfUse(String depositTermsOfUse) {
        this.depositTermsOfUse = depositTermsOfUse;
    }

    public boolean isDepositTermsOfUseEnabled() {
        return depositTermsOfUseEnabled;
    }

    public void setDepositTermsOfUseEnabled(boolean depositTermsOfUseEnabled) {
        this.depositTermsOfUseEnabled = depositTermsOfUseEnabled;
    }
     @Column(name="depositTermsOfUseEnabled")
    private boolean depositTermsOfUseEnabled;
    

    @Column(name="termsOfUseEnabled")
    private boolean termsOfUseEnabled;
     
     
    private String copyright;
    @OneToOne (cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private VDCCollection rootCollection;
    @ManyToMany
    //(cascade={CascadeType.REMOVE })
    @JoinTable(name="VDC_ADV_SEARCH_FIELDS",
    joinColumns=@JoinColumn(name="vdc_id"),
            inverseJoinColumns=@JoinColumn(name="study_field_id"))
            private Collection<StudyField> advSearchFields;
    @ManyToMany
    //(cascade={CascadeType.REMOVE })
    @JoinTable(name="VDC_ANY_SEARCH_FIELDS",
    joinColumns=@JoinColumn(name="vdc_id"),
            inverseJoinColumns=@JoinColumn(name="study_field_id"))
            private Collection<StudyField> anySearchFields;
    @ManyToMany
    //(cascade={CascadeType.REMOVE })
    @JoinTable(name="SUMMARY_FIELDS",
    joinColumns=@JoinColumn(name="vdc_id"),
            inverseJoinColumns=@JoinColumn(name="study_field_id"))
            private Collection<StudyField> summaryFields;
    @ManyToMany
    //(cascade={CascadeType.REMOVE })
    @JoinTable(name="SEARCH_RESULT_FIELDS",
    joinColumns=@JoinColumn(name="vdc_id"),
            inverseJoinColumns=@JoinColumn(name="study_field_id"))
            private Collection<StudyField> searchResultFields;
    
    @ManyToOne
    private VDCUser reviewer;

    private boolean allowStudyComments = true;

    private boolean allowRegisteredUsersToContribute=false;

    private boolean allowContributorsEditAll=false;
    
   
    /** Creates a new instance of VDC */
    public VDC() {
        VDCActivity vdcActivity = new VDCActivity();
        this.setVDCActivity(vdcActivity);
        vdcActivity.setVDC(this);

        rootCollection = new VDCCollection();
        rootCollection.setOwner(this);

        ownedCollections = new ArrayList<VDCCollection>();
        ownedCollections.add(rootCollection);
    }

    public boolean isAllowContributorsEditAll() {
        return allowContributorsEditAll;
    }

    public void setAllowContributorsEditAll(boolean allowContributorsEditAll) {
        this.allowContributorsEditAll = allowContributorsEditAll;
    }

    public boolean isAllowRegisteredUsersToContribute() {
        return allowRegisteredUsersToContribute;
    }

    public void setAllowRegisteredUsersToContribute(boolean allowRegisteredUsersToContribute) {
        this.allowRegisteredUsersToContribute = allowRegisteredUsersToContribute;
    }

    

    public boolean isTermsOfUseEnabled() {
        return termsOfUseEnabled;
    }

    public void setTermsOfUseEnabled(boolean termsOfUseEnabled) {
        this.termsOfUseEnabled = termsOfUseEnabled;
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
    
    public String getVisibility() {
        return visibility;
    }
    
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
    
    public String getHeader() {
        return header;
    }
    
    public void setHeader(String header) {
        this.header = header;
    }
    
    public String getFooter() {
        return footer;
    }
    
    public void setFooter(String footer) {
        this.footer = footer;
    }
    
    public String getAnnouncements() {
        return announcements;
    }
    
    public void setAnnouncements(String announcements) {
        this.announcements = announcements;
    }

    public String getDownloadTermsOfUse() {
        return downloadTermsOfUse;
    }

    public void setDownloadTermsOfUse(String downloadTermsOfUse) {
        this.downloadTermsOfUse = downloadTermsOfUse;
    }

    public boolean isDownloadTermsOfUseEnabled() {
        return downloadTermsOfUseEnabled;
    }

    public void setDownloadTermsOfUseEnabled(boolean downloadTermsOfUseEnabled) {
        this.downloadTermsOfUseEnabled = downloadTermsOfUseEnabled;
    }
    
  
    public String getCopyright() {
        return copyright;
    }
    
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }
    
    public VDCCollection getRootCollection() {
        return rootCollection;
    }
    
    public void setRootCollection(VDCCollection rootCollection) {
        this.rootCollection = rootCollection;
    }
    
    public Collection<StudyField> getAdvSearchFields() {
        return advSearchFields;
    }
    
    public void setAdvSearchFields(Collection<StudyField> advSearchFields) {
        this.advSearchFields = advSearchFields;
    }
    
    public Collection<StudyField> getAnySearchFields() {
        return anySearchFields;
    }
    
    public void setAnySearchFields(Collection<StudyField> anySearchFields) {
        this.anySearchFields = anySearchFields;
    }
    
    public Collection<StudyField> getSummaryFields() {
        return summaryFields;
    }
    
    public void setSummaryFields(Collection<StudyField> summaryFields) {
        this.summaryFields = summaryFields;
    }
    
    public Collection<StudyField> getSearchResultFields() {
        return searchResultFields;
    }
    
    public void setSearchResultFields(Collection<StudyField> searchResultFields) {
        this.searchResultFields = searchResultFields;
    }
    /**
     * Reviewer is a VDCUser who has a curator role in this VDC and
     * is responsible for doing initial "quick review" of study.
     * (To move it from New State to Released State.)
     */
    public VDCUser getReviewer() {
        return reviewer;
    }
    
    public void setReviewer(VDCUser reviewer) {
        this.reviewer = reviewer;
    }
    
    /**
     * Holds value of property logo.
     */
    private String logo;
    
    /**
     * Getter for property logo.
     * @return Value of property logo.
     */
    public String getLogo() {
        return this.logo;
    }
    
    /**
     * Setter for property logo.
     * @param logo New value of property logo.
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }
    
    /**
     * Holds value of property contactEmail.
     */
    private String contactEmail;
    
    /**
     * Getter for property contactEmail.
     * @return Value of property contactEmail.
     */
    public String getContactEmail() {
        return this.contactEmail;
    }
    
    /**
     * Setter for property contactEmail.
     * @param contactEmail New value of property contactEmail.
     */
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    
    
    /**
     * Holds value of property templates.
     */
    @OneToMany(mappedBy="vdc", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<Template> templates;
    
    /**
     * Getter for property templates.
     * @return Value of property templates.
     */
    public Collection<Template> getTemplates() {
        return this.templates;
    }
    
    /**
     * Setter for property templates.
     * @param templates New value of property templates.
     */
    public void setTemplates(Collection<Template> templates) {
        this.templates = templates;
    }
    
    
    
    /**
     * Holds value of property defaultTemplate.
     */
    @ManyToOne
    private Template defaultTemplate;
    
    /**
     * Getter for property defaultTemplate.
     * @return Value of property defaultTemplate.
     */
    public Template getDefaultTemplate() {
        return this.defaultTemplate;
    }
    
    /**
     * Setter for property defaultTemplate.
     * @param defaultTemplate New value of property defaultTemplate.
     */
    public void setDefaultTemplate(Template defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }
    
   
    
    /**
     * Holds value of property id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    
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
    
    private String alias;
    
    public String getAlias() {
        return alias;
    }
    
    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    /**
     * Holds value of property restricted.
     * If restricted = true, then only allowedGroups and allowedUsers can view
     * the VDC.  If false, then everyone can (it is public).
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
    @ManyToMany(cascade={CascadeType.MERGE, CascadeType.PERSIST })
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
    // Don't use Cascade REMOVE here because vdcServiceBean.delete() will
    // delete each study by calling studyService.delete() (which does special logic)
    @OneToMany(mappedBy="owner", cascade={ CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<Study> ownedStudies;
  
    
    
    @OneToMany(mappedBy="vdc", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private java.util.Collection<ContactSubject> contactSubjects;

    @OneToMany(mappedBy="owner", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<VDCCollection> ownedCollections;
    
    public java.util.Collection<ContactSubject> getContactSubjects() {
        return contactSubjects;
    }

    public void setContactSubjects(java.util.Collection<ContactSubject> contactSubjects) {
        this.contactSubjects = contactSubjects;
    }

    /**
     * Holds value of property roleRequests.
     */
       @OneToMany (mappedBy="vdc", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<RoleRequest> roleRequests;

    /**
     * Getter for property roleRequests.
     * @return Value of property roleRequests.
     */
    public Collection<RoleRequest> getRoleRequests() {
        return this.roleRequests;
    }

    /**
     * Setter for property roleRequests.
     * @param roleRequests New value of property roleRequests.
     */
    public void setRoleRequests(Collection<RoleRequest> roleRequests) {
        this.roleRequests = roleRequests;
    }

    /**
     * Holds value of property allowContributorRequests.
     */
    private boolean allowContributorRequests;

    /**
     * Getter for property allowContributorRequests.
     * @return Value of property allowContributorRequests.
     */
    public boolean isAllowContributorRequests() {
        return this.allowContributorRequests;
    }

    /**
     * Setter for property allowContributorRequests.
     * @param allowContributorRequests New value of property allowContributorRequests.
     */
    public void setAllowContributorRequests(boolean allowContributorRequests) {
        this.allowContributorRequests = allowContributorRequests;
    }
    @OneToMany(mappedBy="vdc", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
  //  @OrderBy("vdcUser.userName")
    private List<VDCRole> vdcRoles;

    /**
     * Getter for property vdcRoles.
     * @return Value of property vdcRoles.
     */
    public List<VDCRole> getVdcRoles() {
        return this.vdcRoles;
    }

    /**
     * Setter for property vdcRoles.
     * @param vdcRoles New value of property vdcRoles.
     */
    public void setVdcRoles(List<VDCRole> vdcRoles) {
        this.vdcRoles = vdcRoles;
    }

    /**
     * Holds value of property displayNetworkAnnouncements.
     */
    private boolean displayNetworkAnnouncements;

    /**
     * Getter for property displayNetworkAnnouncements.
     * @return Value of property displayNetworkAnnouncements.
     */
    public boolean isDisplayNetworkAnnouncements() {
        return this.displayNetworkAnnouncements;
    }

    /**
     * Setter for property displayNetworkAnnouncements.
     * @param displayNetworkAnnouncements New value of property displayNetworkAnnouncements.
     */
    public void setDisplayNetworkAnnouncements(boolean displayNetworkAnnouncements) {
        this.displayNetworkAnnouncements = displayNetworkAnnouncements;
    }

    /**
     * Holds value of property displayAnnouncements.
     */
    private boolean displayAnnouncements;

    /**
     * Getter for property displayAnnouncements.
     * @return Value of property displayAnnouncements.
     */
    public boolean isDisplayAnnouncements() {
        return this.displayAnnouncements;
    }

    /**
     * Setter for property displayAnnouncements.
     * @param displayAnnouncements New value of property displayAnnouncements.
     */
    public void setDisplayAnnouncements(boolean displayAnnouncements) {
        this.displayAnnouncements = displayAnnouncements;
    }

    /**
     * Holds value of property displayNewStudies.
     */
    private boolean displayNewStudies;

    /**
     * Getter for property displayNewStudies.
     * @return Value of property displayNewStudies.
     */
    public boolean isDisplayNewStudies() {
        return this.displayNewStudies;
    }

    /**
     * Setter for property displayNewStudies.
     * @param displayNewStudies New value of property displayNewStudies.
     */
    public void setDisplayNewStudies(boolean displayNewStudies) {
        this.displayNewStudies = displayNewStudies;
    }

    /**
     * Make the text the default db type for about.
     */
    @Column(name="aboutThisDataverse", columnDefinition="TEXT")
    /**
     * Holds value of property aboutThisDataverse.
     */
    private String aboutThisDataverse;

    /**
     * Getter for property aboutThisDataverse.
     * @return Value of property aboutThisDataverse.
     */
    public String getAboutThisDataverse() {
        return this.aboutThisDataverse;
    }

    /**
     * Setter for property aboutThisDataverse.
     * @param aboutThisDataverse New value of property aboutThisDataverse.
     */
    public void setAboutThisDataverse(String aboutThisDataverse) {
        this.aboutThisDataverse = aboutThisDataverse;
    }
    
  
    @ManyToMany(mappedBy="linkedVDCs")
    private List<VDCCollection> linkedCollections;

    public List<VDCCollection> getLinkedCollections() {
        return linkedCollections;
    }

    public void setLinkedCollections(List<VDCCollection> linkedCollections) {
        this.linkedCollections = linkedCollections;
    }

    /**
     * Holds value of property creator.
     */
    @ManyToOne
    private VDCUser creator;

    /**
     * Getter for property creator.
     * @return Value of property creator.
     */
    public VDCUser getCreator() {
        return this.creator;
    }

    /**
     * Setter for property creator.
     * @param creator New value of property creator.
     */
    public void setCreator(VDCUser creator) {
        this.creator = creator;
    }

    /**
     * Holds value of property harvestingDataverse.
     */
    @OneToOne (cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    private HarvestingDataverse harvestingDataverse;

    /**
     * Getter for property harvestingDataverse.
     * @return Value of property harvestingDataverse.
     */
    public HarvestingDataverse getHarvestingDataverse() {
        return this.harvestingDataverse;
    }

    /**
     * Setter for property harvestingDataverse.
     * @param harvestingDataverse New value of property harvestingDataverse.
     */
    public void setHarvestingDataverse(HarvestingDataverse harvestingDataverse) {
        this.harvestingDataverse = harvestingDataverse;
    }

    public boolean isHarvestingDv() {
        return harvestingDataverse!=null;
    }
    public Collection<VDCCollection> getOwnedCollections() {
        return ownedCollections;
    }

    public void setOwnedCollections(Collection<VDCCollection> ownedCollections) {
        this.ownedCollections = ownedCollections;
    }

    public Collection<Study> getOwnedStudies() {
        return ownedStudies;
    }

    public void setOwnedStudies(Collection<Study> ownedStudies) {
        this.ownedStudies = ownedStudies;
    }


   
    /**
     * Getter for property affiliation.
     * @return Value of property affiliation.
     */
    public String getAffiliation() {
        return this.affiliation;
    }

    /**
     * Setter for property affiliation.
     * @param affiliation New value of property affiliation.
     */
    public void setAffiliation(String affiliation) {
        if (affiliation==null ) {
            affiliation="";
        }
        this.affiliation = affiliation;
    }
    
    /**
     * Getter for property dtype.
     * @return Value of property dtype.
     */
    public String getDtype() {
        return this.dtype;
    }

    /**
     * Setter for property dtype.
     * @param affiliation New value of property dtype.
     */
    public void setDtype(String dtype) {
        this.dtype = dtype;
    }
 /** ********************* add for VDCGroup support *********************** */
     /**
     * Holds value of property memberVdcs.
     */
    @ManyToMany
    @JoinTable(name="VDCGROUP_VDCS",
            joinColumns=@JoinColumn(name="VDC_ID"),
            inverseJoinColumns=@JoinColumn(name="VDCGROUP_ID"))
    private Collection<VDCGroup> vdcGroups;
    
    public java.util.Collection<VDCGroup> getVdcGroups() {
        return vdcGroups;
    }

    public void setVdcGroups(java.util.Collection<VDCGroup> vdcGroups) {
        this.vdcGroups = vdcGroups;
    }

  
   
    public boolean isVDCRestrictedForUser(VDCUser user, UserGroup ipUserGroup) {

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
            VDCRole userRole = user.getVDCRole(this);
            if (userRole != null) {
                String userRoleName = userRole.getRole().getName();
                if ( userRoleName.equals(RoleServiceLocal.ADMIN) || 
                     userRoleName.equals(RoleServiceLocal.CURATOR) ||
                     userRoleName.equals(RoleServiceLocal.CONTRIBUTOR) ||
                     userRoleName.equals(RoleServiceLocal.PRIVILEGED_VIEWER)
                   ) {
                    return false;
                }
            }
           
            // 3. check users
            // (this step is not necessary for dataverse restriction check)

            // 4. check groups
            Iterator iter = this.getAllowedGroups().iterator();
            while (iter.hasNext()) {
                UserGroup allowedGroup = (UserGroup) iter.next();
                if (user.getUserGroups().contains(allowedGroup)) {
                    return false;
                }
            }
            return true;
        }      

        return false;
    }
    
    /**
     * Scholar Dataverse type merge to VDC April 2008 by wjb
     * 
     */

    private String firstName;
    private String lastName;
    
    /**
     * Getter for property firstName.
     * @return Value of property firstName.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Setter for property firstName.
     * @param firstName New value of property firstName.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter for property lastName.
     * @return Value of property lastName.
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Setter for property lastName.
     * @param lastName New value of property lastName.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
      public boolean userInAllowedGroups(VDCUser user) {
        boolean foundUser=false;
        for (Iterator it = allowedGroups.iterator(); it.hasNext();) {
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
        for (Iterator it = allowedGroups.iterator(); it.hasNext();) {
            UserGroup elem = (UserGroup) it.next();
            if (elem.getId().equals(usergroup.getId())) {
                foundGroup=true;
                break;
            }
        }
        return foundGroup;
    }

    
       public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VDC)) {
            return false;
        }
        VDC other = (VDC)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }       
    
        /**
     * Holds value of property allowedFileGroups.
     */
    @ManyToMany(cascade={CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name="vdc_fileusergroup",
            joinColumns=@JoinColumn(name="vdc_id"),
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
    
    
   /**
     * Holds value of property allowedFileUsers.
     */
    @ManyToMany(cascade={ CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name="vdc_fileuser",
            joinColumns=@JoinColumn(name="vdc_id"),
            inverseJoinColumns=@JoinColumn(name="allowedfileusers_id"))
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
    
       public boolean areFilesRestrictedForUser(VDCUser user, UserGroup ipUserGroup) {
        if (this.filesRestricted) {
            if (user!=null) {
                // check network role
                if (user.getNetworkRole()!=null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN))
                {
                    return false;
                }

                // check vdc role
                VDCRole userRole = user.getVDCRole(this);
                if (userRole != null) {
                    String userRoleName = userRole.getRole().getName();
                    if (userRoleName.equals(RoleServiceLocal.ADMIN) || userRoleName.equals(RoleServiceLocal.CURATOR)) {
                        return false;
                    }
                }
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

    
    private Timestamp releaseDate;
    
    public Timestamp getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Timestamp releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    protected Timestamp createdDate;

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp creationDate) {
        this.createdDate = creationDate;
    }

    private String dvnDescription;

    public String getDvnDescription() {
        return dvnDescription;
    }

    public void setDvnDescription(String dvnDescription) {
        this.dvnDescription = dvnDescription;
    }


    @OneToOne(mappedBy = "vdc", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private VDCActivity vdcActivity;

    public VDCActivity getVDCActivity() {
        return vdcActivity;
    }

    public void setVDCActivity(VDCActivity vdcActivity) {
        this.vdcActivity = vdcActivity;
    }

    @OneToOne(mappedBy = "vdc", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private LockssConfig lockssConfig;

    public LockssConfig getLockssConfig() {
        return lockssConfig;
    }

    public void setLockssConfig(LockssConfig lockssConfig) {
        this.lockssConfig = lockssConfig;
    }
    

    /**
     * @return the allowStudyComments
     */
    public boolean isAllowStudyComments() {
        return allowStudyComments;
    }

    /**
     * @param allowStudyComments the allowStudyComments to set
     */
    public void setAllowStudyComments(boolean allowStudyComments) {
        this.allowStudyComments = allowStudyComments;
    }

        

    public Long getNumberReleasedStudies() {
        VDCServiceLocal vdcService = null;
        if (vdcService == null) {
            try {
                vdcService = (VDCServiceLocal) new InitialContext().lookup("java:comp/env/vdcService");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
        return vdcService.getReleasedStudyCount(id);
    }


}
