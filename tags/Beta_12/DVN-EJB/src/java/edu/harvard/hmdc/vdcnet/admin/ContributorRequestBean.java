/*
 * ContributorRequestBean.java
 *
 * Created on October 23, 2006, 2:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import edu.harvard.hmdc.vdcnet.admin.RoleRequest;

/**
 *
 * @author Ellen Kraffmiller
 */
public class ContributorRequestBean {
    
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
