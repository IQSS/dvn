/*
 * VDCCollRelationship.java
 *
 * Created on August 14, 2006, 10:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
@IdClass(VDCGroupRelationshipId.class)
public class VDCGroupRelationship {
    @Column(name="group_id", insertable=false, updatable=false)
    @Id private Long groupId;
    
    @Column(name="subgroup_id", insertable=false, updatable=false)
    @Id private Long subGroupId;
       
    /** Creates a new instance of VDCCollRelationship */
    public VDCGroupRelationship() {
    }

    /**
     * Holds value of property group.
     */
    @ManyToOne
    @JoinColumn (name="group_id")
    private VDCGroup group;

    /**
     * Getter for property vdcCollection.
     * @return Value of property vdcCollection.
     */
    public VDCGroup getGroup() {
        return this.group;
    }

    /**
     * Setter for property vdcCollection.
     * @param vdcCollection New value of property vdcCollection.
     */
    public void setGroup(VDCGroup group) {
        this.group = group;
    }

    /**
     * Holds value of property subGroup.
     */
    @ManyToOne
    @JoinColumn(name="subgroup_id")
    private VDCGroup subGroup;

    /**
     * Getter for property subCollection.
     * @return Value of property subCollection.
     */
    public VDCGroup getSubGroup() {
        return this.subGroup;
    }

    /**
     * Setter for property subCollection.
     * @param subCollection New value of property subCollection.
     */
    public void setSubGroup(VDCGroup subGroup) {
        this.subGroup = subGroup;
    }
    
}
