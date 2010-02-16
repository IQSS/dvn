/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.util.FileUtil;
import edu.harvard.iq.dvn.ingest.dsb.DSBIngestMessage;
import edu.harvard.iq.dvn.ingest.dsb.DSBWrapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author gdurand
 */
@Stateless
public class StudyFileServiceBean implements StudyFileServiceLocal {

    @PersistenceContext(unitName = "VDCNet-ejbPU")
    EntityManager em;
    @Resource(mappedName = "jms/DSBIngest")
    Queue queue;
    @Resource(mappedName = "jms/DSBQueueConnectionFactory")
    QueueConnectionFactory factory;


    @EJB UserServiceLocal userService;
    @EJB MailServiceLocal mailService;
    @EJB StudyServiceLocal studyService;

    private static final Logger logger = Logger.getLogger("edu.harvard.iq.dvn.core.study.StudyFileServiceBean");


    public StudyFile getStudyFile(Long fileId) {
        StudyFile file = em.find(StudyFile.class, fileId);
        if (file == null) {
            throw new IllegalArgumentException("Unknown studyFileId: " + fileId);
        }


        return file;
    }

    //TODO: VERSION: this method should be removed
    public FileCategory getFileCategory(Long fileCategoryId) {
        FileCategory fileCategory = em.find(FileCategory.class, fileCategoryId);
        if (fileCategory == null) {
            throw new IllegalArgumentException("Unknown fileCategoryId: " + fileCategoryId);
        }


        return fileCategory;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<FileMetadata> getStudyFilesByExtension(String extension) {

        String queryStr = "SELECT fm from FileMetadata sf where lower(sf.fileName) LIKE :extension";

        Query query = em.createQuery(queryStr);
        query.setParameter("extension", "%." + extension.toLowerCase());
        return query.getResultList();

    }

    public void updateStudyFile(StudyFile detachedStudyFile) {
        em.merge(detachedStudyFile);
    }



    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public java.util.List<FileMetadata> getOrderedFilesByStudy(Long studyId) {
        // Note: This ordering is case-sensitive, so names beginning with upperclass chars will appear first.
        // (I tried using UPPER(f.name) to make the sorting case-insensitive, but the EJB query language doesn't seem
        // to like this.)
        String queryStr = "SELECT f FROM FileMetadata f  WHERE f.studyVersion.study.id = " + studyId + " ORDER BY f.category, f.label";
        Query query = em.createQuery(queryStr);
        List<FileMetadata> studyFiles = query.getResultList();
   //     for (StudyFile sf: studyFiles) {
   //         sf.getDataTables().size();
   //     }
        return studyFiles;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public java.util.List<FileMetadata> getOrderedFilesByStudyVersion (Long svId) {
        // Note: This ordering is case-sensitive, so names beginning with upperclass chars will appear first.
        // (I tried using UPPER(f.name) to make the sorting case-insensitive, but the EJB query language doesn't seem
        // to like this.)
        String queryStr = "SELECT f FROM FileMetadata f  WHERE f.studyVersion.id = " + svId + " ORDER BY f.category, f.label";
        Query query = em.createQuery(queryStr);
        List<FileMetadata> studyFiles = query.getResultList();
   //     for (StudyFile sf: studyFiles) {
   //         sf.getDataTables().size();
   //     }
        return studyFiles;
    }

    public Boolean doesStudyHaveSubsettableFiles(Long studyId) {
        // TODO: VERSION: change this to use a study version object
        List<String> subsettableList = new ArrayList();
        Query query = em.createNativeQuery("select fileclass from studyfile where study_id = " + studyId);
        for (Object currentResult : query.getResultList()) {
            subsettableList.add( ((String) ((Vector) currentResult).get(0)) );
        }

        if ( !subsettableList.isEmpty() ) {
            for (String fclass : subsettableList) {
                if ("TabularDataFile".equals(fclass) || "NetworkDataFile".equals(fclass))
                    return Boolean.TRUE;
            }

            return Boolean.FALSE;
        }

        return null;


    }


    public void addFiles(StudyVersion studyVersion, List<StudyFileEditBean> newFiles, VDCUser user) {
        addFiles(studyVersion, newFiles, user, user.getEmail(), DSBIngestMessage.INGEST_MESAGE_LEVEL_ERROR);
    }

    public void addFiles(StudyVersion studyVersion, List<StudyFileEditBean> newFiles, VDCUser user, String ingestEmail) {
        addFiles(studyVersion, newFiles, user, ingestEmail, DSBIngestMessage.INGEST_MESAGE_LEVEL_INFO);
    }

    private void addFiles(StudyVersion studyVersion, List<StudyFileEditBean> newFiles, VDCUser user, String ingestEmail, int messageLevel) {

        Study study = studyVersion.getStudy();

        // step 1: divide the files, based on subsettable or not
        List subsettableFiles = new ArrayList();
        List otherFiles = new ArrayList();

        Iterator iter = newFiles.iterator();
        while (iter.hasNext()) {
            StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();
            if (fileBean.getStudyFile().isSubsettable()) {
                subsettableFiles.add(fileBean);
            } else {
                otherFiles.add(fileBean);
                // also add to study, so that it will be flushed for the ids
                fileBean.getStudyFile().setStudy(study);
                study.getStudyFiles().add(fileBean.getStudyFile());

            }
        }

        // step 2: iterate through nonsubsettable files, moving from temp to new location
        File newDir = FileUtil.getStudyFileDir(study);
        iter = otherFiles.iterator();
        while (iter.hasNext()) {
            StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();
            StudyFile f = fileBean.getStudyFile();
            File tempFile = new File(fileBean.getTempSystemFileLocation());
            File newLocationFile = new File(newDir, f.getFileSystemName());
            try {
                FileUtil.copyFile(tempFile, newLocationFile);
                tempFile.delete();
                f.setFileSystemLocation(newLocationFile.getAbsolutePath());

                fileBean.getFileMetadata().setStudyVersion( studyVersion );
                em.persist(fileBean.getFileMetadata());

            } catch (IOException ex) {
                throw new EJBException(ex);
            }
        }

        // step 3: iterate through subsettable files, sending a message via JMS
        if (subsettableFiles.size() > 0) {
            QueueConnection conn = null;
            QueueSession session = null;
            QueueSender sender = null;
            try {
                conn = factory.createQueueConnection();
                session = conn.createQueueSession(false, 0);
                sender = session.createSender(queue);

                DSBIngestMessage ingestMessage = new DSBIngestMessage(messageLevel);
                ingestMessage.setFileBeans(subsettableFiles);
                ingestMessage.setIngestEmail(ingestEmail);
                ingestMessage.setIngestUserId(user.getId());
                ingestMessage.setStudyId(study.getId());
                Message message = session.createObjectMessage(ingestMessage);

                String detail = "Ingest processing for " + subsettableFiles.size() + " file(s).";
                studyService.addStudyLock(study.getId(), user.getId(), detail);
                try {
                    sender.send(message);
                } catch (Exception ex) {
                    // If anything goes wrong, remove the study lock.
                    studyService.removeStudyLock(study.getId());
                    ex.printStackTrace();
                }

                // send an e-mail
                if (ingestMessage.sendInfoMessage()) {
                    //mailService.sendIngestRequestedNotification(ingestEmail, subsettableFiles);
                }

            } catch (JMSException ex) {
                ex.printStackTrace();
            } finally {
                try {

                    if (sender != null) {
                        sender.close();
                    }
                    if (session != null) {
                        session.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (JMSException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }



    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addIngestedFiles(Long studyId, List fileBeans, Long userId) {
        // first some initialization
        // TODO: VERSION:
        Study study = studyService.getStudy(studyId);
        StudyVersion studyVersion = study.getStudyVersions().get(0);
        em.refresh(study);
        VDCUser user = userService.find(userId);

        File newDir = new File(FileUtil.getStudyFileDir(), study.getAuthority() + File.separator + study.getStudyId());
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        // now iterate through fileBeans
        Iterator iter = fileBeans.iterator();
        while (iter.hasNext()) {
            StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();

            // for now the logic is if the DSB does not return a file, don't copy
            // over anything; this is to cover the situation with the Ingest servlet
            // that uses takes a control card file to add a dataTable to a prexisting
            // file; this will have to change if we do this two files method at the
            // time of the original upload
            if (fileBean.getIngestedSystemFileLocation() != null) {

                StudyFile f = fileBean.getStudyFile();
                String originalFileType = f.getFileType();
                // attach file to study
                fileBean.getFileMetadata().setStudyVersion( studyVersion );
                fileBean.getStudyFile().setStudy(study );
                study.getStudyFiles().add(fileBean.getStudyFile());
                em.persist(fileBean.getFileMetadata());
                
                //fileBean.addFiletoStudy(study);

                // move ingest-created file
                File tempIngestedFile = new File(fileBean.getIngestedSystemFileLocation());
                File newIngestedLocationFile = new File(newDir, f.getFileSystemName());
                try {
                    FileUtil.copyFile(tempIngestedFile, newIngestedLocationFile);
                    tempIngestedFile.delete();
                    if (f instanceof TabularDataFile ){
                        f.setFileType("text/tab-separated-values");
                    }
                    f.setFileSystemLocation(newIngestedLocationFile.getAbsolutePath());

                } catch (IOException ex) {
                    throw new EJBException(ex);
                }
                // If this is a NetworkDataFile,  move the RData file from the temp Ingested location to the system location
                if (f instanceof NetworkDataFile) {
                    File tempRDataFile = new File(FileUtil.replaceExtension(fileBean.getIngestedSystemFileLocation(), "RData"));
                    File newRDataFile = new File(newDir, f.getFileSystemName()+".RData");
                    try {
                        FileUtil.copyFile(tempRDataFile, newRDataFile);
                        tempRDataFile.delete();
                        f.setOriginalFileType(originalFileType);
                    } catch (IOException ex) {
                        throw new EJBException(ex);
                    }
                }

                // also move original file for archiving
                File tempOriginalFile = new File(fileBean.getTempSystemFileLocation());
                File newOriginalLocationFile = new File(newDir, "_" + f.getFileSystemName());
                try {
                    FileUtil.copyFile(tempOriginalFile, newOriginalLocationFile);
                    tempOriginalFile.delete();
                    f.setOriginalFileType(originalFileType);
                } catch (IOException ex) {
                    throw new EJBException(ex);
                }
            } else {
                //fileBean.getStudyFile().setSubsettable(true);
                em.merge(fileBean.getStudyFile());
            }
        }
        // TODO: VERSION
        // calcualte study UNF
        try {
            studyVersion.getMetadata().setUNF(new DSBWrapper().calculateUNF(studyVersion));
        } catch (IOException e) {
            throw new EJBException("Could not calculate new study UNF");
        }

        study.setLastUpdateTime(new Date());
        study.setLastUpdater(user);

    }


 
}
