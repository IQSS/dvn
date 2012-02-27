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
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 * @author wbossons
 */
@ViewScoped
@Named("DeleteClassificationsPage")
public class DeleteClassificationsPage extends VDCBaseBean implements Serializable {
    
    public DeleteClassificationsPage() {
    }
    
    @EJB VDCServiceLocal vdcService;
    @EJB VDCGroupServiceLocal vdcGroupService;


    private String classificationName;
    private Long classId;

    public String getClassificationName() {
        return classificationName;
    }

    public void setClassificationName(String classificationName) {
        this.classificationName = classificationName;
    }
  
    
    public void init() {
        super.init();
        VDCGroup vdcGroup = vdcGroupService.findById(classId);
        classificationName = vdcGroup.getName();
    }
   
    public String delete() {
        try {
            VDCGroup vdcgroup = vdcGroupService.findById(classId);
            vdcGroupService.removeVdcGroup(vdcgroup);
            getVDCRenderBean().getFlash().put("successMessage", "Successfully deleted classification.");
            //this.vdcGroupService.updateGroupOrder(order); // TBD
        } catch (Exception e) {
            getVDCRenderBean().getFlash().put("warningMessage", "A problem occurred trying to delete this classification.");
            e.printStackTrace();

        }

        return "/networkAdmin/ManageClassificationsPage?faces-redirect=true";
    }
    
    public String cancel() {
        return "/networkAdmin/ManageClassificationsPage?faces-redirect=true";
    }



    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }


}
