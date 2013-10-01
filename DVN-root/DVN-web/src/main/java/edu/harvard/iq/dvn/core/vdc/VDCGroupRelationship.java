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
