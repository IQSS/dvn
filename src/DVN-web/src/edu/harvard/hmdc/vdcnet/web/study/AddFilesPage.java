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
package edu.harvard.hmdc.vdcnet.web.study;


import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.io.File;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;
import java.util.*;
import edu.harvard.hmdc.vdcnet.study.EditStudyService;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.async.render.Renderable;
import com.icesoft.faces.component.inputfile.InputFile;
import com.icesoft.faces.context.DisposableBean;
import com.icesoft.faces.webapp.xmlhttp.FatalRenderingException;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;
import com.icesoft.faces.webapp.xmlhttp.TransientRenderingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.commons.fileupload.servelet.*;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import edu.harvard.hmdc.vdcnet.util.InputFileData;
import edu.harvard.hmdc.vdcnet.study.StudyFileEditBean;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.TemplateFileCategory;

public class AddFilesPage extends VDCBaseBean implements java.io.Serializable,
	  Renderable, DisposableBean  {
    @EJB EditStudyService studyService;
    @EJB StudyServiceLocal fileSystemNameService;

/**
 * <p>The AddFilesPage is responsible for the file upload
 * logic as well as the file deletion object.  A users file uploads are only
 * visible to them and are deleted when the session is destroyed.</p>
 *
 * @since 1.7
 */


    public static final Log mLog = LogFactory.getLog(AddFilesPage.class);

    // File sizes used to generate formatted label
    public static final long MEGABYTE_LENGTH_BYTES = 1048000l;
    public static final long KILOBYTE_LENGTH_BYTES = 1024l;

    // render manager for the application, uses session id for on demand
    // render group.
    private RenderManager renderManager;
    private PersistentFacesState persistentFacesState;
    private String sessionId;
    // files associated with the current user
   
     private final List<InputFileData> fileList =
            Collections.synchronizedList(new ArrayList<InputFileData>());
    // latest file uploaded by client
    private InputFileData currentFile;
    // file upload completed percent (Progress)
    private int fileProgress;
    
   
     /**
     * Return the reference to the
     * {@link com.icesoft.faces.webapp.xmlhttp.PersistentFacesState
     * PersistentFacesState} associated with this Renderable.
     * <p/>
     * The typical (and recommended usage) is to get and hold a reference to the
     * PersistentFacesState in the constructor of your managed bean and return
     * that reference from this method.
     *
     * @return the PersistentFacesState associated with this Renderable
     */
    public PersistentFacesState getState() {
        return persistentFacesState;
    }

    public void setRenderManager(RenderManager renderManager) {
        this.renderManager = renderManager;
        renderManager.getOnDemandRenderer(sessionId).add(this);
        
    }

    public InputFileData getCurrentFile() {
        return currentFile;
    }

    public int getFileProgress() {
        return fileProgress;
    }

    public List getFileList() {
        return fileList;
    }
    
  
    public AddFilesPage() {
        persistentFacesState = PersistentFacesState.getInstance();
     
     
        // Get the session id in a container generic way
        sessionId = FacesContext.getCurrentInstance().getExternalContext()
                .getSession(false).toString();
    }

    /**
     * <p>Action event method which is triggered when a user clicks on the
     * upload file button.  Uploaded files are added to a list so that user have
     * the option to delete them programatically.  Any errors that occurs
     * during the file uploaded are added the messages output.</p>
     *
     * @param event jsf action event.
     */
    public void uploadFile(ActionEvent event) {
        InputFile inputFile = (InputFile) event.getSource();
        String str="";
       
	if (inputFile.getStatus() != InputFile.SAVED){
            str = "File " + inputFile.getFileInfo().getFileName()+ " has not been saved";
            mLog.error(str); 
            errorMessage(str);
            return;
          }
            
            // reference our newly updated file for display purposes and
            // added it to our history file list.
        File file = inputFile.getFile();
        currentFile = new InputFileData(inputFile.getFileInfo(),file);
        boolean b =hasFileName(currentFile, false);
       if(b){
            str = "File " + currentFile.getFileInfo().getFileName()+ " is already in the table";
            mLog.error(str);
            System.out.println(str);
           errorMessage(str);
            return;
       }else{
            getValidationFileNames().add(currentFile.getFileName()); 
       }
                
        synchronized (fileList) {
                fileList.add(currentFile);   
           }  
     
    try{
        if (persistentFacesState !=null) persistentFacesState.executeAndRender();
     }catch(RenderingException ee){ 
         mLog.error(ee.getMessage()); } 
        

    }
  private void errorMessage(String str){
       FacesContext context =   FacesContext.getCurrentInstance();
       FacesMessage message = new FacesMessage(str);
       UIInput out = new UIInput(); 
       out.setValid(false); 
      persistentFacesState.getFacesContext().addMessage(out.getClientId(context),message);
      
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
  if (persistentFacesState !=null) {
         renderManager.getOnDemandRenderer(sessionId).requestRender();} 
        
/* 
try {
       
          persistentFacesState.render();
            
        } catch (RenderingException ee) {
            mLog.error(ee.getMessage());
        }
  */
    }

    /**
     * <p>Allows a user to remove a file from a list of uploaded files.  This
     * methods assumes that a request param "fileName" has been set to a valid
     * file name that the user wishes to remove or delete</p>
     *
     * @param event jsf action event
     */ 
    public void removeUploadedFile(ActionEvent event) {
        // Get the inventory item ID from the context.
        FacesContext context = FacesContext.getCurrentInstance();
        Map map = context.getExternalContext().getRequestParameterMap();
        String fileName = (String) map.get("fileName");
        
        InputFileData inputFileData=null;
        boolean found = false; 
        synchronized (fileList) {
            
            Iterator<InputFileData> theit = fileList.iterator();
            
            
            while(theit.hasNext()){
                inputFileData =theit.next();
               
                
                  if (inputFileData.getFileName().equals(fileName)||
                     inputFileData.getFileInfo().getFileName().equals(fileName) ) {
		      found = true; 
                      theit.remove();             
                      break;
                  }
            }
        }
	if (found) hasFileName(fileName, true);
      
        }
     
private boolean  hasFileName( InputFileData inputFileData, boolean remov){
    boolean isin = false; 
    if(getValidationFileNames().size() <= 0) return isin;
   
    String fname = inputFileData.getFileName();
    return hasFileName(fname, remov);
         
}
        
  private boolean  hasFileName( String fname, boolean remov){
    boolean isin = false; 
    Iterator<String> iter = getValidationFileNames().iterator();
        while (iter.hasNext()) {
           
            if (fname.equals(iter.next() ) ) {
                if(remov) iter.remove();
		isin = true;
            }
	}
	return isin; 
}
       
    
    /**
     * Callback method that is called if any exception occurs during an attempt
     * to render this Renderable.
     * <p/>
     * It is up to the application developer to implement appropriate policy
     * when a RenderingException occurs.  Different policies might be
     * appropriate based on the severity of the exception.  For example, if the
     * exception is fatal (the session has expired), no further attempts should
     * be made to render this Renderable and the application may want to remove
     * the Renderable from some or all of the
     * {@link com.icesoft.faces.async.render.GroupAsyncRenderer}s it
     * belongs to. If it is a transient exception (like a client's connection is
     * temporarily unavailable) then the application has the option of removing
     * the Renderable from GroupRenderers or leaving them and allowing another
     * render call to be attempted.
     *
     * @param renderingException The exception that occurred when attempting to
     *                           render this Renderable.
     */
    public void renderingException(RenderingException renderingException) {
        if (mLog.isTraceEnabled() &&
                renderingException instanceof TransientRenderingException) {
            mLog.trace("InputFileController Transient Rendering excpetion:", renderingException);
        } else if (renderingException instanceof FatalRenderingException) {
            if (mLog.isTraceEnabled()) {
                mLog.trace("InputFileController Fatal rendering exception: ", renderingException);
            }
            renderManager.getOnDemandRenderer(sessionId).remove(this);
            renderManager.getOnDemandRenderer(sessionId).dispose();
        }
    }

   
    /**
     * Dispose callback called due to a view closing or session
     * invalidation/timeout
     */
    	public void dispose() throws Exception {
               
       if (mLog.isTraceEnabled()) {
            mLog.trace("OutputProgressController dispose OnDemandRenderer for session: " + sessionId);
        }
        renderManager.getOnDemandRenderer(sessionId).remove(this);
		renderManager.getOnDemandRenderer(sessionId).dispose();
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
    
    List<StudyFileEditBean> files =   Collections.synchronizedList(new ArrayList< StudyFileEditBean>());
    private String replaceExtension(String originalName) {
        int extensionIndex = originalName.lastIndexOf(".");
        if (extensionIndex != -1 ) {
            return originalName.substring(0, extensionIndex) + ".tab" ;
        } else {
            return originalName + ".tab";    
        }
    }
    
   private String getFileExtension(String originalName){
    	String ext = null;
    	if ( originalName.lastIndexOf(".") != -1){
    		ext = (originalName.substring( originalName.lastIndexOf(".") + 1 )).toLowerCase();
    	}
    	return ext;
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
        if (value==null) return;
        String fileName =( (String) value).trim();
        String errorMessage = null;
        if(fileName.equals("")) return; 
        // check invalid characters 
        String fname = currentFile.getFileName();
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
            errorMessage = "cannot contain any of the following characters: \\ / : * ? \" < > | ; "; 
            
        }
        
         if (errorMessage != null) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage("Invalid File Name - " + errorMessage);
            context.addMessage(toValidate.getClientId(context), message);
            currentFile.setFileName(fname);
            return; 
         }
/*       
        // also check prexisting files
        iter = study.getStudyFiles().iterator();
        while (iter.hasNext()) {
            StudyFile file = (StudyFile) iter.next();
            if ( fileName.equals(file.getFileName()) ) {
                errorMessage = "must be unique (a previously existing file already exists with the name).";
                break;
            }
        }      
     */ 
         currentFile.setFileName(fileName);
        // now add this name to the validation list
       getValidationFileNames().add(fileName); 
       //remove the old name 
       hasFileName(fname,true);
         
        
    } 
   
    
    private List<String> validationFileNames = new ArrayList<String>();
    
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
