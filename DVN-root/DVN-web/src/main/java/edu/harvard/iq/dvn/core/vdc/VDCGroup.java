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
 * VDCGroup.java
 *
 * Created on August 2, 2006, 4:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.vdc;

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
 
    public java.util.List<edu.harvard.iq.dvn.core.vdc.VDCGroupRelationship> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(java.util.List<edu.harvard.iq.dvn.core.vdc.VDCGroupRelationship> subGroups) {
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

    public Integer getVdcCount() {
        return this.vdcs.size();
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
    
    private Long defaultDisplayNumber;

    public void setDefaultDisplayNumber(Long defaultDisplayNumber) {
        this.defaultDisplayNumber = defaultDisplayNumber;
    }

    public Long getDefaultDisplayNumber() {
        return defaultDisplayNumber;
    }

    private Long parent;

    /**
     * v1.4 Get the value of parent
     *
     * Introduced in v 1.4. This
     * property represents the hierarchy
     * designed for the network home page
     *
     * @return the value of parent
     *
     * @author wbossons
     */
    public Long getParent() {
        return parent;
    }

    /**
     * Set the value of parent
     *
     * @param parent new value of parent
     */
    public void setParent(Long parent) {
        this.parent = parent;
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
