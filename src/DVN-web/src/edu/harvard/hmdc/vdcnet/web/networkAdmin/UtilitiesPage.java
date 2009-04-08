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
 *  along with this program; if not, see <http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
/*

 * UtilitiesPage.java
 * 
 * Created on Mar 18, 2008, 3:30:20 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.networkAdmin;


import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.async.render.Renderable;
import edu.harvard.hmdc.vdcnet.harvest.HarvestFormatType;
import edu.harvard.hmdc.vdcnet.harvest.HarvestStudyServiceLocal;
import edu.harvard.hmdc.vdcnet.harvest.HarvesterServiceLocal;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.index.Indexer;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.StudyFileEditBean;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.util.FileUtil;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverse;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverseServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
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
import com.icesoft.faces.component.inputfile.InputFile;
import com.icesoft.faces.webapp.xmlhttp.FatalRenderingException;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;
import com.icesoft.faces.webapp.xmlhttp.TransientRenderingException;
import java.util.Collection;
import java.util.EventObject;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.swing.JFileChooser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.faces.event.ActionEvent;
/**
 *
 * @author gdurand
 */
public class UtilitiesPage extends VDCBaseBean implements java.io.Serializable, Renderable   {

    private static final Logger logger = Logger.getLogger("edu.harvard.hmdc.vdcnet.web.networkAdmin.UtilitiesPage");
            
    @EJB StudyServiceLocal studyService;
    @EJB IndexServiceLocal indexService;
    @EJB HarvestStudyServiceLocal harvestStudyService;
    @EJB HarvestingDataverseServiceLocal harvestingDataverseService;    
    @EJB HarvesterServiceLocal harvesterService;
    @EJB VDCServiceLocal vdcService;
    
    private String selectedPanel;
    private Long vdcId;

    
    /** Creates a new instance of ImportStudyPage */
    public UtilitiesPage() {
         persistentFacesState = PersistentFacesState.getInstance();
     
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
            //first delete files
            boolean deleteSucceded = true;
            File indexDir = new File( Indexer.getInstance().getIndexDir() );
            File[] files = indexDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                if ( !files[i].delete() ) {
                    deleteSucceded = false;
                }
            }

            // check to make sure delete is complete (temp nfs files may still exist)
            if (deleteSucceded && indexDir.list().length > 0) {
                System.out.println("*** INDEX ALL - files still exist in the index dir - sleep for a second");
                deleteSucceded = false;
                for (int count = 0; count < 60; count++) {
                    Thread.sleep(1000);
                    if ( indexDir.list().length == 0 ) {
                        deleteSucceded = true;
                        break;
                    }
                }
            }

            if (deleteSucceded) {
                indexService.indexAll();
                 addMessage( "indexMessage", "Reindexing completed." );
            } else {
                addMessage( "indexMessage", "Reindexing failed: There was a problem deleting the files. Please fix this manually, then try again." );
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
            List<StudyFile> studyFiles = studyService.getStudyFilesByExtension(fileExtension);
            Map<String,Integer> fileTypeCounts = new HashMap<String,Integer>();
            
            for ( StudyFile sf : studyFiles ) {
                String newFileType = FileUtil.determineFileType( sf );     
                sf.setFileType( newFileType );
                studyService.updateStudyFile(sf);
                
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
                    
                for ( StudyFile sf : study.getStudyFiles() ) {
                    sf.setFileType( FileUtil.determineFileType( sf ) );
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
    private RenderManager renderManager;
    private PersistentFacesState persistentFacesState;
    public static final Log mLog = LogFactory.getLog(UtilitiesPage.class);
    private InputFile inputFile; 
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
    public InputFile getInputFile(){
        return inputFile;
    }
           
    public void setInputFile(InputFile in){
        inputFile = in;
    }
  
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
                importLogger = Logger.getLogger("edu.harvard.hmdc.vdcnet.web.networkAdmin.UtilitiesPage." + dvAlias + "_" + logTimestamp);
                String logFileName = FileUtil.getImportFileDir() + File.separator + "batch_" + dvAlias + "_" + logTimestamp + ".log";
                logFileHandler = new FileHandler(logFileName);                
                importLogger.addHandler(logFileHandler ); 
               
                importLogger.info("BEGIN BATCH IMPORT (dvId = " + importDVId + ") from directory: " + importBatchDir);
                
                for (int i=0; i < batchDir.listFiles().length; i++ ) {
                    File studyDir = batchDir.listFiles()[i];
                    if (studyDir.isDirectory()) { // one directory per study
                        importLogger.info("Found study directory: " + studyDir.getName());
                        
                        File xmlFile = null;
                        List<StudyFileEditBean> filesToUpload = new ArrayList<StudyFileEditBean>();
                        
                        for (int j=0; j < studyDir.listFiles().length; j++ ) {
                            File file = studyDir.listFiles()[j];
                            if ( "study.xml".equals(file.getName()) ) {
                                xmlFile = file;
                            } else if ( file.getName()!= null && file.getName().startsWith(".")) {
                                // ignore hidden files (ie files that start with "."
                            } else {
                                File tempFile = FileUtil.createTempFile( sessionId, file.getName() );                               
                                FileUtil.copyFile(file, tempFile);
                                StudyFileEditBean fileBean = new StudyFileEditBean( tempFile, studyService.generateFileSystemNameSequence() );                                
                                filesToUpload.add(fileBean);
                            }
                        }
                        
                        if (xmlFile != null) {                                
                            try {
                                importLogger.info("Found study.xml and " + filesToUpload.size() + " other " + (filesToUpload.size() == 1 ? "file." : "files."));
                                
                                Study study = studyService.importStudy( 
                                        xmlFile, importFileFormat, importDVId, getVDCSessionBean().getLoginBean().getUser().getId());
                                importLogger.info("Import of study.xml succeeded: study id = " + study.getId());
                                studiesToIndex.add(study.getId());
                                
                                if ( !filesToUpload.isEmpty() ) {
                                    try {
                                        studyService.addFiles( study, filesToUpload, getVDCSessionBean().getLoginBean().getUser() );
                                        studyService.updateStudy(study); // for now, must call this to persist the new files
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
        getImportFileFormat();
        getImportDVId();
 //  System.out.println("sessid "+ sessionId);
  //getImportFileFormat()getImportFileFormat() System.out.println("render "+ renderManager.getOnDemandRenderer(sessionId).toString()); 
  if (persistentFacesState !=null) {
         renderManager.getOnDemandRenderer(sessionId).requestRender();} 
        
    }  
   
    public String importSingleFile_action(){
        if(inputFile==null) return null; 
         File originalFile = inputFile.getFile();
        
         try {
        
               Study study = studyService.importStudy( 
                   originalFile,getImportFileFormat(), getImportDVId(), getVDCSessionBean().getLoginBean().getUser().getId());
            indexService.updateStudy(study.getId());
             // create result message
            HttpServletRequest req = (HttpServletRequest) getExternalContext().getRequest();
            String studyURL = req.getScheme() +"://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() 
                    + "/dv/" + study.getOwner().getAlias() + "/faces/study/StudyPage.xhtml?studyId=" + study.getId();

            addMessage( "importMessage", "Import succeeded." );
            addMessage( "importMessage", "Study URL: " + studyURL );

         }catch(Exception e) {
            e.printStackTrace();
            addMessage( "harvestMessage", "Import failed: An unexpected error occurred trying to import this study." );
            addMessage( "harvestMessage", "Exception message: " + e.getMessage() );            
        }
                
        return null;  
    }
   public String uploadFile() {
     
       inputFile =getInputFile();  
       
        String str="";
   
	if (inputFile.getStatus() != InputFile.SAVED){
            str = "File " + inputFile.getFileInfo().getFileName()+ " has not been saved. \n"+
                    "Status: "+ inputFile.getStatus();
            logger.info(str); 
            addMessage("importMessage",str);
           if(inputFile.getStatus() != InputFile.INVALID) 
            return null;
          }
  
       return null; 
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
