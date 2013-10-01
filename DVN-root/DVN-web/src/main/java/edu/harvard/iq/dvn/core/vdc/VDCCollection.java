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
 * VDCCollection.java
 *
 * Created on July 28, 2006, 2:41 PM
 *
 */
package edu.harvard.iq.dvn.core.vdc;

import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyField;
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
public class VDCCollection implements java.io.Serializable {

    public static final String STATIC = "static";
    public static final String DYNAMIC = "dynamic";

    public static final int LOCALSCOPE = 0; 
    public static final int SUBNETWORKSCOPE = 1; 
    public static final int ENTIRENETWORKSCOPE = 2;
    

    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    private String query;
    private int scope;
    private String type;
    @ManyToOne
    private VDC owner;
    @ManyToMany(cascade = {CascadeType.REMOVE})
    @JoinTable(name = "COLL_ADV_SEARCH_FIELDS",
    joinColumns = @JoinColumn(name = "vdc_collection_id"),
    inverseJoinColumns = @JoinColumn(name = "study_field_id"))
    private Collection<StudyField> advSearchFields;
    @ManyToMany(cascade = {CascadeType.REMOVE})
    @JoinTable(name = "COLL_ANY_SEARCH_FIELDS",
    joinColumns = @JoinColumn(name = "vdc_collection_id"),
    inverseJoinColumns = @JoinColumn(name = "study_field_id"))
    private Collection<StudyField> anySearchFields;
    @ManyToMany(cascade = {CascadeType.REMOVE})
    @JoinTable(name = "COLL_SEARCH_RESULT_FIELDS",
    joinColumns = @JoinColumn(name = "vdc_collection_id"),
    inverseJoinColumns = @JoinColumn(name = "study_field_id"))
    private Collection<StudyField> searchResultFields;

    public Collection<Study> search(String query) {
        //TODO: complete implementation
        return null;
    }

    /** Creates a new instance of VDCCollection */
    public VDCCollection() {
        this.type = STATIC;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getScope() {
        return scope;
    }
    
    public void setScope(int newScope) {
        this.scope = newScope; 
    }
    
    public boolean isLocalScope() {
        return (scope == LOCALSCOPE);
    }
    
    public void setLocalScope() {
        this.scope = LOCALSCOPE;
    }
    
    public boolean isSubnetworkScope() {
        return (scope == SUBNETWORKSCOPE);
    }
    
    public void setSubnetworkScope() {
        this.scope = SUBNETWORKSCOPE;
    }
    
    public boolean isEntireNetworkScope() {
        return (scope == ENTIRENETWORKSCOPE);
    }
    
    public void setEntireNetworkScope() {
        this.scope = ENTIRENETWORKSCOPE;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public VDC getOwner() {
        return owner;
    }

    public void setOwner(VDC owner) {
        this.owner = owner;
    }
    /**
     * Holds value of property displayOrder.
     */
    private int displayOrder;

    /**
     * Getter for property order.
     * @return Value of property order.
     */
    public int getDisplayOrder() {
        return this.displayOrder;
    }

    /**
     * Setter for property order.
     * @param order New value of property order.
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
     * Holds value of property subCollections.
     */
    @OneToMany(mappedBy = "parentCollection", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("name ASC")
    private Collection<VDCCollection> subCollections;

    /**
     * Getter for property vdcCollRelationships.
     * @return Value of property vdcCollRelationships.
     */
    public Collection<VDCCollection> getSubCollections() {
        return this.subCollections;
    }

    /**
     * Setter for property vdcCollRelationships.
     * @param vdcCollRelationships New value of property vdcCollRelationships.
     */
    public void setSubCollections(Collection<VDCCollection> subCollections) {
        this.subCollections = subCollections;
    }
    @ManyToOne(cascade = CascadeType.MERGE)
    private VDCCollection parentCollection;

    public VDCCollection getParentCollection() {
        return parentCollection;
    }

    public void setParentCollection(VDCCollection parentCollection) {
        this.parentCollection = parentCollection;
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
     * Holds value of property studies.
     */
    @ManyToMany
    @JoinTable(name = "COLL_STUDIES",
    joinColumns = @JoinColumn(name = "vdc_collection_id"),
    inverseJoinColumns = @JoinColumn(name = "study_id"))
    private List<Study> studies;

    /**
     * Getter for property studies.
     * @return Value of property studies.
     */
    public List<Study> getStudies() {
        return this.studies;
    }

    /**
     * Setter for property studies.
     * @param studies New value of property studies.
     */
    public void setStudies(List<Study> studies) {
        this.studies = studies;
    }
    private transient int level;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getFullCollectionName() {
        return getOwner().getName() + " Dataverse/" + getName();
    }
    @ManyToMany //(cascade={CascadeType.REMOVE })
    @JoinTable(name = "VDC_LINKED_COLLECTIONS",
    joinColumns = @JoinColumn(name = "linked_collection_id"),
    inverseJoinColumns = @JoinColumn(name = "vdc_id"))
    private Collection<VDC> linkedVDCs;

    public Collection<VDC> getLinkedVDCs() {
        return linkedVDCs;
    }

    public void setLinkedVDCs(Collection<VDC> linkedVDCs) {
        this.linkedVDCs = linkedVDCs;
    }

    public void setParentRelationship(VDCCollection parentColl) {
        if (parentColl == null) {
            return;  // nothing to set
        }

        this.setParentCollection(parentColl);
        if (parentColl.getSubCollections() == null) {
            parentColl.setSubCollections(new ArrayList());
        }

        parentColl.getSubCollections().add(this);
    }

    public void removeParentRelationship() {
        VDCCollection parentColl = this.getParentCollection();
        if (parentCollection != null) {
            Iterator iter = parentColl.getSubCollections().iterator();
            while (iter.hasNext()) {
                VDCCollection subColl = (VDCCollection) iter.next();
                if (subColl.getId().equals(this.getId())) {
                    iter.remove();
                    break;
                }
            }

            this.setParentCollection(null);
        }
    }

    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VDCCollection)) {
            return false;
        }
        VDCCollection other = (VDCCollection) object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public boolean isDynamic() {
        return (this.getType() != null && this.getType().equals("dynamic"));
    }

    public boolean isRootCollection() {
        return (this.getParentCollection() == null);
    }
}
