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
import java.util.ArrayList;
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

        if (getVDCRequestBean().getCurrentVDC() != null){
           templateList = templateService.getEnabledNetworkTemplates();
           templateList.addAll(templateService.getVDCTemplates(getVDCRequestBean().getCurrentVDCId()));
           defaultTemplateId = getVDCRequestBean().getCurrentVDC().getDefaultTemplate().getId(); 
           
        } else {
           templateList = templateService.getNetworkTemplates();
           defaultTemplateId = getVDCRequestBean().getVdcNetwork().getDefaultTemplate().getId();
        }
        
        // whether a template is being used determines if it can be removed or not (initialized here, so the page doesn't call thew service bean multiple times)
        for (Template template : templateList) {
            if (templateService.isTemplateUsed(template.getId() )) 
            { 
                templateInUseList.add(template.getId());
            }
            
            if (templateService.isTemplateUsedAsVDCDefault(template.getId() )) 
            { 
                templateinsUseAsVDCDefaultList.add(template.getId());
            }
        }
    }
    
    public String updateDefaultAction(Long templateId) {
        // first, verify that the template has not been disabled
        if (templateService.getTemplate(templateId).isEnabled()) {
            if (getVDCRequestBean().getCurrentVDC() == null) {
                vdcNetworkService.updateDefaultTemplate(templateId);
            } else {
                vdcService.updateDefaultTemplate(getVDCRequestBean().getCurrentVDCId(),templateId);
            }

            defaultTemplateId = templateId;
        } else {
            // add flash message
            getVDCRenderBean().getFlash().put("warningMessage","The template you are trying to make Default was disabled by another user. Please reload this page to update.");
            
        }
        return "";
    }
    
        
    public void updateEnabledAction(Long templateId) {
        Template template = null;
        int templateIndex = 0;
        
        // get the the template (and the index, so we can refrsh afterwards)
        for (int i = 0; i < templateList.size(); i++) {
            Template t = templateList.get(i);
            if (t.getId().equals(templateId)) {
                templateIndex = i;
                template = t;
                break;
            }   
        }        
        
        // first, check if we are trying disable a template and verify it has not been made a default
        if (template.isEnabled()) {
            // network level template
            if (getVDCRequestBean().getCurrentVDC() == null) {
                if ( vdcNetworkService.find().getDefaultTemplate().equals(template) || templateService.isTemplateUsedAsVDCDefault(template.getId()) ) {
                    getVDCRenderBean().getFlash().put("warningMessage","The template you are trying to disable was made a default template by another user. Please reload this page to update.");
                    return;
                }
            // vdc level template    
            } else if (vdcService.findById(getVDCRequestBean().getCurrentVDCId()).getDefaultTemplate().equals(template)) {
                getVDCRenderBean().getFlash().put("warningMessage","The template you are trying to disable was made a default template by another user. Please reload this page to update.");
                return;
            }
        }   else {

            for(Template testTemplate : templateList){
                if (testTemplate.isEnabled() && !testTemplate.equals(template) && testTemplate.getName().equals(template.getName())){
                    getVDCRenderBean().getFlash().put("warningMessage","The template you are trying to enable has the same name as another enabled template. Please edit the name and re-try enabling.");
                    return;
                }
            }
        }
        
        template.setEnabled(!template.isEnabled());
        templateService.updateTemplate(template);

        // now update the template in the list (this is needed or a second attempt to change will result in an optimistic lock)
        templateList.set(templateIndex, templateService.getTemplate(template.getId()));
    }
     
    
    
   
    private List<Template> templateList;

    public List<Template> getTemplateList() {       
        return templateList;        
    }
    
    Long defaultTemplateId;

    public Long getDefaultTemplateId() {
        return defaultTemplateId;
    }

    public void setDefaultTemplateId(Long defaultTemplateId) {
        this.defaultTemplateId = defaultTemplateId;
    }    
    
    private List<Long> templateInUseList = new ArrayList();
    private List<Long> templateinsUseAsVDCDefaultList = new ArrayList();  
    

    // helper methods for display
    
    public boolean isDefault(Long templateId) {
        return defaultTemplateId.equals(templateId);
    }
    
    public boolean isInUse(Long templateId) {
        return templateInUseList.contains(templateId);
    }

    public boolean isVDCDefault(Long templateId) {
        return templateinsUseAsVDCDefaultList.contains(templateId);
    }
}
