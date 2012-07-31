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
    
    public TwitterCredentials getTwitterCredentials();     

    public void addTermsOfUse(TermsOfUse tou);  

    VDCNetwork find(Object pk);
    VDCNetwork find();
    
    Long getTotalDataverses(boolean isreleased);
    
    Long getTotalStudies(boolean isreleased);
    
    Long getTotalFiles(boolean isreleased);
    
    void updateDefaultDisplayNumber(VDCNetwork vdcnetwork);

    public void updateDefaultTemplate(Long templateId);   
    
}
