/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.study;

import edu.harvard.iq.dvn.core.study.EditStudyFilesService;
import edu.harvard.iq.dvn.core.study.Metadata;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlDataTable;
import java.util.ArrayList;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


/**
 *
 * @author skraffmiller
 */
@Named("EditStudyFilesPage")
@ViewScoped
@EJB(name="editStudyFiles", beanInterface=edu.harvard.iq.dvn.core.study.EditStudyFilesService.class)
public class EditStudyFilesPage extends VDCBaseBean implements java.io.Serializable {
    private EditStudyFilesService editStudyFilesService;

    @Inject private VersionNotesPopupBean versionNotesPopup;    
   
    
    public EditStudyFilesPage() {
    }

    public void init() {
        super.init();
       // HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
       // String studyIdParam=request.getParameter("studyId");
      //  this.studyId =  new Long (studyIdParam);
       // this.versionNotesPopup = new VersionNotesPopupBean();
       
        try {
            Context ctx = new InitialContext();
            editStudyFilesService = (EditStudyFilesService) ctx.lookup("java:comp/env/editStudyFiles");

        } catch (NamingException e) {
            e.printStackTrace();
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
            context.addMessage(null, errMessage);

        }
        if (getStudyId() != null) {
            editStudyFilesService.setStudyVersion(studyId);
            study = editStudyFilesService.getStudyVersion().getStudy();

            metadata = editStudyFilesService.getStudyVersion().getMetadata();
            currentTitle = metadata.getTitle();

            setFiles(editStudyFilesService.getCurrentFiles());
        }
        else {

            FacesContext context = FacesContext.getCurrentInstance();

            FacesMessage errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "The Study ID is null", null);
            context.addMessage(null, errMessage);
            //Should not get here.
            //Must always be in a study to get to this page.
        }

        // Add empty first element to subcollections, so the input text fields will be visible
        //metadata.initCollections();
        //  initDvnDates();

    }
     

    public VersionNotesPopupBean getVersionNotesPopup() {
        return versionNotesPopup;
    }

    public void setVersionNotesPopup(VersionNotesPopupBean versionNotesPopup) {
        this.versionNotesPopup = versionNotesPopup;
    }

     public void openPopup(ActionEvent ae) {
        versionNotesPopup.setActionType(VersionNotesPopupBean.ActionType.EDIT_STUDY_FILES);
        versionNotesPopup.setVersionNote(metadata.getStudyVersion().getVersionNote());
        //versionNotesPopup.setShowPopup(true);
        versionNotesPopup.openPopup(ae);
    }

    public String save() {
        metadata.getStudyVersion().setVersionNote(versionNotesPopup.getVersionNote());
        versionNotesPopup.setShowPopup(false);


        if (!StringUtil.isEmpty(metadata.getReplicationFor())  ) {
            if (!metadata.getTitle().startsWith("Replication data for:")) {
                metadata.setTitle("Replication data for: "+metadata.getTitle());
            }
        }

        editStudyFilesService.save(getVDCRequestBean().getCurrentVDCId(),getVDCSessionBean().getLoginBean().getUser().getId());

        return "/study/StudyPage?faces-redirect=true&studyId=" + study.getId()+ "&versionNumber=" + metadata.getStudyVersion().getVersionNumber() + "&tab=files&vdcId=" + getVDCRequestBean().getCurrentVDCId();

    }

    public String cancel() {
        editStudyFilesService.cancel();

        if (study==null) {
            return "myOptions";
        }

        if ( metadata.getStudyVersion().getId() == null  && study.getReleasedVersion() != null ) {
            // We are canceling the creation of a new version, so return
            // to the previous version that the user was viewing.
            if (study.isReleased()) {
                getVDCRequestBean().setStudyVersionNumber(study.getReleasedVersion().getVersionNumber());
            } else {
                // The only other option is that the study is deaccessioned
                getVDCRequestBean().setStudyVersionNumber(study.getDeaccessionedVersion().getVersionNumber());
            }
        } else {
            // We are cancelling the edit of an existing version, so just return to that version.
            getVDCRequestBean().setStudyVersionNumber(metadata.getStudyVersion().getVersionNumber());
        }


        return "/study/StudyPage?faces-redirect=true&studyId=" + study.getId()+ "&versionNumber=" + getVDCRequestBean().getStudyVersionNumber() + "&tab=files&vdcId=" + getVDCRequestBean().getCurrentVDCId();

    }


    private Long studyId;

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }



        /**
     * Holds value of property study.
     */
    private Study study;
    private Metadata metadata;
    private String currentTitle;

    /**
     * Getter for property study.
     * @return Value of property study.
     */
    public Study getStudy() {

        return this.study;
    }

    /**
     * Setter for property study.
     * @param study New value of property study.
     */
    public void setStudy(Study study) {
        this.study = study;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public String getCurrentTitle() {
        return currentTitle;
    }

    public void setCurrentTitle(String currentTitle) {
        this.currentTitle = currentTitle;
    }

    private List files;

    public List getFiles() {
        return files;
    }

    public void setFiles(List files) {
        this.files = files;
    }

   private HtmlDataTable filesDataTable = new HtmlDataTable();

    public HtmlDataTable getFilesDataTable() {
        return filesDataTable;
    }

    public void setFilesDataTable(HtmlDataTable filesDataTable) {
        this.filesDataTable = filesDataTable;
    }

    private HtmlCommandButton saveCommand1;
    private HtmlCommandButton saveCommand2;


    public HtmlCommandButton getSaveCommand1() {
        return saveCommand1;
    }

    public void setSaveCommand1(HtmlCommandButton saveCommand1) {
        this.saveCommand1 = saveCommand1;
    }

    public HtmlCommandButton getSaveCommand2() {
        return saveCommand2;
    }

    public void setSaveCommand2(HtmlCommandButton saveCommand2) {
        this.saveCommand2 = saveCommand2;
    }

private List<SelectItem> fileCategoriesItems = null;


    public List getFileCategoryItems() {
        if (fileCategoriesItems == null) {
            fileCategoriesItems = new ArrayList();
            for (String catName : editStudyFilesService.getStudyVersion().getFileCategories()) {
                fileCategoriesItems.add( new SelectItem(catName));
            }
        }

        return fileCategoriesItems;
    }

    private List validationFileNames = new ArrayList();
    
    public List getValidationFileNames() {
        return validationFileNames;
    }

   public void validateFileName(FacesContext context, UIComponent toValidate, Object value) {
        String fileName = (String) value;
        int rowIndex = getFilesDataTable().getRowIndex();
        String errorMessage = null;

        // add (or replace) name to list for validation of uniqueness
        if (validationFileNames.size() < rowIndex + 1) {
            validationFileNames.add(rowIndex, fileName);
        } else {
            validationFileNames.set(rowIndex, fileName);
        }

        // check invalid characters
        if (    fileName.contains("\\") ||
                fileName.contains("/") ||
                fileName.contains(":") ||
                fileName.contains("*") ||
                fileName.contains("?") ||
                fileName.contains("\"") ||
                fileName.contains("<") ||
                fileName.contains(">") ||
                fileName.contains("|") ||
                fileName.contains(";") ||
                fileName.contains("#")) {
            errorMessage = "cannot contain any of the following characters: \\ / : * ? \" < > | ; #";

        } else if (validationFileNames.subList(0, rowIndex).contains(fileName)) { // check versus current list
            errorMessage = errorMessage = "must be unique.";
        }



        if (errorMessage != null) {
            ((UIInput)toValidate).setValid(false);

            FacesMessage message = new FacesMessage("Invalid File Name - " + errorMessage);
            context.addMessage(toValidate.getClientId(context), message);
        }

    }

}
