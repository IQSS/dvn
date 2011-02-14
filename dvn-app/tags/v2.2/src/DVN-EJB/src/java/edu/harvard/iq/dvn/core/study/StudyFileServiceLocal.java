/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.admin.VDCUser;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author gdurand
 */
@Local
public interface StudyFileServiceLocal {


    public StudyFile getStudyFile(Long fileId);

    public List<FileMetadata> getStudyFilesByExtension(String extension);
    public void updateStudyFile(StudyFile detachedStudyFile);

    java.util.List<Long> getOrderedFilesByStudy(Long studyId);
    java.util.List<FileMetadata> getOrderedFilesByStudyVersion(Long svId);
        java.util.List<FileMetadata> getFilesByStudyVersionOrderedById(Long svId);

    public Boolean doesStudyHaveSubsettableFiles(Long studyVersionId);


    public void addFiles(StudyVersion studyVersion, List<StudyFileEditBean> newFiles, VDCUser user);
    public void addFiles(StudyVersion studyVersion, List<StudyFileEditBean> newFiles, VDCUser user, String ingestEmail);
    public void addIngestedFiles(Long studyId, String versionNote, List fileBeans, Long userId);
    
}
