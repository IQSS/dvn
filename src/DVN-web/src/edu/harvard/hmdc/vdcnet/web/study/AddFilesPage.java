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
 * Created on September 25, 2006, 3:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.study;

import com.sun.rave.web.ui.model.UploadedFile;
import edu.harvard.hmdc.vdcnet.dsb.DSBWrapper;
import edu.harvard.hmdc.vdcnet.study.EditStudyService;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.StudyFileEditBean;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.study.TemplateFileCategory;
import edu.harvard.hmdc.vdcnet.util.FileUtil;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

/**
 *
 * @author gdurand
 */
public class AddFilesPage extends VDCBaseBean {
    @EJB EditStudyService studyService;
    @EJB StudyServiceLocal fileSystemNameService;
    
    /** Creates a new instance of AddFilesPage */
    public AddFilesPage() {
    }

    public void init() {
        super.init();
        if ( isFromPage("AddFilesPage") ) {
           studyService = (EditStudyService) sessionGet(studyService.getClass().getName());
           study = studyService.getStudy();
           files = studyService.getNewFiles();
        }
        else {
            // we need to create the studyServiceBean
            if (studyId != null) {
                studyService.setStudy(studyId);
                sessionPut( studyService.getClass().getName(), studyService);
                //sessionPut( (studyService.getClass().getName() + "."  + studyId.toString()), studyService);
                study = studyService.getStudy();
                files = studyService.getNewFiles();
            } else {
                // WE SHOULD HAVE A STUDY ID, throw an error
                System.out.println("ERROR: in addStudyPage, without a serviceBean or a studyId");
            }

        }
    }
    
    
    private Long studyId;

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }
    
    private Study study;
    
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }    
    
    transient  private UploadedFile  uploadedFile;

 
      //
      // Getter for property uploadedFile.
      // @return Value of property uploadedFile.
      //
     public UploadedFile getUploadedFile() {
         return this.uploadedFile;
     }
 
      //
      // Setter for property uploadedFile.
      // @param uploadedFile New value of property uploadedFile.
      //
     public void setUploadedFile(UploadedFile uploadedFile) {
         this.uploadedFile = uploadedFile;
     }

 

    private List files;    
    
    public List getFiles() {
        return files;
    }

    public void setFiles(List f) {
        files = f;
    }

    
    public void fileBrowser_add (ValueChangeEvent event) {
        Object value = event.getNewValue(); 
        if (value != null && !((UploadedFile) value).getOriginalName().equals("") ) {
            UploadedFile uploadedFile = (UploadedFile)value;
            String originalName = uploadedFile.getOriginalName();
            originalName = originalName.substring( originalName.lastIndexOf("/") + 1 );
            originalName = originalName.substring( originalName.lastIndexOf("\\") + 1 );
            String originalFileType = uploadedFile.getContentType();

            // upload the file to a temp directory
            try {            
                String filePathDir = System.getProperty("vdc.temp.file.dir");
                if (filePathDir == null) {
                        throw new Exception("System property \"vdc.temp.file.dir\" has not been set.");
                }           
                String sessionId =  ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false)).getId();
                String tempFileName = originalName;

                // first, check dir
                File tempDir = new File(filePathDir, sessionId);
                if (!tempDir.exists()) {
                    tempDir.mkdirs();
                }
                
                // now create the file
                File file = new File(tempDir, tempFileName);
                boolean fileCreated = file.createNewFile(); 
                int fileSuffix = 1;

                while (!fileCreated) {
                    int extensionIndex = originalName.lastIndexOf(".");
                    if (extensionIndex != -1 ) {
                        tempFileName = originalName.substring(0, extensionIndex) + "_" + fileSuffix++ + originalName.substring(extensionIndex);
                    }  else {
                        tempFileName = originalName + "_" + fileSuffix++;
                    }
                    file = new File(tempDir, tempFileName);
                    fileCreated = file.createNewFile();
                }
                
                uploadedFile.write( file );
                
                DSBWrapper dsb = new DSBWrapper();
                String analyzeFileType = FileUtil.analyzeFile( file );

                
                // now add the studyfile
                StudyFileEditBean f = new StudyFileEditBean( new StudyFile() );
                f.setOriginalFileName(originalName);
                f.getStudyFile().setSubsettable(analyzeFileType.equals("application/x-stata") || 
                                                analyzeFileType.equals("application/x-spss-por") || 
                                                analyzeFileType.equals("application/x-spss-sav") ||
                                                analyzeFileType.equals("application/x-rlang-transport") );
                
                // append ".tab" to name if subsettable
                f.getStudyFile().setFileName(f.getStudyFile().isSubsettable() ? replaceExtension(originalName): originalName);                
                
                // for unknown file types, use the originalFileType determined by the upload
                f.getStudyFile().setFileType( analyzeFileType.equals("application/octet-stream") ? originalFileType : analyzeFileType);
                
                f.setTempSystemFileLocation( file.getAbsolutePath() );
                f.getStudyFile().setFileSystemName(fileSystemNameService.generateFileSystemNameSequence());
                files.add(f);          
                
                newFileAdded = true;
                validateFileName( FacesContext.getCurrentInstance(), event.getComponent(), f.getStudyFile().getFileName() );
                        
            } catch(Exception ex) {
                System.out.println("Exception: " + ex.getMessage());
                // report the problem        
            }                    
        }
    }
    
    private String replaceExtension(String originalName) {
        int extensionIndex = originalName.lastIndexOf(".");
        if (extensionIndex != -1 ) {
            return originalName.substring(0, extensionIndex) + ".tab" ;
        } else {
            return originalName + ".tab";    
        }
    }
    
    public String save_action () {
        // first step is to validate (and remove the files that are not wanted from the list)
        Iterator iter = files.iterator();
        while (iter.hasNext()) {
            StudyFileEditBean file = (StudyFileEditBean) iter.next();
            if ( file.isDeleteFlag()) { 
                iter.remove();
            } 
        }
        
        
        // now call save
        if (files.size() > 0) {
            studyService.setIngestEmail(ingestEmail);
            studyService.save(getVDCRequestBean().getCurrentVDCId(), getVDCSessionBean().getLoginBean().getUser().getId());
        }
        
        getVDCRequestBean().setStudyId(study.getId());
        getVDCRequestBean().setSelectedTab("files");
        return "viewStudy";
    }  
    
    public String cancel_action () {
        //first clean up the temp files
        if (files.size() > 0) {
            Iterator iter = files.iterator();
            while (iter.hasNext()) {
                StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();
                File physicalFile = new File(fileBean.getTempSystemFileLocation());
                physicalFile.delete();
            }
        }        

        studyService.cancel();
        
        getVDCRequestBean().setStudyId(study.getId());
        getVDCRequestBean().setSelectedTab("files");
        return "viewStudy";
    }
    
    public List getTemplateFileCategories() {
        List tfc = new ArrayList();
        Iterator iter = study.getTemplate().getTemplateFileCategories().iterator();
        while (iter.hasNext()) {
            tfc.add( new SelectItem( ((TemplateFileCategory) iter.next()).getName() ) );
        }
        return tfc;
    }      
 
    public boolean isEmailRequested() {
        Iterator iter = files.iterator();
        while (iter.hasNext()) {
            StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();
            if ( fileBean.getStudyFile().isSubsettable() ) {
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
        String fileName = (String) value;
        String errorMessage = null;
        
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
        }

        // now check unique filename against other file names
        Iterator iter = getValidationFileNames().iterator();
        while (iter.hasNext()) {
            if ( fileName.equals( (String) iter.next() ) ) {
                errorMessage = "must be unique.";
                break;
            }
        }
        
        // also check prexisting files
        iter = study.getStudyFiles().iterator();
        while (iter.hasNext()) {
            StudyFile file = (StudyFile) iter.next();
            if ( fileName.equals(file.getFileName()) ) {
                errorMessage = "must be unique (a previously existing file already exists with the name).";
                break;
            }
        }      
        
        // now add this name to the validation list
        getValidationFileNames().add(fileName);        
        
        
        if (errorMessage != null) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage("Invalid File Name - " + errorMessage);
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }    
    
    private List validationFileNames = new ArrayList();
    
    public List getValidationFileNames() {
        return validationFileNames;
    }    
    
    private HtmlDataTable filesDataTable = new HtmlDataTable();

    public HtmlDataTable getFilesDataTable() {
        return filesDataTable;
    }

    public void setFilesDataTable(HtmlDataTable hdt) {
        this.filesDataTable = hdt;
    }        
    
    private boolean newFileAdded = false;
    
    public boolean isNewFileAdded() {
        return newFileAdded;
    }
}
