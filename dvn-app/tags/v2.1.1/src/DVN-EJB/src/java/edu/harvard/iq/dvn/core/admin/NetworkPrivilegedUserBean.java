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
 * NetworkPrivilegedUserBean.java
 *
 * Created on October 30, 2006, 7:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.admin;

/**
 *
 * @author Ellen Kraffmiller
 */
public class NetworkPrivilegedUserBean implements java.io.Serializable  {
    
    /** Creates a new instance of NetworkPrivilegedUserBean */
    public NetworkPrivilegedUserBean(VDCUser user, Long networkRoleId) {
        this.user=user;
        this.networkRoleId=networkRoleId;
    }

    /**
     * Holds value of property user.
     */
    private VDCUser user;

    /**
     * Getter for property user.
     * @return Value of property user.
     */
    public VDCUser getUser() {
        return this.user;
    }

    /**
     * Setter for property user.
     * @param user New value of property user.
     */
    public void setUser(VDCUser user) {
        this.user = user;
    }

    /**
     * Holds value of property networkRoleId.
     */
    private Long networkRoleId;

    /**
     * Getter for property networkRoleId.
     * @return Value of property networkRoleId.
     */
    public Long getNetworkRoleId() {
        return this.networkRoleId;
    }

    /**
     * Setter for property networkRoleId.
     * @param networkRoleId New value of property networkRoleId.
     */
    public void setNetworkRoleId(Long networkRoleId) {
        this.networkRoleId = networkRoleId;
    }
    
}
