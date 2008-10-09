/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import com.icesoft.faces.component.ext.HtmlCommandLink;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroupServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 * @author wbossons
 */
public class ManageClassificationsPage extends VDCBaseBean implements Serializable {

    @EJB VDCGroupServiceLocal vdcGroupService;
    @EJB VDCServiceLocal vdcService;

     private Long cid;
     private HtmlCommandLink linkDelete = new HtmlCommandLink();
     private boolean result;
     private String statusMessage;
     private String SUCCESS_MESSAGE   = new String("Success. The classifications and dataverses operation completed successfully.");
     private String FAIL_MESSAGE      = new String("Problems occurred during the form submission. Please see error messages below.");

     public void init() {
        super.init();
        linkDelete.setValue("Delete");
     }

     public String delete_action(ActionEvent action) {
        statusMessage = SUCCESS_MESSAGE;
        result = true;
        setCid(new Long((String)linkDelete.getAttributes().get("cid")));
        System.out.println("ManageClass...Page: cid=" + cid);
        try {
            VDCGroup vdcgroup = vdcGroupService.findById(cid);
            this.vdcGroupService.removeVdcGroup(vdcgroup);
            //and reset all of the subsequent groups
            //this.vdcGroupService.updateGroupOrder(order); // TBD
        } catch (Exception e) {
            statusMessage = FAIL_MESSAGE + " " + e.getCause().toString();
            result = false;
        } finally {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(statusMessage));
            return "result";
        }
     }

     //getters
     public Long getCid() {
         return this.cid;
     }

     public HtmlCommandLink getLinkDelete() {
         return this.linkDelete;
     }

     //setters
     public void setCid(Long cId) {
         this.cid = cId;
     }

     public void setLinkDelete(HtmlCommandLink linkdelete) {
         this.linkDelete = linkdelete;
     }

}
