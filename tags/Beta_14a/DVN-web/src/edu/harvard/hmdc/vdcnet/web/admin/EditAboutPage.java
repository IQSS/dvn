/*
 * EditAboutPage.java
 *
 * Created on October 16, 2006, 2:25 PM
 * 
 */
package edu.harvard.hmdc.vdcnet.web.admin;

import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import edu.harvard.hmdc.vdcnet.util.ExceptionMessageWriter;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetwork;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class EditAboutPage extends VDCBaseBean {
    @EJB VDCServiceLocal vdcService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    
    private String ERROR_MESSAGE   = new String("An Error Occurred.");
    private String SUCCESS_MESSAGE = new String("Update Successful! Go to About to see your changes.");
    
    private int __placeholder;
    
     /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public EditAboutPage() {
    }
    
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init() {
        super.init();
        success = false;
    }
    
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }
       
    private HtmlCommandButton btnSave = new HtmlCommandButton();

    public HtmlCommandButton getBtnSave() {
        return btnSave;
    }

    public void setBtnSave(HtmlCommandButton hcb) {
        this.btnSave = hcb;
    }

    private HtmlCommandButton btnCancel = new HtmlCommandButton();

    public HtmlCommandButton getBtnCancel() {
        return btnCancel;
    }

    public void setBtnCancel(HtmlCommandButton hcb) {
        this.btnCancel = hcb;
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

    private String aboutThisDataverse;

    public String getAboutThisDataverse() {
        return this.aboutThisDataverse;
    }
    
    public void setAboutThisDataverse(String aboutThisDataverse) {
        this.aboutThisDataverse = aboutThisDataverse;
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
        String msg  = SUCCESS_MESSAGE;
        success = true;
        //add the success message to the queue for global messages
        ExceptionMessageWriter.addGlobalMessage(msg);
        try {
            if (getVDCRequestBean().getCurrentVDCId() == null) {
                // this is a save against the network
                VDCNetwork vdcnetwork = getVDCRequestBean().getVdcNetwork();
                vdcnetwork.setAboutThisDataverseNetwork(aboutThisDataverse);
                vdcNetworkService.edit(vdcnetwork);
            } else {
                VDC vdc = vdcService.find(new Long(getVDCRequestBean().getCurrentVDC().getId()));
                vdc.setAboutThisDataverse(aboutThisDataverse);
                vdcService.edit(vdc);
            }
        } catch (Exception e) {
            // remove the queued message from the FacesContext to avoid default FacesMessage logging
            ExceptionMessageWriter.removeGlobalMessage(SUCCESS_MESSAGE);
            ExceptionMessageWriter.addGlobalMessage(ERROR_MESSAGE);
            ExceptionMessageWriter.logException(e);
        } finally {
            return "result";
        }
    }
    
    public String cancel_action(){
        if (getVDCRequestBean().getCurrentVDCId() == null)
            return "cancelNetwork";
        else
            return "cancelVDC";
    }
    
}

