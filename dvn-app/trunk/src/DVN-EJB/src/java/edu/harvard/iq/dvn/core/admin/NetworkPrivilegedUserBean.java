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
