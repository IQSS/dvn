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
 * EditDepositUseTermsPage.java
 *
 * 
 */
package edu.harvard.iq.dvn.core.web.networkAdmin;

import edu.harvard.iq.dvn.core.web.util.ExceptionMessageWriter;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.FacesException;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class EditNetworkDepositUseTermsPage extends VDCBaseBean implements java.io.Serializable  {

    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
    private boolean termsOfUseEnabled;
    private String termsOfUse;

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
        success = true;
        if (validateTerms()) {
            // action code here
            VDCNetwork vdcNetwork = vdcNetworkService.find();
            vdcNetwork.setDepositTermsOfUse(termsOfUse);
            vdcNetwork.setDepositTermsOfUseEnabled(termsOfUseEnabled);
            vdcNetworkService.edit(vdcNetwork);
            getVDCRequestBean().setSuccessMessage("Successfully updated terms for study creation.");
            return "myNetworkOptions";
        } else {
            success = false;
            return null;
        }
    }

    public String cancel_action() {
        return "cancelNetwork";
    }
    //UTILITY METHODS
    /**
     * Holds value of property success.
     */
    private boolean success;

    /**
     * Getter for property success.
     * @return Value of property success.
     */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * Setter for property success.
     * @param success New value of property success.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
    /* validateTerms
     *
     **<p> Utility method to validate that the user entered terms of use.</p>
     * 
     *
     * @author Wendy Bossons
     */

    public boolean validateTerms() {
        String elementValue = termsOfUse;
        boolean isUseTerms = true;
        if ((elementValue == null || elementValue.equals("")) && (termsOfUseEnabled)) {
            isUseTerms = false;
            FacesMessage message = new FacesMessage("To enable this feature, you must also enter terms of use in the field below.  Please enter terms of use as either plain text or html.");
            FacesContext.getCurrentInstance().addMessage("form1:textArea1", message);
        }
        return isUseTerms;
    }

 


   
}

