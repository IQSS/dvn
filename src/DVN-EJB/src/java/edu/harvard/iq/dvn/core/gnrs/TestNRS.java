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
    @PersistenceUnit(unitName="VDCNet-test") EntityManagerFactory emf;
    EntityManager em;
    
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
        em = emf.createEntityManager();
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
       em = emf.createEntityManager();
       boolean u = em.createQuery(query).getResultList().size()==0;
       em.close();
       return u;
    }

    public String getNewObjectId() {
        return generateStudyIdSequence(getProtocol(), getAuthority());
    }
}
