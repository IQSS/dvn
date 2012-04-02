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
 * EditVariablePage.java
 *
 * Created on Nov 7, 2007, 2:08:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.admin;

import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import edu.harvard.iq.dvn.core.admin.EditLockssService;
import edu.harvard.iq.dvn.core.vdc.LicenseType;
import edu.harvard.iq.dvn.core.vdc.LockssConfig;
import edu.harvard.iq.dvn.core.vdc.LockssConfig.ServerAccess;
import edu.harvard.iq.dvn.core.vdc.LockssServer;
import edu.harvard.iq.dvn.core.vdc.OAISet;
import edu.harvard.iq.dvn.core.vdc.OAISetServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import org.apache.commons.validator.routines.InetAddressValidator;

/**
 *
 * @author Ellen Kraffmiller
 */
@ViewScoped
@Named("EditLockssConfigPage")
public class EditLockssConfigPage extends VDCBaseBean implements java.io.Serializable  {
    
    @EJB VDCServiceLocal vdcService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB OAISetServiceLocal oaiService;
    @EJB EditLockssService editLockssService;
    private LockssConfig lockssConfig;
    private Long selectOAISetId;
    private Long selectLicenseId;
    private Map<Long, LicenseType> licenseTypes = new HashMap();
    private HarvestType selectHarvestType;
    private HtmlSelectOneMenu licenseMenu;
    private HtmlSelectOneMenu oaiSetMenu;
    private HtmlDataTable serverTable;
    
    public enum HarvestType { NONE, ALL, GROUP};

    public void init() {
        super.init();

        // Get lockssConfig Id from either vdc or vdcnetwork object
        Long lockssConfigId = null;
        if (getVDCRequestBean().getCurrentVDC()!=null) {
            if (getVDCRequestBean().getCurrentVDC().getLockssConfig()!=null)
            lockssConfigId = this.getVDCRequestBean().getCurrentVDC().getLockssConfig().getId();
        } else {
            LockssConfig networkConfig = vdcNetworkService.getLockssConfig();
            if (networkConfig!=null) {
                lockssConfigId = networkConfig.getId();
            }
        }

        // Based on lockssConfigId, set up stateful session bean for create/edit,
        // and set default values for form controls
        if (lockssConfigId == null) {
            editLockssService.newLockssConfig(getVDCRequestBean().getCurrentVDCId());
            lockssConfig = editLockssService.getLockssConfig();
            selectHarvestType = HarvestType.NONE;
            selectOAISetId = new Long(-1);
        } else {
            editLockssService.initLockssConfig(lockssConfigId);
            System.out.println("in init - editLockssService" + editLockssService); 
            lockssConfig = editLockssService.getLockssConfig();
            selectLicenseId = lockssConfig.getLicenseType().getId();
            selectHarvestType = HarvestType.valueOf(lockssConfig.getserverAccess().toString());
            if (getVDCRequestBean().getCurrentVDC()==null && lockssConfig.getOaiSet()!=null) {
                this.selectOAISetId = lockssConfig.getOaiSet().getId();
            }
        }

        
        for (LicenseType licenseType : editLockssService.getLicenseTypes()) {
            licenseTypes.put(licenseType.getId(), licenseType);
        }
        initCollection();

    }
    
    public List<SelectItem> getSelectOAISets() {
        List selectItems = new ArrayList<SelectItem>();
        selectItems.add(new SelectItem(null, "No Set (All Owned Studies)"));
        for(OAISet oaiSet: oaiService.findAll()) {
            // Don't show OAISets that have been created for dataverse-level Lockss Harvesting
            if (oaiSet.getLockssConfig()==null || oaiSet.getLockssConfig().getVdc()==null) {
                selectItems.add(new SelectItem(oaiSet.getId(), oaiSet.getName()));
            }
        }
        return selectItems;
    }

    public boolean isLicenseRequired() {
        return !selectHarvestType.equals(HarvestType.NONE);
    }

    public List<LicenseType> getSelectLicenseTypes() {
        List selectItems = new ArrayList<SelectItem>();
        for(LicenseType licenseType: licenseTypes.values()) {

            selectItems.add(new SelectItem(licenseType.getId(), licenseType.getName()));
        }

        return selectItems;
    }

    private boolean validateLicenseType() {

        boolean valid = true;
        if (!this.selectHarvestType.equals(HarvestType.NONE) && selectLicenseId==null) {
            valid=false;
        }
       
        if (!valid) {
            ((UIInput) licenseMenu).setValid(false);
            FacesMessage message = new FacesMessage("This field is required.");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(licenseMenu.getClientId(context), message);
        }
        return valid;
    }

    private boolean validateOaiSet() {
        boolean valid = true;
        if (!this.selectHarvestType.equals(HarvestType.NONE) && getVDCRequestBean().getCurrentVDC()==null && new Long(-1).equals(this.selectOAISetId)) {
            valid=false;
        }
        if (!valid) {
            ((UIInput) oaiSetMenu).setValid(false);
            FacesMessage message = new FacesMessage("This field is required.");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(oaiSetMenu.getClientId(context), message);
        }
        return valid;
    }
     private boolean validateLockssServers() {
        boolean valid = false;

        if (!selectHarvestType.equals(HarvestType.GROUP) && !getLockssConfig().isAllowRestricted()) {
            valid = true;
        } else {
            for (Iterator<LockssServer> it = lockssConfig.getLockssServers().iterator(); it.hasNext();) {
                LockssServer elem = it.next();
                if (elem.getIpAddress() != null && !elem.getIpAddress().trim().isEmpty()) {
                    valid = true;
                }
            }
        }

        if (!valid) {
            String errMessage;
            if (selectHarvestType.equals(HarvestType.GROUP) &&!getLockssConfig().isAllowRestricted() ) {
                errMessage = "Please specify servers that are allowed to harvest.";
            } else if (!selectHarvestType.equals(HarvestType.GROUP) && getLockssConfig().isAllowRestricted()) {
                errMessage = "Please specify servers that are allowed to access restricted data.";
            } else {
                errMessage = "Please specify servers that are allowed to harvest and access restricted data.";
            }
           
            FacesMessage message = new FacesMessage(errMessage);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(serverTable.getClientId(context), message);
            
        }
        return valid;
    }

    public  void validateIpAddress(FacesContext context,
            UIComponent toValidate,
            Object value) {

        boolean valid = false;

        valid = doValidate(value);
        

        if (!valid) {
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Invalid IP address or hostname.");
            context.addMessage(toValidate.getClientId(context), message);
        }


    }

    private static boolean doValidate(Object value) {
        boolean valid = false;
        String address = value.toString();
        // first, assume it's a domain name
        if (address.startsWith("*.")) {
            StringBuffer sb = new StringBuffer(address);
            sb.setCharAt(0, 'a');
            address = sb.toString();
        }
        valid = validateDomainName(address);


        if (!valid) {
            // Try to validate it as an ip address
            String ipAddress = value.toString();
            
            // for the purposes of validation, if the string ends in ".*",
            // replace it with dummy data for the validator.
            if (ipAddress.endsWith(".*")) {
                StringBuffer sb = new StringBuffer(ipAddress);
                sb.setCharAt(ipAddress.length() - 1, '1');
                ipAddress = sb.toString();
                // if necessary, add dummy values to the end of the string,
                // so it will pass validation.
                String[] splitStrings = ipAddress.split("\\.");
                if (splitStrings.length==2) {
                    ipAddress+=".1.1";
                } else if (splitStrings.length==3){
                    ipAddress+=".1";
                }
            }
            InetAddressValidator val = InetAddressValidator.getInstance();

            valid = val.isValid(ipAddress);
        }
        return valid;
    }

    /**
     * Cribbed from: http://pappul.blogspot.com/2006/07/validation-of-host-name-in-java.html
     * @param domainName
     * @return
     */
    private  static boolean  validateDomainName(String domainName) {
        if ((domainName == null) || (domainName.length() > 63)) {
            return false;
        }
        String domainIdentifier = "((\\p{Alnum})([-]|(\\p{Alnum}))*(\\p{Alnum}))|(\\p{Alnum})";
        String domainNameRule = "(" + domainIdentifier + ")((\\.)(" + domainIdentifier + "))*";
        String oneAlpha = "(.)*((\\p{Alpha})|[-])(.)*";

        return domainName.matches(domainNameRule) && domainName.matches(oneAlpha);
    }




    public String save() {  
        boolean validLicenseType = validateLicenseType();
        boolean validOai = validateOaiSet();
        boolean validServers = validateLockssServers();
        if (validLicenseType && validOai && validServers) {
            removeEmptyRows();
            if (selectHarvestType.equals(HarvestType.NONE)) {
                editLockssService.removeLockssConfig();
            } else {
                lockssConfig.setserverAccess(ServerAccess.valueOf(selectHarvestType.toString()));
                lockssConfig.setLicenseType(licenseTypes.get(selectLicenseId));
                editLockssService.saveChanges(selectOAISetId);
            }
            
            // refresh currentVDC object
            if (getVDCRequestBean().getCurrentVDC() != null ) {
                if (selectHarvestType.equals(HarvestType.NONE)) {
                    getVDCRequestBean().getCurrentVDC().setLockssConfig(null);
                } else {
                    getVDCRequestBean().getCurrentVDC().setLockssConfig(lockssConfig);
                }
            } // network level changes is determined at runtime by db call

            
            getVDCRenderBean().getFlash().put("successMessage", "Successfully updated LOCKSS harvest settings.");
                     
            return getReturnPage();
        } else {
                    
            return "";
        }

    }

    
    public String cancel() {
        editLockssService.cancel();
        return getReturnPage();
    }
    
    private String getReturnPage() {
        if (getVDCRequestBean().getCurrentVDC() == null) {
            return "/networkAdmin/NetworkOptionsPage?faces-redirect=true";
        } else {
            return "/admin/OptionsPage?faces-redirect=true&vdcId="+getVDCRequestBean().getCurrentVDC().getId();
        }
    }

    public HtmlSelectOneMenu getLicenseMenu() {
        return licenseMenu;
    }

    public void setLicenseMenu(HtmlSelectOneMenu licenseMenu) {
        this.licenseMenu = licenseMenu;
    }

    public HtmlSelectOneMenu getOaiSetMenu() {
        return oaiSetMenu;
    }

    public void setOaiSetMenu(HtmlSelectOneMenu oaiSetMenu) {
        this.oaiSetMenu = oaiSetMenu;
    }

    public HtmlDataTable getServerTable() {
        return serverTable;
    }

    public void setServerTable(HtmlDataTable serverTable) {
        this.serverTable = serverTable;
    }

    public Long getSelectOAISetId() {
        return selectOAISetId;
    }

    public void setSelectOAISetId(Long selectOAISetId) {
        this.selectOAISetId = selectOAISetId;
    }

    public LockssConfig getLockssConfig() {
        return lockssConfig;
    }

    public void setLockssConfig(LockssConfig lockssConfig) { 
        this.lockssConfig = lockssConfig;
    }

    public Long getSelectLicenseId() {
        return selectLicenseId;
    }

    public void setSelectLicenseId(Long selectLicenseId) {
        this.selectLicenseId = selectLicenseId;
    }

    public HarvestType getSelectHarvestType() {
        return selectHarvestType;
    }

    public void setSelectHarvestType(HarvestType selectHarvestType) {
        this.selectHarvestType = selectHarvestType;
    }



    private void initCollection() {

        if ( lockssConfig.getLockssServers()==null || lockssConfig.getLockssServers().size()==0) {
            LockssServer elem = new LockssServer();
            elem.setLockssConfig(lockssConfig);
            List<LockssServer> servers = new ArrayList();
            servers.add(elem);
            lockssConfig.setLockssServers(servers);
        }
    }
    
    public void addRow(ActionEvent ae) {
        HtmlDataTable dataTable = (HtmlDataTable) ae.getComponent().getParent().getParent();
        LockssServer newElem = new LockssServer();
        newElem.setLockssConfig(lockssConfig);
        lockssConfig.getLockssServers().add(dataTable.getRowIndex() + 1, newElem);
    }

    public void removeRow(ActionEvent ae) {
        HtmlDataTable dataTable = (HtmlDataTable)ae.getComponent().getParent().getParent();
        if (dataTable.getRowCount()>1) {
            List data = (List)dataTable.getValue();
            editLockssService.removeCollectionElement(data,dataTable.getRowIndex());
        }
    }
     private void removeEmptyRows() {
        // Remove empty collection rows
        for (Iterator<LockssServer> it = lockssConfig.getLockssServers().iterator(); it.hasNext();) {
            LockssServer elem =  it.next();
            if (elem.getIpAddress()!=null && elem.getIpAddress().trim().isEmpty()) {
                  editLockssService.removeCollectionElement(it,elem);
            }
        }
     }

     public static void main(String args[]) {
         String test = "1.*.2.3";
         System.out.println("test = "+test+", valid = "+EditLockssConfigPage.doValidate(test));
     }
}
