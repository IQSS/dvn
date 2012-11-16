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
import java.util.List;
import javax.ejb.EJB;

import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;


@Named("AddLinkPage")
@ViewScoped
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
        
        return "/admin/OptionsPage?faces-redirect=true&tab=collections" + getNavigationVDCSuffix();
    }
    
    public String cancel_action() {
        return "/admin/OptionsPage?faces-redirect=true&tab=collections" + getNavigationVDCSuffix();
    }
       
        
}
