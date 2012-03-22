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
 * EditVDCPrivilegesService.java
 *
 * Created on October 23, 2006, 4:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.vdc.VDC;
import javax.ejb.Remove;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Ellen Kraffmiller
 */
public interface EditVDCPrivilegesService extends java.io.Serializable  {
    /**
     * Remove this Stateful Session bean from the EJB Container without 
     * saving updates to the database.
     */
    @Remove
    void cancel();

    /**
     * Getter for property contributorRequests.
     * 
     * @return Value of property contributorRequests.
     */
//    List<ContributorRequestBean> getContributorRequests();

    VDC getVdc();

    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save();

    public void removeAllowedGroup(Long groupId);

    public void addAllowedGroup(Long groupId);
    
    public void removeRole(int listIndex);
    /**
     *  Initialize the bean with a Study for editing
     */
    void setVdc(Long id);    
    

    public void removeAllowedFileGroup(Long groupId);
    
    public void  addAllowedFileGroup(Long groupId);
    
    public void removeAllowedFileUser(Long userId);
    
    public void  addAllowedFileUser(Long userId);

    public edu.harvard.iq.dvn.core.admin.Role findRoleById(java.lang.Long id);
    
}
