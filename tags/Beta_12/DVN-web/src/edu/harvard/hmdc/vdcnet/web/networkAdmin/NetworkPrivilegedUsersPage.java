/*
 * NetworkPrivilegedUsersPage.java
 *
 * Created on October 20, 2006, 3:10 PM
 * 
 */
package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import edu.harvard.hmdc.vdcnet.admin.EditNetworkPrivilegesService;
import edu.harvard.hmdc.vdcnet.admin.NetworkPrivilegedUserBean;
import edu.harvard.hmdc.vdcnet.admin.NetworkRole;
import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.component.UIData;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class NetworkPrivilegedUsersPage extends VDCBaseBean {
    @EJB  EditNetworkPrivilegesService privileges;
    @EJB  NetworkRoleServiceLocal networkRoleService;
    @EJB  UserServiceLocal userService;

   public void init() {
        super.init();
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
    
    private UIData userTable;
    
     public javax.faces.component.UIData getUserTable() {
        return userTable;
    }

    public void setUserTable(javax.faces.component.UIData userTable) {
        this.userTable = userTable;
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

    public String save() {
         HttpServletRequest request = (HttpServletRequest)this.getExternalContext().getRequest();
        String hostName=request.getLocalName();
        int port = request.getLocalPort();
        String portStr="";
        if (port!=80) {
            portStr=":"+port;
        }
        // Needed to send an approval email to approved creators
        String creatorUrl = "http://"+hostName+portStr+request.getContextPath()+"/faces/site/AddSitePage.jsp";

        privileges.save(creatorUrl);
        success=true;
        privileges.init();
        return "result";  // Return back to current page
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

