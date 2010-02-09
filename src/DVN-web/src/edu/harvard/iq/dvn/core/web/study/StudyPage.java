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
import com.sun.jsfcl.data.DefaultTableDataModel;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.DataFileFormatType;
import edu.harvard.iq.dvn.core.study.ReviewStateServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.util.WebStatisticsSupport;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.login.LoginWorkflowBean;
import java.util.ArrayList;
import java.util.List;
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

    @EJB private StudyServiceLocal studyService;
    @EJB private ReviewStateServiceLocal reviewStateService;
    @EJB private MailServiceLocal mailService;


    public StudyPage() {
    }

    // params
    private String globalId;
    private Long studyId;
    private Long versionNumber;

    private boolean studyUIContainsFileDetails=false; // TODO: needed??
    private int selectedIndex;


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


        // If we're coming from EditStudyPage
        if (studyId == null) {
            studyId = getVDCRequestBean().getStudyId();
        }
        if (versionNumber == null) {
            versionNumber = getVDCRequestBean().getStudyVersionNumber();
        }
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
            }

            
            if ("files".equals(tab)) {
                initStudyUIWithFiles(sv);
            } else {
                studyUI = new StudyUI(sv, getVDCSessionBean().getUser());
                initPanelDisplay();
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
        if (isEmpty(studyUI.getMetadata().getPlaceOfAccess()) && isEmpty(studyUI.getMetadata().getOriginalArchive()) && isEmpty(studyUI.getMetadata().getAvailabilityStatus()) && isEmpty(studyUI.getMetadata().getCollectionSize()) && isEmpty(studyUI.getMetadata().getStudyCompletion())) {
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
        // When you first go to the page, these panels will be closed regardless of 
        // whether they contain data
        studyUI.setDataCollectionPanelIsRendered(false);
        studyUI.setDataAvailPanelIsRendered(false);
        if (!getTermsOfUseIsEmpty()) {
            studyUI.setTermsOfUsePanelIsRendered(true);
        }
        studyUI.setNotesPanelIsRendered(false);
    }

    public String confirmDelete() {
        getVDCRequestBean().setStudyId(studyUI.getStudy().getId());
        return "deleteStudy";
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
        studyService.setReadyForReview(studyUI.getStudy().getId());
        studyUI.setStudy(studyService.getStudy(studyId));
    }

    public void setReleased(ActionEvent ae) {
        studyService.setReleased(studyUI.getStudy().getId());
        studyUI.setStudy(studyService.getStudy(studyId));
    }

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

    /** toggleVersionNotesPopup
     * actionListener method for hiding
     * and showing the popup
     *
     * @param ActionEvent
     *
     * @author mheppler
    */
    public void toggleVersionNotesPopup(javax.faces.event.ActionEvent event) {
        showVersionNotesPopup = !showVersionNotesPopup;
        actionComplete = false;
    }

    protected boolean showVersionNotesPopup = false;
    protected boolean actionComplete = false;

    public boolean isActionComplete() {
        return actionComplete;
    }

    public void setActionComplete(boolean actionComplete) {
        this.actionComplete = actionComplete;
    }

    public boolean isShowVersionNotesPopup() {
        return showVersionNotesPopup;
    }

    public void setShowVersionNotesPopup(boolean showVersionNotesPopup) {
        this.showVersionNotesPopup = showVersionNotesPopup;
    }

    protected HtmlCommandLink editStudyVersionNotesLink = new HtmlCommandLink();

    public HtmlCommandLink getEditStudyVersionNotesLink() {
        return editStudyVersionNotesLink;
    }

    public void setEditStudyVersionNotesLink(HtmlCommandLink editStudyVersionNotesLink) {
        this.editStudyVersionNotesLink = editStudyVersionNotesLink;
    }
    
}
