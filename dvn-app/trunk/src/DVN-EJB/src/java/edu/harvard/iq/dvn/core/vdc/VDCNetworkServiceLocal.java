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
 * VDCNetworkServiceLocal.java
 *
 * Created on October 26, 2006, 11:56 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.vdc;

import edu.harvard.iq.dvn.core.study.Template;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author roberttreacy
 */
@Local
public interface VDCNetworkServiceLocal extends java.io.Serializable  {
    void create(VDCNetwork vDCNetwork);

    void edit(VDCNetwork vDCNetwork);

    void destroy(VDCNetwork vDCNetwork);
    
    void updateExportTimer();
    
    void createExportTimer();
    
    TermsOfUse getCurrentTermsOfUse();

    public LockssConfig getLockssConfig();
    
    public void addTermsOfUse(TermsOfUse tou);  

    VDCNetwork find(Object pk);
    VDCNetwork find();
    
    Long getTotalDataverses(boolean isreleased);
    
    Long getTotalStudies(boolean isreleased);
    
    Long getTotalFiles(boolean isreleased);
    
    void updateDefaultDisplayNumber(VDCNetwork vdcnetwork);

    public List<Template> getNetworkTemplates();
    
    
}
