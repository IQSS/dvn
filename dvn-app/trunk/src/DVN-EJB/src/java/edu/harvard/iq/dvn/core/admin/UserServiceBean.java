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
 * UserServiceBean.java
 *
 * Created on September 21, 2006, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import java.lang.String;
import java.lang.String;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 */
@Stateless
public class UserServiceBean implements UserServiceLocal {

    @EJB
    RoleServiceLocal roleService;
    @EJB
    NetworkRoleServiceLocal networkRoleService;
    @EJB
    MailServiceLocal mailService;
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
    @PersistenceContext(unitName = "VDCNet-ejbPU")
    private EntityManager em;

    /**
     * Creates a new instance of UserServiceBean
     */
    public UserServiceBean() {
    }

    public void remove(Long id) {
        VDCUser user = (VDCUser) em.find(VDCUser.class, id);
        if (user != null) {
            // Need to remove this user from other collections it may belong to.
            for (Iterator it = user.getUserGroups().iterator(); it.hasNext();) {
                UserGroup group = (UserGroup) it.next();
                group.getUsers().remove(user);
            }
            for (Iterator it2 = user.getStudies().iterator(); it2.hasNext();) {
                Study study = (Study) it2.next();
                study.getAllowedUsers().remove(user);
            }
            for (Iterator it2 = user.getStudyFiles().iterator(); it2.hasNext();) {
                StudyFile studyFile = (StudyFile) it2.next();
                studyFile.getAllowedUsers().remove(user);
            }
            for (Iterator it = user.getAllowedFileVdcs().iterator(); it.hasNext();) {
                VDC elem = (VDC) it.next();
                elem.getAllowedFileUsers().remove(user);
            }

            em.remove(user);
        }

    }

    public VDCUser find(Long id) {
        return (VDCUser) em.find(VDCUser.class, id);
    }

    public VDCUser findByUserName(String userName) {
        return findByUserName(userName, false);
    }

    public VDCUser findByUserName(String userName, boolean activeOnly) {
        String query = "SELECT u from VDCUser u where u.userName = :userName ";
        if (activeOnly) {
            query += " and u.active=true ";
        }
        VDCUser user = null;
        try {
            user = (VDCUser) em.createQuery(query).setParameter("userName", userName).getSingleResult();
        } catch (javax.persistence.NoResultException e) {
        // DO nothing, just return null.
        }
        // loop thru user  vdc roles, so they are available
        if (user != null && user.getVdcRoles() != null) {
            for (Iterator it = user.getVdcRoles().iterator(); it.hasNext();) {
                VDCRole elem = (VDCRole) it.next();
                Long id = elem.getRole().getId();
                id = elem.getVdc().getId();
            }
        }
        return user;
    }

    
    public VDCUser findByEmail(String email) {
        String query = "SELECT u from VDCUser u where u.email = :email ";
        query += " and u.active=true ";
        
        VDCUser user = null;
        try {
            user = (VDCUser) em.createQuery(query).setParameter("email", email).getSingleResult();
        } catch (javax.persistence.NoResultException e) {
        // DO nothing, just return null.
        }
        return user;
    }

    
    public List findAll() {
        List userList = em.createQuery("select object(o) from VDCUser as o order by o.userName").getResultList();
        // Loop thru VDCRoles to trigger load from the DB before detaching
        for (Iterator<VDCUser> it = userList.iterator(); it.hasNext();) {
            VDCUser elem = it.next();
            Collection vdcRoles = elem.getVdcRoles();
            for (Iterator<VDCRole> it2 = vdcRoles.iterator(); it2.hasNext();) {
                VDCRole vdcRole = it2.next();
                Long id = vdcRole.getRole().getId();
                id = vdcRole.getVdc().getId();
            }

        }
        return userList;
    }

    public void addVdcRole(Long userId, Long vdcId, String roleName) {
        Role role = roleService.findByName(roleName);
        VDCRole vdcRole = new VDCRole();
        vdcRole.setVdcUser(em.find(VDCUser.class, userId));
        vdcRole.setVdc(em.find(VDC.class, vdcId));
        vdcRole.setRole(role);
        em.persist(vdcRole);  // Added for EclipseLink - wasn't being saved otherwise
        VDCUser user = em.find(VDCUser.class, userId);
        user.getVdcRoles().add(vdcRole);
        VDC vdc = em.find(VDC.class, vdcId);
        vdc.getVdcRoles().add(vdcRole);
    }

    public void addCreatorRequest(Long userId) {
    //   NetworkRoleRequest nrr = new NetworkRoleRequest();
    //   NetworkRole = em.find()
    //   nrr.setNetworkRole()

    }

    public void addContributorRequest(Long userId, Long vdcId) {
        VDCUser user = em.find(VDCUser.class, userId);
        VDC vdc = em.find(VDC.class, vdcId);
        Role contributor = roleService.findByName(RoleServiceLocal.CONTRIBUTOR);
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setRole(contributor);
        roleRequest.setVdc(vdc);
        roleRequest.setVdcUser(user);
        vdc.getRoleRequests().add(roleRequest);

    }

    public void makeContributor(Long userId, Long vdcId) {
        VDCUser user = em.find(VDCUser.class, userId);
        VDC vdc = em.find(VDC.class, vdcId);
        // if the current role is priveleged viewer, remove it so we can promote
        // user to contributor.
        if (user.isPrivilegedViewer(vdc)) {
           VDCRole role = user.getVDCRole(vdc);
            user.getVdcRoles().remove(role);
            em.remove(role);
        }
        if (user.getVDCRole(vdc)==null) {
            addVdcRole(userId, vdcId, RoleServiceLocal.CONTRIBUTOR);
            mailService.sendContributorAccountNotification(vdc.getContactEmail(), user.getUserName(), vdc.getName());
        }
    }
    
    public void makeCreator(Long userId) {
        VDCUser user = em.find(VDCUser.class, userId);
        VDCNetwork vdcNetwork = vdcNetworkService.find();
        // If the user already has a networkRole, then he is already a creator or networkAdmin,
        // so don't need to change the role.
        if (user.getNetworkRole()==null) {
            user.setNetworkRole(networkRoleService.getCreatorRole());
            mailService.sendCreatorAccountNotification(vdcNetwork.getContactEmail(), user.getUserName());         

        }
    }

    public void setActiveStatus(Long userId, boolean active) {
        VDCUser user = em.find(VDCUser.class, userId);
        user.setActive(active);

    }

    public boolean validatePassword(Long userId, String password) {
        VDCUser user = em.find(VDCUser.class, userId);
        String encryptedPassword = PasswordEncryption.getInstance().encrypt(password);
        return encryptedPassword.equals(user.getEncryptedPassword());

    }
    
    public void updatePassword(Long userId){
        String plainTextPassword = PasswordEncryption.generateRandomPassword();
        VDCUser user = em.find(VDCUser.class, userId);
        user.setEncryptedPassword(encryptPassword(plainTextPassword));
        mailService.sendPasswordUpdateNotification(user.getEmail(), user.getFirstName(), user.getUserName(), plainTextPassword);
       
    }

    public String encryptPassword(String plainText) {
        return PasswordEncryption.getInstance().encrypt(plainText);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void clearAgreedTermsOfUse() {
        System.out.println("IN clearAgreedTermsOfUse");
        em.createQuery("update VDCUser u set u.agreedTermsOfUse=false "//where " 
                // " u.networkRole is null or "
                //  +" u.networkRole.name <> '"
                //   +NetworkRoleServiceBean.ADMIN+"'"
                ).executeUpdate();

    }

    public void setAgreedTermsOfUse(Long userId, boolean agreed) {
        VDCUser user = em.find(VDCUser.class, userId);
        user.setAgreedTermsOfUse(agreed);
    }

    public boolean hasUserCreatedDataverse(Long userId) {
        String queryStr = "select count(*) from vdc where creator_id = " + userId;
        Query query = em.createNativeQuery(queryStr);
   
        Long count = (Long) query.getSingleResult();
        System.out.println("count is "+count+", type "+count.getClass().getName());
        if (count.compareTo(new Long(0))>0) {
            return true;
        } else {
            return false;
        }
    }
     public boolean hasUserContributed(Long userId) {
       String queryStr = "select count(*) from versioncontributor where contributor_id = " + userId;
        Query query = em.createNativeQuery(queryStr);
    
        Long count = (Long) query.getSingleResult();
        
        System.out.println("count is "+count+", type "+count.getClass().getName());
        if (count.compareTo(new Long(0))>0) {
            return true;
        } else {
            return false;
        }
     }

}
