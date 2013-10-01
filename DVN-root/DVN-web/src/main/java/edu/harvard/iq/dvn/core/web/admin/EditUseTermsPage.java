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

            String    forwardPage="/admin/OptionsPage?faces-redirect=true" + getContextSuffix();
            getVDCRenderBean().getFlash().put("successMessage","Successfully updated file download terms of use.");
            return forwardPage;
        } else {
            return null;
        }

    }
   
    public String cancel_action(){
            return "/admin/OptionsPage?faces-redirect=true" + getContextSuffix();
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

