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
 * ContributorRequestBean.java
 *
 * Created on October 23, 2006, 2:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.admin;



/**
 *
 * @author Ellen Kraffmiller
 */
public class ContributorRequestBean implements java.io.Serializable {
    
    /**
     * Creates a new instance of ContributorRequestBean
     */
    public ContributorRequestBean() {
    }

    /**
     * Holds value of property roleRequest.
     */
    private RoleRequest roleRequest;

    /**
     * Getter for property user.
     * @return Value of property user.
     */
    public RoleRequest getRoleRequest() {
        return this.roleRequest;
    }

    /**
     * Setter for property user.
     * @param user New value of property user.
     */
    public void setRoleRequest(RoleRequest roleRequest) {
        this.roleRequest = roleRequest;
    }

    /**
     * Holds value of property accept.
     */
    private Boolean accept;

    /**
     * Getter for property accept.
     * @return Value of property accept.
     */
    public Boolean getAccept() {
        return this.accept;
    }

    /**
     * Setter for property accept.
     * @param accept New value of property accept.
     */
    public void setAccept(Boolean accept) {
        this.accept = accept;
    }

    public ContributorRequestBean( RoleRequest roleRequest) {
        this.roleRequest=roleRequest;
    }
    
}
