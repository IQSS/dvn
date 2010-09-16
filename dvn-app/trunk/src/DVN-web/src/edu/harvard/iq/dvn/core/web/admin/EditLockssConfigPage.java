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
 *  along with this program; if not, see <http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
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
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

/**
 *
 * @author Ellen Kraffmiller
 */
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

        // Based on lockssConfigId, set up stateful session bean for create/edit
        if (lockssConfigId == null) {
            editLockssService.newLockssConfig(getVDCRequestBean().getCurrentVDCId());
            lockssConfig = editLockssService.getLockssConfig();
            selectHarvestType = HarvestType.NONE;
        } else {
            editLockssService.initLockssConfig(lockssConfigId);
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
           
            selectItems.add(new SelectItem(oaiSet.getId(), oaiSet.getName()));
        }
        return selectItems;
    }

    public List<LicenseType> getSelectLicenseTypes() {
        List selectItems = new ArrayList<SelectItem>();
        for(LicenseType licenseType: licenseTypes.values()) {

            selectItems.add(new SelectItem(licenseType.getId(), licenseType.getName()));
        }

        return selectItems;
    }
     
    public String save() {
        //removeEmptyRows();
        if (selectHarvestType.equals(HarvestType.NONE)) {
            editLockssService.removeLockssConfig();
        } else {
            lockssConfig.setserverAccess(ServerAccess.valueOf(selectHarvestType.toString()));
            lockssConfig.setLicenseType(licenseTypes.get(selectLicenseId));
            editLockssService.saveChanges(selectOAISetId);
        }

        

        return getReturnPage();
    }
    
    
    public String cancel() {
        editLockssService.cancel();
        return getReturnPage();
    }
    
    private String getReturnPage() {
        if (getVDCRequestBean().getCurrentVDC() == null) {
            return "myNetworkOptions";
        } else {
            return "myOptions";
        }
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

        // StudyAuthor
        for (Iterator<LockssServer> it = lockssConfig.getLockssServers().iterator(); it.hasNext();) {
            LockssServer elem =  it.next();
            if (elem.getIpAddress().trim().isEmpty()) {
                  editLockssService.removeCollectionElement(it,elem);
            }
        }
     }
}
