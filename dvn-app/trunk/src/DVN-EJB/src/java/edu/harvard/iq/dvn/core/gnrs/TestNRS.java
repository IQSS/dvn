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
 * TestNRS.java
 *
 * Created on February 14, 2007, 2:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.gnrs;

import java.util.Vector;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 *
 * @author roberttreacy
 */
public class TestNRS extends NRS{
    //@PersistenceUnit(unitName="VDCNet-ejbPU") EntityManagerFactory emf;
    @PersistenceContext(unitName="VDCNet-ejbPU") EntityManager em;
    //EntityManager em;
    
    /** Creates a new instance of TestNRS */
    public TestNRS() {
        this.setProtocol("hdl");
        this.setAuthority("1902.1");
    }
    
    public TestNRS(String protocol, String authority) {
        this.setProtocol(protocol);
        this.setAuthority(authority);
    }

    
    private String generateStudyIdSequence(String protocol, String authority) {
     //   Date now = new Date();
     //   return ""+now.getTime();
     //   return em.createNamedQuery("getStudyIdSequence").getSingleResult().toString();
        String studyId=null;
        //em = emf.createEntityManager();
        do {
            Vector result = (Vector)em.createNativeQuery("select nextval('studyid_seq')").getSingleResult();
            studyId = result.get(0).toString();
        } while (!isUniqueStudyId(studyId, protocol, authority));
        //em.close();
        return studyId;
        
    }
    
    /**
     *  Check that a studyId entered by the user is unique (not currently used for any other study in this Dataverse Network)
     */
   private boolean isUniqueStudyId(String userStudyId, String protocol,String authority) {
       String query = "SELECT s FROM Study s WHERE s.studyId = '" + userStudyId +"'";
       query += " and s.protocol ='"+protocol+"'";
       query += " and s.authority = '"+authority+"'";
       //em = emf.createEntityManager();
       boolean u = em.createQuery(query).getResultList().size()==0;
       //em.close();
       return u;
    }

    public String getNewObjectId() {
        return generateStudyIdSequence(getProtocol(), getAuthority());
    }
}
