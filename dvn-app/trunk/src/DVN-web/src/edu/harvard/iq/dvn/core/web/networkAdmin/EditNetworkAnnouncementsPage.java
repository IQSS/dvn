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
 * EditNetworkAnnouncementsPage.java
 *
 * Created on October 19, 2006, 4:40 PM
 * 
 */
package edu.harvard.iq.dvn.core.web.networkAdmin;

import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.util.ExceptionMessageWriter;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
@ViewScoped
@Named("EditNetworkAnnouncementsPage")
public class EditNetworkAnnouncementsPage extends VDCBaseBean implements java.io.Serializable  {
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }
    
    // </editor-fold>


    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public EditNetworkAnnouncementsPage() {
    }


     /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init() {
        super.init();
            VDCNetwork vdcnetwork = getVDCRequestBean().getVdcNetwork();
            this.setChkEnableNetworkAnnouncements(vdcnetwork.isDisplayAnnouncements()) ;
            this.setNetworkAnnouncements( vdcnetwork.getAnnouncements());        
    }

    
    private String networkAnnouncements;
    
    public String getNetworkAnnouncements() {
        return networkAnnouncements;
    }
    
    public void setNetworkAnnouncements(String networkAnnouncements) {
        this.networkAnnouncements = networkAnnouncements;
    }
    
    private boolean chkEnableNetworkAnnouncements;
    
    public boolean isChkEnableNetworkAnnouncements() {
        return chkEnableNetworkAnnouncements;
    }
    
    public void setChkEnableNetworkAnnouncements(boolean chkEnableNetworkAnnouncements) {
        this.chkEnableNetworkAnnouncements = chkEnableNetworkAnnouncements;
    }
    
    public String save_action() {
        if (validateAnnouncementsText()) {
            setChkEnableNetworkAnnouncements(chkEnableNetworkAnnouncements);
            setNetworkAnnouncements(networkAnnouncements);
            // Get the Network
            VDCNetwork vdcnetwork = getVDCRequestBean().getVdcNetwork();
            vdcnetwork.setDisplayAnnouncements(this.isChkEnableNetworkAnnouncements());
            vdcnetwork.setAnnouncements(this.getNetworkAnnouncements());
            vdcNetworkService.edit(vdcnetwork);
            getVDCRenderBean().getFlash().put("successMessage", "Successfully updated the network description.  Go to the Homepage to see your changes.");
            return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true";
        }
        
        return "";

    }
    
    public String cancel_action(){
        return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true";
    }
    

    
    public boolean validateAnnouncementsText() {
        boolean isAnnouncements = true;
        String elementValue = networkAnnouncements;
        if ( (elementValue == null || elementValue.equals("")) && (isChkEnableNetworkAnnouncements()) ) {
            isAnnouncements = false;
            FacesMessage message = new FacesMessage("To enable announcements, you must also enter announcements in the field below.  Please enter announcements as either plain text or html.");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("editNetworkAnnouncementsForm:networkAnnouncements", message);
            context.renderResponse();
        }
        return isAnnouncements;
    }
}

