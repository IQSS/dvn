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

package edu.harvard.hmdc.vdcnet.admin;

import edu.harvard.hmdc.vdcnet.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
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
    @EJB MailServiceLocal mailService;
    @EJB RoleServiceLocal roleService;
    @EJB UserServiceLocal userService;

    @PersistenceContext(type = PersistenceContextType.EXTENDED,unitName="VDCNet-ejbPU")
    //  @EJB RoleServiceLocal roleService;
    EntityManager em;
    private VDC vdc;
    
    private List privilegedUsers;
    
    public List getPrivilegedUsers() {
        return privilegedUsers;
    }
    
    /**
     *  Initialize the bean with a Study for editing
     */
    public void setVdc(Long id ) {
        vdc = em.find(VDC.class,id);
        if (vdc==null) {
            throw new IllegalArgumentException("Unknown vdc id: "+id);
        }
    /*    contributorRequests = new ArrayList();
        for (Iterator<RoleRequest> it = vdc.getRoleRequests().iterator(); it.hasNext();) {
            RoleRequest elem = it.next();
            if (elem.getRole().getName().equals(RoleServiceLocal.CONTRIBUTOR)) {
                contributorRequests.add(new ContributorRequestBean(elem));
            }
            
        }
     */
        privilegedUsers = new ArrayList();
        for (Iterator it = vdc.getVdcRoles().iterator(); it.hasNext();) {
            VDCRole elem = (VDCRole) it.next();
            List privilegedUsersRow = new ArrayList();
            privilegedUsersRow.add(elem);
            privilegedUsersRow.add(elem.getRoleId());
            privilegedUsers.add(privilegedUsersRow);
            
        }
        
    }
    
    
    
    public  VDC getVdc() {
        return vdc;
    }
    
    
    private Role getContributorRole() {
        return roleService.findByName(RoleServiceLocal.CONTRIBUTOR);
    }
   
   public void addUserRole(String userName) {
            VDCUser user = userService.findByUserName(userName);
            user = em.find(VDCUser.class, user.getId());
            VDCRole vdcRole = new VDCRole();
            vdcRole.setVdcUser(user);
            vdcRole.setVdc(vdc);
            vdc.getVdcRoles().add(vdcRole);        
            List privilegedUsersRow = new ArrayList();
            privilegedUsersRow.add(vdcRole);
            privilegedUsersRow.add(vdcRole.getRoleId());
            privilegedUsers.add(privilegedUsersRow);
        
        
    }
        
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save(String contributorUrl) {
        
      
        
        for (Iterator<List> it = privilegedUsers.iterator(); it.hasNext();) {
            List privilegedUsersRow =  it.next();
            VDCRole vdcRole = (VDCRole)privilegedUsersRow.get(0);  
            Long roleId=null;
            if (privilegedUsersRow.get(1)!=null) {
                if (privilegedUsersRow.get(1) instanceof Long) {
                    roleId =(Long)privilegedUsersRow.get(1);
                } else {
                    roleId=Long.parseLong( (String)privilegedUsersRow.get(1));
                }
                if (roleId!=null) {
                    vdcRole.setRole(roleService.findById(roleId));
                }
            }
         }
       
     
        // Because we have to manage relationships manually with EJB3,
        // we need to manually update each user's vdcRoles collection to match the vdcRoles
        // selected in the form. (This gets kind of weird!)
        for (Iterator<VDCRole> it = vdc.getVdcRoles().iterator(); it.hasNext();) {
            VDCRole vdcRole = (VDCRole) it.next();
            if (vdcRole.getRoleId()!=null) {         
                VDCUser user = (VDCUser)em.find(VDCUser.class, vdcRole.getVdcUser().getId());
                VDCRole userVDCRole = user.getVDCRole(vdc);
                if (userVDCRole!=null) {
                    userVDCRole.setRole(vdcRole.getRole());
                
                } else {
                    user.getVdcRoles().add(vdcRole);
                }
            }            
        }        
        em.flush();
    }
    
    public void removeRole(Long userId) {
        VDCRole removeRole = null;
        for (Iterator<VDCRole> it = vdc.getVdcRoles().iterator(); it.hasNext();) {
            VDCRole vdcRole =  it.next();
            if (vdcRole.getVdcUser().getId()==userId) {
                removeRole=vdcRole;
            }
        }
        if (removeRole!=null) {
            VDCUser user= removeRole.getVdcUser();
            vdc.getVdcRoles().remove(removeRole);
            user.getVdcRoles().remove(removeRole);
            em.remove(removeRole);
        }
        
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
    
    /**
     * Holds value of property contributorRequests.
     */
//    private List<ContributorRequestBean> contributorRequests;
    
    /**
     * Getter for property contributorRequests.
     * @return Value of property contributorRequests.
     */
 //   public List<ContributorRequestBean> getContributorRequests() {
 //       return this.contributorRequests;
//    }
//    
    /**
     * Setter for property contributorRequests.
     * @param contributorRequests New value of property contributorRequests.
     */
 //   public void setContributorRequests(List<ContributorRequestBean> contributorRequests) {
 //       this.contributorRequests = contributorRequests;
 //   }
    
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
    
    
    
}
