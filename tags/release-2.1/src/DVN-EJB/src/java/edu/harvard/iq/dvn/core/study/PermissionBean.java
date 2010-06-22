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
 * PermissionBean.java
 *
 * Created on November 3, 2006, 11:37 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.VDCUser;

/**
 *
 * @author Ellen Kraffmiller
 */
public class PermissionBean  implements java.io.Serializable {
    
    /** Creates a new instance of PermissionBean */
    public PermissionBean() {
    }

    public PermissionBean(VDCUser user) {
        this.user=user;
    }
    
    public PermissionBean(UserGroup group) {
        this.group=group;
    }
    /**
     * Holds value of property checked.
     */
    private boolean checked;

    /**
     * Getter for property removePermission.
     * @return Value of property removePermission.
     */
    public boolean isChecked() {
        return this.checked;
    }

    /**
     * Setter for property removePermission.
     * @param removePermission New value of property removePermission.
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
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
     * Holds value of property group.
     */
    private UserGroup group;

    /**
     * Getter for property group.
     * @return Value of property group.
     */
    public UserGroup getGroup() {
        return this.group;
    }

    /**
     * Setter for property group.
     * @param group New value of property group.
     */
    public void setGroup(UserGroup group) {
        this.group = group;
    }
    
}
