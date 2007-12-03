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
package edu.harvard.hmdc.vdcnet.web.admin;

import edu.harvard.hmdc.vdcnet.util.ExceptionMessageWriter;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class EditUseTermsPage extends VDCBaseBean {
    
    @EJB VDCServiceLocal vdcService;

    private String SUCCESS_MESSAGE = new String("Update Successful!");
    
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
        String msg = SUCCESS_MESSAGE;
        success    = true;
         try {
            if (validateTerms()) {
                // action code here
                VDC vdc = vdcService.find(new Long(getVDCRequestBean().getCurrentVDC().getId()));
                vdc.setDownloadTermsOfUse(termsOfUse);
                vdc.setDownloadTermsOfUseEnabled(termsOfUseEnabled);
                vdcService.edit(vdc);
                FacesContext.getCurrentInstance().addMessage("editUseTermsPage:button1", new FacesMessage(msg));
            } else {
                ExceptionMessageWriter.removeGlobalMessage(SUCCESS_MESSAGE);
                success = false;
            }
        } catch (Exception e) {
            msg = "An error occurred: " + e.getCause().toString();
            System.out.println(msg);
        } finally {
            return null;
        }
    }
    
    public String cancel_action(){
            return "cancelVDC";
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
         if ( (elementValue == null || elementValue.equals("")) && (termsOfUseEnabled) ) {
            isUseTerms = false;
            FacesMessage message = new FacesMessage("To enable this feature, you must also enter terms of use in the field below.  Please enter terms of use as either plain text or html.");
            FacesContext.getCurrentInstance().addMessage("content:EditUseTermsPageView:form1:textArea1", message);
        }
        return isUseTerms;
    }
}

