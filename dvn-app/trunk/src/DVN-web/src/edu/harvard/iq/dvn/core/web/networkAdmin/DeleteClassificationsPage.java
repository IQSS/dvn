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

        return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true&tab=classifications";
    }
    
    public String cancel() {
        return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true&tab=classifications";
    }



    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }


}
