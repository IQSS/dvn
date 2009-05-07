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
 * VDCGroupServiceLocal.java
 *
 * Created on May 23, 2007, 9:54 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.vdc;

import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author wbossons
 */
@Local
public interface VDCGroupServiceLocal extends java.io.Serializable  {
    
    public VDCGroup findById(Long id);

    public VDCGroup findByName(String name);
    
    public java.util.Collection<VDCGroup> findAll();

    public List<VDCGroup> findByParentId(Long id);
    
    public void removeVdcGroup(VDCGroup vdcgroup);
     
    public void create(VDCGroup vdcgroup);
    
    public void updateVdcGroup(VDCGroup vdcgroup);
    
    public void updateWithVdcs(VDCGroup vdcgroup, String[] vdcs);

    public void updateWithVdcs(VDCGroup vdcgroup, Long[] vdcs);
    
    public int getNextInOrder();
    
}
