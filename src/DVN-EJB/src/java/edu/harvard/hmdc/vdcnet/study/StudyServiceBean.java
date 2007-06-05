/*
 * StudyServiceBean.java
 *
 * Created on August 14, 2006, 11:47 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.ddi.DDI20ServiceLocal;
import edu.harvard.hmdc.vdcnet.dsb.DSBWrapper;
import edu.harvard.hmdc.vdcnet.gnrs.GNRSServiceLocal;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.util.FileUtil;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollection;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBException;

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
    
    public void deleteStudy(Long studyId) {
        Study study = em.find(Study.class,studyId);
        for (Iterator<VDCCollection>it = study.getStudyColls().iterator(); it.hasNext();) {
            VDCCollection elem =  it.next();
            elem.getStudies().remove(study);
            
        }
        
        study.getAllowedGroups().clear();
        study.getAllowedUsers().clear();
        
        
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
        if (fileCategoryId==null) {
            throw new IllegalArgumentException("Unknown fileCategoryId: "+fileCategoryId);
        }
        
        
        return fileCategory;
    }
    
    
    
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
    
    
    public java.util.List<FileCategory> getOrderedFileCategories(Long studyId) {
        // Note: This ordering is case-sensitive, so names beginning with upperclass chars will appear first.
        // (I tried using UPPER(f.name) to make the sorting case-insensitive, but the EJB query language doesn't seem
        // to like this.)
        String query = "SELECT f FROM FileCategory f WHERE f.study.id = " + studyId +" ORDER BY f.name";
        List <FileCategory> categories = em.createQuery(query).getResultList();
        
        return categories;
    }
    
    public java.util.List<StudyFile> getOrderedFilesByCategory(Long fileCategoryId) {
        // Note: This ordering is case-sensitive, so names beginning with upperclass chars will appear first.
        // (I tried using UPPER(f.name) to make the sorting case-insensitive, but the EJB query language doesn't seem
        // to like this.)
        String queryStr = "SELECT f FROM StudyFile f WHERE f.fileCategory.id = " + fileCategoryId +" ORDER BY f.fileName";
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
            query+=" and s.lastUpdateTime <'" +endTime+"'";
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
    
    public void addStudyLock(Study study, VDCUser user, String detail) {
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
    
    public void removeStudyLock(Study study) {
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
}
