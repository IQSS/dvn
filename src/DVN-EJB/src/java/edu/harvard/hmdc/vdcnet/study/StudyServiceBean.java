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
import edu.harvard.hmdc.vdcnet.ddi.DDIServiceLocal;
import edu.harvard.hmdc.vdcnet.dsb.DSBIngestMessage;
import edu.harvard.hmdc.vdcnet.dsb.DSBWrapper;
import edu.harvard.hmdc.vdcnet.gnrs.GNRSServiceLocal;
import edu.harvard.hmdc.vdcnet.harvest.HarvestFormatType;
import edu.harvard.hmdc.vdcnet.harvest.HarvestStudyServiceLocal;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.util.FileUtil;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import edu.harvard.hmdc.vdcnet.vdc.ReviewState;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollection;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetwork;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
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
import javax.xml.bind.JAXBException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.lang.RandomStringUtils;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateless
public class StudyServiceBean implements edu.harvard.hmdc.vdcnet.study.StudyServiceLocal, java.io.Serializable {

    @PersistenceContext(unitName = "VDCNet-ejbPU")
    EntityManager em;
    @Resource(mappedName = "jms/DSBIngest")
    Queue queue;
    @Resource(mappedName = "jms/DSBQueueConnectionFactory")
    QueueConnectionFactory factory;
    @EJB
    GNRSServiceLocal gnrsService;
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
    @EJB
    DDIServiceLocal ddiService;
    @EJB
    UserServiceLocal userService;
    @EJB
    IndexServiceLocal indexService;
    @EJB
    ReviewStateServiceLocal reviewStateService;
    @EJB
    StudyExporterFactoryLocal studyExporterFactory;
    @EJB
    MailServiceLocal mailService;
    private static final Logger logger = Logger.getLogger("edu.harvard.hmdc.vdcnet.study.StudyServiceBean");
    private static final SimpleDateFormat exportLogFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
    @EJB
    StudyServiceLocal studyService; // used to force new transaction during import
    @EJB
    HarvestStudyServiceLocal harvestStudyService;

    /**
     * Creates a new instance of StudyServiceBean
     */
    public StudyServiceBean() {
    }

    /**
     * Add given Study to persistent storage.
     */
    /*  Commented out - not used  
    public void addStudy(Study study) {
    // For each collection of dependent objects, set the relationship to this study.
    if (study.getStudyAbstracts() != null) {
    for (Iterator<StudyAbstract> it = study.getStudyAbstracts().iterator(); it.hasNext();) {
    StudyAbstract elem = it.next();
    elem.setStudy(study);
    }
    }
    if (study.getStudyOtherIds() != null) {
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
     */
    public void updateStudy(Study detachedStudy) {
        em.merge(detachedStudy);
    }

    public Study getStudyByHarvestInfo(VDC dataverse, String harvestIdentifier) {
        String queryStr = "SELECT s FROM Study s WHERE s.owner.id = '" + dataverse.getId() + "' and s.harvestIdentifier = '" + harvestIdentifier + "'";
        Query query = em.createQuery(queryStr);
        List resultList = query.getResultList();
        Study study = null;
        if (resultList.size() > 1) {
            throw new EJBException("More than one study found with owner_id= " + dataverse.getId() + " and harvestIdentifier= " + harvestIdentifier);
        }
        if (resultList.size() == 1) {
            study = (Study) resultList.get(0);
        }
        return study;

    }

    public void deleteStudy(Long studyId) {
        deleteStudy(studyId, true);
    }

    public void deleteStudyList(List<Long> studyIds) {
        indexService.deleteIndexList(studyIds);
        for (Long studyId : studyIds) {
            deleteStudy(studyId, false);
        }

    }

    public void deleteStudy(Long studyId, boolean deleteFromIndex) {
        long start = new Date().getTime();
        logger.info("DEBUG: 0\t - deleteStudy - BEGIN");
        Study study = em.find(Study.class, studyId);
        if (study == null) {
            return;
        }
        logger.info("DEBUG: " + (new Date().getTime() - start) + "\t - deleteStudy - remove from collections");
        for (Iterator<VDCCollection> it = study.getStudyColls().iterator(); it.hasNext();) {
            VDCCollection elem = it.next();
            elem.getStudies().remove(study);

        }
        logger.info("DEBUG: " + (new Date().getTime() - start) + "\t - deleteStudy - delete data variables");
        studyService.deleteDataVariables(study.getId());
        
        logger.info("DEBUG: " + (new Date().getTime() - start) + "\t - deleteStudy - delete relationships");
        study.getAllowedGroups().clear();
        study.getAllowedUsers().clear();
        if (study.getOwner() != null) {
            study.getOwner().getOwnedStudies().remove(study);

        }
        for (Iterator<StudyFile> it = study.getStudyFiles().iterator(); it.hasNext();) {
            StudyFile elem = it.next();
            elem.getAllowedGroups().clear();
            elem.getAllowedUsers().clear();
            if (elem.getDataTable() != null && elem.getDataTable().getDataVariables() != null) {
                elem.getDataTable().getDataVariables().clear();
            }
        }
        
        logger.info("DEBUG: " + (new Date().getTime() - start) + "\t - deleteStudy - delete physical files");
        File studyDir = new File(FileUtil.getStudyFileDir() + File.separator + study.getAuthority() + File.separator + study.getStudyId());
        if (studyDir.exists()) {
            File[] files = studyDir.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();

                }
            }
            studyDir.delete();
        }


        // remove from HarvestStudy table
        logger.info("DEBUG: " + (new Date().getTime() - start) + "\t - deleteStudy - delete from HarvestStudy");
        harvestStudyService.markHarvestStudiesAsRemoved( harvestStudyService.findHarvestStudiesByGlobalId( study.getGlobalId() ), new Date() );
        
        logger.info("DEBUG: " + (new Date().getTime() - start) + "\t - deleteStudy - delete from DB and gnrs");
        em.remove(study);
        gnrsService.delete(study.getAuthority(), study.getStudyId());
        
        logger.info("DEBUG: " + (new Date().getTime() - start) + "\t - deleteStudy - delete from Index");
        if (deleteFromIndex) {
            indexService.deleteStudy(studyId);
        }
        
        logger.info("DEBUG: " + (new Date().getTime() - start) + "\t - deleteStudy - right before flush");
        em.flush();  // Force study deletion to the database, for cases when we are calling this before deleting the owning Dataverse
        logger.info("DEBUG: " + (new Date().getTime() - start) + "\t - deleteStudy - FINISH");
        logger.log(Level.INFO, "Successfully deleted Study " + studyId + "!");


    }
    private static final String DELETE_VARIABLE_CATEGORIES = " delete from variablecategory where datavariable_id in ( " +
            "select dv.id from study s, filecategory fc, studyfile sf, datatable dt, datavariable dv" +
            " where s.id= ? " +
            " and s.id = fc.study_id " +
            " and fc.id= sf.filecategory_id " +
            " and sf.id=dt.studyfile_id " +
            " and dt.id= dv.datatable_id )";
    private static final String DELETE_SUMMARY_STATISTICS = " delete from summarystatistic where datavariable_id in ( " +
            "select dv.id from study s, filecategory fc, studyfile sf, datatable dt, datavariable dv" +
            " where s.id= ? " +
            " and s.id = fc.study_id " +
            " and fc.id= sf.filecategory_id " +
            " and sf.id=dt.studyfile_id " +
            " and dt.id= dv.datatable_id )";
    private static final String DELETE_VARIABLE_RANGE_ITEMS = " delete from variablerangeitem where datavariable_id in ( " +
            "select dv.id from study s, filecategory fc, studyfile sf, datatable dt, datavariable dv" +
            " where s.id= ? " +
            " and s.id = fc.study_id " +
            " and fc.id= sf.filecategory_id " +
            " and sf.id=dt.studyfile_id " +
            " and dt.id= dv.datatable_id )";
    private static final String DELETE_VARIABLE_RANGES = " delete from variablerange where datavariable_id in ( " +
            "select dv.id from study s, filecategory fc, studyfile sf, datatable dt, datavariable dv" +
            " where s.id= ? " +
            " and s.id = fc.study_id " +
            " and fc.id= sf.filecategory_id " +
            " and sf.id=dt.studyfile_id " +
            " and dt.id= dv.datatable_id )";
    private static final String DELETE_DATA_VARIABLES = " delete from datavariable where id in ( " +
            "select dv.id from study s, filecategory fc, studyfile sf, datatable dt, datavariable dv" +
            " where s.id= ? " +
            " and s.id = fc.study_id " +
            " and fc.id= sf.filecategory_id " +
            " and sf.id=dt.studyfile_id " +
            " and dt.id= dv.datatable_id )";
    private static final String SELECT_DATAVARIABLE_IDS = "select dv.id from study s, filecategory fc, studyfile sf, datatable dt, datavariable dv" +
            " where s.id= ? " +
            " and s.id = fc.study_id " +
            " and fc.id= sf.filecategory_id " +
            " and sf.id=dt.studyfile_id " +
            " and dt.id= dv.datatable_id ";

    public void deleteDataVariables(Long studyId) {

        em.createNativeQuery(DELETE_VARIABLE_CATEGORIES).setParameter(1, studyId).executeUpdate();
        em.createNativeQuery(DELETE_SUMMARY_STATISTICS).setParameter(1, studyId).executeUpdate();
        em.createNativeQuery(DELETE_VARIABLE_RANGE_ITEMS).setParameter(1, studyId).executeUpdate();
        em.createNativeQuery(DELETE_VARIABLE_RANGES).setParameter(1, studyId).executeUpdate();

        em.createNativeQuery(DELETE_DATA_VARIABLES).setParameter(1, studyId).executeUpdate();

    }

    /**
     *  Get the Study and all it's dependent objects.
     *  Used to view/update all the Study details
     *  Access each dependent object to trigger it's retreival from the database.
     *
     */
    public Study getStudyDetail(Long studyId) {
        Study study = em.find(Study.class, studyId);

        if (study == null) {
            throw new IllegalArgumentException("Unknown studyId: " + studyId);
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

        for (Iterator<StudyRelStudy> it = study.getStudyRelStudies().iterator(); it.hasNext();) {
            StudyRelStudy elem = it.next();
            elem.getId();
        }

        for (Iterator<StudyRelMaterial> it = study.getStudyRelMaterials().iterator(); it.hasNext();) {
            StudyRelMaterial elem = it.next();
            elem.getId();
        }

        for (Iterator<StudyRelPublication> it = study.getStudyRelPublications().iterator(); it.hasNext();) {
            StudyRelPublication elem = it.next();
            elem.getId();
        }
        for (Iterator<StudyOtherRef> it = study.getStudyOtherRefs().iterator(); it.hasNext();) {
            StudyOtherRef elem = it.next();
            elem.getId();
        }

        return study;
    }

    /**
     *   Gets Study without any of its dependent objects
     *
     */
    public Study getStudy(Long studyId) {

        Study study = em.find(Study.class, studyId);
        if (study == null) {
            throw new IllegalArgumentException("Unknown studyId: " + studyId);
        }


        return study;
    }

    /**
     *   Gets Study and dependent objects based on Map parameter;
     *   used by searchPage
     *
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Study getStudyForSearch(Long studyId, Map studyFields) {

        Study study = em.find(Study.class, studyId);
        if (study == null) {
            throw new IllegalArgumentException("Unknown studyId: " + studyId);
        }



        if (studyFields != null) {
            for (Object studyField : studyFields.keySet()) {
                String fieldName = (String) studyField;
                if ("authorName".equals(fieldName)) {
                    for (Iterator<StudyAuthor> it = study.getStudyAuthors().iterator(); it.hasNext();) {
                        StudyAuthor elem = it.next();
                        elem.getId();
                    }
                } else if ("abstractText".equals(fieldName)) {
                    for (Iterator<StudyAbstract> it = study.getStudyAbstracts().iterator(); it.hasNext();) {
                        StudyAbstract elem = it.next();
                        elem.getId();
                    }
                } else if ("producerName".equals(fieldName)) {
                    for (Iterator<StudyProducer> it = study.getStudyProducers().iterator(); it.hasNext();) {
                        StudyProducer elem = it.next();
                        elem.getId();
                    }
                } else if ("distributorName".equals(fieldName)) {
                    for (Iterator<StudyDistributor> it = study.getStudyDistributors().iterator(); it.hasNext();) {
                        StudyDistributor elem = it.next();
                        elem.getId();
                    }
                } else if ("relatedStudies".equals(fieldName)) {
                    for (Iterator<StudyRelStudy> it = study.getStudyRelStudies().iterator(); it.hasNext();) {
                        StudyRelStudy elem = it.next();
                        elem.getId();
                    }
                } else if ("relatedMaterial".equals(fieldName)) {
                    for (Iterator<StudyRelMaterial> it = study.getStudyRelMaterials().iterator(); it.hasNext();) {
                        StudyRelMaterial elem = it.next();
                        elem.getId();
                    }
                } else if ("relatedPublications".equals(fieldName)) {
                    for (Iterator<StudyRelPublication> it = study.getStudyRelPublications().iterator(); it.hasNext();) {
                        StudyRelPublication elem = it.next();
                        elem.getId();
                    }
                }
            }

        }

        return study;
    }

    public List getStudies() {
        String query = "SELECT s FROM Study s ORDER BY s.id";
        return (List) em.createQuery(query).getResultList();
    }

    public List getRecentStudies(Long vdcId, int numResults) {
        String query = "SELECT s FROM Study s WHERE s.owner.id = " + vdcId + " ORDER BY s.createTime desc";
        if (numResults == -1) {
            return (List) em.createQuery(query).getResultList();
        } else {
            return (List) em.createQuery(query).setMaxResults(numResults).getResultList();
        }
    }

    public List<Long> getStudyIdsForExport() {
        String queryStr = "select id from study where isharvested='false' and (lastupdatetime > lastexporttime or lastexporttime is null)";
        Query query = em.createNativeQuery(queryStr);
        List<Long> returnList = new ArrayList<Long>();
        // since query is native, must parse through Vector results
        for (Object currentResult : query.getResultList()) {
            // convert results into Longs
            returnList.add(new Long(((Integer) ((Vector) currentResult).get(0))).longValue());
        }

        return returnList;
    }

    public List<Long> getAllStudyIds() {
        String queryStr = "select id from study";
        Query query = em.createNativeQuery(queryStr);
        List<Long> returnList = new ArrayList<Long>();
        // since query is native, must parse through Vector results
        for (Object currentResult : query.getResultList()) {
            // convert results into Longs
            returnList.add(new Long(((Integer) ((Vector) currentResult).get(0))).longValue());
        }

        return returnList;
    }
    
    public List<Long> getAllNonHarvestedStudyIds() {
        String queryStr = "select id from study where isharvested='false'";
        Query query = em.createNativeQuery(queryStr);
        List<Long> returnList = new ArrayList<Long>();
        // since query is native, must parse through Vector results
        for (Object currentResult : query.getResultList()) {
            // convert results into Longs
            returnList.add(new Long(((Integer) ((Vector) currentResult).get(0))).longValue());
        }

        return returnList;
    }    

    public List getOrderedStudies(List studyIdList, String orderBy) {
        String studyIds = "";
        Iterator iter = studyIdList.iterator();
        while (iter.hasNext()) {
            Long id = (Long) iter.next();
            studyIds += id + (iter.hasNext() ? "," : "");
        }

        String query = "SELECT s.id FROM Study s WHERE s.id in (" + studyIds + ") ORDER BY s." + orderBy;

        //System.out.println("getStudies - Query: " + query);
        return (List) em.createQuery(query).getResultList();
    }

    public List<Study> getReviewerStudies(Long vdcId) {
        String query = "SELECT s FROM Study s WHERE s.reviewState.name = 'In Review' AND s.owner.id = " + vdcId;
        List<Study> studies = em.createQuery(query).getResultList();
        for (Iterator it = studies.iterator(); it.hasNext();) {
            Study elem = (Study) it.next();
            elem.getReviewState();
        }

        return studies;
    }

    public List<Study> getNewStudies(Long vdcId) {
        String query = "SELECT s FROM Study s WHERE s.reviewState.name = 'New' AND s.owner.id = " + vdcId;
        List<Study> studies = em.createQuery(query).getResultList();
        for (Iterator it = studies.iterator(); it.hasNext();) {
            Study elem = (Study) it.next();
            elem.getReviewState();
        }

        return studies;
    }

    public List<Study> getContributorStudies(VDCUser contributor, VDC vdc) {
        String query = "SELECT s FROM Study s WHERE s.creator.id = " + contributor.getId().toString() + " and s.owner.id =" + vdc.getId();
        List<Study> studies = em.createQuery(query).getResultList();
        for (Iterator it = studies.iterator(); it.hasNext();) {
            Study elem = (Study) it.next();
            elem.getReviewState();
        }
        return studies;
    }

    public StudyFile getStudyFile(Long fileId) {
        StudyFile file = em.find(StudyFile.class, fileId);
        if (file == null) {
            throw new IllegalArgumentException("Unknown studyFileId: " + fileId);
        }


        return file;
    }

    public FileCategory getFileCategory(Long fileCategoryId) {
        FileCategory fileCategory = em.find(FileCategory.class, fileCategoryId);
        if (fileCategory == null) {
            throw new IllegalArgumentException("Unknown fileCategoryId: " + fileCategoryId);
        }


        return fileCategory;
    }

    public List<DataFileFormatType> getDataFileFormatTypes() {
        return em.createQuery("select object(t) from DataFileFormatType as t").getResultList();
    }

    public void addFiles(Study study, List<StudyFileEditBean> newFiles, VDCUser user) {
        addFiles(study, newFiles, user, user.getEmail(), DSBIngestMessage.INGEST_MESAGE_LEVEL_ERROR);
    }

    public void addFiles(Study study, List<StudyFileEditBean> newFiles, VDCUser user, String ingestEmail) {
        addFiles(study, newFiles, user, ingestEmail, DSBIngestMessage.INGEST_MESAGE_LEVEL_INFO);
    }

    private void addFiles(Study study, List<StudyFileEditBean> newFiles, VDCUser user, String ingestEmail, int messageLevel) {
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
                // also add to category, so that it will be flushed for the ids
                addFileToCategory(fileBean.getStudyFile(), fileBean.getFileCategoryName(), study);
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
                    mailService.sendIngestRequestedNotification(ingestEmail, subsettableFiles);
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

    private void addFileToCategory(StudyFile file, String catName, Study s) {
        if (catName == null) {
            catName = "";
        }

        Iterator iter = s.getFileCategories().iterator();
        while (iter.hasNext()) {
            FileCategory cat = (FileCategory) iter.next();
            if (cat.getName().equals(catName)) {
                file.setFileCategory(cat);
                cat.getStudyFiles().add(file);
                return;
            }
        }

        // category was not found, so we create a new file category
        FileCategory cat = new FileCategory();
        cat.setStudy(s);
        s.getFileCategories().add(cat);
        cat.setName(catName);
        cat.setStudyFiles(new ArrayList());

        // link cat to file
        file.setFileCategory(cat);
        cat.getStudyFiles().add(file);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addIngestedFiles(Long studyId, List fileBeans, Long userId) {
        // first some initialization
        Study study = getStudy(studyId);
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
                fileBean.addFileToCategory(study);

                // move ingest-created file
                File tempIngestedFile = new File(fileBean.getIngestedSystemFileLocation());
                File newIngestedLocationFile = new File(newDir, f.getFileSystemName());
                try {
                    FileUtil.copyFile(tempIngestedFile, newIngestedLocationFile);
                    tempIngestedFile.delete();
                    f.setFileType("text/tab-separated-values");
                    f.setFileSystemLocation(newIngestedLocationFile.getAbsolutePath());

                } catch (IOException ex) {
                    throw new EJBException(ex);
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
                fileBean.getStudyFile().setSubsettable(true);
                em.merge(fileBean.getStudyFile());
            }
        }

        // calcualte study UNF
        try {
            study.setUNF(new DSBWrapper().calculateUNF(study));
        } catch (IOException e) {
            throw new EJBException("Could not calculate new study UNF");
        }

        study.setLastUpdateTime(new Date());
        study.setLastUpdater(user);

    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public java.util.List<FileCategory> getOrderedFileCategories(Long studyId) {
        // Note: This ordering is case-sensitive, so names beginning with upperclass chars will appear first.
        // (I tried using UPPER(f.name) to make the sorting case-insensitive, but the EJB query language doesn't seem
        // to like this.)
        String query = "SELECT f FROM FileCategory f WHERE f.study.id = " + studyId + " ORDER BY f.name";
        List<FileCategory> categories = em.createQuery(query).getResultList();

        return categories;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public java.util.List<StudyFile> getOrderedFilesByCategory(Long fileCategoryId) {
        // Note: This ordering is case-sensitive, so names beginning with upperclass chars will appear first.
        // (I tried using UPPER(f.name) to make the sorting case-insensitive, but the EJB query language doesn't seem
        // to like this.)
        String queryStr = "SELECT f FROM StudyFile f WHERE f.fileCategory.id = " + fileCategoryId + " ORDER BY f.fileName";
        Query query = em.createQuery(queryStr);
        List<StudyFile> studyFiles = query.getResultList();

        return studyFiles;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public java.util.List<StudyFile> getOrderedFilesByStudy(Long studyId) {
        // Note: This ordering is case-sensitive, so names beginning with upperclass chars will appear first.
        // (I tried using UPPER(f.name) to make the sorting case-insensitive, but the EJB query language doesn't seem
        // to like this.)
        String queryStr = "SELECT f FROM StudyFile f LEFT JOIN FETCH f.dataTable JOIN FETCH f.fileCategory WHERE f.fileCategory.study.id = " + studyId + " ORDER BY f.fileCategory.name, f.fileName";
        Query query = em.createQuery(queryStr);
        List<StudyFile> studyFiles = query.getResultList();

        return studyFiles;
    }

    public String generateStudyIdSequence(String protocol, String authority) {
        //   Date now = new Date();
        //   return ""+now.getTime();
        //   return em.createNamedQuery("getStudyIdSequence").getSingleResult().toString();
        String studyId = null;
        studyId = gnrsService.getNewObjectId(protocol, authority);
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
    public boolean isUniqueStudyId(String userStudyId, String protocol, String authority) {

        String queryStr = "SELECT s from Study s where s.studyId = :studyId  and s.protocol= :protocol and s.authority= :authority";

        Study study = null;

        Query query = em.createQuery(queryStr);
        query.setParameter("studyId", userStudyId);
        query.setParameter("protocol", protocol);
        query.setParameter("authority", authority);
        return query.getResultList().size() == 0;

    }

    public void exportStudyFilesToLegacySystem(String lastUpdateTime, String authority) {
        // Get list of studies that have been updated yesterday,
        // and export them to legacy VDC system

        Logger logger = null;

        String exportLogDirStr = System.getProperty("vdc.export.log.dir");
        if (exportLogDirStr == null) {
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
        if (logger.getHandlers() != null && logger.getHandlers().length > 0) {
            int numHandlers = logger.getHandlers().length;
            for (int i = 0; i < numHandlers; i++) {
                logger.removeHandler(logger.getHandlers()[i]);
            }
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
        FileHandler handler = null;
        try {
            handler = new FileHandler(exportLogDirStr + File.separator + "export_" + formatter.format(new Date()) + ".log");
        } catch (IOException e) {
            throw new EJBException(e);
        }

        // Add handler to the desired logger
        logger.addHandler(handler);


        logger.info("Begin Exporting Studies");
        int studyCount = 0;
        int deletedStudyCount = 0;
        try {

            // For all studies that have been deleted in the dataverse since last export, remove study directory in VDC

            String query = "SELECT s from DeletedStudy s where s.authority = '" + authority + "' ";
            List deletedStudies = em.createQuery(query).getResultList();
            for (Iterator it = deletedStudies.iterator(); it.hasNext();) {
                DeletedStudy deletedStudy = (DeletedStudy) it.next();

                logger.info("Deleting study " + deletedStudy.getGlobalId());
                Study study = em.find(Study.class, deletedStudy.getId());
                File legacyStudyDir = new File(FileUtil.getLegacyFileDir() + File.separatorChar + study.getAuthority() + File.separatorChar + study.getStudyId());

                // Remove files in the directory, then delete the directory.
                File[] studyFiles = legacyStudyDir.listFiles();
                if (studyFiles != null) {
                    for (int i = 0; i < studyFiles.length; i++) {
                        studyFiles[i].delete();
                    }
                }
                legacyStudyDir.delete();
                deletedStudyCount++;

                em.remove(deletedStudy);
            }

            // Do export of all studies updated at "lastUpdateTime""

            if (authority == null) {
                authority = vdcNetworkService.find().getAuthority();
            }
            String beginTime = null;
            String endTime = null;
            if (lastUpdateTime == null) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, -1);
                beginTime = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());  // Use yesterday as default value
                cal.add(Calendar.DAY_OF_YEAR, 1);
                endTime = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            } else {
                beginTime = lastUpdateTime;
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(lastUpdateTime);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DAY_OF_YEAR, 1);
                endTime = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            }
            query = "SELECT s from Study s where s.authority = '" + authority + "' ";
            query += " and s.lastUpdateTime >'" + beginTime + "'";
            //    query+=" and s.lastUpdateTime <'" +endTime+"'";
            query += " order by s.studyId";
            List updatedStudies = em.createQuery(query).getResultList();


            for (Iterator it = updatedStudies.iterator(); it.hasNext();) {
                Study study = (Study) it.next();
                logger.info("Exporting study " + study.getStudyId());

                exportStudyToLegacySystem(study, authority);
                studyCount++;

            }
        } catch (Exception e) {
            logger.severe(e.getMessage());

            String stackTrace = "StackTrace: \n";
            logger.severe("Exception caused by: " + e + "\n");
            StackTraceElement[] ste = e.getStackTrace();
            for (int m = 0; m < ste.length; m++) {
                stackTrace += ste[m].toString() + "\n";
            }
            logger.severe(stackTrace);
        }

        logger.info("End export, " + studyCount + " studies successfully exported, " + deletedStudyCount + " studies deleted.");
    }

    private void exportStudyToLegacySystem(Study study, String authority) throws IOException, JAXBException {

        throw new EJBException("This feature is no longer supported!!");
    /*
    // For each study
    // update study file locations for legacy system
    // Write ddi to an output stream
    // If data file dir exists, delete everything from it
    // copy ddi to study.xml,
    // copy study files.
    File studyDir = new File(FileUtil.getStudyFileDir() + File.separatorChar + authority + File.separatorChar + study.getStudyId());
    File legacyStudyDir = new File(FileUtil.getLegacyFileDir() + File.separatorChar + authority + File.separatorChar + study.getStudyId());
    
    // If the directory exists in the legacy system, then clear out all the files contained in it
    if (legacyStudyDir.exists() && legacyStudyDir.isDirectory()) {
    File[] files = legacyStudyDir.listFiles();
    for (int i = 0; i < files.length; i++) {
    files[i].delete();
    }
    } else {
    legacyStudyDir.mkdirs();
    }
    
    // Export the study to study.xml in the legacy directory
    FileWriter fileWriter = new FileWriter(new File(legacyStudyDir, "study.xml"));
    try {
    ddiService.exportStudy(study, fileWriter, true, true);
    fileWriter.flush();
    } finally {
    fileWriter.close();
    }
    
    // Copy all the study files to the legacy directory
    
    for (Iterator it = study.getStudyFiles().iterator(); it.hasNext();) {
    StudyFile studyFile = (StudyFile) it.next();
    FileUtil.copyFile(new File(studyDir, studyFile.getFileSystemName()), new File(legacyStudyDir, studyFile.getFileSystemName()));
    }
     */

    }

    public String generateFileSystemNameSequence() {
        String fileSystemName = null;
        do {
            Vector result = (Vector) em.createNativeQuery("select nextval('filesystemname_seq')").getSingleResult();
            fileSystemName = result.get(0).toString();
        } while (!isUniqueFileSystemName(fileSystemName));

        return fileSystemName;

    }

    /**
     *  Check that a fileId  unique (not currently used for any other file in this study)
     */
    public boolean isUniqueFileSystemName(String fileSystemName) {
        String query = "SELECT f FROM StudyFile f WHERE f.fileSystemName = '" + fileSystemName + "'";
        return em.createQuery(query).getResultList().size() == 0;
    }

    public List<StudyLock> getStudyLocks() {
        String query = "SELECT sl FROM StudyLock sl";
        return (List<StudyLock>) em.createQuery(query).getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addStudyLock(Long studyId, Long userId, String detail) {
        Study study = em.find(Study.class, studyId);
        VDCUser user = em.find(VDCUser.class, userId);

        StudyLock lock = new StudyLock();
        lock.setStudy(study);
        lock.setUser(user);
        lock.setDetail(detail);
        lock.setStartTime(new Date());

        study.setStudyLock(lock);
        if (user.getStudyLocks() == null) {
            user.setStudyLocks(new ArrayList());
        }
        user.getStudyLocks().add(lock);

        em.persist(lock);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeStudyLock(Long studyId) {
        Study study = em.find(Study.class, studyId);
        em.refresh(study);
        StudyLock lock = study.getStudyLock();
        if (lock != null) {
            VDCUser user = lock.getUser();
            study.setStudyLock(null);
            user.getStudyLocks().remove(lock);
            em.remove(lock);
        }
    }
    
    public void incrementNumberOfDownloads(Long studyFileId) {
        incrementNumberOfDownloads( studyFileId, new Date() );
    }

    public void incrementNumberOfDownloads(Long studyFileId, Date lastDownloadTime) {
        StudyFile sf = getStudyFile(studyFileId);
        StudyFileActivity sfActivity = sf.getStudyFileActivity();

        if (sfActivity == null) {
            sfActivity = new StudyFileActivity();
            sf.setStudyFileActivity(sfActivity);
            sfActivity.setStudyFile(sf);
        }

        sfActivity.setDownloadCount(sfActivity.getDownloadCount() + 1);
        sfActivity.setLastDownloadTime( lastDownloadTime );
    }

    public RemoteAccessAuth lookupRemoteAuthByHost (String hostName) {
        String queryStr = "SELECT r FROM RemoteAccessAuth r WHERE r.hostName= :hostName" ;

        RemoteAccessAuth remoteAuth = null;
	
        try {
            Query query = em.createQuery(queryStr);
            query.setParameter("hostName", hostName);
	    List resultList = query.getResultList();
	    if (resultList.size() > 0) {
		remoteAuth = (RemoteAccessAuth) resultList.get(0);
	    }
	} catch (javax.persistence.NoResultException e) {
            // DO nothing, just return null.
        }
        return remoteAuth;
    }
	

    public Study getStudyByGlobalId(String identifier) {

        String protocol = null;
        String authority = null;
        String studyId = null;
        int index1 = identifier.indexOf(':');
        int index2 = identifier.indexOf('/');
        if (index1 == -1) {
            throw new EJBException("Error parsing identifier: " + identifier + ". ':' not found in string");
        } else {
            protocol = identifier.substring(0, index1);
        }
        if (index2 == -1) {
            throw new EJBException("Error parsing identifier: " + identifier + ". '/' not found in string");

        } else {
            authority = identifier.substring(index1 + 1, index2);
        }
        studyId = identifier.substring(index2 + 1).toUpperCase();

        String queryStr = "SELECT s from Study s where s.studyId = :studyId  and s.protocol= :protocol and s.authority= :authority";

        Study study = null;
        try {
            Query query = em.createQuery(queryStr);
            query.setParameter("studyId", studyId);
            query.setParameter("protocol", protocol);
            query.setParameter("authority", authority);
            study = (Study) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            // DO nothing, just return null.
        }
        return study;
    }

    public DeletedStudy getDeletedStudyByGlobalId(String identifier) {


        String queryStr = "SELECT s from DeletedStudy s where s.globalId = :identifier";

        DeletedStudy study = null;
        try {
            Query query = em.createQuery(queryStr);
            query.setParameter("identifier", identifier);
            study = (DeletedStudy) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            // DO nothing, just return null.
        }
        return study;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Study importHarvestStudy(File xmlFile, Long vdcId, Long userId, String harvestIdentifier) {
        VDC vdc = em.find(VDC.class, vdcId);
        em.refresh(vdc); // workaround to get correct value for harvesting dataverse (to be investigated)

        if (vdc.getHarvestingDataverse() == null) {
            throw new EJBException("importHarvestStudy(...) should only be called for a harvesting dataverse.");
        }

        Study study = doImportStudy(xmlFile, vdc.getHarvestingDataverse().getHarvestFormatType().getId(), vdcId, userId, harvestIdentifier, null);

        // new create exports files for these studies

        studyService.exportStudy(study);


        return study;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Study importStudy(File xmlFile, Long harvestFormatTypeId, Long vdcId, Long userId) {
        return doImportStudy(xmlFile, harvestFormatTypeId, vdcId, userId, null, null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Study importStudy(File xmlFile, Long harvestFormatTypeId, Long vdcId, Long userId, List<StudyFileEditBean> filesToUpload) {
        return doImportStudy(xmlFile, harvestFormatTypeId, vdcId, userId, null, filesToUpload);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Study importStudy(File xmlFile, Long harvestFormatTypeId, Long vdcId, Long userId, String harvestIdentifier, List<StudyFileEditBean> filesToUpload) {
        return doImportStudy(xmlFile, harvestFormatTypeId, vdcId, userId, harvestIdentifier, filesToUpload);
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
            transformer.transform(source, result);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new EJBException("Error occurred while attempting to transform file: " + ex.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
            }
        }


        return ddiFile;

    }

    private void copyXMLFile(Study study, File xmlFile, String xmlFileName) {
        try {
            // create, if needed, the directory
            File newDir = FileUtil.getStudyFileDir(study);
            if (!newDir.exists()) {
                newDir.mkdirs();
            }

            FileUtil.copyFile(xmlFile, new File(newDir, xmlFileName));
        } catch (IOException ex) {
            ex.printStackTrace();
            String msg = "ImportStudy failed: ";
            if (ex.getMessage() != null) {
                msg += ex.getMessage();
            }
            EJBException e = new EJBException(msg);
            e.initCause(ex);
            throw e;
        }
    }

    private void clearStudy(Study study) {
        deleteDataVariables(study.getId());

        for (StudyFile elem : study.getStudyFiles()) {
            if (elem.getDataTable() != null && elem.getDataTable().getDataVariables() != null) {
                elem.getDataTable().getDataVariables().clear();
            }
        }

        for (Iterator iter = study.getFileCategories().iterator(); iter.hasNext();) {
            FileCategory elem = (FileCategory) iter.next();
            removeCollectionElement(iter, elem);
        }
        for (Iterator iter = study.getStudyAbstracts().iterator(); iter.hasNext();) {
            StudyAbstract elem = (StudyAbstract) iter.next();
            removeCollectionElement(iter, elem);
        }
        for (Iterator iter = study.getStudyAuthors().iterator(); iter.hasNext();) {
            StudyAuthor elem = (StudyAuthor) iter.next();
            removeCollectionElement(iter, elem);
        }
        for (Iterator iter = study.getStudyDistributors().iterator(); iter.hasNext();) {
            StudyDistributor elem = (StudyDistributor) iter.next();
            removeCollectionElement(iter, elem);
        }
        for (Iterator iter = study.getStudyGeoBoundings().iterator(); iter.hasNext();) {
            StudyGeoBounding elem = (StudyGeoBounding) iter.next();
            removeCollectionElement(iter, elem);
        }
        for (Iterator iter = study.getStudyGrants().iterator(); iter.hasNext();) {
            StudyGrant elem = (StudyGrant) iter.next();
            removeCollectionElement(iter, elem);
        }
        for (Iterator iter = study.getStudyKeywords().iterator(); iter.hasNext();) {
            StudyKeyword elem = (StudyKeyword) iter.next();
            removeCollectionElement(iter, elem);
        }
        for (Iterator iter = study.getStudyNotes().iterator(); iter.hasNext();) {
            StudyNote elem = (StudyNote) iter.next();
            removeCollectionElement(iter, elem);
        }
        for (Iterator iter = study.getStudyOtherIds().iterator(); iter.hasNext();) {
            StudyOtherId elem = (StudyOtherId) iter.next();
            removeCollectionElement(iter, elem);
        }
        for (Iterator iter = study.getStudyOtherRefs().iterator(); iter.hasNext();) {
            StudyOtherRef elem = (StudyOtherRef) iter.next();
            removeCollectionElement(iter, elem);
        }
        for (Iterator iter = study.getStudyProducers().iterator(); iter.hasNext();) {
            StudyProducer elem = (StudyProducer) iter.next();
            removeCollectionElement(iter, elem);
        }
        for (Iterator iter = study.getStudyRelMaterials().iterator(); iter.hasNext();) {
            StudyRelMaterial elem = (StudyRelMaterial) iter.next();
            removeCollectionElement(iter, elem);
        }
        for (Iterator iter = study.getStudyRelPublications().iterator(); iter.hasNext();) {
            StudyRelPublication elem = (StudyRelPublication) iter.next();
            removeCollectionElement(iter, elem);
        }
        for (Iterator iter = study.getStudyRelStudies().iterator(); iter.hasNext();) {
            StudyRelStudy elem = (StudyRelStudy) iter.next();
            removeCollectionElement(iter, elem);
        }
        for (Iterator iter = study.getStudySoftware().iterator(); iter.hasNext();) {
            StudySoftware elem = (StudySoftware) iter.next();
            removeCollectionElement(iter, elem);
        }
        for (Iterator iter = study.getStudyTopicClasses().iterator(); iter.hasNext();) {
            StudyTopicClass elem = (StudyTopicClass) iter.next();
            removeCollectionElement(iter, elem);
        }

        study.setAccessToSources(null);
        study.setActionsToMinimizeLoss(null);
        study.setAuthority(null);
        study.setAvailabilityStatus(null);
        study.setCharacteristicOfSources(null);
        study.setCitationRequirements(null);
        study.setCleaningOperations(null);
        study.setCollectionMode(null);
        study.setCollectionSize(null);
        study.setConditions(null);
        study.setConfidentialityDeclaration(null);
        study.setContact(null);
        study.setControlOperations(null);
        study.setCountry(null);
        //study.setCreateTime(null);
        //study.setCreator(null);
        study.setDataCollectionSituation(null);
        study.setDataCollector(null);
        study.setDataSources(null);
        study.setDateOfCollectionEnd(null);
        study.setDateOfCollectionStart(null);
        study.setDateOfDeposit(null);
        //study.setDefaultFileCategory(null);
        study.setDepositor(null);
        study.setDepositorRequirements(null);
        study.setDeviationsFromSampleDesign(null);
        study.setDisclaimer(null);
        study.setDistributionDate(null);
        study.setDistributorContact(null);
        study.setDistributorContactAffiliation(null);
        study.setDistributorContactEmail(null);
        study.setFrequencyOfDataCollection(null);
        study.setFundingAgency(null);
        study.setGeographicCoverage(null);
        study.setGeographicUnit(null);
        study.setHarvestDVNTermsOfUse(null);
        study.setHarvestDVTermsOfUse(null);
        study.setHarvestHoldings(null);
        study.setHarvestIdentifier(null);
        //study.setId(null);
        //study.setIsHarvested(null);
        study.setKindOfData(null);
        //study.setLastExportTime(null);
        //study.setLastUpdateTime(null);
        //study.setLastUpdater(null);
        //study.setNumberOfDownloads(null);
        //study.setNumberOfFiles(null);
        study.setOriginOfSources(null);
        study.setOriginalArchive(null);
        study.setOtherDataAppraisal(null);
        //study.setOwner(null);
        study.setPlaceOfAccess(null);
        study.setProductionDate(null);
        study.setProductionPlace(null);
        study.setProtocol(null);
        study.setReplicationFor(null);
        //study.setRequestAccess(null);
        study.setResearchInstrument(null);
        study.setResponseRate(null);
        //study.setRestricted(restricted)s(null);
        study.setRestrictions(null);
        //study.setReviewState(null);
        //study.setReviewer(null);
        study.setSamplingErrorEstimate(null);
        study.setSamplingProcedure(null);
        study.setSeriesInformation(null);
        study.setSeriesName(null);
        study.setSpecialPermissions(null);
        study.setStudyCompletion(null);
        study.setStudyId(null);
        study.setStudyLevelErrorNotes(null);
        study.setStudyVersion(null);
        study.setSubTitle(null);
        //study.setTemplate(null);
        study.setTimeMethod(null);
        study.setTimePeriodCoveredEnd(null);
        study.setTimePeriodCoveredStart(null);
        study.setTitle(null);
        study.setUNF(null);
        study.setUnitOfAnalysis(null);
        study.setUniverse(null);
        //study.setVersion(null);
        study.setVersionDate(null);
        study.setWeighting(null);
    }

    private void removeCollectionElement(Iterator iter, Object elem) {
        iter.remove();
        em.remove(elem);
    }

    private void setDisplayOrders(Study study) {
        int i = 0;
        for (Iterator it = study.getStudyAuthors().iterator(); it.hasNext();) {
            StudyAuthor elem = (StudyAuthor) it.next();
            elem.setDisplayOrder(i);
            i++;

        }
        i = 0;
        for (Iterator it = study.getStudyAbstracts().iterator(); it.hasNext();) {
            StudyAbstract elem = (StudyAbstract) it.next();
            elem.setDisplayOrder(i);
            i++;

        }
        i = 0;
        for (Iterator it = study.getStudyDistributors().iterator(); it.hasNext();) {
            StudyDistributor elem = (StudyDistributor) it.next();
            elem.setDisplayOrder(i);
            i++;

        }
        i = 0;
        for (Iterator it = study.getStudyGeoBoundings().iterator(); it.hasNext();) {
            StudyGeoBounding elem = (StudyGeoBounding) it.next();
            elem.setDisplayOrder(i);
            i++;

        }
        i = 0;
        for (Iterator it = study.getStudyGrants().iterator(); it.hasNext();) {
            StudyGrant elem = (StudyGrant) it.next();
            elem.setDisplayOrder(i);
            i++;
        }

        i = 0;
        for (Iterator it = study.getStudyKeywords().iterator(); it.hasNext();) {
            StudyKeyword elem = (StudyKeyword) it.next();
            elem.setDisplayOrder(i);
            i++;
        }

        i = 0;
        for (Iterator it = study.getStudyNotes().iterator(); it.hasNext();) {
            StudyNote elem = (StudyNote) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        i = 0;
        for (Iterator it = study.getStudyOtherIds().iterator(); it.hasNext();) {
            StudyOtherId elem = (StudyOtherId) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        i = 0;
        for (Iterator it = study.getStudyProducers().iterator(); it.hasNext();) {
            StudyProducer elem = (StudyProducer) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        i = 0;
        for (Iterator it = study.getStudySoftware().iterator(); it.hasNext();) {
            StudySoftware elem = (StudySoftware) it.next();
            elem.setDisplayOrder(i);
            i++;
        }

        i = 0;
        for (Iterator it = study.getStudyTopicClasses().iterator(); it.hasNext();) {
            StudyTopicClass elem = (StudyTopicClass) it.next();
            elem.setDisplayOrder(i);
            i++;
        }

    }

    public Study saveStudy(Study study, Long userId) {
        VDCUser user = em.find(VDCUser.class, userId);

        study.setLastUpdateTime(new Date());
        study.setLastUpdater(user);

        setDisplayOrders(study);
        return study;
    }

    // visible studies are defined as those that are released and not in a restricted vdc
    // (unless you are in vdc)
    public List getVisibleStudies(List studyIds, Long vdcId) {
        if (studyIds != null && studyIds.size() > 0) {
            String query = "SELECT s.id FROM Study s WHERE s.reviewState.name = 'Released' " +
                    "AND (s.owner.restricted = false " +
                    (vdcId != null ? "OR s.owner.id = " + vdcId : "") +
                    ") AND s.id in (" + generateIdString(studyIds) + ")";

            return (List) em.createQuery(query).getResultList();
        } else {
            return new ArrayList();
        }
    }

    // viewable studies are those that are defined as not restricted to the user
    public List getViewableStudies(List<Long> studyIds, Long userId, Long ipUserGroupId) {
        List returnList = new ArrayList();

        if (studyIds != null && studyIds.size() > 0) {
            // dynamically generate the query
            StringBuffer queryString = new StringBuffer("select id from study s ");
            StringBuffer whereClause = new StringBuffer("where ( restricted = false ");
            boolean groupJoinAdded = false;
                        
            if (userId != null) {
                queryString.append("left join study_vdcuser su on (s.id = su.studies_id) ");
                queryString.append("left join study_usergroup sg on (s.id = sg.studies_id ) ");
                whereClause.append("or su.allowedusers_id = ? ");
                whereClause.append("or sg.allowedgroups_id in (select usergroups_id from vdcuser_usergroup where users_id = ?) ");
                groupJoinAdded = true;
            }
            
            if (ipUserGroupId != null) {
                if(!groupJoinAdded) {
                    queryString.append("left join study_usergroup sg on (s.id = sg.studies_id ) ");
                }
                whereClause.append("or sg.allowedgroups_id = ? ");
            }
            
            whereClause.append(") ");
                       
            // now add ids part of where clause
            for (int i = 0; i < studyIds.size(); i++) {
                if (i == 0) {
                    whereClause.append("and id in ( ?");
                } else {
                    whereClause.append(", ?");
                }
            }

            whereClause.append(" )");

            
            // we are now ready to create the query
            Query query = em.createNativeQuery( queryString.toString() + whereClause.toString() );
            
            // now set parameters
            int parameterCount = 1;
            
            if (userId != null) {
                query.setParameter(parameterCount++, userId);
                query.setParameter(parameterCount++, userId);
            }
            
            if (ipUserGroupId != null) {
                query.setParameter(parameterCount++, ipUserGroupId);
            }
            
            for (Long id : studyIds) {
                query.setParameter(parameterCount++, id);
            }

            // since query is native, must parse through Vector results
            for (Object currentResult : query.getResultList()) {
                // convert results into Longs
                returnList.add(new Long(((Integer) ((Vector) currentResult).get(0))).longValue());
            }
        }

        return returnList;

    }

    private String generateIdString(List idList) {
        String ids = "";
        Iterator iter = idList.iterator();
        while (iter.hasNext()) {
            Long id = (Long) iter.next();
            ids += id + (iter.hasNext() ? "," : "");
        }

        return ids;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void exportStudy(Study study) {
        List<String> exportFormats = studyExporterFactory.getExportFormats();
        for (String exportFormat : exportFormats) {
            exportStudyToFormat(study, exportFormat);
        }
        study.setLastExportTime(new Date());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void exportStudy(Long studyId) {
        Study study = em.find(Study.class, studyId);
        exportStudy(study);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void exportStudyToFormat(Long studyId, String exportFormat) {
        Study study = em.find(Study.class, studyId);
        exportStudyToFormat(study, exportFormat);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void exportStudyToFormat(Study study, String exportFormat) {

        File studyDir = FileUtil.getStudyFileDir(study);

        StudyExporter studyExporter = studyExporterFactory.getStudyExporter(exportFormat);
        String fileName = "export_" + exportFormat;
        if (studyExporter.isXmlFormat()) {
            fileName += ".xml";
        }
        File exportFile = new File(studyDir, fileName);
        OutputStream os = null;
        try {
            exportFile.createNewFile();
            os = new FileOutputStream(exportFile);

            studyExporter.exportStudy(study, os);
        } catch (IOException e) {
            throw new EJBException(e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException ex) {
            }
        }

    //  study.setLastExportTime(new Date());

    }

    public void exportUpdatedStudies() {
        exportStudies(studyService.getStudyIdsForExport());
        
        harvestStudyService.updateHarvestStudies();
    }

    public void exportStudies(List<Long> studyIds, String exportFormat) {
        String logTimestamp = exportLogFormatter.format(new Date());
        Logger exportLogger = Logger.getLogger("edu.harvard.hmdc.vdcnet.study.StudyServiceBean.export." + logTimestamp);
        List<Long> harvestedStudyIds = new ArrayList<Long>();
        try {

            exportLogger.addHandler(new FileHandler(FileUtil.getExportFileDir() + File.separator + "export_" + logTimestamp + ".log"));
        } catch (IOException e) {

            logger.severe("Exception adding log file handler " + FileUtil.getExportFileDir() + File.separator + "export_" + logTimestamp + ".log");
            return;
        }
        try {
            exportLogger.info("Begin exporting studies, number of studies to export: " + studyIds.size());
            for (Long studyId : studyIds) {
                Study study = em.find(Study.class, studyId);
                exportLogger.info("Begin export for study " + study.getGlobalId());
                if (exportFormat == null) {
                    studyService.exportStudy(studyId);
                } else {
                    studyService.exportStudyToFormat(studyId, exportFormat);
                }
                exportLogger.info("Complete export for study " + study.getGlobalId());
            }
            exportLogger.info("Completed exporting studies.");
        } catch (EJBException e) {
            logException(e, exportLogger);
            throw e;

        }

    }

    public void exportStudies(List<Long> studyIds) {
        exportStudies(studyIds, null);
    }

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

    private Study doImportStudy(File xmlFile, Long harvestFormatTypeId, Long vdcId, Long userId, String harvestIdentifier, List<StudyFileEditBean> filesToUpload) {
        logger.info("Begin doImportStudy");

        Study study = null;
        Map<String, String> globalIdComponents = null; // used if this is an update of a harvested study
        boolean isHarvest = (harvestIdentifier != null);

        VDC vdc = em.find(VDC.class, vdcId);
        VDCUser creator = em.find(VDCUser.class, userId);


        // Step 1: determine format and transform if necessary
        File ddiFile = xmlFile;
        boolean fileTransformed = false;

        HarvestFormatType hft = em.find(HarvestFormatType.class, harvestFormatTypeId);
        if (hft.getStylesheetFileName() != null) {
            ddiFile = transformToDDI(xmlFile, hft.getStylesheetFileName());
            fileTransformed = true;
        }


        // Step 2a: if harvested, check if exists
        if (isHarvest) {
            study = getStudyByHarvestInfo(vdc, harvestIdentifier);
            if (study != null) {
                if (!study.isIsHarvested()) {
                    // This study actually belongs to the local DVN, so don't continue with harvest
                    // TODO: this check is probably no longer needed, now that we get study by harvestIdentifier
                    throw new EJBException("This study originated in the local DVN - we don't need to harvest it.");
                }

                // store old global ID components
                globalIdComponents = new HashMap<String, String>();
                globalIdComponents.put("globalId", study.getGlobalId());
                globalIdComponents.put("protocol", study.getProtocol());
                globalIdComponents.put("authority", study.getAuthority());
                globalIdComponents.put("studyId", study.getStudyId());

                clearStudy(study);
            }
        }

        // Step 2b: initialize new Study
        if (study == null) {
            ReviewState reviewState = reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_RELEASED);
            study = new Study(vdc, creator, reviewState);
            em.persist(study);
        }


        // Step 3: map the ddi
        ddiService.mapDDI(ddiFile, study);


        //Step 4: post mapping processing
        if (isHarvest) {
            study.setIsHarvested(true);
            study.setHarvestIdentifier(harvestIdentifier);
        } else {
            // clear fields related to harvesting
            study.setHarvestHoldings(null);
            study.setHarvestDVTermsOfUse(null);
            study.setHarvestDVNTermsOfUse(null);
        }
        setDisplayOrders(study);
        boolean registerHandle = determineId(study, vdc, globalIdComponents);


        // step 5: upload files
        if (filesToUpload != null) {
            addFiles(study, filesToUpload, creator);
        }


        // step 6: store the original study files
        copyXMLFile(study, ddiFile, "original_imported_study.xml");

        if (fileTransformed) {
            copyXMLFile(study, xmlFile, "original_imported_study_pretransform.xml");
        }


        // step 7: register if necessary
        if (registerHandle && vdcNetworkService.find().isHandleRegistration()) {
            String handle = study.getAuthority() + "/" + study.getStudyId();
            gnrsService.createHandle(handle);
        }

        logger.info("completed doImportStudyStax() returning study" + study.getGlobalId());
        return study;
    }

    private boolean determineId(Study study, VDC vdc, Map<String, String> globalIdComponents) {
        VDCNetwork vdcNetwork = vdcNetworkService.find();
        String protocol = vdcNetwork.getProtocol();
        String authority = vdcNetwork.getAuthority();
        String globalId = null;

        if (!StringUtil.isEmpty(study.getStudyId())) {
            globalId = study.getGlobalId();
        }


        if (vdc.getHarvestingDataverse() != null) {
            if (vdc.getHarvestingDataverse().getHandlePrefix() != null) { // FOR THIS HARVESTED DATAVERSE, WE TAKE CARE OF HANDLE GENERATION
                if (globalId != null) {
                    throw new EJBException("DDI should not specify a handle, but does.");
                }

                if (globalIdComponents != null) {
                    study.setProtocol(globalIdComponents.get("protocol"));
                    study.setAuthority(globalIdComponents.get("authority"));
                    study.setStudyId(globalIdComponents.get("studyId"));
                } else {
                    boolean generateRandom = vdc.getHarvestingDataverse().isGenerateRandomIds();
                    authority = vdc.getHarvestingDataverse().getHandlePrefix().getPrefix();
                    generateHandle(study, protocol, authority, generateRandom);
                    return true;
                }

            } else {
                if (globalId == null) { // FOR THIS HARVESTED DATAVERSE, THE DDI SHOULD SPECIFY THE HANDLE
                    throw new EJBException("DDI should specify a handle, but does not.");
                } else if (globalIdComponents != null && !globalId.equals(globalIdComponents.get("globalId"))) {
                    throw new EJBException("DDI specifies a handle that is different from current handle for this study.");
                }
            }

        } else { // imported study
            if (globalId == null) {
                generateHandle(study, protocol, authority, true);
                return true;
            }
        }

        return false;
    }

    private void generateHandle(Study study, String protocol, String authority, boolean generateRandom) {
        String studyId = null;

        if (generateRandom) {
            do {
                studyId = RandomStringUtils.randomAlphanumeric(5);
            } while (!isUniqueStudyId(studyId, protocol, authority));


        } else {
            if (study.getStudyOtherIds().size() > 0) {
                studyId = study.getStudyOtherIds().get(0).getOtherId();
                if (!isValidStudyIdString(studyId)) {
                    throw new EJBException("The Other ID (from DDI) was invalid.");
                }
            } else {
                throw new EJBException("No Other ID (from DDI) was available for generating a handle.");
            }
        }

        study.setProtocol(protocol);
        study.setAuthority(authority);
        study.setStudyId(studyId);

    }

    public boolean isValidStudyIdString(String str) {
        final char[] chars = str.toCharArray();
        for (int x = 0; x < chars.length; x++) {
            final char c = chars[x];
            if (StringUtil.isAlphaNumericChar(c) || c == '-' || c == '_' || c == '.') {
                continue;
            }
            return false;
        }
        return true;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<StudyFile> getStudyFilesByExtension(String extension) {

        String queryStr = "SELECT sf from StudyFile sf where lower(sf.fileName) LIKE :extension";

        Query query = em.createQuery(queryStr);
        query.setParameter("extension", "%." + extension.toLowerCase());
        return query.getResultList();

    }

    public void updateStudyFile(StudyFile detachedStudyFile) {
        em.merge(detachedStudyFile);
    }

  
}
