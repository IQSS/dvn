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
import com.icesoft.faces.context.effects.JavascriptContext;
import edu.harvard.iq.dvn.core.study.DataFileFormatType;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyFileServiceLocal; 
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.util.WebStatisticsSupport;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.common.VDCSessionBean;
import edu.harvard.iq.dvn.core.web.login.LoginWorkflowBean;
import edu.harvard.iq.dvn.core.web.servlet.TermsOfUseFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import  com.sun.enterprise.config.serverbeans.Config;
import  com.sun.enterprise.util.SystemPropertyConstants;
import  com.sun.grizzly.config.dom.NetworkListener;
import  org.glassfish.internal.api.Globals;
import  org.glassfish.internal.api.ServerContext;
import org.jvnet.hk2.component.Habitat;
/**
 *
 * @author Ellen Kraffmiller
 */
@Named
@ViewScoped
public class StudyPage extends VDCBaseBean implements java.io.Serializable  {
    private static Logger dbgLog = Logger.getLogger(StudyPage.class.getCanonicalName());
    @EJB private StudyServiceLocal studyService;
    @EJB private StudyFileServiceLocal studyFileService; 

    @Inject private VersionNotesPopupBean versionNotesPopup;
    
    public StudyPage() {
    }

    // params
    private String globalId;
    private Long studyId;
    private Long versionNumber;

    private boolean studyUIContainsFileDetails=false; // TODO: needed??
    private int selectedIndex;

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
    
   public void preRenderView() {
       super.preRenderView();
       // add javascript call on each partial submit to initialize the help tips for added fields
       JavascriptContext.addJavascriptCall(getFacesContext(),"initInlineHelpTip();");
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
                if (sv != null) {
                    studyId = sv.getStudy().getId();
                    getVDCRequestBean().setStudyId(studyId);
                }
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
        if (isEmpty(studyUI.getKeywords()) && isEmpty(studyUI.getTopicClasses()) && isEmpty(studyUI.getAbstracts()) && isEmpty(studyUI.getAbstractDates()) && isEmpty(studyUI.getRelMaterials()) && isEmpty(studyUI.getRelStudies()) && isEmpty(studyUI.getOtherRefs()) && (studyUI.getMetadata().getTimePeriodCoveredStart() == null || isEmpty(studyUI.getMetadata().getTimePeriodCoveredStart())) && (studyUI.getMetadata().getTimePeriodCoveredEnd() == null || isEmpty(studyUI.getMetadata().getTimePeriodCoveredEnd())) && (studyUI.getMetadata().getDateOfCollectionStart() == null || isEmpty(studyUI.getMetadata().getDateOfCollectionStart())) && (studyUI.getMetadata().getDateOfCollectionEnd() == null || isEmpty(studyUI.getMetadata().getDateOfCollectionEnd())) && isEmpty(studyUI.getMetadata().getCountry()) && isEmpty(studyUI.getMetadata().getGeographicCoverage()) && isEmpty(studyUI.getMetadata().getGeographicUnit()) && isEmpty(studyUI.getGeographicBoundings()) && isEmpty(studyUI.getMetadata().getUnitOfAnalysis()) && isEmpty(studyUI.getMetadata().getUniverse()) && isEmpty(studyUI.getMetadata().getKindOfData())) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean getCustomDataCollectionIsEmpty() {
        if (studyUI.getMetadata().getStudyFieldValues() == null || studyUI.getMetadata().getStudyFieldValues().size() == 0) {
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

        if (!getCustomDataCollectionIsEmpty() || !getDataCollectionIsEmpty()) {
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

    public String confirmStudyDelete () {
    //public String confirmStudyDelete () {
        VDC dataverse = null;
        Long dvId = null;

        if (studyUI != null && studyUI.getStudy() != null) {
            dataverse = studyUI.getStudy().getOwner();
        }

        if (StudyDeleteRequestType.DRAFT_VERSION.equals(deleteRequested)) {
            if (studyUI != null && studyUI.getStudyVersion() != null ) {
                getVDCRenderBean().getFlash().put("successMessage", buildSuccessMessage( "Successfully deleted draft version of the study "));
                studyService.destroyWorkingCopyVersion(studyUI.getStudyVersion().getId());
            }
        } else if (StudyDeleteRequestType.REVIEW_VERSION.equals(deleteRequested)) {
            if (studyUI != null && studyUI.getStudyVersion() != null ) {
                getVDCRenderBean().getFlash().put("successMessage", buildSuccessMessage("Successfully deleted review version of the study "));
                studyService.destroyWorkingCopyVersion(studyUI.getStudyVersion().getId());
            }
        } else if (StudyDeleteRequestType.DESTROY_STUDY.equals(deleteRequested)) {
            getVDCRenderBean().getFlash().put("successMessage", buildSuccessMessage("Permanently destroyed study "));
            studyService.deleteStudy(studyId);

        } else {
            getVDCRenderBean().getFlash().put("warningMessage", "Warning: attempted to execute unknown delete action!");
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
        
        return "/study/ManageStudiesPage?faces-redirect=true&vdcId=" + dvId;
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
        // This is the first release of the study.
        // The logic for determining that this study hasn't been released or deaccesstioned is in the StudyPage.
        actionRequested = StudyActionRequestType.RELEASE;
        versionNotesPopup.setVersionNote(studyUI.getStudyVersion().getVersionNote());
        versionNotesPopup.setShowPopup(true);


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

  

    public boolean isTermsOfUseRequired() {

        // We only need to display the terms if the study is Released.
        if (studyUI.getStudy().getReleasedVersion() != null) {
            Map termsOfUseMap = getTermsOfUseMap();
            if (TermsOfUseFilter.isDownloadDvnTermsRequired(getVDCRequestBean().getVdcNetwork(), termsOfUseMap) ||
                TermsOfUseFilter.isDownloadDataverseTermsRequired(studyUI.getStudy(), termsOfUseMap) ||
                TermsOfUseFilter.isDownloadStudyTermsRequired(studyUI.getStudy(), termsOfUseMap)) {
                return true;
            }
        }

        return false;
    }

    public void setTermsOfUseRequired(boolean termsOfUseRequired) {
        // nothing to set; empty method needed by Icefaces inputHidden
    }
    

    private Map getTermsOfUseMap() {
            VDCSessionBean vdcSession = getVDCSessionBean();
            if (vdcSession != null) {
                if (vdcSession.getLoginBean() != null) {
                    return vdcSession.getLoginBean().getTermsfUseMap();
                } else {
                    return vdcSession.getTermsfUseMap();
                }
            }

        return new HashMap();
    }

    public String getApiMetadataUrlWithoutDataSection() {
        String baseMetadataUrl = getApiMetadataUrl(); 
        if (baseMetadataUrl == null || baseMetadataUrl.equals("")) {
            return null; 
        }
       
        if (!studyUI.getStudy().isIsHarvested()) {
            if (studyFileService.doesStudyHaveTabularFiles(studyUI.getStudyVersion().getId())) {
                return baseMetadataUrl + "?partialExclude=codeBook/dataDscr";
            }
        }
        
        return null; 
    }
    
    public String getApiMetadataUrl() {
        // For now, we only display these links if a) this is the released 
        // study version and b) its metadata is not restricted.
        
        // The study also has to have been exported...
        // It really looks like we should instead make a real 
        // metadataFormatsAvailable API call here. -- TODO in 3.1?
                
        if (studyUI.getStudyVersion().isReleased() && !studyUI.getStudy().isRestricted() && (studyUI.getStudy().getLastExportTime() != null)) {
            
            String httpsHostUrl = getServerHostAndPort(true); 
            if (httpsHostUrl == null || httpsHostUrl.equals("")) {
                return null; 
            }
            String finalUrl = "https://" + httpsHostUrl + "/dvn/api/metadata/"+studyId;
        
            return finalUrl; 
        }
        
        return null;
    }
    
    // The utilities below - we have to jump through all these hoops solely 
    // to obtain our own HTTPS port number (!); 
    // This code is based on ... from ... by ...
    // We probably want to move it into its own dedicated utility; it could 
    // be useful elsewhere. 
    
    /**
     * Get the hostname and port of the secure or non-secure http listener for the default
     * virtual server in this server instance.  The string representation will be of the
     * form 'hostname:port'.
     *
     * @param secure true if you want the secure port, false if you want the non-secure port
     * @return the 'hostname:port' combination or null if there were any errors calculating the address
     */
    public String getServerHostAndPort(boolean secure) {
        final String host = getHostName();
        final String port = getPort(secure);
        
        if ((host == null) || (port == null)) {
            return null;
        }
        
        return host + ":" + port;
    }

    /**
     * Lookup the canonical host name of the system this server instance is running on.
     *
     * @return the canonical host name or null if there was an error retrieving it
     */
    private String getHostName() {
        // this value is calculated from InetAddress.getCanonicalHostName when the AS is
        // installed.  asadmin then passes this value as a system property when the server
        // is started.
        String myHost = System.getProperty(SystemPropertyConstants.HOST_NAME_PROPERTY);
                
        return myHost; 
    }

    
    /**
     * Get the http/https port number for the default virtual server of this server instance.
     * <p/>
     * If the 'secure' parameter is true, then return the secure http listener port, otherwise
     * return the non-secure http listener port.
     *
     * @param secure true if you want the secure port, false if you want the non-secure port
     * @return the port or null if there was an error retrieving it.
     */
    private String getPort(boolean secure) {
        try {
            String serverName = System.getProperty(SystemPropertyConstants.SERVER_NAME);
            if (serverName == null) {
                final ServerContext serverContext = Globals.get(org.glassfish.internal.api.ServerContext.class);
                if (serverContext != null) {
                    serverName = serverContext.getInstanceName();
                }

                if (serverName == null) {
                    return null; 
                }
            }
            
            Habitat defaultHabitat = Globals.getDefaultHabitat();            
            Config config = defaultHabitat.getInhabitantByType(com.sun.enterprise.config.serverbeans.Config.class).get();
            
            String[] networkListenerNames = config.getHttpService().getVirtualServerByName(serverName).getNetworkListeners().split(",");
            
            for (String listenerName : networkListenerNames) {
                if (listenerName == null || listenerName.length() == 0) {
                    continue;
                }

                NetworkListener listener = config.getNetworkConfig().getNetworkListener(listenerName.trim());

                if (secure == Boolean.valueOf(listener.findHttpProtocol().getSecurityEnabled())) {
                    return listener.getPort();
                }
            }
        } catch (Throwable t) {
            
            // error condition handled - we'll just log it and return null.
            dbgLog.info("Configuratoin lookup: Exception occurred retrieving port configuration... " + t.getMessage());
            
        }

        return null;
    }

    
}
