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
