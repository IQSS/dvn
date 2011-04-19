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
 * VDCCollRelationshipId.java
 *
 * Created on August 14, 2006, 10:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.vdc;

/**
 *
 * @author Ellen Kraffmiller
 */
public class VDCGroupRelationshipId implements java.io.Serializable  {
    
    /** Creates a new instance of VDCCollRelationshipId */
    public VDCGroupRelationshipId() {
    }

    /**
     * Holds value of property groupId.
     */
    private Long groupId;

    /**
     * Getter for property collectionId.
     * @return Value of property collectionId.
     */
    public Long getGroupId() {
        return this.groupId;
    }

    /**
     * Setter for property collectionId.
     * @param collectionId New value of property collectionId.
     */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /**
     * Holds value of property subGroupId.
     */
    private Long subGroupId;

    /**
     * Getter for property subCollectionId.
     * @return Value of property subCollectionId.
     */
    public Long getSubGroupId() {
        return this.subGroupId;
    }

    /**
     * Setter for property subCollectionId.
     * @param subCollectionId New value of property subCollectionId.
     */
    public void setSubGroupId(Long subGroupId) {
        this.subGroupId = subGroupId;
    }
    
}
