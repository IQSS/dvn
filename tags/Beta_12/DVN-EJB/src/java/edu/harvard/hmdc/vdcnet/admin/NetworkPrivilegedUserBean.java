/*
 * NetworkPrivilegedUserBean.java
 *
 * Created on October 30, 2006, 7:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

/**
 *
 * @author Ellen Kraffmiller
 */
public class NetworkPrivilegedUserBean {
    
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
