/*
 * VDCGroup.java
 *
 * Created on August 2, 2006, 4:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class VDCGroup implements Serializable {
    private int displayOrder;
      @OneToMany(mappedBy="group", cascade=CascadeType.PERSIST)
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
 
    public java.util.List<edu.harvard.hmdc.vdcnet.vdc.VDCGroupRelationship> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(java.util.List<edu.harvard.hmdc.vdcnet.vdc.VDCGroupRelationship> subGroups) {
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
    
/** ********************* NEW FOR NETWORK PAGE UI CHANGES -wjb May 2007 ******************** */
      
    @ManyToMany(mappedBy="vdcGroups")
    @OrderBy("name ASC")
    private java.util.List<VDC> vdcs;

    /**
     * Getter for property memberVdcs.
     * @return Value of property memberVdcs.
     */
    public java.util.List<VDC> getVdcs() {
        return this.vdcs;
    }

    /**
     * Setter for property memberVdcs.
     * @param memberVdcs New value of property memberVdcs.
     */
    public void setVdcs(java.util.List<VDC> memberVdcs) {
        this.vdcs = memberVdcs;
    }

    /**
     * Holds value of property name.
     */
    private String name;

    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Holds value of property description.
     */
    private String description;

    /**
     * Getter for property description.
     * @return Value of property description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Setter for property description.
     * @param description New value of property description.
     */
    public void setDescription(String description) {
        this.description = description;
    }  
    
    @Transient
    private boolean selected = false;
    
    public boolean getSelected() {
        return this.selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
