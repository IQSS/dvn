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
import edu.harvard.iq.dvn.core.study.StudyFile;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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

    public List<StudyAccessRequest> findByUserStudyFiles(Long userId, Long studyId, List<Long> fileIdList) {
        String fileIds = idListString(fileIdList);
        String queryStr = "SELECT s FROM StudyAccessRequest s WHERE s.vdcUser.id = " + userId + " and s.study.id = "+studyId+" and s.studyFile.id IN  (" + fileIds + ")";
        Query query = em.createQuery(queryStr);
        List<StudyAccessRequest> studyRequests = query.getResultList();
        
        return studyRequests; 
    }
    
    private String idListString(List idList) {
        StringBuffer sb = new StringBuffer();
        Iterator iter = idList.iterator();
        while (iter.hasNext()) {
            Long id = (Long) iter.next();
            sb.append(id);
            if (iter.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
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
    
    public void create(Long vdcUserId, Long studyId, Long fileId) {
        StudyAccessRequest studyRequest = new StudyAccessRequest();
        
        VDCUser user = (VDCUser)em.find(VDCUser.class,vdcUserId);
        studyRequest.setVdcUser(user);
        
        Study study = (Study)em.find(Study.class,studyId);
        studyRequest.setStudy(study);
        
        StudyFile studyFile = (StudyFile)em.find(StudyFile.class,fileId); 
        studyRequest.setStudyFile(studyFile); 
        study.getStudyRequests().add(studyRequest);
      
        em.persist(studyRequest);
    }
}
