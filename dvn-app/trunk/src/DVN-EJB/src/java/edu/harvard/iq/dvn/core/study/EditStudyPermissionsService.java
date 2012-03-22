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
/*
 * EditStudyPermissionsService.java
 *
 * Created on November 3, 2006, 3:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.admin.VDCUser;
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
public interface EditStudyPermissionsService extends java.io.Serializable {
    /**
     * Remove this Stateful Session bean from the EJB Container without
     * saving updates to the database.
     */
    @Remove
    void cancel();
   
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

    public void setStudy(java.lang.Long id, java.lang.Long versionNumber);

    public void setCurrentVersionFiles(boolean currentVersionFiles);

    public boolean isCurrentVersionFiles();
    
    
}
