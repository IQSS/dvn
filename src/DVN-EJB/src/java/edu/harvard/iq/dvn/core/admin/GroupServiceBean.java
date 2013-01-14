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
 * UserServiceBean.java
 *
 * Created on September 21, 2006, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.vdc.HarvestingDataverse;
import edu.harvard.iq.dvn.core.vdc.VDC;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
                String parseIp      = new String();
                String regexp       = null;
                Pattern pattern     = null;
                Matcher matcher     = null;
                boolean isMatch     = false;
                if (logindomain.getIpAddress().indexOf(".*",0) != -1) {
                    parseIp = logindomain.getIpAddress().replace(".*", "\\.");
                    regexp  = "^" + parseIp;
                    pattern = Pattern.compile(regexp);
                    matcher = pattern.matcher(remotehost);
                    isMatch = matcher.find();
                } else {
                    parseIp = logindomain.getIpAddress();
                    if (parseIp.equals(remotehost))
                        isMatch = true;
                }
                if (isMatch) {
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
                VDCUser elem = (VDCUser) it.next();
                VDCUser user = em.find(VDCUser.class, elem.getId());
                user.getUserGroups().remove(group);
            }
            for (Iterator it = group.getVdcs().iterator(); it.hasNext();) {
                VDC elem = (VDC) it.next();
                VDC vdc = em.find(VDC.class,elem.getId());
                elem.getAllowedGroups().remove(group);
            }
            for (Iterator it = group.getStudies().iterator(); it.hasNext();) {
                Study elem = (Study) it.next();
                Study study = em.find(Study.class, elem.getId());
                study.getAllowedGroups().remove(group);
            }
            for (Iterator it = group.getStudyFiles().iterator(); it.hasNext();) {
                StudyFile elem = (StudyFile) it.next();
                StudyFile studyFile = em.find(StudyFile.class, elem.getId());            
                studyFile.getAllowedGroups().remove(group);
            }
            
            for (Iterator it = group.getAllowedFileVdcs().iterator(); it.hasNext();) {
                VDC elem = (VDC) it.next();
                elem.getAllowedFileGroups().remove(group);
            }
            group.getUsers().clear();
            group.getVdcs().clear();
            group.getStudies().clear();
            group.getStudyFiles().clear();
            group.getAllowedFileVdcs().clear();
            em.remove(group);
        }
        
    }    
   
}
