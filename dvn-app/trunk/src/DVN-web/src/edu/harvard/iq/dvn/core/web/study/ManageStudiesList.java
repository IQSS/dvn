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
import edu.harvard.iq.dvn.core.web.common.LoginBean;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;



/**
 *
 * @author Ellen Kraffmiller
 */
@ViewScoped
@Named("ManageStudiesList")
public class ManageStudiesList extends VDCBaseBean {
    private @EJB StudyServiceLocal studyService;
    private List<StudyUI> studyUIList;
    
    // dataTable Columns to sort by:
    private static final String ID_COLUMN= "id";
    private static final String TITLE_COLUMN = "title";
    private static final String CREATOR_COLUMN = "creator";
    private static final String DATE_CREATED_COLUMN = "dateCreated";
    private static final String STATUS_COLUMN = "status";
    private static final String DATE_RELEASED_COLUMN = "dateReleased";
    private static final String DATE_DEACCESSIONED_COLUMN = "dateDeaccessioned";
    private static final String VERSION_COLUMN = "version";
    private static final String DATE_UPDATED_COLUMN = "lastUpdated";    
    private static final String ACTION_COLUMN = "actionReleased";
    private DataPaginator paginator;
    private static Logger dbgLog = Logger.getLogger(ManageStudiesList.class.getCanonicalName());
    @Inject private VersionNotesPopupBean versionNotesPopupBean = new VersionNotesPopupBean();

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
            } else if (sortColumnName.equals(DATE_DEACCESSIONED_COLUMN)) {
                orderBy="v.archiveTime";
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
            List deaccessionedStudyVersionIds = null;
            Long vdcId = getVDCRequestBean().getCurrentVDCId();
            
            dbgLog.info("manage studies list; vdcid: "+vdcId);
            
            VDCUser user = getVDCSessionBean().getLoginBean() == null ? null : getVDCSessionBean().getLoginBean().getUser();
            
            if (user != null) {
                dbgLog.info("manage studies list; user: "+user.getUserName());
            } else {
                dbgLog.info("manage studies list; null user.");
            }
            
            if (vdcId != null){
                if (user != null && (contributorFilter || (!isUserCuratorOrAdminOrNetworkAdmin() && !getVDCRequestBean().getCurrentVDC().isAllowContributorsEditAll()))) {
                    studyVersionIds = studyService.getDvOrderedStudyVersionIdsByContributor(vdcId, user.getId(), orderBy, ascending);
                    deaccessionedStudyVersionIds = studyService.getDvOrderedDeaccessionedStudyVersionIdsByContributor(vdcId, vdcId, orderBy, ascending);
                } else {
                    studyVersionIds = studyService.getDvOrderedStudyVersionIds(vdcId, orderBy, ascending);
                    deaccessionedStudyVersionIds = studyService.getDvOrderedDeaccessionedStudyVersionIds(vdcId, orderBy, ascending);
                }
            } else if (user != null){                                
                studyVersionIds = studyService.getAllStudyVersionIdsByContributor(user.getId(), orderBy, ascending);
                deaccessionedStudyVersionIds = studyService.getAllDeaccessionedStudyVersionIdsByContributor(user.getId(), orderBy, ascending);
            }
            
            if (studyVersionIds == null) {
                studyVersionIds = new ArrayList<Long>(); 
            }
            
            studyUIList = new ArrayList<StudyUI>();
            
            for (Object studyVersionId: studyVersionIds) {
                StudyUI studyUI = new StudyUI((Long)studyVersionId,user);
                if (showArchivedStudies || !deaccessionedStudyVersionIds.contains(studyVersionId)) {
                    studyUIList.add(studyUI);
                }
            }

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
    public String getDeaccessionedColumn() {return DATE_DEACCESSIONED_COLUMN; }
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


    public StudyUI getCurrentStudyUI () {
        return this.currentStudyUI;
    }

    public void setCurrentStudyUI (StudyUI csui) {
        this.currentStudyUI = csui;
    }


  
    public void doConfirmVersionNotesPopup(ActionEvent ae) {
      StudyUI studyUI = studyUIList.get(selectedIndex);
        versionNotesPopupBean.setShowPopup(false);

        if (versionPopupMode.equals(VersionPopupMode.REVIEW)) {
            studyService.setReadyForReview(studyUI.getStudyId(), versionNotesPopupBean.getVersionNote());
        } else
              studyService.setReleased(studyUI.getStudyId(), versionNotesPopupBean.getVersionNote());
        // set list to null, to force a fresh retrieval of data
        studyUIList=null;
    }

    public void doShowReviewPopup(ActionEvent ae) {
        selectedIndex=studyDataTable.getRowIndex();
        versionPopupMode = VersionPopupMode.REVIEW;
        versionNotesPopupBean.setVersionNote(studyUIList.get(selectedIndex).getStudyVersion().getVersionNote());
        versionNotesPopupBean.setShowPopup(true);
    }

    public void doShowReleasePopup(ActionEvent ae) {
        selectedIndex=studyDataTable.getRowIndex();
        versionPopupMode = VersionPopupMode.RELEASE;
        versionNotesPopupBean.setVersionNote(studyUIList.get(selectedIndex).getStudyVersion().getVersionNote());
        versionNotesPopupBean.setShowPopup(true);
    }

    private enum VersionPopupMode { REVIEW, RELEASE};
    private VersionPopupMode versionPopupMode;

    int selectedIndex;

  
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

    public void confirmStudyDelete (ActionEvent event) {
        String successMessage = "";

        if (currentStudyUI != null) {
            StudyVersion latestVersion = currentStudyUI.getStudy().getLatestVersion();
            if (latestVersion != null) {
                successMessage = "Successfully deleted working version of the study \""+
                    currentStudyUI.getMetadata().getTitle() + "\", " +
                    currentStudyUI.getStudy().getGlobalId();
                studyService.destroyWorkingCopyVersion(latestVersion.getId());
            }
        }

        showStudyDeletePopup = false;
        currentStudyUI = null;


        // to make the page refresh, simply reset the studyUI list:
        studyUIList = null;

        // and finally, set the status message:
        getExternalContext().getFlash().put("successMessage",successMessage);

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
        }
    }



    public boolean isDraftDeleteRequested () {
        if (currentStudyUI != null) {
            StudyVersion latestVersion = currentStudyUI.getStudy().getLatestVersion();
            if (latestVersion != null) {
               return latestVersion.isDraft();
            }
        }

        return false;
    }

    public boolean isReviewDeleteRequested () {
        if (currentStudyUI != null) {
            StudyVersion latestVersion = currentStudyUI.getStudy().getLatestVersion();
            if (latestVersion != null) {
               return latestVersion.isInReview();
            }
        }

        return false;
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
        boolean rendered = getVDCRequestBean().getCurrentVDC() != null && (isUserCuratorOrAdminOrNetworkAdmin() || isRegAndEdit() || isUserContributorAndAllowedToEdit());
        return rendered;
    }

    private boolean isUserCuratorOrAdmin(){
        VDCUser user = getVDCSessionBean().getLoginBean() == null ? null : getVDCSessionBean().getLoginBean().getUser();
        VDC vdc = getVDCRequestBean().getCurrentVDC();
        if (user!=null) {
            return user.isCurator(vdc)|| user.isAdmin(vdc);
        }
        return false;
    }

    private boolean isUserCuratorOrAdminOrNetworkAdmin(){
        VDCUser user = getVDCSessionBean().getLoginBean() == null ? null : getVDCSessionBean().getLoginBean().getUser();
        VDC vdc = getVDCRequestBean().getCurrentVDC();
        if (user!=null) {
            return user.isCurator(vdc)|| user.isAdmin(vdc) || user.isNetworkAdmin();
        }
        return false;
    }

    private boolean isRegAndEdit(){
        VDC vdc = getVDCRequestBean().getCurrentVDC();
        return vdc.isAllowRegisteredUsersToContribute() && vdc.isAllowContributorsEditAll();
    }

    private boolean isUserContributorAndAllowedToEdit(){
        VDCUser user = getVDCSessionBean().getLoginBean() == null ? null : getVDCSessionBean().getLoginBean().getUser();
        VDC vdc = getVDCRequestBean().getCurrentVDC();
        if (user!=null) {
            return user.isContributor(vdc) && vdc.isAllowContributorsEditAll();
        }
        return false;
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

    private boolean ascending;

    // we only want to resort if the order or column has changed.
    private String oldSort;
    private boolean oldAscending;
    private String sortColumnName;

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public String getSortColumnName() {
        return sortColumnName;
    }

    public void setSortColumnName(String sortColumnName) {
        this.sortColumnName = sortColumnName;
    }

    

    public void init() {
        this.versionNotesPopupBean.setActionType(VersionNotesPopupBean.ActionType.MANAGE_STUDIES);
    }
    
    public ManageStudiesList() {
        this(DATE_CREATED_COLUMN);

    }

    public ManageStudiesList(String defaultSortColumn) {
        sortColumnName = defaultSortColumn;
        ascending = isDefaultAscending(defaultSortColumn);
        oldSort = sortColumnName;
        // make sure sortColumnName on first render
        oldAscending = !ascending;

    }

    private void checkSort() {
        // we only want to sortColumnName if the column or ordering has changed.
            if (!oldSort.equals(sortColumnName) ||
                oldAscending != ascending){
                sort();
                oldSort = sortColumnName;
                oldAscending = ascending;
            }

        }

}
