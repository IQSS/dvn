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
