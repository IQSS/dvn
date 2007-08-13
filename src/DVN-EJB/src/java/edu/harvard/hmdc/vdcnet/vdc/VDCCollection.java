/*
 * VDCCollection.java
 *
 * Created on July 28, 2006, 2:41 PM
 *
 */

package edu.harvard.hmdc.vdcnet.vdc;

import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class VDCCollection {
    private String name;
    private String shortDesc;
    private String longDesc;
    @Column(columnDefinition="TEXT")
    private String query;
    
    @ManyToOne
    private VDC owner;
    
    @ManyToMany (cascade={CascadeType.REMOVE })
    @JoinTable(name="COLL_ADV_SEARCH_FIELDS",
            joinColumns=@JoinColumn(name="vdc_collection_id"),
            inverseJoinColumns=@JoinColumn(name="study_field_id"))
    private Collection<StudyField> advSearchFields;
    
    @ManyToMany (cascade={CascadeType.REMOVE })
    @JoinTable(name="COLL_ANY_SEARCH_FIELDS",
            joinColumns=@JoinColumn(name="vdc_collection_id"),
            inverseJoinColumns=@JoinColumn(name="study_field_id"))
    private Collection<StudyField> anySearchFields;
    
    @ManyToMany  (cascade={CascadeType.REMOVE })
    @JoinTable(name="COLL_SEARCH_RESULT_FIELDS",
            joinColumns=@JoinColumn(name="vdc_collection_id"),
            inverseJoinColumns=@JoinColumn(name="study_field_id"))
    private Collection<StudyField> searchResultFields;
    public Collection<Study> search(String query) {
        //TODO: complete implementation
        return null;
    }
    /** Creates a new instance of VDCCollection */
    public VDCCollection() {
    }
    
    public String getName() {
        
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getShortDesc() {
        return shortDesc;
    }
    
    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }
    
    public String getLongDesc() {
        return longDesc;
    }
    
    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }
    
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
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
   @SequenceGenerator(name="vdccollection_gen", sequenceName="vdccollection_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="vdccollection_gen")    
    
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
@OneToMany(mappedBy="parentCollection",cascade={CascadeType.REMOVE,CascadeType.MERGE ,CascadeType.PERSIST })
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
 
    @ManyToOne (cascade=CascadeType.MERGE)
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
    @ManyToMany (cascade={CascadeType.MERGE })
    @JoinTable(name="COLL_STUDIES",
            joinColumns=@JoinColumn(name="vdc_collection_id"),
            inverseJoinColumns=@JoinColumn(name="study_id"))    
    @OrderBy ("title")
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
    

    @ManyToMany //(cascade={CascadeType.REMOVE })
    @JoinTable(name="VDC_LINKED_COLLECTIONS",
            joinColumns=@JoinColumn(name="linked_collection_id"),
            inverseJoinColumns=@JoinColumn(name="vdc_id"))    
    private Collection<VDC> linkedVDCs;    

    public Collection<VDC> getLinkedVDCs() {
        return linkedVDCs;
    }

    public void setLinkedVDCs(Collection<VDC> linkedVDCs) {
        this.linkedVDCs = linkedVDCs;
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
     * Holds value of property visible.
     */
    private boolean visible;

    /**
     * Getter for property visible.
     * @return Value of property visible.
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Setter for property visible.
     * @param visible New value of property visible.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setParentRelationship (VDCCollection parentColl) {
        if (parentColl == null) {
            return;  // nothing to set
        }
        
        this.setParentCollection( parentColl );
        if (parentColl.getSubCollections() == null) {
            parentColl.setSubCollections( new ArrayList() );
        }
        
        parentColl.getSubCollections().add(this);
    }
    
    public void removeParentRelationship() {
        VDCCollection parentColl = this.getParentCollection();
        if (parentCollection != null) {
            Iterator iter = parentColl.getSubCollections().iterator();
            while (iter.hasNext()) {
                VDCCollection subColl = (VDCCollection) iter.next();
                if ( subColl.getId().equals( this.getId() ) ) {
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
        VDCCollection other = (VDCCollection)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }       
    
}
