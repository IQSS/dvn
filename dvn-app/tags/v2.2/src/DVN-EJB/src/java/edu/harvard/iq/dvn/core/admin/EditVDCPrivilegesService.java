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
    
}
