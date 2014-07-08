/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
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
import java.io.FileInputStream; 
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException; 
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.event.ValueChangeEvent;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.component.menubar.MenuItem;

import com.icesoft.faces.context.effects.JavascriptContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import java.util.*;
import java.util.logging.*;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry; 
import org.icefaces.component.fileentry.*;
//import com.icesoft.faces.component.inputfile.InputFile;

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

//import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.*;
//import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata.*;
import edu.harvard.iq.dvn.ingest.dsb.DSBWrapper;
import edu.harvard.iq.dvn.core.study.TabularDataFile;
import edu.harvard.iq.dvn.core.study.SpecialOtherFile; 
import java.util.zip.GZIPInputStream;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2Utils;
import org.apache.commons.compress.compressors.gzip.GzipUtils;
import org.apache.commons.io.FileUtils;

@ViewScoped
@Named("AddFilesPage")
public class AddFilesPage extends VDCBaseBean implements java.io.Serializable {

    @EJB StudyServiceLocal studyService;
    @EJB StudyFileServiceLocal studyFileService;
    
    @Inject private VersionNotesPopupBean versionNotesPopup;

    //public static final Log mLog = LogFactory.getLog(AddFilesPage.class);
    private static final Logger dbgLog = Logger.getLogger(AddFilesPage.class.getPackage().getName());

    private Long studyId = null;
    private Study study;
    private StudyVersion studyVersion;

    private List<StudyFileEditBean> fileList = new ArrayList();

    //private InputFile inputFile = null;

    private String controlCardFilename = null;
    private String controlCardTempFileLocation = null;
    private String controlCardType = ""; // SPSS, DDI, etc.
    private String controlCardValidationErrorMessage = null;

    private String tarValidationErrorMessage = null;

    private boolean controlCardIngestInProgress = false;


    private String sessionId; // used to generate temp files

    private Collection<SelectItem> fileCategories = null; //for the drop-down list of the html page

    // The selectItems (and groups) below are for the drop-down menu
    // of the supported file types:
    private Collection<SelectItem> fileTypes = null;
    // The [new!] list for the character encoding dropdown:
    private List<MenuItem> characterEncodings = null; 
        
    private SelectItem[] fileTypesSubsettable;
    private SelectItem[] fileTypesNetwork;

    private List<String> preexistingLabels = new ArrayList(); // used for validation
    private List<String> currentLabels = new ArrayList<String>();// used for validation
    private int fileProgress; // TODO: file upload completed percent (Progress), currently not used!!!
    private HtmlDataTable filesDataTable = new HtmlDataTable();
    //private VersionNotesPopupBean versionNotesPopup;
    
    private String dataLanguageEncoding = null; 


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

    //public InputFile getInputFile() {
    //    return inputFile;
    //}

    //public void setInputFile(InputFile f) {
    //    inputFile = f;
    //}

    public String getControlCardFilename () {
        return controlCardFilename;
    }

    public void setControlCardFilename (String ccf) {
        controlCardFilename = ccf;
    }

    public String getControlCardTempFileLocation () {
        return controlCardTempFileLocation;
    }

    public String getControlCardValidationErrorMessage () {
        return controlCardValidationErrorMessage;
    }

    public void setControlCardValidationErrorMessage (String msg) {
        controlCardValidationErrorMessage = msg;
    }

    public void setControlCardTempFileLocation (String ccf) {
        controlCardTempFileLocation = ccf;
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

    HtmlSelectOneMenu selectFileType;

    public HtmlSelectOneMenu getSelectFileType() {
        return selectFileType;
    }

    public void setSelectFileType(HtmlSelectOneMenu selectFileType) {
        setTarValidationErrorMessage(null);
        this.selectFileType = selectFileType;
    }
    
    public String getDataLanguageEncoding() {
        return dataLanguageEncoding;
    }

    public void setDataLanguageEncoding(String dataLanguageEncoding) {
        this.dataLanguageEncoding = dataLanguageEncoding;
    }

    public String getTarValidationErrorMessage() {
        return tarValidationErrorMessage;
    }

    public void setTarValidationErrorMessage(String tarValidationErrorMessage) {
        this.tarValidationErrorMessage = tarValidationErrorMessage;
    }

    public void preRenderView() {
        super.preRenderView();
        // add javascript call on each partial submit to the file browser
        JavascriptContext.addJavascriptCall(getFacesContext(), "jQuery('input[type=file]').change(function(){ clickHiddenAddFileButton(); });");
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
            
            
            // set the initial value for the ingest e-mail
            ingestEmail = getVDCSessionBean().getUser().getEmail();

        } else {
            // WE SHOULD HAVE A STUDY ID, throw an error
            System.out.println("ERROR: in addStudyPage, without a serviceBean or a studyId");
        }
    }

    public void encodingListener(ActionEvent e) {
        dbgLog.fine("entering encoding listener");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        dbgLog.fine("encoding listener: got faces context");
        Map params = facesContext.getExternalContext().getRequestParameterMap();
        dbgLog.fine("encoding listener: got request parameter map");
        String encoding = (String) params.get("characterEncoding");
        if (encoding != null && encoding.length() > 0) {
            dbgLog.fine("setting character encoding to "+encoding);
            setDataLanguageEncoding(encoding);
        } 
    }

    public void uploadFileListener(FileEntryEvent fileEvent) {
        
        File uploadedFile = null; 
        
        FileEntry fe = (FileEntry)fileEvent.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        StringBuilder m = null;

        for (FileEntryResults.FileInfo i : results.getFiles()) {
            //Note that the fileentry component has capabilities for 
            //simultaneous uploads of multiple files.
            
            uploadedFile = i.getFile(); 
        }                                                          

 
        
        StudyFileEditBean fileBean = null;

        if ( ("spss".equals(selectFileType.getValue()) || "ddi".equals(selectFileType.getValue()) || "porextra".equals(selectFileType.getValue())) && (!controlCardIngestInProgress)) {
            // This is a 2 step process:
            // First (in this step) they upload the control card;
            // we store its file bean for the next step, where
            // they will upload the CSV file.

            // Attempt to save the control card in a temporary location:

            File controlCardFile = saveControlCardFile (uploadedFile);

            if (controlCardFile != null ) {
                // Note that we are now validating uploaded control cards at this point.
                // no need to waste time even uploading the raw data file -- which
                // can be significantly bigger than the control card -- if the
                // card is not valid.
                controlCardType = selectFileType.getValue().toString();

                try {
                    // validate:
                    DSBWrapper.validateControlCard(controlCardFile, controlCardType);
                    // if it didn't throw an exception, the file is ok.                     

                    // Save the filenames, we'll need them in the next step:
                    setControlCardFilename(controlCardFile.getName());
                    setControlCardTempFileLocation(controlCardFile.getAbsolutePath());

                    // And set the flag indicating that a control card ingest
                    // is in progress:

                    controlCardIngestInProgress = true;
                    controlCardType = selectFileType.getValue().toString();
                    setControlCardValidationErrorMessage(null);
                    
                } catch (Exception ex) {
                    setControlCardFilename("");
                    controlCardIngestInProgress = false;
                    controlCardType = null; 

                    String errMsg = ex.getMessage();

                    if (errMsg == null || errMsg.equals("")) {
                        errMsg = "Ingest could not read or parse the control card you supplied (no further diagnostics is available);"+
                                "please check your control card and try again.";
                    }

                    setControlCardValidationErrorMessage(errMsg);
                }



            } else {
                // Something went wrong as we tried to save the file;
                // Reset the state of the process:
 
                setControlCardFilename("");
                controlCardIngestInProgress = false;
            }

        } else if (controlCardIngestInProgress) {
            // This is step 2 of the CSV/TAB upload.
            // We had the control card uploaded in the previous step.
            // Now they have uploaded the CSV file.

            fileBean = createStudyFile(uploadedFile, getControlCardTempFileLocation());

            // Enable further uploads of more files by resetting the state
            // of the upload process:
            
            controlCardIngestInProgress = false;
            controlCardType = null;

            // Add the fileBean to the list:
            fileList.add( fileBean );
        } else if ("multizip".equals(selectFileType.getValue())) {
            List <StudyFileEditBean> fileBeanList = null;
            
            fileBeanList = createStudyFilesFromZip (uploadedFile);
            
            for (StudyFileEditBean fb : fileBeanList) {
                fileList.add(fb);
            }
            
        } else if ("multitar".equals(selectFileType.getValue())) {
            List<StudyFileEditBean> fileBeanList = null;

            fileBeanList = createStudyFilesFromTar(uploadedFile);

            for (StudyFileEditBean fb : fileBeanList) {
                fileList.add(fb);
            }

        } else {
            // This is a simple, 1-file ingest, we simply add the file bean
            // to the file list:
            fileBean = createStudyFile(uploadedFile, null);
            fileList.add( fileBean );
        }

        // TODO: figure out what's going on in the validation message code
        // commented out below; 
        // Is this something we need to re-implement? 

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
    
    private File saveControlCardFile(File uploadedInputFile) {
        File file = null;
        File dir = new File(uploadedInputFile.getParentFile(), study.getId().toString() );
        if ( !dir.exists() ) {
            dir.mkdir();
        }
        try {
            file = FileUtil.createTempFile(dir, uploadedInputFile.getName());

            if (!uploadedInputFile.renameTo(file)) {
                // in windows environment, rename doesn't work, so we will copy the file instead
                FileUtil.copyFile(uploadedInputFile, file);
                uploadedInputFile.delete();
            }
        } catch (Exception ex) {
            dbgLog.warning("Fail to create study file.");
            dbgLog.warning(ex.getMessage());

            return null;
        }

        return file;
    }

    private StudyFileEditBean createStudyFile(File uploadedInputFile, String controlCardTempLocation) { 
        //  FileInfo info = inputFile.getFileInfo();
        StudyFileEditBean f = null;
        try {
            File dir = new File(uploadedInputFile.getParentFile(), study.getId().toString() );
            if ( !dir.exists() ) {
                dir.mkdir();
            }
            File file = FileUtil.createTempFile(dir, uploadedInputFile.getName());
            if (!uploadedInputFile.renameTo(file)) {
                // in windows environment, rename doesn't work, so we will copy the file instead
                FileUtil.copyFile(uploadedInputFile, file);
                uploadedInputFile.delete();
            }

            //  File fstudy = FileUtil.createTempFile(sessionId, file.getName());
            if (controlCardTempLocation != null) {
                 if ("porextra".equals(controlCardType)) {
                     Map<String,String> varLabelMap = null; 
                     varLabelMap = createLabelMap (controlCardTempLocation);
                     f = new StudyFileEditBean(file, studyService.generateFileSystemNameSequence(), study);
                     if (f != null && f.getStudyFile() instanceof TabularDataFile && varLabelMap != null) {
                         f.setExtendedVariableLabelMap(varLabelMap);
                     }
                     
                 } else {   
                    f = new StudyFileEditBean(file, studyService.generateFileSystemNameSequence(), study,controlCardTempLocation, controlCardType);
                 }
            } else if ("other".equals(selectFileType.getValue())) {
                // Forced ingest as non-subsettable, even if it is a potentially subsettable type.
                f = new StudyFileEditBean(file, studyService.generateFileSystemNameSequence(), study, true);
            } else {
                f = new StudyFileEditBean(file, studyService.generateFileSystemNameSequence(), study);
            }

            f.setSizeFormatted(file.length());
            
            if (dataLanguageEncoding != null && !(dataLanguageEncoding.equals(""))) {
                f.setDataLanguageEncoding(dataLanguageEncoding);
            }

        } catch (Exception ex) {
            String m = "Failed to create the study file. ";
            dbgLog.warning(m);
            dbgLog.warning(ex.getMessage());
        }
        return f;
    }

    private Map<String,String> createLabelMap (String extendedLabelsFileLocation) {
        Map<String,String> varLabelMap = new HashMap<String,String>(); 
        
        // Simply open the text file supplied, and read the variable-lable 
        // pairs supplied: 
        
        BufferedReader labelsFileReader = null;
        
        try {
            labelsFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(extendedLabelsFileLocation)));
            
            String inLine = null; 
            String[] valueTokens = new String[2];
            
            while ((inLine = labelsFileReader.readLine() ) != null) {
                valueTokens = inLine.split("\t", 2);
                
                if (valueTokens[0] != null && !"".equals(valueTokens[0]) &&
                    valueTokens[1] != null && !"".equals(valueTokens[1])) {
                    
                    valueTokens[1] = valueTokens[1].replaceAll("[\n\r]", ""); 
                    varLabelMap.put(valueTokens[0], valueTokens[1]);
                }
            }
            
        } catch (java.io.FileNotFoundException fnfex) {
            dbgLog.warning("Ingest: could not open Extended Labels file");
            dbgLog.warning(fnfex.getMessage());
            return null; 
        } catch (IOException ioex) {
            dbgLog.warning("Ingest: caught exception trying to process Labels File");
            dbgLog.warning(ioex.getMessage());
            return null;
        } finally {
            if (labelsFileReader != null) {
                try {labelsFileReader.close();}catch(Exception x){};
            }
        }
        
        return varLabelMap;
    }

    private List <StudyFileEditBean> createStudyFilesFromZip(File uploadedInputFile) {
        List <StudyFileEditBean> fbList = new ArrayList <StudyFileEditBean>();
        
        // This is a Zip archive that we want to unpack, then upload/ingest individual
        // files separately
        
        ZipInputStream ziStream = null; 
        ZipEntry zEntry = null; 
        FileOutputStream tempOutStream = null; 
        
        try {
            // Create ingest directory for the study: 
            File dir = new File(uploadedInputFile.getParentFile(), study.getId().toString() );
            if ( !dir.exists() ) {
                dir.mkdir();
            }
            
            // Open Zip stream: 
            
            ziStream = new ZipInputStream (new FileInputStream (uploadedInputFile));
            
            if (ziStream == null) {
                return null; 
            }
            while ((zEntry = ziStream.getNextEntry()) != null) {
                // Note that some zip entries may be directories - we 
                // simply skip them:
                if (!zEntry.isDirectory()) {
                
                    String fileEntryName = zEntry.getName();
                    
                    if (fileEntryName != null && !fileEntryName.equals("")) {
                        
                        String dirName = null;
                        String finalFileName = null; 
                        
                        int ind = fileEntryName.lastIndexOf('/');
                        
                        if (ind > -1) {
                            finalFileName = fileEntryName.substring(ind+1);
                            if (ind > 0) {
                                dirName = fileEntryName.substring(0, ind);
                                dirName = dirName.replace('/', '-');
                            }   
                        } else {
                            finalFileName = fileEntryName; 
                        }
                    
                        // http://superuser.com/questions/212896/is-there-any-way-to-prevent-a-mac-from-creating-dot-underscore-files
                        if (!finalFileName.startsWith("._")) {
                            File tempUploadedFile = FileUtil.createTempFile(dir, finalFileName);

                            tempOutStream = new FileOutputStream(tempUploadedFile);

                            byte[] dataBuffer = new byte[8192];
                            int i = 0;

                            while ((i = ziStream.read(dataBuffer)) > 0) {
                                tempOutStream.write(dataBuffer, 0, i);
                                tempOutStream.flush();
                            }

                            tempOutStream.close();

                            // We now have the unzipped file saved in the upload directory;


                            StudyFileEditBean tempFileBean = new StudyFileEditBean(tempUploadedFile, studyService.generateFileSystemNameSequence(), study);
                            tempFileBean.setSizeFormatted(tempUploadedFile.length());

                            // And, if this file was in a legit (non-null) directory, 
                            // we'll use its name as the file category: 

                            if (dirName != null) {
                                tempFileBean.getFileMetadata().setCategory(dirName);
                            }

                            fbList.add(tempFileBean);
                        }
                    }
                }
                ziStream.closeEntry(); 

            }
            
        } catch (Exception ex) {
            String msg = "Failed ot unpack Zip file/create individual study files";
            
            dbgLog.warning(msg); 
            dbgLog.warning(ex.getMessage());
            
            //return null; 
        } finally {
            if (ziStream != null) {
                try {ziStream.close();} catch (Exception zEx) {}
            }
            if (tempOutStream != null) {
                try {tempOutStream.close();} catch (Exception ioEx) {}
            }
        }
        
        // should we delete uploadedInputFile before return?
        return fbList; 
    }

    // large portions copied from createStudyFilesFromZip
    private List<StudyFileEditBean> createStudyFilesFromTar(File uploadedInputFile) {
        List<StudyFileEditBean> fbList = new ArrayList<StudyFileEditBean>();

        File dir = new File(uploadedInputFile.getParentFile(), study.getId().toString());
        if (!dir.exists()) {
            dir.mkdir();
        }

        List<File> directoriesToDelete = new ArrayList<File>();
        File unzippedFile = null;
        TarArchiveInputStream tiStream = null;
        FileOutputStream tempOutStream = null;
        String unzipError = "";
        if (GzipUtils.isCompressedFilename(uploadedInputFile.getName())) {
            try {
                GZIPInputStream zippedInput = new GZIPInputStream(new FileInputStream(uploadedInputFile));
                unzippedFile = new File(dir, "unzipped-file-" + UUID.randomUUID());
                FileOutputStream unzippedOutput = new FileOutputStream(unzippedFile);
                byte[] dataBuffer = new byte[8192];
                int i = 0;
                while ((i = zippedInput.read(dataBuffer)) > 0) {
                    unzippedOutput.write(dataBuffer, 0, i);
                    unzippedOutput.flush();
                }
                tiStream = new TarArchiveInputStream(new FileInputStream(unzippedFile));
            } catch (Exception ex) {
                unzipError = " A common gzip extension was found but is the file corrupt?";
            }
        } else if (BZip2Utils.isCompressedFilename(uploadedInputFile.getName())) {
            try {
                BZip2CompressorInputStream zippedInput = new BZip2CompressorInputStream(new FileInputStream(uploadedInputFile));
                unzippedFile = new File(dir, "unzipped-file-" + UUID.randomUUID());
                FileOutputStream unzippedOutput = new FileOutputStream(unzippedFile);
                byte[] dataBuffer = new byte[8192];
                int i = 0;
                while ((i = zippedInput.read(dataBuffer)) > 0) {
                    unzippedOutput.write(dataBuffer, 0, i);
                    unzippedOutput.flush();
                }
                tiStream = new TarArchiveInputStream(new FileInputStream(unzippedFile));
            } catch (Exception ex) {
                unzipError = " A common bzip2 extension was found but is the file corrupt?";
            }
        } else {
            try {
                // no need to decompress, carry on
                tiStream = new TarArchiveInputStream(new FileInputStream(uploadedInputFile));
            } catch (FileNotFoundException ex) {
                unzipError = " Is the tar file corrupt?";
            }
        }

        TarArchiveEntry tEntry = null;

        if (tiStream == null) {
            String msg = "Problem reading uploaded file." + unzipError;
            setTarValidationErrorMessage(msg);
            uploadedInputFile.delete();
            fbList = new ArrayList<StudyFileEditBean>();
            return fbList;
        }
        try {
            while ((tEntry = tiStream.getNextTarEntry()) != null) {

                String fileEntryName = tEntry.getName();

                if (!tEntry.isDirectory()) {

                    if (fileEntryName != null && !fileEntryName.equals("")) {

                        String dirName = null;
                        String finalFileName = fileEntryName;

                        int ind = fileEntryName.lastIndexOf('/');

                        if (ind > -1) {
                            finalFileName = fileEntryName.substring(ind + 1);
                            if (ind > 0) {
                                dirName = fileEntryName.substring(0, ind);
                                dirName = dirName.replace('/', '-');
                            }
                        } else {
                            finalFileName = fileEntryName;
                        }

                        // only process normal tar entries, not the ones that start with "._" because they were created on a mac:
                        // http://superuser.com/questions/61185/why-do-i-get-files-like-foo-in-my-tarball-on-os-x
                        // http://superuser.com/questions/212896/is-there-any-way-to-prevent-a-mac-from-creating-dot-underscore-files
                        if (!finalFileName.startsWith("._")) {

                            File tempUploadedFile = null;
                            try {
                                tempUploadedFile = FileUtil.createTempFile(dir, finalFileName);
                            } catch (Exception ex) {
                                Logger.getLogger(AddFilesPage.class.getName()).log(Level.SEVERE, null, ex);
                                String msg = "Problem creating temporary file.";
                                setTarValidationErrorMessage(msg);
                            }

                            tempOutStream = new FileOutputStream(tempUploadedFile);

                            byte[] dataBuffer = new byte[8192];
                            int i = 0;

                            while ((i = tiStream.read(dataBuffer)) > 0) {
                                tempOutStream.write(dataBuffer, 0, i);
                                tempOutStream.flush();
                            }

                            tempOutStream.close();

                            try {
                                StudyFileEditBean tempFileBean = new StudyFileEditBean(tempUploadedFile, studyService.generateFileSystemNameSequence(), study);
                                tempFileBean.setSizeFormatted(tempUploadedFile.length());
                                if (dirName != null) {
                                    tempFileBean.getFileMetadata().setCategory(dirName);
                                }
                                fbList.add(tempFileBean);
                                setTarValidationErrorMessage(null);
                            } catch (Exception ex) {
                                String msg = "Problem preparing files for ingest. Is the tar file corrupt?";
                                setTarValidationErrorMessage(msg);
                                uploadedInputFile.delete();
                                tempUploadedFile.delete();
                                fbList = new ArrayList<StudyFileEditBean>();
                            }
                        }
                    }
                } else {
                    File directory = new File(dir, fileEntryName);
                    directory.mkdir();
                    directoriesToDelete.add(directory);
                }
            }
        } catch (IOException ex) {
            String msg = "Problem reading tar file. Is it corrupt?";
            setTarValidationErrorMessage(msg);
        }

        // teardown and cleanup
        try {
            tiStream.close();
        } catch (IOException ex) {
            Logger.getLogger(AddFilesPage.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (tiStream != null) {
            try {
                tiStream.close();
            } catch (IOException ex) {
                Logger.getLogger(AddFilesPage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (tempOutStream != null) {
            try {
                tempOutStream.close();
            } catch (IOException ex) {
                Logger.getLogger(AddFilesPage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        uploadedInputFile.delete();
        if (unzippedFile != null) {
            unzippedFile.delete();
        }
        for (File dirToDelete : directoriesToDelete) {
            if (dirToDelete.exists()) {
                try {
                    FileUtils.forceDelete(dirToDelete);
                } catch (IOException ex) {
                    Logger.getLogger(AddFilesPage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return fbList;
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
        //TODO: this will need to be reimplemented using some functionality in 
        // the fileentry component. -- l.a.
        //InputFile ifile = (InputFile) event.getSource();
        //fileProgress = ifile.getFileInfo().getPercent();


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
        }
        
        Long versionNumber = studyService.getStudy( this.studyId).getLatestVersion().getVersionNumber();

        return "/study/StudyPage?faces-redirect=true&studyId=" + study.getId()+ "&versionNumber=" + versionNumber + "&tab=files" + getContextSuffix();
    }

    /**
     * Removes files from temp from temp storage 
     * @return String
     */
    public String cancel_action() {
        for (StudyFileEditBean fileBean : fileList) {
            new File(fileBean.getTempSystemFileLocation()).delete();
        }
        
        Long versionNumber = null; 
        
        if ( studyVersion.getId() == null ) {
            versionNumber = study.getReleasedVersion().getVersionNumber();
        } else {
            versionNumber = studyVersion.getVersionNumber();
        }

        return "/study/StudyPage?faces-redirect=true&studyId=" + study.getId()+ "&versionNumber=" + versionNumber + "&tab=files" + getContextSuffix();
    }

    public void removeFile_action(ActionEvent event) {
        int rowIndex = getFilesDataTable().getRowIndex();

        new File(fileList.get(rowIndex).getTempSystemFileLocation()).delete();
        fileList.remove(rowIndex);
        currentLabels.remove(rowIndex);
    }

    public void fileTypeListener(ValueChangeEvent vce) {
        dbgLog.fine("Addfiles: file type listener: value="+selectFileType.getValue());
        FacesContext.getCurrentInstance().renderResponse();
    }

    public void fileTypeActionListener(ActionEvent event) {
        dbgLog.fine("Addfiles: file type action listener: value="+selectFileType.getValue());
        FacesContext.getCurrentInstance().renderResponse();
    }

    public String changeFileTypeAction() {
        dbgLog.fine("Addfiles: change file type action: value="+selectFileType.getValue());
        return "";
    }


    public boolean isTypeNotSelected () {
        dbgLog.fine("AddFiles: isTypeNotSelected: selectFileType value="+selectFileType.getValue());

        if ( selectFileType == null 
            || selectFileType.getValue() == null
            || selectFileType.getValue().equals("") ) {
            return true;
        }

        return false;
    }

    public boolean isControlCardIngestInProgress() {
        return controlCardIngestInProgress;
    }

    public boolean isSPSSCCIngestInProgress() {
        return controlCardIngestInProgress && "spss".equals(controlCardType);
    }

    public boolean isDDICCIngestInProgress() {
        return controlCardIngestInProgress && "ddi".equals(controlCardType);
    }

    public boolean isPorExtraIngestInProgress() {
        dbgLog.fine("AddFiles: checking if SPSS portable ingest with extra labels is in progress.");
        return controlCardIngestInProgress && "porextra".equals(controlCardType);
    }

    public boolean isControlCardIngestRequested() {
        dbgLog.fine("AddFiles: is CCrequested: selectFileType value="+selectFileType.getValue());

        // This method is used to find if we are in the first stage of a CSV
        // file upload, when one of the control card-based ingests was selected
        // in the menu, but before the control card has been uploaded.

        if ( ("spss".equals(selectFileType.getValue()) || "ddi".equals(selectFileType.getValue()) || "porextra".equals(selectFileType.getValue())) && (!controlCardIngestInProgress)) {
            return true;
        }

        return false; 
    }

     public boolean isSPSSCCIngestRequested() {
        // as above, but for SPSS card only:

        if ( ("spss".equals(selectFileType.getValue())) && (!controlCardIngestInProgress)) {
            return true;
        }

        return false;
    }

    public boolean isDDICCIngestRequested() {
        // as above, but for DDI card only:

        if ( ("ddi".equals(selectFileType.getValue())) && (!controlCardIngestInProgress)) {
            return true;
        }

        return false;
    }
    
    public boolean isPorExtraIngestRequested() {
        // as above, but for the "extra labels" file only:
        
        if ( ("porextra".equals(selectFileType.getValue())) && (!controlCardIngestInProgress)) {
            dbgLog.fine("AddFiles: SPSS portable ingest with extra labels is requested.");
            return true;
        }

        return false;
    }

    public boolean isOtherSubsettableIngestRequested() {
        dbgLog.fine("AddFiles: is other subsettable ingest requested? selectFileType value="+selectFileType.getValue());

        if ( "sav".equals(selectFileType.getValue()) ||
             "por".equals(selectFileType.getValue()) ||
             "dta".equals(selectFileType.getValue())) {
            return true;
        }

        return false; 
    }
    
    public boolean isSAVIngestRequested() {
        dbgLog.fine("AddFiles: is SAV ingest requested? selectFileType value="+selectFileType.getValue());

        if ( "sav".equals(selectFileType.getValue()) ) {
            return true;
        }

        return false; 
    }
    
    private boolean showCharEncodingMenu = false; 
    
    public boolean isShowCharEncodingMenu() {
        return showCharEncodingMenu; 
    }
    
    public void setShowCharEncodingMenu(boolean show) {
        showCharEncodingMenu = show; 
    }
    
    public boolean isNetworkDataIngestRequested() {
        dbgLog.fine("AddFiles: is network data ingest requested? selectFileType value="+selectFileType.getValue());

        if ( "graphml".equals(selectFileType.getValue())) {
            return true;
        }

        return false;
    }
    
    public boolean isZipMultipleFilesSelected() {
        if ( "multizip".equals(selectFileType.getValue())) {
            return true;
        }
        return false;
    }

    public boolean isTarMultipleFilesSelected() {
        if ("multitar".equals(selectFileType.getValue())) {
            return true;
        }
        return false;
    }

    public boolean isFITSIngestRequested() {
        if ("fits".equals(selectFileType.getValue())) {
            return true;
        }
        return false; 
    }
    
    public boolean isEmailRequested() {
        for (StudyFileEditBean fileBean : fileList) {
            if (fileBean.getStudyFile().isSubsettable() || (fileBean.getStudyFile() instanceof SpecialOtherFile) ) {
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

    /**
     *
     * @return list of supported file types to display in the AddFilesPage.xhtml
     */
    public Collection<SelectItem> getFileTypes() {

        dbgLog.fine("populating file types menu");

        if (fileTypes == null) {
            fileTypes = new ArrayList();

            fileTypesSubsettable = new SelectItem[7];
            fileTypesNetwork = new SelectItem[1];
            //fileTypesOther = new SelectItem[1];

            fileTypes.add( new SelectItem("", "Choose a Data Type", "", true) );
//            fileTypesSubsettable[0] = new SelectItem("por", "SPSS/POR");
//            fileTypesSubsettable[1] = new SelectItem("sav", "SPSS/SAV");
//            fileTypesSubsettable[2] = new SelectItem("dta", "Stata");
//            fileTypesSubsettable[3] = new SelectItem("rdata", "RData");
//            fileTypesSubsettable[4] = new SelectItem("spss", "CSV (w/SPSS card)");
//            fileTypesSubsettable[5] = new SelectItem("ddi", "TAB (w/DDI)");
//            fileTypesSubsettable[6] = new SelectItem("porextra", "SPSS/POR,(w/labels)");

            /* 
             * Commenting out RData, for the 3.4 release: 
             *
            fileTypesSubsettable[6] = new SelectItem("rdata", "RData");
            * (don't forget to increase the number of items in fileTypesSubsettable
            * when you put it back!)
            * */

//            fileTypes.add( new SelectItemGroup("Tabular Data", "", false, fileTypesSubsettable) );
//
//            fileTypesNetwork[0] = new SelectItem("graphml", "GraphML");
//
//            fileTypes.add( new SelectItemGroup("Network Data", "", false, fileTypesNetwork) );
//            
//            fileTypes.add( new SelectItem("multizip", "Zip Archive (Multiple Files)"));
//            fileTypes.add( new SelectItem("multitar", "Tar Archive (Multiple Files)"));
//            fileTypes.add( new SelectItem("fits", "FITS file"));
            fileTypes.add( new SelectItem("other", "Other") );
        }

        return fileTypes;
    }

    // this is how a menubar can be created inside the backing bean:
    // (as opposed to creating one statically in the xhtml)
    // 
    // TODO: make a final decision, whether this menu should be static
    // (as currently implemented) or defined dynamically, in the backing bean.
    //  -- L.A. 
    
    public List getCharacterEncodings() {
        if (characterEncodings == null) {
            characterEncodings = new ArrayList<MenuItem>(); 
            
            MenuItem topLevel1 = new MenuItem();
                topLevel1.setValue("West European");

                MenuItem topLevel2 = new MenuItem();
                topLevel2.setValue("East European");

                MenuItem topLevel3 = new MenuItem();
                topLevel3.setValue("East Asian");

                characterEncodings.add(topLevel1);
                characterEncodings.add(topLevel2);
                characterEncodings.add(topLevel3);

                MenuItem eastAsian_1 = new MenuItem();
                eastAsian_1.setValue("Chinese Simplified (GB2312)");
                MenuItem eastAsian_2 = new MenuItem();
                eastAsian_2.setValue("Chinese Simplified (HZ)");
                MenuItem eastAsian_3 = new MenuItem();
                eastAsian_3.setValue("Chinese Simplified (GBK)");

                topLevel3.getChildren().add(eastAsian_1);
                topLevel3.getChildren().add(eastAsian_2);
                topLevel3.getChildren().add(eastAsian_3);

        }
      
        return characterEncodings; 
    }
   

}
