/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.study;

import com.icesoft.faces.component.datapaginator.DataPaginator;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlSelectBooleanCheckbox;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.web.SortableList;
import edu.harvard.iq.dvn.core.web.common.LoginBean;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;



/**
 *
 * @author Ellen Kraffmiller
 */
public class ManageStudiesList extends SortableList {
    private @EJB StudyServiceLocal studyService;
    private List<StudyUI> studyUIList;
    
    // dataTable Columns to sort by:
    private static final String ID_COLUMN= "id";
    private static final String TITLE_COLUMN = "title";
    private static final String CREATOR_COLUMN = "creator";
    private static final String DATE_CREATED_COLUMN = "dateCreated";
    private static final String STATUS_COLUMN = "status";
    private static final String DATE_RELEASED_COLUMN = "dateReleased";
    private static final String VERSION_COLUMN = "version";
    private static final String DATE_UPDATED_COLUMN = "lastUpdated";    
    private static final String ACTION_COLUMN = "actionReleased";
    private DataPaginator paginator;
    private static Logger dbgLog = Logger.getLogger(ManageStudiesList.class.getCanonicalName());
    private Long vdcId;
    private LoginBean loginBean;
    private VersionNotesPopupBean versionNotesPopupBean;

    // the StudyUI object for the currently selected study:
    // (for example, selected for deletion)

    private StudyUI currentStudyUI = null;

    public DataPaginator getPaginator() {
        return paginator;
    }

    public void setPaginator(DataPaginator paginator) {
        this.paginator = paginator;
    }

     
    /**
     * Holds value of property harvestDataTable.
     */
    private HtmlDataTable studyDataTable;

    /**
     * Getter for property siteDataTable.
     * @return Value of property siteDataTable.
     */
    public HtmlDataTable getStudyDataTable() {
        return this.studyDataTable;
    }  
    public void setStudyDataTable(HtmlDataTable studyDataTable) {
        this.studyDataTable = studyDataTable;
    }  
   
    public ManageStudiesList() {
        super(DATE_CREATED_COLUMN);
   
    }
    
    protected void sort() {
            String orderBy=null;
            if (sortColumnName == null) {
                return;
            }
            paginator.gotoFirstPage();
            if (sortColumnName.equals(TITLE_COLUMN)) {
                orderBy = "m.title";
            } else if (sortColumnName.equals(ID_COLUMN)) {
                orderBy="s.studyId";
            } else if (sortColumnName.equals(CREATOR_COLUMN)){
                orderBy="cr.userName";
            } else if (sortColumnName.equals(DATE_CREATED_COLUMN)) {
                orderBy="s.createTime";
            } else if (sortColumnName.equals(DATE_RELEASED_COLUMN)) {
                orderBy="v.releaseTime";
            } else if (sortColumnName.equals(STATUS_COLUMN)) {
                orderBy="v.versionState";
             } else if (sortColumnName.equals(VERSION_COLUMN)) {
                orderBy="v.versionNumber";
            } else if (sortColumnName.equals(DATE_UPDATED_COLUMN)) {
                orderBy="s.lastUpdateTime";
            } else {
                throw new RuntimeException("Unknown sortColumnName: "+sortColumnName);
            }
            List studyVersionIds =null;
            if (vdcId != null){
                if (loginBean != null && loginBean.isContributor() && contributorFilter) {
                    studyVersionIds = studyService.getDvOrderedStudyVersionIdsByContributor(vdcId, loginBean.getUser().getId(), orderBy, ascending);
                } else {
                    studyVersionIds = studyService.getDvOrderedStudyVersionIds(vdcId, orderBy, ascending);
                }
            } else{
                studyVersionIds = studyService.getAllStudyVersionIdsByContributor(loginBean.getUser().getId(), orderBy, ascending);
            }
            studyUIList = new ArrayList<StudyUI>();
            VDCUser user = loginBean == null ? null : loginBean.getUser();
            deaccessionedStudiesExist=false;
            for (Object studyVersionId: studyVersionIds) {
                StudyUI studyUI = new StudyUI((Long)studyVersionId,user);
                if (showArchivedStudies || !studyUI.getStudyVersion().isArchived()) {
                    studyUIList.add(studyUI);
                }
                if (studyUI.getStudyVersion().isArchived()){
                    deaccessionedStudiesExist=true;
                }
            }

    }

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }
    public boolean isDefaultAscending(String columnName) {
        return true;
        
    }
    
    public String getIdColumn() { return ID_COLUMN;}
    public String getTitleColumn() {return TITLE_COLUMN; }
    public String getCreatorColumn() {return CREATOR_COLUMN; }
    public String getCreatedColumn() {return DATE_CREATED_COLUMN; }
    public String getStatusColumn() { return STATUS_COLUMN; }
    public String getReleasedColumn() {return DATE_RELEASED_COLUMN; }
    public String getVersionColumn() { return VERSION_COLUMN; }
    public String getUpdatedColumn() { return DATE_UPDATED_COLUMN; }
    public String getActionColumn() {return ACTION_COLUMN; }

    public List<StudyUI> getStudyUIList() {     
        if (studyUIList==null) {
          
            sort();
        } else {
            checkSort();
        }
        return studyUIList;
    }

    public void setStudyUIList(List<StudyUI> studyUIList) {
        this.studyUIList = studyUIList;
    }

    public Long getVdcId() {
        return vdcId;
    }

    public void setVdcId(Long vdcId) {
        this.vdcId = vdcId;
    }

    public StudyUI getCurrentStudyUI () {
        return this.currentStudyUI;
    }

    public void setCurrentStudyUI (StudyUI csui) {
        this.currentStudyUI = csui;
    }


     public void doSetReleased(ActionEvent ae) {
        StudyUI studyUI = studyUIList.get(selectedIndex);
        versionNotesPopupBean.setShowPopup(false);
        studyService.saveVersionNote(studyUI.getStudyId(), versionNotesPopupBean.getVersionNote());
        studyService.setReleased(studyUI.getStudyId());
        // set list to null, to force a fresh retrieval of data
        studyUIList=null;
    }

    public void doSetInReview(ActionEvent ae) {
        StudyUI studyUI = (StudyUI) this.studyDataTable.getRowData();
        studyService.setReadyForReview(studyUI.getStudyVersion());
        // set list to null, to force a fresh retrieval of data
        studyUIList=null;
    }

    public void doSaveAndVersionNotesPopup(ActionEvent ae) {
        selectedIndex=studyDataTable.getRowIndex();
        versionNotesPopupBean.setShowPopup(true);
    }


    int selectedIndex;

    public void review_action(){
        StudyUI studyUI = (StudyUI) this.studyDataTable.getRowData();
        studyService.setReadyForReview(studyUI.getStudyId());
        studyUIList=null;
    }

    public void delete_action(){
        StudyUI studyUI = (StudyUI) this.studyDataTable.getRowData();
        studyService.destroyWorkingCopyVersion(studyUI.getStudyVersion().getId());
        studyUIList=null;
    }

    public void restore_action(){
        StudyUI studyUI = (StudyUI) this.studyDataTable.getRowData();
        studyService.setReleased(studyUI.getStudyId());
        studyUIList=null;
        
    }

   /** Confirmation popup for deleting (working) study versions.
    *
    *  -- you can only delete working versions from the ManageStudies
    *  page. To destroy an archived copy you have to go to to the
    *  StudyPage for that study, we're not supplying buttons for that
    *  in the ManageStudies view.
    */

    private boolean showStudyDeletePopup = false;

    public void toggleStudyDeletePopup(ActionEvent event) {
        if (showStudyDeletePopup) {
            deleteActionLabel = null;
            currentStudyUI = null;
        }
        showStudyDeletePopup = !showStudyDeletePopup;
    }

    public boolean isShowStudyDeletePopup() {
        return showStudyDeletePopup;
    }

    public void setShowStudyDeletePopup(boolean showPopup) {
        this.showStudyDeletePopup = showPopup;
    }

    private String deleteActionLabel;

    public String getDeleteActionLabel() {
        return deleteActionLabel;
    }

    public void setDeleteActionLabel(String actionLabel) {
        this.deleteActionLabel = actionLabel;
    }

    public void confirmStudyDelete (ActionEvent event) {

        if (currentStudyUI != null) {
            StudyVersion latestVersion = currentStudyUI.getStudy().getLatestVersion();
            if (latestVersion != null) {
                studyService.destroyWorkingCopyVersion(latestVersion.getId());
            }
        }

        showStudyDeletePopup = false;
        deleteActionLabel = null;
        currentStudyUI = null;

        FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();
        HttpServletResponse response = (javax.servlet.http.HttpServletResponse) fc.getExternalContext().getResponse();
        try {
            response.sendRedirect("/dvn/faces/study/ManageStudiesPage.xhtml?vdcId="+vdcId);
            fc.responseComplete();
        } catch (Exception ex) {
            // bummer.
            // for some reason, the redirect didn't work. we are already in the ManageStudies page,
            // so this redirect is not strictly necessary.
            // but there's a chance that the page didn't get refreshed properly;
            // UPDATE:
            // It looks like the redirect is not necessary really. To ensure that the list
            // of studies is refreshed, all you need to do is set the list of study UIs to null.
            // I'll try that instead. -- L.A.
        }

    }

    public void confirmDeleteAction (ActionEvent event) {
        showStudyDeletePopup = true;

        StudyUI studyUI = null; 
        
        if (currentStudyUI == null) {
            studyUI = (StudyUI) this.studyDataTable.getRowData();
        } else {
            studyUI = currentStudyUI;
        }

        if ( studyUI != null ) {
            currentStudyUI = studyUI;
            showStudyDeletePopup = true;

            if (studyUI.getStudy() != null) {
                if (studyUI.getStudy().isDraft() ) {
                    deleteActionLabel = "delete this draft study version";
                }
                else if (studyUI.getStudy().isInReview() ) {
                    deleteActionLabel = "delete this review study version";
                }
            }
        }
    }

    private boolean contributorFilter;

    public void filter(ActionEvent event){
        System.out.println("Filter is "+ filterDropdown.getValue());
        contributorFilter = Boolean.valueOf((String)filterDropdown.getValue());
        studyUIList = null;
    }
    
    private boolean showArchivedStudies;

    /**
     * @return the showArchivedStudies
     */
    public boolean isShowArchivedStudies() {
        return showArchivedStudies;
    }

    /**
     * @param showArchivedStudies the showArchivedStudies to set
     */
    public void setShowArchivedStudies(boolean showArchivedStudies) {
        this.showArchivedStudies = showArchivedStudies;
    }

    public List getStudyScopeSelectItems() {
        List selectItems = new ArrayList();
        selectItems.add(new SelectItem(false, "All studies belonging to the dataverse"));
        selectItems.add(new SelectItem(true, "Only studies I have contributed to"));
        return selectItems;
    }


    public boolean isContributorFilterRendered(){
        boolean rendered = VDCBaseBean.getVDCRequestBean().getCurrentVDC() != null && (isUserCuratorOrAdmin() || isRegAndEdit() || isUserContributorAndAllowedToEdit());
        return rendered;
    }

    private boolean isUserCuratorOrAdmin(){
        VDCUser user = loginBean == null ? null : loginBean.getUser();
        VDC vdc = VDCBaseBean.getVDCRequestBean().getCurrentVDC();
        return user.isCurator(vdc)|| user.isAdmin(vdc);
    }

    private boolean isRegAndEdit(){
        VDC vdc = VDCBaseBean.getVDCRequestBean().getCurrentVDC();
        return vdc.isAllowRegisteredUsersToContribute() && vdc.isAllowContributorsEditAll();
    }

    private boolean isUserContributorAndAllowedToEdit(){
        VDCUser user = loginBean == null ? null : loginBean.getUser();
        VDC vdc = VDCBaseBean.getVDCRequestBean().getCurrentVDC();
        return user.isContributor(vdc) && vdc.isAllowContributorsEditAll();
    }

    /**
     * @return the contributorFilter
     */
    public boolean isContributorFilter() {
        return contributorFilter;
    }

    /**
     * @param contributorFilter the contributorFilter to set
     */
    public void setContributorFilter(boolean contributorFilter) {
        this.contributorFilter = contributorFilter;
    }

    private HtmlSelectOneMenu filterDropdown = new HtmlSelectOneMenu();

    /**
     * @return the filterDropdown
     */
    public HtmlSelectOneMenu getFilterDropdown() {
        return filterDropdown;
    }

    /**
     * @param filterDropdown the filterDropdown to set
     */
    public void setFilterDropdown(HtmlSelectOneMenu filterDropdown) {
        this.filterDropdown = filterDropdown;
    }

    public void changeArchive(ValueChangeEvent vce){
        showArchivedStudies = (Boolean)archiveCheckBox.getValue();
        studyUIList=null;
//        sort();
    }

    private HtmlSelectBooleanCheckbox archiveCheckBox;

    /**
     * @return the archiveCheckBox
     */
    public HtmlSelectBooleanCheckbox getArchiveCheckBox() {
        return archiveCheckBox;
    }

    /**
     * @param archiveCheckBox the archiveCheckBox to set
     */
    public void setArchiveCheckBox(HtmlSelectBooleanCheckbox archiveCheckBox) {
        this.archiveCheckBox = archiveCheckBox;
    }

    private boolean deaccessionedStudiesExist;

    /**
     * @return the deaccessionedStudiesExist
     */
    public boolean isDeaccessionedStudiesExist() {
        return deaccessionedStudiesExist;
    }

    /**
     * @param deaccessionedStudiesExist the deaccessionedStudiesExist to set
     */
    public void setDeaccessionedStudiesExist(boolean deaccessionedStudiesExist) {
        this.deaccessionedStudiesExist = deaccessionedStudiesExist;
    }

    /**
     * @return the versionNotesPopupBean
     */
    public VersionNotesPopupBean getVersionNotesPopupBean() {
        return versionNotesPopupBean;
    }

    /**
     * @param versionNotesPopupBean the versionNotesPopupBean to set
     */
    public void setVersionNotesPopupBean(VersionNotesPopupBean versionNotesPopupBean) {
        this.versionNotesPopupBean = versionNotesPopupBean;
    }
}
