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
 * EditBannerFooterPage.java
 *
 * Created on October 10, 2006, 10:46 AM
 */
package edu.harvard.iq.dvn.core.web.admin;

import com.icesoft.faces.component.ext.HtmlInputHidden;
import com.icesoft.faces.component.ext.HtmlInputTextarea;
import com.icesoft.faces.context.effects.JavascriptContext;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
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
@Named("EditBannerFooterPage")
public class EditBannerFooterPage extends VDCBaseBean  implements java.io.Serializable {
    @EJB VDCServiceLocal vdcService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    
    public void init() {
        super.init();
        if (this.getBanner() == null){
            setBanner( (getVDCRequestBean().getCurrentVDCId() == null) ? getVDCRequestBean().getVdcNetwork().getNetworkPageHeader(): getVDCRequestBean().getCurrentVDC().getHeader());
            setFooter( (getVDCRequestBean().getCurrentVDCId() == null) ? getVDCRequestBean().getVdcNetwork().getNetworkPageFooter(): getVDCRequestBean().getCurrentVDC().getFooter());
            
            if (getVDCRequestBean().getCurrentVDCId() != null) {
                setDisplayInFrame(getVDCRequestBean().getCurrentVDC().isDisplayInFrame());
                setParentSite(getVDCRequestBean().getCurrentVDC().getParentSite());
            }
        }
        combinedTextField.setValue(banner + footer);
    }
       
    
  
    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public EditBannerFooterPage() {
    }


    
    private String banner;
    
    public String getBanner(){
        return banner;
    }
    
    public void setBanner(String banner) {
        this.banner = banner;
    }
    
    private String footer;
    
    public String getFooter() {
        return footer;
    }
    
    public void setFooter(String footer) {
        this.footer = footer;
    }
    

    
    // ACTION METHODS
    public String save_action() {
        String forwardPage=null;
        if (getVDCRequestBean().getCurrentVDCId() == null) {
            // this is a save against the network
            VDCNetwork vdcnetwork = getVDCRequestBean().getVdcNetwork();
            vdcnetwork.setNetworkPageHeader(banner);
            vdcnetwork.setNetworkPageFooter(footer);
            vdcNetworkService.edit(vdcnetwork);
            getVDCRequestBean().getVdcNetwork().setNetworkPageHeader(banner);
            getVDCRequestBean().getVdcNetwork().setNetworkPageFooter(footer);
            forwardPage="/networkAdmin/NetworkOptionsPage?faces-redirect=true";
        } else {
            VDC vdc = vdcService.find(new Long(getVDCRequestBean().getCurrentVDC().getId()));
            vdc.setHeader(banner);
            vdc.setFooter(footer);
            vdc.setDisplayInFrame(displayInFrame);
            vdc.setParentSite(parentSite);
            vdcService.edit(vdc);
            forwardPage="/admin/OptionsPage?faces-redirect=true&vdcId="+getVDCRequestBean().getCurrentVDC().getId();
        }
        getVDCRenderBean().getFlash().put("successMessage","Successfully updated layout branding.");
        return forwardPage;
    }

    public String cancel_action(){
        if (getVDCRequestBean().getCurrentVDCId() == null) {
            setBanner(getVDCRequestBean().getVdcNetwork().getNetworkPageHeader());
            setFooter(getVDCRequestBean().getVdcNetwork().getNetworkPageFooter());
            return "/networkAdmin/NetworkOptionsPage?faces-redirect=true";
        } else {
                setBanner(getVDCRequestBean().getCurrentVDC().getHeader());
                setFooter(getVDCRequestBean().getCurrentVDC().getFooter());
                setDisplayInFrame(getVDCRequestBean().getCurrentVDC().isDisplayInFrame());
                setParentSite(getVDCRequestBean().getCurrentVDC().getParentSite());
            return "/admin/OptionsPage?faces-redirect=true&vdcId="+getVDCRequestBean().getCurrentVDC().getId();
        }
    }

    protected HtmlInputTextarea bannerTextField = new HtmlInputTextarea();

    /**
     * Get the value of bannerTextField
     *
     * @return the value of bannerTextField
     */
    public HtmlInputTextarea getBannerTextField() {
        return bannerTextField;
    }

    /**
     * Set the value of bannerTextField
     *
     * @param bannerTextField new value of bannerTextField
     */
    public void setBannerTextField(HtmlInputTextarea bannerTextField) {
        this.bannerTextField = bannerTextField;
    }

    protected HtmlInputTextarea footerTextField = new HtmlInputTextarea();

    /**
     * Get the value of footerTextarea
     *
     * @return the value of footerTextarea
     */
    public HtmlInputTextarea getFooterTextField() {
        return footerTextField;
    }

    /**
     * Set the value of footerTextarea
     *
     * @param footerTextarea new value of footerTextarea
     */
    public void setFooterTextField(HtmlInputTextarea footerTextField) {
        this.footerTextField = footerTextField;
    }

    protected HtmlInputHidden combinedTextField = new HtmlInputHidden();

    /**
     * Get the value of inputHidden
     *
     * @return the value of inputHidden
     */
    public HtmlInputHidden getCombinedTextField() {
       
        return combinedTextField;
    }

    /**
     * Set the value of inputHidden
     *
     * @param inputHidden new value of inputHidden
     */
    public void setCombinedTextField(HtmlInputHidden combinedTextField) {
        this.combinedTextField = combinedTextField;
    }

    // these are only valid for a vdc
    private boolean displayInFrame;
    private String parentSite;

    public boolean isDisplayInFrame() {
        return displayInFrame;
    }

    public void setDisplayInFrame(boolean displayInFrame) {
        this.displayInFrame = displayInFrame;
        // add javascript call on each partial submit to trigger jQuery
        JavascriptContext.addJavascriptCall(getFacesContext(), "initOpenScholarDataverse();");
    }

    public String getParentSite() {
        return parentSite;
    }

    public void setParentSite(String parentSite) {
        this.parentSite = parentSite;
    }
      
}
