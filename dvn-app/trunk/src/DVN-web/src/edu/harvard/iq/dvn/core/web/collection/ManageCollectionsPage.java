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

