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
 * AllUsersDataBean.java
 *
 * Created on October 27, 2006, 12:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.networkAdmin;

import edu.harvard.iq.dvn.core.admin.NetworkRoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCRole;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import java.util.Iterator;
import javax.naming.InitialContext;

/**
 *
 * @author Ellen Kraffmiller
 */
public class AllUsersDataBean implements java.io.Serializable  {
    private UserServiceLocal userService;    
    /**
     * Creates a new instance of AllUsersDataBean
     */
    public AllUsersDataBean() {
    }
    
    private Long userId;
    private Long defaultNetworkAdminId;

    /**
     * Holds value of property user.
     */
    private VDCUser user;

    /**
     * Getter for property user. init if necessary
     * @return Value of property user.
     */
    public VDCUser getUser() {
        if (this.user != null){
            return this.user;
        } else {
            return this.user = initUser(userId);
        }       
    }

    
    private VDCUser initUser(Long id){
        if (userService == null){
            initUserService();
        }
        return userService.find(id);       
    }
    
    private void initUserService() {
        if (userService == null) {
            try {
                userService = (UserServiceLocal) new InitialContext().lookup("java:comp/env/vdcUserService");
            } catch (Exception e) {
               
                e.printStackTrace();
            }
        }
    }
    /**
     * Setter for property user.
     * @param user New value of property user.
     */
    public void setUser(VDCUser user) {
        this.user = user;
    }
    
    public String getRoles() {
        
        String str="";
        if (user.getNetworkRole()!=null) {
            if (user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN)) {
                str+= " Network Admin";
                return str;
            } else if (user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.CREATOR)){
                str+=" Dataverse Creator";
            } else {
                str+=" "+user.getNetworkRole().getName();
            }
        }
        if (user.getVdcRoles().size()>0 && user.getNetworkRole()!=null) {
            str+="; ";
        }
        for (Iterator<VDCRole> it2 = user.getVdcRoles().iterator(); it2.hasNext();) { 
            VDCRole role =  it2.next();
            str+= role.getVdc().getName()+" "+role.getRole().getName();
            if (it2.hasNext()) {
                str+=", ";
            }
            
        }
       return str;
    }
    
    public AllUsersDataBean(Long id, Long defaultId){
        this.userId = id;
        this.defaultNetworkAdminId = defaultId;
    }
   

    public AllUsersDataBean(VDCUser user, boolean defaultNetworkAdmin) {
        this.user=user; 
        this.defaultNetworkAdmin = defaultNetworkAdmin;       
    } 

    private Boolean defaultNetworkAdmin;
  
    /**
     * Getter for property defaultNetworkAdmin.
     * @return Value of property defaultNetworkAdmin.
     */
    public boolean isDefaultNetworkAdmin() {
        if (this.user ==null){
            initUser(this.userId);
        }
        this.defaultNetworkAdmin = this.user.getNetworkRole()!=null
                    && this.user.getId().equals(defaultNetworkAdminId);
        return defaultNetworkAdmin;
    }

   
}
