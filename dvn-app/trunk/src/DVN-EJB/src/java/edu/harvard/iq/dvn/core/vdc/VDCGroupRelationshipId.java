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
