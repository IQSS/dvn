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
 * AddLinkPage.java
 *
 * Created on December 3, 2006, 10:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.collection;

import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCCollection;
import edu.harvard.iq.dvn.core.vdc.VDCCollectionServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;

import javax.faces.model.SelectItem;


public class AddLinkPage extends VDCBaseBean implements java.io.Serializable  {
    @EJB VDCServiceLocal vdcService;
    @EJB VDCCollectionServiceLocal vdcCollectionService;


    
    /** Creates a new instance of AddLinkPage */
    public AddLinkPage() {
    }
 

    private Long dvId;
    private Long collId;

    public Long getDvId() {
        return dvId;
    }

    public void setDvId(Long dvId) {
        this.dvId = dvId;
    }

    public Long getCollId() {
        return collId;
    }

    public void setCollId(Long collId) {
        this.collId = collId;
    }



    public List<SelectItem> getDvItems() {
        List dvSelectItems = new ArrayList<SelectItem>();
        for (VDC vdc : vdcService.findAllPublic()) {
            if (!vdc.getId().equals(getVDCRequestBean().getCurrentVDC().getId()) ) {
                dvSelectItems.add(new SelectItem(vdc.getId(), vdc.getName()));
            }
        }

        return dvSelectItems;
    }

    public List<SelectItem> getCollectionItems() {
        List collSelectItems = new ArrayList<SelectItem>();
                    
        if (dvId != null) {
            List linkedColls = getVDCRequestBean().getCurrentVDC().getLinkedCollections();
            List<VDCCollection> collList = vdcCollectionService.getCollectionList(vdcService.find(dvId));
            for (VDCCollection coll : collList) {
                boolean disabled = linkedColls.contains(coll);
                collSelectItems.add(new SelectItem(coll.getId(), coll.getName(), null, disabled));
            }
        }
        
        return collSelectItems;
    }    
  

    public String save_action() {
        if (collId != null) {
            VDC vdc = getVDCRequestBean().getCurrentVDC();
            VDCCollection coll = vdcCollectionService.find(collId);
            vdc.getLinkedCollections().add( coll );
            coll.getLinkedVDCs().add(vdc);

            vdcCollectionService.edit(coll);
            vdcService.edit(vdc);
        }
        
        return "manageCollections";
    }
       
        
}
