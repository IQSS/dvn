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
package edu.harvard.iq.dvn.core.web.networkAdmin;

import com.icesoft.faces.component.ext.HtmlInputText;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.study.Template;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.web.admin.EditBannerFooterPage;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.util.CharacterValidator;
import java.io.Serializable;
import java.util.Date;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author skraffmiller
 */

@Named("EditSubnetworkPage")
@ViewScoped
public class EditSubnetworkPage extends VDCBaseBean implements Serializable  {
    @EJB VDCNetworkServiceLocal vdcNetworkService;    
    @Inject EditNetworkNamePage editNetworkNamePage;
    @Inject EditBannerFooterPage editBannerFooterPage;
    @Inject EditNetworkAnnouncementsPage editNetworkAnnouncementsPage;
    private String edit = null;
    private String alias = null;
    private String subnetworkAlias = "";
    private String subnetworkCurator = ""; 
    private boolean chkSubnetworkEnabled = false;

    private String originalAlias = "";
    private VDCNetwork originalVDCNetwork = null;
    private boolean addMode = false;

   
    public void init() {
        super.init();

    }
    
    public String getAlias() {return alias;}
    public void setAlias(String alias) {this.alias = alias;}
    public String getEdit() {return edit;}
    public void setEdit(String edit) {this.edit = edit;}
    
    
    public void preRenderView(){
        if (!getVDCRequestBean().getCurrentVdcNetworkURL().isEmpty()){   
            originalVDCNetwork = getVDCRequestBean().getCurrentVdcNetwork();
            subnetworkAlias = originalVDCNetwork.getUrlAlias();
            originalAlias = subnetworkAlias;
            subnetworkCurator = originalVDCNetwork.getCurator();
            chkSubnetworkEnabled = originalVDCNetwork.isReleased();
        } else {
            addMode = true;
        }       
    }    

    private HtmlInputText textFieldSubnetworkAlias = new HtmlInputText();

    public HtmlInputText getTextFieldSubnetworkAlias() {
        return textFieldSubnetworkAlias;
    }

    public void setTextFieldSubnetworkAlias(HtmlInputText hit) {
        this.textFieldSubnetworkAlias = hit;
    }
    
    private HtmlInputText dataverseAlias = new HtmlInputText();

    public HtmlInputText getDataverseAlias() {
        return dataverseAlias;
    }

    public void setDataverseAlias(HtmlInputText hit) {
        this.dataverseAlias = hit;
    }
    
    public String getSubnetworkAlias() {
        return subnetworkAlias;
    }

    public void setSubnetworkAlias(String subnetworkAlias) {
        this.subnetworkAlias = subnetworkAlias;
    }
    
    public String getSubnetworkCurator() {
        return subnetworkCurator;
    }

    public void setSubnetworkCurator(String subnetworkCurator) {
        this.subnetworkCurator = subnetworkCurator;
    }
    
    
    public boolean isChkSubnetworkEnabled() {
        return chkSubnetworkEnabled;
    }

    public void setChkSubnetworkEnabled(boolean chkSubnetworkEnabled) {
        this.chkSubnetworkEnabled = chkSubnetworkEnabled;
    }
    
    public String saveSubNetworkGeneralSettings_action() {
        String networkName = (String) editNetworkNamePage.getTextFieldNetworkName().getValue();
        if (networkName.isEmpty()){
            getVDCRenderBean().getFlash().put("warningMessage", "Subnetwork name is required.");
            return "/dvn" + getVDCRequestBean().getCurrentVdcNetworkURL() +  "/faces/networkAdmin/EditSubnetworkPage.xhtml?edit=true";
        }
        if (!addMode && originalVDCNetwork != null) {
            originalVDCNetwork.setName(editNetworkNamePage.getNetworkName());
            originalVDCNetwork.setDisplayAnnouncements(editNetworkAnnouncementsPage.isChkEnableNetworkAnnouncements());
            originalVDCNetwork.setAnnouncements(editNetworkAnnouncementsPage.getNetworkAnnouncements());
            originalVDCNetwork.setNetworkPageHeader(editBannerFooterPage.getBanner());
            originalVDCNetwork.setNetworkPageFooter(editBannerFooterPage.getFooter());
            originalVDCNetwork.setUrlAlias(subnetworkAlias);
            originalVDCNetwork.setCurator(subnetworkCurator);  
            originalVDCNetwork.setReleased(chkSubnetworkEnabled);
            vdcNetworkService.edit(originalVDCNetwork);
            getVDCRenderBean().getFlash().put("successMessage", "Successfully updated subnetwork.");
        } else {   
            System.out.print("why - when here??");
            VDCNetwork newVdcNetwork = new VDCNetwork();
            newVdcNetwork.setUrlAlias(subnetworkAlias);
            vdcNetworkService.create(newVdcNetwork);
            VDCNetwork createdNetwork = vdcNetworkService.findByAlias(subnetworkAlias);
            createdNetwork.setName(editNetworkNamePage.getNetworkName());
            createdNetwork.setDisplayAnnouncements(editNetworkAnnouncementsPage.isChkEnableNetworkAnnouncements());
            createdNetwork.setAnnouncements(editNetworkAnnouncementsPage.getNetworkAnnouncements());
            createdNetwork.setNetworkPageHeader(editBannerFooterPage.getBanner());
            createdNetwork.setNetworkPageFooter(editBannerFooterPage.getFooter());     
            createdNetwork.setCurator(subnetworkCurator);
            createdNetwork.setNetworkCreated(new Date());
            VDCUser creator = getVDCSessionBean().getLoginBean().getUser();
            createdNetwork.setCreator(creator);
            Template defaultTemplate = vdcNetworkService.findRootNetwork().getDefaultTemplate();
            createdNetwork.setDefaultTemplate(defaultTemplate);  
            VDCNetwork rootNetwork = vdcNetworkService.findRootNetwork();
            createdNetwork.setAllowCreateRequest(rootNetwork.isAllowCreateRequest());
            createdNetwork.setRequireDVaffiliation(rootNetwork.isRequireDVaffiliation());
            createdNetwork.setRequireDVclassification(rootNetwork.isRequireDVclassification());
            createdNetwork.setRequireDVstudiesforrelease(rootNetwork.isRequireDVstudiesforrelease());
            createdNetwork.setReleased(chkSubnetworkEnabled);
            vdcNetworkService.edit(createdNetwork);
            getVDCSessionBean().setVdcNetwork(createdNetwork);
            getVDCRenderBean().getFlash().put("successMessage", "Successfully added subnetwork.");
        }
        return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true&tab=subnetworks";
    } 
    
    public String cancel(){
        return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true&tab=subnetworks";
    }
    
    public void validateAlias(FacesContext context,
            UIComponent toValidate,
            Object value) {
        CharacterValidator charactervalidator = new CharacterValidator();
        charactervalidator.validate(context, toValidate, value);
        String alias = (String) value;
        boolean isValid = true;
        if (!originalAlias.isEmpty() && originalAlias.equals(alias) ){
            return;
        }
        VDCNetwork vdc = vdcNetworkService.findByAlias(alias);
        if (alias.equals("") || vdc != null) {
            isValid = false;
        }
        if (!isValid) {            
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage("This alias is already taken.");
            context.addMessage(toValidate.getClientId(context), message);
        }    
    }    
}

