/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.study;

import com.icesoft.faces.component.datapaginator.DataPaginator;
import com.icesoft.faces.component.ext.HtmlDataTable;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.web.SortableList;
import edu.harvard.iq.dvn.core.web.common.LoginBean;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
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
//    private static final String TEMPLATE_COLUMN = "template";
    private static final String DATE_RELEASED_COLUMN = "dateReleased";
    private static final String VERSION_COLUMN = "version";
    private static final String DATE_UPDATED_COLUMN = "lastUpdated";    
    private static final String ACTION_COLUMN = "actionReleased";
    private DataPaginator paginator;
   
    private Long vdcId;
    private LoginBean loginBean;

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
                orderBy = "metadata.title";
            } else if (sortColumnName.equals(ID_COLUMN)) {
                orderBy="studyId";
            } else if (sortColumnName.equals(CREATOR_COLUMN)){
                orderBy="creator.userName";
            } else if (sortColumnName.equals(DATE_CREATED_COLUMN)) {
                orderBy="createTime";
            } else if (sortColumnName.equals(STATUS_COLUMN)) {
                orderBy="reviewState.name";
//            } else if (sortColumnName.equals(TEMPLATE_COLUMN)) {
//                orderBy="template.name";
            } else if (sortColumnName.equals(DATE_UPDATED_COLUMN)) {
                orderBy="lastUpdateTime";
            } else {
                throw new RuntimeException("Unknown sortColumnName: "+sortColumnName);
            }
            List studyIds =null;
            if (loginBean!=null && loginBean.isContributor()) {
                studyIds = studyService.getDvOrderedStudyIdsByCreator(vdcId, loginBean.getUser().getId(), orderBy, ascending);
            } else {
                studyIds = studyService.getDvOrderedStudyIds(vdcId, orderBy, ascending);
            }
            studyUIList = new ArrayList<StudyUI>();
            VDCUser user = loginBean == null ? null : loginBean.getUser();
            for (Object studyId: studyIds) {
                studyUIList.add(new StudyUI((Long)studyId,user));
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
//    public String getTemplateColumn() {return TEMPLATE_COLUMN; }
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
        StudyUI studyUI = (StudyUI) this.studyDataTable.getRowData();
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

    public String release_action() {
        StudyUI studyUI = (StudyUI) this.studyDataTable.getRowData();
        StudyVersion releasedVersion = studyService.getStudyVersion(studyUI.getStudyId(), null);
        String action = null;
        if (releasedVersion != null) {
            VDCBaseBean.getVDCRequestBean().setStudyId(studyUI.getStudyId());
            VDCBaseBean.getVDCRequestBean().setStudyVersionNumberList(releasedVersion.getVersionNumber() + "," + studyUI.getStudyVersion().getNumberOfFiles());
            VDCBaseBean.getVDCRequestBean().setActionMode("confirmRelease");
            action = "diffStudy";
        } else {
            studyService.setReleased(studyUI.getStudyId());
            studyUIList=null;
            action = "";
        }

        return action;
    }

    public void review_action(){
        StudyUI studyUI = (StudyUI) this.studyDataTable.getRowData();
        studyService.setReadyForReview(studyUI.getStudyId());
        studyUIList=null;
    }

    public void delete_action(){

    }

    public String editStudy_action(){
        return "editStudy";
    }

    public void restore_action(){
        
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
                if (studyUI.getStudy().isNew() ) {
                    deleteActionLabel = "delete this draft version of the study";
                }
                else if (studyUI.getStudy().isInReview() ) {
                    deleteActionLabel = "delete this review version of the study";
                }
            }
        }
    }

    public void filter(ActionEvent event){

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
        selectItems.add(new SelectItem(1, "All studies belonging to the dataverse"));
        selectItems.add(new SelectItem(2, "Only studies I have contributed to"));
        return selectItems;
    }




}
