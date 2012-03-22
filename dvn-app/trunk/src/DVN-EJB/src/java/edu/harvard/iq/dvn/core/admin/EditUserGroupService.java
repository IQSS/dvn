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
 * EditUserGroupService.java
 *
 * Created on October 31, 2006, 5:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.admin;

import java.util.Collection;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remove;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Ellen Kraffmiller
 */
@Local
public interface EditUserGroupService extends java.io.Serializable  {
    /**
     * Remove this Stateful Session bean from the EJB Container without 
     * saving updates to the database.
     */
    @Remove
    void cancel();

    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void deleteUserGroup();

    UserGroup getUserGroup();

    void newUserGroup();

    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
   public void save( );
    /**
     *  Initialize the bean with a Study for editing
     */
    void setUserGroup(Long id);

    void setUserGroup(UserGroup userGroup);
    
    public void removeCollectionElement(Collection coll, Object elem);
    
    public List<UserDetailBean> getUserDetailBeans();
  
    public void setUserDetailBeans(List<UserDetailBean> userDetailBeans);

    public void removeLoginDomains();

    public UserGroup findById(Long id);
    
}
