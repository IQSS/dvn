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

package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
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
    
    public void exportStudyFilesToLegacySystem(String lastUpdateTime, String authority) throws IOException, JAXBException;
// comment out - not used    public void addStudy(Study study); 
        
    public Study getStudy(Long studyId);
    
    public Study getStudyByGlobalId(String globalId);
   

    public Study getStudyByHarvestInfo(VDC vdc, String harvestIdentifier);
     
    public Study getStudyDetail(Long studyId);
    
    public Study getStudyForSearch(Long studyId, Map studyFields);
    
    public void updateStudy(Study study);
    
    public void deleteStudy(Long studyId) ;
    
    public void deleteStudyInNewTransaction(Long studyId, boolean deleteFromIndex) ;
     
    public void deleteStudyList(List<Long> studyIds);
   
    public List getStudies();
    
    public List<Long> getAllNonHarvestedStudyIds();
        
    java.util.List getOrderedStudies(List studyIdList, String orderBy);

    edu.harvard.hmdc.vdcnet.study.StudyFile getStudyFile(Long fileId);

    edu.harvard.hmdc.vdcnet.study.FileCategory getFileCategory(Long fileCategoryId);

    java.util.List getRecentStudies(Long vdcId, int numResults);

    java.util.List <Study> getContributorStudies(VDCUser contributor, VDC vdc);

    java.util.List<edu.harvard.hmdc.vdcnet.study.Study> getReviewerStudies(Long vdcId);
    java.util.List<edu.harvard.hmdc.vdcnet.study.Study> getNewStudies(Long vdcId);
    public List getDvOrderedStudyIds(Long vdcId, String orderBy, boolean ascending );

    void incrementNumberOfDownloads(Long studyId);
    void incrementNumberOfDownloads(Long studyFileId, Date lastDownloadTime);
    RemoteAccessAuth lookupRemoteAuthByHost(String remoteHost); 
    
    List<DataFileFormatType> getDataFileFormatTypes();

    void addIngestedFiles(Long studyId, List fileBeans, Long userId);
    
    java.util.List<FileCategory> getOrderedFileCategories(Long studyId);
    
    java.util.List<StudyFile> getOrderedFilesByCategory(Long fileCategoryId);

    java.util.List<StudyFile> getOrderedFilesByStudy(Long studyId);

    String generateStudyIdSequence(String protocol, String authority);
    
    boolean isUniqueStudyId(String  userStudyId,String protocol,String authority);

    java.lang.String generateFileSystemNameSequence();

    boolean isUniqueFileSystemName(String fileSystemName);

    List<StudyLock> getStudyLocks();
    
    void removeStudyLock(Long studyId);

    void addStudyLock(Long studyId, Long userId, String detail);

    void deleteDataVariables(Long dataTableId);

    edu.harvard.hmdc.vdcnet.study.Study saveStudy(Study study, Long userId);

    edu.harvard.hmdc.vdcnet.study.Study importHarvestStudy(File xmlFile, Long vdcId, Long userId, String harvestIdentifier);
    edu.harvard.hmdc.vdcnet.study.Study importStudy(File xmlFile,  Long harvestFormatTypeId, Long vdcId, Long userId);
    edu.harvard.hmdc.vdcnet.study.Study importStudy(File xmlFile,  Long harvestFormatTypeId, Long vdcId, Long userId, List<StudyFileEditBean> filesToUpload);
    edu.harvard.hmdc.vdcnet.study.Study importStudy(File xmlFile,  Long harvestFormatTypeId, Long vdcId, Long userId, String harvestIdentifier, List<StudyFileEditBean> filesToUpload);

    List getVisibleStudies(List studyIds, Long vdcId);
    List getViewableStudies(List<Long> studyIds);
    List getViewableStudies(List<Long> studyIds, Long userId, Long ipUserGroupId, Long vdcId);

    List getStudyIdsForExport();
    public List<Long> getAllStudyIds(); 
    public void exportStudy(Long studyId);
    public void exportStudy(Study study);
    public void exportStudyToFormat(Long studyId, String exportFormat);
    public void exportStudyToFormat(Study study, String exportFormat);
    public void exportUpdatedStudies();
    public void exportStudies(List<Long> studyIds);
    public void exportStudies(List<Long> studyIds, String exportFormat);
    
    public void addFiles(Study study, List<StudyFileEditBean> newFiles, VDCUser user);
    public void addFiles(Study study, List<StudyFileEditBean> newFiles, VDCUser user, String ingestEmail);
    
    public List<StudyFile> getStudyFilesByExtension(String extension);

    public void updateStudyFile(StudyFile detachedStudyFile);
    public boolean isValidStudyIdString(String studyId);

    public void setIndexTime(Long studyId, Date indexTime);
 
    public Timestamp getLastUpdatedTime(Long vdcId);
    
    public void updateReviewState(Long studyId, String reviewStateName);
    
    public Long getActivityCount(Long vdcId);

    public Long getTotalActivityCount();
}
