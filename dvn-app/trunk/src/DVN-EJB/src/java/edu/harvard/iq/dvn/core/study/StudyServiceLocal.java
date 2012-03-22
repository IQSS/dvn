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
package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.vdc.VDC;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;
import javax.xml.bind.JAXBException;

/**
 * This is the business interface for NewSession enterprise bean.
 */
@Local
public interface StudyServiceLocal extends java.io.Serializable {
   
    /**
     * Add given Study to persistent storage.
     */

    //   Comments for breaking this into multiple classes
    //
    // 1= Retrieval
    // 2= Update
    // 3= Import/Export
    // 4= File related
    // ?= not sure if this is needed
    // x= no longer needed

    //?
    public void exportStudyFilesToLegacySystem(String lastUpdateTime, String authority) throws IOException, JAXBException;
// comment out - not used    public void addStudy(Study study); 
        
    // 1
    public Study getStudy(Long studyId);

    // 1
    public Study getStudyByGlobalId(String globalId);

    // 1 (new to this version)
    public StudyVersion getStudyVersion(String globalId, Long versionNumber);
    public StudyVersion getStudyVersion(Long studyId, Long versionNumber);
    public StudyVersion getStudyVersionById(Long versionId);

    //1
    public Study getStudyByHarvestInfo(VDC vdc, String harvestIdentifier);

    //1
    public Study getStudyForSearch(Long studyId, Map studyFields);

    //2
    public void updateStudy(Study study);
    public void updateStudyVersion(StudyVersion studyVersion);

    //2
    public void deleteStudy(Long studyId);
    
    //2
    public void deleteStudyInNewTransaction(Long studyId, boolean deleteFromIndex);
     
    //2
    public void deleteStudyList(List<Long> studyIds);
   
    //?
    public List getStudies();

    //1
    public List<Long> getAllNonHarvestedStudyIds();

    //1
    java.util.List getOrderedStudies(List studyIdList, String orderBy);

    //?
    java.util.List getRecentStudies(Long vdcId, int numResults);
    
    //?1
    public List getDvOrderedStudyIds(Long vdcId, String orderBy, boolean ascending );

    //?1
    public List getDvOrderedStudyIdsByCreator(Long vdcId, Long creatorId, String orderBy, boolean ascending );

    //4
    public void incrementNumberOfDownloads(Long studyFileId, Long currentVDCId);

    //4
    public void incrementNumberOfDownloads(Long studyFileId, Long currentVDCId, Date lastDownloadTime);

    //4
    public RemoteAccessAuth lookupRemoteAuthByHost(String remoteHost);

    //4
    public List<DataFileFormatType> getDataFileFormatTypes();


    //2
    String generateStudyIdSequence(String protocol, String authority);
    
    //2
    boolean isUniqueStudyId(String  userStudyId,String protocol,String authority);

    //4
    java.lang.String generateFileSystemNameSequence();

    //4
    boolean isUniqueFileSystemName(String fileSystemName);

    //2
    List<StudyLock> getStudyLocks();
    
    //2
    void removeStudyLock(Long studyId);

    //2
    void addStudyLock(Long studyId, Long userId, String detail);

    //2
    void deleteDataVariables(Long dataTableId);

    //2
    edu.harvard.iq.dvn.core.study.Study saveStudyVersion(StudyVersion studyVersion, Long userId);

    //3
    edu.harvard.iq.dvn.core.study.Study importHarvestStudy(File xmlFile, Long vdcId, Long userId, String harvestIdentifier);
    //void importHarvestStudyExperimental(File ddiFile, Study study);
    
    //3
    edu.harvard.iq.dvn.core.study.Study importStudy(File xmlFile,  Long harvestFormatTypeId, Long vdcId, Long userId);
    
    //3
    edu.harvard.iq.dvn.core.study.Study importStudy(File xmlFile,  Long harvestFormatTypeId, Long vdcId, Long userId, List<StudyFileEditBean> filesToUpload);
    
    //3
    edu.harvard.iq.dvn.core.study.Study importStudy(File xmlFile,  Long harvestFormatTypeId, Long vdcId, Long userId, String harvestIdentifier, List<StudyFileEditBean> filesToUpload);

    //1
    List getVisibleStudies(List studyIds, Long vdcId);

    //1
    List getViewableStudies(List<Long> studyIds);
    
    //1
    List getViewableStudies(List<Long> studyIds, Long userId, Long ipUserGroupId, Long vdcId);

    //3
    List getStudyIdsForExport();

    //?
    public List<Long> getAllStudyIds();

    //3
    public void exportStudy(Long studyId);
    public void exportStudy(Study study);
    public void exportStudyToFormat(Long studyId, String exportFormat);
    public void exportStudyToFormat(Study study, String exportFormat);
    public void exportUpdatedStudies();
    public void exportStudies(List<Long> studyIds);
    public void exportStudies(List<Long> studyIds, String exportFormat);

    //2
    public boolean isValidStudyIdString(String studyId);
    public void setIndexTime(Long studyId, Date indexTime);
    public Timestamp getLastUpdatedTime(Long vdcId);
   
    //1
    public long getStudyDownloadCount(Long studyId);
    public Long getActivityCount(Long vdcId);
    public Long getTotalActivityCount();


    //2
    public void setReadyForReview(Long studyId);
    public void setReadyForReview(Long studyId, String versionNote);
    public void setReadyForReview(StudyVersion sv);
    public void saveVersionNote(Long studyId, Long versionNumber, String newVersionNote);
    public void saveVersionNote(Long studyVersionId, String newVersionNote);
    public void setReleased(Long studyId);
    public void setReleased(Long studyId, String versionNote);
    public void destroyWorkingCopyVersion(Long studyVersionId);

    public void deaccessionStudy(StudyVersion studyVersion);

    public List getDvOrderedStudyVersionIds(Long vdcId, String orderBy, boolean ascending);
    public List getDvOrderedStudyVersionIdsByContributor(Long vdcId, Long contributorId, String orderBy, boolean ascending);

    public List getAllStudyVersionIdsByContributor(Long contributorId, String orderBy, boolean ascending);

    public List getDvOrderedDeaccessionedStudyVersionIdsByContributor(java.lang.Long vdcId, java.lang.Long contributorId, java.lang.String orderBy, boolean ascending);

    public List getDvOrderedDeaccessionedStudyVersionIds(java.lang.Long vdcId, java.lang.String orderBy, boolean ascending);

    public List getAllDeaccessionedStudyVersionIdsByContributor(java.lang.Long contributorId, java.lang.String orderBy, boolean ascending);

    public List<MetadataFormatType> findAllMetadataExportFormatTypes();
}
