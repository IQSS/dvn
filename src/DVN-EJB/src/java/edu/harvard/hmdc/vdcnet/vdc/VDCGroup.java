/*
 * VDCGroup.java
 *
 * Created on August 2, 2006, 4:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class VDCGroup {
    private int displayOrder;
      @OneToMany(mappedBy="group")
    private List<VDCGroupRelationship> subGroups;
    
    /** Creates a new instance of VDCGroup */
    public VDCGroup() {
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

  

    /**
     * Holds value of property id.
     */
   @SequenceGenerator(name="vdcgroup_gen", sequenceName="vdcgroup_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="vdcgroup_gen")    
    
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
 
    public List<VDCGroupRelationship> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(List<VDCGroupRelationship> subGroups) {
        this.subGroups = subGroups;
    }
    
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VDCGroup)) {
            return false;
        }
        VDCGroup other = (VDCGroup)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }       
    
    
}
