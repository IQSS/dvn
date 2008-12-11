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
 * PrivilegedUsersPage.java
 *
 * Created on October 4, 2006, 11:29 AM
 */
package edu.harvard.hmdc.vdcnet.web.admin;   

import edu.harvard.hmdc.vdcnet.admin.EditVDCPrivilegesService;
import edu.harvard.hmdc.vdcnet.admin.GroupServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceBean;
import edu.harvard.hmdc.vdcnet.admin.Role;
import edu.harvard.hmdc.vdcnet.admin.RoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCRole;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.util.DateUtil;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIInput;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlInputText;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;
import edu.harvard.hmdc.vdcnet.web.push.beans.NetworkStatsBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class PrivilegedUsersPage extends VDCBaseBean implements java.io.Serializable  {

    @EJB EditVDCPrivilegesService editVDCPrivileges;
    @EJB RoleServiceLocal roleService;
    @EJB UserServiceLocal userService;
    @EJB GroupServiceLocal groupService;
   
    private javax.faces.component.UIData userTable;
    private ListDataModel userList;
    
    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public PrivilegedUsersPage() {
    }

        

    /** 
     * <p>Callback method that is called whenever a page is navigated to,
     * either directly via a URL, or indirectly via page navigation.
     * Customize this method to acquire resources that will be needed
     * for event handlers and lifecycle methods, whether or not this
     * page is performing post back processing.</p>
     * 
     * <p>Note that, if the current request is a postback, the property
     * values of the components do <strong>not</strong> represent any
     * values submitted with this request.  Instead, they represent the
     * property values that were saved for this view when it was rendered.</p>
     */
    public void init() {
        super.init();

        if ( isFromPage("PrivilegedUsersPage")  && sessionGet(editVDCPrivileges.getClass().getName())!=null) {
            editVDCPrivileges= (EditVDCPrivilegesService) sessionGet(editVDCPrivileges.getClass().getName());
            System.out.println("Getting stateful session bean editVDCPrivileges ="+editVDCPrivileges);
            
        } else {
            Long vdcId = getVDCRequestBean().getCurrentVDC().getId();
            editVDCPrivileges.setVdc(vdcId);
            System.out.println("Putting stateful session bean in request, editVDCPrivileges ="+editVDCPrivileges);
           
            sessionPut( editVDCPrivileges.getClass().getName(), editVDCPrivileges);
            //sessionPut( (studyService.getClass().getName() + "."  + studyId.toString()), studyService);
        }
        
        vdc=editVDCPrivileges.getVdc();
        if (vdc.isRestricted()) {
            siteRestriction= "Restricted";
        } else {
            siteRestriction="Public";
        }
       
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

    /**
     * Holds value of property vdc.
     */
    private VDC vdc;

    /**
     * Getter for property vdc.
     * @return Value of property vdc.
     */
    public VDC getVdc() {
        return this.vdc;
    }

    public List getPrivilegedUsers() {
        return this.editVDCPrivileges.getPrivilegedUsers();
    }
    /**
     * Setter for property vdc.
     * @param vdc New value of property vdc.
     */
    public void setVdc(VDC vdc) {
        this.vdc = vdc;
    }

    /**
     * Holds value of property vdcId.
     */
    private Long vdcId;

    /**
     * Getter for property vdcId.
     * @return Value of property vdcId.
     */
    public Long getVdcId() {
        return this.vdcId;
    }

    /**
     * Setter for property vdcId.
     * @param vdcId New value of property vdcId.
     */
    public void setVdcId(Long vdcId) {
        this.vdcId = vdcId;
    }

    /**
     * Holds value of property siteRestriction.
     */
    private String siteRestriction;

    /**
     * Getter for property siteRestriction.
     * @return Value of property siteRestriction.
     */
    public String getSiteRestriction() {
            return siteRestriction;
    }

    /**
     * Setter for property siteRestriction.
     * @param siteRestriction New value of property siteRestriction.
     */
    public void setSiteRestriction(String siteRestriction) {
        this.siteRestriction = siteRestriction;
    }
    
    public String saveChanges() {
        NetworkStatsBean statsBean = (NetworkStatsBean) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("NetworkStatsBean");
        if (siteRestriction.equals("Public")) {
            if (vdc.getReleaseDate() == null) {
                 // update the network stats bean
                if (statsBean != null)
                    statsBean.releaseAndUpdateInlineDataverseValue(vdc.getId(), (List<VDCGroup>)vdc.getVdcGroups());
                vdc.setReleaseDate(DateUtil.getTimestamp());
            }
            vdc.setRestricted(false);
            
        } else {
            if (vdc.getReleaseDate() != null) {
                //update the network stats bean
                if (statsBean != null)
                    statsBean.restrictAndUpdateInlineDataverseValue(vdc.getId(), (List<VDCGroup>)vdc.getVdcGroups());
                vdc.setReleaseDate(null);
            }
            vdc.setRestricted(true);
        }
        // If the user has added a vdcUser to the privileged list, but hasn't assigned
        // a role for the vdcUser, delete the vdcUser from the list before saving changes.
        Collection removeRoles = new ArrayList();
        for (Iterator<List> it = getPrivilegedUsers().iterator(); it.hasNext();) {
            List privilegedUsersRow =  it.next();
            String roleId=null;
            if (privilegedUsersRow.get(1) instanceof Long) {
                roleId = privilegedUsersRow.get(1).toString();
            } else {
                 roleId = (String)privilegedUsersRow.get(1);
            }
            if (roleId==null) {
                removeRoles.add(privilegedUsersRow.get(0));
            }
        }
        for (Iterator<VDCRole> it = removeRoles.iterator(); it.hasNext();) {
            VDCRole elem =  it.next();
             editVDCPrivileges.removeRole(elem.getVdcUser().getId());            
        }
        
        // Repopulate the list with Role objects.  This is necessary because the role objects
        // aren't resubmitted from the form.
       for (Iterator<List> it = getPrivilegedUsers().iterator(); it.hasNext();) {
            List privilegedUsersRow =  it.next();
             VDCRole elem =  (VDCRole)privilegedUsersRow.get(0);
            if (elem.getRoleId()!=null && elem.getRole()==null) {
                elem.setRole(roleService.findById(elem.getRoleId()));
            }
        }
            
        HttpServletRequest request = (HttpServletRequest)this.getExternalContext().getRequest();
        String hostName=request.getLocalName();
        int port = request.getLocalPort();
        String portStr="";
        if (port!=80) {
            portStr=":"+port;
        }
        String contributorUrl = "http://"+hostName+portStr+request.getContextPath()+"/dv/"+getVDCRequestBean().getCurrentVDC().getAlias()+"/faces/admin/OptionsPage.xhtml";
       this.editVDCPrivileges.save(contributorUrl);
        
       
        editVDCPrivileges.setVdc(vdc.getId());
        vdc = editVDCPrivileges.getVdc();
        getVDCRequestBean().setSuccessMessage("Successfully updated dataverse permissions.");
        return "myOptions";  
    } 
    
    public void addUser(ActionEvent ae) {
        
        if (validateUserName(FacesContext.getCurrentInstance(),userInputText, userName)) {
            this.editVDCPrivileges.addUserRole(userName);

            this.userName="";
        }
         
    }
    
    public void removeGroup(ActionEvent ae) {
        editVDCPrivileges.removeAllowedGroup(((UserGroup)groupTable.getRowData()).getId());
        
    }
    
    
    public void addGroup(ActionEvent ae) {
        if (validateGroupName(FacesContext.getCurrentInstance(), groupInputText, groupName)) {
            UserGroup group = groupService.findByName(groupName);
            this.editVDCPrivileges.addAllowedGroup(group.getId());
            groupName="";
        }
        
    }
    
    
    public void removeRole(ActionEvent ea) {
          List privilegedUsersRow = (List)userTable.getRowData();
          VDCRole vdcRole = (VDCRole)privilegedUsersRow.get(0);
          this.editVDCPrivileges.removeRole(vdcRole.getVdcUser().getId());
          this.getPrivilegedUsers().remove(userTable.getRowIndex());
        
      
    }
    
    
    public List getRoleSelectItems() {
        List selectItems = new ArrayList();
        Role role = roleService.findByName(RoleServiceLocal.PRIVILEGED_VIEWER);
        selectItems.add(new SelectItem(role.getId(), "Access To Site"));
        selectItems.add(new SelectItem(roleService.findByName(RoleServiceLocal.CONTRIBUTOR).getId(), "Contributor"));
        selectItems.add(new SelectItem(roleService.findByName(RoleServiceLocal.CURATOR).getId(), "Curator"));
        selectItems.add(new SelectItem(roleService.findByName(RoleServiceLocal.ADMIN).getId(), "Admin"));
        return selectItems;
    }

   
    /**
     * Holds value of property userName.
     */
    private String userName;

    /**
     * Getter for property addUserName.
     * @return Value of property addUserName.
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Setter for property addUserName.
     * @param addUserName New value of property addUserName.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public javax.faces.component.UIData getUserTable() {
        return userTable;
    }

    public void setUserTable(javax.faces.component.UIData userTable) {
        this.userTable = userTable;
    }

    /**
     * Holds value of property groupName.
     */
    private String groupName;

    /**
     * Getter for property groupName.
     * @return Value of property groupName.
     */
    public String getGroupName() {
        return this.groupName;
    }

    /**
     * Setter for property groupName.
     * @param groupName New value of property groupName.
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Holds value of property groupTable.
     */
    private UIData groupTable;

    /**
     * Getter for property groupTable.
     * @return Value of property groupTable.
     */
    public UIData getGroupTable() {
        return this.groupTable;
    }

    /**
     * Setter for property groupTable.
     * @param groupTable New value of property groupTable.
     */
    public void setGroupTable(UIData groupTable) {
        this.groupTable = groupTable;
    }

    public ListDataModel getUserList() {
        return userList;
    }

    public void setUserList(ListDataModel userList) {
        this.userList = userList;
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
    
    
    public boolean validateUserName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String userNameStr = (String) value;
        String msg=null;
        boolean valid=true;
        VDCUser user = null;
   
            user = userService.findByUserName(userNameStr);
            if (user==null) {
                valid=false;
                msg = "User not found.";
            }
        if (valid) {
            for (Iterator it = vdc.getVdcRoles().iterator(); it.hasNext();) {
                VDCRole elem = (VDCRole) it.next();
                if (elem.getVdcUser().equals(user)) {
                    valid=false;
                    msg = "User already in privileged users list.";
                    break;
                }
                
            }
        }
   
        if (valid) {
            if (user.getNetworkRole()!=null && user.getNetworkRole().getName().equals(NetworkRoleServiceBean.ADMIN)) {
                valid=false;
                msg= "User is a Network Administrator and already has all privileges to this dataverse.";
            }
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage(msg);
            context.addMessage(toValidate.getClientId(context), message);
            
           
        }
        return valid;
        
    }

   
     public boolean validateGroupName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String groupNameStr = (String) value;
        String msg=null;
        boolean valid=true;
        UserGroup group = null;
 
        group = this.groupService.findByName(groupNameStr);
        if (group==null) {
            valid=false;
            msg = "Group not found.";
        }

        if (valid) {
            for (Iterator it = vdc.getAllowedGroups().iterator(); it.hasNext();) {
                UserGroup elem = (UserGroup) it.next();
                if (elem.getId().equals(group.getId())) {
                    valid=false;
                    msg = "Group already in privileged groups list.";
                    break;
                }
                
            }
        }

        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage(msg);
            context.addMessage(toValidate.getClientId(context), message);
        }
        return valid;
        
    }   

    /**
     * Holds value of property userInputText.
     */
    private HtmlInputText userInputText;

    /**
     * Getter for property userInputText.
     * @return Value of property userInputText.
     */
    public HtmlInputText getUserInputText() {
        return this.userInputText;
    }

    /**
     * Setter for property userInputText.
     * @param userInputText New value of property userInputText.
     */
    public void setUserInputText(HtmlInputText userInputText) {
        this.userInputText = userInputText;
    }

    /**
     * Holds value of property groupInputText.
     */
    private HtmlInputText groupInputText;

    /**
     * Getter for property groupInputText.
     * @return Value of property groupInputText.
     */
    public HtmlInputText getGroupInputText() {
        return this.groupInputText;
    }

    /**
     * Setter for property groupInputText.
     * @param groupInputText New value of property groupInputText.
     */
    public void setGroupInputText(HtmlInputText groupInputText) {
        this.groupInputText = groupInputText;
    }
    
    public String cancel() {
        String forwardPage="myOptions";
        editVDCPrivileges.cancel();
        this.sessionRemove(editVDCPrivileges.getClass().getName());
        return  forwardPage;
    }
    
  /**
     * Holds value of property groupTable.
     */
    private HtmlDataTable fileGroupTable;
    
    /**
     * Getter for property groupTable.
     * @return Value of property groupTable.
     */
    public HtmlDataTable getFileGroupTable() {
        return this.fileGroupTable;
    }
    
    /**
     * Setter for property groupTable.
     * @param groupTable New value of property groupTable.
     */
    public void setFileGroupTable(HtmlDataTable fileGroupTable) {
        this.fileGroupTable = fileGroupTable;
    }
    
    /**
     * Holds value of property userTable.
     */
    private HtmlDataTable fileUserTable;
    
    /**
     * Getter for property userTable.
     * @return Value of property userTable.
     */
    public HtmlDataTable getFileUserTable() {
        return this.fileUserTable;
    }
    
    /**
     * Setter for property userTable.
     * @param userTable New value of property userTable.
     */
    public void setFileUserTable(HtmlDataTable fileUserTable) {
        this.fileUserTable = fileUserTable;
    }
    
    /**
     * Holds value of property addUserName.
     */
    private String addFileUserName;
    
    /**
     * Getter for property addUserName.
     * @return Value of property addUserName.
     */
    public String getAddFileUserName() {
        return this.addFileUserName;
    }
    
    /**
     * Setter for property addUserName.
     * @param addUserName New value of property addUserName.
     */
    public void setAddFileUserName(String addFileUserName) {
        this.addFileUserName = addFileUserName;
    }
    
    /**
     * Holds value of property addGroupName.
     */
    private String addFileGroupName;
    
    /**
     * Getter for property addGroupName.
     * @return Value of property addGroupName.
     */
    public String getAddFileGroupName() {
        return this.addFileGroupName;
    }
    
    /**
     * Setter for property addGroupName.
     * @param addGroupName New value of property addGroupName.
     */
    public void setAddFileGroupName(String addFileGroupName) {
        this.addFileGroupName = addFileGroupName;
    }
    
    public void removeFileGroup(ActionEvent ae) {
        this.editVDCPrivileges.removeAllowedFileGroup(((UserGroup)fileGroupTable.getRowData()).getId());
        
    }
    
    public void removeFileUser(ActionEvent ae) {
        this.editVDCPrivileges.removeAllowedFileUser(((VDCUser)fileUserTable.getRowData()).getId());
        
    }
    
    public void addFileUser(ActionEvent ae) {
        
        if (validateUserName(FacesContext.getCurrentInstance(),fileUserInputText, addFileUserName)) {
            VDCUser   user = userService.findByUserName(addFileUserName);
            this.editVDCPrivileges.addAllowedFileUser(user.getId());
            
            addFileUserName="";
        }
        
    }
    
    
    public void addFileGroup(ActionEvent ae) {
        if (validateGroupName(FacesContext.getCurrentInstance(), fileGroupInputText, addFileGroupName)) {
            UserGroup group = groupService.findByName(addFileGroupName);
            this.editVDCPrivileges.addAllowedFileGroup(group.getId());
            addFileGroupName="";
        }
        
    }   
     /**
     * Holds value of property userInputText.
     */
    private HtmlInputText fileUserInputText;
    
    /**
     * Getter for property userInputText.
     * @return Value of property userInputText.
     */
    public HtmlInputText getFileUserInputText() {
        return this.fileUserInputText;
    }
    
    /**
     * Setter for property userInputText.
     * @param userInputText New value of property userInputText.
     */
    public void setFileUserInputText(HtmlInputText fileUserInputText) {
        this.fileUserInputText = fileUserInputText;
    }
    
    /**
     * Holds value of property groupInputText.
     */
    private HtmlInputText fileGroupInputText;
    
    /**
     * Getter for property groupInputText.
     * @return Value of property groupInputText.
     */
    public HtmlInputText getFileGroupInputText() {
        return this.fileGroupInputText;
    }
    
    /**
     * Setter for property groupInputText.
     * @param groupInputText New value of property groupInputText.
     */
    public void setFileGroupInputText(HtmlInputText fileGroupInputText) {
        this.fileGroupInputText = fileGroupInputText;
    }

}

