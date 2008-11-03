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
 * VDCUI.java
 *
 * Created on March 9, 2007, 2:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.site;

import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.web.collection.CollectionUI;
import java.util.List;

/**
 *
 * @author gdurand
 */
public class VDCUI  implements java.io.Serializable {
    
    VDC vdc;
    
    /** Creates a new instance of VDCUI */
    public VDCUI(VDC vdc) {
        this.vdc = vdc;
    }
    
    public List getLinkedCollections() {
        return getLinkedCollections(false);
    }
    
    public List getLinkedCollections(boolean getHiddenCollections) {
        // getHiddenCollections no longer used
        return vdc.getLinkedCollections();
    }
    public boolean containsOnlyLinkedCollections() {
        return vdc.getLinkedCollections().size() > 0 &&
               vdc.getRootCollection().getSubCollections().size() == 0 &&
               new CollectionUI(vdc.getRootCollection()).getStudyIds().size() == 0;

    }
}
