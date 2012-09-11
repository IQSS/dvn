/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.admin;

import com.icesoft.faces.component.ext.*;
import com.icesoft.faces.context.Resource;
import com.icesoft.faces.context.effects.JavascriptContext;
import edu.harvard.iq.dvn.core.admin.*;
import edu.harvard.iq.dvn.core.harvest.HarvestFormatType;
import edu.harvard.iq.dvn.core.harvest.HarvesterServiceLocal;
import edu.harvard.iq.dvn.core.harvest.SetDetailBean;
import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.*;
import edu.harvard.iq.dvn.core.util.DateUtil;
import edu.harvard.iq.dvn.core.util.PropertyUtil;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.util.TwitterUtil;
import edu.harvard.iq.dvn.core.vdc.*;
import edu.harvard.iq.dvn.core.web.VDCUIList;
import edu.harvard.iq.dvn.core.web.collection.CollectionUI;
import edu.harvard.iq.dvn.core.web.common.StatusMessage;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.login.LoginWorkflowBean;
import edu.harvard.iq.dvn.core.web.push.beans.NetworkStatsBean;
import edu.harvard.iq.dvn.core.web.site.ClassificationList;
import edu.harvard.iq.dvn.core.web.site.ClassificationUI;
import edu.harvard.iq.dvn.core.web.site.VDCUI;
import edu.harvard.iq.dvn.core.web.util.CharacterValidator;
import edu.harvard.iq.dvn.core.web.util.ExceptionMessageWriter;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.routines.InetAddressValidator;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 *
 * @author gdurand
 */

@ViewScoped
@Named("OptionsPage")
public class OptionsPage extends VDCBaseBean  implements java.io.Serializable {

    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB VDCServiceLocal vdcService; 
    @EJB TemplateServiceLocal templateService;
    @EJB MailServiceLocal mailService;
    @EJB EditVDCPrivilegesService editVDCPrivileges;
    @EJB UserServiceLocal userService;
    @EJB GroupServiceLocal groupService;
    @EJB VDCCollectionServiceLocal vdcCollectionService;
    @EJB RoleServiceLocal roleService;
    @EJB GuestBookResponseServiceBean guestBookResponseServiceBean;
    @EJB OAISetServiceLocal oaiService;
    @EJB EditLockssService editLockssService;
    @EJB EditHarvestSiteService editHarvestSiteService;
    DvnTimerRemote remoteTimerService;
    @EJB HarvesterServiceLocal harvesterService;
    @EJB HandlePrefixServiceLocal handlePrefixService;
    @EJB StudyFieldServiceLocal studyFieldService;
    @EJB IndexServiceLocal indexService;
    @EJB EditUserService editUserService;
    @EJB StudyServiceLocal studyService;

    private String twitterVerifier;

    public String getTwitterVerifier() {
        return twitterVerifier;
    }

    public void setTwitterVerifier(String twitterVerifier) {
        this.twitterVerifier = twitterVerifier;
    }
    
    
    
    public void init() {       
        if (twitterVerifier != null && getSessionMap().get("requestToken") != null) {
            addTwitter();           
        }                 
        //manage templates
        if (getVDCRequestBean().getCurrentVDC() != null){
           templateList = templateService.getEnabledNetworkTemplates();
           templateList.addAll(templateService.getVDCTemplates(getVDCRequestBean().getCurrentVDCId()));
           defaultTemplateId = getVDCRequestBean().getCurrentVDC().getDefaultTemplate().getId();            
        } else {
           templateList = templateService.getNetworkTemplates();
           defaultTemplateId = getVDCRequestBean().getVdcNetwork().getDefaultTemplate().getId();
        }
        
        // whether a template is being used determines if it can be removed or not (initialized here, so the page doesn't call thew service bean multiple times)
        for (Template template : templateList) {
            if (templateService.isTemplateUsed(template.getId() )) 
            { 
                templateInUseList.add(template.getId());
            }            
            if (templateService.isTemplateUsedAsVDCDefault(template.getId() )) 
            { 
                templateinsUseAsVDCDefaultList.add(template.getId());
            }
        }
        
        //Privileged users page        
        vdcId = getVDCRequestBean().getCurrentVDC().getId();
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
        
        //terms of use
        depositTermsOfUse = getVDCRequestBean().getCurrentVDC().getDepositTermsOfUse();
        depositTermsOfUseEnabled = getVDCRequestBean().getCurrentVDC().isDepositTermsOfUseEnabled();
        downloadTermsOfUse = getVDCRequestBean().getCurrentVDC().getDownloadTermsOfUse();
        downloadTermsOfUseEnabled = getVDCRequestBean().getCurrentVDC().isDownloadTermsOfUseEnabled();
        
        //guestbook questionnaire
        guestBookQuestionnaire = getVDCRequestBean().getCurrentVDC().getGuestBookQuestionnaire();
        newQuestion = new CustomQuestion();
        newQuestion.setCustomQuestionValues(new ArrayList());
        if (guestBookQuestionnaire == null) { // set up default guest book questionnaire
            guestBookQuestionnaire = new GuestBookQuestionnaire();
            guestBookQuestionnaire.setEnabled(false);
            guestBookQuestionnaire.setEmailRequired(true);
            guestBookQuestionnaire.setFirstNameRequired(true);
            guestBookQuestionnaire.setLastNameRequired(true);
            guestBookQuestionnaire.setPositionRequired(true);
            guestBookQuestionnaire.setInstitutionRequired(true);
            guestBookQuestionnaire.setVdc(vdc);
            guestBookQuestionnaire.setCustomQuestions(new ArrayList());
            vdc.setGuestBookQuestionnaire(guestBookQuestionnaire);
        } else {
            if (guestBookQuestionnaire.getCustomQuestions() == null) {
                guestBookQuestionnaire.setCustomQuestions(new ArrayList());
            } else {
                for (CustomQuestion customQuestion : guestBookQuestionnaire.getCustomQuestions()) {
                    if (!customQuestion.isHidden()) {
                        CustomQuestionUI customQuestionUI = new CustomQuestionUI();
                        customQuestionUI.setCustomQuestion(customQuestion);
                        customQuestionUI.setEditMode(false);
                        customQuestions.add(customQuestionUI);
                    }
                }
            }
        }
        newQuestion.setGuestBookQuestionnaire(guestBookQuestionnaire);
        questionTypeSelectItems = loadQuestionTypeSelectItems();
               
        //guestbook responses
        Date today = new Date();
        Calendar cal = new GregorianCalendar();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date today30 = cal.getTime();     
        
        guestBookResponsesAll = guestBookResponseServiceBean.findAll();           
        for (GuestBookResponse gbr : guestBookResponsesAll) {
            if (vdc !=null && gbr.getStudy().getOwner().equals(vdc)) {
                guestBookResponses.add(gbr);
                fullCount++;
                if (today30.before(gbr.getResponseTime())){
                    thirtyDayCount++;
                }
                if (!gbr.getCustomQuestionResponses().isEmpty()) {
                    for (CustomQuestionResponse cqr : gbr.getCustomQuestionResponses()) {
                        if (!customQuestionIds.contains(cqr.getCustomQuestion().getId())) {
                            customQuestionIds.add(cqr.getCustomQuestion().getId());
                            columnHeadings.add(cqr.getCustomQuestion().getQuestionString());
                        }
                    }
                }
            }  else if (vdc == null) {
                  guestBookResponses.add(gbr);
                  fullCount++;
                if (today30.before(gbr.getResponseTime())){
                    thirtyDayCount++;
                }
            }
        }
        
        if (!customQuestionIds.isEmpty()) {
            for (GuestBookResponse gbr : guestBookResponses) {
                GuestBookResponseDisplay guestBookResponseDisplay = new GuestBookResponseDisplay();
                guestBookResponseDisplay.setGuestBookResponse(gbr);
                List<String> customQuestionResponseStrings = new ArrayList(customQuestionIds.size());
                for (int i=0; i<customQuestionIds.size(); i++){
                    customQuestionResponseStrings.add(i, "");
                }
                if (!gbr.getCustomQuestionResponses().isEmpty()) {
                    for (Long id : customQuestionIds) {
                        int index = customQuestionIds.indexOf(id);
                        for (CustomQuestionResponse cqr : gbr.getCustomQuestionResponses()) {
                            if (cqr.getCustomQuestion().getId().equals(id)) {
                                customQuestionResponseStrings.set(index, cqr.getResponse());
                            }
                        }
                    }
                }
                guestBookResponseDisplay.setCustomQuestionResponses(customQuestionResponseStrings);
                guestBookResponsesDisplay.add(guestBookResponseDisplay);
            }
        } else {
            for (GuestBookResponse gbr : guestBookResponses) {
                GuestBookResponseDisplay guestBookResponseDisplay = new GuestBookResponseDisplay();
                guestBookResponseDisplay.setGuestBookResponse(gbr);
                guestBookResponsesDisplay.add(guestBookResponseDisplay);
            }
            
        }
        writeCSVString();
        
        // Home panels
        success = false;       
        chkNetworkAnnouncements = getVDCRequestBean().getCurrentVDC().isDisplayNetworkAnnouncements();
        chkLocalAnnouncements = getVDCRequestBean().getCurrentVDC().isDisplayAnnouncements();
        localAnnouncements = getVDCRequestBean().getCurrentVDC().getAnnouncements();
        chkNewStudies = getVDCRequestBean().getCurrentVDC().isDisplayNewStudies();
        
        //Contact us page
        this.setContactUsEmail(vdc.getContactEmail());
        
        //Edit Study Comments
        allowStudyComments = vdc.isAllowStudyComments();
        allowStudyCommentsCheckbox.setValue(allowStudyComments);
        
        //edit site page       
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Iterator iterator = request.getParameterMap().keySet().iterator();
        while (iterator.hasNext()) {
            Object key = (Object) iterator.next();
            if ( key instanceof String && ((String) key).indexOf("dataverseType") != -1 && !request.getParameter((String)key).equals("")) {
                this.setDataverseType(request.getParameter((String)key));
            }
        }
        if (this.dataverseType == null && getVDCRequestBean().getCurrentVDC().getDtype() != null) {
            this.setDataverseType(getVDCRequestBean().getCurrentVDC().getDtype());
        }
            if ( (this.dataverseType == null || this.dataverseType.equals("Scholar")) ) {
                //set the default values for the fields
                VDC scholardataverse = (VDC)vdcService.findScholarDataverseByAlias(vdc.getAlias());
                setDataverseType("Scholar");
                setFirstName(scholardataverse.getFirstName());
                setLastName(scholardataverse.getLastName());
                HtmlInputText affiliationText = new HtmlInputText();
                affiliationText.setValue(scholardataverse.getAffiliation());
                setAffiliation(affiliationText);
                HtmlInputText nameText = new HtmlInputText();
                nameText.setValue(scholardataverse.getName());
                setDataverseName(nameText);
                HtmlInputText aliasText = new HtmlInputText();
                aliasText.setValue(scholardataverse.getAlias());
                HtmlInputTextarea descriptionText = new HtmlInputTextarea();
                descriptionText.setValue(scholardataverse.getDescription());
                setShortDescription(descriptionText);
            } else if (!this.dataverseType.equals("Scholar")) {
                setDataverseType("Basic");
                HtmlInputText nameText = new HtmlInputText();
                nameText.setValue(vdc.getName());
                setDataverseName(nameText);
                HtmlInputText affiliationText = new HtmlInputText();
                affiliationText.setValue(vdc.getAffiliation());
                setAffiliation(affiliationText);
                HtmlInputText aliasText = new HtmlInputText();
                aliasText.setValue(vdc.getAlias());
                HtmlInputTextarea descriptionText = new HtmlInputTextarea();
                descriptionText.setValue(vdc.getDvnDescription());
                setShortDescription(descriptionText);
            }
            // initialize the select
           for (ClassificationUI classUI: classificationList.getClassificationUIs()) {
                if (classUI.getVdcGroup().getVdcs().contains(vdc)) {
                    classUI.setSelected(true);
                }
           }           
        //Edit Lockss settings
        Long lockssConfigId = null;
        
        if (getVDCRequestBean().getCurrentVDC()!=null) {
            if (getVDCRequestBean().getCurrentVDC().getLockssConfig()!=null)
            lockssConfigId = this.getVDCRequestBean().getCurrentVDC().getLockssConfig().getId();
        } else {
            LockssConfig networkConfig = vdcNetworkService.getLockssConfig();
            if (networkConfig!=null) {
                lockssConfigId = networkConfig.getId();
            }
        }
        // Based on lockssConfigId, set up stateful session bean for create/edit,
        // and set default values for form controls
        if (lockssConfigId == null) {
            editLockssService.newLockssConfig(getVDCRequestBean().getCurrentVDCId());
            lockssConfig = editLockssService.getLockssConfig();
            selectHarvestType = EditLockssConfigPage.HarvestType.NONE;
            selectOAISetId = new Long(-1);
        } else {
            editLockssService.initLockssConfig(lockssConfigId);
            lockssConfig = editLockssService.getLockssConfig();
            selectLicenseId = lockssConfig.getLicenseType().getId();
            selectHarvestType = EditLockssConfigPage.HarvestType.valueOf(lockssConfig.getserverAccess().toString());
            if (getVDCRequestBean().getCurrentVDC()==null && lockssConfig.getOaiSet()!=null) {
                this.selectOAISetId = lockssConfig.getOaiSet().getId();
            }
        }
       
        for (LicenseType licenseType : editLockssService.getLicenseTypes()) {
            licenseTypes.put(licenseType.getId(), licenseType);
        }
        initCollection();       
        if (this.getBanner() == null){
            setBanner( (getVDCRequestBean().getCurrentVDCId() == null) ? getVDCRequestBean().getVdcNetwork().getNetworkPageHeader(): getVDCRequestBean().getCurrentVDC().getHeader());
            setFooter( (getVDCRequestBean().getCurrentVDCId() == null) ? getVDCRequestBean().getVdcNetwork().getNetworkPageFooter(): getVDCRequestBean().getCurrentVDC().getFooter());
            
            if (getVDCRequestBean().getCurrentVDCId() != null) {
                setDisplayInFrame(getVDCRequestBean().getCurrentVDC().isDisplayInFrame());
                setParentSite(getVDCRequestBean().getCurrentVDC().getParentSite());
            }
        }
        combinedTextField.setValue(banner + footer);
        
        // Search Fields
        searchResultsFields = getVDCRequestBean().getCurrentVDC().getSearchResultFields();
        for (Iterator it = searchResultsFields.iterator(); it.hasNext();) {
            StudyField elem = (StudyField) it.next();
            if (elem.getName().equals("productionDate")){
                productionDateResults = true;
            }            
            if (elem.getName().equals("producerName")){
                producerResults = true;
            }
            if (elem.getName().equals("distributionDate")){
                distributionDateResults = true;
            }
            if (elem.getName().equals("distributorName")){
                distributorResults = true;
            }
            if (elem.getName().equals("replicationFor")){
                replicationForResults = true;
            }
            if (elem.getName().equals("relatedPublications")){
                relatedPublicationsResults = true;
            }
            if (elem.getName().equals("relatedMaterial")){
                relatedMaterialResults = true;
            }
            if (elem.getName().equals("relatedStudies")){
                relatedStudiesResults = true;
            }
        }
        defaultSortOrder = getVDCRequestBean().getCurrentVDC().getDefaultSortOrder();
        //Add site page
        iterator = request.getParameterMap().keySet().iterator();
        while (iterator.hasNext()) {
            Object key = (Object) iterator.next();
            if (key instanceof String && ((String) key).indexOf("dataverseType") != -1 && !request.getParameter((String) key).equals("")) {
                this.setDataverseType(request.getParameter((String) key));
            }
        }
        //manage dataverses page
        initAlphabeticFilter();
        populateVDCUIList(false);
        
        //add account page
        if ( isFromPage("AddAccountPage") ) {
            editUserService = (EditUserService) sessionGet(editUserService.getClass().getName());
            user = editUserService.getUser();
            
        } else {
            editUserService.newUser();
            sessionPut( editUserService.getClass().getName(), editUserService);
            //sessionPut( (studyService.getClass().getName() + "."  + studyId.toString()), studyService);
            user = editUserService.getUser();
            request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
            if (request.getAttribute("studyId") != null) {
                studyId = new Long(request.getAttribute("studyId").toString());
                editUserService.setRequestStudyId(studyId);
            } else if (this.getRequestParam("studyId") != null) {
                studyId = new Long(Long.parseLong(getRequestParam("studyId")));
                editUserService.setRequestStudyId(studyId);
            }
            
        }
        studyId = editUserService.getRequestStudyId();
        if (studyId!=null) {
            study = studyService.getStudy(studyId);
        }
    }
    
    public void updateEnabledAction(Long templateId) {
        Template template = null;
        int templateIndex = 0;
        
        // get the the template (and the index, so we can refrsh afterwards)
        for (int i = 0; i < templateList.size(); i++) {
            Template t = templateList.get(i);
            if (t.getId().equals(templateId)) {
                templateIndex = i;
                template = t;
                break;
            }   
        }        
        
        // first, check if we are trying disable a template and verify it has not been made a default
        if (template.isEnabled()) {
            // network level template
            if (getVDCRequestBean().getCurrentVDC() == null) {
                if ( vdcNetworkService.find().getDefaultTemplate().equals(template) || templateService.isTemplateUsedAsVDCDefault(template.getId()) ) {
                    getVDCRenderBean().getFlash().put("warningMessage","The template you are trying to disable was made a default template by another user. Please reload this page to update.");
                    return;
                }
            // vdc level template    
            } else if (vdcService.findById(getVDCRequestBean().getCurrentVDCId()).getDefaultTemplate().equals(template)) {
                getVDCRenderBean().getFlash().put("warningMessage","The template you are trying to disable was made a default template by another user. Please reload this page to update.");
                return;
            }
        }   else {

            for(Template testTemplate : templateList){
                if (testTemplate.isEnabled() && !testTemplate.equals(template) && testTemplate.getName().equals(template.getName())){
                    getVDCRenderBean().getFlash().put("warningMessage","The template you are trying to enable has the same name as another enabled template. Please edit the name and re-try enabling.");
                    return;
                }
            }
        }
        
        template.setEnabled(!template.isEnabled());
        templateService.updateTemplate(template);
        // now update the template in the list (this is needed or a second attempt to change will result in an optimistic lock)
        templateList.set(templateIndex, templateService.getTemplate(template.getId()));
    }
    
    public String updateDefaultAction(Long templateId) {
        // first, verify that the template has not been disabled
        if (templateService.getTemplate(templateId).isEnabled()) {
            if (getVDCRequestBean().getCurrentVDC() == null) {
                vdcNetworkService.updateDefaultTemplate(templateId);
            } else {
                vdcService.updateDefaultTemplate(getVDCRequestBean().getCurrentVDCId(),templateId);
            }
            defaultTemplateId = templateId;
        } else {
            // add flash message
            getVDCRenderBean().getFlash().put("warningMessage","The template you are trying to make Default was disabled by another user. Please reload this page to update.");            
        }
        return "";
    }
    
    public String save_action() {
        String forwardPage=null;
        if (getVDCRequestBean().getCurrentVDCId() == null) {
            // this is a save against the network
            VDCNetwork vdcnetwork = getVDCRequestBean().getVdcNetwork();
            vdcnetwork.setNetworkPageHeader(banner);
            vdcnetwork.setNetworkPageFooter(footer);
            vdcNetworkService.edit(vdcnetwork);
            getVDCRequestBean().getVdcNetwork().setNetworkPageHeader(banner);
            getVDCRequestBean().getVdcNetwork().setNetworkPageFooter(footer);
            forwardPage="/networkAdmin/NetworkOptionsPage?faces-redirect=true";
        } else {
            vdc.setHeader(banner);
            vdc.setFooter(footer);
            vdc.setDisplayInFrame(displayInFrame);
            vdc.setParentSite(parentSite);
            vdcService.edit(vdc);
            forwardPage="/admin/OptionsPage?faces-redirect=true&vdcId="+getVDCRequestBean().getCurrentVDC().getId();
        }
        getVDCRenderBean().getFlash().put("successMessage","Successfully updated layout branding.");
        return forwardPage;
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
                // tweet release of dataverse
                TwitterCredentials tc = vdcNetworkService.getTwitterCredentials();
                if (tc != null) {              
                    try {
                        String message = "New dataverse released: " + vdc.getName() + " Dataverse";
                        URL url = new URL("http://" + PropertyUtil.getHostUrl() + "/dvn/dv/" + vdc.getAlias());
                        TwitterUtil.tweet(tc, message, url);
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(PrivilegedUsersPage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }        
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
                // Prior to 3.0, we called a stateless RoleService bean to get the role, but with the upgrade
                // this was creating a new row for each Role; so we moved the finder method to the stateful bean
                // so the row would not be detached and this fixed it
                Role role = editVDCPrivileges.findRoleById(vdcRoleList.get(i).selectedRoleId);
                vdcRoleList.get(i).getVdcRole().setRole(role);
            }
        }

        this.editVDCPrivileges.save();
        
       
        editVDCPrivileges.setVdc(vdc.getId());
        vdc = editVDCPrivileges.getVdc();
        getVDCRenderBean().getFlash().put("successMessage","Successfully updated dataverse permissions.");
        return "/admin/OptionsPage?faces-redirect=true&vdcId="+getVDCRequestBean().getCurrentVDC().getId();
    } 
    
    public String cancel_action(){
        if (getVDCRequestBean().getCurrentVDCId() == null) {
            setBanner(getVDCRequestBean().getVdcNetwork().getNetworkPageHeader());
            setFooter(getVDCRequestBean().getVdcNetwork().getNetworkPageFooter());
            return "/networkAdmin/NetworkOptionsPage?faces-redirect=true";
        } else {
                setBanner(getVDCRequestBean().getCurrentVDC().getHeader());
                setFooter(getVDCRequestBean().getCurrentVDC().getFooter());
                setDisplayInFrame(getVDCRequestBean().getCurrentVDC().isDisplayInFrame());
                setParentSite(getVDCRequestBean().getCurrentVDC().getParentSite());
            return "/admin/OptionsPage?faces-redirect=true&vdcId="+getVDCRequestBean().getCurrentVDC().getId();
        }
    }
    
    private boolean displayInFrame;
    private String parentSite;

    public boolean isDisplayInFrame() {return displayInFrame;}

    public void setDisplayInFrame(boolean displayInFrame) {
        this.displayInFrame = displayInFrame;
        // add javascript call on each partial submit to trigger jQuery
        JavascriptContext.addJavascriptCall(getFacesContext(), "initOpenScholarDataverse();");
    }

    public String getParentSite() {return parentSite;}
    public void setParentSite(String parentSite) {this.parentSite = parentSite;}
    
    private String banner;    
    public String getBanner(){return banner;}    
    public void setBanner(String banner) {this.banner = banner;}
    
    private String footer;
    public String getFooter() {return footer;}
    
    public void setFooter(String footer) {this.footer = footer;}
    
    protected HtmlInputHidden combinedTextField = new HtmlInputHidden();
    public HtmlInputHidden getCombinedTextField() {return combinedTextField;}
    public void setCombinedTextField(HtmlInputHidden combinedTextField) {this.combinedTextField = combinedTextField;}  
    
    public String authorizeTwitter() {
        String callbackURL = "http://" + PropertyUtil.getHostUrl() + "/dvn";
        callbackURL += getVDCRequestBean().getCurrentVDC() == null ? "/faces/networkAdmin/NetworkOptionsPage.xhtml" : 
              getVDCRequestBean().getCurrentVDCURL() + "/faces/admin/OptionsPage.xhtml";
                
        Twitter twitter = new TwitterFactory().getInstance();

        try {
            RequestToken requestToken = twitter.getOAuthRequestToken(callbackURL);
            getSessionMap().put("requestToken", requestToken);
            redirect(requestToken.getAuthorizationURL());
        } catch (TwitterException te) {
            te.printStackTrace();
        }
        
        return null;
    }
    
    public void addTwitter() {
        Long vdcId = getVDCRequestBean().getCurrentVDCId();

        try {
            Twitter twitter = new TwitterFactory().getInstance();
            AccessToken accessToken = twitter.getOAuthAccessToken( (RequestToken) getSessionMap().remove("requestToken"), twitterVerifier );
            vdcService.setTwitterCredentials(accessToken.getToken(),accessToken.getTokenSecret(), vdcId);
            
            if (vdcId != null) {
                // refresh the current vdc object, since it has changed
                getVDCRequestBean().setCurrentVDC( vdcService.findById( vdcId ));
            }
            getVDCRenderBean().getFlash().put("successMessage", "Automatic tweets are now enabled.");
            
        } catch (TwitterException te) {
            te.printStackTrace();
        }                       
    }
    
    public void removeTwitter() {
        Long vdcId = getVDCRequestBean().getCurrentVDCId();
        vdcService.removeTwitterCredentials(vdcId);
        
        if (vdcId != null) {
            // refresh the current vdc object, since it has changed
            getVDCRequestBean().setCurrentVDC( vdcService.findById( vdcId ));
        }        
        
        getVDCRenderBean().getFlash().put("successMessage", "Automatic tweets are now disabled.");
    }

    public boolean isTwitterConfigured() {
        return PropertyUtil.isTwitterConsumerConfigured();
    }    
    
    public boolean isTwitterEnabled() {
        if (getVDCRequestBean().getCurrentVDC() == null ) {
            return (vdcNetworkService.getTwitterCredentials() != null);
        } else {
            return (getVDCRequestBean().getCurrentVDC().getTwitterCredentials() != null);
        }
        
    }
    
    private void sendReleaseEmails() {
        String networkAdminEmailAddress = getVDCRequestBean().getVdcNetwork().getContactEmail();
        vdc = vdcService.find(new Long(getVDCRequestBean().getCurrentVDC().getId()));
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
    
    public enum ContributorSetting {
        CONTRIB_CREATE,
        CONTRIB_EDIT,
        USER_CREATE,
        USER_EDIT
    };

    private ContributorSetting selectedSetting;
    public ContributorSetting getSelectedSetting() {return selectedSetting;}
    public void setSelectedSetting(ContributorSetting selectedSetting) {this.selectedSetting = selectedSetting;}
        
    private List<Template> templateList;
    public List<Template> getTemplateList() {return templateList;}
    
    Long defaultTemplateId;
    public Long getDefaultTemplateId() {return defaultTemplateId;}
    public void setDefaultTemplateId(Long defaultTemplateId) {this.defaultTemplateId = defaultTemplateId;}    
    
    private List<Long> templateInUseList = new ArrayList();
    private List<Long> templateinsUseAsVDCDefaultList = new ArrayList();  

    public boolean isDefault(Long templateId) {return defaultTemplateId.equals(templateId);}   
    public boolean isInUse(Long templateId) {return templateInUseList.contains(templateId);}
    public boolean isVDCDefault(Long templateId) {return templateinsUseAsVDCDefaultList.contains(templateId);}
    
    private String siteRestriction;
    public String getSiteRestriction() {return siteRestriction;}
    public void setSiteRestriction(String siteRestriction) {this.siteRestriction = siteRestriction;}
    
    private String newUserName;
    public String getNewUserName() {return this.newUserName;}
    public void setNewUserName(String userName) {this.newUserName = userName;}
    
    private boolean filesRestricted;
    public boolean isFilesRestricted() {return this.filesRestricted;}
    public void setFilesRestricted(boolean filesRestricted) {this.filesRestricted = filesRestricted;}
    
    private List<RoleListItem> vdcRoleList;
    public List<RoleListItem> getVdcRoleList() {return vdcRoleList;}
    public void setVdcRoleList(List<RoleListItem> vdcRoleList) {this.vdcRoleList = vdcRoleList;}
    
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
    
    private VDC vdc;
    public VDC getVdc() {return this.vdc;}
    public void setVdc(VDC vdc) {this.vdc = vdc;}

    private Long vdcId;
    public Long getVdcId() {return this.vdcId;}
    public void setVdcId(Long vdcId) {this.vdcId = vdcId;} 
    
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
    private void initUserName() {
        newUserName = "Enter Username";
    }
    
    private javax.faces.component.UIData userTable;
    public javax.faces.component.UIData getUserTable() {return userTable;}

    public void setUserTable(javax.faces.component.UIData userTable) {this.userTable = userTable;}
    
    private HtmlInputText userInputText;
    public HtmlInputText getUserInputText() {return this.userInputText;}
    public void setUserInputText(HtmlInputText userInputText) {this.userInputText = userInputText;}

    private HtmlInputText groupInputText;
    public HtmlInputText getGroupInputText() {return this.groupInputText;}
    public void setGroupInputText(HtmlInputText groupInputText) {this.groupInputText = groupInputText;}

    private HtmlDataTable fileGroupTable;
    public HtmlDataTable getFileGroupTable() {return this.fileGroupTable;}
    public void setFileGroupTable(HtmlDataTable fileGroupTable) {this.fileGroupTable = fileGroupTable;}

    private HtmlDataTable fileUserTable;
    public HtmlDataTable getFileUserTable() {return this.fileUserTable;}
    public void setFileUserTable(HtmlDataTable fileUserTable) {this.fileUserTable = fileUserTable;}

    private String addFileUserName;
    public String getAddFileUserName() {return this.addFileUserName;}
    public void setAddFileUserName(String addFileUserName) {this.addFileUserName = addFileUserName;}

    private String addFileGroupName;
    public String getAddFileGroupName() {return this.addFileGroupName;}
    public void setAddFileGroupName(String addFileGroupName) {this.addFileGroupName = addFileGroupName;}
    
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

    private HtmlInputText fileUserInputText;
    public HtmlInputText getFileUserInputText() {return this.fileUserInputText;}
    public void setFileUserInputText(HtmlInputText fileUserInputText) {this.fileUserInputText = fileUserInputText;}

    private HtmlInputText fileGroupInputText;
    public HtmlInputText getFileGroupInputText() {return this.fileGroupInputText;}
    public void setFileGroupInputText(HtmlInputText fileGroupInputText) {this.fileGroupInputText = fileGroupInputText;}

    private boolean success;
    public boolean isSuccess() {return this.success;}
    public void setSuccess(boolean success) {this.success = success;}

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

    private boolean hasStudies(VDC vdcIn) {

        if (vdcIn.getNumberReleasedStudies() > 0 || vdcIn.isHarvestingDv()
                || vdcIn.getRootCollection().getStudies().size() > 0) {
            return true;
        }

        if (vdcIn.getRootCollection().getStudies().size() > 0) {
            return true;
        }

        if (vdcIn.getOwnedCollections().size() > 1) {
            for (VDCCollection vdcc : vdcIn.getOwnedCollections()) {
                if (vdcc.getStudies().size() > 0) {
                    return true;
                }
            }
        }

        if (vdcIn.getLinkedCollections().size() > 0) {
            for (VDCCollection vdcc : vdcIn.getLinkedCollections()) {
                if (vdcc.getStudies().size() > 0) {
                    return true;
                }
            }
        }

        if (vdcIn != null) {
            Long collectionId = new Long(0);
            if (new VDCUI(vdcIn).containsOnlyLinkedCollections()) {
                collectionId = new Long(getVDCRequestBean().getCurrentVDC().getLinkedCollections().get(0).getId());
            } else {
                collectionId = new Long(getVDCRequestBean().getCurrentVDC().getRootCollection().getId());
            }

            CollectionUI collUI = new CollectionUI(vdcCollectionService.find(new Long(collectionId)));
            if (collUI.getStudyIds().size() > 0) {
                return true;
            }

        }
        return false;
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
    
    private Long newRoleId;
    public Long getNewRoleId() {return newRoleId;}
    public void setNewRoleId(Long newRoleId) {this.newRoleId = newRoleId;}

    private String groupName;
    public String getGroupName() {return this.groupName;}
    public void setGroupName(String groupName) {this.groupName = groupName;}

    private UIData groupTable;
    public UIData getGroupTable() {return this.groupTable;}
    public void setGroupTable(UIData groupTable) {this.groupTable = groupTable;}
    
    public String tousave_action() {
        if (validateTerms()) {
            VDC vdc = vdcService.find(new Long(getVDCRequestBean().getCurrentVDC().getId()));
            vdc.setDepositTermsOfUse(depositTermsOfUse);
            vdc.setDepositTermsOfUseEnabled(depositTermsOfUseEnabled);
            vdc.setDownloadTermsOfUse(downloadTermsOfUse);
            vdc.setDownloadTermsOfUseEnabled(downloadTermsOfUseEnabled);
            vdcService.edit(vdc);
            String    forwardPage="/admin/OptionsPage?faces-redirect=true&vdcId="+getVDCRequestBean().getCurrentVDC().getId();
            getVDCRenderBean().getFlash().put("successMessage","Successfully updated terms of use for study creation.");
            return forwardPage;                      
        } else {
            return null;
        }
    }
    
    public String toucancel_action(){
            return  "/admin/OptionsPage?faces-redirect=true&vdcId="+getVDCRequestBean().getCurrentVDC().getId();
    }
    
    private boolean validateTerms() {
        String elementValue = downloadTermsOfUse;
        boolean isUseTerms = true;
        if ( (elementValue == null || elementValue.equals("")) && (downloadTermsOfUseEnabled) ) {
            isUseTerms = false;
            FacesMessage message = new FacesMessage("This field is required.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(textAreaDownloadUse.getClientId(fc), message);
        }
        elementValue = depositTermsOfUse;
        if ( (elementValue == null || elementValue.equals("")) && (depositTermsOfUseEnabled) ) {
            isUseTerms = false;
            FacesMessage message = new FacesMessage("This field is required.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(textAreaDepositUse.getClientId(fc), message);
        }             
        return isUseTerms;
    }
    
    private HtmlInputTextarea textAreaDepositUse;
    public HtmlInputTextarea getTextAreaDepositUse() {return textAreaDepositUse;}
    public void setTextAreaDepositUse(HtmlInputTextarea textAreaDepositUse) {this.textAreaDepositUse = textAreaDepositUse;}

    private HtmlInputTextarea textAreaDownloadUse; 
    public HtmlInputTextarea getTextAreaDownloadUse() {return textAreaDownloadUse;}
    public void setTextAreaDownloadUse(HtmlInputTextarea textAreaDownloadUse) {this.textAreaDownloadUse = textAreaDownloadUse;}
   
    private boolean depositTermsOfUseEnabled;
    public boolean isDepositTermsOfUseEnabled() {return depositTermsOfUseEnabled;}
    public void setDepositTermsOfUseEnabled(boolean termsOfUseEnabled) {this.depositTermsOfUseEnabled = termsOfUseEnabled;}
    
    private String depositTermsOfUse;
    public String getDepositTermsOfUse() {return depositTermsOfUse;}
    public void setDepositTermsOfUse(String termsOfUse) {this.depositTermsOfUse = termsOfUse;}
    
    private boolean downloadTermsOfUseEnabled;
    public boolean isDownloadTermsOfUseEnabled() {return downloadTermsOfUseEnabled;}
    public void setDownloadTermsOfUseEnabled(boolean termsOfUseEnabled) {
        this.downloadTermsOfUseEnabled = termsOfUseEnabled;
    }
    private String downloadTermsOfUse;
    public String getDownloadTermsOfUse() {return downloadTermsOfUse;}
    public void setDownloadTermsOfUse(String termsOfUse) {this.downloadTermsOfUse = termsOfUse;}   
    
    private GuestBookQuestionnaire guestBookQuestionnaire;
    private List<CustomQuestionUI> customQuestions = new ArrayList();
    private String questionType;
    private CustomQuestion newQuestion;
    
    private List<SelectItem> loadQuestionTypeSelectItems() {
        List selectItems = new ArrayList<SelectItem>();
        selectItems.add(new SelectItem("text", "Plain Text Input"));
        selectItems.add(new SelectItem("radiobuttons", "Radio Buttons"));
        return selectItems;
    }
    private List<SelectItem> questionTypeSelectItems = new ArrayList();
    public List<SelectItem> getQuestionTypeSelectItems() {return questionTypeSelectItems;}
    public void setQuestionTypeSelectItems(List<SelectItem> questionTypeSelectItems) {this.questionTypeSelectItems = questionTypeSelectItems;}
    
    public String guestbooksave_action() {
        if (validateTerms()) {
            vdcService.save(vdc);
            String forwardPage = "/admin/OptionsPage?faces-redirect=true&vdcId=" + getVDCRequestBean().getCurrentVDC().getId();
            getVDCRenderBean().getFlash().put("successMessage", "Successfully updated guest book questionnaire.");
            return forwardPage;
        } else {
            return null;
        }
    }

    public String guestbookcancel_action() {
        return "/admin/OptionsPage?faces-redirect=true&vdcId=" + getVDCRequestBean().getCurrentVDC().getId();
    }

    public boolean isQuestionRemovable() {return true;}

    private List<SelectItem> questionInputLevelSelectItems = new ArrayList();
    public List<SelectItem> getQuestionInputLevelSelectItems() {
        return this.questionInputLevelSelectItems;
    }
    private HtmlSelectOneMenu questionTypeListBox;
    private HtmlInputText inputCustomQuestionText;
    private HtmlInputText inputCustomQuestionTextTable;
    private HtmlDataTable customQuestionsDataTable;

    public HtmlDataTable getCustomQuestionsDataTable() {return customQuestionsDataTable;}
    public void setCustomQuestionsDataTable(HtmlDataTable customQuestionsPanelSeries) {this.customQuestionsDataTable = customQuestionsPanelSeries;}

    public HtmlInputText getInputCustomQuestionText() {return inputCustomQuestionText;}
    public void setInputCustomQuestionText(HtmlInputText inputQuestionText) {this.inputCustomQuestionText = inputQuestionText;}

    public HtmlInputText getInputCustomQuestionTextTable() {return inputCustomQuestionTextTable;}
    public void setInputCustomQuestionTextTable(HtmlInputText inputCustomQuestionTextTable) {this.inputCustomQuestionTextTable = inputCustomQuestionTextTable;}
    public HtmlSelectOneMenu getQuestionTypeListBox() {return questionTypeListBox;}
    public void setQuestionTypeListBox(HtmlSelectOneMenu questionTypeListBox) {this.questionTypeListBox = questionTypeListBox;}

    public GuestBookQuestionnaire getGuestBookQuestionnaire() {return guestBookQuestionnaire;}

    public boolean getGuestBookQuestionnaireEnabled() {return guestBookQuestionnaire.isEnabled();}
    public void setGuestBookQuestionnaire(GuestBookQuestionnaire guestBookQuestionnaire) {this.guestBookQuestionnaire = guestBookQuestionnaire;}

    public void toggleQuestionType(ValueChangeEvent ae) {
        if (newQuestion.getCustomQuestionValues().isEmpty()) {
            CustomQuestionValue addCQV = new CustomQuestionValue();
            addCQV.setCustomQuestion(newQuestion);
            newQuestion.getCustomQuestionValues().add(addCQV);
        }
    }

    public void addCustomQuestion() {
        String questionText = (String) inputCustomQuestionText.getValue();
        if (questionText.trim().isEmpty()) {
            getVDCRenderBean().getFlash().put("customQuestionWarningMessage", "Please enter question text.");
            return;
        }
        newQuestion.setQuestionType(questionType);
        if (newQuestion.getQuestionType().equals("radiobuttons")) {
            if (newQuestion.getCustomQuestionValues().isEmpty()) {
                getVDCRenderBean().getFlash().put("customQuestionWarningMessage", "Please enter answer text.");
                return;
            } else {
                for (CustomQuestionValue cqv : newQuestion.getCustomQuestionValues()) {
                    if (cqv.getValueString().trim().isEmpty()) {
                        getVDCRenderBean().getFlash().put("customQuestionWarningMessage", "Please enter answer text.");
                        return;
                    }
                }
            }
        } else {
            newQuestion.setCustomQuestionValues(null);
        }
        newQuestion.setGuestBookQuestionnaire(guestBookQuestionnaire);
        newQuestion.setQuestionString(questionText);

        if (guestBookQuestionnaire.getCustomQuestions() == null) {
            guestBookQuestionnaire.setCustomQuestions(new ArrayList());
        }

        CustomQuestionUI customQuestionUI = new CustomQuestionUI();
        customQuestionUI.setCustomQuestion(newQuestion);
        customQuestionUI.setEditMode(false);
        customQuestions.add(customQuestionUI);
        guestBookQuestionnaire.getCustomQuestions().add(newQuestion);
        inputCustomQuestionText.setValue("");
        newQuestion = new CustomQuestion();
        newQuestion.setCustomQuestionValues(new ArrayList());
        questionType = "text";
    }

    public void removeCustomQuestion(ActionEvent ae) {
        //we will do an actual remove if there are no responses and a virtual remove if there are responses
        boolean remove = false;
        CustomQuestionUI customQuestionUI = (CustomQuestionUI) customQuestionsDataTable.getRowData();
        CustomQuestionUI customQuestionUIRemove = new CustomQuestionUI();
        for (CustomQuestionUI customQuestionUITest : customQuestions) {
            if (customQuestionUITest.getCustomQuestion().getQuestionString().equals(customQuestionUI.getCustomQuestion().getQuestionString())) {
                customQuestionUIRemove = customQuestionUITest;
                if (customQuestionUITest.getCustomQuestion().getId() != null && !customQuestionUITest.getCustomQuestion().getCustomQuestionResponses().isEmpty()) {
                    customQuestionUITest.getCustomQuestion().setHidden(true);
                } else {
                    remove = true;
                }
            }
        }
        customQuestions.remove(customQuestionUIRemove);
        if (remove) {
            vdc.getGuestBookQuestionnaire().getCustomQuestions().remove(customQuestionsDataTable.getRowIndex());
        }
    }

    public void editCustomQuestion(ActionEvent ae) {
        CustomQuestionUI customQuestionUI = (CustomQuestionUI) customQuestionsDataTable.getRowData();
        getInputCustomQuestionTextTable().setValue(customQuestionUI.getCustomQuestion().getQuestionString());
        customQuestionUI.setEditMode(true);
    }

    public void saveCustomQuestion(ActionEvent ae) {
        CustomQuestionUI customQuestionUI = (CustomQuestionUI) customQuestionsDataTable.getRowData();
        customQuestionUI.setEditMode(false);
    }

    public List<CustomQuestionUI> getCustomQuestions() {return customQuestions;}
    public void setCustomQuestions(List<CustomQuestionUI> customQuestions) {this.customQuestions = customQuestions;}

    public void addCustomRow(ActionEvent ae) {
        HtmlDataTable dataTable = (HtmlDataTable) ae.getComponent().getParent().getParent();
        int row = dataTable.getRowIndex();
        CustomQuestionValue data = (CustomQuestionValue) dataTable.getRowData();
        CustomQuestionValue newElem = new CustomQuestionValue();
        newElem.setCustomQuestion(data.getCustomQuestion());
        newElem.setValueString("");
        data.getCustomQuestion().getCustomQuestionValues().add(newElem);
    }

    public void removeCustomRow(ActionEvent ae) {
        HtmlDataTable dataTable = (HtmlDataTable) ae.getComponent().getParent().getParent();
        if (dataTable.getRowCount() > 1) {
            CustomQuestionValue data = (CustomQuestionValue) dataTable.getRowData();
            for (CustomQuestion cq : getGuestBookQuestionnaire().getCustomQuestions()) {
                if (cq.getQuestionString().equals(data.getCustomQuestion().getQuestionString())) {
                    cq.getCustomQuestionValues().remove(data);
                }
            }
        }
    }

    public void addCustomRowInit(ActionEvent ae) {
        HtmlDataTable dataTable = (HtmlDataTable) ae.getComponent().getParent().getParent();
        int row = dataTable.getRowIndex();
        CustomQuestionValue data = (CustomQuestionValue) dataTable.getRowData();
        CustomQuestionValue newElem = new CustomQuestionValue();
        newElem.setCustomQuestion(newQuestion);
        newElem.setValueString("");
        newQuestion.getCustomQuestionValues().add(newElem);
    }

    public void removeCustomRowInit(ActionEvent ae) {
        HtmlDataTable dataTable = (HtmlDataTable) ae.getComponent().getParent().getParent();
        if (dataTable.getRowCount() > 1) {
            int index =  dataTable.getRowIndex();
            newQuestion.getCustomQuestionValues().remove(index);
        }
    }

    public String getQuestionType() {return questionType;}
    public void setQuestionType(String questionType) {this.questionType = questionType;}
    public CustomQuestion getNewQuestion() {return newQuestion;}
    public void setNewQuestion(CustomQuestion newQuestion) {this.newQuestion = newQuestion;}
    
    //Download Response Page
    
    private List<GuestBookResponse> guestBookResponses = new ArrayList();
    private List<GuestBookResponseDisplay> guestBookResponsesDisplay = new ArrayList();
    private List<GuestBookResponse> guestBookResponsesAll = new ArrayList();
    private List<String> columnHeadings = new ArrayList();
    private List<Long> customQuestionIds = new ArrayList();
    private Long fullCount = new Long(0);
    private Long thirtyDayCount = new Long(0);
    private String csvString = "";
    
    private String getColumnString() {
        String csvColumnString = "";
        csvColumnString += "User/Session,First Name,Last Name,Email,Institution,Position,";
        if (vdc==null){
           csvColumnString += "Dataverse,";
        }
        csvColumnString += "Study Global ID, Study Title,Study File,Time,Type,Session";
        if (!columnHeadings.isEmpty()){
            for (String heading: columnHeadings){
                csvColumnString += "," + getSafeCString(heading);
            }
        }
        return csvColumnString;
    }
    
    private void writeCSVString() {
        String csvOutput = getColumnString() + "\n";
        String csvCol;
        for (GuestBookResponseDisplay gbrd: guestBookResponsesDisplay){
            csvCol = "";
            if(gbrd.getGuestBookResponse().getVdcUser() != null){
               csvCol += gbrd.getGuestBookResponse().getVdcUser().getUserName();
            } else {
               csvCol += "Anonymous - + " + gbrd.getGuestBookResponse().getSessionId();
            }
            csvCol += "," +getSafeCString(gbrd.getGuestBookResponse().getFirstname());
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getLastname());   
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getEmail());
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getInstitution());
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getPosition());
            if (vdc==null){
                csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getStudy().getOwner().getName());
            }
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getStudy().getGlobalId());
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getStudy().getLatestVersion().getMetadata().getTitle());
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getStudyFile().getFileName());
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getResponseTime().toString());
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getDownloadtype());
            csvCol += "," + getSafeCString(gbrd.getGuestBookResponse().getSessionId());
            if (!gbrd.getCustomQuestionResponses().isEmpty()){
                for (String response: gbrd.getCustomQuestionResponses()){
                    csvCol += "," + getSafeCString(response);
                }
            }
            csvCol += "\n";
            csvOutput = csvOutput + csvCol;
        }
        csvString = csvOutput;
    }
    
    private String getSafeCString(String strIn){
        String retString = strIn;
        if (strIn == null){
            return "";
        }
        int nextSpace = strIn.indexOf(",");  
        if(nextSpace > 0){
            // If the string is already enclosed in double quotes, remove them:
            retString = retString.replaceFirst("^\"", "");
            retString = retString.replaceFirst("\"$", "");

            // Escape all remaining double quotes, by replacing each one with
            // 2 double quotes in a row ("").
            // This is an ancient (IBM Mainframe ancient) CSV convention.
            // (see http://en.wikipedia.org/wiki/Comma-separated_values#Basic_rules)
            // Excel apparently follows it to this day. On the other hand,
            // Excel does NOT understand backslash escapes and will in fact
            // be confused by it!
            retString = retString.replaceAll("\"", "\"\"");
            // finally, add double quotes around the string:
            retString = "\"" + retString + "\"";
        } 
        return retString;
    }
    
    public List<GuestBookResponse> getGuestBookResponses() {return guestBookResponses;}
    public void setGuestBookResponses(List<GuestBookResponse> guestBookResponses) {this.guestBookResponses = guestBookResponses;}
    
    public Long getFullCount() {return fullCount;}
    public void setFullCount(Long fullCount) {this.fullCount = fullCount;}

    public Long getThirtyDayCount() {return thirtyDayCount;}
    public void setThirtyDayCount(Long thirtyDayCount) {this.thirtyDayCount = thirtyDayCount;}
    
    public List<String> getColumnHeadings() {return columnHeadings;}
    public void setColumnHeadings(List<String> columnHeadings) {this.columnHeadings = columnHeadings;}
    public List<GuestBookResponseDisplay> getGuestBookResponsesDisplay() {return guestBookResponsesDisplay;}
    public void setGuestBookResponsesDisplay(List<GuestBookResponseDisplay> guestBookResponsesDisplay) {this.guestBookResponsesDisplay = guestBookResponsesDisplay;}
    
    public Resource getDownloadCSV() {return new OptionsPage.ExportFileResource("csv");}
    
    private void writeFile(File fileIn, String dataIn, int bufSize) {
        ByteBuffer dataByteBuffer = ByteBuffer.wrap(dataIn.getBytes());
        try {
            FileOutputStream outputFile = new FileOutputStream(fileIn, true);
            WritableByteChannel outChannel = outputFile.getChannel();
            try {
                outChannel.write(dataByteBuffer);
                outputFile.close();
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        } catch (IOException e) {
            throw new EJBException(e);
        }
    }
    
    public void openFile(){}
    
    public class ExportFileResource implements Resource, Serializable{
        File file;
        String fileType;

        public ExportFileResource(String fileType) {
            this.fileType = fileType;
        }

        public String calculateDigest() {
            return file != null ? file.getPath() : null;
        }

        public Date lastModified() {
            return file != null ? new Date(file.lastModified()) : null;
        }

        public InputStream open() throws IOException {
            try {
                file = File.createTempFile("downloadFile","tmp");
            } catch (IOException ioException){               
                 System.out.print("Guestbookresponse open exception: " + ioException);
            }

            writeFile(file, csvString, csvString.length());
            System.out.print("after write file" );
            return new FileInputStream(file);
        }

        public void withOptions(Resource.Options options) throws IOException {
            String filePrefix = "dataDownload_" + new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss").format(new Date());
            options.setFileName(filePrefix + "." + fileType);

        }

        public File getFile() {
            return file;
        }

    }
    
    // Edit Home Panels
    private boolean chkNetworkAnnouncements = false;
    
    public boolean isChkNetworkAnnouncements() {return chkNetworkAnnouncements;}    
    public void setChkNetworkAnnouncements(boolean chkNetworkAnnouncements) {this.chkNetworkAnnouncements = chkNetworkAnnouncements;}
    
    private boolean chkLocalAnnouncements;    
    public boolean isChkLocalAnnouncements() {return chkLocalAnnouncements;}    
    public void setChkLocalAnnouncements(boolean chkLocalAnnouncements) {this.chkLocalAnnouncements = chkLocalAnnouncements;}
    
    private String localAnnouncements;    
    public String getLocalAnnouncements() {return localAnnouncements;}   
    public void setLocalAnnouncements(String localAnnouncements) {this.localAnnouncements = localAnnouncements;}
    
    private boolean chkNewStudies;    
    public boolean isChkNewStudies() {return chkNewStudies;}    
    public void setChkNewStudies(boolean chkNewStudies) {this.chkNewStudies = chkNewStudies;}
    
    //Contact Us Page
    private String contactUsEmail;   
    public String getContactUsEmail() {return this.contactUsEmail;}   
    public void setContactUsEmail(String contactUsEmail) {this.contactUsEmail = contactUsEmail;}
    
    // Edit Study Comments
    
    protected boolean allowStudyComments = true;
    protected HtmlSelectBooleanCheckbox allowStudyCommentsCheckbox = new HtmlSelectBooleanCheckbox();    
    public HtmlSelectBooleanCheckbox getAllowStudyCommentsCheckbox() {return allowStudyCommentsCheckbox;}
    public void setAllowStudyCommentsCheckbox(HtmlSelectBooleanCheckbox allowStudyCommentsCheckbox) {this.allowStudyCommentsCheckbox = allowStudyCommentsCheckbox;}
    public boolean isAllowStudyComments() {return allowStudyComments;}
    public void setAllowStudyComments(boolean allowStudyComments) {this.allowStudyComments = allowStudyComments;}
    
    //Edit Site Page
    private StatusMessage   msg;
    private HtmlInputText          affiliation;
    private HtmlInputText   dataverseAlias;
    private HtmlInputText   dataverseName;
    private HtmlInputTextarea shortDescriptionInput;
    private String          dataverseType = "";
    private String          firstName = new String("");
    private String          lastName;
    private HtmlInputTextarea  shortDescription;    
    private HtmlOutputLabel componentLabel1 = new HtmlOutputLabel();

    public HtmlOutputLabel getComponentLabel1() {return componentLabel1;}
    public void setComponentLabel1(HtmlOutputLabel hol) {this.componentLabel1 = hol;}
    private HtmlOutputText componentLabel1Text = new HtmlOutputText();
    public HtmlOutputText getComponentLabel1Text() {return componentLabel1Text;}
    public void setComponentLabel1Text(HtmlOutputText hot) {this.componentLabel1Text = hot;}
    public HtmlInputText getDataverseName() {return dataverseName;}
    public void setDataverseName(HtmlInputText hit) {this.dataverseName = hit;}
    private HtmlOutputLabel componentLabel2 = new HtmlOutputLabel();
    public HtmlOutputLabel getComponentLabel2() {return componentLabel2;}
    public void setComponentLabel2(HtmlOutputLabel hol) {this.componentLabel2 = hol;}
    private HtmlOutputText componentLabel2Text = new HtmlOutputText();
    public HtmlOutputText getComponentLabel2Text() {return componentLabel2Text;}
    public void setComponentLabel2Text(HtmlOutputText hot) {this.componentLabel2Text = hot;}
    public HtmlInputText getDataverseAlias() {return dataverseAlias;} 
    public HtmlInputTextarea getShortDescription() {return shortDescription;}
    public void setDataverseAlias(HtmlInputText hit) {this.dataverseAlias = hit;}
    private HtmlCommandButton button1 = new HtmlCommandButton();
    public HtmlCommandButton getButton1() {return button1;}
    public void setButton1(HtmlCommandButton hcb) {this.button1 = hcb;}

    private HtmlCommandButton button2 = new HtmlCommandButton();
    public HtmlCommandButton getButton2() {return button2;}
    public void setButton2(HtmlCommandButton hcb) {this.button2 = hcb;}
    public void setMsg(StatusMessage msg){this.msg = msg;}
    public void setDataverseType(String dataverseType) {this.dataverseType = dataverseType;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastname) {this.lastName = lastname;}
    public void setAffiliation(HtmlInputText affiliation) {this.affiliation = affiliation;}
    public void setShortDescription(HtmlInputTextarea shortDescription) {this.shortDescription = shortDescription;}
    public void setShortDescriptionInput(HtmlInputTextarea shortDescriptionInput) {this.shortDescriptionInput = shortDescriptionInput;}

    ClassificationList classificationList =  new ClassificationList();
    public ClassificationList getClassificationList() {return classificationList;}
    public void setClassificationList(ClassificationList classificationList) {this.classificationList = classificationList;}     
    public StatusMessage getMsg(){return msg;}
    public String getDataverseType() {return dataverseType;}
    
    public void validateIsEmpty(FacesContext context, UIComponent toValidate, Object value) {
        String newValue = (String) value;
        if (newValue == null || newValue.trim().length() == 0) {
            FacesMessage message = new FacesMessage("The field must have a value.");
            context.addMessage(toValidate.getClientId(context), message);
            ((UIInput) toValidate).setValid(false);
        }
    }

    public void validateShortDescription(FacesContext context, UIComponent toValidate, Object value) {
        String newValue = (String) value;
        if (newValue != null && newValue.trim().length() > 0) {
            if (newValue.length() > 255) {
                ((UIInput) toValidate).setValid(false);
                FacesMessage message = new FacesMessage("The field cannot be more than 255 characters in length.");
                context.addMessage(toValidate.getClientId(context), message);
            }
        }
        if ((newValue == null || newValue.trim().length() == 0) && getVDCRequestBean().getVdcNetwork().isRequireDVdescription()) {
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage("The field must have a value.");
            context.addMessage(toValidate.getClientId(context), message);
            context.renderResponse();
        }
    }

    public void validateIsEmptyRequiredAffiliation(FacesContext context, UIComponent toValidate, Object value) {
        String newValue = (String) value;
        if ((newValue == null || newValue.trim().length() == 0) && getVDCRequestBean().getVdcNetwork().isRequireDVaffiliation()) {
            FacesMessage message = new FacesMessage("The field must have a value.");
            context.addMessage(toValidate.getClientId(context), message);
            ((UIInput) toValidate).setValid(false);
            context.renderResponse();
        }
    }
    
    public void validateName(FacesContext context, UIComponent toValidate, Object value) {
        String name = (String) value;
        if (name != null && name.trim().length() == 0) {
            FacesMessage message = new FacesMessage("The dataverse name field must have a value.");
            context.addMessage(toValidate.getClientId(context), message);
            ((UIInput)toValidate).setValid(false);
        }
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        if (!name.equals(thisVDC.getName())){
            boolean nameFound = false;
            VDC vdc = vdcService.findByName(name);
            if (vdc != null) {
                nameFound=true;
            }            
            if (nameFound) {
                ((UIInput)toValidate).setValid(false);                
                FacesMessage message = new FacesMessage("This name is already taken.");
                context.addMessage(toValidate.getClientId(context), message);
            }
        }
    }

    public void validateAlias(FacesContext context, UIComponent toValidate, Object value) {
        CharacterValidator charactervalidator = new CharacterValidator();
        charactervalidator.validate(context, toValidate, value);
        String alias = (String) value;
        StringTokenizer strTok = new StringTokenizer(alias);
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        if (!alias.equals(thisVDC.getAlias())) {
            boolean aliasFound = false;
            VDC vdc = vdcService.findByAlias(alias);
            if (vdc != null) {
                aliasFound = true;
            }

            if (aliasFound) {
                ((UIInput) toValidate).setValid(false);
                FacesMessage message = new FacesMessage("This alias is already taken.");
                context.addMessage(toValidate.getClientId(context), message);
            }
        }
    }
    
    public String saveGeneralSettings_action() {
        success = true;
        if (!validateAnnouncementsText()) {
            success = false;
            return "result";
        }

        if (validateClassificationCheckBoxes()) {
            String dataversetype = dataverseType;
            vdc.setDtype(dataversetype);
            vdc.setName((String) dataverseName.getValue());
            vdc.setAlias((String) dataverseAlias.getValue());
            vdc.setAffiliation((String) affiliation.getValue());
            vdc.setDvnDescription((String) shortDescription.getValue());
            if (dataverseType.equals("Scholar")) {
                vdc.setFirstName(this.firstName);
                vdc.setLastName(this.lastName);

            } else {
                vdc.setFirstName(null);
                vdc.setLastName(null);
            }
            saveClassifications(vdc);
            vdc.setDisplayNetworkAnnouncements(chkNetworkAnnouncements);
            vdc.setDisplayAnnouncements(chkLocalAnnouncements);
            vdc.setAnnouncements(localAnnouncements);
            vdc.setDisplayNewStudies(chkNewStudies);
            vdc.setContactEmail(this.getContactUsEmail());
            allowStudyComments = (Boolean)allowStudyCommentsCheckbox.getValue();
            vdc.setAllowStudyComments(allowStudyComments);
            vdcService.edit(vdc);
            getVDCRequestBean().setCurrentVDC(vdc);
            getVDCRenderBean().getFlash().put("successMessage", "Successfully updated general settings.");
            return "/admin/OptionsPage?faces-redirect=true&vdcId=" + vdc.getId();
        } else {
            success = false;
            return null;
        }
    }
     private void saveClassifications(VDC vdc) {
        vdc.getVdcGroups().clear();
        for (ClassificationUI classUI: classificationList.getClassificationUIs()) {
            if (classUI.isSelected()) {
                vdc.getVdcGroups().add(classUI.getVdcGroup());
            }
        }
    }
     
    public boolean validateClassificationCheckBoxes() {
        if (!getVDCRequestBean().getVdcNetwork().isRequireDVclassification()){
            return true;
        }
        else {
            for (ClassificationUI classUI: classificationList.getClassificationUIs()) {
                if (classUI.isSelected()) {
                    return true;
                }
            }
            FacesMessage message = new FacesMessage("You must select at least one classification for your dataverse.");
            FacesContext.getCurrentInstance().addMessage("editsiteform", message);
            return false;
        }
    }
    
    public boolean validateAnnouncementsText() {
        boolean isAnnouncements = true;
        String elementValue = localAnnouncements;
        if ( (elementValue == null || elementValue.equals("")) && (chkLocalAnnouncements) ) {
            isAnnouncements = false;
            success = false;
            FacesMessage message = new FacesMessage("To enable announcements, you must also enter announcements in the field below.  Please enter local announcements as either plain text or html.");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("editHomePanelsForm:localAnnouncements", message);
            context.renderResponse();
        }
        return isAnnouncements;
    }
    
    //Edit Lockss Config page
    private LockssConfig lockssConfig;
    private Long selectOAISetId;
    private Long selectLicenseId;
    private Map<Long, LicenseType> licenseTypes = new HashMap();
    private EditLockssConfigPage.HarvestType selectHarvestType;
    private HtmlSelectOneMenu licenseMenu;
    private HtmlSelectOneMenu oaiSetMenu;
    private HtmlDataTable serverTable;
    
    public List<SelectItem> getSelectOAISets() {
        List selectItems = new ArrayList<SelectItem>();
        selectItems.add(new SelectItem(null, "No Set (All Owned Studies)"));
        for(OAISet oaiSet: oaiService.findAll()) {
            // Don't show OAISets that have been created for dataverse-level Lockss Harvesting
            if (oaiSet.getLockssConfig()==null || oaiSet.getLockssConfig().getVdc()==null) {
                selectItems.add(new SelectItem(oaiSet.getId(), oaiSet.getName()));
            }
        }
        return selectItems;
    }

    public boolean isLicenseRequired() {
        return !selectHarvestType.equals(EditLockssConfigPage.HarvestType.NONE);
    }

    public List<LicenseType> getSelectLicenseTypes() {
        List selectItems = new ArrayList<SelectItem>();
        for(LicenseType licenseType: licenseTypes.values()) {
            selectItems.add(new SelectItem(licenseType.getId(), licenseType.getName()));
        }
        return selectItems;
    }

    private boolean validateLicenseType() {
        boolean valid = true;
        if (!this.selectHarvestType.equals(EditLockssConfigPage.HarvestType.NONE) && selectLicenseId==null) {
            valid=false;
        }       
        if (!valid) {
            ((UIInput) licenseMenu).setValid(false);
            FacesMessage message = new FacesMessage("This field is required.");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(licenseMenu.getClientId(context), message);
        }
        return valid;
    }

    private boolean validateOaiSet() {
        boolean valid = true;
        if (!this.selectHarvestType.equals(EditLockssConfigPage.HarvestType.NONE) && getVDCRequestBean().getCurrentVDC()==null && new Long(-1).equals(this.selectOAISetId)) {
            valid=false;
        }
        if (!valid) {
            ((UIInput) oaiSetMenu).setValid(false);
            FacesMessage message = new FacesMessage("This field is required.");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(oaiSetMenu.getClientId(context), message);
        }
        return valid;
    }
     private boolean validateLockssServers() {
        boolean valid = false;

        if (!selectHarvestType.equals(EditLockssConfigPage.HarvestType.GROUP) && !getLockssConfig().isAllowRestricted()) {
            valid = true;
        } else {
            for (Iterator<LockssServer> it = lockssConfig.getLockssServers().iterator(); it.hasNext();) {
                LockssServer elem = it.next();
                if (elem.getIpAddress() != null && !elem.getIpAddress().trim().isEmpty()) {
                    valid = true;
                }
            }
        }

        if (!valid) {
            String errMessage;
            if (selectHarvestType.equals(EditLockssConfigPage.HarvestType.GROUP) &&!getLockssConfig().isAllowRestricted() ) {
                errMessage = "Please specify servers that are allowed to harvest.";
            } else if (!selectHarvestType.equals(EditLockssConfigPage.HarvestType.GROUP) && getLockssConfig().isAllowRestricted()) {
                errMessage = "Please specify servers that are allowed to access restricted data.";
            } else {
                errMessage = "Please specify servers that are allowed to harvest and access restricted data.";
            }
           
            FacesMessage message = new FacesMessage(errMessage);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(serverTable.getClientId(context), message);            
        }
        return valid;
    }

    public  void validateIpAddress(FacesContext context, UIComponent toValidate, Object value) {
        boolean valid = doValidate(value);
        if (!valid) {
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Invalid IP address or hostname.");
            context.addMessage(toValidate.getClientId(context), message);
        }
    }

    private static boolean doValidate(Object value) {
        boolean valid = false;
        String address = value.toString();
        // first, assume it's a domain name
        if (address.startsWith("*.")) {
            StringBuffer sb = new StringBuffer(address);
            sb.setCharAt(0, 'a');
            address = sb.toString();
        }
        valid = validateDomainName(address);
        if (!valid) {
            // Try to validate it as an ip address
            String ipAddress = value.toString();
            
            // for the purposes of validation, if the string ends in ".*",
            // replace it with dummy data for the validator.
            if (ipAddress.endsWith(".*")) {
                StringBuffer sb = new StringBuffer(ipAddress);
                sb.setCharAt(ipAddress.length() - 1, '1');
                ipAddress = sb.toString();
                // if necessary, add dummy values to the end of the string,
                // so it will pass validation.
                String[] splitStrings = ipAddress.split("\\.");
                if (splitStrings.length==2) {
                    ipAddress+=".1.1";
                } else if (splitStrings.length==3){
                    ipAddress+=".1";
                }
            }
            InetAddressValidator val = InetAddressValidator.getInstance();
            valid = val.isValid(ipAddress);
        }
        return valid;
    }

    /**
     * Cribbed from: http://pappul.blogspot.com/2006/07/validation-of-host-name-in-java.html
     * @param domainName
     * @return
     */
    private  static boolean  validateDomainName(String domainName) {
        if ((domainName == null) || (domainName.length() > 63)) {
            return false;
        }
        String domainIdentifier = "((\\p{Alnum})([-]|(\\p{Alnum}))*(\\p{Alnum}))|(\\p{Alnum})";
        String domainNameRule = "(" + domainIdentifier + ")((\\.)(" + domainIdentifier + "))*";
        String oneAlpha = "(.)*((\\p{Alpha})|[-])(.)*";

        return domainName.matches(domainNameRule) && domainName.matches(oneAlpha);
    }
    
    public String locksSS_save() {          
        boolean validLicenseType = validateLicenseType();
        boolean validOai = validateOaiSet();
        boolean validServers = validateLockssServers();
        if (validLicenseType && validOai && validServers) {
            removeEmptyRows();
            if (selectHarvestType.equals(EditLockssConfigPage.HarvestType.NONE)) {
                editLockssService.removeLockssConfig();
            } else {
                lockssConfig.setserverAccess(LockssConfig.ServerAccess.valueOf(selectHarvestType.toString()));
                lockssConfig.setLicenseType(licenseTypes.get(selectLicenseId));
                editLockssService.saveChanges(selectOAISetId);
            }           
            // refresh currentVDC object
            if (getVDCRequestBean().getCurrentVDC() != null ) {
                if (selectHarvestType.equals(EditLockssConfigPage.HarvestType.NONE)) {
                    getVDCRequestBean().getCurrentVDC().setLockssConfig(null);
                } else {
                    getVDCRequestBean().getCurrentVDC().setLockssConfig(lockssConfig);
                }
            } // network level changes is determined at runtime by db call           
            getVDCRenderBean().getFlash().put("successMessage", "Successfully updated LOCKSS harvest settings.");                     
            return getReturnPage();
        } else {                   
            return "";
        }
    }
    
    public String locksSS_cancel() {
        editLockssService.cancel();
        return getReturnPage();
    }
    
    private String getReturnPage() {
        if (getVDCRequestBean().getCurrentVDC() == null) {
            return "/networkAdmin/NetworkOptionsPage?faces-redirect=true";
        } else {
            return "/admin/OptionsPage?faces-redirect=true&vdcId="+getVDCRequestBean().getCurrentVDC().getId();
        }
    }

    public HtmlSelectOneMenu getLicenseMenu() {return licenseMenu;}
    public void setLicenseMenu(HtmlSelectOneMenu licenseMenu) {this.licenseMenu = licenseMenu;}
    public HtmlSelectOneMenu getOaiSetMenu() {return oaiSetMenu;}
    public void setOaiSetMenu(HtmlSelectOneMenu oaiSetMenu) {this.oaiSetMenu = oaiSetMenu;}
    public HtmlDataTable getServerTable() {return serverTable;}
    public void setServerTable(HtmlDataTable serverTable) {this.serverTable = serverTable;}
    public Long getSelectOAISetId() {return selectOAISetId;}
    public void setSelectOAISetId(Long selectOAISetId) {this.selectOAISetId = selectOAISetId;}
    public LockssConfig getLockssConfig() { return lockssConfig;}
    public void setLockssConfig(LockssConfig lockssConfig) { this.lockssConfig = lockssConfig;}
    public Long getSelectLicenseId() {return selectLicenseId;}
    public void setSelectLicenseId(Long selectLicenseId) {this.selectLicenseId = selectLicenseId;}
    public EditLockssConfigPage.HarvestType getSelectHarvestType() {return selectHarvestType;}
    public void setSelectHarvestType(EditLockssConfigPage.HarvestType selectHarvestType) {this.selectHarvestType = selectHarvestType;}

    private void initCollection() {
        if ( lockssConfig.getLockssServers()==null || lockssConfig.getLockssServers().size()==0) {
            LockssServer elem = new LockssServer();
            elem.setLockssConfig(lockssConfig);
            List<LockssServer> servers = new ArrayList();
            servers.add(elem);
            lockssConfig.setLockssServers(servers);
        }
    }
    
    public void addRow(ActionEvent ae) {
        HtmlDataTable dataTable = (HtmlDataTable) ae.getComponent().getParent().getParent();
        LockssServer newElem = new LockssServer();
        newElem.setLockssConfig(lockssConfig);
        lockssConfig.getLockssServers().add(dataTable.getRowIndex() + 1, newElem);
    }

    public void removeRow(ActionEvent ae) {
        HtmlDataTable dataTable = (HtmlDataTable)ae.getComponent().getParent().getParent();
        if (dataTable.getRowCount()>1) {
            List data = (List)dataTable.getValue();
            editLockssService.removeCollectionElement(data,dataTable.getRowIndex());
        }
    }
     private void removeEmptyRows() {
        for (Iterator<LockssServer> it = lockssConfig.getLockssServers().iterator(); it.hasNext();) {
            LockssServer elem =  it.next();
            if (elem.getIpAddress()!=null && elem.getIpAddress().trim().isEmpty()) {
                  editLockssService.removeCollectionElement(it,elem);
            }
        }
     }
     
     //Edit Harvest Site Page     
    HtmlSelectBooleanCheckbox scheduledCheckbox;
    HtmlSelectOneMenu schedulePeriod;
    String _HARVEST_DTYPE = "Basic";

    public HtmlSelectOneMenu getSchedulePeriod() {return schedulePeriod;}
    public void setSchedulePeriod(HtmlSelectOneMenu schedulePeriod) {this.schedulePeriod = schedulePeriod;}
    private HarvestingDataverse harvestingDataverse;     
    public List getAllowedFileGroups() {return editHarvestSiteService.getAllowedFileGroups();}
    public List getAllowedFileUsers() {return editHarvestSiteService.getAllowedFileUsers();}
   private String validatedServerUrl = null;
    
    public void validateOAIServer(FacesContext context, UIComponent toValidate, Object value) {
        String validationMessage = null;
        String currentServerUrl = ((String) value).trim();

        if (isSaving()) {
            if (validatedServerUrl == null || !validatedServerUrl.equals( currentServerUrl )) {
                validationMessage = "You must first validate the server.";
            }
        } else {
            boolean valid = assignHarvestingSets(currentServerUrl, (String) inputHarvestType.getLocalValue());
            if (valid) {
                assignMetadataFormats(currentServerUrl, (String) inputHarvestType.getLocalValue());
                validatedServerUrl = currentServerUrl;
            } else {
                validationMessage = "Invalid OAI Server Url";
            }
        }
        if (validationMessage != null) {
             ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage(validationMessage);
            context.addMessage(toValidate.getClientId(context), message);
        }
    }

    public void getOAISets(ActionEvent ea) {        }

    private Long harvestId;
    public Long getHarvestId() {return harvestId;}
    public void setHarvestId(Long harvestIdIn) {harvestId = harvestIdIn;}    
    public HarvestingDataverse getHarvestingDataverse() {return harvestingDataverse;}    
    public void setHarvestingDataverse(HarvestingDataverse harvestingDataverse) {this.harvestingDataverse = harvestingDataverse;}   
    public EditHarvestSiteService getEditHarvestSiteService() {return editHarvestSiteService;}    
    public void setEditHarvestSiteService(EditHarvestSiteService editHarvestSiteService) {this.editHarvestSiteService = editHarvestSiteService;}
        
    public String save() {
        Long userId = getVDCSessionBean().getLoginBean().getUser().getId();
        if ( harvestingDataverse.isOai() ) {
            String schedulePeriod=editHarvestSiteService.getHarvestingDataverse().getSchedulePeriod();
            Integer dayOfWeek = editHarvestSiteService.getHarvestingDataverse().getScheduleDayOfWeek();
            Integer hourOfDay = editHarvestSiteService.getHarvestingDataverse().getScheduleHourOfDay();
            if (schedulePeriod!=null && schedulePeriod.equals("notSelected")) {
                editHarvestSiteService.getHarvestingDataverse().setSchedulePeriod(null);
            }
            if  (hourOfDay!=null && hourOfDay.intValue()==-1) {
                 editHarvestSiteService.getHarvestingDataverse().setScheduleHourOfDay(null);
            }
            if  (dayOfWeek!=null && dayOfWeek.intValue()==-1) {
                 editHarvestSiteService.getHarvestingDataverse().setScheduleDayOfWeek(null);
            }     
        } else {
             editHarvestSiteService.getHarvestingDataverse().setScheduled(false);
             editHarvestSiteService.getHarvestingDataverse().setSchedulePeriod(null); 
             editHarvestSiteService.getHarvestingDataverse().setScheduleHourOfDay(null);
             editHarvestSiteService.getHarvestingDataverse().setScheduleDayOfWeek(null);
        }
        
        editHarvestSiteService.save(userId, dataverseNameHarvest, dataverseAliasHarvest, filesRestricted, _HARVEST_DTYPE, dataverseAffiliationHarvest);
        remoteTimerService.updateHarvestTimer(harvestingDataverse);
        
        if (isCreateMode()) {
            getVDCRenderBean().getFlash().put("successMessage","Your new dataverse has been created!");
            return "/site/AddSiteSuccessPage?faces-redirect=true&vdcId=" + editHarvestSiteService.getHarvestingDataverse().getVdc().getId();            
        } else {
            getVDCRenderBean().getFlash().put("successMessage","Successfully updated harvesting settings.");
            return generateReturnPage();
        }
    }
    
    public String cancel() {
        if (isCreateMode()) {
            return "/networkAdmin/NetworkOptionsPage?faces-redirect=true";
        } else {
            return generateReturnPage();
        }
    }
    
    private String generateReturnPage() {
        if (getVDCRequestBean().getCurrentVDCId() != null) {
            return "/admin/OptionsPage?faces-redirect=true&vdcId=" + getVDCRequestBean().getCurrentVDCId();
        } else {
            return "/site/HarvestSitesPage.xhtml?faces-redirect=true";
        }
    }
    
    private String dataverseNameHarvest;
    public String getDataverseNameHarvest() {return this.dataverseNameHarvest;}
    public void setDataverseNameHarvest(String dataverseName) {this.dataverseNameHarvest = dataverseName;}
    private String dataverseAliasHarvest;
    public String getDataverseAliasHarvest() {return this.dataverseAliasHarvest;}
    public void setDataverseAliasHarvest(String dataverseAlias) {this.dataverseAliasHarvest = dataverseAlias;}
    private String dataverseAffiliationHarvest;
    public String getdataverseAffiliationHarvest() {return this.dataverseAffiliationHarvest;}
    public void setdataverseAffiliationHarvest(String dataverseAffiliation) {this.dataverseAffiliationHarvest = dataverseAffiliation;}
    private HtmlDataTable groupTableHarvest;
    public HtmlDataTable getGroupTableHarvest() {return this.groupTableHarvest;}
    public void setGroupTableHarvest(HtmlDataTable groupTable) {this.groupTableHarvest = groupTable;}

    private HtmlDataTable userTableHarvest;
    public HtmlDataTable getUserTableHarvest() {return this.userTableHarvest;}
    public void setUserTableHarvest(HtmlDataTable userTable) {this.userTableHarvest = userTable;}

    private String addUserName;
    public String getAddUserName() {return this.addUserName;}
    public void setAddUserName(String addUserName) {this.addUserName = addUserName;}

    private String addGroupName;
    public String getAddGroupName() {return this.addGroupName;}
    public void setAddGroupName(String addGroupName) {this.addGroupName = addGroupName;}
    
    public void removeGroup(ActionEvent ae) {
        this.editHarvestSiteService.removeAllowedFileGroup(((UserGroup)groupTable.getRowData()).getId());        
    }
    
    public void removeUser(ActionEvent ae) {
        this.editHarvestSiteService.removeAllowedFileUser(((VDCUser)userTable.getRowData()).getId());        
    }
    
    public void addUser(ActionEvent ae) {        
        if (validateUserName(FacesContext.getCurrentInstance(),userInputText, addUserName)) {
            VDCUser   user = userService.findByUserName(addUserName);
            this.editHarvestSiteService.addAllowedFileUser(user.getId());            
            addUserName="";
        }        
    }
       
    public void addGroup(ActionEvent ae) {
        if (validateGroupName(FacesContext.getCurrentInstance(), groupInputText, addGroupName)) {
            UserGroup group = groupService.findByName(addGroupName);
            this.editHarvestSiteService.addAllowedFileGroup(group.getId());
            addGroupName="";
        }        
    }

    private HtmlInputText userInputTextHarvest;
    public HtmlInputText getUserInputTextHarvest() {return this.userInputTextHarvest;}
    public void setUserInputTextHarvest(HtmlInputText userInputText) {this.userInputTextHarvest = userInputText;}

    private HtmlInputText groupInputTextHarvest;
    public HtmlInputText getGroupInputTextHarvest() {return this.groupInputTextHarvest;}
    public void setGroupInputTextHarvest(HtmlInputText groupInputText) {this.groupInputTextHarvest = groupInputText;}
        
    private void assignMetadataFormats(String oaiUrl, String harvestType) {
        if (HarvestingDataverse.HARVEST_TYPE_OAI.equals( harvestType ) ) {
            if (oaiUrl!=null) {          
                editHarvestSiteService.setMetadataFormats(harvesterService.getMetadataFormats(oaiUrl));
            } else {
                editHarvestSiteService.setMetadataFormats(null);
            }
        } else if (HarvestingDataverse.HARVEST_TYPE_NESSTAR.equals( harvestType ) ) {
            List<String> formats = new ArrayList();
            formats.add("ddi");
            editHarvestSiteService.setMetadataFormats(formats);
        }  else {
            editHarvestSiteService.setMetadataFormats(null);
        }
    }

    private boolean assignHarvestingSets(String oaiUrl, String harvestType)   {        
        boolean valid=true;
        if (HarvestingDataverse.HARVEST_TYPE_OAI.equals(harvestType) && oaiUrl!=null) {
            try {     
            editHarvestSiteService.setHarvestingSets(harvesterService.getSets(oaiUrl));
            } catch (EJBException e) {
                valid=false;
            }
        } else {
            editHarvestSiteService.setHarvestingSets(null);
        }
        return valid;
    }
    

    public List<SelectItem> getHarvestingSetsSelect() {
        List<SelectItem> harvestingSetsSelect = new ArrayList<SelectItem>();
        if (this.editHarvestSiteService.getHarvestingSets()!=null) {
            for (Iterator it = this.editHarvestSiteService.getHarvestingSets().iterator(); it.hasNext();) {
                SetDetailBean elem = (SetDetailBean) it.next();
                harvestingSetsSelect.add(new SelectItem(elem.getSpec(),elem.getName()));             
            }
        }
        return harvestingSetsSelect;
    }
    
    public List<SelectItem> getMetadataFormatsSelect() {
        List<SelectItem> metadataFormatsSelect = new ArrayList<SelectItem>();
        if (this.editHarvestSiteService.getMetadataFormats()!=null) {
            for (Iterator it = this.editHarvestSiteService.getMetadataFormats().iterator(); it.hasNext();) {
                String elem = (String) it.next();
                
                HarvestFormatType hft = harvesterService.findHarvestFormatTypeByMetadataPrefix(elem);
                if (hft != null) {        
                    metadataFormatsSelect.add(new SelectItem(hft.getId(),hft.getName()));
                }
            }
        }
        return metadataFormatsSelect;
    }
       
   public List<SelectItem> getHandlePrefixSelect() {
        List<SelectItem> handlePrefixSelect = new ArrayList<SelectItem>();
        List<HandlePrefix> prefixList = handlePrefixService.findAll();
        for (Iterator it = prefixList.iterator(); it.hasNext();) {
            HandlePrefix prefix = (HandlePrefix) it.next();
            handlePrefixSelect.add(new SelectItem(prefix.getId(),"Register harvested studies with prefix "+prefix.getPrefix()));
        }
        
        return handlePrefixSelect;
    }    
   
    private Long handlePrefixId;
    public Long getHandlePrefixId() {
        Long id=null;
        if (harvestingDataverse.getHandlePrefix()!=null) {
            id = harvestingDataverse.getHandlePrefix().getId();
        }
        return id;
    }
    public void setHandlePrefixId(Long handlePrefixId) {
        this.handlePrefixId = handlePrefixId;
    }

    private HtmlSelectOneMenu handlePrefixSelectOneMenu;
    public HtmlSelectOneMenu getHandlePrefixSelectOneMenu() {return this.handlePrefixSelectOneMenu;}
    public void setHandlePrefixSelectOneMenu(HtmlSelectOneMenu handlePrefixSelectOneMenu) {this.handlePrefixSelectOneMenu = handlePrefixSelectOneMenu;}
    public Boolean getSubsetRestrictedWrapper() {return harvestingDataverse.isSubsetRestricted();}

    public void setSubsetRestrictedWrapper(Boolean subsetRestrictedWrapper) {
        if (subsetRestrictedWrapper != null) {
            harvestingDataverse.setSubsetRestricted(subsetRestrictedWrapper);
        } else {
            harvestingDataverse.setSubsetRestricted(false);
        }
    }    
    
    public void validateSchedulePeriod(FacesContext context, UIComponent toValidate, Object value) {        
        boolean valid=true;        
        if (isOai() && scheduledCheckbox.getLocalValue().equals(Boolean.TRUE))
            if ( ((String)value).equals("notSelected")  ) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("This field is required.");
            context.addMessage(toValidate.getClientId(context), message);
        }        
    }

    public void validateHourOfDay(FacesContext context, UIComponent toValidate, Object value) {       
        boolean valid=true;      
         
        if (isOai() && schedulePeriod!=null && schedulePeriod.getLocalValue()!=null &&(schedulePeriod.getLocalValue().equals("daily") || schedulePeriod.getLocalValue().equals("weekly"))) {
            if ( value==null || ((Integer)value).equals(new Integer(-1))) {
                valid=false;
            }
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("This field is required.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }

    public void validateDayOfWeek(FacesContext context, UIComponent toValidate, Object value) {       
        boolean valid=true;      
         
        if (isOai() && schedulePeriod!=null&& schedulePeriod.getLocalValue()!=null && schedulePeriod.getLocalValue().equals("weekly") ) {
               if ( value==null || ((Integer)value).equals(new Integer(-1))) {
                valid=false;
            }
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("This field is required.");
            context.addMessage(toValidate.getClientId(context), message);
        }       
    }

    private boolean isOai() {
        return (inputHarvestType != null && inputHarvestType.getLocalValue().equals("oai"));
    }

    public HtmlSelectBooleanCheckbox getScheduledCheckbox() {return scheduledCheckbox;}
    public void setScheduledCheckbox(HtmlSelectBooleanCheckbox scheduledCheckbox) {this.scheduledCheckbox = scheduledCheckbox;}
    
    private boolean isUpdateMode() {
       return editHarvestSiteService.getEditMode().equals(EditHarvestSiteService.EDIT_MODE_UPDATE);
    }
    
    private boolean isCreateMode() {
       return editHarvestSiteService.getEditMode().equals(EditHarvestSiteService.EDIT_MODE_CREATE);
    }

    public boolean getCreateMode() {
        return isCreateMode();
    }
    
    private HtmlSelectOneRadio inputHarvestType;
    public HtmlSelectOneRadio getInputHarvestType() {return inputHarvestType;}
    public void setInputHarvestType(HtmlSelectOneRadio inputHarvestTypeIn) {inputHarvestType = inputHarvestTypeIn;}
    
    public String getPageTitle() {     
        if (isCreateMode()) {
            return ResourceBundle.getBundle("BundlePageInfo").getString("createHarvestingDvTitle");
        } else {
            return ResourceBundle.getBundle("BundlePageInfo").getString("editHarvestingDvTitle");
        }        
    }

    private HtmlCommandButton saveCommand;
    public HtmlCommandButton getSaveCommand() { return saveCommand;}
    public void setSaveCommand(HtmlCommandButton saveCommand) {this.saveCommand = saveCommand;}

    public boolean isSaving() {
        // check to see if the current request is from the user clicking one of the save buttons
        FacesContext fc = FacesContext.getCurrentInstance();
        Map reqParams = fc.getExternalContext().getRequestParameterMap();
        return reqParams.containsKey(saveCommand.getClientId(fc));
    }
    
    private String defaultSortOrder = "";
    public String getDefaultSortOrder() {return defaultSortOrder;}
    public void setDefaultSortOrder(String defaultSortOrder) {this.defaultSortOrder = defaultSortOrder;}
    
    Collection <StudyField> searchResultsFields;
    private boolean productionDateResults;
    public boolean isProductionDateResults() {return productionDateResults;}
    public void setProductionDateResults(boolean productionDateResults) {this.productionDateResults = productionDateResults;}
    
    private boolean producerResults;    
    public boolean isProducerResults(){return producerResults;}   
    public void setProducerResults(boolean checked){this.producerResults = checked;}
    
    private boolean distributionDateResults;    
    public boolean isDistributionDateResults(){return distributionDateResults;}    
    public void setDistributionDateResults(boolean checked){this.distributionDateResults = checked;}
    
    private boolean distributorResults;   
    public boolean isDistributorResults(){return distributorResults;}  
    public void setDistributorResults(boolean checked){this.distributorResults = checked;}
    
    private boolean replicationForResults;   
    public boolean isReplicationForResults(){return replicationForResults;}    
    public void setReplicationForResults(boolean checked){this.replicationForResults = checked;}
    
    private boolean relatedPublicationsResults;    
    public boolean isRelatedPublicationsResults(){return relatedPublicationsResults;}
    public void setRelatedPublicationsResults(boolean checked){this.relatedPublicationsResults = checked;}
    
    private boolean relatedMaterialResults;    
    public boolean isRelatedMaterialResults(){return relatedMaterialResults;}    
    public void setRelatedMaterialResults(boolean checked){this.relatedMaterialResults = checked;}
    
    private boolean relatedStudiesResults;   
    public boolean isRelatedStudiesResults(){return relatedStudiesResults;}    
    public void setRelatedStudiesResults(boolean checked){this.relatedStudiesResults = checked;}

    public IndexServiceLocal getIndexService() {return indexService;}
    public StudyFieldServiceLocal getStudyFieldService() {return studyFieldService;}
    
    private List <StudyField> getDefaultSearchResultsFields(){
        ArrayList searchResultsFields = new ArrayList();
        List allStudyFields = studyFieldService.findAll();
        for (Iterator it = allStudyFields.iterator(); it.hasNext();) {
            StudyField elem = (StudyField) it.next();
            if (elem.isSearchResultField()){
                searchResultsFields.add(elem);
            }
        }
        return searchResultsFields;        
    }

    public String saveCustomization() {
        String forwardPage;
        vdc.setHeader(banner);
        vdc.setFooter(footer);
        vdc.setDisplayInFrame(displayInFrame);
        vdc.setParentSite(parentSite);
        List <StudyField> newSearchResultsFields = getDefaultSearchResultsFields();
        if (productionDateResults){
            StudyField productionDateResultsField = studyFieldService.findByName("productionDate");
            newSearchResultsFields.add(productionDateResultsField);
        }        
        if (producerResults){
            StudyField producerResultsField = studyFieldService.findByName("producer");
            newSearchResultsFields.add(producerResultsField);
        }
        if (distributionDateResults){
            StudyField distributionDateResultsField = studyFieldService.findByName("distributionDate");
            newSearchResultsFields.add(distributionDateResultsField);
        }
        if (distributorResults){
            StudyField distributorResultsField = studyFieldService.findByName("distributor");
            newSearchResultsFields.add(distributorResultsField);
        }
        if (replicationForResults){
            StudyField replicationResultsField = studyFieldService.findByName("publicationReplicationData");
            newSearchResultsFields.add(replicationResultsField);
        }
        if (relatedPublicationsResults){
            StudyField relatedpubResultsField = studyFieldService.findByName("publication");
            newSearchResultsFields.add(relatedpubResultsField);
        }
        if (relatedMaterialResults){
            StudyField relatedmatResultsField = studyFieldService.findByName("relatedMaterial");
            newSearchResultsFields.add(relatedmatResultsField);
        }
        if (relatedStudiesResults){
            StudyField relatedstudiesResultsField = studyFieldService.findByName("relatedStudies");
            newSearchResultsFields.add(relatedstudiesResultsField);
        }
        if (!newSearchResultsFields.isEmpty()){
            vdc.setSearchResultFields(newSearchResultsFields);
        }
        
        vdc.setDefaultSortOrder(defaultSortOrder);       
        vdcService.edit(vdc);
        forwardPage = "/admin/OptionsPage?faces-redirect=true&vdcId=" + getVDCRequestBean().getCurrentVDC().getId();
        getVDCRenderBean().getFlash().put("successMessage", "Successfully updated customization settings.");
        return forwardPage;
    }

    public String cancelCustomization() {
        return "/admin/OptionsPage?faces-redirect=true&vdcId=" + getVDCRequestBean().getCurrentVDC().getId();
    }
    // Add site page
    public String create() {

    //    Long selectedgroup  = this.getSelectedGroup();
        String dtype        = dataverseType;
        String name         = (String) dataverseName.getValue();
        String alias        = (String) dataverseAlias.getValue();
        String strAffiliation = (String) affiliation.getValue();
        String strShortDescription = (String) shortDescription.getValue();
        Long userId = getVDCSessionBean().getLoginBean().getUser().getId();

        boolean success = true;
        if (validateClassificationCheckBoxes()) {
            vdcService.create(userId, name, alias, dtype);
            VDC createdVDC = vdcService.findByAlias(alias);
            saveClassifications(createdVDC);
            createdVDC.setDtype(dataverseType);
            createdVDC.setDisplayNetworkAnnouncements(getVDCRequestBean().getVdcNetwork().isDisplayAnnouncements());
            createdVDC.setDisplayAnnouncements(getVDCRequestBean().getVdcNetwork().isDisplayVDCAnnouncements());
            createdVDC.setDisplayNewStudies(getVDCRequestBean().getVdcNetwork().isDisplayVDCRecentStudies());
            createdVDC.setAboutThisDataverse(getVDCRequestBean().getVdcNetwork().getDefaultVDCAboutText());
            createdVDC.setContactEmail(getVDCSessionBean().getLoginBean().getUser().getEmail());
            createdVDC.setAffiliation(strAffiliation);
            createdVDC.setDvnDescription(strShortDescription);
            createdVDC.setAnnouncements(strShortDescription); // also set default dv home page description from the the DVN home page short description
            vdcService.edit(createdVDC);

            String hostUrl = PropertyUtil.getHostUrl();
            VDCUser creator = userService.findByUserName(getVDCSessionBean().getLoginBean().getUser().getUserName());
            String toMailAddress = getVDCSessionBean().getLoginBean().getUser().getEmail();
            String siteAddress = hostUrl + "/dvn/dv/" + createdVDC.getAlias();

            mailService.sendAddSiteNotification(toMailAddress, name, siteAddress);

            // Refresh User object in LoginBean so it contains the user's new role of VDC administrator.
            getVDCSessionBean().getLoginBean().setUser(creator);
            getVDCRenderBean().getFlash().put("successMessage","Your new dataverse has been created!");
            return "/site/AddSiteSuccessPage?faces-redirect=true&vdcId=" + createdVDC.getId();
        }
        else {
            success = false;
            return null;
        }

    }
    
    private ArrayList alphaCharacterList;
    private void initAlphabeticFilter() {
        if (alphaCharacterList == null) {
            alphaCharacterList = new ArrayList();
            alphaCharacterList.add(String.valueOf('#')); 
            for ( char ch = 'A';  ch <= 'Z';  ch++ ) {
              alphaCharacterList.add(String.valueOf(ch));
            }
        }
    }

    public ArrayList getAlphaCharacterList() {return this.alphaCharacterList;}
    public void setAlphaCharacterList(ArrayList list) {this.alphaCharacterList = list;}
    private VDCUIList vdcUIList;
    public VDCUIList getVdcUIList() {return this.vdcUIList;}
    private Long vdcUIListSize;   
    private boolean hideRestricted = false;
    private HtmlInputHidden hiddenAlphaCharacter = new HtmlInputHidden();
    public HtmlInputHidden getHiddenAlphaCharacter() {return hiddenAlphaCharacter;}
    public void setHiddenAlphaCharacter(HtmlInputHidden hiddenAlphaCharacter) {this.hiddenAlphaCharacter = hiddenAlphaCharacter;}
    private Long groupId = new Long("-1");
    
    private void populateVDCUIList(boolean isAlphaSort) {
        // new logic for alpha sort
        if (!isAlphaSort) {
            if (vdcUIList == null || (vdcUIList.getAlphaCharacter() != null && ("All".equals((String)hiddenAlphaCharacter.getValue()))) ) {
                vdcUIList = new VDCUIList(groupId, hideRestricted);
                vdcUIList.setAlphaCharacter(new String(""));
                vdcUIList.setSortColumnName(vdcUIList.getDateCreatedColumnName());
           }
        } else {
            if (!((String)hiddenAlphaCharacter.getValue()).equals(vdcUIList.getAlphaCharacter())) {
                vdcUIList = new VDCUIList(groupId, (String)hiddenAlphaCharacter.getValue(), hideRestricted);
                vdcUIList.setAlphaCharacter((String)hiddenAlphaCharacter.getValue());
                vdcUIList.setOldSort(new String(""));
                vdcUIList.setSortColumnName(vdcUIList.getNameColumnName());
            }
        }
        vdcUIList.getVdcUIList();
        vdcUIListSize = new Long(String.valueOf(vdcUIList.getVdcUIList().size()));
    }
//Add Acount PAge
    private VDCUser user;
    public VDCUser getUser() {return this.user;}
    public void setUser(VDCUser user) {this.user = user;}
    private Long studyId;
    public Long getStudyId() {return this.studyId;}
    public void setStudyId(Long studyId) {this.studyId = studyId;}
    private Study study;
    public Study getStudy() {return this.study;}
    public void setStudy(Study study) {this.study = study;}
    HtmlInputSecret inputPassword;
    public HtmlInputSecret getInputPassword() {return inputPassword;}
    public void setInputPassword(HtmlInputSecret inputPassword) {this.inputPassword = inputPassword;}
    public EditUserService getEditUserService() {return editUserService;}
    
    public String createAccount() {
        String workflowValue=null;
        user.setActive(true);
        editUserService.save();
        System.out.print("After save");
        if (StringUtil.isEmpty(workflowValue)) {
            getVDCRenderBean().getFlash().put("successMessage", "User account created successfully." );
        } 
        LoginWorkflowBean loginWorkflowBean = (LoginWorkflowBean)this.getBean("LoginWorkflowBean");   
        System.out.print("After get bean");
        return loginWorkflowBean.processAddAccount(user);     
    }
    
    public String cancelAddAccount() {
        editUserService.cancel();
        // if user is logged in return to the appropriate options page
        // if not logged in, to the appropriate home page
        if (getVDCSessionBean().getLoginBean() != null) {
            if (getVDCRequestBean().getCurrentVDC() != null) {
                return "/admin/OptionsPage?faces-redirect=true" + getNavigationVDCSuffix();
            } else {
                return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true";
            }
        } else {
            return getVDCRequestBean().home();
        }
    }
    
    public void validatePassword(FacesContext context, UIComponent toValidate, Object value) {
        String retypedPassword = (String) value;
        String errorMessage = null;
        
        if (!inputPassword.getLocalValue().equals(retypedPassword)  ) {
            errorMessage = "Passwords do not match.";
        }
               
        if (errorMessage != null) {
            ((UIInput)toValidate).setValid(false);           
            FacesMessage message = new FacesMessage(errorMessage);
            context.addMessage(toValidate.getClientId(context), message);
        }       
    }
}
