/*
 * PermissionBean.java
 *
 * Created on November 3, 2006, 11:37 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;

/**
 *
 * @author Ellen Kraffmiller
 */
public class PermissionBean {
    
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
