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
package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.admin.RoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCRole;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.ddi.DDIServiceLocal;
import edu.harvard.iq.dvn.core.gnrs.GNRSServiceLocal;
import edu.harvard.iq.dvn.core.harvest.HarvestFormatType;
import edu.harvard.iq.dvn.core.harvest.HarvestStudyServiceLocal;
import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion.VersionState;
import edu.harvard.iq.dvn.core.util.FileUtil;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCActivity;
import edu.harvard.iq.dvn.core.vdc.VDCCollection;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
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
public class StudyServiceBean implements edu.harvard.iq.dvn.core.study.StudyServiceLocal, java.io.Serializable {

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
    VariableServiceLocal variableService;

    @EJB
    StudyExporterFactoryLocal studyExporterFactory;
    @EJB
    MailServiceLocal mailService;
    private static final Logger logger = Logger.getLogger("edu.harvard.iq.dvn.core.study.StudyServiceBean");
    private static final SimpleDateFormat exportLogFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
    @EJB
    StudyServiceLocal studyService; // used to force new transaction during import
    @EJB
    HarvestStudyServiceLocal harvestStudyService;
    @EJB
    VDCServiceLocal vdcService;
    @EJB
    StudyFileServiceLocal studyFileService;
    @EJB
    RoleServiceLocal roleService;

    /**
     * Creates a new instance of StudyServiceBean
     */
    public StudyServiceBean() {
    }


    public void updateStudy(Study detachedStudy) {
        em.merge(detachedStudy);
    }

    public void updateStudyVersion(StudyVersion studyVersion) {
        em.merge(studyVersion);
    }

    public void setReadyForReview(Long studyId) {
        setReadyForReview(studyId, null);
    }

    public void setReadyForReview(Long studyId, String versionNote) {

        Study study = em.find(Study.class, studyId);
        StudyVersion sv = study.getLatestVersion();

        setReadyForReview(sv, versionNote);
    }

    public void setReadyForReview(StudyVersion sv) {
        setReadyForReview(sv, null);
    }

    public void setReadyForReview(StudyVersion sv, String versionNote) {

        sv.setVersionState(StudyVersion.VersionState.IN_REVIEW);
        if (versionNote != null) {
            sv.setVersionNote(versionNote);
        }

        Study study = sv.getStudy();

        VDCUser user = study.getCreator();
        // If the user adding the study is a Contributor, send notification to all Curators in this VDC
        // and send an email to the Contributor about the status of the study

        if (user.getVDCRole(study.getOwner()) != null && user.getVDCRole(study.getOwner()).getRole().getName().equals(RoleServiceLocal.CONTRIBUTOR)) {
            mailService.sendStudyInReviewNotification(user.getEmail(), sv.getMetadata().getTitle());

            // Notify all curators and admins that study is in review
            for (Iterator it = study.getOwner().getVdcRoles().iterator(); it.hasNext();) {
                VDCRole elem = (VDCRole) it.next();
                if (elem.getRole().getName().equals(RoleServiceLocal.CURATOR) || elem.getRole().getName().equals(RoleServiceLocal.ADMIN)) {
                    mailService.sendStudyAddedCuratorNotification(elem.getVdcUser().getEmail(), user.getUserName(), sv.getMetadata().getTitle(), study.getOwner().getName());
                }
            }

        }
    }

    public void saveVersionNote(Long studyId, Long versionNumber, String newVersionNote) {
        Study study = em.find(Study.class, studyId);
        StudyVersion sv = null;

        if ( study != null ) {
            if (versionNumber != null) {
                sv = study.getStudyVersionByNumber(versionNumber);
            } else {
                sv = study.getLatestVersion();
            }

            if ( sv != null ) {
                sv.setVersionNote(newVersionNote);
            }
        }
    }

    public void saveVersionNote(Long studyVersionId, String newVersionNote) {
        StudyVersion sv = em.find(StudyVersion.class, studyVersionId);

        if ( sv != null ) {
            sv.setVersionNote(newVersionNote);
        }
    }

    public void setReleased(Long studyId) {
        setReleased(studyId, null);
    }

    public void setReleased(Long studyId, String versionNote) {

        Study study = em.find(Study.class, studyId);
        StudyVersion latestVersion = study.getLatestVersion();
        if (!latestVersion.isWorkingCopy()) {
            throw new EJBException("Cannot release latestVersion, incorrect state: "+latestVersion.getVersionState());
        }

        Date releaseDate = new Date();

        // Archive the previously released or deaccessioned version
        StudyVersion releasedVersion = study.getReleasedVersion();
        if (releasedVersion!=null) {
            releasedVersion.setVersionState(StudyVersion.VersionState.ARCHIVED);
            releasedVersion.setArchiveTime(releaseDate);
            releasedVersion.setArchiveNote("Replaced by version " + latestVersion.getVersionNumber());
        } else {
            StudyVersion deaccessionedVersion = study.getDeaccessionedVersion();
            if (deaccessionedVersion!=null) {
                deaccessionedVersion.setVersionState(StudyVersion.VersionState.ARCHIVED);
            }
        }

        latestVersion.setVersionState(StudyVersion.VersionState.RELEASED);
        latestVersion.setReleaseTime(releaseDate);

        if (versionNote != null) {
            latestVersion.setVersionNote(versionNote);
        }

        VDCRole studyCreatorRole = study.getCreator().getVDCRole(study.getOwner());

        if (studyCreatorRole != null && studyCreatorRole.getRole().getName().equals(RoleServiceLocal.CONTRIBUTOR)) {
            mailService.sendStudyReleasedNotification(study.getCreator().getEmail(), latestVersion.getMetadata().getTitle(), study.getOwner().getName());
        }

        // finally, re-index the study metadata:
        indexService.updateStudy(studyId);
    }


    public void deaccessionStudy(StudyVersion sv) {
        sv.setVersionState(StudyVersion.VersionState.DEACCESSIONED);
        sv.setArchiveTime(new Date());
        em.merge(sv);

        indexService.deleteStudy(sv.getStudy().getId());
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
            studyService.deleteStudyInNewTransaction(studyId, false);
        }

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteStudyInNewTransaction(Long studyId, boolean deleteFromIndex) {
        deleteStudy(studyId, deleteFromIndex);
    }


    public void deleteStudy(Long studyId, boolean deleteFromIndex) {
        long start = new Date().getTime();
        //System.out.println("DEBUG: 0\t - deleteStudy - BEGIN");
        Study study = em.find(Study.class, studyId);
        if (study == null) {
            return;
        }
        //System.out.println("DEBUG: " + (new Date().getTime() - start) + "\t - deleteStudy - remove from collections");
        for (Iterator<VDCCollection> it = study.getStudyColls().iterator(); it.hasNext();) {
            VDCCollection elem = it.next();
            elem.getStudies().remove(study);

        }
        //System.out.println("DEBUG: " + (new Date().getTime() - start) + "\t - deleteStudy - delete data variables");
        studyService.deleteDataVariables(study.getId());

        //System.out.println("DEBUG: " + (new Date().getTime() - start) + "\t - deleteStudy - delete relationships");
        study.getAllowedGroups().clear();
        study.getAllowedUsers().clear();

        for (Iterator<StudyFile> it = study.getStudyFiles().iterator(); it.hasNext();) {
            StudyFile elem = it.next();
            elem.getAllowedGroups().clear();
            elem.getAllowedUsers().clear();
            elem.clearData();
        }

        //System.out.println("DEBUG: " + (new Date().getTime() - start) + "\t - deleteStudy - delete physical files");
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
        //System.out.println("DEBUG: " + (new Date().getTime() - start) + "\t - deleteStudy - delete from HarvestStudy");
        harvestStudyService.markHarvestStudiesAsRemoved( harvestStudyService.findHarvestStudiesByGlobalId( study.getGlobalId() ), new Date() );

        //System.out.println("DEBUG: " + (new Date().getTime() - start) + "\t - deleteStudy - delete from DB and gnrs");
        em.remove(study);
        gnrsService.delete(study.getAuthority(), study.getStudyId());

        //System.out.println("DEBUG: " + (new Date().getTime() - start) + "\t - deleteStudy - delete from Index");
        if (deleteFromIndex) {
            indexService.deleteStudy(studyId);
        }

        //System.out.println("DEBUG: " + (new Date().getTime() - start) + "\t - deleteStudy - right before flush");
        em.flush();  // Force study deletion to the database, for cases when we are calling this before deleting the owning Dataverse
        //System.out.println("DEBUG: " + (new Date().getTime() - start) + "\t - deleteStudy - FINISH");
        logger.log(Level.INFO, "Successfully deleted Study " + studyId + "!");


    }

    public void destroyWorkingCopyVersion(Long studyVersionId) {
        long start = new Date().getTime();

        StudyVersion studyVersion = em.find(StudyVersion.class, studyVersionId);
        if (studyVersion == null) {
            return;
        }

        // We need to determine if there are already released versions of this
        // study. If so, we can destroy this version and leave the older versions
        // and the study intact. However, if this working version is the first
        // version of the study, we have to destroy the study as well -- otherwise
        // we'll have that version-less study shell left behind, which can mess up
        // ManageStudies page and possibly other partds of the system.

        Long studyId = null;

        if (studyVersion.getStudy() != null) {
            studyId = studyVersion.getStudy().getId();
        }

        if (studyId != null) {
            if (getStudyVersion(studyId, null) == null) {
                deleteStudy(studyId, true);
            } else {
                // We need to make sure to delete all the Study Files that would
                // otherwise become "orphans" -- meaning, the files that were
                // only added in this working version, that are not part of
                // the released version of the study.
                //
                // I'm going to simply retrieve the lists of file metadata
                // for both versions, ordered by file Id, and delete the "tail"
                // of the working version list.

                StudyVersion releasedVersion = getStudyVersion(studyId, null);

                List<FileMetadata> fileMetadataList = new ArrayList<FileMetadata>();
                List<FileMetadata> fileMetadataListReleased = new ArrayList<FileMetadata>();

                fileMetadataList = studyFileService.getFilesByStudyVersionOrderedById(studyVersion.getId());
                fileMetadataListReleased = studyFileService.getFilesByStudyVersionOrderedById(releasedVersion.getId());

                int i = fileMetadataList.size();

                if ( i > 0 ) {
                    FileMetadata fm1 = fileMetadataList.get(i-1);

                    Long lastReleasedStudyFileId = new Long (0);

                    if (fileMetadataListReleased.size() > 0) {
                        lastReleasedStudyFileId = fileMetadataListReleased.get(fileMetadataListReleased.size()-1).getStudyFile().getId();
                    }

                    while ( i > 0 &&
                            fm1 != null &&
                            fm1.getStudyFile().getId().compareTo(lastReleasedStudyFileId) > 0 ) {

                        StudyFile studyFile = fm1.getStudyFile();

                        String fileSystemLocation = studyFile.getFileSystemLocation();
                        if ( fileSystemLocation != null ) {
                            File fileToDelete = new File (fileSystemLocation);

                            // For subsettable files, also remove the preserved
                            // originals and cached format conversions:
                            if (studyFile.isSubsettable()) {
                                if (fileToDelete != null) {
                                    // original:
                                    File originalDataFile = new File(fileToDelete.getParent(), "_" + studyFile.getFileSystemName());
                                    if (originalDataFile != null) {
                                        originalDataFile.delete();
                                    }
                                    // cached alternative formats (including .RData file
                                    // that we create for Network Data files):
                                    File fileDir = new File (fileToDelete.getParent());
                                    File[] formatConvertedFiles =
                                            fileDir.listFiles(
                                                new ConvertedFilenamesFilter(
                                                    studyFile.getFileSystemName()));
                                    for (int j = 0; j < formatConvertedFiles.length; j++) {
                                        formatConvertedFiles[j].delete();
                                    }
                                }
                            }
                            // End of subsettable file special case;

                            // For image files, we may have thumbnail files
                            // cached on disk as well:
                            if (studyFile.getFileType().substring(0, 6).equalsIgnoreCase("image/")) {
                                File imageThumbnailFile = new File(fileSystemLocation + ".thumb");
                                if (imageThumbnailFile != null) {
                                    imageThumbnailFile.delete();
                                }
                            }
                            // End of image file special case.

                            // And now delete the file itself:
                            if (fileToDelete != null) {
                                fileToDelete.delete();
                            }
                        }

                        i--;
                        if (i > 0) {
                            fm1 = fileMetadataList.get(i-1);
                        }
                    }
                }

                em.remove(studyVersion);
                em.flush();  // Force deletion to the database, for cases when we are calling this before deleting the owning Dataverse

                logger.log(Level.INFO, "Successfully deleted StudyVersion " + studyVersionId + "!");
            }
        }
    }

    /* these delete queires seem to take too long, so we are currently trying testing something different for deleting variables
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
    */

    private static final String DELETE_VARIABLE_CATEGORIES_PREFIX = "delete from variablecategory where datavariable_id in ";
    private static final String DELETE_SUMMARY_STATISTICS_PREFIX = " delete from summarystatistic where datavariable_id in ";
    private static final String DELETE_VARIABLE_RANGE_ITEMS_PREFIX = " delete from variablerangeitem where datavariable_id in ";
    private static final String DELETE_VARIABLE_RANGES_PREFIX = "delete from variablerange where datavariable_id in ";
    private static final String DELETE_DATA_VARIABLES_PREFIX = "delete from datavariable where id in ";
    private static final String DELETE_DATA_VARIABLE_MAPPINGS_PREFIX = "delete from datavariablemapping where datavariable_id in ";
    private static final String SELECT_DATATABLE_IDS = "select dt.id from study s, studyfile sf, datatable dt " +
            "where s.id= ? " +
            "and s.id = sf.study_id " +
            "and sf.id=dt.studyfile_id ";

    private static final String SELECT_DATAVARIABLE_IDS_PREFIX = "select dv.id from datavariable dv where dv.datatable_id in ";

    public void deleteDataVariables(Long studyId) {
        // because the delte was taking a while, we tested spearate queires to get the info and it seemed to work much
        // faster, so now this delete goes in steps

        // step 1: determine dtIds
        List dtIdList = new ArrayList();
        Query query = em.createNativeQuery(SELECT_DATATABLE_IDS).setParameter(1, studyId);
        for (Object currentResult : query.getResultList()) {
            // since query is native, must parse through Vector results and convert to Long
            dtIdList.add(new Long(((Integer) ((Vector) currentResult).get(0))).longValue());
        }

        if ( !dtIdList.isEmpty() ) {
            // step 2: determine variables
            List varList = new ArrayList();
            query = em.createNativeQuery(SELECT_DATAVARIABLE_IDS_PREFIX + "(" + generateTempTableString(dtIdList) + ")");
            for (Object currentResult : query.getResultList()) {
                varList.add(new Long(((Integer) ((Vector) currentResult).get(0))).longValue());
            }

            if ( !varList.isEmpty() ) {
                // step 3: delete!
                String varString = "(" + generateTempTableString(varList) + ")";

                em.createNativeQuery(DELETE_VARIABLE_CATEGORIES_PREFIX + varString).executeUpdate();
                em.createNativeQuery(DELETE_SUMMARY_STATISTICS_PREFIX + varString).executeUpdate();
                em.createNativeQuery(DELETE_VARIABLE_RANGE_ITEMS_PREFIX + varString).executeUpdate();
                em.createNativeQuery(DELETE_VARIABLE_RANGES_PREFIX + varString).executeUpdate();
                em.createNativeQuery(DELETE_DATA_VARIABLE_MAPPINGS_PREFIX + varString).executeUpdate();
                em.createNativeQuery(DELETE_DATA_VARIABLES_PREFIX + varString).executeUpdate();

            }
        }

        //em.createNativeQuery(DELETE_VARIABLE_CATEGORIES).setParameter(1, studyId).executeUpdate();
        //em.createNativeQuery(DELETE_SUMMARY_STATISTICS).setParameter(1, studyId).executeUpdate();
        //em.createNativeQuery(DELETE_VARIABLE_RANGE_ITEMS).setParameter(1, studyId).executeUpdate();
        //em.createNativeQuery(DELETE_VARIABLE_RANGES).setParameter(1, studyId).executeUpdate();
        //em.createNativeQuery(DELETE_DATA_VARIABLES).setParameter(1, studyId).executeUpdate();
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
     *   Should only be sued for studies with Released versions
     *   used by studyListingPage
     *
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Study getStudyForSearch(Long studyId, Map studyFields) {

        Study study = em.find(Study.class, studyId);
        if (study == null) {
            throw new IllegalArgumentException("Unknown studyId: " + studyId);
        }

        Metadata metadata = null;
        if (study.getReleasedVersion() != null) {
            metadata = study.getReleasedVersion().getMetadata();
        } else {
            throw new IllegalArgumentException("No released version available for this study: " + studyId);
        }

        if (studyFields != null) {
            for (Object studyField : studyFields.keySet()) {

                String fieldName = (String) studyField;
                if ("authorName".equals(fieldName)) {
                    for (Iterator<StudyAuthor> it = metadata.getStudyAuthors().iterator(); it.hasNext();) {
                        StudyAuthor elem = it.next();
                        elem.getId();
                    }
                } else if ("abstractText".equals(fieldName)) {
                    for (Iterator<StudyAbstract> it = metadata.getStudyAbstracts().iterator(); it.hasNext();) {
                        StudyAbstract elem = it.next();
                        elem.getId();
                    }
                } else if ("producerName".equals(fieldName)) {
                    for (Iterator<StudyProducer> it = metadata.getStudyProducers().iterator(); it.hasNext();) {
                        StudyProducer elem = it.next();
                        elem.getId();
                    }
                } else if ("distributorName".equals(fieldName)) {
                    for (Iterator<StudyDistributor> it = metadata.getStudyDistributors().iterator(); it.hasNext();) {
                        StudyDistributor elem = it.next();
                        elem.getId();
                    }
                } else if ("relatedStudies".equals(fieldName)) {
                    for (Iterator<StudyRelStudy> it = metadata.getStudyRelStudies().iterator(); it.hasNext();) {
                        StudyRelStudy elem = it.next();
                        elem.getId();
                    }
                } else if ("relatedMaterial".equals(fieldName)) {
                    for (Iterator<StudyRelMaterial> it = metadata.getStudyRelMaterials().iterator(); it.hasNext();) {
                        StudyRelMaterial elem = it.next();
                        elem.getId();
                    }
                } else if ("relatedPublications".equals(fieldName)) {
                    for (Iterator<StudyRelPublication> it = metadata.getStudyRelPublications().iterator(); it.hasNext();) {
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
        String queryStr = "select s.id from study s, studyversion sv where s.id = sv.study_id and sv.versionstate = '"+ StudyVersion.VersionState.RELEASED +
                "' and s.isharvested='false' and (sv.releasetime > s.lastexporttime or s.lastexporttime is null)";
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

    /**
     *  this method only returns released studies
     * @param studyIdList
     * @param orderBy
     * @return list of oredered ids
     */
    public List getOrderedStudies(List studyIdList, String orderBy) {
        if (orderBy == null || studyIdList == null || studyIdList.size() == 0) {
            return studyIdList;
        }

        String studyIds = generateTempTableString(studyIdList);
        String queryStr = null;

        if (orderBy.equals("globalId")) {
            queryStr = "SELECT s.id " +
                    "from study s " +
                    "where s.id in (" + studyIds + ") " +
                    "ORDER BY s.protocol, s.authority, s.studyId";

        } else if (orderBy.equals("title")) {
            queryStr = "SELECT s.id " +
                    "from metadata m, studyversion sv, study s " +
                    "where sv.metadata_id = m.id " +
                    "and s.id = sv.study_id " +
                    "and sv.versionstate = '" + StudyVersion.VersionState.RELEASED + "'" +
                    "and s.id in (" + studyIds + ") " +
                    "ORDER BY m.title";

        } else if (orderBy.equals("releaseTime")) {
            queryStr = "SELECT s.id " +
                    "from studyversion sv, study s " +
                    "where s.id = sv.study_id " +
                    "and sv.versionstate = '" + StudyVersion.VersionState.RELEASED + "'" +
                    "and s.id in (" + studyIds + ") " +
                    "ORDER BY sv.releasetime desc";

        } else if (orderBy.equals("productionDate")) {
            queryStr = "SELECT s.id " +
                    "from metadata m, studyversion sv, study s " +
                    "where sv.metadata_id = m.id " +
                    "and s.id = sv.study_id " +
                    "and sv.versionstate = '" + StudyVersion.VersionState.RELEASED + "'" +
                    "and s.id in (" + studyIds + ") " +
                    "ORDER BY m.productionDate desc";
        } else if (orderBy.equals("downloadCount")) {
            // this query runs fine in Postgres, but will need to be tested with other DBs if they are used
            queryStr = "select s.id " +
                    "from metadata m, studyversion sv, study s " +
                    "LEFT OUTER JOIN studyfileactivity sfa on  s.id = sfa.study_id " +
                    "where sv.metadata_id = m.id " +
                    "and s.id = sv.study_id " +
                    "and sv.versionstate = '" + StudyVersion.VersionState.RELEASED + "'" +
                    "and s.id in (" + studyIds + ")" +
                    "group by s.id, m.title " +
                    "order by " +
                    "(CASE WHEN sum(downloadcount) is null THEN -1 ELSE sum(downloadcount) END) desc, m.title" ;
        }

        if (queryStr != null) {
            Query query = em.createNativeQuery(queryStr);
            List<Long> returnList = new ArrayList<Long>();
            // since query is native, must parse through Vector results
            for (Object currentResult : query.getResultList()) {
                // convert results into Longs
                returnList.add(new Long(((Integer)currentResult).longValue()));
            }
            return returnList;

        } else {
            return studyIdList; // invalid order by
        }
    }

    public List getDvOrderedStudyVersionIds(Long vdcId, String orderBy, boolean ascending) {
        List<Long> returnList = new ArrayList<Long>();
        String queryStr = "SELECT v.id" +
                " from studyversion v, study s, metadata m, vdcuser cr" +
                " WHERE v.study_id = s.id" +
                " and v.metadata_id = m.id" +
                " and s.creator_id = cr.id" +
                " and s.owner_id = " + vdcId +
                " and v.id in (SELECT max(v.id) from studyversion v group by v.study_id)" +
                " ORDER BY " + orderBy;

        if (!ascending) {
            queryStr += " desc";
        }
        Query query = em.createNativeQuery(queryStr);
        // since query is native, must parse through Vector results
        for (Object currentResult : query.getResultList()) {
            // convert results into Longs
            returnList.add(new Long(((Integer)currentResult)).longValue());
        }
        return returnList;
    }

    public List getDvOrderedDeaccessionedStudyVersionIds(Long vdcId, String orderBy, boolean ascending) {
        List<Long> returnList = new ArrayList<Long>();
        String queryStr = "SELECT v.id" +
                " from studyversion v, study s, metadata m, vdcuser cr" +
                " WHERE v.study_id = s.id" +
                " and v.metadata_id = m.id" +
                " and s.creator_id = cr.id" +
                " and s.owner_id = " + vdcId +
                " and v.versionstate = '" + StudyVersion.VersionState.DEACCESSIONED + "'"  +
                " and v.id in (SELECT max(v.id) from studyversion v group by v.study_id)" +
                " ORDER BY " + orderBy;

        if (!ascending) {
            queryStr += " desc";
        }
        Query query = em.createNativeQuery(queryStr);
        // since query is native, must parse through Vector results
        for (Object currentResult : query.getResultList()) {
            // convert results into Longs
            returnList.add(new Long(((Integer)currentResult)).longValue());
        }
        return returnList;
    }



    public List getAllDeaccessionedStudyVersionIdsByContributor(Long contributorId, String orderBy, boolean ascending) {
        List<Long> returnList = new ArrayList<Long>();
        String queryStr = "SELECT v.id" +
                " from studyversion v, study s, metadata m, vdcuser cr " +
                " WHERE v.study_id = s.id" +
                " and v.metadata_id = m.id" +
                " and s.creator_id = cr.id" +
                " and v.versionstate = '" + StudyVersion.VersionState.DEACCESSIONED + "'"  +
                " and v.id in (SELECT max(v.id) from studyversion v, versioncontributor c " +
                " where c.studyversion_id = v.id and c.contributor_id = " + contributorId +
                " group by v.study_id)" +
                " ORDER BY " + orderBy;

        if (!ascending) {
            queryStr += " desc";
        }
        Query query = em.createNativeQuery(queryStr);
        // since query is native, must parse through Vector results
        for (Object currentResult : query.getResultList()) {
            // convert results into Longs
            returnList.add(new Long(((Integer)currentResult)).longValue());
        }
        return returnList;
    }

        public List getAllStudyVersionIdsByContributor(Long contributorId, String orderBy, boolean ascending) {
        List<Long> returnList = new ArrayList<Long>();
        String queryStr = "SELECT v.id" +
                " from studyversion v, study s, metadata m, vdcuser cr " +
                " WHERE v.study_id = s.id" +
                " and v.metadata_id = m.id" +
                " and s.creator_id = cr.id" +
                " and v.id in (SELECT max(v.id) from studyversion v, versioncontributor c " +
                " where c.studyversion_id = v.id and c.contributor_id = " + contributorId +
                " group by v.study_id)" +
                " ORDER BY " + orderBy;

        if (!ascending) {
            queryStr += " desc";
        }
        Query query = em.createNativeQuery(queryStr);
        // since query is native, must parse through Vector results
        for (Object currentResult : query.getResultList()) {
            // convert results into Longs
            returnList.add(new Long(((Integer)currentResult)).longValue());
        }
        return returnList;
    }

    public List getDvOrderedStudyVersionIdsByContributor(Long vdcId, Long contributorId, String orderBy, boolean ascending) {
        List<Long> returnList = new ArrayList<Long>();
        String queryStr = "SELECT v.id" +
                " from studyversion v, study s, metadata m, vdcuser cr " +
                " WHERE v.study_id=s.id" +
                " and v.metadata_id = m.id" +
                " and s.creator_id = cr.id" +
                " and s.owner_id = " + vdcId +
                " and v.id in (SELECT max(v.id) from studyversion v, versioncontributor c " +
                " where c.studyversion_id = v.id and c.contributor_id = " + contributorId +
                " group by v.study_id)" +
                " ORDER BY " + orderBy;

        if (!ascending) {
            queryStr += " desc";
        }
        Query query = em.createNativeQuery(queryStr);
        // since query is native, must parse through Vector results
        for (Object currentResult : query.getResultList()) {
            // convert results into Longs
            returnList.add(new Long(((Integer)currentResult)).longValue());
        }

        return returnList;
    }

    public List getDvOrderedDeaccessionedStudyVersionIdsByContributor(Long vdcId, Long contributorId, String orderBy, boolean ascending) {
        List<Long> returnList = new ArrayList<Long>();
        String queryStr = "SELECT v.id" +
                " from studyversion v, study s, metadata m, vdcuser cr " +
                " WHERE v.study_id=s.id" +
                " and v.metadata_id = m.id" +
                " and s.creator_id = cr.id" +
                " and s.owner_id = " + vdcId +
                " and v.versionstate = '" + StudyVersion.VersionState.DEACCESSIONED + "'"  +
                " and v.id in (SELECT max(v.id) from studyversion v, versioncontributor c " +
                " where c.studyversion_id = v.id and c.contributor_id = " + contributorId +
                " group by v.study_id)" +
                " ORDER BY " + orderBy;

        if (!ascending) {
            queryStr += " desc";
        }
        Query query = em.createNativeQuery(queryStr);
        // since query is native, must parse through Vector results
        for (Object currentResult : query.getResultList()) {
            // convert results into Longs
            returnList.add(new Long(((Integer)currentResult)).longValue());
        }

        return returnList;
    }

       public List getDvOrderedStudyIds(Long vdcId, String orderBy, boolean ascending ) {
          String query = "SELECT s.id FROM Study s WHERE s.owner.id = " + vdcId + " ORDER BY s."+orderBy;
          if (!ascending) {
              query+= " desc";
          }
            return (List) em.createQuery(query).getResultList();
        }
        public List getDvOrderedStudyIdsByCreator(Long vdcId, Long creatorId, String orderBy, boolean ascending ) {
          String query = "SELECT s.id FROM Study s WHERE s.owner.id = " + vdcId + " and s.creator.id = " +creatorId+" ORDER BY s."+orderBy;
          if (!ascending) {
              query+= " desc";
          }
            return (List) em.createQuery(query).getResultList();
        }







    public List<DataFileFormatType> getDataFileFormatTypes() {
        return em.createQuery("select object(t) from DataFileFormatType as t").getResultList();
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

        logger = Logger.getLogger("edu.harvard.iq.dvn.core.web.servlet.VDCExportServlet");

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

            /* THIS IS LEGACY CODE AND SHOULD BE DELETED
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
            */

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
            String query = "SELECT s from Study s where s.authority = '" + authority + "' ";
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
            //Vector result = (Vector) em.createNativeQuery("select nextval('filesystemname_seq')").getSingleResult();
            //fileSystemName = result.get(0).toString();
            Long result = (Long) em.createNativeQuery("select nextval('filesystemname_seq')").getSingleResult();
            fileSystemName = result.toString();
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

    public void incrementNumberOfDownloads(Long studyFileId, Long currentVDCId) {
        incrementNumberOfDownloads( studyFileId, currentVDCId, new Date() );
    }

    public void incrementNumberOfDownloads(Long studyFileId, Long currentVDCId, Date lastDownloadTime) {
        StudyFile sf = studyFileService.getStudyFile(studyFileId);
        Study study = sf.getStudy();
        StudyFileActivity sfActivity = sf.getStudyFileActivity();

        if (sfActivity == null) {
            sfActivity = new StudyFileActivity();
            sfActivity.setStudyFile(sf);
            sfActivity.setStudy(study);

            sf.setStudyFileActivity(sfActivity);
            study.getStudyFileActivity().add(sfActivity);
        }

        sfActivity.setDownloadCount(sfActivity.getDownloadCount() + 1);
        sfActivity.setLastDownloadTime( lastDownloadTime );

        if (currentVDCId == null) {
            VDCActivity vdcActivity = study.getOwner().getVDCActivity();
            vdcActivity.setLocalStudyNetworkDownloadCount( vdcActivity.getLocalStudyNetworkDownloadCount() + 1);

        } else if ( currentVDCId.equals(study.getOwner().getId()) ) {
            VDCActivity vdcActivity = study.getOwner().getVDCActivity();
            vdcActivity.setLocalStudyLocalDownloadCount( vdcActivity.getLocalStudyLocalDownloadCount() + 1);

        } else {
            VDCActivity vdcActivity1 = study.getOwner().getVDCActivity();
            vdcActivity1.setLocalStudyForeignDownloadCount( vdcActivity1.getLocalStudyForeignDownloadCount() + 1);

            VDCActivity vdcActivity2 = vdcService.findById(currentVDCId).getVDCActivity();
            vdcActivity2.setForeignStudyLocalDownloadCount( vdcActivity2.getForeignStudyLocalDownloadCount() + 1);
        }

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

    public StudyVersion getStudyVersion(String globalId, Long versionNumber) {
        Study study = getStudyByGlobalId(globalId);
        if (study != null) {
            return getStudyVersion( study.getId(), versionNumber );
        } else {
            return null;
        }
    }


    public StudyVersion getStudyVersion(Long studyId, Long versionNumber) {
        Study study = em.find(Study.class, studyId);
        StudyVersion studyVersion = null;

        if (study != null) {
            if (versionNumber == null) { // return either the released or deaccessioned version
                 studyVersion = study.getReleasedVersion();

                if (studyVersion == null) {
                    studyVersion = study.getDeaccessionedVersion();
                }

            } else if (versionNumber == -1) { // return the latest version
                studyVersion = study.getLatestVersion();
            } else {
                studyVersion = study.getStudyVersionByNumber(versionNumber);

                if (studyVersion==null) {
                    throw new IllegalArgumentException("No studyVersion found for study id "+studyId+"versionNumber "+versionNumber);
                }
            }

        } else {
            throw new IllegalArgumentException("No study found for study id "+studyId);
        }

        //String queryStr = "SELECT sv FROM StudyVersion sv WHERE sv.study.id = '" + studyId;
        //if (versionNumber != null) {
        //    queryStr += "' and sv.versionNumber = '" + versionNumber + "'";
        //} else {
            // get reelased version
        //    queryStr += "' and  sv.versionState = :releasedState ";
        //}

        //Query query = em.createQuery(queryStr);

        //if (versionNumber == null) {
        //    query.setParameter("releasedState", VersionState.RELEASED);
        //}

        //List resultList = query.getResultList();
        //StudyVersion studyVersion = null;
        //if (resultList.size() > 1) {
        //    throw new EJBException("More than one study version found with studyId = " + studyId + " and versionNumber = " + versionNumber);
        //}
        //if (resultList.size() == 1) {
        //    studyVersion = (StudyVersion) resultList.get(0);
        //}
        return studyVersion;
    }

    public StudyVersion getStudyVersionById(Long versionId){
        StudyVersion sv = em.find(StudyVersion.class, versionId);
        if (sv==null){
            throw new IllegalArgumentException("Unknown version id " + versionId);
        }
        return sv;
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


        logger.info("completed importHarvestStudy() returning study" + study.getGlobalId());
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

    private void clearCollection(Collection collection) {
        if (collection!=null) {
            for (Iterator it = collection.iterator(); it.hasNext();) {
                Object elem =  it.next();
                it.remove();
                em.remove(elem);
            }
        }
    }


    private void resetStudyForHarvesting(StudyVersion studyVersion) {
        // first delete variables via query (for performance)
        Study study = studyVersion.getStudy();
        deleteDataVariables(study.getId());

        for (StudyFile elem : study.getStudyFiles()) {
            elem.clearData();
        }

        // then delete files
        clearCollection(study.getStudyFiles());

        // now create new, empty metadata object and delete old one
        Metadata m = new Metadata();
        em.persist(m); // persist because otherwise update study can fail with non null metadata id exception
        em.remove(studyVersion.getMetadata());
        studyVersion.setMetadata(m);

        // clear global id componenents
        study.setProtocol(null);
        study.setAuthority(null);
        study.setStudyId(null);

        // finally, flush, so that everything is removed and new objects get properly created
        em.flush();
    }


    private void setDisplayOrders(Metadata metadata) {

        int i = 0;
        for (Iterator it = metadata.getStudyAuthors().iterator(); it.hasNext();) {
            StudyAuthor elem = (StudyAuthor) it.next();
            elem.setDisplayOrder(i);
            i++;

        }
        i = 0;
        for (Iterator it = metadata.getStudyAbstracts().iterator(); it.hasNext();) {
            StudyAbstract elem = (StudyAbstract) it.next();
            elem.setDisplayOrder(i);
            i++;

        }
        i = 0;
        for (Iterator it = metadata.getStudyDistributors().iterator(); it.hasNext();) {
            StudyDistributor elem = (StudyDistributor) it.next();
            elem.setDisplayOrder(i);
            i++;

        }
        i = 0;
        for (Iterator it = metadata.getStudyGeoBoundings().iterator(); it.hasNext();) {
            StudyGeoBounding elem = (StudyGeoBounding) it.next();
            elem.setDisplayOrder(i);
            i++;

        }
        i = 0;
        for (Iterator it = metadata.getStudyGrants().iterator(); it.hasNext();) {
            StudyGrant elem = (StudyGrant) it.next();
            elem.setDisplayOrder(i);
            i++;
        }

        i = 0;
        for (Iterator it = metadata.getStudyKeywords().iterator(); it.hasNext();) {
            StudyKeyword elem = (StudyKeyword) it.next();
            elem.setDisplayOrder(i);
            i++;
        }

        i = 0;
        for (Iterator it = metadata.getStudyNotes().iterator(); it.hasNext();) {
            StudyNote elem = (StudyNote) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        i = 0;
        for (Iterator it = metadata.getStudyOtherIds().iterator(); it.hasNext();) {
            StudyOtherId elem = (StudyOtherId) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        i = 0;
        for (Iterator it = metadata.getStudyOtherRefs().iterator(); it.hasNext();) {
            StudyOtherRef elem = (StudyOtherRef) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        i = 0;
        for (Iterator it = metadata.getStudyProducers().iterator(); it.hasNext();) {
            StudyProducer elem = (StudyProducer) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        i = 0;
        for (Iterator it = metadata.getStudyRelPublications().iterator(); it.hasNext();) {
            StudyRelPublication elem = (StudyRelPublication) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        i = 0;
           for (Iterator it = metadata.getStudyRelStudies().iterator(); it.hasNext();) {
            StudyRelStudy elem = (StudyRelStudy) it.next();
            elem.setDisplayOrder(i);
            i++;
        }

        i = 0;
           for (Iterator it = metadata.getStudyRelMaterials().iterator(); it.hasNext();) {
            StudyRelMaterial elem = (StudyRelMaterial) it.next();
            elem.setDisplayOrder(i);
            i++;
        }

        i = 0;
        for (Iterator it = metadata.getStudySoftware().iterator(); it.hasNext();) {
            StudySoftware elem = (StudySoftware) it.next();
            elem.setDisplayOrder(i);
            i++;
        }

        i = 0;
        for (Iterator it = metadata.getStudyTopicClasses().iterator(); it.hasNext();) {
            StudyTopicClass elem = (StudyTopicClass) it.next();
            elem.setDisplayOrder(i);
            i++;
        }

    }

    public Study saveStudyVersion(StudyVersion studyVersion, Long userId) {
        VDCUser user = em.find(VDCUser.class, userId);
        Date lastUpdateTime = new Date();
        studyVersion.setLastUpdateTime(lastUpdateTime);
        studyVersion.getStudy().setLastUpdateTime(lastUpdateTime);
        VDC studyVDC  = studyVersion.getStudy().getOwner();

        if ( !user.isNetworkAdmin()) {             
            userService.makeContributor(user.getId(), studyVDC.getId());

            VDCRole vdcRole = roleService.findByUserVDC(userId, studyVDC.getId());
            if ( vdcRole.getRole().getName().equals(RoleServiceLocal.CONTRIBUTOR) ) {
                studyVersion.setVersionState(VersionState.DRAFT);
            }

        }
        studyVersion.updateVersionContributors(user);
        setDisplayOrders(studyVersion.getMetadata());
        return studyVersion.getStudy();
    }

    // visible studies are defined as those that are released and not in a restricted vdc
    // (unless you are in vdc)
    public List getVisibleStudies(List studyIds, Long vdcId) {
        List returnList = new ArrayList();
        if (studyIds != null && studyIds.size() > 0) {
            generateTempTableString(studyIds);
            String queryString = "SELECT s.id " +
                    "FROM study s, studyversion sv, vdc v, tempid ts " +
                    "WHERE s.id = sv.study_id " +
                    "AND s.owner_id = v.id " +
                    "AND sv.versionState = '" + StudyVersion.VersionState.RELEASED + "' " +
                    "AND s.id = ts.tempid " +
                    "AND (v.restricted = false " +
                    (vdcId != null ? "OR v.id = " + vdcId : "") +
                    ") ORDER BY ts.orderby";

            Query query = em.createNativeQuery(queryString);
            // since query is native, must parse through Vector results
            for (Object currentResult : query.getResultList()) {
                // convert results into Longs
                returnList.add(new Long(((Integer)currentResult).longValue()));
            }
        }

        return returnList;
    }

    private String generateTempTableString(List<Long> studyIds) {
        // first step: create the temp table with the ids
        em.createNativeQuery("DROP TABLE IF EXISTS tempid").executeUpdate();
        em.createNativeQuery("CREATE TEMPORARY TABLE tempid (tempid integer primary key, orderby integer)").executeUpdate();
        em.createNativeQuery("INSERT INTO tempid VALUES " + generateIDsforTempInsert(studyIds)).executeUpdate();

        return "select tempid from tempid";
    }

    private String generateIDsforTempInsert(List idList) {
        int count = 0;
        StringBuffer sb = new StringBuffer();
        Iterator iter = idList.iterator();
        while (iter.hasNext()) {
            Long id = (Long) iter.next();
            sb.append("(").append(id).append(",").append(count++).append(")");
            if (iter.hasNext()) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    // this method will return only the subset of studies that are public
    public List getViewableStudies(List<Long> studyIds) {
        return getViewableStudies(studyIds, null, null, null);
    }

    // viewable studies are those that are defined as not restricted to the user
    public List getViewableStudies(List<Long> studyIds, Long userId, Long ipUserGroupId, Long vdcId) {
        List returnList = new ArrayList();

        if (studyIds != null && studyIds.size() > 0) {
            // dynamically generate the query
            StringBuffer queryString = new StringBuffer("select id from study s ");
            StringBuffer whereClause = new StringBuffer("where ( restricted = false ");
            boolean groupJoinAdded = false;

            if (vdcId != null) { // if this parameter is passed, the user is an admin or curator of this vdc
                whereClause.append("or owner_id = ? ");
            }

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

            if (vdcId != null) {
                query.setParameter(parameterCount++, vdcId);
            }

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
        // only export released studies
        // TODO: we should clean up the export logic to handle export of versions
        if (study.getReleasedVersion() == null) {
            return;
        }

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
        Logger exportLogger = Logger.getLogger("edu.harvard.iq.dvn.core.study.StudyServiceBean.export." + logTimestamp);
        List<Long> harvestedStudyIds = new ArrayList<Long>();
        try {

            exportLogger.addHandler(new FileHandler(FileUtil.getExportFileDir() + File.separator + "export_" + logTimestamp + ".log"));
        } catch (IOException e) {

            logger.severe("Exception adding log file handler " + FileUtil.getExportFileDir() + File.separator + "export_" + logTimestamp + ".log");
            return;
        }
        try {
            int exportCount = 0;
            exportLogger.info("Begin exporting studies, number of possible studies to export: " + studyIds.size());
            for (Long studyId : studyIds) {
                Study study = em.find(Study.class, studyId);
                if (study.getReleasedVersion() != null){
                    exportLogger.info("Begin export for study " + study.getGlobalId());
                    if (exportFormat == null) {
                    studyService.exportStudy(studyId); //TODO check why do we pass the id and not the study
                    } else {
                    studyService.exportStudyToFormat(studyId, exportFormat); //TODO check why do we pass the id and not the study
                    }
                    exportLogger.info("Complete export for study " + study.getGlobalId());
                    exportCount++;
                } else {
                    exportLogger.info("No released version for study " + study.getGlobalId() + "; skipping export.");
                }
            }
            exportLogger.info("Completed exporting studies. Actual number of studies exported: " + exportCount);
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
        StudyVersion studyVersion = null;

        boolean newStudy = true;

        VDC vdc = em.find(VDC.class, vdcId);


        // Note on the logic below:
        // It IS possible for the method to be called on a Harvested study, but without the
        // harvestIdentifier supplied! This happens when harvesting from Nesstar sources
        // (see more comments on Nesstar harvesting below).
        // (then we should probably check explicitly that this is indeed a Nesstar
        // harvesting dataverse).
        //  --L.A.
        boolean isHarvest = (harvestIdentifier != null || (vdc.getHarvestingDataverse() != null));
        Map<String, String> globalIdComponents = null; // used if this is an update of a harvested study


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
            if (harvestIdentifier == null) {
                // When harvesting from Nesstar sources, no unique identifiers
                // are provided on the protocol level. Instead, we have to
                // check the actual DDI metadata and see if it provides
                // an ID we could use as the harvestidentifier.
                //  -- L.A.
                Study tmpStudy = new Study(vdc, creator, StudyVersion.VersionState.RELEASED);
                StudyVersion tmpStudyVersion = tmpStudy.getLatestVersion();
                String tmpStudyId = null;

                // We are doing an extra run of ddiService.mapDDI in order
                // to achieve this; note that mapDDI is a fairly cheap
                // operation, since it doesn't parse the data portion of the
                // ddi.

                ddiService.mapDDI(ddiFile, tmpStudyVersion);

                if (tmpStudyVersion.getMetadata().getStudyOtherIds().size() > 0) {
                    tmpStudyId = tmpStudyVersion.getMetadata().getStudyOtherIds().get(0).getOtherId();
                }

                if (tmpStudyId == null || !isValidStudyIdString(tmpStudyId)) {
                    throw new EJBException("No suitable ID was found in the Nesstar-harvested metadata.");
                }

                harvestIdentifier = tmpStudyId;

            }

            study = getStudyByHarvestInfo(vdc, harvestIdentifier);

            if (study != null) {
//                if (!study.isIsHarvested()) {
//                    // This study actually belongs to the local DVN, so don't continue with harvest
//                    // TODO: this check is probably no longer needed, now that we get study by harvestIdentifier
//                    throw new EJBException("This study originated in the local DVN - we don't need to harvest it.");
//                }
                newStudy = false;

                // store old global ID components
                globalIdComponents = new HashMap<String, String>();
                globalIdComponents.put("globalId", study.getGlobalId());
                globalIdComponents.put("protocol", study.getProtocol());
                globalIdComponents.put("authority", study.getAuthority());
                globalIdComponents.put("studyId", study.getStudyId());

                studyVersion = study.getLatestVersion();
                resetStudyForHarvesting(studyVersion);
            }
        }

        // Step 2b: initialize new Study
        if (study == null) {
            VersionState newVersionState = isHarvest ? StudyVersion.VersionState.RELEASED : StudyVersion.VersionState.DRAFT;
            study = new Study(vdc, creator, newVersionState);

            studyVersion = study.getLatestVersion();

            // if not a harvest, set initial date of deposit (this may get overridden during map ddi step
            if (!isHarvest) {
                studyVersion.getMetadata().setDateOfDeposit(  new SimpleDateFormat("yyyy-MM-dd").format(new Date()) );
            }

        }

        em.persist(study);



        // Step 3: map the ddi
        Map dataFilesMap = ddiService.mapDDI(ddiFile, studyVersion);

        logger.info("doImportStudy: ddi mapped");


        //Step 4: post mapping processing
        if (isHarvest) {
            study.setIsHarvested(true);
            study.setHarvestIdentifier(harvestIdentifier);
        } else {
            // clear fields related to harvesting
            studyVersion.getMetadata().setHarvestHoldings(null);
            studyVersion.getMetadata().setHarvestDVTermsOfUse(null);
            studyVersion.getMetadata().setHarvestDVNTermsOfUse(null);
        }

        //em.flush();
	// This flush statement may in fact produce an error condition, 
	// since some of our StudyFiles may not have been persisted yet 
	// We are going to take care of that in the next step; but we 
	// shouldn't be trying to flush until we do. So I'm commenting it
	// out. -- L.A. 


        // step 5: persist files from ddi (since studyFile is not persisted when the new FileMetadata objects are created - since
        // the studyFile often already exists - we need to manually persist the study files here)

        for (FileMetadata fmd : studyVersion.getFileMetadatas()) {
           em.persist( fmd.getStudyFile() );
        }

        Map variablesMap = ddiService.reMapDDI(ddiFile, studyVersion, dataFilesMap);

        logger.info("doImportStudy: ddi re-mapped");

        logger.info("reading the variables map;");

        if (variablesMap != null) {
        
            for (Object mapKey : variablesMap.keySet()) {
                List<DataVariable> variablesMapEntry = (List<DataVariable>) variablesMap.get(mapKey);
                Long fileId = (Long)mapKey;
                if (variablesMapEntry != null) {
                    logger.info("found non-empty map entry for datatable id "+fileId);

                    DataVariable dv = variablesMapEntry.get(0);
                    DataTable tmpDt = dv.getDataTable();

                    if (tmpDt != null) {
                        tmpDt.setDataVariables(variablesMapEntry);
                        logger.info("added variables to datatable "+tmpDt.getId());
                    } else {
                        logger.info("first variable on the map for id "+tmpDt.getId()+" is referencing NULL datatable! WTF?");
                    }

                } else {
                    logger.info("found empty map entry for datatable id "+fileId);
             }
  
            }
        }
        

        saveStudyVersion(studyVersion, userId);
        if (isHarvest) {
            studyVersion.setReleaseTime( new Date() );
        }

        boolean registerHandle = determineId(studyVersion, vdc, globalIdComponents);
        if (newStudy && !studyService.isUniqueStudyId( study.getStudyId(), study.getProtocol(), study.getAuthority() ) ) {
            throw new EJBException("A study with this globalId already exists (likely cause: the study was previously harvested into a different dataverse).");
        }



        // step 5: upload files
        if (filesToUpload != null) {
            studyFileService.addFiles(studyVersion, filesToUpload, creator);
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

        logger.info("completed doImportStudy() returning study" + study.getGlobalId());
        return study;
    }


 
    private boolean determineId(StudyVersion sv, VDC vdc, Map<String, String> globalIdComponents) {
        Study study = sv.getStudy();

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
                    generateHandle(sv.getMetadata(), protocol, authority, generateRandom);
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
                generateHandle(sv.getMetadata(), protocol, authority, true);
                return true;
            }
        }

        return false;
    }

    private void generateHandle(Metadata metadata, String protocol, String authority, boolean generateRandom) {
        String studyId = null;

        if (generateRandom) {
            do {
                studyId = RandomStringUtils.randomAlphanumeric(5);
            } while (!isUniqueStudyId(studyId, protocol, authority));


        } else {
            if (metadata.getStudyOtherIds().size() > 0) {
                studyId = metadata.getStudyOtherIds().get(0).getOtherId();
                if (!isValidStudyIdString(studyId)) {
                    throw new EJBException("The Other ID (from DDI) was invalid.");
                }
            } else {
                throw new EJBException("No Other ID (from DDI) was available for generating a handle.");
            }
        }

        Study study = metadata.getStudyVersion().getStudy();
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


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setIndexTime(Long studyId, Date indexTime) {
        Study study = em.find(Study.class, studyId);
        study.setLastIndexTime(indexTime);
        em.merge(study);
    }

    public Timestamp getLastUpdatedTime(Long vdcId) {
        String queryString  = "SELECT max(lastupdatetime) from study where owner_id=" + vdcId;
        Query query         = em.createNativeQuery(queryString);
        //Object object       = ((List)query.getSingleResult()).get(0);
        Timestamp timestamp = (Timestamp)(query.getSingleResult());
        return timestamp;
    }

    public long getStudyDownloadCount(Long studyId) {
        String queryString  = "select sum(downloadcount) " +
                "from studyfileactivity  sfa " +
                "where sfa.study_id = " + studyId;
        Long studyDownloadCount = null;
        Query query = em.createNativeQuery(queryString);
        try {
            Object object = ((List)query.getSingleResult()).get(0);
            studyDownloadCount = (Long)object;
        } catch (Exception nre) {} // empty catch; return 0

        return studyDownloadCount != null ? studyDownloadCount.longValue() : 0;
    }

    public Long getActivityCount(Long vdcId) {
        String queryString  = " select sum(downloadcount) " +
                "from studyfileactivity  sfa, study s "+
                "where  sfa.study_id = s.id "+
                "and s.owner_id = " + vdcId;
        Long longValue = null;
        Query query         = em.createNativeQuery(queryString);
        try {
            Object object       = ((List)query.getSingleResult()).get(0);
            longValue      = (Long)object;
        } catch (Exception nre) {
            longValue = new Long("0");
        } finally {
            return longValue;
        }
    }

    public Long getTotalActivityCount() {
        String queryString  = "select sum(downloadcount) " +
                "from studyfileactivity  sfa, study s " +
                "where sfa.study_id = s.id";
        Query query         = em.createNativeQuery(queryString);
        Object object       = ((List)query.getSingleResult()).get(0);
        Long longValue      = (Long)object;
        return longValue;
    }

    public class ConvertedFilenamesFilter implements FilenameFilter{

        private String baseName;

        public ConvertedFilenamesFilter (String baseName) {
            super();
            this.baseName = baseName;
        }

        public boolean accept(File dir, String name) {
                return (name.matches(baseName + "\\.D[0-9]*") ||
                        name.equals(baseName + ".RData"));
        }
    }



}
