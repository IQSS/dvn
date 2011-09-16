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
