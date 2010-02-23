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
import edu.harvard.iq.dvn.core.study.EditStudyService;
import com.icesoft.faces.component.inputfile.InputFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.commons.fileupload.servelet.*;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import edu.harvard.iq.dvn.core.study.StudyFileEditBean;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.TemplateFileCategory;
import edu.harvard.iq.dvn.core.study.FileMetadata;
import edu.harvard.iq.dvn.core.study.StudyFileServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyLock;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.util.FileUtil;
import javax.faces.event.ValueChangeEvent;

public class AddFilesPage extends VDCBaseBean implements java.io.Serializable {

    @EJB EditStudyService editStudyService;
    @EJB StudyServiceLocal studyService;
    @EJB StudyFileServiceLocal studyFileService;

    public static final Log mLog = LogFactory.getLog(AddFilesPage.class);


    private String sessionId;
    private Long studyId = null;
    private List<StudyFileEditBean> fileList = new ArrayList();




    // latest file uploaded by client

    // file upload completed percent (Progress)
    private int fileProgress;
    //for the drop-down list of the html page 
    private Collection<SelectItem> fileCategories = null;
    Collection<StudyFile> objStudyFiles;
    private String[] studyFileNames = null;
    private InputFile inputFile = null;




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

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long st) {

        studyId = st;
    }

    /**
     * Original code
     */
    public void init() {
        super.init();
        if (isStudyLocked()) {
            return;
        }

        if (studyId != null) {
            sessionId = FacesContext.getCurrentInstance().getExternalContext().getSession(false).toString();
            study = studyService.getStudy(studyId);
            studyVersion = study.getEditVersion();
        } else {
            // WE SHOULD HAVE A STUDY ID, throw an error
            System.out.println("ERROR: in addStudyPage, without a serviceBean or a studyId");
        }
 
       /*
        if (isFromPage("AddFilesPage")) {
            editStudyService = (EditStudyService) sessionGet(editStudyService.getClass().getName());
            study = editStudyService.getStudy();
            fileList = editStudyService.getNewFiles();

        } else {
            // we need to create the studyServiceBean

            if (studyId != null) {

                editStudyService.setStudy(studyId);
                //sessionPut(editStudyService.getClass().getName(), editStudyService);
                sessionPut( (studyService.getClass().getName() + "."  + studyId.toString()), studyService);
                study = editStudyService.getStudy();
                fileList = editStudyService.getNewFiles();

            } else {
                // WE SHOULD HAVE A STUDY ID, throw an error
                System.out.println("ERROR: in addStudyPage, without a serviceBean or a studyId");
            }

        }*/
    }


    public void uploadFile(ActionEvent event) {
        inputFile = (InputFile) event.getSource();
        fileList.add(createStudyFile(inputFile) );

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


        currentFile = createStudyFile(inputFile);
        if (currentFile == null) {
            str = "StudyFileEditBean cannot be created ";
            mLog.error(str);
            errorMessage(str);
            return;
        }
        if (!currentFile.getOriginalFileName().equals(currentFile.getStudyFile().getFileName())) {
            str = "StudyFileEditBean original name differs from study file name";
            mLog.error(str);
        //  errorMessage(str);
        }

        // check if the file is in the data table already with the same name, i.e.
        // hasFileName checks if currentFile name is in getValidationFileNames
        //and if del=true removes it from validation names
        boolean del = true;

        boolean b = hasFileName(currentFile.getStudyFile().getFileName(), del);
        if (b) {
            str = "File " + currentFile.getStudyFile().getFileName() + " is already in the table";
            mLog.error(str);
            errorMessage(str);
            if (!del) {
                return;
            }


        }
        //add it to the validation names
        getValidationFileNames().add(currentFile.getStudyFile().getFileName().trim());


        fileList.add(currentFile);
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
            errorMessage(m);
            mLog.error(ex.getMessage());
        }
        return f;
    }



    private void errorMessage(String str) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage message = new FacesMessage(str);
        UIInput out = new UIInput();
        out.setValid(false);

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

    /**
     * <p>Allows a user to remove one file from a list of uploaded files.  This
     * methods assumes that a request param "fileName" has been set to a valid
     * file name that the user wishes to remove or delete</p>
     *
     * @param event jsf action event
     */
    /*
    public void removeUploadedFile(ActionEvent event) {
        // Get the inventory item ID from the context.
        FacesContext context = FacesContext.getCurrentInstance();
        Map map = context.getExternalContext().getRequestParameterMap();
        String fileName = (String) map.get("fileName");

        boolean found = removeFromFileLst(fileName);
        //remove from the validation names
        if (found) {
            hasFileName(fileName, true);
        }

    }
*/
    /**
     * The file name is in the data table collection, then remove it. 
     * @param fname String with file name
     * @return boolean if it ghas been removed from collection of files in data table
     */
    /*
    public boolean removeFromFileLst(String fname) {
        StudyFileEditBean inputFileData = null;
        boolean found = false;
        synchronized (fileList) {

            Iterator<StudyFileEditBean> theit = fileList.iterator();

            while (theit.hasNext()) {
                inputFileData = theit.next();
                if ((inputFileData.getStudyFile().getFileName().trim()).equals(fname.trim())) {
                    found = true;
                    theit.remove();
                    break;
                }
            }
        }
        return found;
    }
     * */

    /**
     *
     * @param inputFileData StudyFileEditBean
     * @param remov boolean whether to remove it from validation file names
     * @return boolean
     */
    private boolean hasFileName(StudyFileEditBean inputFileData, boolean remov) {
        boolean isin = false;
        if (getValidationFileNames().size() <= 0) {
            return isin;
        }

        String fname = inputFileData.getStudyFile().getFileName();
        return hasFileName(fname, remov);

    }

    /**
     * It is used to remove a file name from the validation list
     * @param fname String with file name
     * @param remov boolean whether to remove it from validation file names
     * @return boolean
     */
    private boolean hasFileName(String fname, boolean remov) {
        boolean isin = false;
        Iterator<String> iter = getValidationFileNames().iterator();
        while (iter.hasNext()) {

            if (fname.trim().equals(iter.next().trim()) && !isin) {
                if (remov) {
                    iter.remove();
                }
                isin = true;
            }
        }
        return isin;
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

    /**
     * Helper function to init: It obtaines the collection of files 
     * stored in the Study and also builds a collection with the names of the files.
     * Calls buildCategories as well.
     * @param mess String to indicate which method is calling this function
     */
    public void existentFiles(String mess) {
        objStudyFiles = study.getStudyFiles();
        if (objStudyFiles == null || objStudyFiles.size() <= 0) {
            objStudyFiles = new ArrayList<StudyFile>();
        }

        Iterator<StudyFile> itfl = objStudyFiles.iterator();
        Collection<String> studyFiles = new ArrayList<String>();
        while (itfl.hasNext()) {
            studyFiles.add(itfl.next().getFileName().trim());
        }
        //make the files names in the Study unique
        Collection<String> noDups = new HashSet<String>(studyFiles);
        int sz = noDups.size();
        //the names for existent files in the Study stored in array of String
        studyFileNames = noDups.toArray(new String[sz]);
        Arrays.sort(studyFileNames);
        for (int n = 0; n < sz; ++n) {
            mLog.debug(mess + "/nFile stored are " + studyFileNames[n]);
        }

    }

    private Study study;
    private StudyVersion studyVersion;

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



    /**
     * Action method that saves the files in the data table
     * @return String for neext view
     */
    public String save_action() {
        /*
        //any files in storage with the same file names
        if (studyFileNames == null) {
            existentFiles("From Save");
        }
        if (studyFileNames != null && studyFileNames.length > 0) {

            Iterator<StudyFileEditBean> edbean = fileList.iterator();
            while (edbean.hasNext()) {

                StudyFileEditBean tmp = edbean.next();
                StudyFile sf = tmp.getStudyFile();
                String nm = sf.getFileName();
                int found = Arrays.binarySearch(studyFileNames, nm);

                //mLog.debug(tmp.getStudyFile().getDescription() + "; File Description");
                //if the file name exist remove it from temp directory and do not store
                if (found >= 0) {
                    edbean.remove();
                    removeUploadFiles(nm);
                }

            }
        }
        for (int n = 0; n < fileList.size(); ++n) {
            mLog.debug(fileList.get(n).getStudyFile().getFileName());
        }
        if (detectDuplicates()) {
            String m = "Duplicate file names are not allowed";
            System.out.println(m);
            errorMessage(m);
            return null;
        }
         */
        // now call save
        if (fileList.size() > 0) {
            studyFileService.addFiles(studyVersion, fileList, getVDCSessionBean().getLoginBean().getUser(), ingestEmail);
            //editStudyService.setIngestEmail(ingestEmail);
            //editStudyService.save(getVDCRequestBean().getCurrentVDCId(), getVDCSessionBean().getLoginBean().getUser().getId());
        }
        
        getVDCRequestBean().setStudyId(study.getId());
        getVDCRequestBean().setStudyVersionNumber(studyVersion.getVersionNumber());
        getVDCRequestBean().setSelectedTab("files");

        return "viewStudy";
    }
    // detect duplicate file names in the validation and data table lists
    /*
    private boolean detectDuplicates() {
        return (duplicatesValidationNames() || duplicatesDataListNames());
    }
    //find duplicate strings in the validation list

    private boolean duplicatesValidationNames() {

        int sz0 = getValidationFileNames().size();
        if (sz0 <= 0) {
            return false;
        }
        Collection<String> noDups = new HashSet<String>(getValidationFileNames());
        int sz1 = noDups.size();
        boolean res = sz0 != sz1;
        if (res) {
            getValidationFileNames().clear();
            getValidationFileNames().addAll(noDups);
        }
        return res;
    }

    //find duplitcate file names in the data table list
    private boolean duplicatesDataListNames() {

        if (fileList.size() <= 0) {
            return false;
        }
        Iterator<StudyFileEditBean> it = fileList.iterator();
        List<String> names = new ArrayList<String>();
        while (it.hasNext()) {
            names.add((it.next()).getStudyFile().getFileName().trim());
        }
        int sz0 = names.size();
        Collection<String> noDups = new HashSet<String>(names);
        int sz1 = noDups.size();

        return (sz0 != sz1);
    }
     */

    /**
     * Removes files from temp from temp storage 
     * @return String
     */
    public String cancel_action() {
        //first clean up the temp files  
        //removeUploadFiles();
        //editStudyService.cancel();
        getVDCRequestBean().setStudyId(study.getId());
        getVDCRequestBean().setStudyVersionNumber(study.getLatestVersion().getVersionNumber());
        getVDCRequestBean().setSelectedTab("files");
        return "viewStudy";
    }

    /**
     * Find the absolute path for the temporary directory where the 
     * files are being uploaded 
     */
    public File directoryToUpload(String nm) {
//        if (currentFile == null) {
  //          return null;
    //    }
        return null;
        //get the path to the temporary directory in web.xml = upload
/*
        String p = currentFile.getTempSystemFileLocation().trim();

        mLog.debug("File Location " + p);
        if (nm == null) {
            nm = currentFile.getOriginalFileName();
        }
        mLog.debug("Name " + nm);
        String fp = p.replaceAll(nm, "");
        int ln = fp.length();
        //the absolute path to the temp directory in web.xml
        String sessionFileUploadPath = fp.substring(0, ln - 1);
        mLog.debug("upload dir " + sessionFileUploadPath);
        mLog.debug("Temp dir is..." + fp.substring(0, ln - 1));
        return new File(sessionFileUploadPath);
*/

    }
/*
    public void removeUploadFiles() {
        removeUploadFiles(null);
    }
*/
    /**
     * Remove all temp files from the upload directory (null) or only filenm0
     * filenm0 String or null
     **/
    /*
    public void removeUploadFiles(String filenm0) {
        File sessionfileUploadDirectory = directoryToUpload(null);
        if (sessionfileUploadDirectory == null) {
            mLog.debug("I am a null from removeUploadFiles");
            return;
        }

        File[] filein = sessionfileUploadDirectory.listFiles();
        mLog.debug("No files in dir is..." + filein.length);
        for (int n = 0; n < filein.length; ++n) {
            String fname = filein[n].getName();
            mLog.debug("Contains file..." + filein[n].getName() + "...." + filein[n].isFile());
            if (filenm0 != null && !(fname.equals(filenm0))) {
                continue;
            }
            if (getValidationFileNames().contains(fname)) {

                //remove from validation names
                hasFileName(fname, true);
                //remove from the backing dataTable list
                boolean found = removeFromFileLst(fname);
                if (found) {
                    mLog.debug("Deleting file..." + fname);
                }
            }

            boolean res = filein[n].delete();
            if (!res) {
                filein[n].delete();
            }
            mLog.debug("Deleted " + res + "; the file " + filein[n]);

        }
    }
    */
    public List<SelectItem> getTemplateFileCategories() {
        List<SelectItem> tfc = new ArrayList<SelectItem>();
        Iterator<TemplateFileCategory> iter = study.getTemplate().getTemplateFileCategories().iterator();
        while (iter.hasNext()) {
            tfc.add(new SelectItem(iter.next().getName()));
        }
        return tfc;
    }

    /*
    public boolean isProgressRequested() {

        return currentFile != null;

    }
     * */

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

    public void validateFileName(FacesContext context,
            UIComponent toValidate,
            Object value) {
            /*
        if (value == null) {
            return;
        }
        String errorMessage = null;
        if (((UIInput) toValidate) != null) {
            ((UIInput) toValidate).setValid(true);
        }

        if (toValidate != null) {
            Map<String, Object> comp = toValidate.getAttributes();
            Set<String> ss = comp.keySet();


        }
        int inx = getFilesDataTable().getRowIndex();
        // mLog.debug("I am in row..."+inx+";;;"+fileName);
        //this is the original file name for the present row in the table
        String or = fileList.get(inx).getStudyFile().getFileName();
        String fname = or;
        //the new file name assigned to the same row inx
        String fileName = ((String) value).trim();
        if (fileName.equals("") || fileName == null) {
            errorMessage = "Enter a valid file name";
            displayError(context, (UIInput) toValidate, errorMessage);

        }
        // check invalid characters 

        //mLog.debug(currentFile.getOriginalFileName());
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
            errorMessage = "cannot contain any of the following characters: \\ / : * ? \" < > | ; ";
            displayError(context, (UIInput) toValidate, errorMessage);

        }

        // also check prexisting files
        if (errorMessage == null && studyFileNames != null && studyFileNames.length > 0) {
            for (String dummy : studyFileNames) {
                if (fileName.equals(dummy)) {
                    errorMessage = "must be unique (a previous study file exists with the same name).";
                    displayError(context, (UIInput) toValidate, errorMessage);
                    break;

                }
            }
        }

        //make sure there are no other files with the same name in the validation list
        //assuming that the original file name in the row is different from fileName
        if (!fileName.equals(fname) && hasFileName(fileName, true)) {
            errorMessage = "must be unique (a previous file in the table exists with same name).";
            displayError(context, (UIInput) toValidate, errorMessage);
        }
        //check if  other rows have the same names, assuming the row file name has not changed
        int rwcount = getFilesDataTable().getRowCount();
        if (fileName.equals(fname)) {
            for (int n = 0; n < (rwcount - 1); ++n) {
                if (n == inx) {
                    continue;
                }
                String otherfile = fileList.get(n).getStudyFile().getFileName().trim();
                if (otherfile.equals(fileName)) {
                    errorMessage = "must be unique (a previous file in the table exists with same name).";
                    displayError(context, (UIInput) toValidate, errorMessage);
                }
            }
        }
        // now add this name to the validation list
        //fileList.get(inx).getStudyFile().setFileName(fileName);
        getValidationFileNames().add(fileName.trim());
        //remove the old name from validation names
        hasFileName(fname, true);
*/
    }

    private void displayError(FacesContext context, UIInput toValidate, String errorMessage) {

        if (errorMessage != null) {
            ((UIInput) toValidate).setValid(false);

            FacesMessage message = new FacesMessage("Invalid File Name - " + errorMessage);
            context.addMessage(toValidate.getClientId(context), message);
            errorMessage = null;
            return;

        }
    }
    private List<String> validationFileNames = new ArrayList<String>();

    public List<String> getValidationFileNames() {
        return validationFileNames;
    }
    private HtmlDataTable filesDataTable = new HtmlDataTable();

    public HtmlDataTable getFilesDataTable() {
        return filesDataTable;
    }

    public void setFilesDataTable(HtmlDataTable hdt) {
        this.filesDataTable = hdt;
    }

    /**
     * 
     * @return list of SelectItem to display in the AddFilesPage.xhtml 
     */
    public Collection<SelectItem> getFileCategories() {
        if (fileCategories == null) {
            fileCategories = new ArrayList();
            if (study != null) {
                StudyVersion sv = study.getStudyVersions().get(0);

                for (FileMetadata fmd : sv.getFileMetadatas()) {
                fileCategories.add( new SelectItem( (fmd.getCategory()) ) );
                }
                 //Collections.sort(fileCats);
            }
        }

        return fileCategories;
    }


    private String fileCategoryName = null;

    public String getFileCategoryName() {
        return fileCategoryName;
    }

    public void setFileCategoryName(String key) {
        mLog.debug("Category name..." + key);
        fileCategoryName = key.trim();


    }

    public void addCategory(ValueChangeEvent e) {
        //currentFile.setFileCategoryName(((String) e.getNewValue()).trim());
    }

}
