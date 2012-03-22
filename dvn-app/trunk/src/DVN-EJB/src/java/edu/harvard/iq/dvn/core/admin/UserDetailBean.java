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
