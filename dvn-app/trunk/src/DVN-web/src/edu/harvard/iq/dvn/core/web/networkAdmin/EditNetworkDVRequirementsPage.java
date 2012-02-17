/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.networkAdmin;

import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author skraffmiller
 */
@ViewScoped
@Named("EditNetworkDVRequirementsPage")
public class EditNetworkDVRequirementsPage extends VDCBaseBean implements java.io.Serializable {
@EJB VDCNetworkServiceLocal vdcNetworkService;
VDCNetwork vdcnetwork;
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
    public EditNetworkDVRequirementsPage() {
    }


     /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init() {
        super.init();
        vdcnetwork = getVDCRequestBean().getVdcNetwork();
        setRequireDvaffiliation(vdcnetwork.isRequireDVaffiliation());
        setRequireDvclassification(vdcnetwork.isRequireDVclassification());
        setRequireDvdescription(vdcnetwork.isRequireDVdescription());
        setRequireDvstudiesforrelease(vdcnetwork.isRequireDVstudiesforrelease());

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

    private boolean requireDvdescription;

    public boolean isRequireDvdescription() {
        return requireDvdescription;
    }

    public void setRequireDvdescription(boolean requireDvdescription) {
        this.requireDvdescription = requireDvdescription;
    }


    private boolean requireDvaffiliation;

    public boolean isRequireDvaffiliation() {
        return requireDvaffiliation;
    }

    public void setRequireDvaffiliation(boolean requireDvaffiliation) {
        this.requireDvaffiliation = requireDvaffiliation;
    }


    private boolean requireDvclassification;

    public boolean isRequireDvclassification() {
        return requireDvclassification;
    }

    public void setRequireDvclassification(boolean requireDvclassification) {
        this.requireDvclassification = requireDvclassification;
    }
    private boolean chkEnableNetworkAnnouncements;

    private boolean requireDvstudiesforrelease;

    public boolean isRequireDvstudiesforrelease() {
        return requireDvstudiesforrelease;
    }

    public void setRequireDvstudiesforrelease(boolean requireDvstudiesforrelease) {
        this.requireDvstudiesforrelease = requireDvstudiesforrelease;
    }


    public String save_action() {
            vdcnetwork.setRequireDVaffiliation(requireDvaffiliation);
            vdcnetwork.setRequireDVclassification(requireDvclassification);
            vdcnetwork.setRequireDVdescription(requireDvdescription);
            vdcnetwork.setRequireDVstudiesforrelease(requireDvstudiesforrelease);
            vdcNetworkService.edit(vdcnetwork);
            getExternalContext().getFlash().put("successMessage", "Successfully updated the network dataverse creation and release requirements.  ");
            return "myNetworkOptions";
    }

    public String cancel_action(){
        if (getVDCRequestBean().getCurrentVDCId() == null)
            return "cancelNetwork";
        else
            return "cancelVDC";
    }

}
