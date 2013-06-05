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
 * EditHomePanelsPage.java
 *
 * Created on October 13, 2006, 10:53 AM
 * 
 */
package edu.harvard.iq.dvn.core.web.admin;

import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.util.ExceptionMessageWriter;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
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
@Named("EditHomePanelsPage")
public class EditHomePanelsPage extends VDCBaseBean implements java.io.Serializable  {
     @EJB VDCServiceLocal vdcService;
     
     private String SUCCESS_MESSAGE = new String("Update Successful! Go to the Homepage to see your changes.");
     
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    
     /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public EditHomePanelsPage() {
        super.init();
    }
    
     /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init(){
        super.init();
        success = false;
        
        chkNetworkAnnouncements = getVDCRequestBean().getCurrentVDC().isDisplayNetworkAnnouncements();
        chkLocalAnnouncements = getVDCRequestBean().getCurrentVDC().isDisplayAnnouncements();
        localAnnouncements = getVDCRequestBean().getCurrentVDC().getAnnouncements();
        chkNewStudies = getVDCRequestBean().getCurrentVDC().isDisplayNewStudies();
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
    
    private boolean chkNetworkAnnouncements = false;
    
    public boolean isChkNetworkAnnouncements() {
        return chkNetworkAnnouncements;
    }
    
    public void setChkNetworkAnnouncements(boolean chkNetworkAnnouncements) {
        this.chkNetworkAnnouncements = chkNetworkAnnouncements;
    }
    
    private boolean chkLocalAnnouncements;
    
    public boolean isChkLocalAnnouncements() {
        return chkLocalAnnouncements;
    }
    
    public void setChkLocalAnnouncements(boolean chkLocalAnnouncements) {
        this.chkLocalAnnouncements = chkLocalAnnouncements;
    }
    
    private String localAnnouncements;
    
    public String getLocalAnnouncements() {
        return localAnnouncements;
    }
    
    public void setLocalAnnouncements(String localAnnouncements) {
        this.localAnnouncements = localAnnouncements;
    }
    
    private boolean chkNewStudies;
    
    public boolean isChkNewStudies() {
        return chkNewStudies;
    }
    
    public void setChkNewStudies(boolean chkNewStudies) {
        this.chkNewStudies = chkNewStudies;
    }
    
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
    
    public String save_action() {
        success = true;
            if (validateAnnouncementsText()) {
                // GET the VDC
                VDC vdc = vdcService.find(new Long(getVDCRequestBean().getCurrentVDC().getId()));
                vdc.setDisplayNetworkAnnouncements(chkNetworkAnnouncements);
                vdc.setDisplayAnnouncements(chkLocalAnnouncements);
                vdc.setAnnouncements(localAnnouncements);
                vdc.setDisplayNewStudies(chkNewStudies);
                vdcService.edit(vdc);
                getVDCRenderBean().getFlash().put("successMessage", "Successfully updated dataverse description.");
                 return "/admin/OptionsPage?faces-redirect=true" + getContextSuffix();
            } else {
                ExceptionMessageWriter.removeGlobalMessage(SUCCESS_MESSAGE);
                success = false;
                return "result";
            }
    }
    
    public String cancel_action(){
        if (getVDCRequestBean().getCurrentVDCId() == null)
            return "cancelNetwork";
        else
             return "/admin/OptionsPage?faces-redirect=true" + getContextSuffix();
    }
    
    //UTILITY METHODS
    
    /** validateAnnouncementsText
     *
     *
     * @author wbossons
     *
     */
    
    public boolean validateAnnouncementsText() {
        boolean isAnnouncements = true;
        String elementValue = localAnnouncements;
        if ( (elementValue == null || elementValue.equals("")) && (chkLocalAnnouncements) ) {
            isAnnouncements = false;
            success = false;
            FacesMessage message = new FacesMessage("To enable announcements, you must also enter announcements in the field below.  Please enter local announcements as either plain text or html.");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("editHomePanelsForm:localAnnouncements", message);
            context.renderResponse();
        }
        return isAnnouncements;
    }
}

