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
 * NetworkPrivilegedUsersPage.java
 *
 * Created on October 20, 2006, 3:10 PM
 * 
 */
package edu.harvard.iq.dvn.core.web.networkAdmin;

import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.admin.EditNetworkPrivilegesService;
import edu.harvard.iq.dvn.core.admin.NetworkPrivilegedUserBean;
import edu.harvard.iq.dvn.core.admin.NetworkRole;
import edu.harvard.iq.dvn.core.admin.NetworkRoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIData;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
@ViewScoped
@Named("NetworkPrivilegedUsersPage")
public class NetworkPrivilegedUsersPage extends VDCBaseBean implements java.io.Serializable  {
    @EJB  EditNetworkPrivilegesService privileges;
    @EJB  NetworkRoleServiceLocal networkRoleService;
    @EJB  UserServiceLocal userService;

   public void init() {
        super.init();

                     
    }
        
   
   
    
    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public NetworkPrivilegedUsersPage() {
    }



    /** 
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    public void preprocess() {
    }

    /** 
     * <p>Callback method that is called just before rendering takes place.
     * This method will <strong>only</strong> be called for the page that
     * will actually be rendered (and not, for example, on a page that
     * handled a postback and then navigated to a different page).  Customize
     * this method to allocate resources that will be required for rendering
     * this page.</p>
     */
    public void prerender() {
    }

    /** 
     * <p>Callback method that is called after rendering is completed for
     * this request, if <code>init()</code> was called (regardless of whether
     * or not this was the page that was actually rendered).  Customize this
     * method to release resources acquired in the <code>init()</code>,
     * <code>preprocess()</code>, or <code>prerender()</code> methods (or
     * acquired during execution of an event handler).</p>
     */
    public void preRenderView() {
        if ( isFromPage("NetworkPrivilegedUsersPage") && sessionGet(privileges.getClass().getName())!=null) {
            privileges = (EditNetworkPrivilegesService) sessionGet(privileges.getClass().getName());
            System.out.println("Getting stateful session bean editNetworkPrivileges ="+getPrivileges());
            
        } else {
             
            System.out.println("Putting stateful session bean in request, editNetworkPrivileges ="+getPrivileges());
            privileges.init();
            sessionPut( getPrivileges().getClass().getName(),privileges);
            //sessionPut( (studyService.getClass().getName() + "."  + studyId.toString()), studyService);
        }

    } 
    
    public void destroy() {
    }

    public EditNetworkPrivilegesService getPrivileges() {
        return privileges;
    }

    public void setPrivileges(EditNetworkPrivilegesService privileges) {
        this.privileges = privileges;
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
    
    
    private String TOUuserName; 
    
    public String getTOUUserName() {
        return this.TOUuserName; 
    }
    public void setTOUUserName(String name) {
        this.TOUuserName = name; 
    }
    
    private UIData userTable;
    
     public javax.faces.component.UIData getUserTable() {
        return userTable;
    }

    public void setUserTable(javax.faces.component.UIData userTable) {
        this.userTable = userTable;
    }

    private UIData TOUuserTable;
    
    public javax.faces.component.UIData getUserTOUTable() {
        return TOUuserTable;
    }

    public void setUserTOUTable(javax.faces.component.UIData tut) {
        this.TOUuserTable = tut;
    }

    public List getRoleSelectItems() {
        List selectItems = new ArrayList();
        NetworkRole role = networkRoleService.findByName(NetworkRoleServiceLocal.CREATOR);
        selectItems.add(new SelectItem(role.getId(), "Dataverse Creator"));
        selectItems.add(new SelectItem(networkRoleService.findByName(NetworkRoleServiceLocal.ADMIN).getId(), "Network Admin"));
        return selectItems;
    }
      
    public void clearRole(ActionEvent ea) {
        NetworkPrivilegedUserBean user = (NetworkPrivilegedUserBean)userTable.getRowData();
        user.setNetworkRoleId(null);
    }
    
    public void clearTOURole(ActionEvent ea) {
        VDCUser user = (VDCUser)TOUuserTable.getRowData();
        user.setBypassTermsOfUse(false);
    }
    
    public void addUser(ActionEvent ae) {
        VDCUser user = null;
         // The following is a workaround for the issue
        // of single quotes in form fields. In this instance
        // it is throwing a sql exception because the ' is 
        // terminating the query prematurely, leaving invalid chars in the query.
        // TBD: A better fix for this issue.
        if (userName.indexOf("'") != -1) {
            setUserNotFound(true);
        } else {
            user = userService.findByUserName(userName);
            if (user==null) {
                setUserNotFound(true);
            } else {
                this.privileges.addPrivilegedUser(user.getId());
            }
        }
    } 
    
    public void addTOUUser(ActionEvent ae) {
        VDCUser user = null; 
        // See comment in the addUser method!
        if (TOUuserName.indexOf("'") != -1) {
            setTOUUserNotFound(true);
        } else {
            user = userService.findByUserName(TOUuserName);
            if (user==null) {
                setTOUUserNotFound(true);
            } else {
                this.privileges.addTOUPrivilegedUser(user.getId());
            }
        }
    }

    public String save() {
        HttpServletRequest request = (HttpServletRequest)this.getExternalContext().getRequest();
        String hostName=request.getLocalName();
        int port = request.getLocalPort();
        String portStr="";
        if (port!=80) {
            portStr=":"+port;
        }
        // Needed to send an approval email to approved creators
        String creatorUrl = "http://"+hostName+portStr+request.getContextPath()+"/faces/site/AddSitePage.xhtml";
        privileges.save(creatorUrl);
        privileges.init();
        getExternalContext().getFlash().put("successMessage", "Successfully updated network permissions.");
        return "myNetworkOptions";
    } 

    
    public void test(ActionEvent ae) {
        System.out.println("this is a test");
    }

    private boolean userNotFound;

    public boolean isUserNotFound() {
        return userNotFound;
    }

    public void setUserNotFound(boolean userNotFound) {
        this.userNotFound = userNotFound;
    }
    
    private boolean TOUuserNotFound; 
    
    public boolean isTOUUserNotFound() {
        return TOUuserNotFound; 
    }
    
    public void setTOUUserNotFound(boolean userNotFound) {
        this.TOUuserNotFound = userNotFound; 
    }

    public boolean getDisplayPrivilegedUsers() {
        return getPrivileges().getPrivilegedUsers().size()>1;
    }
    
    public boolean getDisplayTOUPrivilegedUsers() {
        return getPrivileges().getTOUPrivilegedUsers().size()>0;
    }
    /**
     * Holds value of property success.
     */
    private boolean success;

    /**
     * Getter for property success.
     * @return Value of property success.
     */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * Setter for property success.
     * @param success New value of property success.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String cancel() {
        String forwardPage="myNetworkOptions";
        privileges.cancel();
        this.sessionRemove(privileges.getClass().getName());
        return  forwardPage;
    }   
}

