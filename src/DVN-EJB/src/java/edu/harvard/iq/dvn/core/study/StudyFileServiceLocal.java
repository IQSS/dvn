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
    public FileCategory getFileCategory(Long fileCategoryId);

    public List<FileMetadata> getStudyFilesByExtension(String extension);
    public void updateStudyFile(StudyFile detachedStudyFile);

    java.util.List<FileMetadata> getOrderedFilesByStudy(Long studyId);

    public Boolean doesStudyHaveSubsettableFiles(Long studyId);


    public void addFiles(StudyVersion studyVersion, List<StudyFileEditBean> newFiles, VDCUser user);
    public void addFiles(StudyVersion studyVersion, List<StudyFileEditBean> newFiles, VDCUser user, String ingestEmail);
    public void addIngestedFiles(Long studyId, List fileBeans, Long userId);
    
}
