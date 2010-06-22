/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.networkAdmin;

import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlMessages;
import edu.harvard.iq.dvn.core.vdc.VDCGroup;
import edu.harvard.iq.dvn.core.vdc.VDCGroupServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.io.Serializable;
import java.util.Iterator;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @author wbossons
 */
public class DeleteClassificationsPage extends VDCBaseBean implements Serializable {
    
    public DeleteClassificationsPage() {
        //
    }
    
    @EJB VDCServiceLocal vdcService;
    @EJB VDCGroupServiceLocal vdcGroupService;

    private HtmlCommandButton linkDelete = new HtmlCommandButton();
    private HtmlMessages  iceMessage = new HtmlMessages();

    private String result;
    private String resultLink;
    private String statusMessage;
    private String SUCCESS_MESSAGE   = new String("Success. The classifications and dataverses operation completed successfully.");
    private String FAIL_MESSAGE      = new String("Problems occurred during the form submission. Please see error messages below.");
    private String classificationName;

    private Long cid;

    public String getClassificationName() {
        return classificationName;
    }

    public void setClassificationName(String classificationName) {
        this.classificationName = classificationName;
    }
  
   /**
     * <p>Callback method that is called whenever a page is navigated to,
     * either directly via a URL, or indirectly via page navigation.
     * Customize this method to acquire resources that will be needed
     * for event handlers and lifecycle methods, whether or not this
     * page is performing post back processing.</p>
     *
     * <p>Note that, if the current request is a postback, the property
     * values of the components do <strong>not</strong> represent any
     * values submitted with this request.  Instead, they represent the
     * property values that were saved for this view when it was rendered.</p>
     */
    
    public void init() {
        super.init();
        if (cid != null) deleteId = cid;
        if (deleteId!=null) {
            VDCGroup vdcGroup = vdcGroupService.findById(deleteId);
            classificationName = vdcGroup.getName();
        }
    }
   
    public String delete() {
        statusMessage = SUCCESS_MESSAGE;
        result = "true";
        setDeleteId(new Long((linkDelete.getAttributes().get("deleteId").toString())));
        setClassificationName((String)linkDelete.getAttributes().get("classificationName"));
        try {
            VDCGroup vdcgroup = vdcGroupService.findById(deleteId);
            vdcGroupService.removeVdcGroup(vdcgroup);
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String referer      = (String)request.getHeader("referer");
            result       = referer.substring(referer.lastIndexOf("/")+1, referer.indexOf("."));
            result       = getFriendlyLinkName();
            resultLink   = referer;
            //this.vdcGroupService.updateGroupOrder(order); // TBD
        } catch (Exception e) {
            statusMessage = FAIL_MESSAGE + " " + e.getCause().toString();
            result = "failed";
        } finally {
            Iterator iterator = FacesContext.getCurrentInstance().getMessages("AddClassificationsPageForm");
            while (iterator.hasNext()) {
                iterator.remove();
            }
            FacesContext.getCurrentInstance().addMessage("AddClassificationsPageForm", new FacesMessage(statusMessage));
            return "success";
        }
    }
    
    public String cancel() {
        return "cancel";
    }

    
    /**
     * Holds value of property deleteId.
     */
    private Long deleteId;

    /**
     * Getter for property deleteId.
     * @return Value of property deleteId.
     */
    public Long getDeleteId() {
        return this.deleteId;
    }

    /**
     * Setter for property deleteId.
     * @param deleteId New value of property deleteId.
     */
    public void setDeleteId(Long deleteId) {
        this.deleteId = deleteId;
    }
    
   //getters

    public HtmlCommandButton getLinkDelete() {
        return linkDelete;
    }

    public String getFAIL_MESSAGE() {
        return FAIL_MESSAGE;
    }

    public String getSUCCESS_MESSAGE() {
        return SUCCESS_MESSAGE;
    }

    public HtmlMessages getIceMessage() {
        return iceMessage;
    }

    public String getResult() {
        return result;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Long getCid() {
        return cid;
    }

    private String getFriendlyLinkName() {
        if (result.indexOf("ManageClassificationsPage") != -1)
            return "Manage Classifications";
        else
            return "";
    }

    //setters

    public void setLinkDelete(HtmlCommandButton linkDelete) {
        this.linkDelete = linkDelete;
    }

    public void setFAIL_MESSAGE(String FAIL_MESSAGE) {
        this.FAIL_MESSAGE = FAIL_MESSAGE;
    }

    public void setSUCCESS_MESSAGE(String SUCCESS_MESSAGE) {
        this.SUCCESS_MESSAGE = SUCCESS_MESSAGE;
    }

    public void setIceMessage(HtmlMessages iceMessage) {
        this.iceMessage = iceMessage;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getResultLink() {
        return resultLink;
    }

    public void setResultLink(String resultLink) {
        this.resultLink = resultLink;
    }

}
