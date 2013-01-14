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
 * UserServiceBean.java
 *
 * Created on September 21, 2006, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.admin;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 */
@Stateless
public class RoleServiceBean implements RoleServiceLocal { 

    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    
    /**
     * Creates a new instance of UserServiceBean
     */
    public RoleServiceBean() {
    }


    public VDCRole findByUserVDC(Long userId, Long vdcId) {
     String query="SELECT r from VDCRole r where r.vdcUser.id = "+userId+" and r.vdc.id = "+vdcId;
       VDCRole role=null;
       try {
           role=(VDCRole)em.createQuery(query).getSingleResult();
       } catch (javax.persistence.NoResultException e) {
           // Do nothing, just return null. 
       }
       return role;
    }    
    
    public Role findByName(String name) {
       String query="SELECT r from Role r where r.name = '"+name+"'";
       Role role=null;
       role=(Role)em.createQuery(query).getSingleResult();   
       return role;
    }
    public Role findById(Long id) {
        return em.find(Role.class,id);
    }
   
}
