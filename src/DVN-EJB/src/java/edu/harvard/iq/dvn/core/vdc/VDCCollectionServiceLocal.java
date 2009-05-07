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
 * VDCCollectionServiceLocal.java
 *
 * Created on September 22, 2006, 11:02 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.vdc;

import edu.harvard.iq.dvn.core.study.Study;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author roberttreacy
 */
@Local
public interface VDCCollectionServiceLocal extends java.io.Serializable  {
    void create(VDCCollection vDCCollection);

    void edit(VDCCollection vDCCollection);

    void destroy(VDCCollection vDCCollection);

    VDCCollection find(Object pk);

    List findAll();
    
    public List findSubCollections(Long id);

    java.util.List findSubCollections(Long id, boolean getAllCollections);


    public VDCCollection findByNameWithinDataverse(String name, VDC dataverse);
    
    public List<Long> getStudyIds(VDCCollection coll);
    public List<Study> getStudies(VDCCollection coll);
    
    public List<VDCCollection> getCollectionList(VDC vdc);
    public List<VDCCollection> getCollectionList(VDC vdc, VDCCollection collectionToExclude);
    public List<VDCCollection> getCollectionList(VDCCollection coll);
    
}
