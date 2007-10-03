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
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;
import javax.xml.bind.JAXBException;

/**
 * This is the business interface for NewSession enterprise bean.
 */
@Local
public interface StudyServiceLocal {
   
    /**
     * Add given Study to persistent storage.
     */
    
    public void exportStudyFilesToLegacySystem(String lastUpdateTime, String authority) throws IOException, JAXBException;
    public void addStudy(Study study); 
        
    public Study getStudy(Long studyId);
    
    public Study getStudyByGlobalId(String globalId);
   
    public Study getStudyByHarvestInfo(String authority, String harvestIdentifier);
   
    public Study getStudyDetail(Long studyId);
    
    public Study getStudyForSearch(Long studyId, Map studyFields);
    
    public void updateStudy(Study study);
    
    public void deleteStudy(Long studyId) ;
   
    public List getStudies();
        
    java.util.List getStudies(List studyIdList, String orderBy);

    edu.harvard.hmdc.vdcnet.study.StudyFile getStudyFile(Long fileId);

    edu.harvard.hmdc.vdcnet.study.FileCategory getFileCategory(Long fileCategoryId);

    java.util.List getRecentStudies(Long vdcId, int numResults);

    java.util.List <Study> getContributorStudies(VDCUser contributor);

    java.util.List<edu.harvard.hmdc.vdcnet.study.Study> getReviewerStudies(Long vdcId);
   java.util.List<edu.harvard.hmdc.vdcnet.study.Study> getNewStudies(Long vdcId);

    void incrementNumberOfDownloads(Long studyId);
    
    List<DataFileFormatType> getDataFileFormatTypes();

    void addIngestedFiles(Long studyId, List fileBeans, Long userId);
    
    java.util.List<FileCategory> getOrderedFileCategories(Long studyId);
    
    java.util.List<StudyFile> getOrderedFilesByCategory(Long fileCategoryId);

    java.util.List<StudyFile> getOrderedFilesByStudy(Long studyId);

    String generateStudyIdSequence(String protocol, String authority);
    
    boolean isUniqueStudyId(String  userStudyId,String protocol,String authority);

    java.lang.String generateFileSystemNameSequence();

    boolean isUniqueFileSystemName(String fileSystemName);

    void removeStudyLock(Long studyId);

    void addStudyLock(Long studyId, Long userId, String detail);

    void deleteDataVariables(Long dataTableId);

    edu.harvard.hmdc.vdcnet.study.Study saveStudy(Study study, Long userId);

    edu.harvard.hmdc.vdcnet.study.Study importLegacyStudy(File xmlFile, Long vdcId, Long userId);
    edu.harvard.hmdc.vdcnet.study.Study importHarvestStudy(File xmlFile, Long vdcId, Long userId, String harvestIdentifier, boolean allowUpdates);
    edu.harvard.hmdc.vdcnet.study.Study importStudy(File xmlFile,  int xmFileFormatId, Long vdcId, Long userId, boolean registerHandle, boolean generateHandle, boolean allowUpdates, boolean checkRestrictions, boolean retrieveFiles, String harvestIdentifier);

    List getVisibleStudies(List studyIds, Long vdcId);
    List getViewableStudies(List<Long> studyIds, Long userId, Long ipUserGroupId);

    List getStudyIdsForExport();
    public void exportStudy(Long studyId);

    public void exportUpdatedStudies();
}
