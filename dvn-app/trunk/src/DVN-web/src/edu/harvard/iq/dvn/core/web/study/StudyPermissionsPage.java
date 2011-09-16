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
 * StudyPermissionsPage.java
 *
 * Created on October 11, 2006, 2:03 PM
 *
 */
package edu.harvard.iq.dvn.core.web.study;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import com.icesoft.faces.component.ext.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
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
    public void removeFilePermissions(ActionEvent ae) {
        editStudyPermissions.removeFilePermissions();
    }
 
    public void  updateRequests(ActionEvent ae) {
        HttpServletRequest request = (HttpServletRequest)this.getExternalContext().getRequest();
        String hostName=request.getLocalName();
        int port = request.getLocalPort();
        String portStr="";
        if (port!=80) {
            portStr=":"+port;
        }
        String studyUrl = "http://"+hostName+portStr+request.getContextPath()+getVDCRequestBean().getCurrentVDCURL()+"/faces/study/StudyPage.xhtml?studyId="+studyId+"&tab=files";
        
        
        editStudyPermissions.updateRequests(studyUrl);
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
        this.getVDCRequestBean().setStudyId(editStudyPermissions.getStudy().getId());
        this.editStudyPermissions.save();
        return "viewStudy";
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
        String forwardPage="viewStudy";
       
        editStudyPermissions.cancel();
        this.sessionRemove(editStudyPermissions.getClass().getName());
        getVDCRequestBean().setStudyId(studyId);        
        return  forwardPage;
    }
}

