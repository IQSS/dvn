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
 * EditNetworkAnnouncementsPage.java
 *
 * Created on October 19, 2006, 4:40 PM
 * 
 */
package edu.harvard.iq.dvn.core.web.networkAdmin;

import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import edu.harvard.iq.dvn.core.admin.DvnTimerRemote;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.util.ExceptionMessageWriter;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class EditExportSchedulePage extends VDCBaseBean implements java.io.Serializable  {
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB (name="dvnTimer")
    DvnTimerRemote remoteTimerService;
     /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public EditExportSchedulePage() {
    }


     /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init() {
        super.init();
        exportPeriod  = new HtmlSelectOneMenu();
        success = false;
           if (!isFromPage("EditExportSchedulePage")) {
               VDCNetwork vdcNetwork = this.getVDCRequestBean().getVdcNetwork();
               exportPeriod.setValue( vdcNetwork.getExportPeriod());
               exportHourOfDay=vdcNetwork.getExportHourOfDay();
               exportDayOfWeek = vdcNetwork.getExportDayOfWeek();
           } 
    }
    /** 
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    public void preprocess() {
    }

    /** 
     * <p>Callback method that is called just before rendering takes place.
     * This method will <strong>only</strong> be called for the page that
     * will actually be rendered (and not, for example, on a page that
     * handled a postback and then navigated to a different page).  Customize
     * this method to allocate resources that will be required for rendering
     * this page.</p>
     */
    public void prerender() {
    }

    /** 
     * <p>Callback method that is called after rendering is completed for
     * this request, if <code>init()</code> was called (regardless of whether
     * or not this was the page that was actually rendered).  Customize this
     * method to release resources acquired in the <code>init()</code>,
     * <code>preprocess()</code>, or <code>prerender()</code> methods (or
     * acquired during execution of an event handler).</p>
     */
    public void destroy() {
    }
    
  
    
    public String save() {
        String msg = SUCCESS_MESSAGE;
        success = true;
        
            if (true) {
                // Get the Network
                VDCNetwork vdcnetwork = getVDCRequestBean().getVdcNetwork();
                vdcnetwork.setExportPeriod(exportSchedulePeriod);
                vdcnetwork.setExportHourOfDay(exportHourOfDay);
                if (exportDayOfWeek != null && exportDayOfWeek.intValue()==-1){
                    exportDayOfWeek = null;
                }
                vdcnetwork.setExportDayOfWeek(exportDayOfWeek);
                vdcNetworkService.edit(vdcnetwork);
                remoteTimerService.createExportTimer(vdcnetwork);
                getVDCRequestBean().setSuccessMessage("Successfully updated export schedule.");
            } else {
                ExceptionMessageWriter.removeGlobalMessage(SUCCESS_MESSAGE);
                success = false;
            }
            return "myNetworkOptions";
     
    }
    
   
    
    //UTILITY METHODS
    
    /** validateAnnouncementsText
     *
     *
     * @author wbossons
     *
     */
    
    private String SUCCESS_MESSAGE = new String("Update Successful!");
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

    private String exportSchedulePeriod;
    HtmlSelectOneMenu exportPeriod;
    Integer exportDayOfWeek;
     Integer exportHourOfDay;

    public HtmlSelectOneMenu getExportPeriod() {
        return exportPeriod;
    }

    public void setExportPeriod(HtmlSelectOneMenu exportPeriod) {
        this.exportPeriod = exportPeriod;
    }

    public Integer getExportDayOfWeek() {
        return exportDayOfWeek;
    }

    public void setExportDayOfWeek(Integer exportDayOfWeek) {
        this.exportDayOfWeek = exportDayOfWeek;
    }

    public Integer getExportHourOfDay() {
        return exportHourOfDay;
    }

    public void setExportHourOfDay(Integer exportHourOfDay) {
        this.exportHourOfDay = exportHourOfDay;
    }

    public void validateHourOfDay(FacesContext context,
            UIComponent toValidate,
            Object value) {

        boolean valid = true;


        if (exportPeriod.getLocalValue() != null && (exportPeriod.getLocalValue().equals("daily") || exportPeriod.getLocalValue().equals("weekly"))) {
            if (value == null || ((Integer) value).equals(new Integer(-1))) {
                valid = false;
            }
        }
        if (!valid) {
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage("This field is required.");
            context.addMessage(toValidate.getClientId(context), message);
        }

    }

    public void validateDayOfWeek(FacesContext context,
            UIComponent toValidate,
            Object value) {

        boolean valid = true;


        if (exportPeriod != null && exportPeriod.getLocalValue() != null && exportPeriod.getLocalValue().equals("weekly")) {
            if (value == null || ((Integer) value).equals(new Integer(-1))) {
                valid = false;
            }
        }
        if (!valid) {
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage("This field is required.");
            context.addMessage(toValidate.getClientId(context), message);
        }

    }

    public void validateExportPeriod(FacesContext context,
            UIComponent toValidate,
            Object value) {

        boolean valid = true;


        if (((String) value).equals("notSelected")) {
            exportPeriod.setValue("notSelected");
            valid = false;
        }
        if (!valid) {
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage("This field is required.");
            context.addMessage(toValidate.getClientId(context), message);
        }

    }

    /**
     * @return the exportSchedulePeriod
     */
    public String getExportSchedulePeriod() {
        return exportSchedulePeriod;
    }

    /**
     * @param exportSchedulePeriod the exportSchedulePeriod to set
     */
    public void setExportSchedulePeriod(String exportSchedulePeriod) {
        this.exportSchedulePeriod = exportSchedulePeriod;
    }

    
}

