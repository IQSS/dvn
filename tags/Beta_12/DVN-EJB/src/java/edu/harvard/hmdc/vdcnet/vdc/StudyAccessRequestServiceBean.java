/*
 * StudyAccessRequestServiceBean.java
 *
 * Created on January 30, 2007, 11:29 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyAccessRequest;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateless
public class StudyAccessRequestServiceBean implements edu.harvard.hmdc.vdcnet.vdc.StudyAccessRequestServiceLocal {
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
