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
