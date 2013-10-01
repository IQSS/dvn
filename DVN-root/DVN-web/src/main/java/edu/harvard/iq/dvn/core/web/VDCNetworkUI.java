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
package edu.harvard.iq.dvn.core.web;

import edu.harvard.iq.dvn.core.vdc.VDCNetwork;

/**
 *
 * @author skraffmiller
 */
public class VDCNetworkUI {
    private VDCNetwork vdcNetwork;
    private Long vdcCount;
    private Boolean defaultTemplateSelected;

    public Long getVdcCount() {
        return vdcCount;
    }

    public void setVdcCount(Long vdcCount) {
        this.vdcCount = vdcCount;
    }

    public VDCNetwork getVdcNetwork() {
        return vdcNetwork;
    }

    public void setVdcNetwork(VDCNetwork vdcNetwork) {
        this.vdcNetwork = vdcNetwork;
    }
    
        public Boolean getDefaultTemplateSelected() {
        return defaultTemplateSelected;
    }

    public void setDefaultTemplateSelected(Boolean defaultTemplateSelected) {
        this.defaultTemplateSelected = defaultTemplateSelected;
    }   
}

