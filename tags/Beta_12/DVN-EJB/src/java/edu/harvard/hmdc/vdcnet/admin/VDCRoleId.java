/*
 * VDCRoleId.java
 *
 * Created on August 10, 2006, 4:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import java.io.Serializable;

/**
 *
 * @author Ellen Kraffmiller
 */
public class VDCRoleId implements Serializable {
    
    /** Creates a new instance of VDCRoleId */
    public VDCRoleId() {
    }

    /**
     * Holds value of property roleId.
     */
    private Long roleId;

    /**
     * Getter for property employeeId.
     * @return Value of property employeeId.
     */
    public Long getRoleId() {
        return this.roleId;
    }

    /**
     * Setter for property employeeId.
     * @param employeeId New value of property employeeId.
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * Holds value of property vdcId.
     */
     private Long vdcId;

    /**
     * Getter for property vdcId.
     * @return Value of property vdcId.
     */
    public Long getVdcId() {
        return this.vdcId;
    }

    /**
     * Setter for property vdcId.
     * @param vdcId New value of property vdcId.
     */
    public void setVdcId(Long vdcId) {
        this.vdcId = vdcId;
    }

    /**
     * Holds value of property vdcUserId.
     */
    private Long vdcUserId;

    /**
     * Getter for property vdcUserId.
     * @return Value of property vdcUserId.
     */
    public Long getVdcUserId() {
        return this.vdcUserId;
    }

    /**
     * Setter for property vdcUserId.
     * @param vdcUserId New value of property vdcUserId.
     */
    public void setVdcUserId(Long vdcUserId) {
        this.vdcUserId = vdcUserId;
    }
    
}
