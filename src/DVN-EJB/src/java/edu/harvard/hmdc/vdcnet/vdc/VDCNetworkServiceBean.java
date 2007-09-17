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
 * VDCNetworkServiceBean.java
 *
 * Created on October 26, 2006, 11:56 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class VDCNetworkServiceBean implements VDCNetworkServiceLocal {

    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    
    /**
     * Creates a new instance of VDCNetworkServiceBean
     */
    public VDCNetworkServiceBean() {
    }

    public void create(VDCNetwork vDCNetwork) {
        em.persist(vDCNetwork);
    }

    public void edit(VDCNetwork vDCNetwork) {
        em.merge(vDCNetwork);
    }

    public void destroy(VDCNetwork vDCNetwork) {
        em.merge(vDCNetwork);
        em.remove(vDCNetwork);
    }

    public VDCNetwork find(Object pk) {
        return (VDCNetwork) em.find(VDCNetwork.class, pk);
    }

  
    
    public VDCNetwork find() {
        return (VDCNetwork) em.find(VDCNetwork.class, new Long(1));
    }
    
}
