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
 * EditVDCPrivilegesServiceBean.java
 *
 * Created on September 29, 2006, 1:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDC;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EditVDCPrivilegesServiceBean implements EditVDCPrivilegesService  {

    @PersistenceContext(type = PersistenceContextType.EXTENDED,unitName="VDCNet-ejbPU")
    //  @EJB RoleServiceLocal roleService;
    EntityManager em;
    private VDC vdc;

   
    
    
    /**
     *  Initialize the bean with a vdc for editing
     *  The privilegedUsers List is for the table of user roles in the form
     */
    public void setVdc(Long id ) {
        vdc = em.find(VDC.class,id);
        if (vdc==null) {
            throw new IllegalArgumentException("Unknown vdc id: "+id);
        }
 
       
        
    }
    
    
    
    public  VDC getVdc() {
        return vdc;
    }
    
   
    public Role findRoleById(Long id) {
        return em.find(Role.class,id);
    }    
   
 
        
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    
    public void save() {
        // Don't need to do any processing, just commit the changes to the DB
        
    }

    public void removeRole(int index) {
        VDCRole removeRole = vdc.getVdcRoles().get(index);
        vdc.getVdcRoles().remove(removeRole);
        em.remove(removeRole);
    }
   
    /**
     * Remove this Stateful Session bean from the EJB Container without
     * saving updates to the database.
     */
    @Remove
    public void cancel() {
        
    }
    /**
     * Creates a new instance of EditVDCPrivilegesServiceBean
     */
    public EditVDCPrivilegesServiceBean() {
    }
    
 
    
    public void removeAllowedGroup(Long groupId) {
        UserGroup group = em.find(UserGroup.class,groupId);
        vdc.getAllowedGroups().remove(group);
        group.getVdcs().remove(vdc);
    }
    
    public void  addAllowedGroup(Long groupId) {
        UserGroup group = em.find(UserGroup.class,groupId);
        vdc.getAllowedGroups().add(group);
        group.getVdcs().add(vdc);
        
    }
    
   
    public void removeAllowedFileGroup(Long groupId) {
        UserGroup group = em.find(UserGroup.class,groupId);
        getVdc().getAllowedFileGroups().remove(group);
        
    }
    
    public void  addAllowedFileGroup(Long groupId) {
        UserGroup group = em.find(UserGroup.class,groupId);
        getVdc().getAllowedFileGroups().add(group);
       
        
    }
    
     public void removeAllowedFileUser(Long userId) {
        VDCUser user = em.find(VDCUser.class,userId);
        getVdc().getAllowedFileUsers().remove(user);
      
    }
    
    public void  addAllowedFileUser(Long userId) {
        VDCUser user = em.find(VDCUser.class,userId);
        getVdc().getAllowedFileUsers().add(user);
     
        
    }
    
    
}
