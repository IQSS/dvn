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
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverse;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Stateless;
import javax.faces.FacesException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 */
@Stateless
public class GroupServiceBean implements GroupServiceLocal  { 

    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    
    /**
     * Creates a new instance of UserServiceBean
     */
    public GroupServiceBean() {
    }


    
    public UserGroup findByName(String name) {
       String query="SELECT g from UserGroup g where g.name = :groupName ";
       UserGroup group=null;
      
       try {
           group=(UserGroup)em.createQuery(query).setParameter("groupName",name).getSingleResult();
       }   catch(NoResultException e) {
           // do nothing, just return null
       }
      
       return group;
    }
    public UserGroup findById(Long id) {
        return em.find(UserGroup.class,id);
    }
    
    public List<UserGroup> findAll() {
        List <UserGroup> userGroups = (List <UserGroup>) em.createQuery("SELECT ug from UserGroup ug ORDER BY ug.name").getResultList();
        
        // Trigger loading of dependent objects
        for (Iterator it = userGroups.iterator(); it.hasNext();) {
            UserGroup elem = (UserGroup) it.next();
            if (elem.getLoginDomains().size()>0) {
                for (Iterator it2 = elem.getLoginDomains().iterator(); it2.hasNext();) {
                    LoginDomain ld = (LoginDomain) it2.next();
                    Long id = ld.getId();
                    
                }
                 for (Iterator it3 = elem.getUsers().iterator(); it3.hasNext();) {
                    VDCUser user = (VDCUser) it3.next();
                    Long id = user.getId();
                    
                }
           }
            
        }
        return userGroups;

    }
    
    
    public UserGroup findIpGroupUser(String remotehost) {
        UserGroup group = null;
        try {
            List logindomains = (List<LoginDomain>) em.createQuery("Select ld from LoginDomain ld").getResultList();
            LoginDomain logindomain = null;
            Iterator iterator = logindomains.iterator();
            while (iterator.hasNext()) {
                logindomain = (LoginDomain) iterator.next();
                // domain name substring match
                if (logindomain.getIpAddress().valueOf(logindomain.getIpAddress().charAt(0)).equals("*")) {
                    if (remotehost.indexOf(logindomain.getIpAddress().substring(1)) != -1) {
                        group = logindomain.getUserGroup();
                        break;
                    }
                }
                // integer substring match.
                String parseIp = logindomain.getIpAddress().replace("*", "");
                if (remotehost.contains(parseIp)) {
                   group = logindomain.getUserGroup();
                   break;
                }
            }
        } catch (IllegalArgumentException iae) {
                throw new FacesException(iae);
        } catch (IllegalStateException ise) {
                throw new FacesException(ise);
        } finally {
            return group;
        }
    }
    
   public Collection<LoginAffiliate> findAllLoginAffiliates(){
       Collection<LoginAffiliate> loginaffiliates = (Collection<LoginAffiliate>) em.createQuery("SELECT la from LoginAffiliate la ORDER BY la.id").getResultList();
       return loginaffiliates;
   }
   
   public void remove(Long id) {
        UserGroup group= (UserGroup)em.find(UserGroup.class,id);
        if (group!=null) {
            
            for (Iterator it = group.getUsers().iterator(); it.hasNext();) {
                VDCUser elem = (VDCUser)it.next();
                elem.getUserGroups().remove(group);
            }
            // The UserGroup object is not the owner for these ManyToMany relationships, 
            // so the relationships have to be removed manually (ie we can't rely on Cascade.REMOVE)
            for (Iterator it = group.getUsers().iterator(); it.hasNext();) {
                VDCUser elem = (VDCUser) it.next();
                elem.getUserGroups().remove(group);
            }
            for (Iterator it = group.getVdcs().iterator(); it.hasNext();) {
                VDC elem = (VDC) it.next();
                elem.getAllowedGroups().remove(group);
            }
            for (Iterator it = group.getStudies().iterator(); it.hasNext();) {
                Study elem = (Study) it.next();
                elem.getAllowedGroups().remove(group);
            }
            for (Iterator it = group.getStudyFiles().iterator(); it.hasNext();) {
                StudyFile elem = (StudyFile) it.next();
                elem.getAllowedGroups().remove(group);
            }
            
            for (Iterator it = group.getHarvestingDataverses().iterator(); it.hasNext();) {
                HarvestingDataverse elem = (HarvestingDataverse) it.next();
                elem.getAllowedFileGroups().remove(group);
            }
            group.getUsers().clear();
            group.getVdcs().clear();
            group.getStudies().clear();
            group.getStudyFiles().clear();
            group.getHarvestingDataverses().clear();
            em.remove(group);
        }
        
    }    
   
}
