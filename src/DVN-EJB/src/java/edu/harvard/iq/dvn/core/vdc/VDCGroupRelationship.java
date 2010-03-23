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
 * VDCCollRelationship.java
 *
 * Created on August 14, 2006, 10:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.vdc;

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
public class VDCGroupRelationship implements java.io.Serializable  {
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
    @JoinColumn (name="group_id", nullable=false)
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
    @JoinColumn(name="subgroup_id", nullable=false)
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
