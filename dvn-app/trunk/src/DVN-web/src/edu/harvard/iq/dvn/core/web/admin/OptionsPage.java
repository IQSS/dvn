/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.admin;

import com.icesoft.faces.component.datapaginator.DataPaginator;
import com.icesoft.faces.component.ext.*;
import com.icesoft.faces.component.paneltabset.PanelTabSet;
import com.icesoft.faces.context.Resource;
import com.icesoft.faces.context.effects.JavascriptContext;
import edu.harvard.iq.dvn.core.admin.*;
import edu.harvard.iq.dvn.core.gnrs.GNRSServiceLocal;
import edu.harvard.iq.dvn.core.harvest.HarvestFormatType;
import edu.harvard.iq.dvn.core.harvest.HarvestStudyServiceLocal;
import edu.harvard.iq.dvn.core.harvest.HarvesterServiceLocal;
import edu.harvard.iq.dvn.core.harvest.SetDetailBean;
import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.index.Indexer;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.*;
import edu.harvard.iq.dvn.core.util.*;
import edu.harvard.iq.dvn.core.vdc.*;
import edu.harvard.iq.dvn.core.web.VDCUIList;
import edu.harvard.iq.dvn.core.web.collection.CollectionUI;
import edu.harvard.iq.dvn.core.web.common.StatusMessage;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.login.LoginWorkflowBean;
import edu.harvard.iq.dvn.core.web.networkAdmin.AllUsersDataBean;
import edu.harvard.iq.dvn.core.web.networkAdmin.IndexLockFileNameFilter;
import edu.harvard.iq.dvn.core.web.networkAdmin.UserGroupsInfoBean;
import edu.harvard.iq.dvn.core.web.networkAdmin.UtilitiesPage;
import edu.harvard.iq.dvn.core.web.push.beans.NetworkStatsBean;
import edu.harvard.iq.dvn.core.web.site.ClassificationList;
import edu.harvard.iq.dvn.core.web.site.ClassificationUI;
import edu.harvard.iq.dvn.core.web.site.VDCUI;
import edu.harvard.iq.dvn.core.web.study.StudyCommentUI;
import edu.harvard.iq.dvn.core.web.util.CharacterValidator;
import edu.harvard.iq.dvn.core.web.util.ExceptionMessageWriter;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.icefaces.component.fileentry.FileEntry;
import org.icefaces.component.fileentry.FileEntryEvent;
import org.icefaces.component.fileentry.FileEntryResults;
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
    @EJB (name="dvnTimer")DvnTimerRemote remoteTimerService;
    @EJB HarvesterServiceLocal harvesterService;
    @EJB HandlePrefixServiceLocal handlePrefixService;
    @EJB StudyFieldServiceLocal studyFieldService;
    @EJB IndexServiceLocal indexService;
    @EJB EditUserService editUserService;
    @EJB StudyServiceLocal studyService;
    @EJB VDCGroupServiceLocal vdcGroupService;
    @EJB StudyCommentService studyCommentService;
    @EJB HarvestingDataverseServiceLocal harvestingDataverseService;
    @EJB OAISetServiceLocal oaiSetService;    
    @EJB  EditNetworkPrivilegesService privileges;
    @EJB  NetworkRoleServiceLocal networkRoleService;
    @EJB StudyFileServiceLocal studyFileService;
    @EJB HarvestStudyServiceLocal harvestStudyService;
    @EJB GNRSServiceLocal gnrsService;
    
    @PersistenceContext(unitName = "VDCNet-ejbPU")
    EntityManager em;
    
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
        
        if (getVDCRequestBean().getCurrentVDC() != null) {
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
            vdcRoleList.add(new RoleListItem(null, null));
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
               
 
        }
        
        // Home panels
        success = false;
        if (getVDCRequestBean().getCurrentVDC() != null) {
            chkNetworkAnnouncements = getVDCRequestBean().getCurrentVDC().isDisplayNetworkAnnouncements();
            chkLocalAnnouncements = getVDCRequestBean().getCurrentVDC().isDisplayAnnouncements();
            localAnnouncements = getVDCRequestBean().getCurrentVDC().getAnnouncements();
            chkNewStudies = getVDCRequestBean().getCurrentVDC().isDisplayNewStudies();

            //Contact us page
            this.setContactUsEmail(vdc.getContactEmail());

            //Edit Study Comments
            allowStudyComments = vdc.isAllowStudyComments();
            allowStudyCommentsCheckbox.setValue(allowStudyComments);
        } else {
            this.setContactUsEmail(vdcNetworkService.find().getContactEmail());
        }
               
        //edit site page  
        if (getVDCRequestBean().getCurrentVDC() != null) {
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
      if (getVDCRequestBean().getCurrentVDC() != null) {
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
      }
        //Add site page
      if (getVDCRequestBean().getCurrentVDC() == null){
                  HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Iterator iterator = request.getParameterMap().keySet().iterator();
        while (iterator.hasNext()) {
            Object key = (Object) iterator.next();
            if (key instanceof String && ((String) key).indexOf("dataverseType") != -1 && !request.getParameter((String) key).equals("")) {
                this.setDataverseType(request.getParameter((String) key));
            }
        }
        //manage dataverses page
        initAlphabeticFilter();
        populateVDCUIList(false);         
      }
       
        //add account page
        if ( isFromPage("AddAccountPage") ) {
            editUserService = (EditUserService) sessionGet(editUserService.getClass().getName());
            user = editUserService.getUser();            
        } else {
            editUserService.newUser();
            sessionPut( editUserService.getClass().getName(), editUserService);
            //sessionPut( (studyService.getClass().getName() + "."  + studyId.toString()), studyService);
            user = editUserService.getUser();
           HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
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
        
        if (getVDCRequestBean().getCurrentVDC() == null){
            // Manage Controlled Vocab...
            controlledVocabularyList = templateService.getNetworkControlledVocabulary();
            //Manage Classifications
            List list = (List)vdcGroupService.findAll();
            itemBeansSize = list.size();
            initClassifications();
            //Edit export schedule page
            VDCNetwork vdcNetwork = this.getVDCRequestBean().getVdcNetwork();
            exportSchedulePeriod = vdcNetwork.getExportPeriod();
            exportHourOfDay = vdcNetwork.getExportHourOfDay();
            exportDayOfWeek = vdcNetwork.getExportDayOfWeek();

            setSelectExportPeriod(loadSelectExportPeriod());       
            //OAI Sets 
            initSetData();

            //Network Settings
            VDCNetwork thisVdcNetwork = vdcNetworkService.find(new Long(1));
            networkName=thisVdcNetwork.getName();
            this.setChkEnableNetworkAnnouncements(thisVdcNetwork.isDisplayAnnouncements()) ;
            this.setNetworkAnnouncements( thisVdcNetwork.getAnnouncements());  

            //DV requirements page
            setRequireDvaffiliation(thisVdcNetwork.isRequireDVaffiliation());
            setRequireDvclassification(thisVdcNetwork.isRequireDVclassification());
            setRequireDvdescription(thisVdcNetwork.isRequireDVdescription());
            setRequireDvstudiesforrelease(thisVdcNetwork.isRequireDVstudiesforrelease());

            //NetworkPrivileges page

            //Network Terms of Use
            networkAccountTermsOfUse = getVDCRequestBean().getVdcNetwork().getTermsOfUse();
            networkAccountTermsOfUseEnabled = getVDCRequestBean().getVdcNetwork().isTermsOfUseEnabled();
            networkDepositTermsOfUse = getVDCRequestBean().getVdcNetwork().getDepositTermsOfUse();
            networkDepositTermsOfUseEnabled = getVDCRequestBean().getVdcNetwork().isDepositTermsOfUseEnabled();
            networkDownloadTermsOfUse = getVDCRequestBean().getVdcNetwork().getDownloadTermsOfUse();
            networkDownloadTermsOfUseEnabled = getVDCRequestBean().getVdcNetwork().isDownloadTermsOfUseEnabled();
        }

        if (getVDCRequestBean().getCurrentVDC() == null){
            //usergroups page  
            initGroupData();
            //all user page
            //initUserData(); -- moved to click event on the tab
            
        }        
        initSelectedTabIndex();
        //end init
    }
    
    private String tab;
    public String getTab() { return tab;}
    public void setTab(String tab) {this.tab = tab;}
    private String tab2;
    public String getTab2() {return tab2;}
    public void setTab2(String tab2) { this.tab2 = tab2;}
    private int selectedIndex;
    public int getSelectedIndex() {return selectedIndex;}
    public void setSelectedIndex(int selectedIndex) {this.selectedIndex = selectedIndex;}
    private PanelTabSet tabSet1 = new PanelTabSet();
    public PanelTabSet getTabSet1() {return tabSet1;}
    public void setTabSet1(PanelTabSet tabSet1) {this.tabSet1 = tabSet1;}
    private PanelTabSet permissionsSubTab = new PanelTabSet();
    public PanelTabSet getPermissionsSubTab() {return permissionsSubTab;}
    public void setPermissionsSubTab(PanelTabSet permissionsSubTab) {this.permissionsSubTab = permissionsSubTab;}
    private PanelTabSet harvestingSubTab = new PanelTabSet();
    public PanelTabSet getHarvestingSubTab() {return harvestingSubTab;}
    public void setHarvestingSubTab(PanelTabSet harvestingSubTab) {this.harvestingSubTab = harvestingSubTab;}
    private PanelTabSet dvSettingsSubTab = new PanelTabSet();
    public PanelTabSet getDvSettingsSubTab() {return dvSettingsSubTab;}
    public void setDvSettingsSubTab(PanelTabSet dvSettingsSubTab) {this.dvSettingsSubTab = dvSettingsSubTab;}
    private PanelTabSet dvPermissionsSubTab = new PanelTabSet();
    public PanelTabSet getDvPermissionsSubTab() {return dvPermissionsSubTab;}
    public void setDvPermissionsSubTab(PanelTabSet dvPermissionsSubTab) {this.dvPermissionsSubTab = dvPermissionsSubTab;}
    private PanelTabSet networkGeneralSettingsSubTab = new PanelTabSet();
    public PanelTabSet getNetworkGeneralSettingsSubTab() {return networkGeneralSettingsSubTab;}
    public void setNetworkGeneralSettingsSubTab(PanelTabSet networkGeneralSettingsSubTab) {this.networkGeneralSettingsSubTab = networkGeneralSettingsSubTab;}
    
    private void initSelectedTabIndex() {
        
        if (tab == null && getVDCRequestBean().getSelectedTab() != null) {
            tab = getVDCRequestBean().getSelectedTab();
        }
        System.out.print("tab " + tab);
        System.out.print("tab2 " + tab2);
        if (tab != null  && vdc != null) {
            if (tab.equals("studies")) {
                selectedIndex=0;
            } else if (tab.equals("collections")) {
                selectedIndex=1;
            } else if (tab.equals("templates")) {
                selectedIndex=2;
            } else if (tab.equals("permissions")) {
                selectedIndex=3;
                dvPermissionsSubTab.setSelectedIndex(0);
                if (tab2 != null && tab2.equals("tou")){
                   dvPermissionsSubTab.setSelectedIndex(1); 
                }
                if (tab2 != null && tab2.equals("guestbook")){
                   dvPermissionsSubTab.setSelectedIndex(2); 
                }
            } else if (tab.equals("settings")) {
                selectedIndex=4;
                dvSettingsSubTab.setSelectedIndex(0); 
                if (tab2 != null && tab2.equals("customization")){
                   dvSettingsSubTab.setSelectedIndex(2); 
                }
            } 
            tabSet1.setSelectedIndex(selectedIndex);
        }
        if (tab != null  && vdc == null) {
            if (tab.equals("dataverseOptions")) {
                selectedIndex=0;
            } else if (tab.equals("settings")) {
                selectedIndex=1;
                networkGeneralSettingsSubTab.setSelectedIndex(0);
                if (tab2 != null && tab2.equals("advanced")){
                   networkGeneralSettingsSubTab.setSelectedIndex(1); 
                }
            } else if (tab.equals("classifications")) {
                selectedIndex=2;
            } else if (tab.equals("templates")) {
                selectedIndex=3;
            } else if (tab.equals("vocabulary")){
                selectedIndex=4;
            } else if (tab.equals("harvesting")) {
                selectedIndex=5;
                if (tab2 != null && tab2.equals("oaisets")){
                   initUserData();
                   harvestingSubTab.setSelectedIndex(1); 
                }
                if (tab2 != null && tab2.equals("settings")){
                   initUserData();
                   harvestingSubTab.setSelectedIndex(2); 
                }
            } else if (tab.equals("permissions")) {
                initPrivilegedUserData();
                permissionsSubTab.setSelectedIndex(0);
                if (tab2 != null && tab2.equals("users")){
                   initUserData();
                   permissionsSubTab.setSelectedIndex(1); 
                }
                if (tab2 != null && tab2.equals("groups")){
                   permissionsSubTab.setSelectedIndex(2); 
                }
                if (tab2 != null && tab2.equals("tou")){
                   permissionsSubTab.setSelectedIndex(3); 
                }
                selectedIndex=6;
            } else if (tab.equals("utilities")) {
                selectedIndex=7;
            } 
            tabSet1.setSelectedIndex(selectedIndex);
        }
    }
    
    public void updateGuestBookResponses(ValueChangeEvent vce) {
        initGuestBookResponses();
    }
    
       
    public String initGuestBookResponses(){

        //guestbook responses
        Date today = new Date();
        Calendar cal = new GregorianCalendar();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date today30 = cal.getTime();     
        if (getVDCRequestBean().getCurrentVDC() != null) {
            vdcId = getVDCRequestBean().getCurrentVDC().getId();
            if (!show30Days){
                guestBookResponsesAll = guestBookResponseServiceBean.findAllByVdc(vdcId);
            } else {
                guestBookResponsesAll = guestBookResponseServiceBean.findAllWithin30Days();
            }
        } else {
           if (!show30Days){
               guestBookResponsesAll = guestBookResponseServiceBean.findAll(); 
           } else {
               guestBookResponsesAll = guestBookResponseServiceBean.findAllWithin30Days();
           }
           
        } 
        guestBookResponses.clear();
        guestBookResponsesDisplay.clear();
        fullCount = new Long(0);
        thirtyDayCount = new Long(0);
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
        JavascriptContext.addJavascriptCall(getFacesContext(), "updateGuestBookTableSize();");
        return "";
    }
    
    private String twitterVerifier;
    public String getTwitterVerifier() {return twitterVerifier;}
    public void setTwitterVerifier(String twitterVerifier) {this.twitterVerifier = twitterVerifier;}
    
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
    
    public String savePermissionsChanges() {

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
        return "";
        /*return "/admin/OptionsPage?faces-redirect=true&vdcId="+getVDCRequestBean().getCurrentVDC().getId()+ "&tab=permissions";*/
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
        System.out.print(userNameStr);
   
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
            getVDCRenderBean().getFlash().put("successMessage","Successfully updated terms of use for this dataverse.");
            return "/admin/OptionsPage?faces-redirect=true&vdcId="+getVDCRequestBean().getCurrentVDC().getId()+"&tab=permissions&tab2=tou";                      
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
            FacesMessage message = new FacesMessage("To enable this feature, you must also enter terms of use in the field below.  Please enter terms of use as either plain text or html.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(textAreaDownloadUse.getClientId(fc), message);
        }
        elementValue = depositTermsOfUse;
        if ( (elementValue == null || elementValue.equals("")) && (depositTermsOfUseEnabled) ) {
            isUseTerms = false;
            FacesMessage message = new FacesMessage("To enable this feature, you must also enter terms of use in the field below.  Please enter terms of use as either plain text or html.");
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
            vdc.setGuestBookQuestionnaire(guestBookQuestionnaire);
            vdcService.edit(vdc);
            String forwardPage = "/admin/OptionsPage?faces-redirect=true&vdcId=" + getVDCRequestBean().getCurrentVDC().getId() + "&tab=permissions&tab2=guestbook";
            getVDCRenderBean().getFlash().put("successMessage", "Successfully updated guest book questionnaire.");
            return forwardPage;
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
    private boolean show30Days = true;
    public boolean isShow30Days() {return show30Days;}
    public void setShow30Days(boolean show30Days) {this.show30Days = show30Days;}
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
    private HtmlInputText   affiliation;
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
        success = true;
        if (!validateAnnouncementsText()) {
            getVDCRenderBean().getFlash().put("warningMessage", "To enable announcements, you must also enter announcements in the field below.  Please enter local announcements as either plain text or html.)");   
            success = false;
            return "result";
        }
        if (validateClassificationCheckBoxes()) {
            String dataversetype = dataverseType;
            vdc.setDtype(dataversetype);
            vdc.setName((String) dataverseName.getValue());
            vdc.setAlias((String) dataverseAlias.getValue());
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
            return "/admin/OptionsPage?faces-redirect=true&vdcId=" + vdc.getId() + "&tab=settings&tab2=general";
        } else {  
            getVDCRenderBean().getFlash().put("warningMessage", "Could not update general settings.");
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
            context.addMessage("OptionsPage:localAnnouncements", message);
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
    private boolean showEditExportSchedulePage = false;
    public boolean isShowEditExportSchedulePage() {return showEditExportSchedulePage;}
    public void setShowEditExportSchedulePage(boolean showEditExportSchedulePage) {this.showEditExportSchedulePage = showEditExportSchedulePage;}
    
    public void showEditExportSchedulePageAction(ActionEvent event){
            VDCNetwork vdcNetwork = this.getVDCRequestBean().getVdcNetwork();
            exportSchedulePeriod = vdcNetwork.getExportPeriod();
            exportHourOfDay = vdcNetwork.getExportHourOfDay();
            exportDayOfWeek = vdcNetwork.getExportDayOfWeek();

            setSelectExportPeriod(loadSelectExportPeriod());  
        setShowEditExportSchedulePage(true);
    }
    
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
                if(showEditExportSchedulePage){
                    saveExportSchedule();
                }
            } else {
                saveExportSchedule();
            }    
            
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
            return "/networkAdmin/NetworkOptionsPage?faces-redirect=true&tab=harvesting&tab2=settings";
            
        } else {
            return "";
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
    
    private HtmlDataTable harvestDataTable;
    public HtmlDataTable getHarvestDataTable() {return this.harvestDataTable;}
    public void setHarvestDataTable(HtmlDataTable userTable) {this.harvestDataTable = userTable;}
    
   public void doRunNow(ActionEvent ae) {
        HarvestingDataverse hd = (HarvestingDataverse) this.harvestDataTable.getRowData();
        harvesterService.doAsyncHarvest(hd);
        Date previousDate = hd.getLastHarvestTime();
        HarvestingDataverse tempHD = null;
        Date tempDate = null;
        try {
            do {
                Thread.sleep(100);  // sleep for 1/10 second to wait for harvestingNow or lastHarvestDate to be updated
                tempHD = harvestingDataverseService.find(hd.getId());
                tempDate = tempHD.getLastHarvestTime();
            } while (!tempHD.isHarvestingNow() && !isHarvestingDateUpdated(previousDate, tempDate));
        } catch (InterruptedException e) {
        }
        this.harvestSiteList = harvestingDataverseService.findAll();
    }
    public void doSchedule(ActionEvent ae) {
        HarvestingDataverse hd = (HarvestingDataverse) this.harvestDataTable.getRowData();
        hd.setScheduled(true);
        remoteTimerService.updateHarvestTimer(hd);
        harvestingDataverseService.edit(hd);
        // set list to null, to force a fresh retrieval of data
        harvestSiteList=null;
    }

    public void doUnschedule(ActionEvent ae) {
        HarvestingDataverse hd = (HarvestingDataverse) this.harvestDataTable.getRowData();
        hd.setScheduled(false);
        remoteTimerService.updateHarvestTimer(hd);
        harvestingDataverseService.edit(hd);
        // set list to null, to force a fresh retrieval of data
        harvestSiteList=null;
    }
    private boolean isHarvestingDateUpdated(Date previousDate, Date tempDate) {
        boolean isUpdated=false;
        if (previousDate==null) {
            if (tempDate!=null) {
                isUpdated=true;
            }
        } else if (!previousDate.equals(tempDate)){
            isUpdated= true;
        }
        return isUpdated;
    }
    
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
    
    public void addUserDV(ActionEvent ae) {
     
        if (validateUserName(FacesContext.getCurrentInstance(),userInputText, newUserName)) {
            VDCUser user = userService.findByUserName(newUserName);

            VDCRole vdcRole = new VDCRole();
            vdcRole.setVdcUser(user);
            vdcRole.setRole(roleService.findById(newRoleId));
            vdcRole.setVdc(vdc);
            // Add the new vdcRole object to the second position in the display list -
            // the first position stays null to allow for more inserts.
            vdcRoleList.add(1, new OptionsPage.RoleListItem(vdcRole, vdcRole.getRoleId()));
            // Add new vdcRole to the actual list in the vdc object
            vdc.getVdcRoles().add(0,vdcRole);

            // Reset newUserName, to be ready for new input from user
            initUserName();
            // Reset newRoleId, to be ready for new input from user
            newRoleId=null;    
        }        
    }
    
    public void addGroupDV(ActionEvent ae) {
        if (validateGroupName(FacesContext.getCurrentInstance(), groupInputText, groupName)) {
            UserGroup group = groupService.findByName(groupName);
            this.editVDCPrivileges.addAllowedGroup(group.getId());
            groupName="";
        }       
    }
    
    public void removeGroupDV(ActionEvent ae) {
        editVDCPrivileges.removeAllowedGroup(((UserGroup)groupTable.getRowData()).getId());   
    }
    
    public void removeRole(ActionEvent ea) {      
        vdcRoleList.remove(userTable.getRowIndex());
        editVDCPrivileges.removeRole(userTable.getRowIndex()-1);     
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
        getVDCRenderBean().getFlash().put("successMessage", "Successfully updated customization settings.");
        return "";
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
    public Long getVdcUIListSize() {return vdcUIListSize;}
    private Long vdcUnreleased;
    public Long getVdcUnreleased() {return vdcUnreleased;}
    private boolean hideRestricted = false;
    private HtmlInputHidden hiddenAlphaCharacter = new HtmlInputHidden();
    public HtmlInputHidden getHiddenAlphaCharacter() {return hiddenAlphaCharacter;}
    public void setHiddenAlphaCharacter(HtmlInputHidden hiddenAlphaCharacter) {this.hiddenAlphaCharacter = hiddenAlphaCharacter;}
    private Long groupId = new Long("-1");
    
    public void changeAlphaCharacter(ValueChangeEvent event) {
        String newValue = (String)event.getNewValue();
        String oldValue = (String)event.getOldValue();
        if (!newValue.isEmpty()) {
            if (newValue.equals("All")) {
                populateVDCUIList(false);
            } else {
                this.vdcUIList.getPaginator().gotoFirstPage();
                hiddenAlphaCharacter.setValue(newValue);
                populateVDCUIList(true);
            }
        }
    }
    
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
        vdcUnreleased = new Long(0);
        vdcUIListSize = new Long(String.valueOf(vdcUIList.getVdcUIList().size()));
        for (VDCUI vdcUI : vdcUIList.getVdcUIList()){
            if(vdcUI.getVdc().isRestricted()){
                vdcUnreleased++;
            }
        }
    }
    
    public DataPaginator getPaginator() {
        if (this.vdcUIList != null) {
            return this.vdcUIList.getPaginator();
        }
        return null; 
    }
    
    public void setPaginator (DataPaginator paginator) {
       if (this.vdcUIList != null) {
            this.vdcUIList.setPaginator(paginator);
        }
    }
//Add Acount Page
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
        if (StringUtil.isEmpty(workflowValue)) {
            getVDCRenderBean().getFlash().put("successMessage", "User account created successfully." );
        } 
        LoginWorkflowBean loginWorkflowBean = (LoginWorkflowBean)this.getBean("LoginWorkflowBean");   
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
    
    //manage controlled vocab
    private List<ControlledVocabulary> controlledVocabularyList;
    public List<ControlledVocabulary> getControlledVocabularyList() {return controlledVocabularyList;}
    public void setControlledVocabularyList(List<ControlledVocabulary> controlledVocabularyList) {this.controlledVocabularyList = controlledVocabularyList;}  
    
    //manage classifications
    private int itemBeansSize = 0;
    public int getItemBeansSize() {return itemBeansSize;}
    private HtmlGraphicImage toggleImage = new HtmlGraphicImage();
    public HtmlGraphicImage getToggleImage() {return toggleImage;}
    public void setToggleImage(HtmlGraphicImage toggleImage) {this.toggleImage = toggleImage;}
    
    protected void initClassifications() {
         if (list == null) {
             list = new ClassificationList();
         }
         list.getClassificationUIs(new Long("-1"));
     }
     ClassificationList list = null;
     public ClassificationList getList() {return list;}
    public void toggleChildren(javax.faces.event.ActionEvent event) {
        Long parentNodeId = new Long(toggleImage.getAttributes().get("groupingId").toString());
        list.getClassificationUIs(parentNodeId);  
    }
     //Comment review page
     protected List<StudyCommentUI> commentsForReview = null;
     protected Long totalNotifications;
    public Long getTotalNotifications() {return totalNotifications;}
    protected HtmlCommandLink deleteCommentLink;
    public HtmlCommandLink getDeleteCommentLink() {return deleteCommentLink;}
    public void setDeleteCommentLink(HtmlCommandLink deleteCommentLink) {this.deleteCommentLink = deleteCommentLink;}
    protected HtmlCommandLink ignoreCommentFlagLink;
    public HtmlCommandLink getIgnoreCommentFlagLink() {return ignoreCommentFlagLink;}
    public void setIgnoreCommentFlagLink(HtmlCommandLink ignoreCommentFlagLink) {this.ignoreCommentFlagLink = ignoreCommentFlagLink;}
        protected Long flaggedCommentId;
    
     public List<StudyCommentUI> getCommentsForReview() {
        if (commentsForReview == null) {
            commentsForReview = new ArrayList();
            List<StudyComment> tempCommentsForReview = studyCommentService.getAbusiveStudyComments();
            Iterator iterator = tempCommentsForReview.iterator();
            while (iterator.hasNext()) {
                StudyComment studyComment     = (StudyComment)iterator.next();
                StudyCommentUI studyCommentUI = new StudyCommentUI(studyComment);
                commentsForReview.add(studyCommentUI);
            }
            totalNotifications = new Long(Integer.toString(commentsForReview.size()));
        }
        return commentsForReview;
    }
    public void deleteFlaggedComment(ActionEvent event) {
         if (deleteCommentLink.getAttributes().get("commentId") != null) {
             flaggedCommentId = new Long(deleteCommentLink.getAttributes().get("commentId").toString());
         }
         String deletedMessage = "You reported as abusive a comment in the study titled, " +
                                getFlaggedStudyTitle() + ". " + "\n" +
                                "The comment was, \"" + getFlaggedStudyComment() + "\". " + "\n" +
                               "This comment was deleted in accordance with the " +
                                "study comments terms of use.";
         studyCommentService.deleteComment(flaggedCommentId, deletedMessage);
         getVDCRenderBean().getFlash().put("successMessage","Successfully deleted the flagged comment.");
         //cleanup
         flaggedCommentId  = new Long("0");
         commentsForReview = null;
     }

     public void ignoreCommentFlag(ActionEvent event) {
         if (ignoreCommentFlagLink.getAttributes().get("commentId") != null) {
             flaggedCommentId = new Long(ignoreCommentFlagLink.getAttributes().get("commentId").toString());
         }
         String okMessage = "You reported as abusive a comment in the study titled, " +
                                getFlaggedStudyTitle() + ". " + "\n" +
                                "The comment was, \"" + getFlaggedStudyComment() + "\". " + "\n" +
                                "According to the terms of use of this study, the " +
                                "reported comment is not an abuse. This comment will remain posted, and will " +
                                "no longer appear to you as reported.";
         studyCommentService.okComment(flaggedCommentId, okMessage);
         getVDCRenderBean().getFlash().put("successMessage","Successfully ignored the flagged comment.");
         //cleanup
         flaggedCommentId  = new Long("0");
         commentsForReview = null;
     }
     protected String getFlaggedStudyComment() {
         String comment = new String("");
         Iterator iterator = commentsForReview.iterator();
         while (iterator.hasNext()) {
             StudyCommentUI studycommentui = (StudyCommentUI)iterator.next();
             if (studycommentui.getStudyComment().getId().equals(flaggedCommentId)) {
                 comment = studycommentui.getStudyComment().getComment();
                 break;
             }
         }
         return comment;
     }
     protected String getFlaggedStudyTitle() {
         String title = new String("");
         Iterator iterator = commentsForReview.iterator();
         while (iterator.hasNext()) {
             StudyCommentUI studycommentui = (StudyCommentUI)iterator.next();
             if (studycommentui.getStudyComment().getId().equals(flaggedCommentId)) {              
                 title = studycommentui.getStudyComment().getStudyVersion().getStudy().getReleasedVersion().getMetadata().getTitle();
                 break;
             }
         }
         return title;
     }
     
     //harvest sites page
    private List<HarvestingDataverse> harvestSiteList;
    public List<HarvestingDataverse> getHarvestSiteList() {
        if (harvestSiteList==null) {
            harvestSiteList = harvestingDataverseService.findAll();
        }
        return harvestSiteList;
    }
    //Edit Export Schedule page
    HtmlSelectOneMenu exportPeriod;    
    private String exportSchedulePeriod;
    Integer exportDayOfWeek;
    Integer exportHourOfDay;
    private List <SelectItem> selectExportPeriod = new ArrayList();

    public HtmlSelectOneMenu getExportPeriod() {return exportPeriod;}
    public void setExportPeriod(HtmlSelectOneMenu exportPeriod) {this.exportPeriod = exportPeriod;}
    public String getExportSchedulePeriod() {return exportSchedulePeriod;}
    public void setExportSchedulePeriod(String exportSchedulePeriod) {this.exportSchedulePeriod = exportSchedulePeriod;}    
    public Integer getExportDayOfWeek() {return exportDayOfWeek;}
    public void setExportDayOfWeek(Integer exportDayOfWeek) {this.exportDayOfWeek = exportDayOfWeek;}
    public Integer getExportHourOfDay() {return exportHourOfDay;}
    public void setExportHourOfDay(Integer exportHourOfDay) {this.exportHourOfDay = exportHourOfDay;}    
    public void setSelectExportPeriod(List<SelectItem> selectExportPeriod) {this.selectExportPeriod = selectExportPeriod;}
    public List<SelectItem> getSelectExportPeriod() {return selectExportPeriod;}
    private HtmlDataTable dataTable;
    public HtmlDataTable getDataTable() {return this.dataTable;}
    public void setDataTable(HtmlDataTable dataTable) {this.dataTable = dataTable;}
    
    private HtmlDataTable dataTableUserGroups;
    public HtmlDataTable getDataTableUserGroups() {return this.dataTableUserGroups;}
    public void setDataTableUserGroups(HtmlDataTable dataTable) {this.dataTableUserGroups = dataTable;}
    
    public List<SelectItem> loadSelectExportPeriod() {
        List selectItems = new ArrayList<SelectItem>();
        if (this.getVDCRequestBean().getVdcNetwork().getExportPeriod() == null
            || this.getVDCRequestBean().getVdcNetwork().getExportPeriod().equals("")){
            selectItems.add(new SelectItem("", "Not Selected"));
        }
         selectItems.add(new SelectItem("daily", "Export daily"));
         selectItems.add(new SelectItem("weekly", "Export weekly"));
        if ((this.getVDCRequestBean().getVdcNetwork().getExportPeriod() != null)
            && (!this.getVDCRequestBean().getVdcNetwork().getExportPeriod().equals(""))){
            selectItems.add(new SelectItem("none", "Disable export"));
        }
        return selectItems;
    }
    
    public void validateExportHourOfDay(FacesContext context, UIComponent toValidate, Object value) {
        boolean valid = true;
        if (exportPeriod.getLocalValue() != null && (exportPeriod.getLocalValue().equals("daily") || exportPeriod.getLocalValue().equals("weekly"))) {
            if (value == null || ((Integer) value).equals(new Integer(-1))) {
                valid = false;
            }
        }
        if (!valid) {
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage("This field is required.");
            context.addMessage(toValidate.getClientId(context), message);
        }
    }

    public void validateExportDayOfWeek(FacesContext context, UIComponent toValidate, Object value) {
        boolean valid = true;
        if (exportPeriod != null && exportPeriod.getLocalValue() != null && exportPeriod.getLocalValue().equals("weekly")) {
            if (value == null || ((Integer) value).equals(new Integer(-1))) {
                valid = false;
            }
        }
        if (!valid) {
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage("This field is required.");
            context.addMessage(toValidate.getClientId(context), message);
        }
    }

    public void validateExportPeriod(FacesContext context, UIComponent toValidate, Object value) {
        boolean valid = true;
        if (((String) value).equals("notSelected")) {
            exportPeriod.setValue("notSelected");
            valid = false;
        }
        if (!valid) {
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage("This field is required.");
            context.addMessage(toValidate.getClientId(context), message);
        }
    }
    private void saveExportSchedule() {
        VDCNetwork vdcnetwork = getVDCRequestBean().getVdcNetwork();
        vdcnetwork.setExportPeriod(exportSchedulePeriod);
        vdcnetwork.setExportHourOfDay(exportHourOfDay);
        if (exportDayOfWeek != null && exportDayOfWeek.intValue()==-1){
            exportDayOfWeek = null;
        }
        vdcnetwork.setExportDayOfWeek(exportDayOfWeek);
        vdcNetworkService.edit(vdcnetwork);
        remoteTimerService.createExportTimer(vdcnetwork);
        getVDCRenderBean().getFlash().put("successMessage","Successfully updated export schedule.");  
    }
    
    //OAI sets data
    List<OAISet> oaiSets; 
    private HtmlDataTable oaiSetDataTable;
    public HtmlDataTable getOaiSetDataTable() {return this.oaiSetDataTable;}
    public void setOaiSetDataTable(HtmlDataTable dataTable) {this.oaiSetDataTable = dataTable;}
    public  List<OAISet> getOaiSets() { return oaiSets;}
    private void initSetData() {oaiSets = oaiSetService.findAllOrderedSorted();}
    public void deleteSet(ActionEvent ae) {
        OAISet oaiSet=(OAISet)oaiSetDataTable.getRowData();        
        oaiSetService.remove(oaiSet.getId());
        initSetData();  // Re-fetch list to reflect Delete action       
        getVDCRenderBean().getFlash().put("successMessage", "Successfully deleted OAI set.");
    }
    
   public String saveNetworkGeneralSettings_action() {
        VDCNetwork thisVdcNetwork = vdcNetworkService.find(new Long(1));
        thisVdcNetwork.setName((String)textFieldNetworkName.getValue());
        thisVdcNetwork.setContactEmail(this.getContactUsEmail());
        thisVdcNetwork.setDisplayAnnouncements(this.isChkEnableNetworkAnnouncements());
        thisVdcNetwork.setAnnouncements(this.getNetworkAnnouncements());
        vdcNetworkService.edit(thisVdcNetwork);
        getVDCRequestBean().setVdcNetwork(thisVdcNetwork);        
        getVDCRenderBean().getFlash().put("successMessage", "Successfully updated general network settings.");
        return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true&tab=settings&tab2=general";
    }
   
   
   
    String networkName;
    public void setNetworkName(String name){networkName=name;}
    public String getNetworkName(){return networkName;}
    private HtmlInputText textFieldNetworkName = new HtmlInputText();
    public HtmlInputText getTextFieldNetworkName() {return textFieldNetworkName;}
    public void setTextFieldNetworkName(HtmlInputText hit) {this.textFieldNetworkName = hit;}
    private String networkAnnouncements;   
    public String getNetworkAnnouncements() {return networkAnnouncements;}    
    public void setNetworkAnnouncements(String networkAnnouncements) {this.networkAnnouncements = networkAnnouncements;}   
    private boolean chkEnableNetworkAnnouncements;   
    public boolean isChkEnableNetworkAnnouncements() {return chkEnableNetworkAnnouncements;}    
    public void setChkEnableNetworkAnnouncements(boolean chkEnableNetworkAnnouncements) {this.chkEnableNetworkAnnouncements = chkEnableNetworkAnnouncements;}
    
    //Network Customization
    public String saveNetworkCustomization() {
        VDCNetwork thisVdcNetwork = vdcNetworkService.find(new Long(1));
        thisVdcNetwork.setNetworkPageHeader(banner);
        thisVdcNetwork.setNetworkPageFooter(footer);     
        vdcNetworkService.edit(thisVdcNetwork);
        getVDCRenderBean().getFlash().put("successMessage", "Successfully updated network customization.");
        return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true";
    }
    
    //DV Requirements
    private boolean requireDvdescription;
    public boolean isRequireDvdescription() {return requireDvdescription;}
    public void setRequireDvdescription(boolean requireDvdescription) {this.requireDvdescription = requireDvdescription;}
    private boolean requireDvaffiliation;
    public boolean isRequireDvaffiliation() {return requireDvaffiliation;}
    public void setRequireDvaffiliation(boolean requireDvaffiliation) {this.requireDvaffiliation = requireDvaffiliation;}
    private boolean requireDvclassification;
    public boolean isRequireDvclassification() {return requireDvclassification;}
    public void setRequireDvclassification(boolean requireDvclassification) {this.requireDvclassification = requireDvclassification;}
    private boolean requireDvstudiesforrelease;
    public boolean isRequireDvstudiesforrelease() {return requireDvstudiesforrelease;}
    public void setRequireDvstudiesforrelease(boolean requireDvstudiesforrelease) {this.requireDvstudiesforrelease = requireDvstudiesforrelease;}

    public String saveNetworkAdvancedSettings_action() {
            VDCNetwork vdcnetwork = vdcNetworkService.find(new Long(1));
            vdcnetwork.setRequireDVaffiliation(requireDvaffiliation);
            vdcnetwork.setRequireDVclassification(requireDvclassification);
            vdcnetwork.setRequireDVdescription(requireDvdescription);
            vdcnetwork.setRequireDVstudiesforrelease(requireDvstudiesforrelease);
            vdcNetworkService.edit(vdcnetwork);
            getVDCRenderBean().getFlash().put("successMessage", "Successfully updated the network dataverse creation and release requirements.  ");
            return "/networkAdmin/NetworkOptionsPage?faces-redirect=true&tab=settings&tab2=advanced";
    }
    
    //Network Priviledged users page
    public EditNetworkPrivilegesService getPrivileges() {return privileges;}
    public void setPrivileges(EditNetworkPrivilegesService privileges) {this.privileges = privileges;}
    private String userName;
    public String getUserName() {return this.userName;}
    public void setUserName(String userName) {this.userName = userName;}       
    private String TOUuserName;   
    public String getTOUUserName() {return this.TOUuserName;}
    public void setTOUUserName(String name) {this.TOUuserName = name;}
    
    /*
    private UIData userTable;
    
     public javax.faces.component.UIData getUserTable() {
        return userTable;
    }

    public void setUserTable(javax.faces.component.UIData userTable) {
        this.userTable = userTable;
    }
*/
    private UIData TOUuserTable;    
    public UIData getUserTOUTable() {return TOUuserTable;}
    public void setUserTOUTable(javax.faces.component.UIData tut) {this.TOUuserTable = tut;}

    public List getNetworkRoleSelectItems() {
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
    
    public void addNetworkUser(ActionEvent ae) {
        VDCUser user = null;
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
    
    private boolean userNotFound;
    public boolean isUserNotFound() {return userNotFound;}
    public void setUserNotFound(boolean userNotFound) {this.userNotFound = userNotFound;}    
    private boolean TOUuserNotFound;    
    public boolean isTOUUserNotFound() {return TOUuserNotFound; }   
    public void setTOUUserNotFound(boolean userNotFound) {this.TOUuserNotFound = userNotFound; }
    public boolean getDisplayPrivilegedUsers() {return getPrivileges().getPrivilegedUsers().size()>1;}    
    public boolean getDisplayTOUPrivilegedUsers() {return getPrivileges().getTOUPrivilegedUsers().size()>0;}
    public String saveNetworkPrivilegedUsersPage() {
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
        getVDCRenderBean().getFlash().put("successMessage", "Successfully updated network permissions.");
        return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true&tab=permissions";
    }
    
    //NetworkTerms of Use
    private boolean networkAccountTermsOfUseEnabled;
    private String networkAccountTermsOfUse;
    public boolean isNetworkAccountTermsOfUseEnabled() {return networkAccountTermsOfUseEnabled;}
    public void setNetworkAccountTermsOfUseEnabled(boolean termsOfUseEnabled) {this.networkAccountTermsOfUseEnabled = termsOfUseEnabled;}
    public String getNetworkAccountTermsOfUse() {return networkAccountTermsOfUse;}
    public void setNetworkAccountTermsOfUse(String termsOfUse) {this.networkAccountTermsOfUse = termsOfUse;}
    private boolean networkDepositTermsOfUseEnabled;
    private String networkDepositTermsOfUse;
    public boolean isNetworkDepositTermsOfUseEnabled() {return networkDepositTermsOfUseEnabled;}
    public void setNetworkDepositTermsOfUseEnabled(boolean termsOfUseEnabled) {this.networkDepositTermsOfUseEnabled = termsOfUseEnabled;}
    public String getNetworkDepositTermsOfUse() {return networkDepositTermsOfUse;}
    public void setNetworkDepositTermsOfUse(String termsOfUse) {this.networkDepositTermsOfUse = termsOfUse;}
    private boolean networkDownloadTermsOfUseEnabled;
    private String networkDownloadTermsOfUse;
    public boolean isNetworkDownloadTermsOfUseEnabled() {return networkDownloadTermsOfUseEnabled;}
    public void setNetworkDownloadTermsOfUseEnabled(boolean termsOfUseEnabled) {this.networkDownloadTermsOfUseEnabled = termsOfUseEnabled;}
    public String getnetworkDownloadTermsOfUse() {return networkDownloadTermsOfUse;}
    public void setnetworkDownloadTermsOfUse(String termsOfUse) {this.networkDownloadTermsOfUse = termsOfUse;}

    public String save_NetworkTermsOfUseAction() {
        if (validateAccountTerms()) {
            // action code here
            VDCNetwork vdcNetwork = vdcNetworkService.find();
            vdcNetwork.setTermsOfUse(networkAccountTermsOfUse);
            vdcNetwork.setTermsOfUseEnabled(networkAccountTermsOfUseEnabled);
            vdcNetwork.setDepositTermsOfUse(networkDepositTermsOfUse);
            vdcNetwork.setDepositTermsOfUseEnabled(networkDepositTermsOfUseEnabled);
            vdcNetwork.setDownloadTermsOfUse(networkDownloadTermsOfUse);
            vdcNetwork.setDownloadTermsOfUseEnabled(networkDownloadTermsOfUseEnabled);
            vdcNetwork.setTermsOfUseUpdated(new Date());
            vdcNetworkService.edit(vdcNetwork);
            userService.clearAgreedTermsOfUse();
            getVDCRenderBean().getFlash().put("successMessage", "Successfully updated network terms of use.");
            return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true&tab=permissions&tab2=tou";
        } else {
            getVDCRenderBean().getFlash().put("warningMessage", "To enable this terms of use, you must also enter terms of use in the field below. Please enter terms of use as either plain text or html.");
            return null;
        }
    }

    private boolean validateAccountTerms() {
        String elementValue = networkAccountTermsOfUse;
        boolean isUseTerms = true;
        if ((elementValue == null || elementValue.equals("")) && (networkAccountTermsOfUseEnabled)) {
            isUseTerms = false;
            FacesMessage message = new FacesMessage("To enable this feature, you must also enter terms of use in the field below. Please enter terms of use as either plain text or html.");
            FacesContext.getCurrentInstance().addMessage("form1:textArea1", message);
        }
        elementValue = networkDepositTermsOfUse;
        if ((elementValue == null || elementValue.equals("")) && (networkDepositTermsOfUseEnabled)) {
            isUseTerms = false;
            FacesMessage message = new FacesMessage("To enable this feature, you must also enter terms of use in the field below. Please enter terms of use as either plain text or html.");
            FacesContext.getCurrentInstance().addMessage("form1:textArea1", message);
        }
        elementValue = networkDownloadTermsOfUse;
        if ((elementValue == null || elementValue.equals("")) && (networkDownloadTermsOfUseEnabled)) {
            isUseTerms = false;
            FacesMessage message = new FacesMessage("To enable this feature, you must also enter terms of use in the field below. Please enter terms of use as either plain text or html.");
            FacesContext.getCurrentInstance().addMessage("form1:textArea1", message);
        }
        return isUseTerms;
    }    
    
    //Utilities page
    
    private File uploadedDdiFile = null; 
        // <editor-fold defaultstate="collapsed" desc="studyLock utilities">  

    
    String studyLockStudyId;

    public String getStudyLockStudyId() {
        return studyLockStudyId;
    }

    public void setStudyLockStudyId(String studyLockStudyId) {
        this.studyLockStudyId = studyLockStudyId;
    }
    
    public List getStudyLockList() {
        return studyService.getStudyLocks();
    }
    
    public String removeLock_action() {
        try {
            studyService.removeStudyLock( new Long( studyLockStudyId) );
            addMessage( "studyLockMessage", "Study lock removed (for study id = " + studyLockStudyId + ")" );
        } catch (NumberFormatException nfe) {
            addMessage( "studyLockMessage", "Action failed: The study id must be of type Long." );
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "studyLockMessage", "Action failed: An unknown error occurred trying to remove lock for study id = " + studyLockStudyId );
        }
       
        return null;
    }    
    // </editor-fold>        

    

    
    String indexDVId;
    String indexStudyIds;
    private String fixGlobalId; 
    private String studyIdRange;
    private String handleCheckReport; 

    public String getIndexDVId() {
        return indexDVId;
    }

    public void setIndexDVId(String indexDVId) {
        this.indexDVId = indexDVId;
    }

    public String getIndexStudyIds() {
        return indexStudyIds;
    }
    
    public void setIndexStudyIds(String indexStudyIds) {
        this.indexStudyIds = indexStudyIds;
    }
    
    public String getFixGlobalId() {
        return fixGlobalId; 
    }
    
    public void setFixGlobalId(String gid) {
        fixGlobalId = gid; 
    }

    public String getStudyIdRange() {
        return studyIdRange; 
    }
    
    public void setStudyIdRange(String sir) {
        studyIdRange = sir; 
    }
    
    public String getHandleCheckReport() {
        return handleCheckReport; 
    }
    
    public void setHandleCheckReport(String hcr) {
        handleCheckReport = hcr; 
    }

    private boolean deleteLockDisabled;
    
    public String getIndexLocks(){
        String indexLocks = "There is no index lock at this time.";
        deleteLockDisabled = true;
        File lockFile = getLockFile();
        if (lockFile != null) {
            indexLocks = "There has been a lock on the index since " + (new Date(lockFile.lastModified())).toString() + ".";
            deleteLockDisabled = false;
        }
        return indexLocks;
    }

    private File getLockFileFromDir(File lockFileDir) {
        File lockFile=null;
        if (lockFileDir.exists()) {
            File[] locks = lockFileDir.listFiles(new IndexLockFileNameFilter());
            if (locks.length > 0) {
                lockFile = locks[0];
            }
        }
        return lockFile;
    }
    
    private File getLockFile() {
        File lockFile = null;
        File lockFileDir = null;
        String lockDir = System.getProperty("org.apache.lucene.lockDir");
        if (lockDir != null) {
            lockFileDir = new File(lockDir);
            lockFile = getLockFileFromDir(lockFileDir);
        } else {
            lockFileDir = new File(Indexer.getInstance().getIndexDir());
            lockFile = getLockFileFromDir(lockFileDir);
        }
        return lockFile;

    }
    
    public String indexAll_action() {
        try {
            File indexDir = new File( Indexer.getInstance().getIndexDir() );
            if (!indexDir.exists() || indexDir.list().length == 0) {
                indexService.indexAll();
                 addMessage( "indexMessage", "Reindexing completed." );
            } else {
                addMessage( "indexMessage", "Reindexing failed: The index directory must be empty before 'index all' can be run." );
            }
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "indexMessage", "Reindexing failed: An unknown error occurred trying to reindex the DVN." );
        } 
       
        return null;
    }
    
    public String indexDV_action() {
        try {
            VDC vdc =  vdcService.findById( new Long( indexDVId) );
            if (vdc != null) { 
                List studyIDList = new ArrayList();
                for (Study study :  vdc.getOwnedStudies() ) {
                    studyIDList.add( study.getId() );
                }
                indexService.updateIndexList( studyIDList );
                addMessage( "indexMessage", "Indexing completed (for dataverse id = " + indexDVId + ".)" );
            } else {
                addMessage( "indexMessage", "Indexing failed: There is no dataverse with dvId = " + indexDVId + "." );                    
            }
        } catch (NumberFormatException nfe) {
            addMessage( "indexMessage", "Indexing failed: The dataverse id must be of type Long." );
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "indexMessage", "Indexing failed: An unknown error occurred trying to index dataverse with id = " + indexDVId + "." );
        } 
            
        return null;
    }    
    
    public String indexStudies_action() {
        try {
            Map tokenizedLists = determineStudyIds(indexStudyIds);
            indexService.updateIndexList( (List) tokenizedLists.get("idList") );

            addMessage( "indexMessage", "Indexing request completed." );
            addStudyMessages( "indexMessage", tokenizedLists);

        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "indexMessage", "Indexing failed: An unknown error occurred trying to index the following: \"" + indexStudyIds + "\"." );
        }            
 
        return null;
    }   
    
    public String indexLocks_action(){
        File lockFile = getLockFile();
        if (lockFile.exists()){
            if (lockFile.delete()){
                addMessage("indexMessage", "Index lock deleted.");
            } else {
                addMessage("indexMessage", "Index lock could not be deleted.");
            }
        }
        return null;
    }
    
    public String indexBatch_action() {
        try {
            indexService.indexBatch();
            addMessage("indexMessage", "Indexing update completed.");
        } catch (Exception e) {
            e.printStackTrace();
            addMessage("indexMessage", "Indexing failed: An unknown error occurred trying to update the index.");
        }
        return null;
    }
    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="export utilities">    

    String exportFormat;
    String exportDVId;
    String exportStudyIds;
    
    public String getExportFormat() {
        return exportFormat;
    }

    public void setExportFormat(String exportFormat) {
        this.exportFormat = exportFormat == null || exportFormat.equals("") ? null : exportFormat;
    }

    public String getExportDVId() {
        return exportDVId;
    }

    public void setExportDVId(String exportDVId) {
        this.exportDVId = exportDVId;
    }

    public String getExportStudyIds() {
        return exportStudyIds;
    }

    public void setExportStudyIds(String exportStudyIds) {
        this.exportStudyIds = exportStudyIds;
    }
    
     public String exportUpdated_action() {
        try {
            studyService.exportUpdatedStudies();
            addMessage( "exportMessage", "Export succeeded (for updated studies).");
        } catch (Exception e) {
            addMessage( "exportMessage", "Export failed: Exception occurred while exporting studies.  See export log for details.");   
        }
        return null;
    }  
     
     public String updateHarvestStudies_action() {
        try {
            harvestStudyService.updateHarvestStudies();
            addMessage( "exportMessage", "Update Harvest Studies succeeded.");
        } catch (Exception e) {
            addMessage( "exportMessage", "Export failed: An unknown exception occurred while updating harvest studies.");   
        }
        return null;
    }       
     
    public String exportAll_action() {
        try {
            List<Long> allStudyIds = studyService.getAllStudyIds();
            studyService.exportStudies(allStudyIds, exportFormat);
            addMessage( "exportMessage", "Export succeeded for all studies." );

        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "exportMessage", "Export failed: An unknown error occurred trying to export all studies." );
        }    
        
        return null;
    }  
    
    public String exportDV_action() {
        try {
            VDC vdc =  vdcService.findById( new Long(exportDVId) );
            if (vdc != null) { 
                List studyIDList = new ArrayList();
                for (Study study :  vdc.getOwnedStudies() ) {
                    studyIDList.add( study.getId() );
                }

                studyService.exportStudies(studyIDList, exportFormat);
                addMessage( "exportMessage", "Export succeeded (for dataverse id = " + exportDVId + ")" );
            } else {
                addMessage( "exportMessage", "Export failed: There is no dataverse with dvId = " + exportDVId );                    
            }
        } catch (NumberFormatException nfe) {
            addMessage( "exportMessage", "Export failed: The dataverse id must be of type Long.");
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "exportMessage", "Export failed: An unknown error occurred trying to export dataverse with id = " + exportDVId );
        } 
        
        return null;
    }      
    
    public String exportStudies_action() {
        try {
            Map tokenizedLists = determineStudyIds(exportStudyIds);
            studyService.exportStudies( (List) tokenizedLists.get("idList"), exportFormat );
         
            addMessage( "exportMessage", "Export request completed." );
            addStudyMessages( "exportMessage", tokenizedLists);

        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "exportMessage", "Export failed: An unknown error occurred trying to export the following: \"" + exportStudyIds + "\"" );
        }        
        return null;
    }    
    // </editor-fold>        

    
    // <editor-fold defaultstate="collapsed" desc="harvest utilities">  

    Long harvestDVId;
    String harvestIdentifier;

    public Long getHarvestDVId() {
        return harvestDVId;
    }

    public void setHarvestDVId(Long harvestDVId) {
        this.harvestDVId = harvestDVId;
    }

    public String getHarvestIdentifier() {
        return harvestIdentifier;
    }

    public void setHarvestIdentifier(String harvestIdentifier) {
        this.harvestIdentifier = harvestIdentifier;
    }
    
    public List<SelectItem> getHarvestDVs() {
        List harvestDVSelectItems = new ArrayList<SelectItem>();
        Iterator iter = harvestingDataverseService.findAll().iterator();
        while (iter.hasNext()) {
            HarvestingDataverse hd = (HarvestingDataverse) iter.next();
            harvestDVSelectItems.add( new SelectItem(hd.getId(), hd.getVdc().getName()) );
            
        }
        return harvestDVSelectItems;
    }
    
    public String harvestStudy_action() {
        String link = null;
        HarvestingDataverse hd = null;
        try {
            hd = harvestingDataverseService.find( harvestDVId );
            Long studyId = harvesterService.getRecord(hd, harvestIdentifier, hd.getHarvestFormatType().getMetadataPrefix());
            
            if (studyId != null) {
                indexService.updateStudy(studyId);
                
                // create link String
                HttpServletRequest req = (HttpServletRequest) getExternalContext().getRequest();
                link = req.getScheme() +"://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() 
                        + "/dv/" + hd.getVdc().getAlias() + "/faces/study/StudyPage.xhtml?studyId=" + studyId;
            }
                       
            addMessage( "harvestMessage", "Harvest succeeded" + (link == null ? "." : ": " + link ) );
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "harvestMessage", "Harvest failed: An unexpected error occurred trying to get this record." );
            addMessage( "harvestMessage", "Exception message: " + e.getMessage() );
            addMessage( "harvestMessage", "Harvest URL: " + hd.getServerUrl() + "?verb=GetRecord&identifier=" + harvestIdentifier + "&metadataPrefix=" + hd.getHarvestFormatType().getMetadataPrefix() );
        }
       
        return null;
    }    
    // </editor-fold>        

    
    // <editor-fold defaultstate="collapsed" desc="file utilities">    

    
    String fileExtension;
    String fileStudyIds;

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileStudyIds() {
        return fileStudyIds;
    }

    public void setFileStudyIds(String fileStudyIds) {
        this.fileStudyIds = fileStudyIds;
    }
  
    public String determineFileTypeForExtension_action() {
        try {
            List<FileMetadata> fileMetadatas = studyFileService.getStudyFilesByExtension(fileExtension);
            Map<String,Integer> fileTypeCounts = new HashMap<String,Integer>();
            
            for ( FileMetadata fmd : fileMetadatas ) {
                StudyFile sf = fmd.getStudyFile();
                String newFileType = FileUtil.determineFileType( fmd );
                sf.setFileType( newFileType );
                studyFileService.updateStudyFile(sf);
                
                Integer count = fileTypeCounts.get(newFileType);
                if ( fileTypeCounts.containsKey(newFileType)) {
                    fileTypeCounts.put( newFileType, fileTypeCounts.get(newFileType) + 1 );     
                } else {
                    fileTypeCounts.put( newFileType, 1 );    
                }
            }
            
            addMessage( "fileMessage", "Determine File Type request completed for extension ." + fileExtension );
            for (String key : fileTypeCounts.keySet()) {
                addMessage( "fileMessage", fileTypeCounts.get(key) + (fileTypeCounts.get(key) == 1 ? " file" : " files") + " set to type: " + key);                
            }

        
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "fileMessage", "Request failed: An unknown error occurred trying to process the following extension: \"" + fileExtension + "\"" );
        }       

        return null;
    }
    
    public String determineFileTypeForStudies_action() {
        try {
            Map tokenizedLists = determineStudyIds(fileStudyIds);

            for (Iterator<Long>  iter = ((List<Long>) tokenizedLists.get("idList")).iterator(); iter.hasNext();) {
                Long studyId = iter.next();
                Study study = studyService.getStudy(studyId);
                // determine the file type for the latest version of the study
                for ( FileMetadata fmd : study.getLatestVersion().getFileMetadatas() ) {
                    fmd.getStudyFile().setFileType( FileUtil.determineFileType( fmd ) );
                } 
                
                studyService.updateStudy(study);
            }          
        
            addMessage( "fileMessage", "Determine File Type request completed." );
            addStudyMessages("fileMessage", tokenizedLists);
            
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "fileMessage", "Request failed: An unknown error occurred trying to process the following: \"" + fileStudyIds + "\"" );
        }            
 
        return null;
    }   
    // </editor-fold>
    
   
    // <editor-fold defaultstate="collapsed" desc="import utilities">    
 
    
    private Long importDVId;
    private Long importFileFormat;
    private String importBatchDir;
    
    // file upload completed percent (Progress)
    private int fileProgress;
    // render manager for the application, uses session id for on demand
    // render group.
    private String sessionId;
    //L.A.private RenderManager renderManager;
    //L.A.private PersistentFacesState persistentFacesState;
    public static final Log mLog = LogFactory.getLog(UtilitiesPage.class);
    //L.A.private InputFile inputFile; 
    public Long getImportDVId() {
        return importDVId;
    }

    public void setImportDVId(Long importDVId) {
        this.importDVId = importDVId;
    }

    public Long getImportFileFormat() {
        return importFileFormat;
    }

    public void setImportFileFormat(Long importFileFormat) {
        this.importFileFormat = importFileFormat;
    }

    public String getImportBatchDir() {
        return importBatchDir;
    }

    public void setImportBatchDir(String importBatchDir) {
        this.importBatchDir = importBatchDir;
    }
    public int getFileProgress(){
        return fileProgress;
    }
    public void setFileProgress(int p){
        fileProgress=p;
    }  
    //L.A.public InputFile getInputFile(){
    //L.A.    return inputFile;
    //L.A. }
           
    //L.A. public void setInputFile(InputFile in){
    //L.A.    inputFile = in;
    //L.A.}
  
     public List<SelectItem> getImportDVs() {
        List importDVsSelectItems = new ArrayList<SelectItem>();
        Iterator iter = vdcService.findAllNonHarvesting().iterator();
        while (iter.hasNext()) {
            VDC vdc = (VDC) iter.next();
            importDVsSelectItems.add( new SelectItem(vdc.getId(), vdc.getName()) );
            
        }
        return importDVsSelectItems;        
    } 
     
    public List<SelectItem> getImportFileFormatTypes() {
        List<SelectItem> metadataFormatsSelect = new ArrayList<SelectItem>();
                
        for (HarvestFormatType hft : harvesterService.findAllHarvestFormatTypes() ) {
            metadataFormatsSelect.add(new SelectItem(hft.getId(),hft.getName()));
        }

        return metadataFormatsSelect;
    }      
     
    public String importBatch_action() {
        FileHandler logFileHandler = null;
        Logger importLogger = null;
        
        if(importBatchDir==null || importBatchDir.equals("")) return null;
        try {
            int importFailureCount = 0;
            int fileFailureCount = 0;
            List<Long> studiesToIndex = new ArrayList<Long>();
            //sessionId =  ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false)).getId();

            File batchDir = new File(importBatchDir);
            if (batchDir.exists() && batchDir.isDirectory()) {
 
                // create Logger
                String logTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss").format(new Date());
                String dvAlias = vdcService.find(importDVId).getAlias();
                importLogger = Logger.getLogger("edu.harvard.iq.dvn.core.web.networkAdmin.UtilitiesPage." + dvAlias + "_" + logTimestamp);
                String logFileName = FileUtil.getImportFileDir() + File.separator + "batch_" + dvAlias + "_" + logTimestamp + ".log";
                logFileHandler = new FileHandler(logFileName);                
                importLogger.addHandler(logFileHandler ); 
               
                importLogger.info("BEGIN BATCH IMPORT (dvId = " + importDVId + ") from directory: " + importBatchDir);
                
                for (int i=0; i < batchDir.listFiles().length; i++ ) {
                    File studyDir = batchDir.listFiles()[i];
                    if (studyDir.isDirectory()) { // one directory per study
                        importLogger.info("Found study directory: " + studyDir.getName());
                        
                        File xmlFile = null;
                        Map<File,String> filesToUpload = new HashMap();
                        
                        for (int j=0; j < studyDir.listFiles().length; j++ ) {
                            File file = studyDir.listFiles()[j];
                            if ( "study.xml".equals(file.getName()) ) {
                                xmlFile = file;
                            } else {
                                addFile(file, "", filesToUpload);
                            }
                        }
                        
                        if (xmlFile != null) {                                
                            try {
                                importLogger.info("Found study.xml and " + filesToUpload.size() + " other " + (filesToUpload.size() == 1 ? "file." : "files."));
                                // TODO: we need to incorporate the add files step into the same transaction of the import!!!
                                Study study = studyService.importStudy( 
                                        xmlFile, importFileFormat, importDVId, getVDCSessionBean().getLoginBean().getUser().getId());
                                study.getLatestVersion().setVersionNote("Study imported via batch import.");
                                importLogger.info("Import of study.xml succeeded: study id = " + study.getId());
                                studiesToIndex.add(study.getId());
                                
                                if ( !filesToUpload.isEmpty() ) {

                                    List<StudyFileEditBean> fileBeans = new ArrayList();
                                    for (File file : filesToUpload.keySet()) {
                                        StudyFileEditBean fileBean = new StudyFileEditBean( file, studyService.generateFileSystemNameSequence(), study );
                                        fileBean.getFileMetadata().setCategory (filesToUpload.get(file));
                                        fileBeans.add(fileBean);
                                    }

                                    try {
                                        studyFileService.addFiles( study.getLatestVersion(), fileBeans, getVDCSessionBean().getLoginBean().getUser() );
                                        importLogger.info("File upload succeeded.");
                                    } catch (Exception e) {
                                        fileFailureCount++;
                                        importLogger.severe("File Upload failed (dir = " + studyDir.getName() + "): exception message = " + e.getMessage());
                                        logException (e, importLogger);
                                    }
                                }
  
                            } catch (Exception e) {
                                importFailureCount++;
                                importLogger.severe("Import failed (dir = " + studyDir.getName() + "): exception message = " + e.getMessage());
                                logException (e, importLogger);
                            }
                            
                            
                        } else { // no ddi.xml found in studyDir
                            importLogger.warning("No study.xml file was found in study directory. Skipping... ");    
                        }
                    } else {
                        importLogger.warning("Found non directory at top level. Skipping... (filename = " + studyDir.getName() +")");
                    }
                }
                
                // generate status message
                String statusMessage = studiesToIndex.size() + (studiesToIndex.size() == 1 ? " study" : " studies") + " successfully imported";
                statusMessage += (fileFailureCount == 0 ? "" : " (" + fileFailureCount + " of which failed file upload)");
                statusMessage += (importFailureCount == 0 ? "." : "; " + importFailureCount + (importFailureCount == 1 ? " study" : " studies") + " failed import.");                 
                
                importLogger.info("COMPLETED BATCH IMPORT: " + statusMessage );
                
                // now index all studies
                importLogger.info("POST BATCH IMPORT, start calls to index.");
                indexService.updateIndexList(studiesToIndex);
                importLogger.info("POST BATCH IMPORT, calls to index finished.");

                
                addMessage( "importMessage", "Batch Import request completed." );
                addMessage( "importMessage", statusMessage );
                addMessage( "importMessage", "For more detail see log file at: " + logFileName );

            } else {
                addMessage( "importMessage", "Batch Import failed: " + importBatchDir + " does not exist or is not a directory." );    
            }
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "importMessage", "Batch Import failed: An unexpected error occurred during processing." );
            addMessage( "importMessage", "Exception message: " + e.getMessage() );
        } finally {
            if ( logFileHandler != null ) {
                logFileHandler.close();
                importLogger.removeHandler(logFileHandler);
            }
         //   importBatchDir = "";
        }

        return null;       
    }

    private void addFile(File file, String catName, Map<File,String> filesToUpload) throws Exception{
        if ( file.getName()!= null && file.getName().startsWith(".")) {
             // ignore hidden files (ie files that start with "."
        } else if (file.isDirectory()) {
            String tempCatName = StringUtil.isEmpty(catName) ?  file.getName() : catName + " - " + file.getName();
            for (int j=0; j < file.listFiles().length; j++ ) {
                addFile( file.listFiles()[j], tempCatName, filesToUpload );
            }
        } else {
            File tempFile = FileUtil.createTempFile( sessionId, file.getName() );
            FileUtil.copyFile(file, tempFile);
            filesToUpload.put(tempFile, catName);
        }
    }

   /**
     * Return the reference to the
     * {@link com.icesoft.faces.webapp.xmlhttp.PersistentFacesState
     * PersistentFacesState} associated with this Renderable.
     * <p/>
     * The typical (and recommended usage) is to get and hold a reference to the
     * PersistentFacesState in the constructor of your managed bean and return
     * that reference from this method.
     *
     * @return the PersistentFacesState associated with this Renderable
     */
    //L.A.public PersistentFacesState getState() {
    //L.A.    return persistentFacesState;
    //L.A.}

    //L.A.public void setRenderManager(RenderManager renderManager) {
    //L.A.    this.renderManager = renderManager;
    //L.A    renderManager.getOnDemandRenderer(sessionId).add(this);
        
    //L.A.}
  /**
     * Callback method that is called if any exception occurs during an attempt
     * to render this Renderable.
     * <p/>
     * It is up to the application developer to implement appropriate policy
     * when a RenderingException occurs.  Different policies might be
     * appropriate based on the severity of the exception.  For example, if the
     * exception is fatal (the session has expired), no further attempts should
     * be made to render this Renderable and the application may want to remove
     * the Renderable from some or all of the
     * {@link com.icesoft.faces.async.render.GroupAsyncRenderer}s it
     * belongs to. If it is a transient exception (like a client's connection is
     * temporarily unavailable) then the application has the option of removing
     * the Renderable from GroupRenderers or leaving them and allowing another
     * render call to be attempted.
     *
     * @param renderingException The exception that occurred when attempting to
     *                           render this Renderable.
     */
    /*
     Commenting out the entire method -- L.A.
    public void renderingException(RenderingException renderingException) {
        if (mLog.isTraceEnabled() &&
                renderingException instanceof TransientRenderingException) {
            mLog.trace("InputFileController Transient Rendering excpetion:", renderingException);
        } else if (renderingException instanceof FatalRenderingException) {
            if (mLog.isTraceEnabled()) {
                mLog.trace("InputFileController Fatal rendering exception: ", renderingException);
            }
            renderManager.getOnDemandRenderer(sessionId).remove(this);
            renderManager.getOnDemandRenderer(sessionId).dispose();
        }
    }
     L.A. */
   
    /**
     * Dispose callback called due to a view closing or session
     * invalidation/timeout
     */

    public void dispose() throws Exception {
               
       if (mLog.isTraceEnabled()) {
            mLog.trace("OutputProgressController dispose OnDemandRenderer for session: " + sessionId);
        }
        //L.A. renderManager.getOnDemandRenderer(sessionId).remove(this); 
        //L.A. renderManager.getOnDemandRenderer(sessionId).dispose();
    }   
     /**
     * <p>This method is bound to the inputFile component and is executed
     * multiple times during the file upload process.  Every call allows
     * the user to finds out what percentage of the file has been uploaded.
     * This progress information can then be used with a progressBar component
     * for user feedback on the file upload progress. </p>
     *
     * @param event holds a InputFile object in its source which can be probed
     *              for the file upload percentage complete.
     */
    public void fileUploadProgress(EventObject event) {
        //L.A.InputFile ifile = (InputFile) event.getSource();
        //L.A.fileProgress = ifile.getFileInfo().getPercent();
        getImportFileFormat();
        getImportDVId();
        //  System.out.println("sessid "+ sessionId);
        //getImportFileFormat()getImportFileFormat() System.out.println("render "+ renderManager.getOnDemandRenderer(sessionId).toString()); 
        //L.A.if (persistentFacesState !=null) {
        //L.A.    renderManager.getOnDemandRenderer(sessionId).requestRender();} 
    }  
   
    public String importSingleFile_action(){
        //L.A.if(inputFile==null) return null; 
        //L.A.File originalFile = inputFile.getFile();
          
        //File originalFile = null; 
        
        if (uploadedDdiFile != null) {
            
            try {
        
                Study study = studyService.importStudy( 
                    uploadedDdiFile,getImportFileFormat(), getImportDVId(), getVDCSessionBean().getLoginBean().getUser().getId());
                indexService.updateStudy(study.getId());
                // create result message
                HttpServletRequest req = (HttpServletRequest) getExternalContext().getRequest();
                String studyURL = req.getScheme() +"://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() 
                    + "/dv/" + study.getOwner().getAlias() + "/faces/study/StudyPage.xhtml?globalId=" + study.getGlobalId();

                addMessage( "importMessage", "Import succeeded." );
                addMessage( "importMessage", "Study URL: " + studyURL );

            }catch(Exception e) {
                e.printStackTrace();
                addMessage( "harvestMessage", "Import failed: An unexpected error occurred trying to import this study." );
                addMessage( "harvestMessage", "Exception message: " + e.getMessage() );            
            }
        }
                
        return null;  
    }
        
   public String uploadFile() {
     
       //L.A. inputFile =getInputFile();  
       
        String str="";
   
        
        /* Commenting out everything InputFile-related: -- L.A. 
	if (inputFile.getStatus() != InputFile.SAVED){
            str = "File " + inputFile.getFileInfo().getFileName()+ " has not been saved. \n"+
                    "Status: "+ inputFile.getStatus();
            logger.info(str); 
            addMessage("importMessage",str);
           if(inputFile.getStatus() != InputFile.INVALID) 
            return null;
          }
         -- L.A. */
       return null; 
   }
   
   
    public void uploadFileListener(FileEntryEvent fileEvent) {
        
        File uploadedFile = null; 
        
        FileEntry fe = (FileEntry)fileEvent.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        StringBuilder m = null;

        for (FileEntryResults.FileInfo i : results.getFiles()) {
            //Note that the fileentry component has capabilities for 
            //simultaneous uploads of multiple files.
            
            uploadedFile = i.getFile(); 
        }                                                          

        uploadedDdiFile = uploadedFile; 
    }
  
// </editor-fold>

    
    // <editor-fold defaultstate="collapsed" desc="delete utilities">    

    
    String deleteStudyIds;

    public String getDeleteStudyIds() {
        return deleteStudyIds;
    }

    public void setDeleteStudyIds(String deleteStudyIds) {
        this.deleteStudyIds = deleteStudyIds;
    }

  
    
    public String deleteStudies_action() {
        try {
            Map tokenizedLists = determineStudyIds(deleteStudyIds);
            studyService.deleteStudyList( (List) tokenizedLists.get("idList") );

            addMessage( "deleteMessage", "Delete request completed." );
            addStudyMessages( "deleteMessage", tokenizedLists);

        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "deleteMessage", "Delete failed: An unknown error occurred trying to delete the following: \"" + deleteStudyIds + "\"" );
        }            
 
        return null;
    }   
    

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="handle utilities">


       public String handleRegisterAll_action() {
        try {
            gnrsService.registerAll();

            addMessage( "handleMessage", "Handle registration request completed." );

        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "handleMessage", "Handle registration failed: An unknown error occurred trying to index the following: \"" + indexStudyIds + "\"" );
        }

        return null;
    }


    
        public String handleFixAll_action() {
        try {
            gnrsService.fixAll();

            addMessage( "handleMessage", "Handle re-registration request completed." );

        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "handleMessage", "Handle registration failed: An unknown error occurred trying to index the following: \"" + indexStudyIds + "\"" );
        }

        return null;
    }
        
    public String handleFixSingle_action() {
        // TODO: also need a method to do this on a range of study IDs (?)
        String hdl = fixGlobalId; 
        
        hdl = hdl.replaceFirst("^hdl:", "");
        
        try {
            gnrsService.fixHandle(hdl);

            addMessage( "handleMessage", "Re-registered handle "+hdl );

        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "handleMessage", "Handle (re)registration failed for \"" + hdl + "\"" );
        }

        return null;
    }
    
    
    public String handleCheckRange_action() {
        handleCheckReport = null; 
        
        if (studyIdRange == null || 
                !(studyIdRange.matches("^[0-9][0-9]*$") || 
                studyIdRange.matches("^[0-9][0-9]*\\-[0-9][0-9]*$"))) {
            addMessage("handleMessage", "Invalid study ID range!");
            return null; 
            
        }
        
        String checkOutput = ""; 
        
        if (studyIdRange.indexOf('-') > 0) {
            // range: 
            Long idStart = null; 
            Long idEnd = null; 
            
            try {
                String rangeStart = studyIdRange.substring(0, studyIdRange.indexOf('-'));
                String rangeEnd = studyIdRange.substring(studyIdRange.indexOf('-')+1);
 
                idStart = new Long (rangeStart);
                idEnd = new Long (rangeEnd); 
            } catch (Exception ex) {
                addMessage("handleMessage", "Invalid study ID range: "+studyIdRange);
                return null; 
            }
                
            if (!(idStart.compareTo(idEnd) < 0)) {
                addMessage("handleMessage", "Invalid numeric range: " + studyIdRange);
                return null;
            }

            Long studyId = idStart;

            while (studyId.compareTo(idEnd) <= 0) {
                try {
                    Study chkStudy = studyService.getStudy(studyId);
                    String chkHandle = chkStudy.getAuthority() + "/" + chkStudy.getStudyId();
                    if (gnrsService.isHandleRegistered(chkHandle)) {
                        checkOutput = checkOutput.concat(studyId + "\thdl:" + chkHandle + "\tok\n");
                    } else {
                        checkOutput = checkOutput.concat(studyId + "\thdl:" + chkHandle + "\tNOT REGISTERED\n");
                    }
                    
                } catch (Exception ex) {
                    checkOutput = checkOutput.concat(studyId + "\t\tNO SUCH STUDY\n");
                }
                studyId = studyId + 1;
            }
            //addMessage("handleMessage", checkOutput);
            handleCheckReport = checkOutput; 

            
            
        } else {
            // single id: 
            try {
                Long studyId = new Long (studyIdRange);
                Study chkStudy = studyService.getStudy(studyId);
                String chkHandle = chkStudy.getAuthority() + "/" + chkStudy.getStudyId();
                if (gnrsService.isHandleRegistered(chkHandle)) {
                    checkOutput = studyId + "\thdl:" + chkHandle + "\t\tok\n";
                } else {
                    checkOutput = studyId + "\thdl:" + chkHandle + "\t\tNOT REGISTERED\n";
                }

                //addMessage("handleMessage", checkOutput);
                handleCheckReport = checkOutput;
                
            } catch (Exception ex) {
                addMessage("handleMessage", "No such study: id="+studyIdRange);
            }
        }
        return null; 
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="study utilities">


    String createStudyDraftIds;

    public String getCreateStudyDraftIds() {
        return createStudyDraftIds;
    }

    public void setCreateStudyDraftIds(String createStudyDraftIds) {
        this.createStudyDraftIds = createStudyDraftIds;
    }





    public String createStudyDrafts_action() {
        try {
            Map tokenizedLists = determineStudyIds(createStudyDraftIds);
            List ignoredList = new ArrayList();

            for (Iterator it = ((List) tokenizedLists.get("idList")).iterator(); it.hasNext();) {
                Long studyId = (Long) it.next();
                Study study = studyService.getStudy(studyId);
                Long currentVersionNumber = study.getLatestVersion().getVersionNumber();
                StudyVersion editVersion = study.getEditVersion();
                if ( currentVersionNumber.equals(editVersion.getVersionNumber()) ){
                    // working copy already exists
                    it.remove();
                    ignoredList.add(studyId);
                } else {
                    // save new version
                    studyService.saveStudyVersion(editVersion, getVDCSessionBean().getLoginBean().getUser().getId());
                    studyService.updateStudyVersion(editVersion);
                }
            }

            tokenizedLists.put("ignoredList", ignoredList );
            tokenizedLists.put("ignoredReason", "working verison already exists" );

            addMessage( "studyMessage", "Create Study Draft request completed." );
            addStudyMessages( "studyMessage", tokenizedLists);

        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "studyMessage", "Create Drafts failed: An unknown error occurred trying to delete the following: \"" + deleteStudyIds + "\"" );
        }

        return null;
    }


    // </editor-fold>

    // ****************************
    // Common methods
    // ****************************
    
    private void addMessage(String component, String message) {
        FacesMessage facesMsg = new FacesMessage(message);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(component, facesMsg);          
    }
    
    private void addStudyMessages (String component, Map tokenizedLists) {

            if ( tokenizedLists.get("idList") != null && !((List) tokenizedLists.get("idList")).isEmpty() ) {            
                addMessage( component, "The following studies were successfully processed: " + tokenizedLists.get("idList") + "." );
            }
            if ( tokenizedLists.get("ignoredList") != null && !((List) tokenizedLists.get("ignoredList")).isEmpty() ) {            
                addMessage( component, "The following studies were ignored (" 
                        + ((String) tokenizedLists.get("ignoredReason")) + "): " + tokenizedLists.get("ignoredList") + "." );
            }            
            if ( tokenizedLists.get("invalidStudyIdList") != null && !((List) tokenizedLists.get("invalidStudyIdList")).isEmpty() ) {
                addMessage( component, "The following study ids were invalid: " + tokenizedLists.get("invalidStudyIdList") + "." ); 
            }
            if ( tokenizedLists.get("failedTokenList") != null && !((List) tokenizedLists.get("failedTokenList")).isEmpty() ) {
                addMessage( component, "The following tokens could not be interpreted: " + tokenizedLists.get("failedTokenList") + "." );
            }
    }

    
    private Map determineIds(String ids) {
        List<Long> idList = new ArrayList();
        List<String> failedTokenList = new ArrayList(); 
                    
        StringTokenizer st = new StringTokenizer(ids, ",; \t\n\r\f");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            
            try {
                idList.add( new Long(token) );
            } catch (NumberFormatException nfe) {
                if ( token.indexOf("-") == -1 ) {
                    failedTokenList.add(token);
                } else {
                    try {
                        Long startId = new Long( token.substring( 0, token.indexOf("-") ) );
                        Long endId = new Long( token.substring( token.indexOf("-") + 1 ) );  
                        for (long i = startId.longValue(); i <= endId.longValue(); i++) {
                            idList.add( new Long(i) );
                        }
                    } catch (NumberFormatException nfe2) {
                        failedTokenList.add( token );
                    }
                }
            }
        }
        
        Map returnMap = new HashMap();
        returnMap.put("idList", idList);
        returnMap.put("failedTokenList", failedTokenList);
        
        return returnMap;
    }   


    private Map determineStudyIds(String studyIds) {
        Map tokenizedLists = determineIds(studyIds);
        List invalidStudyIdList = new ArrayList();

        for (Iterator<Long>  iter = ((List<Long>) tokenizedLists.get("idList")).iterator(); iter.hasNext();) {
            Long id = iter.next();
            try {
                studyService.getStudy(id);
            } catch (EJBException e) {  
                if (e.getCause() instanceof IllegalArgumentException) {
                    invalidStudyIdList.add(id);
                    iter.remove();
                } else {
                    throw e;
                }
            }  
        }

        tokenizedLists.put("invalidStudyIdList", invalidStudyIdList);
        return tokenizedLists;
    }
    
    // duplicate from harvester
    private void logException(Throwable e, Logger logger) {

        boolean cause = false;
        String fullMessage = "";
        do {
            String message = e.getClass().getName() + " " + e.getMessage();
            if (cause) {
                message = "\nCaused By Exception.................... " + e.getClass().getName() + " " + e.getMessage();
            }
            StackTraceElement[] ste = e.getStackTrace();
            message += "\nStackTrace: \n";
            for (int m = 0; m < ste.length; m++) {
                message += ste[m].toString() + "\n";
            }
            fullMessage += message;
            cause = true;
        } while ((e = e.getCause()) != null);
        logger.severe(fullMessage);
    }

    public boolean isDeleteLockDisabled() {
        return deleteLockDisabled;
    }

    public void setDeleteLockDisabled(boolean deleteLockDisabled) {
        this.deleteLockDisabled = deleteLockDisabled;
    }
    
    // user groups page
    List<UserGroupsInfoBean> groups; 
    public  List<UserGroupsInfoBean> getGroups() {return groups;}
    
    private void initGroupData() {
        groups = new ArrayList<UserGroupsInfoBean>();
        List<UserGroup> userGroups = groupService.findAll();
        for (Iterator it = userGroups.iterator(); it.hasNext();) {
            UserGroup elem = (UserGroup) it.next();
            groups.add(new UserGroupsInfoBean(elem));
        }      
    }
    
    public void deleteGroup(ActionEvent ae) {
        UserGroupsInfoBean bean=(UserGroupsInfoBean)dataTableUserGroups.getRowData();
        UserGroup userGroup = bean.getGroup();
        groupService.remove(userGroup.getId());
        initGroupData();  // Re-fetch list to reflect Delete action       
    }
    
    public void initUserData() {        
        userData = new ArrayList();
        List users = userService.findAll();
        for (Iterator it = users.iterator(); it.hasNext();) {
            VDCUser elem = (VDCUser) it.next();
            boolean defaultNetworkAdmin = elem.getNetworkRole()!=null
                    && elem.getId().equals(vdcNetworkService.find().getDefaultNetworkAdmin().getId());
            userData.add(new AllUsersDataBean(elem, defaultNetworkAdmin));
        }
    }
    
    public void initPrivilegedUserData() {        
            privileges.init();
            sessionPut( getPrivileges().getClass().getName(),privileges);
    }
    
    private List<edu.harvard.iq.dvn.core.web.networkAdmin.AllUsersDataBean> userData;
    public List<edu.harvard.iq.dvn.core.web.networkAdmin.AllUsersDataBean> getUserData() {return this.userData;}
    public void setUserData(List<edu.harvard.iq.dvn.core.web.networkAdmin.AllUsersDataBean> userData) {this.userData = userData;}
    public void activateUser(ActionEvent ae) {
        AllUsersDataBean bean=(AllUsersDataBean)dataTable.getRowData();
        VDCUser user = bean.getUser();
        userService.setActiveStatus(bean.getUser().getId(),true);
        initUserData();  // Re-fetch list to reflect Delete action       
    }
    
     public void deactivateUser(ActionEvent ae) {
        AllUsersDataBean bean=(AllUsersDataBean)dataTable.getRowData();
        userService.setActiveStatus(bean.getUser().getId(),false);
        initUserData();  // Re-fetch list to reflect Delete action        
    }
     
}    
