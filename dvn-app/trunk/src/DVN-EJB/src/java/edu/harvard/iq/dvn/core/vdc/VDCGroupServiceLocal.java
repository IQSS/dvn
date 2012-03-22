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
