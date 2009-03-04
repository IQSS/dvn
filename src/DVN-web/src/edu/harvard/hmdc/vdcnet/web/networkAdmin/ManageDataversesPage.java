/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import com.icesoft.faces.component.ext.HtmlCommandLink;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.VDCUIList;
import edu.harvard.hmdc.vdcnet.web.common.StatusMessage;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * @author wbossons
 */
public class ManageDataversesPage extends VDCBaseBean implements Serializable {

    
    @EJB
    VDCServiceLocal vdcService;
    

    private ArrayList vdcUI;
    private boolean result;
    private Long vdcUIListSize;
    private Long cid;
    private Long groupId = new Long("-1");
    private HtmlCommandLink linkDelete = new HtmlCommandLink();
    private StatusMessage msg;
    private String defaultVdcPath;
    private String statusMessage;
    private String SUCCESS_MESSAGE = new String("Success. The classifications and dataverses operation completed successfully.");
    private String FAIL_MESSAGE = new String("Problems occurred during the form submission. Please see error messages below.");
    private VDCUIList vdcUIList;

    
    public ManageDataversesPage() {
    }

    public void init() {
        super.init();
        populateVDCUIList(false);
    }

    private void populateVDCUIList(boolean isAlphaSort) {
        boolean isNewGroup = false;
        vdcUIList = new VDCUIList(groupId);
        vdcUIList.getVdcUIList();
        vdcUIListSize = new Long(String.valueOf(vdcUIList.getVdcUIList().size()));
    }

    //action methods
    public String delete_action() {
        statusMessage = SUCCESS_MESSAGE;
        result = true;
        setCid(new Long((String) linkDelete.getAttributes().get("cid")));
        try {
            VDC vdc = vdcService.findById(cid);
            vdcService.delete(cid);
        } catch (Exception e) {
            statusMessage = FAIL_MESSAGE + " " + e.getCause().toString();
            result = false;
        } finally {
            Iterator iterator = FacesContext.getCurrentInstance().getMessages("ManageDataversesPageForm");
            while (iterator.hasNext()) {
                iterator.remove();
            }
            FacesContext.getCurrentInstance().addMessage("ManageDataversesPageForm", new FacesMessage(statusMessage));
            return "result";
        }
    }

    //getters
    public Long getCid() {
        return this.cid;
    }

    public String getDefaultVdcPath() {
        return defaultVdcPath;
    }

    public HtmlCommandLink getLinkDelete() {
        return this.linkDelete;
    }
    
    public StatusMessage getMsg() {
        return msg;
    }

    public VDCUIList getVdcUIList() {
         return this.vdcUIList;
     }



    //setters
    public void setCid(Long cId) {
        this.cid = cId;
    }

    public void setLinkDelete(HtmlCommandLink linkdelete) {
        this.linkDelete = linkdelete;
    }

    public void setMsg(StatusMessage msg) {
        this.msg = msg;
    }

    public void setResult(boolean result) {
        this.result = result;
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

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() {
    }
}
