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
import java.text.SimpleDateFormat;
import java.util.*;
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
    
    public List<Long> findAllIds() {
        return findAllIds(null);
    }

    public List<Long> findAllIds(Long vdcId) {
        if (vdcId == null){
           return em.createQuery("select o.id from GuestBookResponse as o order by o.responseTime desc").getResultList(); 
        } 
        return em.createQuery("select o.id from GuestBookResponse  o, Study s where o.study.id = s.id and s.owner.id = " + vdcId  +  " order by o.responseTime desc").getResultList();
    }   
    public List<Long> findAllIds30Days() {
        return findAllIds30Days(null);
    }
    
    public List<Long> findAllIds30Days(Long vdcId) {
        String beginTime;
        String endTime;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -30);
        beginTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());  // Use yesterday as default value
        cal.add(Calendar.DAY_OF_YEAR, 31);
        endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
        String queryString = "select o.id from GuestBookResponse as o  ";
        if (vdcId != null){
             queryString += ", Study s where o.study.id = s.id and s.owner.id = " + vdcId + " and "  ;
        } else {
            queryString += " where ";
        }
        queryString += " o.responseTime >='" + beginTime + "'";
        queryString += " and o.responseTime<='" + endTime + "'";
        queryString += "  order by o.responseTime desc";
        Query query = em.createQuery(queryString);

        return query.getResultList();
    }
    
    public Long findCount30Days(){
        return findCount30Days(null);
    }
    
    public Long findCount30Days(Long vdcId) {
        String beginTime;
        String endTime;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -30);
        beginTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());  // Use yesterday as default value
        cal.add(Calendar.DAY_OF_YEAR, 31);
        endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
        String queryString = "select count(o.id) from GuestBookResponse as o  ";
        if (vdcId != null){
             queryString += ", Study s where o.study_id = s.id and s.owner_id = " + vdcId + " and "  ;
        } else {
            queryString += " where ";
        }
        queryString += " o.responseTime >='" + beginTime + "'";
        queryString += " and o.responseTime<='" + endTime + "'";
        Query query = em.createNativeQuery(queryString);
        return (Long) query.getSingleResult();   
    }
    
    public Long findCountAll(){
        return findCountAll(null);
    }
    
    public Long findCountAll(Long vdcId) {
        String queryString = "";
        if (vdcId !=null){
            queryString = "select count(o.id) from GuestBookResponse  o, Study s where o.study_id = s.id and s.owner_id = " + vdcId  +  " "; 
        } else {
            queryString = "select count(o.id) from GuestBookResponse  o ";
        }

        Query query = em.createNativeQuery(queryString);
        return (Long) query.getSingleResult();    
    }

    public List<GuestBookResponse> findAllByVdc(Long vdcId) {
        return em.createQuery("select object(o) from GuestBookResponse  o, Study s where o.study.id = s.id and s.owner.id = " + vdcId  +  " order by o.responseTime desc").getResultList();
    }
    
    public List<GuestBookResponse> findAllWithin30Days() {
        return findAllWithin30Days(null);
    }

    public List<GuestBookResponse> findAllWithin30Days(Long vdcId) {
        String beginTime;
        String endTime;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -30);
        beginTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());  // Use yesterday as default value
        cal.add(Calendar.DAY_OF_YEAR, 31);
        endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
        String queryString = "select object(o) from GuestBookResponse as o  ";
        if (vdcId != null){
             queryString += ", Study s where o.study.id = s.id and s.owner.id = " + vdcId + " and "  ;
        } else {
            queryString += " where ";
        }
        queryString += " o.responseTime >='" + beginTime + "'";
        queryString += " and o.responseTime<='" + endTime + "'";
        queryString += "  order by o.responseTime desc";
        Query query = em.createQuery(queryString);

        return query.getResultList();
    }
    
    private List<Object[]> convertIntegerToLong(List<Object[]> list, int index) {
        for (Object[] item : list) {
            item[index] = new Long( (Integer) item[index]);
        }
           
        return list;
    } 
    


    private String generateTempTableString(List<Long> studyIds) {
        // first step: create the temp table with the ids

        em.createNativeQuery(" BEGIN; SET TRANSACTION READ WRITE; DROP TABLE IF EXISTS tempid; END;").executeUpdate();
        em.createNativeQuery(" BEGIN; SET TRANSACTION READ WRITE; CREATE TEMPORARY TABLE tempid (tempid integer primary key, orderby integer); END;").executeUpdate();
        em.createNativeQuery(" BEGIN; SET TRANSACTION READ WRITE; INSERT INTO tempid VALUES " + generateIDsforTempInsert(studyIds) + "; END;").executeUpdate();
        return "select tempid from tempid";
    }
    
    private String generateIDsforTempInsert(List idList) {
        int count = 0;
        StringBuffer sb = new StringBuffer();
        Iterator iter = idList.iterator();
        while (iter.hasNext()) {
            Long id = (Long) iter.next();
            sb.append("(").append(id).append(",").append(count++).append(")");
            if (iter.hasNext()) {
                sb.append(",");
            }
        }

        return sb.toString();
    }
    
    public List<Object[]> findDownloadInfoAll(List<Long> gbrIds) {  
        //this query will return multiple rows per response where the study name has changed over version
        //these multiples are filtered out by the method that actually writes the download csv,
            String varString = "(" + generateTempTableString(gbrIds) + ") ";
            String gbrDownloadQueryString = "select u.username, gbr.sessionid, "
            + " gbr.firstname, gbr.lastname, gbr.email, gbr.institution, "
            + " vdc.name, s.protocol, s.authority, m.title, fmd.label, gbr.responsetime, gbr.position, gbr.study_id, gbr.id, gbr.downloadType "
            + " from guestbookresponse gbr LEFT OUTER JOIN vdcuser u ON  " 
            + "(gbr.vdcuser_id =u.id),  " 
            + " vdc, study s, studyversion sv, metadata m, filemetadata  fmd  " 
            + "where gbr.study_id = s.id  " 
            + "and s.owner_id = vdc.id  " 
            + "and s.id = sv.study_id  " 
            + "and sv.metadata_id = m.id " 
            + "and gbr.studyfile_id = fmd.studyfile_id " 
            + "and sv.id = fmd.studyversion_id " 
            + "and sv.id = gbr.studyversion_id " 
            + " and gbr.id in " + varString
            + " group by u.username, gbr.sessionid, "
            + " gbr.firstname, gbr.lastname, gbr.email, gbr.institution, "
            + " vdc.name, s.protocol, s.authority, m.title, fmd.label, gbr.responsetime, gbr.position, gbr.study_id, gbr.id, s.id, gbr.downloadType  "    +    
            "order by s.id, gbr.id";
            System.out.print(gbrDownloadQueryString);
        Query query = em.createNativeQuery(gbrDownloadQueryString);
        
        return  convertIntegerToLong(query.getResultList(),14);
    } 
    
    public List<Object[]> findCustomResponsePerGuestbookResponse(Long gbrId) {

        String gbrCustomQuestionQueryString = "select response, cq.id "
                + " from guestbookresponse gbr, customquestion cq, customquestionresponse cqr "
                + "where gbr.guestbookquestionnaire_id = cq.guestbookquestionnaire_id "
                + " and gbr.id = cqr.guestbookresponse_id "
                + "and cq.id = cqr.customquestion_id "
                + " and cqr.guestbookresponse_id =  " + gbrId;
        Query query = em.createNativeQuery(gbrCustomQuestionQueryString);

        return convertIntegerToLong(query.getResultList(),1);
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
