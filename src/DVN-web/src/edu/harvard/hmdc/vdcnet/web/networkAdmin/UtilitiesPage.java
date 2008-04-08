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

import com.sun.rave.web.ui.model.UploadedFile;
import edu.harvard.hmdc.vdcnet.harvest.HarvestFormatType;
import edu.harvard.hmdc.vdcnet.harvest.HarvesterServiceLocal;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author gdurand
 */
public class UtilitiesPage extends VDCBaseBean implements java.io.Serializable   {

    private static final Logger logger = Logger.getLogger("edu.harvard.hmdc.vdcnet.web.networkAdmin.UtilitiesPage");
            
    @EJB StudyServiceLocal studyService;
    @EJB IndexServiceLocal indexService;
    @EJB HarvestingDataverseServiceLocal harvestingDataverseService;    
    @EJB HarvesterServiceLocal harvesterService;
    @EJB VDCServiceLocal vdcService;
    
    private String selectedPanel;
    private Long vdcId;

    
    /** Creates a new instance of ImportStudyPage */
    public UtilitiesPage() {
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
    
    public String indexAll_action() {
        try {
            //first delete files
            boolean deleteFailed = false;
            File indexDir = new File("index-dir");
            File[] files = indexDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                if ( !files[i].delete() ) {
                    deleteFailed = true;
                }
            }

            if (!deleteFailed) {
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
                        + "/dv/" + hd.getVdc().getAlias() + "/faces/study/StudyPage.jsp?studyId=" + studyId;
            }
                       
            addMessage( "harvestMessage", "Harvest succeeded" + (link == null ? "." : ": " + link ) );
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "harvestMessage", "Harvest failed: An unexpected error occurred trying to get this record." );
            addMessage( "harvestMessage", "Exception message: " + e.getMessage() );
            addMessage( "harvestMessage", "Harvest URL: " + hd.getOaiServer() + "?verb=GetRecord&identifier=" + harvestIdentifier + "&metadataPrefix=" + hd.getHarvestFormatType().getMetadataPrefix() );
        }
       
        return null;
    }    
    // </editor-fold>        

    
    // <editor-fold defaultstate="collapsed" desc="file utilities">    
    public boolean isFilePanelRendered() {
        return "file".equals(selectedPanel);
    }
    
    String fileStudyIds;

    public String getFileStudyIds() {
        return fileStudyIds;
    }

    public void setFileStudyIds(String fileStudyIds) {
        this.fileStudyIds = fileStudyIds;
    }
  
    
    public String determineFileTypeStudies_action() {
        try {
            Map tokenizedLists = determineStudyIds(fileStudyIds);
            List harvestedStudies = new ArrayList();

            for (Iterator<Long>  iter = ((List<Long>) tokenizedLists.get("idList")).iterator(); iter.hasNext();) {
                Long studyId = iter.next();
                Study study = studyService.getStudy(studyId);
                
                if ( study.isIsHarvested() ) {
                    harvestedStudies.add(study.getId());
                    iter.remove();
                 } else {
                    for ( StudyFile sf : study.getStudyFiles() ) {
                        if (sf.isSubsettable()) {
                            // skip subsettable files
                        } else {
                            sf.setFileType( FileUtil.determineFileType( new File( sf.getFileSystemLocation() ), sf.getFileName() ) );
                        }
                    } 
                    studyService.updateStudy(study);
                }          
            }
            tokenizedLists.put("ignoredList", harvestedStudies);
            tokenizedLists.put("ignoredReason", "because they are harvested studies");
            addMessage( "fileMessage", "Determine File Type request completed." );
            addStudyMessages("fileMessage", tokenizedLists);
            
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "fileMessage", "Action failed: An unknown error occurred trying to process the following: \"" + fileStudyIds + "\"" );
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
    private UploadedFile importFile;

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
    
    public UploadedFile getImportFile() {
        return importFile;
    }

    public void setImportFile(UploadedFile importFile) {
        this.importFile = importFile;
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
        try {
            int studyCount = 0;
            String sessionId =  ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false)).getId();
            
            File batchDir = new File(importBatchDir);
            if (batchDir.exists() && batchDir.isDirectory()) {
                for (int i=0; i < batchDir.listFiles().length; i++ ) {
                    File studyDir = batchDir.listFiles()[i];
                    if (studyDir.isDirectory()) { // one directory per study
                        
                        File xmlFile = null;
                        List<StudyFileEditBean> filesToUpload = new ArrayList<StudyFileEditBean>();
                        
                        for (int j=0; j < studyDir.listFiles().length; j++ ) {
                            File file = studyDir.listFiles()[j];
                            if ( "study.xml".equals(file.getName()) ) {
                                xmlFile = file;
                            } else {
                                File tempFile = FileUtil.createTempFile( sessionId, file.getName() );                               
                                FileUtil.copyFile(file, tempFile);
                                StudyFileEditBean fileBean = new StudyFileEditBean( tempFile, studyService.generateFileSystemNameSequence() );                                
                                filesToUpload.add(fileBean);
                            }
                        }
                        
                        if (xmlFile != null) {
                            try {
                                Study study = studyService.importStudy( 
                                        xmlFile, importFileFormat, importDVId, getVDCSessionBean().getLoginBean().getUser().getId(), filesToUpload);

                                indexService.updateStudy(study.getId());
                                studyCount++;
                            } catch (Exception e) {
                                // handle error
                            }
                            
                            
                        } else { // no ddi.xml found in studyDir
                            
                        }
                    }
                }
                
                addMessage( "importMessage", "Batch Import request completed." );
                addMessage( "importMessage", studyCount + (studyCount == 1 ? " study" : " studies") + " successfully imported." );

            } else {
                addMessage( "importMessage", "Batch Import failed: " + importBatchDir + " does not exist or is not a directory." );    
            }
        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "importMessage", "Batch Import failed: An unexpected error occurred during processing." );
            addMessage( "importMessage", "Exception message: " + e.getMessage() );
        }

        return null;       
    }    

    public String importSingleFile_action() {
        try {
            File xmlFile = File.createTempFile("study", ".xml");
            importFile.write(xmlFile);

            Study study = studyService.importStudy(
                    xmlFile, importFileFormat, importDVId, getVDCSessionBean().getLoginBean().getUser().getId());
            indexService.updateStudy(study.getId());

            // create result message
            HttpServletRequest req = (HttpServletRequest) getExternalContext().getRequest();
            String studyURL = req.getScheme() +"://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() 
                    + "/dv/" + study.getOwner().getAlias() + "/faces/study/StudyPage.jsp?studyId=" + study.getId();

            addMessage( "importMessage", "Import succeeded." );
            addMessage( "importMessage", "Study URL: " + studyURL );

        } catch (Exception e) {
            e.printStackTrace();
            addMessage( "harvestMessage", "Import failed: An unexpected error occurred trying to import this study." );
            addMessage( "harvestMessage", "Exception message: " + e.getMessage() );            
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
}
