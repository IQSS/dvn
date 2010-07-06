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
 * StudyAccessRequestServiceBean.java
 *
 * Created on January 30, 2007, 11:29 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.vdc;

import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyAccessRequest;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateless
public class StudyAccessRequestServiceBean implements edu.harvard.iq.dvn.core.vdc.StudyAccessRequestServiceLocal {
      @PersistenceContext(unitName="VDCNet-ejbPU") EntityManager em;

   public StudyAccessRequest findByUserStudy(Long userId, Long studyId) {
      String query="SELECT s from StudyAccessRequest s where s.vdcUser.id = "+userId+" and s.study.id = "+studyId;
              
        StudyAccessRequest studyRequest=null;
        try {
            studyRequest=(StudyAccessRequest)em.createQuery(query).getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            // DO nothing, just return null.
        }
        return studyRequest;
    }
    
     public void create(Long vdcUserId, Long studyId) {
        StudyAccessRequest studyRequest = new StudyAccessRequest();
        
        VDCUser user = (VDCUser)em.find(VDCUser.class,vdcUserId);
        studyRequest.setVdcUser(user);
        
        Study study = (Study)em.find(Study.class,studyId);
        studyRequest.setStudy(study);
        study.getStudyRequests().add(studyRequest);
      
        em.persist(studyRequest);
    }
   
}
