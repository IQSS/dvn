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
package edu.harvard.hmdc.vdcnet.web.study;

import com.sun.jsfcl.data.DefaultTableDataModel;
import com.sun.rave.web.ui.component.Tab;
import com.sun.rave.web.ui.component.TabSet;
import edu.harvard.hmdc.vdcnet.admin.RoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCRole;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.study.DataFileFormatType;
import edu.harvard.hmdc.vdcnet.study.ReviewStateServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.util.WebStatisticsSupport;
import edu.harvard.hmdc.vdcnet.vdc.ReviewState;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import edu.harvard.hmdc.vdcnet.web.login.LoginWorkflowBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
    /**
     * Creates a new instance of StudyPage
     */
    private TabSet tabSet1 = new TabSet();

    public TabSet getTabSet1() {
        return tabSet1;
    }

    public void setTabSet1(TabSet ts) {
        this.tabSet1 = ts;
    }
    private Tab tab1 = new Tab();

    public Tab getTab1() {
        return tab1;
    }

    public void setTab1(Tab t) {
        this.tab1 = t;
    }
    private Tab tab2 = new Tab();

    public Tab getTab2() {
        return tab2;
    }

    public void setTab2(Tab t) {
        this.tab2 = t;
    }

    public StudyPage() {
    }

    public String tab1_action() {
        // TODO: Replace with your code

        return null;
    }

    public String tab2_action() {
        // TODO: Replace with your code

        return null;
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

        // check for studyListingIndex param
        if (getRequestParam("studyListingIndex") != null) {
            studyListingIndex = getRequestParam("studyListingIndex");
            // check if index from this session
            String sessionId = ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false)).getId();
            if (!sessionId.equals(studyListingIndex.substring(studyListingIndex.indexOf("_") + 1))) {
                studyListingIndex = null;
            }
        }

        // set tab if it was it was sent as pamameter or part of request bean
        if (getTab() != null) {
            getTabSet1().setSelected(getTab());
        } else if (getVDCRequestBean().getSelectedTab() != null) {
            getTabSet1().setSelected(getVDCRequestBean().getSelectedTab());
        }

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
            HttpServletRequest request = (HttpServletRequest) this.getExternalContext().getRequest();
            if (studyId != null) {
                if ("files".equals(getTabSet1().getSelected())) {
                    studyUI = new StudyUI(
                            studyService.getStudyDetail(studyId),
                            getVDCRequestBean().getCurrentVDC(),
                            getVDCSessionBean().getLoginBean() != null ? this.getVDCSessionBean().getLoginBean().getUser() : null,
                            getVDCSessionBean().getIpUserGroup());
                } else {
                    studyUI = new StudyUI(studyService.getStudyDetail(studyId));
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
        if (!studyUI.getStudy().getOwner().isDownloadTermsOfUseEnabled() && isEmpty(studyUI.getStudy().getConfidentialityDeclaration()) && isEmpty(studyUI.getStudy().getSpecialPermissions()) && isEmpty(studyUI.getStudy().getRestrictions()) && isEmpty(studyUI.getStudy().getContact()) && isEmpty(studyUI.getStudy().getCitationRequirements()) && isEmpty(studyUI.getStudy().getDepositorRequirements()) && isEmpty(studyUI.getStudy().getConditions()) && isEmpty(studyUI.getStudy().getDisclaimer())) {
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
        studyUI.setTermsOfUsePanelIsRendered(false);
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
        if (tab == null || tab.equals("files") || tab.equals("catalog")) {
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

    public boolean isUserAuthorizedToEdit() {
        boolean authorized = false;
        if (getVDCSessionBean().getLoginBean() != null) {
            authorized = studyUI.getStudy().isUserAuthorizedToEdit(getVDCSessionBean().getLoginBean().getUser());
        }
        return authorized;
    }

    public boolean isUserAuthorizedToRelease() {
        boolean authorized = false;
        if (getVDCSessionBean().getLoginBean() != null) {
            authorized = studyUI.getStudy().isUserAuthorizedToRelease(getVDCSessionBean().getLoginBean().getUser());
        }
        return authorized;
    }

    public void setReadyForReview(ActionEvent ae) {
        ReviewState inReview = this.reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_IN_REVIEW);
        studyUI.getStudy().setReviewState(inReview);
        studyService.updateStudy(studyUI.getStudy());


        Study study = studyUI.getStudy();
        VDCUser user = getVDCSessionBean().getLoginBean().getUser();
        // If the user adding the study is a Contributor, send notification to all Curators in this VDC
        // and send an email to the Contributor about the status of the study

        if (user.getVDCRole(study.getOwner()) != null && user.getVDCRole(study.getOwner()).getRole().getName().equals(RoleServiceLocal.CONTRIBUTOR)) {
            mailService.sendStudyInReviewNotification(user.getEmail(), study.getTitle());

            // Notify all curators and admins that study is in review
            for (Iterator it = study.getOwner().getVdcRoles().iterator(); it.hasNext();) {
                VDCRole elem = (VDCRole) it.next();
                if (elem.getRole().getName().equals(RoleServiceLocal.CURATOR) || elem.getRole().getName().equals(RoleServiceLocal.ADMIN)) {

                    mailService.sendStudyAddedCuratorNotification(elem.getVdcUser().getEmail(), user.getUserName(), study.getTitle(), study.getOwner().getName());
                }
            }

        }
    }

    public void setReleased(ActionEvent ae) {
        ReviewState released = this.reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_RELEASED);
        studyUI.getStudy().setReviewState(released);
        studyService.updateStudy(studyUI.getStudy());
        //    studyService.updateReviewState(studyUI.getStudy().getId(),ReviewStateServiceLocal.REVIEW_STATE_RELEASED);
        VDCRole studyCreatorRole = studyUI.getStudy().getCreator().getVDCRole(studyUI.getStudy().getOwner());

        if (studyCreatorRole != null && studyCreatorRole.getRole().getName().equals(RoleServiceLocal.CONTRIBUTOR)) {
            mailService.sendStudyReleasedNotification(studyUI.getStudy().getCreator().getEmail(), studyUI.getStudy().getTitle(), studyUI.getStudy().getOwner().getName());
        }
    }

    public String requestFileAccess() {
        LoginWorkflowBean loginWorkflowBean = (LoginWorkflowBean) this.getBean("LoginWorkflowBean");
        return loginWorkflowBean.beginFileAccessWorkflow(studyUI.getStudy().getId());
       
    }
    private List<DataFileFormatType> dataFileFormatTypes;

    public List getDataFileFormatTypes() {
        return getDataFileFormatTypes(false);
    }

    public List getDataFileFormatTypesWithOriginalFile() {
        return getDataFileFormatTypes(true);
    }

    public List getDataFileFormatTypes(boolean includeOriginalFile) {
        // initialize once
        if (dataFileFormatTypes == null) {
            dataFileFormatTypes = studyService.getDataFileFormatTypes();
        }

        List selectItems = new ArrayList();
        selectItems.add(new SelectItem("", "Tab delimited"));

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
    private String studyListingIndex;

    public String getStudyListingIndex() {
        return studyListingIndex;
    }

    public void setStudyListingIndex(String studyListingIndex) {
        this.studyListingIndex = studyListingIndex;
    }

    public String getStudyListingIndexAsParameter() {
        return studyListingIndex != null ? "&studyListingIndex=" + studyListingIndex : "";
    }
}
