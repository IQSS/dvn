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
 * HarvestSitesPage.java
 *
 * Created on April 5, 2007, 10:20 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.admin;

import edu.harvard.iq.dvn.core.study.Template;
import edu.harvard.iq.dvn.core.study.TemplateServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.util.List;
import javax.ejb.EJB;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import com.icesoft.faces.component.ext.HtmlDataTable;
import java.util.HashMap;
import java.util.Map;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author Ellen Kraffmiller
 */
@Named("ManageTemplatesPage")
@ViewScoped
public class ManageTemplatesPage extends VDCBaseBean implements java.io.Serializable  {
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB VDCServiceLocal vdcService;
    @EJB TemplateServiceLocal templateService;

    
    /** Creates a new instance of HarvestSitesPage */
    public ManageTemplatesPage() {
    }
    
    public void init(){
        super.init();
        
        templateList = vdcNetworkService.getNetworkTemplates(); 

        if (getVDCRequestBean().getCurrentVDC() != null){
           templateList.addAll(vdcService.getOrderedTemplates(getVDCRequestBean().getCurrentVDCId()));
           defaultTemplateId= getVDCRequestBean().getCurrentVDC().getDefaultTemplate().getId(); 
           
        } else {      
           defaultTemplateId = getVDCRequestBean().getVdcNetwork().getDefaultTemplate().getId();
        }
        
        // whether a template is being used deermines if it can be removed or not
        for (Template template : templateList) {
            templateInUseMap.put( template.getId(), templateService.isTemplateUsed(template.getId()) );
        }
    }
    
    public String updateDefaultAction(Long templateId) {      
        if (getVDCRequestBean().getCurrentVDC() == null) {
            vdcNetworkService.updateDefaultTemplate(templateId);
        } else {
            vdcService.updateDefaultTemplate(getVDCRequestBean().getCurrentVDCId(),templateId);
        }
        
        defaultTemplateId = templateId;
        return "";
    }
    
    
   
    private List<Template> templateList;

    public List<Template> getTemplateList() {       
        return templateList;        
    }
    
    private Map<Long,Boolean> templateInUseMap = new HashMap();
    
    public Map<Long,Boolean> getTemplateInUseMap() {       
        return templateInUseMap;        
    }   
    
    Long defaultTemplateId;

    public Long getDefaultTemplateId() {
        return defaultTemplateId;
    }

    public void setDefaultTemplateId(Long defaultTemplateId) {
        this.defaultTemplateId = defaultTemplateId;
    }
}
