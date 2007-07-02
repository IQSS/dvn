/*
 * EditUserGroupService.java
 *
 * Created on October 31, 2006, 5:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

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
public interface EditUserGroupService {
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
    
}
