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

import com.icesoft.faces.component.paneltabset.TabChangeEvent;
import com.sun.jsfcl.data.DefaultTableDataModel;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.DataFileFormatType;
import edu.harvard.iq.dvn.core.study.ReviewStateServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
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

    @EJB
    private StudyServiceLocal studyService;
    @EJB
    private ReviewStateServiceLocal reviewStateService;
    @EJB
    private MailServiceLocal mailService;

    private int selectedIndex;

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
      
    }


    public StudyPage() {
    }

 
    private DefaultTableDataModel dataTable5Model = new DefaultTableDataModel();

    public DefaultTableDataModel getDataTable5Model() {
        return dataTable5Model;
    }

    public void setDataTable5Model(DefaultTableDataModel dtdm) {
        this.dataTable5Model = dtdm;
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

    public String getCitationDate() {
        String str = "";
        if (getStudyUI().getStudy().getProductionDate() != null) {
            str = getStudyUI().getStudy().getProductionDate();
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

    public void init() {
        super.init();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        if (request.getHeader("referer") != null && ((String)request.getHeader("referer")).indexOf("CommentReview") != -1) {
            setTab("comments");
        }
        // set tab if it was it was sent as pamameter or part of request bean
        initSelectedTabIndex();
        if (isFromPage("StudyPage")) {
            setStudyUI((StudyUI) sessionGet(StudyUI.class.getName()));
            setStudyId(studyUI.getStudy().getId());
            studyUI.setStudy(studyService.getStudyDetail(studyId));

        } else {
            // if studyId was passes as a URL parameter it will
            // alread be set (and not null); otherwise:

            // If we're coming from EditStudyPage
            if (studyId == null) {
                studyId = getVDCRequestBean().getStudyId();
            }
            // we need to create the studyServiceBean
            //request = (HttpServletRequest) this.getExternalContext().getRequest();
            if (studyId != null) {
                if ("files".equals(tab)) {
                    initStudyUIWithFiles();
                } else {                
                        studyUI = new StudyUI(studyService.getStudyDetail(studyId),getVDCSessionBean().getUser());
                }
                // flag added to start with all file categories closed
                if (getRequestParam("renderFiles") != null && getRequestParam("renderFiles").equals("false")) {
                    for (FileCategoryUI catUI : studyUI.getCategoryUIList()) {
                        catUI.setRendered(false);
                    }
                }
                sessionPut(studyUI.getClass().getName(), studyUI);
                initPanelDisplay();

            } else {
                // WE SHOULD HAVE A STUDY ID, throw an error
                System.out.println("ERROR: in StudyPage, without a serviceBean or a studyId");
            }
        }
        allowStudyComments = studyUI.getStudy().getOwner().isAllowStudyComments();

    }

    private boolean isEmpty(String s) {
        if (s == null || s.trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getAbstractAndScopePanelIsEmpty() {
        if (isEmpty(studyUI.getKeywords()) && isEmpty(studyUI.getTopicClasses()) && isEmpty(studyUI.getAbstracts()) && isEmpty(studyUI.getAbstractDates()) && isEmpty(studyUI.getRelPublications()) && isEmpty(studyUI.getRelMaterials()) && isEmpty(studyUI.getRelStudies()) && isEmpty(studyUI.getOtherRefs()) && (studyUI.getStudy().getTimePeriodCoveredStart() == null || isEmpty(studyUI.getStudy().getTimePeriodCoveredStart())) && (studyUI.getStudy().getTimePeriodCoveredEnd() == null || isEmpty(studyUI.getStudy().getTimePeriodCoveredEnd())) && (studyUI.getStudy().getDateOfCollectionStart() == null || isEmpty(studyUI.getStudy().getDateOfCollectionStart())) && (studyUI.getStudy().getDateOfCollectionEnd() == null || isEmpty(studyUI.getStudy().getDateOfCollectionEnd())) && isEmpty(studyUI.getStudy().getCountry()) && isEmpty(studyUI.getStudy().getGeographicCoverage()) && isEmpty(studyUI.getStudy().getGeographicUnit()) && isEmpty(studyUI.getGeographicBoundings()) && isEmpty(studyUI.getStudy().getUnitOfAnalysis()) && isEmpty(studyUI.getStudy().getUniverse()) && isEmpty(studyUI.getStudy().getKindOfData())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getDataCollectionIsEmpty() {
        if (isEmpty(studyUI.getStudy().getTimeMethod()) && isEmpty(studyUI.getStudy().getDataCollector()) && isEmpty(studyUI.getStudy().getFrequencyOfDataCollection()) && isEmpty(studyUI.getStudy().getSamplingProcedure()) && isEmpty(studyUI.getStudy().getDeviationsFromSampleDesign()) && isEmpty(studyUI.getStudy().getCollectionMode()) && isEmpty(studyUI.getStudy().getResearchInstrument()) && isEmpty(studyUI.getStudy().getDataSources()) && isEmpty(studyUI.getStudy().getOriginOfSources()) && isEmpty(studyUI.getStudy().getCharacteristicOfSources()) && isEmpty(studyUI.getStudy().getAccessToSources()) && isEmpty(studyUI.getStudy().getDataCollectionSituation()) && isEmpty(studyUI.getStudy().getActionsToMinimizeLoss()) && isEmpty(studyUI.getStudy().getControlOperations()) && isEmpty(studyUI.getStudy().getWeighting()) && isEmpty(studyUI.getStudy().getCleaningOperations()) && isEmpty(studyUI.getStudy().getStudyLevelErrorNotes()) && isEmpty(studyUI.getStudy().getResponseRate()) && isEmpty(studyUI.getStudy().getSamplingErrorEstimate()) && isEmpty(studyUI.getStudy().getOtherDataAppraisal())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getDataAvailIsEmpty() {
        if (isEmpty(studyUI.getStudy().getPlaceOfAccess()) && isEmpty(studyUI.getStudy().getOriginalArchive()) && isEmpty(studyUI.getStudy().getAvailabilityStatus()) && isEmpty(studyUI.getStudy().getCollectionSize()) && isEmpty(studyUI.getStudy().getStudyCompletion())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getTermsOfUseIsEmpty() {
        if (isEmpty(studyUI.getStudy().getHarvestDVNTermsOfUse()) 
            && isEmpty(studyUI.getStudy().getHarvestDVTermsOfUse())
            && !getVDCRequestBean().getVdcNetwork().isDownloadTermsOfUseEnabled() 
            && !studyUI.getStudy().getOwner().isDownloadTermsOfUseEnabled()  
            && isEmpty(studyUI.getStudy().getConfidentialityDeclaration()) && isEmpty(studyUI.getStudy().getSpecialPermissions()) && isEmpty(studyUI.getStudy().getRestrictions()) && isEmpty(studyUI.getStudy().getContact()) && isEmpty(studyUI.getStudy().getCitationRequirements()) && isEmpty(studyUI.getStudy().getDepositorRequirements()) && isEmpty(studyUI.getStudy().getConditions()) && isEmpty(studyUI.getStudy().getDisclaimer())) {
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
        if (tab == null || tab.equals("files") || tab.equals("catalog") || tab.equals("comments")) {
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
    
    private boolean studyUIContainsFileDetails=false;
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
            }
        } 
    }
    
    private void initStudyUIWithFiles() {
          if (!studyUIContainsFileDetails) {
             studyUI = new StudyUI(
                            studyService.getStudyDetail(studyId),
                            getVDCRequestBean().getCurrentVDC(),
                            getVDCSessionBean().getLoginBean() != null ? this.getVDCSessionBean().getLoginBean().getUser() : null,
                            getVDCSessionBean().getIpUserGroup());
             studyUIContainsFileDetails=true;
          }
    }

    public void processTabChange(TabChangeEvent tabChangeEvent) throws AbortProcessingException {

        // workaround t solve the problem where the tabs don't switch after login
        String tab;
        switch (tabChangeEvent.getNewTabIndex()) {
            case 0: tab="catalog"; break;
            case 1: tab="files"; break;
            case 2: tab="comments"; break;
            default: tab="catalog"; break;
        }

        redirect("/faces/study/StudyPage.xhtml?studyId=" + studyId + "&tab=" + tab + getVDCRequestBean().getStudyListingIndexAsParameter());

        /*
        // If the user clicks on the files tab,
        // make sure the StudyUI object contains file details.
        if (tabChangeEvent.getNewTabIndex() == 2) {
            getVDCRequestBean().setSelectedTab("comments");
            setTab("comments");
        }
        if ( tabChangeEvent.getNewTabIndex() == 1) {
            initStudyUIWithFiles();
            getVDCRequestBean().setSelectedTab("files");
            setTab("files");
        }
        // If user clicks on the catalog tab, reset the open/closed settings for each section
        if (tabChangeEvent.getNewTabIndex() == 0) {
            initPanelDisplay();
            getVDCRequestBean().setSelectedTab("catalog");
            setTab("catalog");
        }
         * */
    }
}
