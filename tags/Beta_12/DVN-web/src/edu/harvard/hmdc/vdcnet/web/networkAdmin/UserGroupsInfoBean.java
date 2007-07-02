package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import edu.harvard.hmdc.vdcnet.admin.LoginDomain;
import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import java.util.Iterator;
/*
 * UserGroupsInfoBean.java
 *
 * Created on November 1, 2006, 12:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Ellen Kraffmiller
 */
public class UserGroupsInfoBean {
    
    /** Creates a new instance of UserGroupsInfoBean */
    public UserGroupsInfoBean() {
    }
    
    public UserGroupsInfoBean(UserGroup group) {
        this.group = group;
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

   
    /**
     * Getter for property details.
     * @return Value of property details.
     */
    public String getDetails() {
        String str = "";
        for (Iterator it = group.getLoginDomains().iterator(); it.hasNext();) {
            LoginDomain elem = (LoginDomain) it.next();
            str+=elem.getIpAddress();
            if (it.hasNext()) {
                str+=", ";
            }   
        }
        if (group.getLoginDomains().size()>0 && group.getUsers().size()>0) {
            str+="; ";
        }
        for (Iterator it2 = group.getUsers().iterator(); it2.hasNext();) {
            VDCUser elem = (VDCUser) it2.next();
            str+=elem.getUserName();
            if (it2.hasNext()) {
                str+=", ";
            }   
        }
        return str;
    }

  
    
}
