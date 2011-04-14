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
package edu.harvard.iq.dvn.core.web.admin;   

import edu.harvard.iq.dvn.core.admin.EditVDCPrivilegesService;
import edu.harvard.iq.dvn.core.admin.GroupServiceLocal;
import edu.harvard.iq.dvn.core.admin.NetworkRoleServiceBean;
import edu.harvard.iq.dvn.core.admin.Role;
import edu.harvard.iq.dvn.core.admin.RoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.util.DateUtil;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIInput;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlInputText;
import edu.harvard.iq.dvn.core.admin.VDCRole;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.util.PropertyUtil;
import edu.harvard.iq.dvn.core.vdc.VDCCollection;
import edu.harvard.iq.dvn.core.vdc.VDCGroup;
import edu.harvard.iq.dvn.core.web.push.beans.NetworkStatsBean;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

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
    @EJB MailServiceLocal mailService;

    public class RoleListItem {
        private VDCRole vdcRole;
        private Long selectedRoleId;

        public RoleListItem(VDCRole vdcRole, Long selectedRoleId) {
            this.vdcRole = vdcRole;
            this.selectedRoleId = selectedRoleId;
        }

        public Long getSelectedRoleId() {
            return selectedRoleId;
        }

        public void setSelectedRoleId(Long selectedRoleId) {
            this.selectedRoleId = selectedRoleId;
        }

        public VDCRole getVdcRole() {
            return vdcRole;
        }

        public void setVdcRole(VDCRole vdcRole) {
            this.vdcRole = vdcRole;
        }
    }
   
    private javax.faces.component.UIData userTable;
    private List<RoleListItem> vdcRoleList;

    public List<RoleListItem> getVdcRoleList() {
        return vdcRoleList;
    }

    public void setVdcRoleList(List<RoleListItem> vdcRoleList) {
        this.vdcRoleList = vdcRoleList;
    }
   

     public enum ContributorSetting {
        CONTRIB_CREATE,
        CONTRIB_EDIT,
        USER_CREATE,
        USER_EDIT
    };

    private ContributorSetting selectedSetting;

    

    public ContributorSetting getSelectedSetting() {
        return selectedSetting;
    }

    public void setSelectedSetting(ContributorSetting selectedSetting) {
        this.selectedSetting = selectedSetting;
    }

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

        Long vdcId = getVDCRequestBean().getCurrentVDC().getId();
        editVDCPrivileges.setVdc(vdcId);



        vdc = editVDCPrivileges.getVdc();
        if (vdc.isRestricted()) {
            siteRestriction = "Restricted";
        } else {
            siteRestriction = "Public";
        }
        initContributorSetting();
        initUserName();

        vdcRoleList = new ArrayList<RoleListItem>();

        // Add empty VDCRole to list, to allow user input of a new VDCRole
        vdcRoleList.add(new RoleListItem(null,null));

        // Add rest of items to the list from vdc object
        for (VDCRole vdcRole : vdc.getVdcRoles()) {
            vdcRoleList.add(new RoleListItem(vdcRole, vdcRole.getRoleId()));
        }
       

        setFilesRestricted(vdc.isFilesRestricted());
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


    /**
     * Holds value of property newUserName.
     */
    private String newUserName;

    /**
     * Getter for property addUserName.
     * @return Value of property addUserName.
     */
    public String getNewUserName() {
        return this.newUserName;
    }

    /**
     * Setter for property addUserName.
     * @param addUserName New value of property addUserName.
     */
    public void setNewUserName(String userName) {
        this.newUserName = userName;
    }


    public Long newRoleId;

    public Long getNewRoleId() {
        return newRoleId;
    }

    public void setNewRoleId(Long newRoleId) {
        this.newRoleId = newRoleId;
    }

    public String saveChanges() {
        NetworkStatsBean statsBean = (NetworkStatsBean) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("NetworkStatsBean");
        if (siteRestriction.equals("Public")) {
            if (vdc.getReleaseDate() == null) {
                 // update the network stats bean
                if (statsBean != null)
                    statsBean.releaseAndUpdateInlineDataverseValue(vdc.getId(), (List<VDCGroup>)vdc.getVdcGroups());
                vdc.setReleaseDate(DateUtil.getTimestamp());
                sendReleaseEmails();
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

        if (filesRestricted != vdc.isFilesRestricted()) {
            vdc.setFilesRestricted(filesRestricted);
            if (vdc.getHarvestingDataverse() != null) {
                vdc.getHarvestingDataverse().setSubsetRestricted(filesRestricted);
            }
        }

        saveContributorSetting();

        // For each item in display role list, update the vdc with the selected role
        for (int i=1; i< vdcRoleList.size(); i++) {
            if (vdcRoleList.get(i).selectedRoleId!=null) {
                Role role = roleService.findById(vdcRoleList.get(i).selectedRoleId);
                vdcRoleList.get(i).getVdcRole().setRole(role);
            }
        }

        this.editVDCPrivileges.save();
        
       
        editVDCPrivileges.setVdc(vdc.getId());
        vdc = editVDCPrivileges.getVdc();

        getVDCRequestBean().setSuccessMessage("Successfully updated dataverse permissions.");
        return "myOptions";  
    } 

    private void initContributorSetting() {
        if (!vdc.isAllowRegisteredUsersToContribute() && !vdc.isAllowContributorsEditAll()) {
            selectedSetting = ContributorSetting.CONTRIB_CREATE;
        }
        else if (!vdc.isAllowRegisteredUsersToContribute() && vdc.isAllowContributorsEditAll()) {
            selectedSetting = ContributorSetting.CONTRIB_EDIT;
        }
        else if (vdc.isAllowRegisteredUsersToContribute() && !vdc.isAllowContributorsEditAll()) {
            selectedSetting = ContributorSetting.USER_CREATE;
        }
        else if (vdc.isAllowRegisteredUsersToContribute() && vdc.isAllowContributorsEditAll()) {
            selectedSetting = ContributorSetting.USER_EDIT;
        }

    }

    private void sendReleaseEmails() {
        String networkAdminEmailAddress = getVDCRequestBean().getVdcNetwork().getContactEmail();

        String toMailAddress = vdc.getContactEmail();
        String siteAddress = "unknown";
        String hostUrl = PropertyUtil.getHostUrl();
        siteAddress = hostUrl + "/dvn" + getVDCRequestBean().getCurrentVDCURL();
        String name = vdc.getName();
        if (toMailAddress != null){
            mailService.sendReleaseSiteNotification(toMailAddress, name, siteAddress);
        }
        else {
             Logger.getLogger("Release Emails vdc contact email is null");
        }
        if (networkAdminEmailAddress != null){
            mailService.sendReleaseSiteNotificationNetwork(networkAdminEmailAddress, name, siteAddress);
        }
        else {
             Logger.getLogger("Release Emails Network contact email is null");
        }

        
    }

    private void initUserName() {
        newUserName = "Enter Username";
    }

    private void saveContributorSetting() {
        switch (selectedSetting) {
            case CONTRIB_CREATE:
                vdc.setAllowRegisteredUsersToContribute(false);
                vdc.setAllowContributorsEditAll(false);
                break;
            case CONTRIB_EDIT:
                vdc.setAllowRegisteredUsersToContribute(false);
                vdc.setAllowContributorsEditAll(true);
                break;
            case USER_CREATE:
                vdc.setAllowRegisteredUsersToContribute(true);
                vdc.setAllowContributorsEditAll(false);
                break;
            case USER_EDIT:
                vdc.setAllowRegisteredUsersToContribute(true);
                vdc.setAllowContributorsEditAll(true);
       }
    }




    public void addUser(ActionEvent ae) {
     
        if (validateUserName(FacesContext.getCurrentInstance(),userInputText, newUserName)) {
            VDCUser user = userService.findByUserName(newUserName);

            VDCRole vdcRole = new VDCRole();
            vdcRole.setVdcUser(user);
            vdcRole.setRole(roleService.findById(newRoleId));
            vdcRole.setVdc(vdc);
            // Add the new vdcRole object to the second position in the display list -
            // the first position stays null to allow for more inserts.
            vdcRoleList.add(1, new RoleListItem(vdcRole, vdcRole.getRoleId()));

            // Add new vdcRole to the actual list in the vdc object
            vdc.getVdcRoles().add(0,vdcRole);

            // Reset newUserName, to be ready for new input from user
            initUserName();

            // Reset newRoleId, to be ready for new input from user
            newRoleId=null;
           
     
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
        vdcRoleList.remove(userTable.getRowIndex());
        editVDCPrivileges.removeRole(userTable.getRowIndex()-1);
      
    }
    
    
    public List getRoleSelectItems() {
        List selectItems = new ArrayList();
        if (!vdc.isHarvestingDv()) {
            selectItems.add(new SelectItem(roleService.findByName(RoleServiceLocal.CONTRIBUTOR).getId(), "Contributor"));
        }
        selectItems.add(new SelectItem(roleService.findByName(RoleServiceLocal.CURATOR).getId(), "Curator"));
        selectItems.add(new SelectItem(roleService.findByName(RoleServiceLocal.ADMIN).getId(), "Admin"));
        Role role = roleService.findByName(RoleServiceLocal.PRIVILEGED_VIEWER);
        selectItems.add(new SelectItem(role.getId(), "Access Restricted Site"));
        return selectItems;
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

    public boolean isEnableSelectRelease(){
        if (getVDCRequestBean().getVdcNetwork().isRequireDVstudiesforrelease() == false || !vdc.isRestricted()){
           return true;
        }
        return hasStudies(vdc);
    }


    public boolean isReleasable(){
        if (getVDCRequestBean().getVdcNetwork().isRequireDVstudiesforrelease() == false){
           return true; 
        }    
        return hasStudies(vdc);
    }

    public boolean isNotReleasableAndNotReleased(){
        if (getVDCRequestBean().getVdcNetwork().isRequireDVstudiesforrelease() == false){
           return false;
        }
        else {
           return (!(hasStudies(vdc))  && vdc.isRestricted() );
        }
    }

    public boolean isReleasedWithoutRequiredStudies(){
        if (!vdc.isRestricted() && getVDCRequestBean().getVdcNetwork().isRequireDVstudiesforrelease() == true
                && !(hasStudies(vdc))){
           return true;
        }
        else {
           return false;
        }
    }

    private boolean hasStudies(VDC vdcIn){

        if (vdcIn.getNumberReleasedStudies() > 0 || vdcIn.isHarvestingDv()
                || vdcIn.getRootCollection().getStudies().size() > 0) {
                    return true;
        }

        if (vdcIn.getRootCollection().getStudies().size() > 0 ) {
            return true;
        }

        if (vdcIn.getOwnedCollections().size() > 1  ) {
            for (VDCCollection vdcc: vdcIn.getOwnedCollections() ) {
                if (vdcc.getStudies().size() > 0){
                    return true;
                }
            }
        }

        if (vdcIn.getLinkedCollections().size() > 0 ){
            for (VDCCollection vdcc: vdcIn.getLinkedCollections() ) {
                if (vdcc.getStudies().size() > 0){
                    return true;
                }
            }
        }
        
        return false;
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
            for ( VDCRole vdcRole : vdc.getVdcRoles()) {
                if (vdcRole!=null && vdcRole.getVdcUser().getId().equals(user.getId())) {
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

    private boolean filesRestricted;

    /**
     * Getter for property restricted.
     * @return Value of property restricted.
     */
    public boolean isFilesRestricted() {
        return this.filesRestricted;
    }

    /**
     * Setter for property restricted.
     * @param restricted New value of property restricted.
     */
    public void setFilesRestricted(boolean filesRestricted) {
        this.filesRestricted = filesRestricted;
    }

}

