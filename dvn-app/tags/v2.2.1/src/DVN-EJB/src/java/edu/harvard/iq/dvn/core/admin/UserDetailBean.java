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
 * UserDetailBean.java
 *
 * Created on November 2, 2006, 2:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.admin;

/**
 *
 * @author Ellen Kraffmiller
 */
public class UserDetailBean  implements java.io.Serializable {
    
    /**
     * Creates a new instance of UserDetailBean
     */
    public UserDetailBean() {
    }

    /**
     * Holds value of property userName.
     */
    private String userName;

    /**
     * Getter for property userName.
     * @return Value of property userName.
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Setter for property userName.
     * @param userName New value of property userName.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Holds value of property valid.
     */
    private boolean valid=true;

    /**
     * Getter for property valid.
     * @return Value of property valid.
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     * Setter for property valid.
     * @param valid New value of property valid.
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * Holds value of property duplicate.
     */
    private boolean duplicate;

    /**
     * Getter for property duplicate.
     * @return Value of property duplicate.
     */
    public boolean isDuplicate() {
        return this.duplicate;
    }

    /**
     * Setter for property duplicate.
     * @param duplicate New value of property duplicate.
     */
    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }
    
}
