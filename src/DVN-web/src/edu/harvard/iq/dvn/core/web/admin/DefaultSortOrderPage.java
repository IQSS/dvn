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
package edu.harvard.iq.dvn.core.web.admin;

import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

@ViewScoped
@Named("DefaultSortOrderPage")
public class DefaultSortOrderPage extends VDCBaseBean implements java.io.Serializable  {
     @EJB VDCServiceLocal vdcService;
     private String defaultSortOrder = "";

    public String getDefaultSortOrder() {
        return defaultSortOrder;
    }

    public void setDefaultSortOrder(String defaultSortOrder) {
        this.defaultSortOrder = defaultSortOrder;
    }

    public void init() {
        super.init();
        defaultSortOrder = getVDCRequestBean().getCurrentVDC().getDefaultSortOrder();

    }

    public String cancel(){
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        getVDCRequestBean().setCurrentVDC(thisVDC);
        String    forwardPage="/admin/OptionsPage?faces-redirect=true" + getContextSuffix();
        return forwardPage;
    }

    public String save(){
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();

        thisVDC.setDefaultSortOrder(defaultSortOrder);
        vdcService.edit(thisVDC);

        getVDCRequestBean().setCurrentVDC(thisVDC);
        String    forwardPage="/admin/OptionsPage?faces-redirect=true" + getContextSuffix();
        getVDCRenderBean().getFlash().put("successMessage","Successfully updated default sort order.");
        return forwardPage;
        
    }

}
