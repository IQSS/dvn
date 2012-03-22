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
 * UtilitiesPage.java
 * 
 * Created on Mar 18, 2008, 3:30:20 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.networkAdmin;


//import com.icesoft.faces.async.render.RenderManager;
//import com.icesoft.faces.async.render.Renderable;
import edu.harvard.iq.dvn.core.harvest.HarvestFormatType;
import edu.harvard.iq.dvn.core.harvest.HarvestStudyServiceLocal;
import edu.harvard.iq.dvn.core.harvest.HarvesterServiceLocal;
import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.index.Indexer;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.StudyFileEditBean;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.util.FileUtil;
import edu.harvard.iq.dvn.core.vdc.HarvestingDataverse;
import edu.harvard.iq.dvn.core.vdc.HarvestingDataverseServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.gnrs.GNRSServiceLocal;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
//import com.icesoft.faces.component.inputfile.InputFile;
import org.icefaces.component.fileentry.*;
//import com.icesoft.faces.webapp.xmlhttp.FatalRenderingException;
//import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
//import com.icesoft.faces.webapp.xmlhttp.RenderingException;
//import com.icesoft.faces.webapp.xmlhttp.TransientRenderingException;
import edu.harvard.iq.dvn.core.study.FileMetadata;
import edu.harvard.iq.dvn.core.study.StudyFileServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.util.StringUtil;
import java.util.EventObject;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.inject.Named;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 *
 * @author gdurand
 */
@ViewScoped
@Named("UtilitiesPage")
public class UtilitiesPage extends VDCBaseBean implements java.io.Serializable   {

    private static final Logger logger = Logger.getLogger("edu.harvard.iq.dvn.core.web.networkAdmin.UtilitiesPage");
            
    @EJB StudyServiceLocal studyService;
    @EJB StudyFileServiceLocal studyFileService;
    @EJB IndexServiceLocal indexService;
    @EJB HarvestStudyServiceLocal harvestStudyService;
    @EJB HarvestingDataverseServiceLocal harvestingDataverseService;    
    @EJB HarvesterServiceLocal harvesterService;
    @EJB VDCServiceLocal vdcService;
    @EJB GNRSServiceLocal gnrsService;
    
    private String selectedPanel;
    private Long vdcId;

    private File uploadedDdiFile = null; 
    
    /** Creates a new instance of ImportStudyPage */
    public UtilitiesPage() {
       //L.A.persistentFacesState = PersistentFacesState.getInstance();
     
        // Get the session id in a container generic way
       ExternalContext ext =  FacesContext.getCurrentInstance().getExternalContext();
       sessionId = ext.getSession(false).toString();
       
    }
    
    public void init() {
        super.init();
        vdcId = getVDCRequestBean().getCurrentVDCId();
        selectedPanel = getRequestParam("selectedPanel");
    }

    public String getSelectedPanel() {
        return selectedPanel;
    }

    public void setSelectedPanel(String selectedPanel) {
        this.selectedPanel = selectedPanel;
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="studyLock utilities">  
    public boolean isStudyLockPanelRendered() {
        return "studyLock".equals(selectedPanel);
    }
    
    String studyLockStudyId;

    public String getStudyLockStudyId() {
        return studyLockStudyId;
    }

    public void setStudyLockStudyId(String studyLockStudyId) {
        this.studyLockStudyId = studyLockStudyId;
    }
    
    public List getStudyLockList() {
        return studyService.getStudyLocks();
    }
    
    public String removeLock_action() {
        try {
            studyService.removeStudyLock( new Long( studyLockStudyId) );
            addMessage( "studyLockMessage", "Study lock removed (for study id = " + studyLockStudyId + ")" );
        } catch (NumberFormatException nfe) {
            addMessage( "studyLockMessage", "Action failed: The study id must be of type Long." );
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "studyLockMessage", "Action failed: An unknown error occurred trying to remove lock for study id = " + studyLockStudyId );
        }
       
        return null;
    }    
    // </editor-fold>        

    
    // <editor-fold defaultstate="collapsed" desc="index utilities">    
    public boolean isIndexPanelRendered() {
        return "index".equals(selectedPanel);
    }
    
    String indexDVId;
    String indexStudyIds;
    private String fixGlobalId; 
    private String studyIdRange;
    private String handleCheckReport; 

    public String getIndexDVId() {
        return indexDVId;
    }

    public void setIndexDVId(String indexDVId) {
        this.indexDVId = indexDVId;
    }

    public String getIndexStudyIds() {
        return indexStudyIds;
    }
    
    public void setIndexStudyIds(String indexStudyIds) {
        this.indexStudyIds = indexStudyIds;
    }
    
    public String getFixGlobalId() {
        return fixGlobalId; 
    }
    
    public void setFixGlobalId(String gid) {
        fixGlobalId = gid; 
    }

    public String getStudyIdRange() {
        return studyIdRange; 
    }
    
    public void setStudyIdRange(String sir) {
        studyIdRange = sir; 
    }
    
    public String getHandleCheckReport() {
        return handleCheckReport; 
    }
    
    public void setHandleCheckReport(String hcr) {
        handleCheckReport = hcr; 
    }

    private boolean deleteLockDisabled;
    
    public String getIndexLocks(){
        String indexLocks = "There is no index lock at this time";
        deleteLockDisabled = true;
        File lockFile = getLockFile();
        if (lockFile != null) {
            indexLocks = "There has been a lock on the index since " + (new Date(lockFile.lastModified())).toString();
            deleteLockDisabled = false;
        }
        return indexLocks;
    }

    private File getLockFileFromDir(File lockFileDir) {
        File lockFile=null;
        if (lockFileDir.exists()) {
            File[] locks = lockFileDir.listFiles(new IndexLockFileNameFilter());
            if (locks.length > 0) {
                lockFile = locks[0];
            }
        }
        return lockFile;
    }
    
    private File getLockFile() {
        File lockFile = null;
        File lockFileDir = null;
        String lockDir = System.getProperty("org.apache.lucene.lockDir");
        if (lockDir != null) {
            lockFileDir = new File(lockDir);
            lockFile = getLockFileFromDir(lockFileDir);
        } else {
            lockFileDir = new File(Indexer.getInstance().getIndexDir());
            lockFile = getLockFileFromDir(lockFileDir);
        }
        return lockFile;

    }
    
    public String indexAll_action() {
        try {
            File indexDir = new File( Indexer.getInstance().getIndexDir() );
            if (!indexDir.exists() || indexDir.list().length == 0) {
                indexService.indexAll();
                 addMessage( "indexMessage", "Reindexing completed." );
            } else {
                addMessage( "indexMessage", "Reindexing failed: The index directory must be empty before 'index all' can be run." );
            }
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "indexMessage", "Reindexing failed: An unknown error occurred trying to reindex the DVN." );
        } 
       
        return null;
    }
    
    public String indexDV_action() {
        try {
            VDC vdc =  vdcService.findById( new Long( indexDVId) );
            if (vdc != null) { 
                List studyIDList = new ArrayList();
                for (Study study :  vdc.getOwnedStudies() ) {
                    studyIDList.add( study.getId() );
                }
                indexService.updateIndexList( studyIDList );
                addMessage( "indexMessage", "Indexing completed (for dataverse id = " + indexDVId + ")" );
            } else {
                addMessage( "indexMessage", "Indexing failed: There is no dataverse with dvId = " + indexDVId );                    
            }
        } catch (NumberFormatException nfe) {
            addMessage( "indexMessage", "Indexing failed: The dataverse id must be of type Long." );
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "indexMessage", "Indexing failed: An unknown error occurred trying to index dataverse with id = " + indexDVId );
        } 
            
        return null;
    }    
    
    public String indexStudies_action() {
        try {
            Map tokenizedLists = determineStudyIds(indexStudyIds);
            indexService.updateIndexList( (List) tokenizedLists.get("idList") );

            addMessage( "indexMessage", "Indexing request completed." );
            addStudyMessages( "indexMessage", tokenizedLists);

        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "indexMessage", "Indexing failed: An unknown error occurred trying to index the following: \"" + indexStudyIds + "\"" );
        }            
 
        return null;
    }   
    
    public String indexLocks_action(){
        File lockFile = getLockFile();
        if (lockFile.exists()){
            if (lockFile.delete()){
                addMessage("indexMessage", "Index lock deleted");
            } else {
                addMessage("indexMessage", "Index lock could not be deleted");
            }
        }
        return null;
    }
    
    public String indexBatch_action() {
        try {
            indexService.indexBatch();
            addMessage("indexMessage", "Indexing update completed.");
        } catch (Exception e) {
            e.printStackTrace();
            addMessage("indexMessage", "Indexing failed: An unknown error occurred trying to update the index");
        }
        return null;
    }
    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="export utilities">    
    public boolean isExportPanelRendered() {
        return "export".equals(selectedPanel);
    }
    
    String exportFormat;
    String exportDVId;
    String exportStudyIds;
    
    public String getExportFormat() {
        return exportFormat;
    }

    public void setExportFormat(String exportFormat) {
        this.exportFormat = exportFormat == null || exportFormat.equals("") ? null : exportFormat;
    }

    public String getExportDVId() {
        return exportDVId;
    }

    public void setExportDVId(String exportDVId) {
        this.exportDVId = exportDVId;
    }

    public String getExportStudyIds() {
        return exportStudyIds;
    }

    public void setExportStudyIds(String exportStudyIds) {
        this.exportStudyIds = exportStudyIds;
    }
    
     public String exportUpdated_action() {
        try {
            studyService.exportUpdatedStudies();
            addMessage( "exportMessage", "Export succeeded (for updated studies).");
        } catch (Exception e) {
            addMessage( "exportMessage", "Export failed: Exception occurred while exporting studies.  See export log for details.");   
        }
        return null;
    }  
     
     public String updateHarvestStudies_action() {
        try {
            harvestStudyService.updateHarvestStudies();
            addMessage( "exportMessage", "Update Harvest Studies succeeded.");
        } catch (Exception e) {
            addMessage( "exportMessage", "Export failed: An unknown exception occurred while updating harvest studies.");   
        }
        return null;
    }       
     
    public String exportAll_action() {
        try {
            List<Long> allStudyIds = studyService.getAllStudyIds();
            studyService.exportStudies(allStudyIds, exportFormat);
            addMessage( "exportMessage", "Export succeeded for all studies." );

        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "exportMessage", "Export failed: An unknown error occurred trying to export all studies." );
        }    
        
        return null;
    }  
    
    public String exportDV_action() {
        try {
            VDC vdc =  vdcService.findById( new Long(exportDVId) );
            if (vdc != null) { 
                List studyIDList = new ArrayList();
                for (Study study :  vdc.getOwnedStudies() ) {
                    studyIDList.add( study.getId() );
                }

                studyService.exportStudies(studyIDList, exportFormat);
                addMessage( "exportMessage", "Export succeeded (for dataverse id = " + exportDVId + ")" );
            } else {
                addMessage( "exportMessage", "Export failed: There is no dataverse with dvId = " + exportDVId );                    
            }
        } catch (NumberFormatException nfe) {
            addMessage( "exportMessage", "Export failed: The dataverse id must be of type Long.");
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "exportMessage", "Export failed: An unknown error occurred trying to export dataverse with id = " + exportDVId );
        } 
        
        return null;
    }      
    
    public String exportStudies_action() {
        try {
            Map tokenizedLists = determineStudyIds(exportStudyIds);
            studyService.exportStudies( (List) tokenizedLists.get("idList"), exportFormat );
         
            addMessage( "exportMessage", "Export request completed." );
            addStudyMessages( "exportMessage", tokenizedLists);

        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "exportMessage", "Export failed: An unknown error occurred trying to export the following: \"" + exportStudyIds + "\"" );
        }        
        return null;
    }    
    // </editor-fold>        

    
    // <editor-fold defaultstate="collapsed" desc="harvest utilities">  
    public boolean isHarvestPanelRendered() {
        return "harvest".equals(selectedPanel);
    }
    
    Long harvestDVId;
    String harvestIdentifier;

    public Long getHarvestDVId() {
        return harvestDVId;
    }

    public void setHarvestDVId(Long harvestDVId) {
        this.harvestDVId = harvestDVId;
    }

    public String getHarvestIdentifier() {
        return harvestIdentifier;
    }

    public void setHarvestIdentifier(String harvestIdentifier) {
        this.harvestIdentifier = harvestIdentifier;
    }
    
    public List<SelectItem> getHarvestDVs() {
        List harvestDVSelectItems = new ArrayList<SelectItem>();
        Iterator iter = harvestingDataverseService.findAll().iterator();
        while (iter.hasNext()) {
            HarvestingDataverse hd = (HarvestingDataverse) iter.next();
            harvestDVSelectItems.add( new SelectItem(hd.getId(), hd.getVdc().getName()) );
            
        }
        return harvestDVSelectItems;
    }
    
    public String harvestStudy_action() {
        String link = null;
        HarvestingDataverse hd = null;
        try {
            hd = harvestingDataverseService.find( harvestDVId );
            Long studyId = harvesterService.getRecord(hd, harvestIdentifier, hd.getHarvestFormatType().getMetadataPrefix());
            
            if (studyId != null) {
                indexService.updateStudy(studyId);
                
                // create link String
                HttpServletRequest req = (HttpServletRequest) getExternalContext().getRequest();
                link = req.getScheme() +"://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() 
                        + "/dv/" + hd.getVdc().getAlias() + "/faces/study/StudyPage.xhtml?studyId=" + studyId;
            }
                       
            addMessage( "harvestMessage", "Harvest succeeded" + (link == null ? "." : ": " + link ) );
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "harvestMessage", "Harvest failed: An unexpected error occurred trying to get this record." );
            addMessage( "harvestMessage", "Exception message: " + e.getMessage() );
            addMessage( "harvestMessage", "Harvest URL: " + hd.getServerUrl() + "?verb=GetRecord&identifier=" + harvestIdentifier + "&metadataPrefix=" + hd.getHarvestFormatType().getMetadataPrefix() );
        }
       
        return null;
    }    
    // </editor-fold>        

    
    // <editor-fold defaultstate="collapsed" desc="file utilities">    
    public boolean isFilePanelRendered() {
        return "file".equals(selectedPanel);
    }
    
    String fileExtension;
    String fileStudyIds;

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileStudyIds() {
        return fileStudyIds;
    }

    public void setFileStudyIds(String fileStudyIds) {
        this.fileStudyIds = fileStudyIds;
    }
  
    public String determineFileTypeForExtension_action() {
        try {
            List<FileMetadata> fileMetadatas = studyFileService.getStudyFilesByExtension(fileExtension);
            Map<String,Integer> fileTypeCounts = new HashMap<String,Integer>();
            
            for ( FileMetadata fmd : fileMetadatas ) {
                StudyFile sf = fmd.getStudyFile();
                String newFileType = FileUtil.determineFileType( fmd );
                sf.setFileType( newFileType );
                studyFileService.updateStudyFile(sf);
                
                Integer count = fileTypeCounts.get(newFileType);
                if ( fileTypeCounts.containsKey(newFileType)) {
                    fileTypeCounts.put( newFileType, fileTypeCounts.get(newFileType) + 1 );     
                } else {
                    fileTypeCounts.put( newFileType, 1 );    
                }
            }
            
            addMessage( "fileMessage", "Determine File Type request completed for extension ." + fileExtension );
            for (String key : fileTypeCounts.keySet()) {
                addMessage( "fileMessage", fileTypeCounts.get(key) + (fileTypeCounts.get(key) == 1 ? " file" : " files") + " set to type: " + key);                
            }

        
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "fileMessage", "Request failed: An unknown error occurred trying to process the following extension: \"" + fileExtension + "\"" );
        }       

        return null;
    }
    
    public String determineFileTypeForStudies_action() {
        try {
            Map tokenizedLists = determineStudyIds(fileStudyIds);

            for (Iterator<Long>  iter = ((List<Long>) tokenizedLists.get("idList")).iterator(); iter.hasNext();) {
                Long studyId = iter.next();
                Study study = studyService.getStudy(studyId);
                // determine the file type for the latest version of the study
                for ( FileMetadata fmd : study.getLatestVersion().getFileMetadatas() ) {
                    fmd.getStudyFile().setFileType( FileUtil.determineFileType( fmd ) );
                } 
                
                studyService.updateStudy(study);
            }          
        
            addMessage( "fileMessage", "Determine File Type request completed." );
            addStudyMessages("fileMessage", tokenizedLists);
            
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "fileMessage", "Request failed: An unknown error occurred trying to process the following: \"" + fileStudyIds + "\"" );
        }            
 
        return null;
    }   
    // </editor-fold>
    
   
    // <editor-fold defaultstate="collapsed" desc="import utilities">    
    public boolean isImportPanelRendered() {
        return "import".equals(selectedPanel);
    } 
    
    private Long importDVId;
    private Long importFileFormat;
    private String importBatchDir;
    
    // file upload completed percent (Progress)
    private int fileProgress;
    // render manager for the application, uses session id for on demand
    // render group.
    private String sessionId;
    //L.A.private RenderManager renderManager;
    //L.A.private PersistentFacesState persistentFacesState;
    public static final Log mLog = LogFactory.getLog(UtilitiesPage.class);
    //L.A.private InputFile inputFile; 
    public Long getImportDVId() {
        return importDVId;
    }

    public void setImportDVId(Long importDVId) {
        this.importDVId = importDVId;
    }

    public Long getImportFileFormat() {
        return importFileFormat;
    }

    public void setImportFileFormat(Long importFileFormat) {
        this.importFileFormat = importFileFormat;
    }

    public String getImportBatchDir() {
        return importBatchDir;
    }

    public void setImportBatchDir(String importBatchDir) {
        this.importBatchDir = importBatchDir;
    }
    public int getFileProgress(){
        return fileProgress;
    }
    public void setFileProgress(int p){
        fileProgress=p;
    }  
    //L.A.public InputFile getInputFile(){
    //L.A.    return inputFile;
    //L.A. }
           
    //L.A. public void setInputFile(InputFile in){
    //L.A.    inputFile = in;
    //L.A.}
  
     public List<SelectItem> getImportDVs() {
        List importDVsSelectItems = new ArrayList<SelectItem>();
        Iterator iter = vdcService.findAllNonHarvesting().iterator();
        while (iter.hasNext()) {
            VDC vdc = (VDC) iter.next();
            importDVsSelectItems.add( new SelectItem(vdc.getId(), vdc.getName()) );
            
        }
        return importDVsSelectItems;        
    } 
     
    public List<SelectItem> getImportFileFormatTypes() {
        List<SelectItem> metadataFormatsSelect = new ArrayList<SelectItem>();
                
        for (HarvestFormatType hft : harvesterService.findAllHarvestFormatTypes() ) {
            metadataFormatsSelect.add(new SelectItem(hft.getId(),hft.getName()));
        }

        return metadataFormatsSelect;
    }      
     
    public String importBatch_action() {
        FileHandler logFileHandler = null;
        Logger importLogger = null;
        
        if(importBatchDir==null || importBatchDir.equals("")) return null;
        try {
            int importFailureCount = 0;
            int fileFailureCount = 0;
            List<Long> studiesToIndex = new ArrayList<Long>();
            //sessionId =  ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false)).getId();

            File batchDir = new File(importBatchDir);
            if (batchDir.exists() && batchDir.isDirectory()) {
 
                // create Logger
                String logTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss").format(new Date());
                String dvAlias = vdcService.find(importDVId).getAlias();
                importLogger = Logger.getLogger("edu.harvard.iq.dvn.core.web.networkAdmin.UtilitiesPage." + dvAlias + "_" + logTimestamp);
                String logFileName = FileUtil.getImportFileDir() + File.separator + "batch_" + dvAlias + "_" + logTimestamp + ".log";
                logFileHandler = new FileHandler(logFileName);                
                importLogger.addHandler(logFileHandler ); 
               
                importLogger.info("BEGIN BATCH IMPORT (dvId = " + importDVId + ") from directory: " + importBatchDir);
                
                for (int i=0; i < batchDir.listFiles().length; i++ ) {
                    File studyDir = batchDir.listFiles()[i];
                    if (studyDir.isDirectory()) { // one directory per study
                        importLogger.info("Found study directory: " + studyDir.getName());
                        
                        File xmlFile = null;
                        Map<File,String> filesToUpload = new HashMap();
                        
                        for (int j=0; j < studyDir.listFiles().length; j++ ) {
                            File file = studyDir.listFiles()[j];
                            if ( "study.xml".equals(file.getName()) ) {
                                xmlFile = file;
                            } else {
                                addFile(file, "", filesToUpload);
                            }
                        }
                        
                        if (xmlFile != null) {                                
                            try {
                                importLogger.info("Found study.xml and " + filesToUpload.size() + " other " + (filesToUpload.size() == 1 ? "file." : "files."));
                                // TODO: we need to incorporate the add files step into the same transaction of the import!!!
                                Study study = studyService.importStudy( 
                                        xmlFile, importFileFormat, importDVId, getVDCSessionBean().getLoginBean().getUser().getId());
                                study.getLatestVersion().setVersionNote("Study imported via batch import.");
                                importLogger.info("Import of study.xml succeeded: study id = " + study.getId());
                                studiesToIndex.add(study.getId());
                                
                                if ( !filesToUpload.isEmpty() ) {

                                    List<StudyFileEditBean> fileBeans = new ArrayList();
                                    for (File file : filesToUpload.keySet()) {
                                        StudyFileEditBean fileBean = new StudyFileEditBean( file, studyService.generateFileSystemNameSequence(), study );
                                        fileBean.getFileMetadata().setCategory (filesToUpload.get(file));
                                        fileBeans.add(fileBean);
                                    }

                                    try {
                                        studyFileService.addFiles( study.getLatestVersion(), fileBeans, getVDCSessionBean().getLoginBean().getUser() );
                                        importLogger.info("File upload succeeded.");
                                    } catch (Exception e) {
                                        fileFailureCount++;
                                        importLogger.severe("File Upload failed (dir = " + studyDir.getName() + "): exception message = " + e.getMessage());
                                        logException (e, importLogger);
                                    }
                                }
  
                            } catch (Exception e) {
                                importFailureCount++;
                                importLogger.severe("Import failed (dir = " + studyDir.getName() + "): exception message = " + e.getMessage());
                                logException (e, importLogger);
                            }
                            
                            
                        } else { // no ddi.xml found in studyDir
                            importLogger.warning("No study.xml file was found in study directory. Skipping... ");    
                        }
                    } else {
                        importLogger.warning("Found non directory at top level. Skipping... (filename = " + studyDir.getName() +")");
                    }
                }
                
                // generate status message
                String statusMessage = studiesToIndex.size() + (studiesToIndex.size() == 1 ? " study" : " studies") + " successfully imported";
                statusMessage += (fileFailureCount == 0 ? "" : " (" + fileFailureCount + " of which failed file upload)");
                statusMessage += (importFailureCount == 0 ? "." : "; " + importFailureCount + (importFailureCount == 1 ? " study" : " studies") + " failed import.");                 
                
                importLogger.info("COMPLETED BATCH IMPORT: " + statusMessage );
                
                // now index all studies
                importLogger.info("POST BATCH IMPORT, start calls to index.");
                indexService.updateIndexList(studiesToIndex);
                importLogger.info("POST BATCH IMPORT, calls to index finished.");

                
                addMessage( "importMessage", "Batch Import request completed." );
                addMessage( "importMessage", statusMessage );
                addMessage( "importMessage", "For more detail see log file at: " + logFileName );

            } else {
                addMessage( "importMessage", "Batch Import failed: " + importBatchDir + " does not exist or is not a directory." );    
            }
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "importMessage", "Batch Import failed: An unexpected error occurred during processing." );
            addMessage( "importMessage", "Exception message: " + e.getMessage() );
        } finally {
            if ( logFileHandler != null ) {
                logFileHandler.close();
                importLogger.removeHandler(logFileHandler);
            }
         //   importBatchDir = "";
        }

        return null;       
    }

    private void addFile(File file, String catName, Map<File,String> filesToUpload) throws Exception{
        if ( file.getName()!= null && file.getName().startsWith(".")) {
             // ignore hidden files (ie files that start with "."
        } else if (file.isDirectory()) {
            String tempCatName = StringUtil.isEmpty(catName) ?  file.getName() : catName + " - " + file.getName();
            for (int j=0; j < file.listFiles().length; j++ ) {
                addFile( file.listFiles()[j], tempCatName, filesToUpload );
            }
        } else {
            File tempFile = FileUtil.createTempFile( sessionId, file.getName() );
            FileUtil.copyFile(file, tempFile);
            filesToUpload.put(tempFile, catName);
        }
    }

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
    //L.A.public PersistentFacesState getState() {
    //L.A.    return persistentFacesState;
    //L.A.}

    //L.A.public void setRenderManager(RenderManager renderManager) {
    //L.A.    this.renderManager = renderManager;
    //L.A    renderManager.getOnDemandRenderer(sessionId).add(this);
        
    //L.A.}
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
    /*
     Commenting out the entire method -- L.A.
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
     L.A. */
   
    /**
     * Dispose callback called due to a view closing or session
     * invalidation/timeout
     */

    public void dispose() throws Exception {
               
       if (mLog.isTraceEnabled()) {
            mLog.trace("OutputProgressController dispose OnDemandRenderer for session: " + sessionId);
        }
        //L.A. renderManager.getOnDemandRenderer(sessionId).remove(this); 
        //L.A. renderManager.getOnDemandRenderer(sessionId).dispose();
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
        //L.A.InputFile ifile = (InputFile) event.getSource();
        //L.A.fileProgress = ifile.getFileInfo().getPercent();
        getImportFileFormat();
        getImportDVId();
        //  System.out.println("sessid "+ sessionId);
        //getImportFileFormat()getImportFileFormat() System.out.println("render "+ renderManager.getOnDemandRenderer(sessionId).toString()); 
        //L.A.if (persistentFacesState !=null) {
        //L.A.    renderManager.getOnDemandRenderer(sessionId).requestRender();} 
    }  
   
    public String importSingleFile_action(){
        //L.A.if(inputFile==null) return null; 
        //L.A.File originalFile = inputFile.getFile();
          
        //File originalFile = null; 
        
        if (uploadedDdiFile != null) {
            
            try {
        
                Study study = studyService.importStudy( 
                    uploadedDdiFile,getImportFileFormat(), getImportDVId(), getVDCSessionBean().getLoginBean().getUser().getId());
                indexService.updateStudy(study.getId());
                // create result message
                HttpServletRequest req = (HttpServletRequest) getExternalContext().getRequest();
                String studyURL = req.getScheme() +"://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() 
                    + "/dv/" + study.getOwner().getAlias() + "/faces/study/StudyPage.xhtml?globalId=" + study.getGlobalId();

                addMessage( "importMessage", "Import succeeded." );
                addMessage( "importMessage", "Study URL: " + studyURL );

            }catch(Exception e) {
                e.printStackTrace();
                addMessage( "harvestMessage", "Import failed: An unexpected error occurred trying to import this study." );
                addMessage( "harvestMessage", "Exception message: " + e.getMessage() );            
            }
        }
                
        return null;  
    }
        
   public String uploadFile() {
     
       //L.A. inputFile =getInputFile();  
       
        String str="";
   
        
        /* Commenting out everything InputFile-related: -- L.A. 
	if (inputFile.getStatus() != InputFile.SAVED){
            str = "File " + inputFile.getFileInfo().getFileName()+ " has not been saved. \n"+
                    "Status: "+ inputFile.getStatus();
            logger.info(str); 
            addMessage("importMessage",str);
           if(inputFile.getStatus() != InputFile.INVALID) 
            return null;
          }
         -- L.A. */
       return null; 
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

        uploadedDdiFile = uploadedFile; 
    }
  
// </editor-fold>

    
    // <editor-fold defaultstate="collapsed" desc="delete utilities">    
    public boolean isDeletePanelRendered() {
        return "delete".equals(selectedPanel);
    }
    
    String deleteStudyIds;

    public String getDeleteStudyIds() {
        return deleteStudyIds;
    }

    public void setDeleteStudyIds(String deleteStudyIds) {
        this.deleteStudyIds = deleteStudyIds;
    }

  
    
    public String deleteStudies_action() {
        try {
            Map tokenizedLists = determineStudyIds(deleteStudyIds);
            studyService.deleteStudyList( (List) tokenizedLists.get("idList") );

            addMessage( "deleteMessage", "Delete request completed." );
            addStudyMessages( "deleteMessage", tokenizedLists);

        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "deleteMessage", "Delete failed: An unknown error occurred trying to delete the following: \"" + deleteStudyIds + "\"" );
        }            
 
        return null;
    }   
    

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="handle utilities">
     public boolean isHandlePanelRendered() {
        return "handle".equals(selectedPanel);
    }

       public String handleRegisterAll_action() {
        try {
            gnrsService.registerAll();

            addMessage( "handleMessage", "Handle registration request completed." );

        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "handleMessage", "Handle registration failed: An unknown error occurred trying to index the following: \"" + indexStudyIds + "\"" );
        }

        return null;
    }


    
        public String handleFixAll_action() {
        try {
            gnrsService.fixAll();

            addMessage( "handleMessage", "Handle re-registration request completed." );

        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "handleMessage", "Handle registration failed: An unknown error occurred trying to index the following: \"" + indexStudyIds + "\"" );
        }

        return null;
    }
        
    public String handleFixSingle_action() {
        // TODO: also need a method to do this on a range of study IDs (?)
        String hdl = fixGlobalId; 
        
        hdl = hdl.replaceFirst("^hdl:", "");
        
        try {
            gnrsService.fixHandle(hdl);

            addMessage( "handleMessage", "Re-registered handle "+hdl );

        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "handleMessage", "Handle (re)registration failed for \"" + hdl + "\"" );
        }

        return null;
    }
    
    
    public String handleCheckRange_action() {
        handleCheckReport = null; 
        
        if (studyIdRange == null || 
                !(studyIdRange.matches("^[0-9][0-9]*$") || 
                studyIdRange.matches("^[0-9][0-9]*\\-[0-9][0-9]*$"))) {
            addMessage("handleMessage", "Invalid study ID range!");
            return null; 
            
        }
        
        String checkOutput = ""; 
        
        if (studyIdRange.indexOf('-') > 0) {
            // range: 
            Long idStart = null; 
            Long idEnd = null; 
            
            try {
                String rangeStart = studyIdRange.substring(0, studyIdRange.indexOf('-'));
                String rangeEnd = studyIdRange.substring(studyIdRange.indexOf('-')+1);
 
                idStart = new Long (rangeStart);
                idEnd = new Long (rangeEnd); 
            } catch (Exception ex) {
                addMessage("handleMessage", "Invalid study ID range: "+studyIdRange);
                return null; 
            }
                
            if (!(idStart.compareTo(idEnd) < 0)) {
                addMessage("handleMessage", "Invalid numeric range: " + studyIdRange);
                return null;
            }

            Long studyId = idStart;

            while (studyId.compareTo(idEnd) <= 0) {
                try {
                    Study chkStudy = studyService.getStudy(studyId);
                    String chkHandle = chkStudy.getAuthority() + "/" + chkStudy.getStudyId();
                    if (gnrsService.isHandleRegistered(chkHandle)) {
                        checkOutput = checkOutput.concat(studyId + "\thdl:" + chkHandle + "\tok\n");
                    } else {
                        checkOutput = checkOutput.concat(studyId + "\thdl:" + chkHandle + "\tNOT REGISTERED\n");
                    }
                    
                } catch (Exception ex) {
                    checkOutput = checkOutput.concat(studyId + "\t\tNO SUCH STUDY\n");
                }
                studyId = studyId + 1;
            }
            //addMessage("handleMessage", checkOutput);
            handleCheckReport = checkOutput; 

            
            
        } else {
            // single id: 
            try {
                Long studyId = new Long (studyIdRange);
                Study chkStudy = studyService.getStudy(studyId);
                String chkHandle = chkStudy.getAuthority() + "/" + chkStudy.getStudyId();
                if (gnrsService.isHandleRegistered(chkHandle)) {
                    checkOutput = studyId + "\thdl:" + chkHandle + "\t\tok\n";
                } else {
                    checkOutput = studyId + "\thdl:" + chkHandle + "\t\tNOT REGISTERED\n";
                }

                //addMessage("handleMessage", checkOutput);
                handleCheckReport = checkOutput;
                
            } catch (Exception ex) {
                addMessage("handleMessage", "No such study: id="+studyIdRange);
            }
        }
        return null; 
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="study utilities">
    public boolean isStudyPanelRendered() {
        return "study".equals(selectedPanel);
    }

    String createStudyDraftIds;

    public String getCreateStudyDraftIds() {
        return createStudyDraftIds;
    }

    public void setCreateStudyDraftIds(String createStudyDraftIds) {
        this.createStudyDraftIds = createStudyDraftIds;
    }





    public String createStudyDrafts_action() {
        try {
            Map tokenizedLists = determineStudyIds(createStudyDraftIds);
            List ignoredList = new ArrayList();

            for (Iterator it = ((List) tokenizedLists.get("idList")).iterator(); it.hasNext();) {
                Long studyId = (Long) it.next();
                Study study = studyService.getStudy(studyId);
                Long currentVersionNumber = study.getLatestVersion().getVersionNumber();
                StudyVersion editVersion = study.getEditVersion();
                if ( currentVersionNumber.equals(editVersion.getVersionNumber()) ){
                    // working copy already exists
                    it.remove();
                    ignoredList.add(studyId);
                } else {
                    // save new version
                    studyService.saveStudyVersion(editVersion, getVDCSessionBean().getLoginBean().getUser().getId());
                    studyService.updateStudyVersion(editVersion);
                }
            }

            tokenizedLists.put("ignoredList", ignoredList );
            tokenizedLists.put("ignoredReason", "working verison already exists" );

            addMessage( "studyMessage", "Create Study Draft request completed." );
            addStudyMessages( "studyMessage", tokenizedLists);

        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "studyMessage", "Create Drafts failed: An unknown error occurred trying to delete the following: \"" + deleteStudyIds + "\"" );
        }

        return null;
    }


    // </editor-fold>

    // ****************************
    // Common methods
    // ****************************
    
    private void addMessage(String component, String message) {
        FacesMessage facesMsg = new FacesMessage(message);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(component, facesMsg);          
    }
    
    private void addStudyMessages (String component, Map tokenizedLists) {

            if ( tokenizedLists.get("idList") != null && !((List) tokenizedLists.get("idList")).isEmpty() ) {            
                addMessage( component, "The following studies were successfully processed: " + tokenizedLists.get("idList") );
            }
            if ( tokenizedLists.get("ignoredList") != null && !((List) tokenizedLists.get("ignoredList")).isEmpty() ) {            
                addMessage( component, "The following studies were ignored (" 
                        + ((String) tokenizedLists.get("ignoredReason")) + "): " + tokenizedLists.get("ignoredList") );
            }            
            if ( tokenizedLists.get("invalidStudyIdList") != null && !((List) tokenizedLists.get("invalidStudyIdList")).isEmpty() ) {
                addMessage( component, "The following study ids were invalid: " + tokenizedLists.get("invalidStudyIdList") ); 
            }
            if ( tokenizedLists.get("failedTokenList") != null && !((List) tokenizedLists.get("failedTokenList")).isEmpty() ) {
                addMessage( component, "The following tokens could not be interpreted: " + tokenizedLists.get("failedTokenList") );
            }
    }

    
    private Map determineIds(String ids) {
        List<Long> idList = new ArrayList();
        List<String> failedTokenList = new ArrayList(); 
                    
        StringTokenizer st = new StringTokenizer(ids, ",; \t\n\r\f");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            
            try {
                idList.add( new Long(token) );
            } catch (NumberFormatException nfe) {
                if ( token.indexOf("-") == -1 ) {
                    failedTokenList.add(token);
                } else {
                    try {
                        Long startId = new Long( token.substring( 0, token.indexOf("-") ) );
                        Long endId = new Long( token.substring( token.indexOf("-") + 1 ) );  
                        for (long i = startId.longValue(); i <= endId.longValue(); i++) {
                            idList.add( new Long(i) );
                        }
                    } catch (NumberFormatException nfe2) {
                        failedTokenList.add( token );
                    }
                }
            }
        }
        
        Map returnMap = new HashMap();
        returnMap.put("idList", idList);
        returnMap.put("failedTokenList", failedTokenList);
        
        return returnMap;
    }   


    private Map determineStudyIds(String studyIds) {
        Map tokenizedLists = determineIds(studyIds);
        List invalidStudyIdList = new ArrayList();

        for (Iterator<Long>  iter = ((List<Long>) tokenizedLists.get("idList")).iterator(); iter.hasNext();) {
            Long id = iter.next();
            try {
                studyService.getStudy(id);
            } catch (EJBException e) {  
                if (e.getCause() instanceof IllegalArgumentException) {
                    invalidStudyIdList.add(id);
                    iter.remove();
                } else {
                    throw e;
                }
            }  
        }

        tokenizedLists.put("invalidStudyIdList", invalidStudyIdList);
        return tokenizedLists;
    }
    
    // duplicate from harvester
    private void logException(Throwable e, Logger logger) {

        boolean cause = false;
        String fullMessage = "";
        do {
            String message = e.getClass().getName() + " " + e.getMessage();
            if (cause) {
                message = "\nCaused By Exception.................... " + e.getClass().getName() + " " + e.getMessage();
            }
            StackTraceElement[] ste = e.getStackTrace();
            message += "\nStackTrace: \n";
            for (int m = 0; m < ste.length; m++) {
                message += ste[m].toString() + "\n";
            }
            fullMessage += message;
            cause = true;
        } while ((e = e.getCause()) != null);
        logger.severe(fullMessage);
    }

    public boolean isDeleteLockDisabled() {
        return deleteLockDisabled;
    }

    public void setDeleteLockDisabled(boolean deleteLockDisabled) {
        this.deleteLockDisabled = deleteLockDisabled;
    }
}
