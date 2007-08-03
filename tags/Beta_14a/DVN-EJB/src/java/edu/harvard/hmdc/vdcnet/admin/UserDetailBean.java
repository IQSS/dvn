/*
 * UserDetailBean.java
 *
 * Created on November 2, 2006, 2:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

/**
 *
 * @author Ellen Kraffmiller
 */
public class UserDetailBean {
    
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
