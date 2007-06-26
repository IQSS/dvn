/*
 * VDC.java
 *
 * Created on July 28, 2006, 2:22 PM
 *
 */

package edu.harvard.hmdc.vdcnet.vdc;

import edu.harvard.hmdc.vdcnet.admin.RoleRequest;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.VDCRole;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyField;
import edu.harvard.hmdc.vdcnet.study.Template;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class VDC {
    private String name;
    private String description;
    private String visibility;
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
    
    @Column(name="termsOfUse", columnDefinition="TEXT")
    private String termsOfUse;
    private boolean termsOfUseEnabled;
    
    private String copyright;
    @OneToOne (cascade=CascadeType.REMOVE)
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
    
    
    
    public Collection<Study> search(String query) {
        //TODO: complete implementation
        return null;
    }
    /** Creates a new instance of VDC */
    public VDC() {
        
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
    
    public String getTermsOfUse() {
        return termsOfUse;
    }
    
    public void setTermsOfUse(String termsOfUse) {
        this.termsOfUse = termsOfUse;
    }

    public boolean isTermsOfUseEnabled() {
        return termsOfUseEnabled;
    }
    
    public void setTermsOfUseEnabled(boolean termsOfUseEnabled) {
        this.termsOfUseEnabled = termsOfUseEnabled;
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
    @ManyToMany(cascade={CascadeType.REMOVE })
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
     * Holds value of property reviewState.
     */
    @ManyToOne
    private ReviewState reviewState;
    
    /**
     * Getter for property reviewState.
     * @return Value of property reviewState.
     */
    public ReviewState getReviewState() {
        return this.reviewState;
    }
    
    /**
     * Setter for property reviewState.
     * @param reviewState New value of property reviewState.
     */
    public void setReviewState(ReviewState reviewState) {
        this.reviewState = reviewState;
    }
    
    /**
     * Holds value of property id.
     */
    @SequenceGenerator(name="vdc_gen", sequenceName="vdc_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="vdc_gen")
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
    
    @OneToMany(mappedBy="owner", cascade={ CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
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
    private Collection<VDCRole> vdcRoles;

    /**
     * Getter for property vdcRoles.
     * @return Value of property vdcRoles.
     */
    public Collection<VDCRole> getVdcRoles() {
        return this.vdcRoles;
    }

    /**
     * Setter for property vdcRoles.
     * @param vdcRoles New value of property vdcRoles.
     */
    public void setVdcRoles(Collection<VDCRole> vdcRoles) {
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

    @ManyToMany(mappedBy="linkedVDCs",cascade={CascadeType.REMOVE })
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

    public boolean isHarvestingDataverse() {
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
}
