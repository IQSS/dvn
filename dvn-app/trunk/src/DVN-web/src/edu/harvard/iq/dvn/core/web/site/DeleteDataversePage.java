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
 * StudyPage.java
 *
 * Created on September 5, 2006, 4:25 PM
 * Copyright mcrosas
 */
package edu.harvard.iq.dvn.core.web.site;

import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
import com.icesoft.faces.component.ext.HtmlInputHidden;
import edu.harvard.iq.dvn.core.admin.DvnTimerRemote;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;


/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
@ViewScoped
@Named("DeleteDataversePage")
public class DeleteDataversePage extends VDCBaseBean implements java.io.Serializable  {
    @EJB VDCServiceLocal vdcService;
    @EJB (name="dvnTimer")
    DvnTimerRemote remoteTimerService;
    
    HtmlInputHidden hiddenVdcId;
    HtmlInputHidden hiddenVdcName;

    private String vdcName;
    private Long cid;
    private String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


    public String getVdcName() {
        return vdcName;
    }

    public void setVdcName(String vdcName) {
        this.vdcName = vdcName;
    }
    
    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public DeleteDataversePage() {
   
        
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
        if (deleteId != null) {
            VDC vdc = vdcService.find(deleteId);
            vdcName = vdc.getName();
        }
    }
    
    
    
    public String delete() {
        deleteId = (Long)hiddenVdcId.getValue();
        VDC vdc = vdcService.find(deleteId);
        if (vdc.isHarvestingDv()) {
            remoteTimerService.removeHarvestTimer(vdc.getHarvestingDataverse());
        }
        vdcService.delete(deleteId);

        getVDCRenderBean().getFlash().put("result", getFriendlyLinkName( from ) );
        getVDCRenderBean().getFlash().put("resultLink", from);
        return "/site/DeleteDataverseSuccessPage?faces-redirect=true";
    }
    
    public String cancel() {
      
        return from;
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

    public HtmlInputHidden getHiddenVdcId() {
        return hiddenVdcId;
    }

    public void setHiddenVdcId(HtmlInputHidden hiddenVdcId) {
        this.hiddenVdcId = hiddenVdcId;
    }

    public HtmlInputHidden getHiddenVdcName() {
        return hiddenVdcName;
    }

    public void setHiddenVdcName(HtmlInputHidden hiddenVdcName) {
        this.hiddenVdcName = hiddenVdcName;
    }


    private String getFriendlyLinkName(String result) {
        if ("manageDataverses".equals(result))
            return "Manage Dataverses";
        else if ("manageHarvesting".equals(result))
            return "Manage Harvesting";
        else
            return "";
    }
      
}

