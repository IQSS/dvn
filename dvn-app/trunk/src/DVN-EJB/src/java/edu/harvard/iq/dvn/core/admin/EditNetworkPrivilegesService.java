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
    
    List<NetworkPrivilegedUserBean>getPrivilegedUsersByName(String searchName);

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

    public void initTOUPrivilegedUsers();

    public void save(java.lang.String creatorUrl, boolean allowCreate);
    
}
