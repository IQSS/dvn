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
import edu.harvard.iq.dvn.core.web.VDCNetworkUI;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author skraffmiller
 */

@Named("ManageSubnetworksPage")
@ViewScoped
public class ManageSubnetworksPage extends VDCBaseBean implements Serializable {
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    private List <VDCNetworkUI> vdcSubnetworks = new ArrayList();
 
    public void init() {
        super.init();
        initSubnetworks();
    }
    

    public void initSubnetworks(){
        vdcSubnetworks.clear();
        List <VDCNetwork> vdcSubnetworkList = vdcNetworkService.getVDCSubNetworks();
        for (VDCNetwork vdcNetwork: vdcSubnetworkList){
            VDCNetworkUI vdcNetworkUI = new VDCNetworkUI();
            vdcNetworkUI.setVdcNetwork(vdcNetwork);
            vdcNetworkUI.setVdcCount(new Long(vdcNetwork.getNetworkVDCs().size()));
            vdcSubnetworks.add(vdcNetworkUI);
        }
    }
            
    
    public List<VDCNetworkUI> getVdcSubnetworks() {
        return vdcSubnetworks;
    }

    public void setVdcSubnetworks(List<VDCNetworkUI> subnetworks) {
        this.vdcSubnetworks = subnetworks;
    }
    
    public Long getSubnetworkCount() {
        return new Long(vdcSubnetworks.size());
    }


}

