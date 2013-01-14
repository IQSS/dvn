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
 * VDCRoleId.java
 *
 * Created on August 10, 2006, 4:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.admin;

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
