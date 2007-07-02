/*
 * EditStudyPermissionsService.java
 *
 * Created on November 3, 2006, 3:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.Local;
import javax.ejb.Remove;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Ellen Kraffmiller
 */
@Local
public interface EditStudyPermissionsService {
    /**
     * Remove this Stateful Session bean from the EJB Container without
     * saving updates to the database.
     */
    @Remove
    void cancel();
    
    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void deleteStudy();
    
    /**
     * Getter for property filePermissions.
     *
     * @return Value of property filePermissions.
     */
    Collection<FileDetailBean> getFileDetails();
    
    Study getStudy();
    
    /**
     * Getter for property studyPermissions.
     *
     * @return Value of property studyPermissions.
     */
    Collection<PermissionBean> getStudyPermissions();
    
    void removeCollectionElement(Collection coll, Object elem);
    
    void removeCollectionElement(Iterator iter, Object elem);
    
    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void save();
    
    /**
     * Setter for property filePermissions.
     *
     * @param filePermissions New value of property filePermissions.
     */
    void setFileDetails(Collection<FileDetailBean> fileDetails);
    
    /**
     *  Initialize the bean with a Study for editing
     */
    void setStudy(Long id);
    
    /**
     * Setter for property studyPermissions.
     *
     * @param studyPermissions New value of property studyPermissions.
     */
    void setStudyPermissions(Collection<PermissionBean> studyPermissions);
    
    String getStudyRestriction();
    
    void setStudyRestriction(String restriction);
    
    void addStudyUser(Long userId);
    
    void addStudyGroup(Long groupId);
    
    void removeStudyPermissions();
    
    Collection<StudyRequestBean> getStudyRequests();
    
    
    void setStudyRequests(Collection<StudyRequestBean> studyRequests);
    
    void addFileUser(Long id);
    void addFileGroup(Long id);
   
    void removeFilePermissions();
    
    void updateRequests(String studyUrl);
    
    void setFileRestriction( boolean restricted);
    
    
}
