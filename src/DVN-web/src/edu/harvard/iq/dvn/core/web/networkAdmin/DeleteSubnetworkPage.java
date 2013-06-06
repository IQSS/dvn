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

import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

@Named("DeleteSubnetworkPage")
@ViewScoped
public class DeleteSubnetworkPage extends VDCBaseBean implements Serializable {

    private static final Logger logger = Logger.getLogger(DeleteSubnetworkPage.class.getCanonicalName());
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
    private Long deleteId;
    private String subnetworkUrlAlias;

    public Long getDeleteId() {
        return deleteId;
    }

    public void setDeleteId(Long deleteId) {
        this.deleteId = deleteId;
    }

    public String getSubnetworkUrlAlias() {
        return subnetworkUrlAlias;
    }

    public void preRenderView() {
        VDCNetwork subnetwork = vdcNetworkService.findById(deleteId);
        subnetworkUrlAlias = subnetwork.getUrlAlias();
    }

    public String delete_action() {
        VDCNetwork subnetworkToDelete = vdcNetworkService.findById(deleteId);
        String name = subnetworkToDelete.getName();
        String id = subnetworkToDelete.getId().toString();
        logger.info("deleting subnetwork \"" + name + "\" (id " + id + ")");
        vdcNetworkService.deleteSubnetwork(subnetworkToDelete);
        getVDCRenderBean().getFlash().put("successMessage", "Successfully deleted subnetwork \"" + name + "\"");
        return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true&tab=subnetworks";
    }

    public String cancel() {
        return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true&tab=subnetworks";
    }
}
