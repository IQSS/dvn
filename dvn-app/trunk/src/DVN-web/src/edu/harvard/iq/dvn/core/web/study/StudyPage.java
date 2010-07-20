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
 * StudyPage.java
 *
 * Created on September 19, 2006, 2:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.study;

import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.paneltabset.PanelTabSet;
import com.icesoft.faces.component.paneltabset.TabChangeEvent;
import com.icesoft.faces.context.Resource;
import com.icesoft.faces.context.Resource.Options;
import com.icesoft.faces.context.effects.JavascriptContext;
import edu.harvard.iq.dvn.core.study.DataFileFormatType;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.util.PropertyUtil;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.util.WebStatisticsSupport;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.login.LoginWorkflowBean;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyPage extends VDCBaseBean implements java.io.Serializable  {
    private static Logger dbgLog = Logger.getLogger(StudyPage.class.getCanonicalName());
    @EJB private StudyServiceLocal studyService;


    public StudyPage() {
    }

    // params
    private String globalId;
    private Long studyId;
    private Long versionNumber;

    private boolean studyUIContainsFileDetails=false; // TODO: needed??
    private int selectedIndex;

    private VersionNotesPopupBean versionNotesPopup;

    public VersionNotesPopupBean getVersionNotesPopup() {
        return versionNotesPopup;
    }

    public void setVersionNotesPopup(VersionNotesPopupBean versionNotesPopup) {
        this.versionNotesPopup = versionNotesPopup;
    }

    

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;

    }

    public void init() {
        super.init();
        //TODO: see if this can be removed and handled through regular tab handling
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        if (request.getHeader("referer") != null && ((String) request.getHeader("referer")).indexOf("CommentReview") != -1) {
            setTab("comments");
        }
        // set tab if it was it was sent as pamameter or part of request bean
        initSelectedTabIndex();
        
        versionNotesPopup.setActionType(VersionNotesPopupBean.ActionType.EDIT_NOTE);

        // If we're coming from EditStudyPage
        if (studyId == null) {
            studyId = getVDCRequestBean().getStudyId();
        }
        if (versionNumber == null) {
            versionNumber = getVDCRequestBean().getStudyVersionNumber();
        }

        // No versionNumber?
        // What do we do, default to the latest?
        // Yes, to the latest released, if available.
        // (that happens inside getStudyVersion)

        // first determine study, via gloablId or studyId param
        if (globalId != null || studyId != null) {
            StudyVersion sv = null;

            if (globalId != null) {
                sv = studyService.getStudyVersion(globalId, versionNumber);
                studyId = sv.getStudy().getId();
                getVDCRequestBean().setStudyId(studyId);
            } else {
                sv = studyService.getStudyVersion(studyId, versionNumber);
            }

            if (sv == null) {
                redirect("/faces/IdDoesNotExistPage.xhtml?type=Study%20Version");
                return;
            
            } else if ( sv.isDeaccessioned() && versionNumber == null ) {
                deaccessionedView = true;
                studyUI = new StudyUI(sv, getVDCSessionBean().getUser()); // TODO: could this be simpler
            
            } else {
                initPage(sv);
            }

            allowStudyComments = studyUI.getStudy().getOwner().isAllowStudyComments();

            /* TODO: do we need this code still for closing all file categories?
            // flag added to start with all file categories closed
            if (getRequestParam("renderFiles") != null && getRequestParam("renderFiles").equals("false")) {
                for (FileCategoryUI catUI : studyUI.getCategoryUIList()) {
                    catUI.setRendered(false);
                }
            }
            */
            if ( globalId == null  ){
                globalId = studyUI.getStudy().getGlobalId();
            }

        } else {
            // WE SHOULD HAVE A STUDY ID, throw an error
            System.out.println("ERROR: in StudyPage, without a globalId or a studyId");
        }
    }

    /**
     *  Get the tab name from the request parameter or
     *  the VDCRequestBean, and set the selected index based on the
     *  tab name.
     */
    private void initSelectedTabIndex() {
        if (tab == null && getVDCRequestBean().getSelectedTab() != null) {
            tab = getVDCRequestBean().getSelectedTab();
        }
        if (tab != null) {
            if (tab.equals("catalog")) {
                selectedIndex=0;
            } else if (tab.equals("files")) {
                selectedIndex=1;
            } else if (tab.equals("comments")) {
                selectedIndex=2;
            } else if (tab.equals("versions")) {
                selectedIndex=3;
            } 

            tabSet1.setSelectedIndex(selectedIndex);
        }
    }

    private void initPage(StudyVersion sv) {
        if (tabSet1.getSelectedIndex()==1) {
            initStudyUIWithFiles(sv);
        } else {
            studyUI = new StudyUI(sv, getVDCSessionBean().getUser());
            sessionPut(studyUI.getClass().getName(), studyUI);
            initPanelDisplay();
        }
    }
    // TODO: consolidate logic into previous method
    public void processTabChange(TabChangeEvent tabChangeEvent) throws AbortProcessingException {

        // If user clicks on the catalog tab, reset the open/closed settings for each section
        if (tabChangeEvent.getNewTabIndex()==0) {
            initPanelDisplay();
        }
        // If the user clicks on the files tab,
        // make sure the StudyUI object contains file details.
        if ( tabChangeEvent.getNewTabIndex()==1) {
            initStudyUIWithFiles(studyUI.getStudyVersion());
        }
    }

    private void initStudyUIWithFiles(StudyVersion studyVersion) {
          if (!studyUIContainsFileDetails) {
             studyUI = new StudyUI(
                            studyVersion,
                            getVDCRequestBean().getCurrentVDC(),
                            getVDCSessionBean().getLoginBean() != null ? this.getVDCSessionBean().getLoginBean().getUser() : null,
                            getVDCSessionBean().getIpUserGroup());
             studyUIContainsFileDetails=true;
          }
    }

    private PanelTabSet tabSet1 = new PanelTabSet();

    public PanelTabSet getTabSet1() {
        return tabSet1;
    }

    public void setTabSet1(PanelTabSet tabSet1) {
        this.tabSet1 = tabSet1;
    }


    public String getCitationDate() {
        String str = "";
        if (getStudyUI().getMetadata().getProductionDate() != null) {
            str = getStudyUI().getMetadata().getProductionDate();
        }
        return str;
    }
    /**
     * Holds value of property citationInformationPanel.
     */
    private HtmlPanelGrid citationInformationPanel;

    /**
     * Getter for property citationInformationSection.
     * @return Value of property citationInformationSection.
     */
    public HtmlPanelGrid getCitationInformationPanel() {
        return this.citationInformationPanel;
    }

    /**
     * Setter for property citationInformationSection.
     * @param citationInformationSection New value of property citationInformationSection.
     */
    public void setCitationInformationPanel(HtmlPanelGrid citationInformationPanel) {
        this.citationInformationPanel = citationInformationPanel;
    }
    /**
     * Holds value of property abstractAndScopePanel.
     */
    private HtmlPanelGrid abstractAndScopePanel;

    /**
     * Getter for property abstractAndScopePanel.
     * @return Value of property abstractAndScopePanel.
     */
    public HtmlPanelGrid getAbstractAndScopePanel() {

        return this.abstractAndScopePanel;
    }

    /**
     * Setter for property abstractAndScopePanel.
     * @param abstractAndScopePanel New value of property abstractAndScopePanel.
     */
    public void setAbstractAndScopePanel(HtmlPanelGrid abstractAndScopePanel) {
        this.abstractAndScopePanel = abstractAndScopePanel;
    }
    /**
     * Holds value of property dataCollectionPanel.
     */
    private HtmlPanelGrid dataCollectionPanel;

    /**
     * Getter for property dataCollectionPanel.
     * @return Value of property dataCollectionPanel.
     */
    public HtmlPanelGrid getDataCollectionPanel() {
        return this.dataCollectionPanel;
    }

    /**
     * Setter for property dataCollectionPanel.
     * @param dataCollectionPanel New value of property dataCollectionPanel.
     */
    public void setDataCollectionPanel(HtmlPanelGrid dataCollectionPanel) {
        this.dataCollectionPanel = dataCollectionPanel;
    }
    /**
     * Holds value of property dataAvailPanel.
     */
    private HtmlPanelGrid dataAvailPanel;

    /**
     * Getter for property dataAvail.
     * @return Value of property dataAvail.
     */
    public HtmlPanelGrid getDataAvailPanel() {
        return this.dataAvailPanel;


    }

    /**
     * Setter for property dataAvail.
     * @param dataAvail New value of property dataAvail.
     */
    public void setDataAvailPanel(HtmlPanelGrid dataAvailPanel) {
        this.dataAvailPanel = dataAvailPanel;
    }
    /**
     * Holds value of property termsOfUsePanel.
     */
    private HtmlPanelGrid termsOfUsePanel;

    /**
     * Getter for property termsOfUse.
     * @return Value of property termsOfUse.
     */
    public HtmlPanelGrid getTermsOfUsePanel() {
        return this.termsOfUsePanel;


    }

    /**
     * Setter for property termsOfUse.
     * @param termsOfUse New value of property termsOfUse.
     */
    public void setTermsOfUsePanel(HtmlPanelGrid termsOfUsePanel) {
        this.termsOfUsePanel = termsOfUsePanel;
    }

    private void updateDisplay(HtmlPanelGrid panel) {
        if (panel.isRendered()) {
            panel.setRendered(false);
        } else {
            panel.setRendered(true);

        }
    }

    public void updateCitationInfoDisplay(ActionEvent actionEvent) {
        studyUI.setCitationInformationPanelIsRendered(!studyUI.isCitationInformationPanelIsRendered());
    }

    public void updateAbstractScopeDisplay(ActionEvent actionEvent) {
        studyUI.setAbstractAndScopePanelIsRendered(!studyUI.isAbstractAndScopePanelIsRendered());
    }

    public void updateDataAvailDisplay(ActionEvent actionEvent) {
        studyUI.setDataAvailPanelIsRendered(!studyUI.isDataAvailPanelIsRendered());
    }

    public void updateTermsOfUseDisplay(ActionEvent actionEvent) {
        studyUI.setTermsOfUsePanelIsRendered(!studyUI.isTermsOfUsePanelIsRendered());
    }

    public void updateDataCollectionDisplay(ActionEvent actionEvent) {
        studyUI.setDataCollectionPanelIsRendered(!studyUI.isDataCollectionPanelIsRendered());
    }

    public void updateNotesDisplay(ActionEvent actionEvent) {
        studyUI.setNotesPanelIsRendered(!studyUI.isNotesPanelIsRendered());
    }
    /**
     * Holds value of property notesPanel.
     */
    private HtmlPanelGrid notesPanel;

    /**
     * Getter for property notesPanel.
     * @return Value of property notesPanel.
     */
    public HtmlPanelGrid getNotesPanel() {
        return this.notesPanel;
    }

    /**
     * Setter for property notesPanel.
     * @param notesPanel New value of property notesPanel.
     */
    public void setNotesPanel(HtmlPanelGrid notesPanel) {
        this.notesPanel = notesPanel;
    }

    private boolean isEmpty(String s) {
        if (s == null || s.trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getAbstractAndScopePanelIsEmpty() {
        if (isEmpty(studyUI.getKeywords()) && isEmpty(studyUI.getTopicClasses()) && isEmpty(studyUI.getAbstracts()) && isEmpty(studyUI.getAbstractDates()) && isEmpty(studyUI.getRelPublications()) && isEmpty(studyUI.getRelMaterials()) && isEmpty(studyUI.getRelStudies()) && isEmpty(studyUI.getOtherRefs()) && (studyUI.getMetadata().getTimePeriodCoveredStart() == null || isEmpty(studyUI.getMetadata().getTimePeriodCoveredStart())) && (studyUI.getMetadata().getTimePeriodCoveredEnd() == null || isEmpty(studyUI.getMetadata().getTimePeriodCoveredEnd())) && (studyUI.getMetadata().getDateOfCollectionStart() == null || isEmpty(studyUI.getMetadata().getDateOfCollectionStart())) && (studyUI.getMetadata().getDateOfCollectionEnd() == null || isEmpty(studyUI.getMetadata().getDateOfCollectionEnd())) && isEmpty(studyUI.getMetadata().getCountry()) && isEmpty(studyUI.getMetadata().getGeographicCoverage()) && isEmpty(studyUI.getMetadata().getGeographicUnit()) && isEmpty(studyUI.getGeographicBoundings()) && isEmpty(studyUI.getMetadata().getUnitOfAnalysis()) && isEmpty(studyUI.getMetadata().getUniverse()) && isEmpty(studyUI.getMetadata().getKindOfData())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getDataCollectionIsEmpty() {
        if (isEmpty(studyUI.getMetadata().getTimeMethod()) && isEmpty(studyUI.getMetadata().getDataCollector()) && isEmpty(studyUI.getMetadata().getFrequencyOfDataCollection()) && isEmpty(studyUI.getMetadata().getSamplingProcedure()) && isEmpty(studyUI.getMetadata().getDeviationsFromSampleDesign()) && isEmpty(studyUI.getMetadata().getCollectionMode()) && isEmpty(studyUI.getMetadata().getResearchInstrument()) && isEmpty(studyUI.getMetadata().getDataSources()) && isEmpty(studyUI.getMetadata().getOriginOfSources()) && isEmpty(studyUI.getMetadata().getCharacteristicOfSources()) && isEmpty(studyUI.getMetadata().getAccessToSources()) && isEmpty(studyUI.getMetadata().getDataCollectionSituation()) && isEmpty(studyUI.getMetadata().getActionsToMinimizeLoss()) && isEmpty(studyUI.getMetadata().getControlOperations()) && isEmpty(studyUI.getMetadata().getWeighting()) && isEmpty(studyUI.getMetadata().getCleaningOperations()) && isEmpty(studyUI.getMetadata().getStudyLevelErrorNotes()) && isEmpty(studyUI.getMetadata().getResponseRate()) && isEmpty(studyUI.getMetadata().getSamplingErrorEstimate()) && isEmpty(studyUI.getMetadata().getOtherDataAppraisal())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getDataAvailIsEmpty() {
        if (isEmpty(studyUI.getStudyVersion().getNumberOfFiles()) && isEmpty(studyUI.getMetadata().getPlaceOfAccess()) && isEmpty(studyUI.getMetadata().getOriginalArchive()) && isEmpty(studyUI.getMetadata().getAvailabilityStatus()) && isEmpty(studyUI.getMetadata().getCollectionSize()) && isEmpty(studyUI.getMetadata().getStudyCompletion())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getTermsOfUseIsEmpty() {
        if (isEmpty(studyUI.getMetadata().getHarvestDVNTermsOfUse())
            && isEmpty(studyUI.getMetadata().getHarvestDVTermsOfUse())
            && !getVDCRequestBean().getVdcNetwork().isDownloadTermsOfUseEnabled() 
            && !studyUI.getStudy().getOwner().isDownloadTermsOfUseEnabled()
            && isEmpty(studyUI.getMetadata().getConfidentialityDeclaration()) && isEmpty(studyUI.getMetadata().getSpecialPermissions()) && isEmpty(studyUI.getMetadata().getRestrictions()) && isEmpty(studyUI.getMetadata().getContact()) && isEmpty(studyUI.getMetadata().getCitationRequirements()) && isEmpty(studyUI.getMetadata().getDepositorRequirements()) && isEmpty(studyUI.getMetadata().getConditions()) && isEmpty(studyUI.getMetadata().getDisclaimer())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getNotesIsEmpty() {
        if (isEmpty(studyUI.getNotes())) {
            return true;
        } else {
            return false;
        }
    }

    public void initPanelDisplay() {
        // We will always have citation info, 
        // so this is always rendered the first time we go to this page.
        studyUI.setCitationInformationPanelIsRendered(true);
        if (!getAbstractAndScopePanelIsEmpty()) {
            studyUI.setAbstractAndScopePanelIsRendered(true);
        }
        
        // When you first go to the page, if these sections contain data, these panels will ALSO be open 
        // they were previously set to be closed

        if (!getDataCollectionIsEmpty()) {
            studyUI.setDataCollectionPanelIsRendered(true);
        }
        if (!getDataAvailIsEmpty()) {
            studyUI.setDataAvailPanelIsRendered(true);
        }
        if (!getTermsOfUseIsEmpty()) {
            studyUI.setTermsOfUsePanelIsRendered(true);
        }
        if (!getNotesIsEmpty()) {
            studyUI.setNotesPanelIsRendered(true);
        }
    }
    
    private String tab;

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        if ( tab == null || tab.equals("files") || tab.equals("catalog") || tab.equals("comments") || tab.equals("versions") ) {
            this.tab = tab;
        }
    }
    /**
     * Holds value of property panelsInitialized.
     */
    private boolean panelsInitialized;

    /**
     * Getter for property panalsInitialized.
     * @return Value of property panalsInitialized.
     */
    public boolean isPanelsInitialized() {
        return this.panelsInitialized;
    }

    /**
     * Setter for property panalsInitialized.
     * @param panalsInitialized New value of property panalsInitialized.
     */
    public void setPanelsInitialized(boolean panelsInitialized) {
        this.panelsInitialized = panelsInitialized;
    }

    public StudyServiceLocal getStudyService() {
        return studyService;
    }

    public void setStudyService(StudyServiceLocal studyService) {
        this.studyService = studyService;
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

   

    public void setReadyForReview(ActionEvent ae) {
        studyUI.getStudyVersion().setVersionState(StudyVersion.VersionState.IN_REVIEW);
        studyService.setReadyForReview(studyUI.getStudy().getId());
    }

 //   public void setReleased(ActionEvent ae) {
 //       studyService.setReleased(studyUI.getStudy().getId());
 //       studyUI.setStudy(studyService.getStudy(studyId));
 //   }

    public String requestFileAccess() {
        LoginWorkflowBean loginWorkflowBean = (LoginWorkflowBean) this.getBean("LoginWorkflowBean");
        return loginWorkflowBean.beginFileAccessWorkflow(studyUI.getStudy().getId());
       
    }
    private List<DataFileFormatType> dataFileFormatTypes;

    public List getDataFileFormatTypes() {
        return getDataFileFormatTypes(false, false);
    }

    public List getDataFileFormatTypesWithOriginalFile() {
        return getDataFileFormatTypes(true, false);
    }

    public List getDataFileFormatTypesWithTab() {
        return getDataFileFormatTypes(false, true);
    }

    public List getDataFileFormatTypesWithTabWithOriginalFile() {
        return getDataFileFormatTypes(true, true);
    }

    public List getDataFileFormatTypes(boolean includeOriginalFile, boolean FixedFieldFile) {
        // initialize once
        if (dataFileFormatTypes == null) {
            dataFileFormatTypes = studyService.getDataFileFormatTypes();
        }

        List selectItems = new ArrayList();

	if ( FixedFieldFile ) {
	    selectItems.add(new SelectItem("", "Fixed-Field"));
	    // "D00" is a special reserved format for generating 
	    // tab-delimited files from data files stored in 
	    // fixed-field format:
	    selectItems.add(new SelectItem("D00", "Tab delimited"));
	} else {
	    selectItems.add(new SelectItem("", "Tab delimited"));
	}

        if (includeOriginalFile) {
            selectItems.add(new SelectItem(DataFileFormatType.ORIGINAL_FILE_DATA_FILE_FORMAT, "Original File"));
        }

        for (DataFileFormatType type : dataFileFormatTypes) {
            selectItems.add(new SelectItem(type.getValue(), type.getName()));
        }

        return selectItems;
    }
    /**
     * web statistics related
     * argument and methods
     *
     * @author wbossons
     */
    private String xff;

    public String getXff() {
        if (this.xff == null) {
            WebStatisticsSupport webstatistics = new WebStatisticsSupport();
            int headerValue = webstatistics.getParameterFromHeader("X-Forwarded-For");
            setXFF(webstatistics.getQSArgument("xff", headerValue));
    }
        return this.xff;
    }

    public void setXFF(String xff) {
        this.xff = xff;
    }

    protected boolean allowStudyComments = true;

    /**
     * Get the value of allowStudyComments
     *
     * @return the value of allowStudyComments
     */
    public boolean isAllowStudyComments() {
        return allowStudyComments;
    }

    /**
     * Set the value of allowStudyComments
     *
     * @param allowStudyComments new value of allowStudyComments
     */
    public void setAllowStudyComments(boolean allowStudyComments) {
        this.allowStudyComments = allowStudyComments;
    }

    public String beginRequestWorkflow() {
            LoginWorkflowBean lwf = (LoginWorkflowBean) getBean("LoginWorkflowBean");       
            return lwf.beginFileAccessWorkflow(studyId);
    }

   /** confirmation popup for deleting study versions in various states
    *
    * 
    */
    private enum StudyDeleteRequestType {DRAFT_VERSION, REVIEW_VERSION, DESTROY_STUDY};
    private StudyDeleteRequestType deleteRequested = null;


    private boolean showStudyDeletePopup = false;
    
    public void toggleStudyDeletePopup(javax.faces.event.ActionEvent event) {
        if (showStudyDeletePopup && (deleteRequested != null)) {
            deleteRequested = null;
        }
        showStudyDeletePopup = !showStudyDeletePopup;
    }

    public boolean isShowStudyDeletePopup() {
        return showStudyDeletePopup;
    }

    public void setShowStudyDeletePopup(boolean showPopup) {
        this.showStudyDeletePopup = showPopup;
    }

    public void confirmStudyDelete (ActionEvent ae) {
    //public String confirmStudyDelete () {
        VDC dataverse = null;
        String successMessage = "";
        Long dvId = null;

        if (studyUI != null && studyUI.getStudy() != null) {
            dataverse = studyUI.getStudy().getOwner();
        }

        if (StudyDeleteRequestType.DRAFT_VERSION.equals(deleteRequested)) {
            if (studyUI != null && studyUI.getStudyVersion() != null ) {
                successMessage = buildSuccessMessage( "Successfully deleted draft version of the study ");
                studyService.destroyWorkingCopyVersion(studyUI.getStudyVersion().getId());
            }
        } else if (StudyDeleteRequestType.REVIEW_VERSION.equals(deleteRequested)) {
            if (studyUI != null && studyUI.getStudyVersion() != null ) {
                successMessage = buildSuccessMessage("Successfully deleted review version of the study ");
                studyService.destroyWorkingCopyVersion(studyUI.getStudyVersion().getId());
            }
        } else if (StudyDeleteRequestType.DESTROY_STUDY.equals(deleteRequested)) {
            successMessage = buildSuccessMessage("Permanently destroyed study ");
            studyService.deleteStudy(studyId);

        } else {
            successMessage = "Warning: attempted to execute unknown delete action!";
        }

        showStudyDeletePopup = false;
        deleteRequested = null;

        // Once we have successfully deleted whatever it was that we
        // wanted to delete/destroy, we are sending the user to the
        // ManageStudies Page:

        if (getVDCRequestBean().getCurrentVDC() != null) {
            dvId = getVDCRequestBean().getCurrentVDC().getId();
        } else {
            dvId = dataverse.getId();
        }
        
        redirect("/faces/study/ManageStudiesPage.xhtml?vdcId="+dvId
                +"&successMessage="
                +successMessage);

    }

    private String buildSuccessMessage(String inString){
        String successMessage = new String();

        /*  -
         * When we were trying to render out the study title we tried
          to encode the illegal characters.
        try {
               successMessage = URLEncoder.encode( inString + "\"" +
               studyUI.getMetadata().getTitle() + "\", " +
               studyUI.getStudy().getGlobalId(), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
               successMessage =  inString + "\"" +
               studyUI.getMetadata().getTitle() + "\", " +
               studyUI.getStudy().getGlobalId();
        }
         */
              successMessage =  inString +
               " " +
               studyUI.getStudy().getGlobalId();
        return successMessage;
    }
    
    public void confirmDraftDeleteAction (ActionEvent event) {
        showStudyDeletePopup = true;
        deleteRequested = StudyDeleteRequestType.DRAFT_VERSION;
    }

    public void confirmInreviewDeleteAction (ActionEvent event) {
        showStudyDeletePopup = true;
        deleteRequested = StudyDeleteRequestType.REVIEW_VERSION;
    }

    public void confirmStudyDestroyAction (ActionEvent event) {
        showStudyDeletePopup = true;
        deleteRequested = StudyDeleteRequestType.DESTROY_STUDY;
    }

    public boolean isDraftDeleteRequested () {
        return StudyDeleteRequestType.DRAFT_VERSION.equals(deleteRequested);
    }

    public boolean isReviewDeleteRequested () {
        return StudyDeleteRequestType.REVIEW_VERSION.equals(deleteRequested);
    }

    public boolean isStudyDestroyRequested () {
        return StudyDeleteRequestType.DESTROY_STUDY.equals(deleteRequested);
    }

    /** toggleVersionNotesPopup
     * actionListener method for hiding
     * and showing the popup
     *
     * @param ActionEvent
     *studyPage.studyUI.studyVersion.versionNote
     * @author mheppler
    */
    
    private enum StudyActionRequestType {REVIEW, RELEASE};
    private StudyActionRequestType actionRequested = null;

   

    public void confirmReleased(ActionEvent ae) {
        // IMPORTANT:
        // as of now (May 2010), this method is no longer being used;
        // this decision, whether to release immediately or redirect
        // to the Diff page is made in the .xhtml page, through
        // rendering rules. 

        // See if the study already has released versions;
        // If not (i.e., this is the first release), we want to
        // simply switch the version state to "released"
        // -- but not before we present the Version Notes popup
        // to them.
        // If it is not the first release, we don't need the popup, but
        // we redirect them to the Diff page, so that they can review
        // the changes before they proceed with releasing the version.
        // The Version Notes popup will be presented when they hit
        // "release" there.

        StudyVersion releasedVersion = studyService.getStudyVersion(getStudyId(), null);

        if ( releasedVersion == null ) {
            // This is the first release of the study.
            actionRequested = StudyActionRequestType.RELEASE;
            versionNotesPopup.setVersionNote(studyUI.getStudyVersion().getVersionNote());
            versionNotesPopup.setShowPopup(true);
        } else {
            // Redirecting to the Differences page;
            // Need to set the correct HTTP parameters:
            redirect("/faces/study/StudyVersionDifferencesPage.xhtml?studyId="
                    +studyId+"&versionNumberList="
                    +releasedVersion.getVersionNumber()+","
                    +getVersionNumber()
                    +"&actionMode=confirmRelease"
                    +"&versionNumber="
                    +getVersionNumber());
        }

    }

    public void confirmSubmitForReview(ActionEvent ae) {
        actionRequested = StudyActionRequestType.REVIEW;
        versionNotesPopup.setVersionNote(studyUI.getStudyVersion().getVersionNote());
        versionNotesPopup.setShowPopup(true);
    }

    public void editStudyVersionNotes(ActionEvent ae) {
        actionRequested = null;
        versionNotesPopup.setVersionNote(studyUI.getStudyVersion().getVersionNote());
        versionNotesPopup.setShowPopup(true);
    }



    //public String saveVersionNote(javax.faces.event.ActionEvent event) {
    public String saveVersionNote() {

        studyUI.getStudyVersion().setVersionNote( versionNotesPopup.getVersionNote() );
        versionNotesPopup.setShowPopup(false);


        if (actionRequested != null) {

            if (actionRequested.equals(StudyActionRequestType.RELEASE)) {
                actionRequested = null;
                // If we made it this far and the desired action is
                // release, this must be the first release of the study;
                // otherwise the user would already have been redirected to the
                // Differences page.
                // But I'm still going to check that assumption before modifying
                // the state of the version:

                StudyVersion releasedVersion = studyService.getStudyVersion(getStudyId(), null);

                if (releasedVersion == null) {
                    studyService.setReleased(studyUI.getStudy().getId(), studyUI.getStudyVersion().getVersionNote());
                    // Get updated studyVersion to display on the page (we need this to get the releaseTime & state)
                    StudyVersion updatedVersion = studyService.getStudyVersion(studyUI.getStudyVersion().getStudy().getId(), studyUI.getStudyVersion().getVersionNumber());

                    // and reset page components
                    studyUIContainsFileDetails=false; // TODO: could we move this to be a member variable of StudyUI?
                    initPage(updatedVersion);

                }
            } else if (actionRequested.equals(StudyActionRequestType.REVIEW)) {
                studyUI.getStudyVersion().setVersionState(StudyVersion.VersionState.IN_REVIEW);
                studyService.setReadyForReview(studyUI.getStudy().getId(), studyUI.getStudyVersion().getVersionNote());

                actionRequested = null;
            }
        

        } else {
            // The user is editing the version note without modifying the state
            // of the version.
            studyService.saveVersionNote (studyUI.getStudyVersion().getId(), studyUI.getStudyVersion().getVersionNote());

        }

        return "";
    }
   

    protected HtmlCommandLink editStudyVersionNotesLink = new HtmlCommandLink();

    public HtmlCommandLink getEditStudyVersionNotesLink() {
        return editStudyVersionNotesLink;
    }

    public void setEditStudyVersionNotesLink(HtmlCommandLink editStudyVersionNotesLink) {
        this.editStudyVersionNotesLink = editStudyVersionNotesLink;
    }

    private boolean deaccessionedView = false;

    public boolean isDeaccessionedView() {
        return deaccessionedView;
    }

    public void setDeaccessionedView(boolean deaccessionedView) {
        this.deaccessionedView = deaccessionedView;
    }

  
    private String fileIdStr;


     public String getFileIdStr() {
        return fileIdStr;
    }


    public  synchronized void setFileIdStr(String fileIdStr) {
        dbgLog.fine("SetFileIdStr - " +fileIdStr);
        this.fileIdStr = fileIdStr;
        if (!StringUtil.isEmpty(fileIdStr)) {       
            notifyAll();
            JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(),"resetZipOutputResourceDisable();");
        }
    }

   
    public Resource getDownloadZipResource() {
        return new RFileResource( getVDCRequestBean().getCurrentVDCId(),  this);
    }

   

    static class RFileResource implements Resource, Serializable{
        File file;
        String currentVDCURL;
        Long vdcId;
        StudyPage studyPage;

      
       
        public RFileResource(Long vdcId, StudyPage studyPage) {
            currentVDCURL = getVDCRequestBean().getCurrentVDCURL();
            this.vdcId = vdcId;
            this.studyPage = studyPage;
        }

        public String calculateDigest() {
            return file != null ? file.getPath() : null;
        }

        public Date lastModified() {
            return file != null ? new Date(file.lastModified()) : null;
        }

        public InputStream open() throws IOException {
            String fileDownloadURL = "http://" + PropertyUtil.getHostUrl() + "/dvn" + currentVDCURL + "/FileDownload/";
            // If necessary, wait for these fields to be filled by the icefaces partial submit - called by Javascript in the StudyPage
            while (StringUtil.isEmpty(studyPage.getFileIdStr())) {
                try {
                    wait();
                } catch (Exception e) {
                }
            }

            fileDownloadURL += studyPage.getStudyUI().getStudy().getId() + ".zip?fileId=" + studyPage.getFileIdStr() + "&versionNumber=" + this.studyPage.getStudyUI().getStudyVersion().getVersionNumber() + studyPage.getXff();

            dbgLog.fine("..........OPENING STREAM: " + fileDownloadURL);
            return new URL(fileDownloadURL).openStream();
        }

        public void withOptions(Options arg0) throws IOException {
        }
    }

    
    
}
