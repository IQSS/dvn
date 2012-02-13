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
 * EditNetworkPrivilegesService.java
 *
 * Created on October 28, 2006, 6:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import java.util.List;
import javax.ejb.Remove;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Ellen Kraffmiller
 */
public interface EditNetworkPrivilegesService  extends java.io.Serializable  {
    /**
     * Remove this Stateful Session bean from the EJB Container without
     * saving updates to the database.
     */
    @Remove
    void cancel();

    List<CreatorRequestBean> getCreatorRequests();

    VDCNetwork getNetwork();

    List<NetworkPrivilegedUserBean> getPrivilegedUsers();

    /**
     *  Initialize the bean with a Study for editing
     */
    void init();

    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void save(String creatorUrl);

    void setCreatorRequests(List<CreatorRequestBean> creatorRequests);

    void setNetwork(VDCNetwork network);

    void setPrivilegedUsers(List<NetworkPrivilegedUserBean> privilegedUsers);
    
    public void addPrivilegedUser(Long userId );
    
    public List<VDCUser> getTOUPrivilegedUsers();
    public void setTOUPrivilegedUsers(List<VDCUser> TOUprivilegedUsers);    
    public void addTOUPrivilegedUser(Long userId );
    
}
