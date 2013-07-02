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
 * AddAccountPage.java
 *
 * Created on October 4, 2006, 1:04 PM
 *
 */
package edu.harvard.iq.dvn.core.web.login;

import com.icesoft.faces.context.effects.JavascriptContext;
import edu.harvard.iq.dvn.core.admin.EditUserService;
import edu.harvard.iq.dvn.core.admin.RoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.StudyFileServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.vdc.StudyAccessRequestServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.study.FileCategoryUI;
import edu.harvard.iq.dvn.core.web.study.StudyFileUI;
import edu.harvard.iq.dvn.core.web.study.StudyUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
@ViewScoped
@Named("FileRequestPage")
public class FileRequestPage extends VDCBaseBean implements java.io.Serializable  {

    @EJB
    StudyAccessRequestServiceLocal studyRequestService;
    @EJB
    EditUserService editService;
    @EJB
    RoleServiceLocal roleService;
    @EJB
    StudyServiceLocal studyService;
    @EJB
    MailServiceLocal mailService;
    @EJB
    UserServiceLocal userService;
    @EJB
    StudyFileServiceLocal studyFileService;
    
    Long studyId;

    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public FileRequestPage() {
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
        if (versionNumber == null) {
            versionNumber = getVDCRequestBean().getStudyVersionNumber();
        }
        
        if (studyId!=null) {
            StudyVersion sv = null;
            sv = studyService.getStudyVersion(studyId, versionNumber);
            initStudyUIWithFiles(sv); 
            LoginWorkflowBean lwf = (LoginWorkflowBean) getBean("LoginWorkflowBean");       
            lwf.beginFileAccessWorkflow(studyId);
        }
   
    }
    
    public void preRenderView() {
        super.preRenderView();
        JavascriptContext.addJavascriptCall(getFacesContext(), "checkButton();");        
    }

    private Long getRequestStudyId() {
        LoginWorkflowBean lwf = (LoginWorkflowBean) getBean("LoginWorkflowBean");
        if (studyId!=null) {
            return studyId;
        } else {
            return lwf.getStudyId();
        }
    }
    
    private List<Long> getRequestFileIdList() { 
        LoginWorkflowBean lwf = (LoginWorkflowBean) getBean("LoginWorkflowBean");
        if (fileIdList != null) {
            return listofLong(fileIdList);
        } else {
            return lwf.getFileIdList();
        }
    } 
    
    private String fileListString(List idList) {
        StringBuffer sb = new StringBuffer();
        Iterator iter = idList.iterator();
        while (iter.hasNext()) {
            Long id = (Long) iter.next();
            StudyFile studyFile = studyFileService.getStudyFile(id);
            String fileLabel = studyFile.getFileName();
            sb.append(fileLabel);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
    
    public String generateRequest() {

        LoginWorkflowBean lwf = (LoginWorkflowBean) getBean("LoginWorkflowBean");
        VDCUser user = this.getVDCSessionBean().getLoginBean().getUser();
        if (!studyRequestService.findByUserStudyFiles(user.getId(), getRequestStudyId(), getRequestFileIdList()).isEmpty()) {
            getVDCRenderBean().getFlash().put("successMessage", "You have already requested access to this file in this study. Please wait for approval from the administrator.");
        } else {
            Study study = studyService.getStudy(getRequestStudyId());
        
            Iterator iter = getRequestFileIdList().iterator();
            while (iter.hasNext()) {
                Long fId = (Long) iter.next();
                //System.out.print(fId);
                StudyFile studyFile = studyFileService.getStudyFile(fId);
                studyRequestService.create(user.getId(), study.getId(), studyFile.getId());
            }
            // Notify Admin of request
            mailService.sendFileAccessRequestNotification(study.getOwner().getContactEmail(), user.getUserName(), study.getReleasedVersion().getMetadata().getTitle(), study.getGlobalId(), fileListString(getRequestFileIdList())); 

        // Send confirmation to user
 
            mailService.sendFileAccessRequestConfirmation(user.getEmail(), study.getReleasedVersion().getMetadata().getTitle(), study.getGlobalId(), fileListString(getRequestFileIdList())); 
     
            getVDCRenderBean().getFlash().put("successMessage", "Thanks for your interest in these files. You will be notified as soon as your request is approved.");
        } 
        return "/study/StudyPage.xhtml?faces-redirect=true&studyId=" + getRequestStudyId() + "&tab=files" + getContextSuffix();

    }
    
    public String cancel() {
        return "/study/StudyPage.xhtml?faces-redirect=true&studyId=" + getRequestStudyId() + "&tab=files" + getContextSuffix();        
    }
    
    
    /**
     * Holds value of property fileRequest.
     */
    private boolean fileRequest;

    /**
     * Getter for property contributorRequest.
     * @return Value of property contributorRequest.
     */
    public boolean isFileRequest() {
        return this.fileRequest;
    }

    /**
     * Setter for property contributorRequest.
     * @param contributorRequest New value of property contributorRequest.
     */
    public void setFileRequest(boolean fileRequest) {
        this.fileRequest = fileRequest;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }
    
    Long fileId;
    public Long getFileId() {return fileId;}
    public void setFileId(Long fileId) {this.fileId = fileId;}
    
    /**
     * Holds value of property fileIdList.
     */
    private String fileIdList;

    /**
     * Getter for property fileIdList.
     * @return Value of property fileIdList.
     */
     public String getFileIdList() {
        return this.fileIdList;
    } 

    /**
     * Setter for property fileIdList.
     * @param fileIdList New value of property fileIdList.
     */
    public void setFileIdList(String fileIdList) {
        this.fileIdList = fileIdList;
    }

    private Long versionNumber;
    
    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }
        
    /**
     * Holds value of property studyUI.
     */
    private StudyUI studyUI;

    /**
     * Getter for property studyUI.
     * @return Value of property studyUI.
     */
    public StudyUI getStudyUI() {
        return this.studyUI;
    }

    /**
     * Setter for property studyUI.
     * @param studyUI New value of property studyUI.
     */
    public void setStudyUI(StudyUI studyUI) {
        this.studyUI = studyUI;
    }
    
    private boolean studyUIContainsFileDetails=false;

    private void initStudyUIWithFiles(StudyVersion studyVersion) {
        if (!studyUIContainsFileDetails) {
            studyUI = new StudyUI(
                            studyVersion,
                            getVDCSessionBean().getLoginBean() != null ? this.getVDCSessionBean().getLoginBean().getUser() : null,
                            getVDCSessionBean().getIpUserGroup(), true);
            
            //set study file UI to true if study file is choosed by user
            for ( FileCategoryUI catUI : studyUI.getCategoryUIList()) {
                for (StudyFileUI sfui : catUI.getStudyFileUIs()) {
                    if (sfui.getStudyFile().getId().equals(fileId) ) {
                        sfui.setSelected(true);
                        break;
                    }
                }
            }
            studyUIContainsFileDetails=true;
        }
    }
    
    private List<Long> listofLong(String str) {
        String [] items = str.split(",");
        List<String> strList = Arrays.asList(items);
        List<Long> longList = new ArrayList<Long>();

        for(String s : strList) {
            longList.add(Long.parseLong(s));   
        }
        return longList;
    }    
}
