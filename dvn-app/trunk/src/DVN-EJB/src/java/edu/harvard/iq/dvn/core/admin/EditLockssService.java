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
package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.vdc.LicenseType;
import edu.harvard.iq.dvn.core.vdc.LockssConfig;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Local;

/**
 * This is the business interface for EditLockssService enterprise bean.
 */
@Local
public interface EditLockssService extends java.io.Serializable { 
    public void initLockssConfig( Long lockssConfigId);
    public void newLockssConfig(Long vdcId);
    public void removeLockssConfig();
    public LockssConfig getLockssConfig();
    public void cancel();
    public void saveChanges(Long oaiSetId);
  
    public void removeCollectionElement(Collection coll, Object elem);
    public void removeCollectionElement(List list,int index);
    public void removeCollectionElement(Iterator iter, Object elem);
    public void updateOaiSet(Long oaiSetId);
    public List<LicenseType> getLicenseTypes();
    public boolean isNewLockssConfig(); 
    
   


}
