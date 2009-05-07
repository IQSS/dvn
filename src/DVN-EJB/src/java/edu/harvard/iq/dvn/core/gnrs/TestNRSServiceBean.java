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
 * TestNRSServiceBean.java
 *
 * Created on February 14, 2007, 4:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.gnrs;

import java.util.Vector;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class TestNRSServiceBean implements edu.harvard.iq.dvn.core.gnrs.TestNRSServiceLocal {
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
