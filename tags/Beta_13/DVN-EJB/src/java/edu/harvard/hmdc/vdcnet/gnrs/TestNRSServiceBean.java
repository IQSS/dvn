/*
 * TestNRSServiceBean.java
 *
 * Created on February 14, 2007, 4:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.gnrs;

import java.util.Vector;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class TestNRSServiceBean implements edu.harvard.hmdc.vdcnet.gnrs.TestNRSServiceLocal {
    @PersistenceContext(unitName="VDCNet-ejbPU") EntityManager em;
    
    /** Creates a new instance of TestNRSServiceBean */
    public TestNRSServiceBean() {
    }
    
    public String getNewObjectId(String protocol, String authority){
        return generateStudyIdSequence(protocol, authority);
    }
    
    private String generateStudyIdSequence(String protocol, String authority) {
     //   Date now = new Date();
     //   return ""+now.getTime();
     //   return em.createNamedQuery("getStudyIdSequence").getSingleResult().toString();
        String studyId=null;
        do {
            Vector result = (Vector)em.createNativeQuery("select nextval('studyid_seq')").getSingleResult();
            studyId = result.get(0).toString();
        } while (!isUniqueStudyId(studyId, protocol, authority));
        em.close();
        return studyId;
        
    }
    
    /**
     *  Check that a studyId entered by the user is unique (not currently used for any other study in this Dataverse Network)
     */
   private boolean isUniqueStudyId(String userStudyId, String protocol,String authority) {
       String query = "SELECT s FROM Study s WHERE s.studyId = '" + userStudyId +"'";
       query += " and s.protocol ='"+protocol+"'";
       query += " and s.authority = '"+authority+"'";
       return em.createQuery(query).getResultList().size()==0;
    }

}
