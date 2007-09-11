/*
 * StudyServiceBean.java
 *
 * Created on August 14, 2006, 11:47 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.ddi.DDI20ServiceBean;
import edu.harvard.hmdc.vdcnet.ddi.DDI20ServiceLocal;
import edu.harvard.hmdc.vdcnet.dsb.DSBWrapper;
import edu.harvard.hmdc.vdcnet.gnrs.GNRSServiceLocal;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CodeBook;
import edu.harvard.hmdc.vdcnet.util.FileUtil;
import edu.harvard.hmdc.vdcnet.vdc.ReviewState;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollection;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetwork;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateless

public class StudyServiceBean implements edu.harvard.hmdc.vdcnet.study.StudyServiceLocal {
    @PersistenceContext(unitName="VDCNet-ejbPU") EntityManager em;
    @EJB GNRSServiceLocal gnrsService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB DDI20ServiceLocal ddiService;
    @EJB UserServiceLocal userService;
    @EJB IndexServiceLocal indexService;
    @EJB ReviewStateServiceLocal reviewStateService;
    private static final Logger logger = Logger.getLogger("edu.harvard.hmdc.vdcnet.study.StudyServiceBean");
    
    
    private JAXBContext jaxbContext = null;
    private Unmarshaller DDIUnmarshaller;
    
    public void ejbCreate() {
        try {
            jaxbContext = javax.xml.bind.JAXBContext.newInstance("edu.harvard.hmdc.vdcnet.jaxb.ddi20");
            DDIUnmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        }
    }
    
    
    @EJB StudyServiceLocal studyService; // used to force new transaction during import
    
    /**
     * Creates a new instance of StudyServiceBean
     */
    public StudyServiceBean() {
    }
    
    
    /**
     * Add given Study to persistent storage.
     */
    public void addStudy(Study study){
        // For each collection of dependent objects, set the relationship to this study.
        if (study.getStudyAbstracts()!=null ) {
            for (Iterator<StudyAbstract> it = study.getStudyAbstracts().iterator(); it.hasNext();) {
                StudyAbstract elem = it.next();
                elem.setStudy(study);
            }
        }
        if (study.getStudyOtherIds()!=null) {
            for (Iterator<StudyOtherId> it = study.getStudyOtherIds().iterator(); it.hasNext();) {
                
                StudyOtherId elem = it.next();
                elem.setStudy(study);
            }
        }
        
        Template template = study.getTemplate();
        study.setTemplate(null);
        em.persist(study);
        study.setTemplate(template);
        
        
    }
    
    
    public void updateStudy(Study detachedStudy){
        em.merge(detachedStudy);
    }
    
    public Study getStudyByHarvestInfo(String authority, String harvestIdentifier) {
        String queryStr = "SELECT s FROM Study s WHERE s.authority = '"+authority+"' and s.harvestIdentifier = '" + harvestIdentifier+"'" ;
        Query query= em.createQuery(queryStr);
        List resultList = query.getResultList();
        Study study=null;
        if (resultList.size()>1) {
            throw new EJBException("More than one study found with authority= "+authority+" and harvestIdentifier= "+harvestIdentifier);
        }
        if (resultList.size()==1) {
            study = (Study)resultList.get(0);
        }
        return study;
        
    }
    
    
    public void deleteStudy(Long studyId) {
        Study study = em.find(Study.class,studyId);
        
        for (Iterator<VDCCollection>it = study.getStudyColls().iterator(); it.hasNext();) {
            VDCCollection elem =  it.next();
            elem.getStudies().remove(study);
            
        }
        studyService.deleteDataVariables(study.getId());
        
        study.getAllowedGroups().clear();
        study.getAllowedUsers().clear();
        study.getOwner().getOwnedStudies().remove(study);
        study.setOwner(null);
 
        for (Iterator<StudyFile>it = study.getStudyFiles().iterator(); it.hasNext();) {
            StudyFile elem =  it.next();
            elem.getAllowedGroups().clear();
            elem.getAllowedUsers().clear();
            
        }       
        
        File studyDir = new File(FileUtil.getStudyFileDir()+File.separator+ study.getAuthority()+File.separator+study.getStudyId());
        if (studyDir.exists()) {
            File[] files = studyDir.listFiles();
            if (files!=null) {
                for(int i=0;i<files.length;i++) {
                    files[i].delete();
                    
                }
            }
            studyDir.delete();
        }
        
        
        
        // Save Study primary key in DeletedStudy table, so we can export
        // the study deletion to the old VDC.
        DeletedStudy ds = new DeletedStudy();
        ds.setId(study.getId());
        ds.setStudyId(study.getStudyId());
        ds.setAuthority(study.getAuthority());
        em.persist(ds);
        
        em.remove(study);
        gnrsService.delete(study.getAuthority(),study.getStudyId());
        indexService.deleteStudy(studyId);
        
        
        
    }
    
    private static final String DELETE_VARIABLE_CATEGORIES = " delete from variablecategory where datavariable_id in ( "+
            "select dv.id from study s, filecategory fc, studyfile sf, datatable dt, datavariable dv"+
            " where s.id= ? "+
            " and s.id = fc.study_id "+
            " and fc.id= sf.filecategory_id "+
            " and sf.id=dt.studyfile_id "+
            " and dt.id= dv.datatable_id )";
    
    private static final String DELETE_SUMMARY_STATISTICS = " delete from summarystatistic where datavariable_id in ( "+
            "select dv.id from study s, filecategory fc, studyfile sf, datatable dt, datavariable dv"+
            " where s.id= ? "+
            " and s.id = fc.study_id "+
            " and fc.id= sf.filecategory_id "+
            " and sf.id=dt.studyfile_id "+
            " and dt.id= dv.datatable_id )";
    
    private static final String DELETE_VARIABLE_RANGE_ITEMS = " delete from variablerangeitem where datavariable_id in ( "+
            "select dv.id from study s, filecategory fc, studyfile sf, datatable dt, datavariable dv"+
            " where s.id= ? "+
            " and s.id = fc.study_id "+
            " and fc.id= sf.filecategory_id "+
            " and sf.id=dt.studyfile_id "+
            " and dt.id= dv.datatable_id )";
    
    
    private static final String DELETE_VARIABLE_RANGES = " delete from variablerange where datavariable_id in ( "+
            "select dv.id from study s, filecategory fc, studyfile sf, datatable dt, datavariable dv"+
            " where s.id= ? "+
            " and s.id = fc.study_id "+
            " and fc.id= sf.filecategory_id "+
            " and sf.id=dt.studyfile_id "+
            " and dt.id= dv.datatable_id )";
    
    
    
    
    private static final String DELETE_DATA_VARIABLES = " delete from datavariable where id in ( "+
            "select dv.id from study s, filecategory fc, studyfile sf, datatable dt, datavariable dv"+
            " where s.id= ? "+
            " and s.id = fc.study_id "+
            " and fc.id= sf.filecategory_id "+
            " and sf.id=dt.studyfile_id "+
            " and dt.id= dv.datatable_id )";
    
    private static final String SELECT_DATAVARIABLE_IDS = "select dv.id from study s, filecategory fc, studyfile sf, datatable dt, datavariable dv"+
            " where s.id= ? "+
            " and s.id = fc.study_id "+
            " and fc.id= sf.filecategory_id "+
            " and sf.id=dt.studyfile_id "+
            " and dt.id= dv.datatable_id ";
    
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDataVariables(Long studyId) {
        
        em.createNativeQuery(DELETE_VARIABLE_CATEGORIES).setParameter(1,studyId).executeUpdate();
        em.createNativeQuery(DELETE_SUMMARY_STATISTICS).setParameter(1,studyId).executeUpdate();
        em.createNativeQuery(DELETE_VARIABLE_RANGE_ITEMS).setParameter(1,studyId).executeUpdate();
        em.createNativeQuery(DELETE_VARIABLE_RANGES).setParameter(1,studyId).executeUpdate();
        
        em.createNativeQuery(DELETE_DATA_VARIABLES).setParameter(1,studyId).executeUpdate();
        
    }
    /**
     *  Get the Study and all it's dependent objects.
     *  Used to view/update all the Study details
     *  Access each dependent object to trigger it's retreival from the database.
     *
     */
    public Study getStudyDetail(Long studyId) {
        Study study = em.find(Study.class,studyId);
        
        if (study==null) {
            throw new IllegalArgumentException("Unknown studyId: "+studyId);
        }
        
        for (Iterator<StudyAbstract> it = study.getStudyAbstracts().iterator(); it.hasNext();) {
            StudyAbstract elem = it.next();
            elem.getId();
        }
        for (Iterator<StudyAuthor> it = study.getStudyAuthors().iterator(); it.hasNext();) {
            StudyAuthor elem = it.next();
            elem.getId();
        }
        for (Iterator<StudyDistributor> it = study.getStudyDistributors().iterator(); it.hasNext();) {
            StudyDistributor elem = it.next();
            elem.getId();
        }
        
        for (Iterator<StudyKeyword> it = study.getStudyKeywords().iterator(); it.hasNext();) {
            StudyKeyword elem = it.next();
            elem.getId();
        }
        
        for (Iterator<StudyNote> it = study.getStudyNotes().iterator(); it.hasNext();) {
            StudyNote elem = it.next();
            elem.getId();
        }
        
        for (Iterator<StudyProducer> it = study.getStudyProducers().iterator(); it.hasNext();) {
            StudyProducer elem = it.next();
            elem.getId();
        }
        for (Iterator<StudySoftware> it = study.getStudySoftware().iterator(); it.hasNext();) {
            StudySoftware elem = it.next();
            elem.getId();
        }
        for (Iterator<StudyTopicClass> it = study.getStudyTopicClasses().iterator(); it.hasNext();) {
            StudyTopicClass elem = it.next();
            elem.getId();
        }
        
        for (Iterator<StudyOtherId> it = study.getStudyOtherIds().iterator(); it.hasNext();) {
            StudyOtherId elem = it.next();
            elem.getId();
        }
        
        for (Iterator<TemplateField> it = study.getTemplate().getTemplateFields().iterator(); it.hasNext();) {
            TemplateField elem = it.next();
            elem.getId();
        }
        
        return study;
    }
    
    /**
     *   Gets Study, and dependent objects that contain most-needed data.
     *  (At this point, only StudyAuthor)
     *  This can be used for displaying Study search results, for example.
     *
     */
    public Study getStudy(Long studyId) {
        
        Study study = em.find(Study.class,studyId);
        if (study==null) {
            throw new IllegalArgumentException("Unknown studyId: "+studyId);
        }
        for (Iterator<StudyAuthor> it = study.getStudyAuthors().iterator(); it.hasNext();) {
            StudyAuthor elem = it.next();
            elem.getId();
        }
        
        return study;
    }
    
    public List getStudies() {
        String query = "SELECT s FROM Study s ORDER BY s.id";
        return (List)em.createQuery(query).getResultList();
    }
    

    public List getRecentStudies(Long vdcId, int numResults) {
        String query = "SELECT s FROM Study s WHERE s.owner.id = " + vdcId + " ORDER BY s.createTime desc";
        if (numResults == -1) {
            return (List)em.createQuery(query).getResultList();
        } else {
            return (List)em.createQuery(query).setMaxResults(numResults).getResultList();
        }
    }
    
    public List getStudies(List studyIdList, String orderBy) {
        String studyIds = "";
        Iterator iter = studyIdList.iterator();
        while (iter.hasNext()) {
            Long id = (Long) iter.next();
            studyIds += id + (iter.hasNext() ? "," : "" );
        }
        
        String query = "SELECT s.id FROM Study s WHERE s.id in (" + studyIds + ") ORDER BY s." + orderBy;
        
        //System.out.println("getStudies - Query: " + query);
        return (List) em.createQuery(query).getResultList();
    }
    
    public List <Study> getReviewerStudies(Long vdcId){
        String query = "SELECT s FROM Study s WHERE s.reviewState.name = 'In Review' AND s.owner.id = " + vdcId;
        List <Study> studies = em.createQuery(query).getResultList();
        for (Iterator it = studies.iterator(); it.hasNext();) {
            Study elem = (Study) it.next();
            elem.getReviewState();
        }
        
        return studies;
    }
    
    public List <Study> getNewStudies(Long vdcId){
        String query = "SELECT s FROM Study s WHERE s.reviewState.name = 'New' AND s.owner.id = " + vdcId;
        List <Study> studies = em.createQuery(query).getResultList();
        for (Iterator it = studies.iterator(); it.hasNext();) {
            Study elem = (Study) it.next();
            elem.getReviewState();
        }
        
        return studies;
    }
    
    public List <Study> getContributorStudies(VDCUser contributor){
        String query = "SELECT s FROM Study s WHERE s.creator.id = " + contributor.getId().toString();
        List <Study> studies = em.createQuery(query).getResultList();
        for (Iterator it = studies.iterator(); it.hasNext();) {
            Study elem = (Study) it.next();
            elem.getReviewState();
        }
        return studies;
    }
    
    public StudyFile getStudyFile(Long fileId) {
        StudyFile file = em.find(StudyFile.class,fileId);
        if (file==null) {
            throw new IllegalArgumentException("Unknown studyFileId: "+fileId);
        }
        
        
        return file;
    }
    
    public FileCategory getFileCategory(Long fileCategoryId) {
        FileCategory fileCategory = em.find(FileCategory.class,fileCategoryId);
        if (fileCategory==null) {
            throw new IllegalArgumentException("Unknown fileCategoryId: "+fileCategoryId);
        }
        
        
        return fileCategory;
    }
    
    public List<DataFileFormatType> getDataFileFormatTypes() {
        return em.createQuery("select object(t) from DataFileFormatType as t").getResultList();
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addIngestedFiles(Long studyId, List fileBeans, Long userId) {
        // first some initialization
        Study study = getStudy(studyId);
        VDCUser user = userService.find(userId);
        
        File newDir = new File(FileUtil.getStudyFileDir(), study.getAuthority() + File.separator + study.getStudyId());
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
        
        // now iterate through fileBeans
        Iterator iter = fileBeans.iterator();
        while (iter.hasNext()) {
            StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();
            
            StudyFile f = fileBean.getStudyFile();
            // attach file to study
            fileBean.addFileToCategory(study);
            
            // move ingest-created file
            File tempIngestedFile = new File(fileBean.getIngestedSystemFileLocation());
            File newIngestedLocationFile = new File(newDir, f.getFileSystemName());
            try {
                FileUtil.copyFile( tempIngestedFile, newIngestedLocationFile );
                tempIngestedFile.delete();
                f.setFileType("text/tab-separated-values");
                f.setFileSystemLocation(newIngestedLocationFile.getAbsolutePath());
                
            } catch (IOException ex) {
                throw new EJBException(ex);
            }
            
            
            // also move original file for archiving
            File tempOriginalFile = new File(fileBean.getTempSystemFileLocation());
            File newOriginalLocationFile = new File(newDir, "_" + f.getFileSystemName() );
            try {
                FileUtil.copyFile( tempOriginalFile, newOriginalLocationFile );
                tempOriginalFile.delete();
            } catch (IOException ex) {
                throw new EJBException(ex);
            }
        }
        
        // calcualte study UNF
        try {
            study.setUNF(new DSBWrapper().calculateUNF(study) );
        } catch (IOException e) {
            throw new EJBException("Could not calculate new study UNF");
        }
        
        study.setLastUpdateTime( new Date() );
        study.setLastUpdater( user );
        
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public java.util.List<FileCategory> getOrderedFileCategories(Long studyId) {
        // Note: This ordering is case-sensitive, so names beginning with upperclass chars will appear first.
        // (I tried using UPPER(f.name) to make the sorting case-insensitive, but the EJB query language doesn't seem
        // to like this.)
        String query = "SELECT f FROM FileCategory f WHERE f.study.id = " + studyId +" ORDER BY f.name";
        List <FileCategory> categories = em.createQuery(query).getResultList();
        
        return categories;
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public java.util.List<StudyFile> getOrderedFilesByCategory(Long fileCategoryId) {
        // Note: This ordering is case-sensitive, so names beginning with upperclass chars will appear first.
        // (I tried using UPPER(f.name) to make the sorting case-insensitive, but the EJB query language doesn't seem
        // to like this.)
        String queryStr = "SELECT f FROM StudyFile f WHERE f.fileCategory.id = " + fileCategoryId +" ORDER BY f.fileName";
        Query query =em.createQuery(queryStr);
        List <StudyFile> studyFiles = query.getResultList();
        
        return studyFiles;
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public java.util.List<StudyFile> getOrderedFilesByStudy(Long studyId) {
        // Note: This ordering is case-sensitive, so names beginning with upperclass chars will appear first.
        // (I tried using UPPER(f.name) to make the sorting case-insensitive, but the EJB query language doesn't seem
        // to like this.)
        String queryStr = "SELECT f FROM StudyFile f LEFT JOIN FETCH f.dataTable JOIN FETCH f.fileCategory WHERE f.fileCategory.study.id = " + studyId +" ORDER BY f.fileCategory.name, f.fileName";
        Query query =em.createQuery(queryStr);
        List <StudyFile> studyFiles = query.getResultList();
        
        return studyFiles;
    }    
    
    public String generateStudyIdSequence(String protocol, String authority) {
        //   Date now = new Date();
        //   return ""+now.getTime();
        //   return em.createNamedQuery("getStudyIdSequence").getSingleResult().toString();
        String studyId=null;
        studyId = gnrsService.getNewObjectId(protocol,authority);
        /*
        do {
            Vector result = (Vector)em.createNativeQuery("select nextval('studyid_seq')").getSingleResult();
            studyId = result.get(0).toString();
        } while (!isUniqueStudyId(studyId, protocol, authority));
         */
        return studyId;
        
    }
    
    /**
     *  Check that a studyId entered by the user is unique (not currently used for any other study in this Dataverse Network)
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean isUniqueStudyId(String userStudyId, String protocol,String authority) {
        String query = "SELECT s FROM Study s WHERE s.studyId = '" + userStudyId +"'";
        query += " and s.protocol ='"+protocol+"'";
        query += " and s.authority = '"+authority+"'";
        return em.createQuery(query).getResultList().size()==0;
    }
    
    
    public void exportStudyFiles( String lastUpdateTime, String authority)  {
        // Get list of studies that have been updated yesterday,
        // and export them to legacy VDC system
        
        Logger logger = null;
        
        String exportLogDirStr = System.getProperty("vdc.export.log.dir");
        if (exportLogDirStr==null) {
            System.out.println("Missing system property: vdc.export.log.dir.  Please add to JVM options");
            return;
        }
        File exportLogDir = new File(exportLogDirStr);
        if (!exportLogDir.exists()) {
            exportLogDir.mkdir();
        }
        
        logger = Logger.getLogger("edu.harvard.hmdc.vdcnet.web.servlet.VDCExportServlet");
        
        // Everytime export runs, we want to write to a separate log file (handler).
        // So if export has run previously, remove the previous handler
        if (logger.getHandlers()!=null && logger.getHandlers().length>0) {
            int numHandlers= logger.getHandlers().length;
            for (int i=0; i<numHandlers;i++) {
                logger.removeHandler(logger.getHandlers()[i]);
            }
        }
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
        FileHandler handler=null;
        try {
            handler = new FileHandler(exportLogDirStr+ File.separator+ "export_"+formatter.format(new Date())+".log");
        } catch(IOException e) {
            throw new EJBException(e);
        }
        
        // Add handler to the desired logger
        logger.addHandler(handler);
        
        
        logger.info("Begin Exporting Studies");
        int studyCount=0;
        int deletedStudyCount=0;
        try {
            
            // For all studies that have been deleted in the dataverse since last export, remove study directory in VDC
            
            String query = "SELECT s from DeletedStudy s where s.authority = '"+authority+"' " ;
            List deletedStudies = em.createQuery(query).getResultList();
            for (Iterator it = deletedStudies.iterator(); it.hasNext();) {
                DeletedStudy deletedStudy = (DeletedStudy) it.next();
                
                logger.info("Deleting study "+deletedStudy.getStudyId());
                
                File legacyStudyDir = new File(FileUtil.getLegacyFileDir()+File.separatorChar + authority + File.separatorChar + deletedStudy.getStudyId());
                
                // Remove files in the directory, then delete the directory.
                File[] studyFiles = legacyStudyDir.listFiles();
                if (studyFiles!=null ){
                    for (int i=0;i<studyFiles.length;i++) {
                        studyFiles[i].delete();
                    }
                }
                legacyStudyDir.delete();
                deletedStudyCount++;
                
                em.remove(deletedStudy);
            }
            
            // Do export of all studies updated at "lastUpdateTime""
            
            if (authority== null) {
                authority = vdcNetworkService.find().getAuthority();
            }
            String beginTime=null;
            String endTime=null;
            if (lastUpdateTime==null) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, -1);
                beginTime =new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());  // Use yesterday as default value
                cal.add(Calendar.DAY_OF_YEAR,1);
                endTime =new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            }    else {
                beginTime=lastUpdateTime;
                Date date=new SimpleDateFormat("yyyy-MM-dd").parse(lastUpdateTime);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DAY_OF_YEAR,1);
                endTime =new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            }
            query = "SELECT s from Study s where s.authority = '"+authority+"' " ;
            query+=" and s.lastUpdateTime >'" +beginTime+"'";
            //    query+=" and s.lastUpdateTime <'" +endTime+"'";
            query+=" order by s.studyId";
            List updatedStudies = em.createQuery(query).getResultList();
            
            
            for (Iterator it = updatedStudies.iterator(); it.hasNext();) {
                Study study = (Study) it.next();
                logger.info("Exporting study "+study.getStudyId());
                
                exportStudy(study,authority);
                studyCount++;
                
            }
        } catch(Exception e) {
            logger.severe(e.getMessage());
            
            String stackTrace = "StackTrace: \n";
            logger.severe("Exception caused by: "+e+"\n");
            StackTraceElement[] ste = e.getStackTrace();
            for(int m=0;m<ste.length;m++) {
                stackTrace+=ste[m].toString()+"\n";
            }
            logger.severe(stackTrace);
        }
        
        logger.info("End export, "+studyCount +" studies successfully exported, "+deletedStudyCount+" studies deleted.");
    }
    
    private void exportStudy(Study study,String authority) throws IOException, JAXBException {
        // For each study
        // update study file locations for legacy system
        // Write ddi to an output stream
        // If data file dir exists, delete everything from it
        // copy ddi to study.xml,
        // copy study files.
        File studyDir = new File(FileUtil.getStudyFileDir() + File.separatorChar+authority +File.separatorChar+ study.getStudyId());
        File legacyStudyDir = new File(FileUtil.getLegacyFileDir()+File.separatorChar + authority + File.separatorChar + study.getStudyId());
        
        // If the directory exists in the legacy system, then clear out all the files contained in it
        if (legacyStudyDir.exists() && legacyStudyDir.isDirectory()) {
            File[] files = legacyStudyDir.listFiles();
            for (int i=0; i<files.length; i++) {
                files[i].delete();
            }
        } else {
            legacyStudyDir.mkdirs();
        }
        
        // Export the study to study.xml in the legacy directory
        FileWriter fileWriter = new FileWriter(new File(legacyStudyDir,"study.xml"));
        try {
            ddiService.exportStudy(study, fileWriter, true);
            fileWriter.flush();
        } finally{
            fileWriter.close();
        }
        
        // Copy all the study files to the legacy directory
        
        for (Iterator it = study.getStudyFiles().iterator(); it.hasNext();) {
            StudyFile studyFile = (StudyFile) it.next();
            FileUtil.copyFile(new File(studyDir,studyFile.getFileSystemName()), new File(legacyStudyDir,studyFile.getFileSystemName()));
        }
        
        
    }
    
    public String generateFileSystemNameSequence() {
        String fileSystemName = null;
        do {
            Vector result = (Vector)em.createNativeQuery("select nextval('filesystemname_seq')").getSingleResult();
            fileSystemName = result.get(0).toString();
        } while (!isUniqueFileSystemName(fileSystemName));
        
        return fileSystemName;
        
    }
    
    /**
     *  Check that a fileId  unique (not currently used for any other file in this study)
     */
    public boolean isUniqueFileSystemName(String fileSystemName) {
        String query = "SELECT f FROM StudyFile f WHERE f.fileSystemName = '" + fileSystemName +"'";
        return em.createQuery(query).getResultList().size()==0;
    }
    
    public void addStudyLock(Long studyId, Long userId, String detail) {
        Study study = em.find(Study.class,studyId);
        VDCUser user = em.find(VDCUser.class,userId);
        
        StudyLock lock = new StudyLock();
        lock.setStudy(study);
        lock.setUser(user);
        lock.setDetail(detail);
        lock.setStartTime( new Date() );
        
        study.setStudyLock(lock);
        if (user.getStudyLocks() == null) {
            user.setStudyLocks( new ArrayList() );
        }
        user.getStudyLocks().add(lock);
        
        em.persist(lock);
    }
    
    public void removeStudyLock(Long studyId) {
        Study study = em.find(Study.class,studyId);
        StudyLock lock = study.getStudyLock();
        VDCUser user = lock.getUser();
        
        study.setStudyLock(null);
        user.getStudyLocks().remove(lock);
        
        em.remove(lock);
    }
    
    public void incrementNumberOfDownloads(Long studyId) {
        Study s = getStudy(studyId);
        StudyDownload sd = s.getStudyDownload();
        
        if (sd == null) {
            sd = new StudyDownload();
            s.setStudyDownload( sd );
            sd.setStudy(s);
        }
        
        sd.setNumberOfDownloads( sd.getNumberOfDownloads() + 1 );
    }
    
    public Study getStudyByGlobalId(String identifier) {
        
        String protocol=null;
        String authority=null;
        String studyId=null;
        int index1 = identifier.indexOf(':');
        int index2 = identifier.indexOf('/');
        if (index1==-1) {
            throw new EJBException("Error parsing identifier: "+identifier+". ':' not found in string");
        } else {
            protocol=identifier.substring(0,index1);
        }
        if (index2 == -1) {
            throw new EJBException("Error parsing identifier: "+identifier+". '/' not found in string");
            
        } else {
            authority=identifier.substring(index1+1, index2);
        }
        studyId = identifier.substring(index2+1);
        
        String queryStr="SELECT s from Study s where s.studyId = :studyId  and s.protocol= :protocol and s.authority= :authority";
        
        Study study=null;
        try {
            Query query = em.createQuery(queryStr);
            query.setParameter("studyId",studyId);
            query.setParameter("protocol",protocol);
            query.setParameter("authority",authority);
            study=(Study)query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            // DO nothing, just return null.
        }
        return study;
    }
    
    

  
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Study importHarvestStudy(File xmlFile, Long vdcId, Long userId, String harvestIdentifier, boolean allowUpdates) {
        VDC vdc = em.find(VDC.class, vdcId);
        em.refresh(vdc); // workaround to get correct value for harvesting dataverse (to be investigated)
        
        if (vdc.getHarvestingDataverse() == null) {
            throw new EJBException("importHarvestStudy(...) should only be called for a harvesting dataverse.");
        }
        
        boolean createNewHandle = (vdc.getHarvestingDataverse().getHandlePrefix() != null);
        int format = vdc.getHarvestingDataverse().getFormat().equals("ddi") ? 0 : 1; // 1 is mif; eventually this will be dynamic

        //return doImportStudy(xmlFile, format, vdcId, userId, createNewHandle, createNewHandle, true, false, false, harvestIdentifier);
        return doImportStudy(xmlFile, format, vdcId, userId, createNewHandle, createNewHandle, allowUpdates, false, false, harvestIdentifier);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Study importLegacyStudy(File xmlFile, Long vdcId, Long userId) {
        VDC vdc = em.find(VDC.class, vdcId);
        em.refresh(vdc); // workaround to get correct value for harvesting dataverse (to be investigated)
        
        if (vdc.getHarvestingDataverse() != null) {
            throw new EJBException("importLegacyStudy(...) should never be called for a harvesting dataverse.");
        }
        
        return doImportStudy(xmlFile, 0,vdcId, userId, true, false, false, true, true, null);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Study importStudy(File xmlFile, int xmlFileFormat, Long vdcId, Long userId, boolean registerHandle, boolean generateHandle, boolean allowUpdates, boolean checkRestrictions, boolean retrieveFiles, String harvestIdentifier) {
        return doImportStudy(xmlFile, xmlFileFormat, vdcId, userId, registerHandle, generateHandle, allowUpdates, checkRestrictions, retrieveFiles, harvestIdentifier);
    }
    
    
    private Study doImportStudy(File xmlFile, int xmlFileFormat, Long vdcId, Long userId, boolean registerHandle, boolean generateHandle, boolean allowUpdates, boolean checkRestrictions, boolean retrieveFiles, String harvestIdentifier) {
        logger.info("Begin doImportStudy");
        VDCNetwork vdcNetwork = vdcNetworkService.find();
        VDC vdc = em.find(VDC.class, vdcId);
        
        File ddiFile = xmlFile;
        boolean fileTransformed = false;
        
        if (xmlFileFormat != 0) {
            ddiFile = transformToDDI(xmlFile, "mif2ddi.xsl");
            fileTransformed = true;
        }
        
        CodeBook _cb = generateCodeBook(ddiFile);
        String id = null;
        String globalId = null;
        
        if (generateHandle) {
            globalId = ddiService.determineId(_cb, DDI20ServiceBean.AGENCY_HANDLE);
            if (globalId != null) {
                throw new EJBException("DDI should not specify a handle, but does.");
            }
            
            id = ddiService.determineId(_cb, null);
            if (id != null) {
                // first replace any slashes and/or spaces
                id = id.replace('/', '-').replace(' ', '_');
                if (vdc.getHarvestingDataverse() != null) {
                    if (vdc.getHarvestingDataverse().getHandlePrefix() != null) {
                        globalId = "hdl:" + vdc.getHarvestingDataverse().getHandlePrefix().getPrefix() + "/" + id;
                    } else {
                        throw new EJBException("generateHandle cannot be true for a nonregistering harvesting dataverse.");
                    }
                    
                } else {
                    globalId = vdcNetwork.getProtocol() + ":" + vdcNetwork.getAuthority()  + "/" + id;
                }
            } else {
                throw new EJBException("No Other ID was found in DDI for generating a handle.");
            }
            
        } else {
            globalId = ddiService.determineId(_cb, DDI20ServiceBean.AGENCY_HANDLE);
            if (globalId == null) {
                throw new EJBException("DDI should specify a handle, but does not.");
            }
        }
        
        
        Study study = getStudyByGlobalId(globalId);
        
        if (study == null) {
            VDCUser creator = em.find(VDCUser.class,userId);
            ReviewState reviewState = reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_RELEASED);
            study = new Study(vdc, creator, reviewState);
            em.persist(study);
        } else if (allowUpdates) {
            registerHandle = false; // this is an update, study should already be registered
            clearStudy(study);
        } else {
            throw new EJBException("Study already exists and this import was called with allowUpdates = false.");
        }
        
        
        // now we map
        logger.info("calling mapDDI()");
        ddiService.mapDDI( _cb, study );
        logger.info("completed mapDDI, studyId = "+study.getStudyId());
        _cb=null;
        System.gc();
        
        //post mapping processing
        if ( harvestIdentifier != null ) {
            study.setIsHarvested(true);
            study.setHarvestIdentifier(harvestIdentifier);
        } else {
            study.setHarvestHoldings(null); // clear the holdings field
        }
        
        if (generateHandle) {
            if (vdc.getHarvestingDataverse() != null) {
                study.setProtocol("hdl");
                study.setAuthority(vdc.getHarvestingDataverse().getHandlePrefix().getPrefix());
                study.setStudyId( id );
            } else {
                study.setProtocol(vdcNetwork.getProtocol());
                study.setAuthority(vdcNetwork.getAuthority());
                study.setStudyId( id );
            }
        } else if (!study.getGlobalId().equals(globalId)) {
            // this should never happen
            throw new EJBException("Mismatch between predetermined gloablId and study globalId.");
        }
        
        
        // If study comes from old VDC, set restrictions based on  restrictions in VDC repository
        if (checkRestrictions) {
            setImportedStudyRestrictions(study);
        }
        
        if (retrieveFiles) {
            retrieveFiles(study);
        }
        
        copyXMLFile(study, ddiFile, "original_imported_study.xml");
        
        if (fileTransformed) {
            copyXMLFile(study, xmlFile, "original_imported_study_pretransform.xml");
        }
        
        saveStudy(study, userId);
        
        if ( registerHandle && vdcNetworkService.find().isHandleRegistration() ) {
            String handle = study.getAuthority() + "/" + study.getStudyId();
            gnrsService.createHandle(handle);
        }
        
        logger.info("completed doImportStudy() returning study"+study.getGlobalId());
        return study;
    }
    
    private File transformToDDI(File xmlFile, String xslFileName) {
        File ddiFile = null;
        InputStream in = null;
        OutputStream out = null;
        
        try {
            // prepare source
            in = new FileInputStream(xmlFile);
            StreamSource source = new StreamSource(in);
            
            // prepare result
            ddiFile = File.createTempFile("ddi", ".xml");
            out = new FileOutputStream(ddiFile);
            StreamResult result = new StreamResult(out);
            
            // now transform
            StreamSource xslSource = new StreamSource(new File(xslFileName));
            Transformer transformer = TransformerFactory.newInstance().newTransformer(xslSource);
            transformer.transform( source, result );
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new EJBException("Error occurred while attempting to transform file.");
        } finally {
            try {
                if (in != null) { in.close(); }
            } catch (Exception e) {}
            try {
                if (out != null) { out.close(); }
            } catch (Exception e) {}
        }
        
        
        return ddiFile;
        
    }
    
    
    private CodeBook generateCodeBook(Object obj ) {
        CodeBook _cb = null;
        
        if (obj instanceof CodeBook) {
            _cb = (CodeBook)obj;
        } else if (obj instanceof Node || obj instanceof File ) {
            // first unmarshall the XML
            
            try {
                if (obj instanceof Node) {
                    _cb  = (CodeBook) DDIUnmarshaller.unmarshal( (Node)obj );
                } else {
                    logger.info("begin unmarshal of file "+((File)obj).getName());
                    Object unmarshalObj= DDIUnmarshaller.unmarshal( (File)obj );
                    _cb  = (CodeBook)unmarshalObj;
                    
                }
                logger.info("Completed unmarshal");
            } catch(JAXBException ex) {
                EJBException e = new EJBException("Import Study failed: "+ex.getMessage() );
                e.initCause(ex);
                throw e;
            }
        } else {
            throw new EJBException("Invalid type for parameter obj: "+obj.getClass().getName()+". obj must instance CodeBook, Node, or File.");
        }
        
        return _cb;
    }
    
    private void setImportedStudyRestrictions(Study study) {
        RepositoryWrapper repositoryWrapper = new RepositoryWrapper();
        try {
            String studyObjectHandle = study.getProtocol()+":"+study.getAuthority()+"/"+study.getStudyId();
            if (repositoryWrapper.isObjectRestricted(studyObjectHandle)) {
                study.setRestricted(true);
            }
            for (Iterator it = study.getFileCategories().iterator(); it.hasNext();) {
                FileCategory cat = (FileCategory) it.next();
                for (Iterator it2 = cat.getStudyFiles().iterator(); it2.hasNext();) {
                    StudyFile studyFile = (StudyFile) it2.next();
                    String fileSystemName = determineLegacyFile(studyFile).getName();
                    if (repositoryWrapper.isObjectRestricted(studyObjectHandle+"/"+fileSystemName)) {
                        studyFile.setRestricted(true);
                    }
                }
                
            }
        } catch (IOException e) {
            EJBException ex = new EJBException("Exception setting restrictions for study "+study.getGlobalId());
            ex.initCause(e);
            throw ex;
            
            
        } catch (SAXException e) {
            EJBException ex = new EJBException("Exception setting restrictions for study "+study.getGlobalId());
            ex.initCause(e);
            throw ex;
            
        }
        
        
    }
    
    private File determineLegacyFile(StudyFile file) {
        
        String legacyFileDir = FileUtil.getLegacyFileDir();
        
        int startIndex = file.getFileSystemLocation().indexOf("/hdl:") + 5;
        String parsedFileLocation = file.getFileSystemLocation().substring( startIndex );
        
        // this line is just for when testing on a windows machine!
        parsedFileLocation = parsedFileLocation.replace('/', File.separatorChar);
        
        File legacyFile = new File(legacyFileDir, parsedFileLocation);
        
        return legacyFile;
    }
    
    private void copyXMLFile( Study study, File xmlFile, String xmlFileName ) {
        try {
            // create, if needed, the directory
            File newDir = new File(FileUtil.getStudyFileDir(), study.getAuthority() + File.separator + study.getStudyId());
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            
            FileUtil.copyFile( xmlFile, new File(newDir, xmlFileName ));
        } catch (IOException ex) {
            ex.printStackTrace();
            String msg = "ImportStudy failed: ";
            if (ex.getMessage()!=null) {
                msg+=ex.getMessage();
            }
            EJBException e = new EJBException(msg );
            e.initCause(ex);
            throw e;
        }
    }
    
    private void retrieveFiles( Study study ) {
        try {
            
            // create, if needed, the directory
            File newDir = new File(FileUtil.getStudyFileDir(), study.getAuthority() + File.separator + study.getStudyId());
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            
            Iterator iter = study.getStudyFiles().iterator();
            while (iter.hasNext()) {
                StudyFile file = (StudyFile) iter.next();
                File inputFile = determineLegacyFile( file );
                file.setFileSystemName( inputFile.getName() );
                File outputFile = new File(newDir, file.getFileSystemName() );
                FileUtil.copyFile( inputFile, outputFile );
                file.setFileSystemLocation( outputFile.getAbsolutePath() );
                if (file.isSubsettable()) {
                    file.setFileType( "text/tab-separated-values" );
                } else {
                    // we need to incorprate something like JHOVE to determine otherMat filetypes
                }
            }
            
            
        } catch (IOException ex) {
            ex.printStackTrace();
            EJBException e= new EJBException("RetrieveFiles failed: " + ex.getMessage() );
            e.initCause(ex);
            throw e;
        }
    }
    
    private void clearStudy(Study study) {
        // should this be done with bulk deletes??
        
        for (Iterator iter = study.getFileCategories().iterator(); iter.hasNext();) {
            FileCategory elem = (FileCategory) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyAbstracts().iterator(); iter.hasNext();) {
            StudyAbstract elem = (StudyAbstract) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyAuthors().iterator(); iter.hasNext();) {
            StudyAuthor elem = (StudyAuthor) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyDistributors().iterator(); iter.hasNext();) {
            StudyDistributor elem = (StudyDistributor) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyGeoBoundings().iterator(); iter.hasNext();) {
            StudyGeoBounding elem = (StudyGeoBounding) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyGrants().iterator(); iter.hasNext();) {
            StudyGrant elem = (StudyGrant) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyKeywords().iterator(); iter.hasNext();) {
            StudyKeyword elem = (StudyKeyword) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyNotes().iterator(); iter.hasNext();) {
            StudyNote elem = (StudyNote) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyOtherIds().iterator(); iter.hasNext();) {
            StudyOtherId elem = (StudyOtherId) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyOtherRefs().iterator(); iter.hasNext();) {
            StudyOtherRef elem = (StudyOtherRef) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyProducers().iterator(); iter.hasNext();) {
            StudyProducer elem = (StudyProducer) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyRelMaterials().iterator(); iter.hasNext();) {
            StudyRelMaterial elem = (StudyRelMaterial) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyRelPublications().iterator(); iter.hasNext();) {
            StudyRelPublication elem = (StudyRelPublication) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyRelStudies().iterator(); iter.hasNext();) {
            StudyRelStudy elem = (StudyRelStudy) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudySoftware().iterator(); iter.hasNext();) {
            StudySoftware elem = (StudySoftware) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyTopicClasses().iterator(); iter.hasNext();) {
            StudyTopicClass elem = (StudyTopicClass) iter.next();
            removeCollectionElement(iter,elem);
        }
    }
    
    private void removeCollectionElement(Iterator iter, Object elem) {
        iter.remove();
        em.remove(elem);
    }
    
    private void setDisplayOrders(Study study) {
        int i=0;
        for (Iterator it = study.getStudyAuthors().iterator(); it.hasNext();) {
            StudyAuthor elem = (StudyAuthor) it.next();
            elem.setDisplayOrder(i);
            i++;
            
        }
        i=0;
        for (Iterator it = study.getStudyAbstracts().iterator(); it.hasNext();) {
            StudyAbstract elem = (StudyAbstract) it.next();
            elem.setDisplayOrder(i);
            i++;
            
        }
        i=0;
        for (Iterator it = study.getStudyDistributors().iterator(); it.hasNext();) {
            StudyDistributor elem = (StudyDistributor) it.next();
            elem.setDisplayOrder(i);
            i++;
            
        }
        i=0;
        for (Iterator it = study.getStudyGeoBoundings().iterator(); it.hasNext();) {
            StudyGeoBounding elem = (StudyGeoBounding) it.next();
            elem.setDisplayOrder(i);
            i++;
            
        }
        i=0;
        for (Iterator it = study.getStudyGrants().iterator(); it.hasNext();) {
            StudyGrant elem = (StudyGrant) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        
        i=0;
        for (Iterator it = study.getStudyKeywords().iterator(); it.hasNext();) {
            StudyKeyword elem = (StudyKeyword) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        
        i=0;
        for (Iterator it = study.getStudyNotes().iterator(); it.hasNext();) {
            StudyNote elem = (StudyNote) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        i=0;
        for (Iterator it = study.getStudyOtherIds().iterator(); it.hasNext();) {
            StudyOtherId elem = (StudyOtherId) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        i=0;
        for (Iterator it = study.getStudyProducers().iterator(); it.hasNext();) {
            StudyProducer elem = (StudyProducer) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        i=0;
        for (Iterator it = study.getStudySoftware().iterator(); it.hasNext();) {
            StudySoftware elem = (StudySoftware) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        
        i=0;
        for (Iterator it = study.getStudyTopicClasses().iterator(); it.hasNext();) {
            StudyTopicClass elem = (StudyTopicClass) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        
    }
    
    public Study saveStudy(Study study, Long userId) {
        VDCUser user = em.find(VDCUser.class,userId);
        
        study.setLastUpdateTime(new Date());
        study.setLastUpdater(user);
        
        setDisplayOrders(study);
        return study;
    }

    // visible studies are defined as those that are released and not in a restricted vdc
    // (unless you are in vdc)
    public List getVisibleStudies(List studyIds, Long vdcId) {
        if ( studyIds != null && studyIds.size() > 0) {
            String query = "SELECT s.id FROM Study s WHERE s.reviewState.name = 'Released' " +
                    "AND (s.owner.restricted = false " + 
                    (vdcId != null ? "OR s.owner.id = " + vdcId : "") +
                    ") AND s.id in (" + generateIdString(studyIds) + ")";

            return (List)em.createQuery(query).getResultList();    
        } else {
            return new ArrayList();
        }
    } 
   

    private static final String SELECT_VIEWABLE_STUDIES = "select id from study s " +
        "left join study_vdcuser su on (s.id = su.studies_id) " +
        "left join study_usergroup sg on (s.id = sg.studies_id )" +
        "where ( " +
        "restricted = false " +
        "or su.allowedusers_id = ? " +
        "or sg.allowedgroups_id in (select usergroups_id from vdcuser_usergroup where users_id = ?) " + 
        "or sg.allowedgroups_id = ? " +
        ") ";

    
    // viewable studies are those that are defined as not restricted to the user
    public List getViewableStudies(List<Long> studyIds, Long userId, Long ipUserGroupId) {
        List returnList = new ArrayList();

        if ( studyIds != null && studyIds.size() > 0) {
            // first we must add the dynamic part to the query
            StringBuffer queryString = new StringBuffer();
            if (studyIds.size() == 1) {
                queryString.append ( SELECT_VIEWABLE_STUDIES + "and id in ( ? ) " );    
            } else {
                for (int i=0; i< studyIds.size(); i++) {
                    if (i == 0 ) {
                            queryString.append ( SELECT_VIEWABLE_STUDIES + "and id in ( ?, " );
                    } else if ( i == (studyIds.size() - 1) ) {
                            queryString.append ( " ? ) " );
                    } else {
                            queryString.append ( " ?, " );
                    }
                }
            }

            Query query = em.createNativeQuery(queryString.toString());
            query.setParameter(1,userId);
            query.setParameter(2,userId);
            query.setParameter(3,ipUserGroupId);

            // now set dynamic paramters
            int parameterCount = 4;
            for (Long id : studyIds) {
                query.setParameter( parameterCount++, id);
            }
            
            // since query is native, must parse through Vector results
            for (Object currentResult : query.getResultList() ) {
                // convert results into Longs
                returnList.add ( new Long( ((Integer) ((Vector) currentResult).get(0) )).longValue() );
            };

        }

        return returnList;
    
    }     

    
    private String generateIdString(List idList) {
        String ids = "";
        Iterator iter = idList.iterator();
        while (iter.hasNext()) {
            Long id = (Long) iter.next();
            ids += id + (iter.hasNext() ? "," : "" );
        }
        
        return ids;
    }    
    
}
