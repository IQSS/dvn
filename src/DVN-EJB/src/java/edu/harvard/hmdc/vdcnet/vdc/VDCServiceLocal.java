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
 * VDCServiceLocal.java
 *
 * Created on September 21, 2006, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import java.util.List;
import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author roberttreacy
 */
@Local
public interface VDCServiceLocal {
    public void create(VDC vDC);
    
    public void create(Long userId, String name, String alias);
    
    public void createScholarDataverse(Long userId, String firstName, String lastName, String affiliation, String alias);

    public ScholarDataverse findScholarDataverseByAlias(String alias);

    public void edit(VDC vDC);

    public void destroy(VDC vDC);

    public VDC find(Object pk);
    
    public VDC findById(Long id);

    public List findAll();

    public VDC findByAlias(String alias);

    public VDC findByName(String name);

    public VDC getVDCFromRequest(HttpServletRequest request);
    
    public void addContributorRequest(Long vdcId, Long userId);

    java.util.List getLinkedCollections(VDC vdc);

    java.util.List getLinkedCollections(VDC vdc, boolean getHiddenCollections);
     
    public void delete (Long vdcId);
    
    public List findAllNonHarvesting();

    public List findVdcsNotInGroups();
    
}
