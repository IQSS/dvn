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
import com.icesoft.faces.component.ext.HtmlDataTable;

import javax.faces.model.SelectItem;
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
import edu.harvard.hmdc.vdcnet.study.StudyFileEditBean;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.TemplateFileCategory;
import edu.harvard.hmdc.vdcnet.study.FileCategory;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;


public class AddFilesPage extends VDCBaseBean implements java.io.Serializable,
	  Renderable, DisposableBean  {
    @EJB EditStudyService studyService;
    @EJB StudyServiceLocal fileSystemNameService;
  
  private static final String COMPONENT_ID="input_filename";
/**
 * <p>The AddFilesPage is responsible for the file upload
 * logic as well as the file deletion object.  A users file uploads are only
 * visible to them and are deleted when the session is destroyed.</p>
 *
 * @since 1.7
 */
   //private sta
    //public static Logger mLog = Logger.getLogger(AddFilesPage.class.getName());
public static final Log mLog = LogFactory.getLog(AddFilesPage.class);
    // File sizes used to generate formatted label
   
    // render manager for the application, uses session id for on demand
    // render group.
    private RenderManager renderManager;
    private PersistentFacesState persistentFacesState;
    private String sessionId;
    // files associated with the current user
   private Long studyId= null; 
   private  List<StudyFileEditBean> fileList =
            Collections.synchronizedList(new ArrayList<StudyFileEditBean>());
    // latest file uploaded by client
    private StudyFileEditBean currentFile=null;
    // file upload completed percent (Progress)
    private int fileProgress;
    //for the drop-down list of the html page 
    private Collection<SelectItem> fileCategories= null;
    Collection<StudyFile> objStudyFiles; 
    //the names of the study files that already exist in storage 
    private String[] studyFileNames=null;  
    //ActionEvent object associated with ice:inputFile component 
    private InputFile inputFile = null;
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

    public StudyFileEditBean getCurrentFile() {
        return currentFile;
    }
    public void setCurrentFile(StudyFileEditBean f) {
        currentFile=f;
    }
    public InputFile getInputFile( ) {
        return inputFile;
    }
    public void setInputFile(InputFile f) {
        inputFile=f;
    }
 
    public int getFileProgress() {
        return fileProgress;
    }

    public List getFileList() {
        return fileList;
    }
    
  public Long getStudyId(){
      return studyId;
  }
  public void setStudyId(long st){
      
      studyId = st; 
  }
    public AddFilesPage() {
                persistentFacesState = PersistentFacesState.getInstance();
  
        // Get the session id in a container generic way
        sessionId = FacesContext.getCurrentInstance().getExternalContext()
                .getSession(false).toString();
        String studyEV = (( HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("studyId");
        studyId = Long.parseLong(studyEV); 
        if(fileCategories == null )
            fileCategories=Collections.synchronizedList(new ArrayList<SelectItem>());
        
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
   
        this.inputFile = inputFile; 
        String str="";
	if (inputFile.getStatus() != InputFile.SAVED){
            str = "Uploaded File: " + inputFile.getFileInfo().getFileName()+ "\n" + 
                   "InputFile Status: "+ inputFile.getStatus();
            mLog.error(str); 
            System.out.println(str);
         //   errorMessage(str);
           if(inputFile.getStatus() != InputFile.INVALID){
               str = "Error saving the file. Status "+ inputFile.getStatus();
               System.out.println(str); 
          //     errorMessage(str); 
            //return;
           }
          }
                     
          
         currentFile = createStudyFile(inputFile);
         if(currentFile==null){
            str = "StudyFileEditBean cannot be created ";
            mLog.error(str); 
           errorMessage(str);
            return; 
         }
         if(!currentFile.getOriginalFileName().equals(currentFile.getStudyFile().getFileName())){
             str = "StudyFileEditBean original name differs from study file name";
             mLog.error(str); 
          //  errorMessage(str);
         }
      
      // check if the file is in the data table already with the same name, i.e.  
      // hasFileName checks if currentFile name is in getValidationFileNames  
      //and if del=true removes it from validation names   
      boolean del = true;
      
       boolean b =hasFileName(currentFile.getStudyFile().getFileName(), del);
       if(b){
            str = "File " + currentFile.getStudyFile().getFileName()+ " is already in the table";
            mLog.error(str);
            errorMessage(str);
        if(!del)   return;
     
     
       }
            //add it to the validation names 
           getValidationFileNames().add(currentFile.getStudyFile().getFileName().trim()); 
       
       // reference our newly updated file for display purposes and
       // added it to our history file list.
              
        synchronized (fileList) {
                fileList.add(currentFile);   
           }  
    
        try{
        if (persistentFacesState !=null) persistentFacesState.executeAndRender();
     }catch(RenderingException ee){ 
         mLog.error(ee.getMessage()); } 
     

    }
    
      private StudyFileEditBean createStudyFile(InputFile inputFile){
        File file = inputFile.getFile();
      //  FileInfo info = inputFile.getFileInfo();
        StudyFileEditBean f = null;
        try{
      //  File fstudy = FileUtil.createTempFile(sessionId, file.getName());
        f = new StudyFileEditBean(file, fileSystemNameService.generateFileSystemNameSequence());
        f.setSizeFormatted(file.length());
        f.setFileCategoryName(""); 
        
        }catch(Exception ex){
            String m = "Fail to create the study file. ";
            mLog.error(m);
            errorMessage(m);
            mLog.error(ex.getMessage()); 
        }
        return f;
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
        
    }

    /**
     * <p>Allows a user to remove one file from a list of uploaded files.  This
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
        
       boolean found = removeFromFileLst(fileName);
       //remove from the validation names 
	if (found) hasFileName(fileName, true);
      
        }
    /**
     * The file name is in the data table collection, then remove it. 
     * @param fname String with file name
     * @return boolean if it ghas been removed from collection of files in data table
     */
public boolean removeFromFileLst(String fname){
     StudyFileEditBean inputFileData=null;
     boolean found = false; 
        synchronized (fileList) {
            
            Iterator<StudyFileEditBean> theit = fileList.iterator();
             
            while(theit.hasNext()){
              inputFileData =theit.next();  
                  if ((inputFileData.getStudyFile().getFileName().trim()).equals(fname.trim())) {
		      found = true; 
                      theit.remove();             
                      break;
                  }
            }
        }
     return found;
   }
/**
 * 
 * @param inputFileData StudyFileEditBean
 * @param remov boolean whether to remove it from validation file names
 * @return boolean 
 */
private boolean  hasFileName( StudyFileEditBean inputFileData, boolean remov){
    boolean isin = false; 
    if(getValidationFileNames().size() <= 0) return isin;
   
    String fname = inputFileData.getStudyFile().getFileName();
    return hasFileName(fname, remov);
         
}
/**
 * It is used to remove a file name from the validation list 
 * @param fname String with file name 
 * @param remov boolean whether to remove it from validation file names
 * @return boolean
 */
        
  private boolean  hasFileName( String fname, boolean remov){
    boolean isin = false; 
    Iterator<String> iter = getValidationFileNames().iterator();
        while (iter.hasNext()) {
           
            if (fname.trim().equals(iter.next().trim() ) && !isin) {
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
/**
 * Original code 
 */        
        
      
   
    public void init() {
        super.init();
   
        if (isFromPage("AddFilesPage")) {
           studyService = (EditStudyService) sessionGet(studyService.getClass().getName());
           study = studyService.getStudy();
           fileList = studyService.getNewFiles();
       
        }
        else {
            // we need to create the studyServiceBean
       
            if (studyId != null) {
              
                studyService.setStudy(studyId);
                sessionPut( studyService.getClass().getName(), studyService);
                //sessionPut( (studyService.getClass().getName() + "."  + studyId.toString()), studyService);
                study = studyService.getStudy();
                fileList = studyService.getNewFiles();
                
            } else {
                // WE SHOULD HAVE A STUDY ID, throw an error
                System.out.println("ERROR: in addStudyPage, without a serviceBean or a studyId");
            }

        }
        //Added by EV: Files already existent in the Study 
      existentFiles("From init"); 
       //existent categories
      
      fileCategories = this. buildCategories(); 
    }
    /**
     * Helper function to init: It obtaines the collection of files 
     * stored in the Study and also builds a collection with the names of the files.
     * Calls buildCategories as well.
     * @param mess String to indicate which method is calling this function
     */
    public void existentFiles(String mess){
         objStudyFiles  = study.getStudyFiles();
        if(objStudyFiles==null || objStudyFiles.size()<=0) 
            objStudyFiles = new ArrayList<StudyFile>();
           
        Iterator<StudyFile> itfl = objStudyFiles.iterator();
        Collection<String> studyFiles = new ArrayList<String>();
        while(itfl.hasNext()){
            studyFiles.add(itfl.next().getFileName().trim());
        }
        //make the files names in the Study unique
        Collection<String> noDups = new HashSet<String>(studyFiles);
        int sz = noDups.size();
        //the names for existent files in the Study stored in array of String
        studyFileNames = noDups.toArray(new String[sz]);
        Arrays.sort(studyFileNames); 
        for(int n=0; n < sz;++n)
            mLog.debug(mess +"/nFile stored are "+ studyFileNames[n]);       
         
    }
    /**
     * Find the names of the categories that exists in the Study
     * @return String array with the names of categories in the study
     */
    private String[] studyCategories(){
         List<FileCategory> tfc =  study.getFileCategories(); 
        mLog.debug("Files categories are "+ tfc.size());
        Collection<FileCategory> tfcuniq= new HashSet<FileCategory>(tfc);
        int ln = tfcuniq.size();
        if(ln <=0) return null;
        Iterator<FileCategory> iter = tfcuniq.iterator();
        int cnt=0;
        //category names that are stored in the study
        String [] catstudy = new String[ln];
        while(iter.hasNext()){
            FileCategory tmp = iter.next(); 
            catstudy[cnt]= tmp.getName().trim();
            mLog.debug(catstudy[cnt]);
            cnt++; 
        }
        Arrays.sort(catstudy);
        return catstudy;
    }
    /**
     * Find the categories in the study for displaying in a dropdown menu
     * @return Collection<SelectItem>
     */
    public Collection<SelectItem> buildCategories(){
        if(study==null || study.getFileCategories()==null)return fileCategories;
       String [] catstudy =studyCategories();
       if (catstudy==null) return fileCategories;
       //category names that are stored in drop down list of SelectItem      
       String catfiles[] = new String[fileCategories.size()];
       int cnt=0;
       for(SelectItem sel:fileCategories){
            String key = ((String) sel.getValue()).trim();
            catfiles[cnt]= key;
            cnt++; 
        }
        Arrays.sort(catfiles);
        //add the study categories to the drop down list of SelectItem
        for(String str: catstudy){
          String key = str.trim();
          int found = Arrays.binarySearch(catfiles, key);  
          if(found < 0) fileCategories.add(new SelectItem(key));   
       }
        
   
      return fileCategories; 
        
    }  
    private Study study;
    
    public Study getStudy() {
      
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }    
    
  /**
   * Action method that saves the files in the data table
   * @return String for neext view
   */
   public String save_action () {
       //any files in storage with the same file names
       if(studyFileNames == null) existentFiles("From Save"); 
       if(studyFileNames != null && studyFileNames.length>0){
           
        Iterator<StudyFileEditBean> edbean = fileList.iterator();
        while(edbean.hasNext()){
            
         StudyFileEditBean tmp=  edbean.next();
         StudyFile sf = tmp.getStudyFile();
         String nm = sf.getFileName();
         int found = Arrays.binarySearch(studyFileNames, nm);
              
              mLog.debug(tmp.getStudyFile().getDescription()+"; File Description");
            //if the file name exist remove it from temp directory and do not store
            if(found>=0){
                edbean.remove();
                removeUploadFiles(nm);
            }
            
        }
       }
         for(int n=0; n < fileList.size(); ++n){
            mLog.debug(fileList.get(n).getStudyFile().getFileName());
        }
        if(detectDuplicates()) {
            String m = "Duplicate file names are not allowed";
            System.out.println(m);
            errorMessage(m); 
            return null;
        }
        // now call save
        if (fileList.size() > 0) {
            studyService.setIngestEmail(ingestEmail);
            studyService.save(getVDCRequestBean().getCurrentVDCId(), getVDCSessionBean().getLoginBean().getUser().getId());
        }
        
        getVDCRequestBean().setStudyId(study.getId());
        getVDCRequestBean().setSelectedTab("files");
        return "viewStudy";
    } 
   // detect duplicate file names in the validation and data table lists
   private boolean detectDuplicates(){
       return (duplicatesValidationNames() ||duplicatesDataListNames());
   }
   //find duplicate strings in the validation list
   private boolean duplicatesValidationNames(){
       
       int sz0 = getValidationFileNames().size();
       if(sz0<=0) return false;
        Collection<String> noDups = new HashSet<String>(getValidationFileNames());
        int sz1 = noDups.size();
        boolean res = sz0 != sz1;
        if(res){
          getValidationFileNames().clear();
          getValidationFileNames().addAll(noDups);
        }
        return res; 
   }
        
   //find duplitcate file names in the data table list     
   
   private boolean duplicatesDataListNames(){
       
       if(fileList.size() <= 0) return false;
        Iterator<StudyFileEditBean> it = fileList.iterator();
        List<String> names = new ArrayList<String>();
        while(it.hasNext()){
            names.add((it.next()).getStudyFile().getFileName().trim());
        }
        int sz0 = names.size();
        Collection<String> noDups =  new HashSet<String>(names);
        int sz1 = noDups.size();
       
        return (sz0 != sz1);
   }
    /**
     * Removes files from temp from temp storage 
     * @return String
     */
    public String cancel_action () {
        //first clean up the temp files  
        removeUploadFiles();
        studyService.cancel();
        getVDCRequestBean().setStudyId(study.getId());
        getVDCRequestBean().setSelectedTab("files");
        return "viewStudy";
    }
    /**
     * Find the absolute path for the temporary directory where the 
     * files are being uploaded 
     */  
     public File directoryToUpload(String nm){
        if(currentFile == null) return null;
      //get the path to the temporary directory in web.xml = upload 
        
       String p = currentFile.getTempSystemFileLocation().trim();
      
       mLog.debug("File Location "+p);
       if(nm==null)
        nm = currentFile.getOriginalFileName();
        mLog.debug("Name "+nm); 
        String fp =  p.replaceAll(nm, "");
        int ln =  fp.length();
        //the absolute path to the temp directory in web.xml
        String sessionFileUploadPath = fp.substring(0,ln-1);
        mLog.debug("upload dir "+ sessionFileUploadPath); 
        mLog.debug("Temp dir is..." + fp.substring(0,ln-1));
        return  new File(sessionFileUploadPath);
         
          
     } 
     public void removeUploadFiles(){
           removeUploadFiles(null); 
      }
     /**
      * Remove all temp files from the upload directory (null) or only filenm0
      * filenm0 String or null 
      **/
     public void removeUploadFiles(String filenm0) {
        File sessionfileUploadDirectory= directoryToUpload(null);
        if(sessionfileUploadDirectory  == null) {
            mLog.debug("I am a null from removeUploadFiles");
            return;
        }
        
         File [] filein = sessionfileUploadDirectory.listFiles();
         mLog.debug("No files in dir is..."+ filein.length);
         for(int n=0; n < filein.length; ++n){
                String fname = filein[n].getName();
              mLog.debug("Contains file..."+ filein[n].getName()+ "...."+ filein[n].isFile());
              if(filenm0!=null && !(fname.equals(filenm0))) continue;  
               if(getValidationFileNames().contains(fname)){
                 
                    //remove from validation names
                    hasFileName(fname,true);
            //remove from the backing dataTable list
                    boolean found=  removeFromFileLst(fname);
                    if(found)  mLog.debug("Deleting file..."+ fname);
             }
              
                   boolean res = filein[n].delete();
                   if(!res) filein[n].delete();
                    mLog.debug("Deleted "+ res+"; the file "+filein[n]);
                  
               }
           }
         
     
 
                
    public List<SelectItem> getTemplateFileCategories() {
        List<SelectItem> tfc = new ArrayList<SelectItem>();
        Iterator<TemplateFileCategory> iter = study.getTemplate().getTemplateFileCategories().iterator();
        while (iter.hasNext()) {
            tfc.add( new SelectItem( iter.next().getName()) );
        }
        return tfc;
    }      
  
    public boolean isProgressRequested(){
         
    return currentFile != null;         
       
} 
    public boolean isEmailRequested() {
        Iterator< StudyFileEditBean> iter = fileList.iterator();
        while (iter.hasNext()) {
            StudyFileEditBean fileBean = iter.next();
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
        String errorMessage = null;
        if(((UIInput)toValidate)!=null)
        ((UIInput)toValidate).setValid(true);
    
        if(toValidate != null){
        Map<String,Object> comp =toValidate.getAttributes();
        Set<String> ss= comp.keySet();
       
        boolean flag=false;
        for(String str: ss){
            if(str.contains(COMPONENT_ID))  { 
                flag=true;
                mLog.debug("I am COMPONENT "+ COMPONENT_ID);
            }
        }
      // if(!flag) return; 
        }
        String or = currentFile.getStudyFile().getFileName().trim();
        String fname = or;
        String fileName =( (String) value).trim();
        currentFile.getStudyFile().setFileName(fileName);
       // currentFile.setOriginalFileName(fileName);
      
        if(fileName.equals("") || fileName==null) {
             errorMessage = "Enter a valid file name"; 
            displayError(context, (UIInput) toValidate, errorMessage);
           
        } 
        // check invalid characters 
        
         mLog.debug(currentFile.getOriginalFileName());
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
            displayError(context, (UIInput) toValidate, errorMessage);
        
        }
     
        // also check prexisting files
       if(errorMessage == null && studyFileNames != null && studyFileNames.length > 0 ){
       for(String dummy: studyFileNames){
          if ( fileName.equals(dummy) ) {
                errorMessage = "must be unique (a previous study file exists with the same name).";
                displayError(context, (UIInput) toValidate, errorMessage);
                break;
               
            }
        }
       }
         
         //make sure there are no other files with the same name in the validation list 
         //this did not exist when it was working: !fileName.equals(fname) 
        if(!fileName.equals(fname) && hasFileName(fileName, true) ){
           errorMessage = "must be unique (a previous file exists with the same name).";
           displayError(context, (UIInput) toValidate, errorMessage); 
        }
        // now add this name to the validation list
     
        getCurrentFile().getStudyFile().setFileName(fileName);
        getValidationFileNames().add(fileName.trim()); 
       //remove the old name from validation names 
        hasFileName(fname,true); 
       
      }
    private void displayError(FacesContext context, UIInput toValidate, String errorMessage){
        
         if (errorMessage != null) {
            ((UIInput)toValidate).setValid(false);
             
            FacesMessage message = new FacesMessage("Invalid File Name - " + errorMessage);
            context.addMessage(toValidate.getClientId(context), message);
            errorMessage=null;   
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
   
     public Collection<SelectItem>  getFileCategories( ){
           mLog.debug("In getFileCategories");
           Collection<SelectItem> uniq = new HashSet<SelectItem>(fileCategories);
           Collection<String> noDups = new HashSet<String>();
           for(SelectItem select: uniq){
               noDups.add(((String) select.getValue()).trim());
           }
        int sz = noDups.size();
        String [] cats = noDups.toArray(new String[sz]);
        if(cats.length >1) Arrays.sort(cats);
        if(currentFile != null){
           String str = currentFile.getFileCategoryName().trim();
           int found=0;
           if(str != null &&!str.equals(""))
           found = Arrays.binarySearch(cats, str.trim());
           FileCategory c= null;
           if(found < 0) {
              mLog.debug("Added category "+ str);
              c= new FileCategory();
              c.setName(str);
              Collection<StudyFile> studf = c.getStudyFiles();
              if (studf == null) studf = new ArrayList<StudyFile>();
              studf.add(currentFile.getStudyFile());
              c.setStudyFiles(studf);
              
           }
       
        if(c!=null) {
           if(study.getFileCategories()==null) 
                study.setFileCategories(new ArrayList<FileCategory>());
            study.getFileCategories().add(c);
            fileCategories.add(new SelectItem(str)); 
        }
       
        }
          
       

         return fileCategories; 
      }
 
     public void setFileCategories(Collection<SelectItem> tfc) {
           mLog.debug("In setFileCategories");
         Iterator<SelectItem> iter = tfc.iterator();
         fileCategories.addAll(tfc);
         
         while(iter.hasNext()){
         String val =((String) iter.next().getValue()).trim();
         FileCategory cat = new FileCategory();
         cat.setName(val);
          if(study.getFileCategories()==null)study.setFileCategories(new ArrayList<FileCategory>());
         study.getFileCategories().add(cat);
         
        // currentFile.getStudyFile().setFileCategory(cat);
         }
         
}
     private String fileCategoryName=null; 
     public String getFileCategoryName(){
         return fileCategoryName;
     }
     public void setFileCategoryName(String key){
         mLog.debug("Category name..." +key);
         fileCategoryName =key.trim();
      
        
     }
     
     public void addCategory(ValueChangeEvent e){
         currentFile.setFileCategoryName(((String) e.getNewValue()).trim());
         currentFile.addFileToCategory(study);
        
           }
}
