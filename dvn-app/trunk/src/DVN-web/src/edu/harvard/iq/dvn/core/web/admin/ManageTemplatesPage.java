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
import edu.harvard.iq.dvn.core.vdc.HarvestingDataverseServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.util.List;
import javax.ejb.EJB;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Iterator;
import com.icesoft.faces.component.ext.HtmlDataTable;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
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
    }
    
    public String updateDefaultAction() {
        Template currentTemplate = (Template)((Object[])this.templateDataTable.getRowData())[0];
        
        if (getVDCRequestBean().getCurrentVDC() == null) {
            vdcNetworkService.updateDefaultTemplate(currentTemplate.getId());
        } else {
            vdcService.updateDefaultTemplate(getVDCRequestBean().getCurrentVDCId(),currentTemplate.getId());
        }
        
        defaultTemplateId = currentTemplate.getId();
        return "";
    }
    
    
   
    private List<Template> templateList;

    public List<Template> getTemplateList() {       
        return templateList;        
    }
    
      public DataModel getTemplateData(){
         List displayFields = new ArrayList();
        for (Iterator it = templateList.iterator(); it.hasNext();) {
            Template template = (Template) it.next();
           
                Object[] row = new Object[3];
                row[0] = template;
                row[1] = getRemoveText(template);
                row[2] = template.isNetwork();
             
                displayFields.add(row);
           
        }
       return new ListDataModel(displayFields);
       
    }
    
    private String getRemoveText(Template template) {
        String removeText=null;
        if (template.getId().equals(this.defaultTemplateId)) {
            removeText = "Cannot remove - default template";
        } else if (templateService.isTemplateUsed(template.getId())){
            removeText="Cannot remove - template associated with created studies";
        } else if (getVDCRequestBean().getCurrentVDCId() != null && template.isNetwork()){
            removeText="Cannot remove - network level template";
        }
        return removeText;
    }
    
    
    Long defaultTemplateId;

    public Long getDefaultTemplateId() {
        return defaultTemplateId;
    }

    public void setDefaultTemplateId(Long defaultTemplateId) {
        this.defaultTemplateId = defaultTemplateId;
    }
    
    
    /**
     * Holds value of property dataverseDataTable.
     */
    private HtmlDataTable templateDataTable;
    
    /**
     * Getter for property dataverseDataTable.
     * @return Value of property dataverseDataTable.
     */
    public HtmlDataTable getTemplateDataTable() {
        return this.templateDataTable;
    }
    
    /**
     * Setter for property dataverseDataTable.
     * @param dataverseDataTable New value of property dataverseDataTable.
     */
    public void setTemplateDataTable(HtmlDataTable templateDataTable) {
        this.templateDataTable = templateDataTable;
    }

}
