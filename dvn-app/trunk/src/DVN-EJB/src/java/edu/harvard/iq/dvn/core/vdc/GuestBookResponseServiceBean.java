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
import edu.harvard.iq.dvn.core.web.common.LoginBean;
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
        return em.createQuery("select object(o) from GuestBookResponse as o order by o.responseTime desc").getResultList();
    }
    
    
    private GuestBookQuestionnaire findNetworkQuestionniare(){
        GuestBookQuestionnaire questionniare = new GuestBookQuestionnaire();
        String queryStr = "SELECT gbq FROM GuestBookQuestionnaire gbq WHERE gbq.vdc is null; ";
        Query query = em.createQuery(queryStr);
        List resultList = query.getResultList();

        if (resultList.size() >= 1) {
           questionniare = (GuestBookQuestionnaire) resultList.get(0);
        }
        return questionniare;
        
    }
    
    public GuestBookResponse initNetworkGuestBookResponse(Study study, StudyFile studyFile, LoginBean loginBean) {
        GuestBookResponse guestBookResponse = new GuestBookResponse();
        guestBookResponse.setGuestBookQuestionnaire(findNetworkQuestionniare());
        guestBookResponse.setStudy(study);
        guestBookResponse.setResponseTime(new Date());
        if (loginBean != null) {
            guestBookResponse.setEmail(loginBean.getUser().getEmail());
            guestBookResponse.setFirstname(loginBean.getUser().getFirstName());
            guestBookResponse.setLastname(loginBean.getUser().getLastName());
            guestBookResponse.setInstitution(loginBean.getUser().getInstitution());
            guestBookResponse.setPosition(loginBean.getUser().getPosition());
            guestBookResponse.setVdcUser(loginBean.getUser());
        } else {
            guestBookResponse.setEmail("");
            guestBookResponse.setFirstname("");
            guestBookResponse.setLastname("");
            guestBookResponse.setInstitution("");
            guestBookResponse.setPosition("");
            guestBookResponse.setVdcUser(null);

        }
        return guestBookResponse;
    }
    
    
    public GuestBookResponse findById(Long id) {
       return em.find(GuestBookResponse.class,id);
    }   
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void update(GuestBookResponse guestBookResponse) {
        em.persist(guestBookResponse);
    }
    
}
