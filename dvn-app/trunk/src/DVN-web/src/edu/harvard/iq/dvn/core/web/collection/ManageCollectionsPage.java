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
 * ManageCollectionsPage.java
 *
 * Created on October 2, 2006, 1:56 PM
 */
package edu.harvard.iq.dvn.core.web.collection;

import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCCollection;
import edu.harvard.iq.dvn.core.vdc.VDCCollectionServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
@Named("ManageCollectionsPage")
@ViewScoped
public class ManageCollectionsPage extends VDCBaseBean implements java.io.Serializable  {
    @EJB VDCServiceLocal vdcService;
    @EJB VDCCollectionServiceLocal collService;
    
    public List getCollections() {
        List collUIs = new ArrayList();
        // we re find the currentVDC to make sure and have any updates (when returning from AddEditCollection)
        for (VDCCollection coll : collService.getCollectionList( vdcService.find( getVDCRequestBean().getCurrentVDC().getId() ) ) ) {
            collUIs.add( new CollectionUI(coll) );
        }
        return collUIs;
    }
    
     public List getLinkedCollections() {
        List collUIs = new ArrayList();
        VDC vdc = getVDCRequestBean().getCurrentVDC();
        // we refind the currentVDC to make sure and have any updates (when returning from AddEditCollection)
        for (VDCCollection coll : vdc.getLinkedCollections() ) {
            collUIs.add( new CollectionUI(coll,vdc) );
        }
        return collUIs;         
    }

}

