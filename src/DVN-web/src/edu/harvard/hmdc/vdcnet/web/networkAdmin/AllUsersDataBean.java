/*
 * AllUsersDataBean.java
 *
 * Created on October 27, 2006, 12:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCRole;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import java.util.Iterator;

/**
 *
 * @author Ellen Kraffmiller
 */
public class AllUsersDataBean {
    
    /**
     * Creates a new instance of AllUsersDataBean
     */
    public AllUsersDataBean() {
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
    
    public String getRoles() {
        
        String str="";
        if (user.getNetworkRole()!=null) {
            if (user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN)) {
                str+= " Network Admin";
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
    
   

    public AllUsersDataBean(VDCUser user, boolean defaultNetworkAdmin) {
        this.user=user; 
        this.defaultNetworkAdmin = defaultNetworkAdmin;
       
    } 

    private boolean defaultNetworkAdmin;
  
    /**
     * Getter for property defaultNetworkAdmin.
     * @return Value of property defaultNetworkAdmin.
     */
    public boolean isDefaultNetworkAdmin() {
        return defaultNetworkAdmin;
    }

   
}
