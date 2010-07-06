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
 * AddFilesPage.java
 *
 * Created on September, 2008, 3:54 PM
 * evillalon@iq.harvard.edu
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 *
 *
 * @author gdurand
 */
package edu.harvard.iq.dvn.core.web.study;

import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.io.File;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import com.icesoft.faces.component.ext.HtmlDataTable;

import javax.faces.model.SelectItem;
import java.util.*;
import com.icesoft.faces.component.inputfile.InputFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import edu.harvard.iq.dvn.core.study.StudyFileEditBean;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.FileMetadata;
import edu.harvard.iq.dvn.core.study.StudyFileServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyLock;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.util.FileUtil;
import edu.harvard.iq.dvn.core.util.StringUtil;

public class AddFilesPage extends VDCBaseBean implements java.io.Serializable {

    @EJB StudyServiceLocal studyService;
    @EJB StudyFileServiceLocal studyFileService;

    public static final Log mLog = LogFactory.getLog(AddFilesPage.class);

    private Long studyId = null;
    private Study study;
    private StudyVersion studyVersion;

    private List<StudyFileEditBean> fileList = new ArrayList();

    private InputFile inputFile = null;
    private String sessionId; // used to generate temp files
    private Collection<SelectItem> fileCategories = null; //for the drop-down list of the html page
    private List<String> preexistingLabels = new ArrayList(); // used for validation
    private List<String> currentLabels = new ArrayList<String>();// used for validation
    private int fileProgress; // TODO: file upload completed percent (Progress), currently not used!!!
    private HtmlDataTable filesDataTable = new HtmlDataTable();
    private VersionNotesPopupBean versionNotesPopup;



    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long st) {

        studyId = st;
    }

    public Study getStudy() {

        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public StudyVersion getStudyVersion() {
        return studyVersion;
    }

    public void setStudyVersion(StudyVersion studyVersion) {
        this.studyVersion = studyVersion;
    }

    public InputFile getInputFile() {
        return inputFile;
    }

    public void setInputFile(InputFile f) {
        inputFile = f;
    }

    public int getFileProgress() {
        return fileProgress;
    }

    public List<StudyFileEditBean> getFileList() {
        return fileList;
    }

    public HtmlDataTable getFilesDataTable() {
        return filesDataTable;
    }

    public void setFilesDataTable(HtmlDataTable hdt) {
        this.filesDataTable = hdt;
    }

    public VersionNotesPopupBean getVersionNotesPopup() {
        return versionNotesPopup;
    }

    public void setVersionNotesPopup(VersionNotesPopupBean versionNotesPopup) {
        this.versionNotesPopup = versionNotesPopup;
    }

    public void init() {
        super.init();
        if (isStudyLocked()) {
            return;
        }

        if (studyId != null) {
            sessionId = FacesContext.getCurrentInstance().getExternalContext().getSession(false).toString();
            study = studyService.getStudy(studyId);
            studyVersion = study.getEditVersion();

            // determines labels already in use
            for (FileMetadata fmd : studyVersion.getFileMetadatas()) {
                preexistingLabels.add(fmd.getLabel());
            }

        } else {
            // WE SHOULD HAVE A STUDY ID, throw an error
            System.out.println("ERROR: in addStudyPage, without a serviceBean or a studyId");
        }
    }

    public void uploadFile(ActionEvent event) {
        inputFile = (InputFile) event.getSource();
        StudyFileEditBean fileBean = createStudyFile(inputFile);
        fileList.add( fileBean );


        // produce FacesMessage for current upload (will be added to text during fummy validation)
        //currentUploadFacesMessage = doValidateFileName(FacesContext.getCurrentInstance(), null, fileBean.getFileMetadata().getLabel());

        /*
        int newRowIndex = getFilesDataTable().getRows();
        String clientId = "form1:dummy_datatable:0:fileDataTable:" + newRowIndex  +":input_filename";

        System.out.println("compID: " + clientId);

        String errorMessage = validateFileName2(FacesContext.getCurrentInstance(), null, fileBean.getFileMetadata().getLabel(),false);

        if (errorMessage != null) {
                FacesMessage message = new FacesMessage("Invalid File Name - " + errorMessage);
                FacesContext.getCurrentInstance().addMessage(clientId, message);
        }
        */
        /*
        String str = "";
        if (inputFile.getStatus() != InputFile.SAVED) {
            str = "Uploaded File: " + inputFile.getFileInfo().getFileName() + "\n" +
                    "InputFile Status: " + inputFile.getStatus();
            mLog.error(str);
            System.out.println(str);
            //   errorMessage(str);
            if (inputFile.getStatus() != InputFile.INVALID) {
                str = "Error saving the file. Status " + inputFile.getStatus();
                System.out.println(str);
            //     errorMessage(str);
            //return;
            }
        }
*/

    }

    private StudyFileEditBean createStudyFile(InputFile inputFile) { //"dvn" + File.separator +
        //  FileInfo info = inputFile.getFileInfo();
        StudyFileEditBean f = null;
        try {
            File dir = new File(inputFile.getFile().getParentFile(), study.getId().toString() );
            if ( !dir.exists() ) {
                dir.mkdir();
            }
            File file = FileUtil.createTempFile(dir, inputFile.getFile().getName());
            if (!inputFile.getFile().renameTo(file)) {
                // in windows environment, rename doesn't work, so we will copy the file instead
                FileUtil.copyFile(inputFile.getFile(), file);
                inputFile.getFile().delete();
            }

            //  File fstudy = FileUtil.createTempFile(sessionId, file.getName());
            f = new StudyFileEditBean(file, studyService.generateFileSystemNameSequence(),study);
            f.setSizeFormatted(file.length());

        } catch (Exception ex) {
            String m = "Fail to create the study file. ";
            mLog.error(m);
            mLog.error(ex.getMessage());
        }
        return f;
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
        InputFile ifile = (InputFile) event.getSource();
        fileProgress = ifile.getFileInfo().getPercent();


    }


    private boolean isStudyLocked() {
        Study lockedStudy = studyService.getStudy(studyId);
        StudyLock studyLock = null;
        if (studyId != null) {
            Study study = studyService.getStudy(studyId);
            studyLock = study.getStudyLock();
        }
        if (studyLock != null) {

            String studyLockMessage = "Study upload details: " + lockedStudy.getGlobalId() + " - " + studyLock.getDetail();
            redirect("/faces/login/StudyLockedPage.xhtml?message=" + studyLockMessage);
            return true;
        }

        return false;
    }


    public void openPopup(ActionEvent ae) {
        versionNotesPopup.setActionType(VersionNotesPopupBean.ActionType.ADD_FILES);
        versionNotesPopup.setVersionNote(studyVersion.getVersionNote());
        versionNotesPopup.openPopup(ae);
    }

    public String save_action() {
        studyVersion.setVersionNote(versionNotesPopup.getVersionNote());
        versionNotesPopup.setShowPopup(false);

        if (fileList.size() > 0) {
            studyFileService.addFiles(studyVersion, fileList, getVDCSessionBean().getLoginBean().getUser(), ingestEmail);
            //editStudyService.setIngestEmail(ingestEmail);
            //editStudyService.save(getVDCRequestBean().getCurrentVDCId(), getVDCSessionBean().getLoginBean().getUser().getId());
        }
        
        getVDCRequestBean().setStudyId(study.getId());
        Long versionNumber = studyService.getStudy( this.studyId).getLatestVersion().getVersionNumber();
        getVDCRequestBean().setStudyVersionNumber(versionNumber);
        getVDCRequestBean().setSelectedTab("files");

        return "viewStudy";
    }

    /**
     * Removes files from temp from temp storage 
     * @return String
     */
    public String cancel_action() {
        for (StudyFileEditBean fileBean : fileList) {
            new File(fileBean.getTempSystemFileLocation()).delete();
        }

        getVDCRequestBean().setStudyId(study.getId());
        if ( studyVersion.getId() == null ) {
            getVDCRequestBean().setStudyVersionNumber(study.getReleasedVersion().getVersionNumber());
        } else {
            getVDCRequestBean().setStudyVersionNumber(studyVersion.getVersionNumber());
        }
        getVDCRequestBean().setSelectedTab("files");       
        return "viewStudy";
    }

    public void removeFile_action(ActionEvent event) {
        int rowIndex = getFilesDataTable().getRowIndex();

        new File(fileList.get(rowIndex).getTempSystemFileLocation()).delete();
        fileList.remove(rowIndex);
        currentLabels.remove(rowIndex);
    }



    public boolean isEmailRequested() {
        for (StudyFileEditBean fileBean : fileList) {
            if (fileBean.getStudyFile().isSubsettable()) {
                return true;
            }
        }

        return false;
    }

    private String ingestEmail;

    public String getIngestEmail() {
        return ingestEmail;
    }

    public void setIngestEmail(String ingestEmail) {
        this.ingestEmail = ingestEmail;
    }



    public void validateFileName(FacesContext context, UIComponent toValidate, Object value) {

        String fileName = ((String) value).trim();
        int rowIndex = getFilesDataTable().getRowIndex();
        String removeIndexString = getRequestParam("removeAction");
        FacesMessage errorMessage = null;

        // add (or replace) name to list for validation of uniqueness
        if (currentLabels.size() < rowIndex + 1) {
            currentLabels.add(rowIndex, fileName);
        } else {
            currentLabels.set(rowIndex, fileName);
        }

        // if remove for this index was clicked, skip validation
        try {
            Integer removeIndex = Integer.valueOf( removeIndexString );
            if (removeIndex!= null && removeIndex == rowIndex) {
                return;
            }
        } catch (NumberFormatException nfe) { // do nothing, just means no remove Index was passed
        }



        // check invalid characters 
        if (fileName.contains("\\") ||
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

            errorMessage = new FacesMessage("Invalid File Name - cannot contain any of the following characters: \\ / : * ? \" < > | ; ");
        
        } else if (currentLabels.subList(0, rowIndex).contains(fileName)) { // check versus current list
            errorMessage = new FacesMessage("Invalid File Name - must be unique.");
        
        } else if (preexistingLabels.contains(fileName)) { //also check prexisting files
            errorMessage = new FacesMessage("Invalid File Name - must be unique (a previous study file exists with the same name).");
        }

        if (errorMessage != null) {
            // allow remove action to continue (i.i. don't set input to invalid
            if (StringUtil.isEmpty(removeIndexString)) {
                ((UIInput) toValidate).setValid(false);
            }

            context.addMessage(toValidate.getClientId(context), errorMessage);
        }

    }





    /**
     * 
     * @return list of SelectItem to display in the AddFilesPage.xhtml 
     */
    public Collection<SelectItem> getFileCategories() {
        if (fileCategories == null) {
            fileCategories = new ArrayList(); 
            for (String catName : studyVersion.getFileCategories()) {
                fileCategories.add( new SelectItem(catName));
            }
        }

        return fileCategories;
    }

   

}
