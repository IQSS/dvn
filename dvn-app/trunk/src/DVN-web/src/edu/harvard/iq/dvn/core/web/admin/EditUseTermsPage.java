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
 * EditUseTermsPage.java
 *
 * Created on October 19, 2006, 11:01 AM
 * 
 */
package edu.harvard.iq.dvn.core.web.admin;

import com.icesoft.faces.component.ext.HtmlMessages;
import edu.harvard.iq.dvn.core.web.util.ExceptionMessageWriter;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
@ViewScoped
@Named("EditUseTermsPage")
public class EditUseTermsPage extends VDCBaseBean implements java.io.Serializable  {
    
    @EJB VDCServiceLocal vdcService;

    private boolean termsOfUseEnabled;
    private String termsOfUse;

    public void init() {
        super.init();
        termsOfUse = getVDCRequestBean().getCurrentVDC().getDownloadTermsOfUse();
        termsOfUseEnabled = getVDCRequestBean().getCurrentVDC().isDownloadTermsOfUseEnabled();
    }

    public boolean isTermsOfUseEnabled() {
        return termsOfUseEnabled;
    }

    public void setTermsOfUseEnabled(boolean termsOfUseEnabled) {
        this.termsOfUseEnabled = termsOfUseEnabled;
    }

    public String getTermsOfUse() {
        return termsOfUse;
    }

    public void setTermsOfUse(String termsOfUse) {
        this.termsOfUse = termsOfUse;
    }    
    
    public String save_action() {
        if (validateTerms()) {
            // action code here
            VDC vdc = vdcService.find(new Long(getVDCRequestBean().getCurrentVDC().getId()));
            vdc.setDownloadTermsOfUse(termsOfUse);
            vdc.setDownloadTermsOfUseEnabled(termsOfUseEnabled);
            vdcService.edit(vdc);

            String    forwardPage="/admin/OptionsPage?faces-redirect=true&vdcId="+getVDCRequestBean().getCurrentVDC().getId();
            getExternalContext().getFlash().put("successMessage","Successfully updated file download terms of use.");
            return forwardPage;
        } else {
            return null;
        }

    }
   
    public String cancel_action(){
            return "/admin/OptionsPage?faces-redirect=true&vdcId="+getVDCRequestBean().getCurrentVDC().getId();
    }



    private boolean validateTerms() {
        String elementValue = termsOfUse;
        boolean isUseTerms = true;
         if ( (elementValue == null || elementValue.equals("")) && (termsOfUseEnabled) ) {
            isUseTerms = false;
            FacesMessage message = new FacesMessage("This field is required.");
            FacesContext.getCurrentInstance().addMessage("form1:textArea1", message);
        }
        return isUseTerms;
    }
}

