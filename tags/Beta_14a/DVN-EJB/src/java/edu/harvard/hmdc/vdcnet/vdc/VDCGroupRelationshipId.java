/*
 * VDCCollRelationshipId.java
 *
 * Created on August 14, 2006, 10:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

/**
 *
 * @author Ellen Kraffmiller
 */
public class VDCGroupRelationshipId {
    
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
