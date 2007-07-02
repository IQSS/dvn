/*
 * UserServiceBean.java
 *
 * Created on September 21, 2006, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 */
@Stateless
public class UserServiceBean implements UserServiceLocal {
    @EJB RoleServiceLocal roleService;
    
    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    
    /**
     * Creates a new instance of UserServiceBean
     */
    public UserServiceBean() {
    }
    
    public void remove(Long id) {
        VDCUser user= (VDCUser)em.find(VDCUser.class,id);
        if (user!=null) {
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
        String query="SELECT u from VDCUser u where u.userName = :userName ";
        if (activeOnly) {
            query+=" and u.active=true ";
        }
        VDCUser user=null;
        try {
            user=(VDCUser)em.createQuery(query).setParameter("userName",userName).getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            // DO nothing, just return null.
        }
        // loop thru user  vdc roles, so they are available
        if (user!=null && user.getVdcRoles()!=null) {
            for (Iterator it = user.getVdcRoles().iterator(); it.hasNext();) {
                VDCRole elem = (VDCRole) it.next();
                Long id = elem.getRole().getId();
                id = elem.getVdc().getId();
            }
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
                VDCRole vdcRole =  it2.next();
                Long id  = vdcRole.getRole().getId();
                id = vdcRole.getVdc().getId();
            }
            
        }
        return userList;
    }
    
    public void addVdcRole(Long userId, Long vdcId, String roleName){
        Role role = roleService.findByName(roleName);
        VDCRole vdcRole = new VDCRole();
        vdcRole.setVdcUser(em.find(VDCUser.class, userId));
        vdcRole.setVdc(em.find(VDC.class,vdcId));
        vdcRole.setRole(role);
        VDCUser user = em.find(VDCUser.class, userId);
        user.getVdcRoles().add(vdcRole);
        VDC vdc = em.find(VDC.class,vdcId);
        vdc.getVdcRoles().add(vdcRole);
    }
    
    public void addCreatorRequest(Long userId) {
     //   NetworkRoleRequest nrr = new NetworkRoleRequest();
     //   NetworkRole = em.find()
     //   nrr.setNetworkRole()
        
    }
    
    public void addContributorRequest(Long userId, Long vdcId) {
       VDCUser user = em.find(VDCUser.class,userId);
       VDC vdc = em.find(VDC.class, vdcId);
       Role contributor = roleService.findByName(RoleServiceLocal.CONTRIBUTOR);
       RoleRequest roleRequest = new RoleRequest();
       roleRequest.setRole(contributor);
       roleRequest.setVdc(vdc);
       roleRequest.setVdcUser(user);
       vdc.getRoleRequests().add(roleRequest);
        
    }
    
    public void setActiveStatus(Long userId, boolean active) {
        VDCUser user = em.find(VDCUser.class,userId);
        user.setActive(active);
        
    }
}
