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
   Version 3.1.
*/
package edu.harvard.iq.dvn.core.vdc;

import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author skraffmiller
 */
@Stateless
public class GuestBookResponseServiceBean {
    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em; 
    
    public List<GuestBookResponse> findAll() {
        return em.createQuery("select object(o) from GuestBookResponse as o order by o.responseTime").getResultList();
    }
    
    private GuestBookResponse findByStudyFileAndUser(Study study, VDCUser vdcUser) {
        GuestBookResponse response = new GuestBookResponse();
        String queryStr = "SELECT gbr FROM GuestBookResponse gbr WHERE gbr.study.id = " + study.getId() + " and gbr.vdcUser.id = " + vdcUser.getId() + " order by gbr.responseTime DESC ";
        Query query = em.createQuery(queryStr);
        List resultList = query.getResultList();

        if (resultList.size() >= 1) {
           response = (GuestBookResponse) resultList.get(0);
        }
        return response;
    }
    
    private GuestBookResponse initGuestBookResponse(GuestBookResponse guestBookResponseSource, Study study, StudyFile studyFile) {
        GuestBookResponse guestBookResponse = new GuestBookResponse();
        guestBookResponse.setGuestBookQuestionnaire(guestBookResponseSource.getGuestBookQuestionnaire());
        guestBookResponse.setStudy(study);
        guestBookResponse.setResponseTime(new Date());

        if (study.getOwner().getGuestBookQuestionnaire().getCustomQuestions() != null && !study.getOwner().getGuestBookQuestionnaire().getCustomQuestions().isEmpty()) {
            guestBookResponse.setCustomQuestionResponses(new ArrayList());
            for (CustomQuestionResponse cq : guestBookResponseSource.getCustomQuestionResponses()) {
                CustomQuestionResponse response = new CustomQuestionResponse();
                response.setGuestBookResponse(guestBookResponse);
                response.setResponse(cq.getResponse());
                response.setStaticQuestionString(cq.getStaticQuestionString());
                guestBookResponse.getCustomQuestionResponses().add(response);
            }
        }
        guestBookResponse.setEmail(guestBookResponseSource.getEmail());
        guestBookResponse.setFirstname(guestBookResponseSource.getFirstname());
        guestBookResponse.setLastname(guestBookResponseSource.getLastname());
        guestBookResponse.setInstitution(guestBookResponseSource.getInstitution());
        guestBookResponse.setPosition(guestBookResponseSource.getPosition());
        guestBookResponse.setVdcUser(guestBookResponseSource.getVdcUser());
        guestBookResponse.setStudyFile(studyFile);
        return guestBookResponse;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addGuestBookRecord(Study study, VDCUser vdcUser, StudyFile studyFile){
        GuestBookResponse guestBookResponseSource =  findByStudyFileAndUser(study, vdcUser);
        GuestBookResponse guestBookResponseAdd = initGuestBookResponse(guestBookResponseSource, study, studyFile);
        Long timeDiff = guestBookResponseAdd.getResponseTime().getTime() - guestBookResponseSource.getResponseTime().getTime();
        if (!(guestBookResponseSource.getStudyFile().equals(studyFile))
                || timeDiff > 30000   ){
                    em.persist(guestBookResponseAdd);
        }
    }
    
    public GuestBookResponse findById(Long id) {
       return em.find(GuestBookResponse.class,id);
    }   
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void update(GuestBookResponse guestBookResponse) {
        em.persist(guestBookResponse);
    }
    
}
