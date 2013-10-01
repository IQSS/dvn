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
 * StudyPermissionsPage.java
 *
 * Created on October 11, 2006, 2:03 PM
 *
 */
package edu.harvard.iq.dvn.core.web.study;

import com.icesoft.faces.component.ext.HtmlCommandLink;
import edu.harvard.iq.dvn.core.admin.GroupServiceLocal;
import edu.harvard.iq.dvn.core.admin.NetworkRoleServiceBean;
import edu.harvard.iq.dvn.core.admin.RoleServiceBean;
import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCRole;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.study.EditStudyPermissionsService;
import edu.harvard.iq.dvn.core.study.PermissionBean;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.Iterator;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlSelectBooleanCheckbox;
import edu.harvard.iq.dvn.core.util.PropertyUtil;
import java.util.Collection;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
@Named("StudyPermissionsPage")
@ViewScoped
@EJB(name="editStudyPermissions", beanInterface=edu.harvard.iq.dvn.core.study.EditStudyPermissionsService.class)
public class StudyPermissionsPage extends VDCBaseBean  implements java.io.Serializable {
   
    private EditStudyPermissionsService editStudyPermissions;
    @EJB
    private UserServiceLocal userService;
    @EJB GroupServiceLocal groupService;
    private boolean viewCurrentFiles = true;

    public boolean isViewCurrentFiles() {
        return viewCurrentFiles;
    }

    public void setViewCurrentFiles(boolean viewCurrentFiles) {
        this.viewCurrentFiles = viewCurrentFiles;
    }
    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public StudyPermissionsPage() {
    }
    
    
    public void init() {
    try {
            Context ctx = new InitialContext();
            editStudyPermissions = (EditStudyPermissionsService) ctx.lookup("java:comp/env/editStudyPermissions");

        } catch (NamingException e) {
            e.printStackTrace();
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
            context.addMessage(null, errMessage);

        }
            editStudyPermissions.setStudy(getStudyId());
            studyUI = new StudyUI(editStudyPermissions.getStudy());
            long latestVersion = editStudyPermissions.getStudy().getLatestVersion().getVersionNumber();
            editStudyPermissions.setStudy(getStudyId(), latestVersion);
    }
    

   
    
    public EditStudyPermissionsService getEditStudyPermissions() {
        return editStudyPermissions;
    }
    
    public void setEditStudyPermissions(EditStudyPermissionsService editStudyPermissions) {
        this.editStudyPermissions = editStudyPermissions;
    }
    
    /**
     * Holds value of property newStudyUser.
     */
    private String newStudyUser;
    
    /**
     * Getter for property newStudyUser.
     * @return Value of property newStudyUser.
     */
    public String getNewStudyUser() {
        return this.newStudyUser;
    }
    
    /**
     * Setter for property newStudyUser.
     * @param newStudyUser New value of property newStudyUser.
     */
    public void setNewStudyUser(String newStudyUser) {
        this.newStudyUser = newStudyUser;
    }
    
    /**
     * Holds value of property newFileUser.
     */
    private String newFileUser;
    
    /**
     * Getter for property newFileUser.
     * @return Value of property newFileUser.
     */
    public String getNewFileUser() {
        return this.newFileUser;
    }
    
    /**
     * Setter for property newFileUser.
     * @param newFileUser New value of property newFileUser.
     */
    public void setNewFileUser(String newFileUser) {
        this.newFileUser = newFileUser;
    }
    
    public void addStudyPermission(ActionEvent ae) {
        
        VDCUser user = userService.findByUserName(newStudyUser);
        UserGroup group = null;
        if (user==null) {
            group = groupService.findByName(newStudyUser);
        }
        if (user==null && group==null) {
            String msg = "Invalid user or group name.";
            FacesMessage message = new FacesMessage(msg);
            FacesContext.getCurrentInstance().addMessage(studyUserInputText.getClientId(FacesContext.getCurrentInstance()), message);
        } else {
            if (user!=null ) {
                if (validateStudyUserName(FacesContext.getCurrentInstance(),studyUserInputText, newStudyUser)) {
                    this.editStudyPermissions.addStudyUser(user.getId());
                    newStudyUser="";
                }
            } else {
                if (validateStudyGroupName(FacesContext.getCurrentInstance(),studyUserInputText, newStudyUser)) {
                    this.editStudyPermissions.addStudyGroup(group.getId());
                    newStudyUser="";
                }
            }
        }
    }
    
    
    
    
    public void addFilePermission(ActionEvent ae) {
        if (!StringUtil.isEmpty(newFileUser)) {
            VDCUser user = userService.findByUserName(newFileUser);
            UserGroup group = null;
            if (user==null) {
                group = groupService.findByName(newFileUser);
            }
            if (user==null && group==null) {
                String msg = "Invalid user or group name.";
                FacesMessage message = new FacesMessage(msg);
                FacesContext.getCurrentInstance().addMessage(fileUserInputText.getClientId(FacesContext.getCurrentInstance()), message);
            } else {
                if (user!=null ) {
                    if (validateFileUserName(FacesContext.getCurrentInstance(),fileUserInputText, newFileUser)) {
                        this.editStudyPermissions.addFileUser(user.getId());
                        newFileUser="";
                    }
                } else {
                     this.editStudyPermissions.addFileGroup(group.getId());
                    newFileUser="";
                }
            }
        }
        if (!StringUtil.isEmpty(this.selectFilePermission)) {
            editStudyPermissions.setFileRestriction(selectFilePermission.equals("Restricted"));
        }
        
    }
    public void viewAllFiles(ActionEvent ae) {
        setViewCurrentFiles(false);
        editStudyPermissions.setCurrentVersionFiles(false);
    }
    
    public void viewCurrentFiles(ActionEvent ae) {
        setViewCurrentFiles(true);
        editStudyPermissions.setCurrentVersionFiles(true);
    }
    
    public void removeStudyUserGroup(ActionEvent ae) {
        editStudyPermissions.removeStudyPermissions();
    }
    
    public void removeFilePermissions() {
        editStudyPermissions.removeFilePermissions(removeChecked);
        showFileAccessPopup = !showFileAccessPopup; 
    }

    public void  updateRequests(ActionEvent ae) {  
        editStudyPermissions.updateRequests();
        getVDCRenderBean().getFlash().put("warningMessage", "User request approvals and denials won't take effect until you click the 'Save' button.");    
    }
    
    /**
     * Holds value of property studyId.
     */
    private Long studyId;
    
    /**
     * Getter for property studyId.
     * @return Value of property studyId.
     */
    public Long getStudyId() {
        return this.studyId;
    }
    
    /**
     * Setter for property studyId.
     * @param studyId New value of property studyId.
     */
    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }
    
    public String save() {
        Long studyId = editStudyPermissions.getStudy().getId();
        this.editStudyPermissions.save();
           
        return "/study/StudyPage?faces-redirect=true&studyId=" + studyId + "&versionNumber=" + getVDCRequestBean().getStudyVersionNumber() + getContextSuffix();
        
    }
    
    
    public boolean validateStudyUserName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String userNameStr = (String) value;
        String msg=null;
        boolean valid=true;
        
        VDCUser user = userService.findByUserName(userNameStr);
        if (user==null) {
            valid=false;
            msg = "User not found.";
        }
        if (valid) {
            for (Iterator it = this.editStudyPermissions.getStudyPermissions().iterator(); it.hasNext();) {
                PermissionBean pb = (PermissionBean)it.next();
                if (pb.getUser()!=null && pb.getUser().getId().equals(user.getId())) {
                    valid=false;
                    msg = "User already in study permissions list.";
                    break;
                }
                
            }
        }
        
        if (valid) {
            if ( !editStudyPermissions.getStudy().isStudyRestrictedForUser(user,null)) {
                valid=false;
                msg= "This user already has a Network or Dataverse Role that allows access to the study.";
            }
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage(msg);
            context.addMessage(toValidate.getClientId(context), message);
            
            
        }
        return valid;
        
    }
    
    
    public boolean validateStudyGroupName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String groupNameStr = (String) value;
        String msg=null;
        boolean valid=true;
        
        UserGroup group = this.groupService.findByName(groupNameStr);
        if (group==null) {
            valid=false;
            msg = "Group not found.";
        }
        if (valid) {
            for (Iterator it = this.editStudyPermissions.getStudyPermissions().iterator(); it.hasNext();) {
                PermissionBean pb = (PermissionBean)it.next();
                if (pb.getGroup()!=null && pb.getGroup().getId().equals(group.getId())) {
                    valid=false;
                    msg = "Group already in study permissions list.";
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
    
    public boolean validateFileUserName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String userNameStr = (String) value;
        String msg=null;
        boolean valid=true;
        
        VDCUser user = userService.findByUserName(userNameStr);
        if (user==null) {
            valid=false;
            msg = "User not found.";
        }
       
        if (valid) {
            if (user.getNetworkRole()!=null && user.getNetworkRole().getName().equals(NetworkRoleServiceBean.ADMIN)) {
                valid=false;
                msg= "User is a Network Administrator and already has all privileges to this dataverse.";
            }
        }
       if (valid) {
            VDCRole vdcRole = user.getVDCRole(this.getVDCRequestBean().getCurrentVDC());
            if ((vdcRole!=null && (vdcRole.getRole().getName().equals(RoleServiceBean.ADMIN) || vdcRole.getRole().getName().equals(RoleServiceBean.CURATOR)))
                 || editStudyPermissions.getStudy().getCreator().getId().equals(user.getId())) {
                valid=false;
                msg= "User already has a Network or Dataverse Role that allows access to the restricted files.";
            }
        }        
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage(msg);
            context.addMessage(toValidate.getClientId(context), message);
            
            
        }
        return valid;
        
    }
    
    
    public boolean validateFileGroupName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String groupNameStr = (String) value;
        String msg=null;
        boolean valid=true;
        
        UserGroup group = this.groupService.findByName(groupNameStr);
        if (group==null) {
            valid=false;
            msg = "Group not found.";
        }
    
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage(msg);
            context.addMessage(toValidate.getClientId(context), message);
        }
        return valid;
        
    }
    
    /**
     * Holds value of property studyUserInputText.
     */
    private HtmlInputText studyUserInputText;
    
    /**
     * Getter for property studyUserInputText.
     * @return Value of property studyUserInputText.
     */
    public HtmlInputText getStudyUserInputText() {
        return this.studyUserInputText;
    }
    
    /**
     * Setter for property studyUserInputText.
     * @param studyUserInputText New value of property studyUserInputText.
     */
    public void setStudyUserInputText(HtmlInputText studyUserInputText) {
        this.studyUserInputText = studyUserInputText;
    }
    
    /**
     * Holds value of property fileUserInputText.
     */
    private HtmlInputText fileUserInputText;
    
    /**
     * Getter for property fileUserInputText.
     * @return Value of property fileUserInputText.
     */
    public HtmlInputText getFileUserInputText() {
        return this.fileUserInputText;
    }
    
    /**
     * Setter for property fileUserInputText.
     * @param fileUserInputText New value of property fileUserInputText.
     */
    public void setFileUserInputText(HtmlInputText fileUserInputText) {
        this.fileUserInputText = fileUserInputText;
    }
    
    /**
     * Holds value of property selectFilePermission.
     */
    private String selectFilePermission;
    
    /**
     * Getter for property selectFilePermission.
     * @return Value of property selectFilePermission.
     */
    public String getSelectFilePermission() {
        return this.selectFilePermission;
    }
    
    /**
     * Setter for property selectFilePermission.
     * @param selectFilePermission New value of property selectFilePermission.
     */
    public void setSelectFilePermission(String selectFilePermission) {
        this.selectFilePermission = selectFilePermission;
    }

    /**
     *  Wrapper for Study object used to display Study Title information
     */
    private StudyUI studyUI;

    public StudyUI getStudyUI() {
        return studyUI;
    }

    public void setStudyUI(StudyUI studyUI) {
        this.studyUI = studyUI;
    }


    
    
      public String cancel() {
       
        editStudyPermissions.cancel();
        this.sessionRemove(editStudyPermissions.getClass().getName());     
        return "/study/StudyPage?faces-redirect=true&studyId=" + studyId + "&versionNumber=" + getVDCRequestBean().getStudyVersionNumber() + getContextSuffix();

    }
      
    /**
     * Holds value of property viewFileCheckBox.
     */
    private HtmlSelectBooleanCheckbox viewFileCheckBox;

    /**
     * @return the viewFileCheckBox
     */
    public HtmlSelectBooleanCheckbox getViewFileCheckBox() {
        return viewFileCheckBox;
    }

    /**
     * @param viewFileCheckBox the viewFileCheckBox to set
     */
    public void setViewFileCheckBox(HtmlSelectBooleanCheckbox viewFileCheckBox) {
        this.viewFileCheckBox = viewFileCheckBox;
    }
    
    /**
     * Holds value of property viewAllFiles.
     */
    private boolean viewAllFiles;

    /**
     * @return the viewAllFiles
     */
    public boolean isViewAllFiles() {
        return viewAllFiles;
    }

    /**
     * @param viewAllFiles the viewAllFiles to set
     */
    public void setViewAllFiles(boolean viewAllFiles) {
        this.viewAllFiles = viewAllFiles;
    }
    
    public void changeViewFiles(ValueChangeEvent vce){
        viewAllFiles = (Boolean)viewFileCheckBox.getValue();
        setViewCurrentFiles(!viewAllFiles);
        editStudyPermissions.setCurrentVersionFiles(!viewAllFiles);
    }
      
    private HtmlInputText inputFilterTerm;

    public HtmlInputText getInputFilterTerm() {
        return this.inputFilterTerm;
    }

    public void setInputFilterTerm(HtmlInputText inputFilterTerm) {
        this.inputFilterTerm = inputFilterTerm;
    }
    
    public String updateAllFilesList(){
        String checkString = (String) getInputFilterTerm().getValue(); 
        return this.editStudyPermissions.updateAllFilesList(checkString);
    }
    
    /**
     * Holds value of property showFileAccessPopup.
     */
    protected boolean showFileAccessPopup = false;

    /**
     * Get the value of showFileAccessPopup
     *
     * @return the value of showFileAccessPopup
     */
    public boolean isShowFileAccessPopup() {
        return showFileAccessPopup;
    }

    /**
     * Set the value of showFileAccessPopup
     *
     * @param showFileAccessPopup new value of showFileAccessPopup
     */
    public void setShowFileAccessPopup(boolean showFileAccessPopup) {
        this.showFileAccessPopup = showFileAccessPopup;
    }
    
    private Collection<PermissionBean> restrictedFilePermissions;
    
    public Collection<PermissionBean> getRestrictedFilePermissions() {
        return restrictedFilePermissions;
    }
    
    public void setRestrictedFilePermissions(Collection<PermissionBean> restrictedFilePermissions) {
        this.restrictedFilePermissions = restrictedFilePermissions;
    }
    
    public void toggleFileAccessPopup(Long fileId) {
        setRestrictedFilePermissions(editStudyPermissions.getFilePermissions(fileId));         
        showFileAccessPopup = !showFileAccessPopup;
    }    
    
    /** 
     * Holds value of property removeChecked.
     */
    private boolean removeChecked;

    /**
     * Getter for property removeChecked.
     * @return Value of property removeChecked.
     */
    public boolean isRemoveChecked() {
        return this.removeChecked;
    }

    /**
     * Setter for property removeChecked.
     * @param removeChecked New value of property removeChecked.
     */
    public void setRemoveChecked(boolean removeChecked) {
        this.removeChecked = removeChecked;
    }
}
