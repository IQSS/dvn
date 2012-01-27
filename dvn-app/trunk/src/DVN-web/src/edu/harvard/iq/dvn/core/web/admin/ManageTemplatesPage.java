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
    @EJB HarvestingDataverseServiceLocal harvestingDataverseService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB VDCServiceLocal vdcService;
    @EJB TemplateServiceLocal templateService;

    
    /** Creates a new instance of HarvestSitesPage */
    public ManageTemplatesPage() {
    }
    
    public void init(){

        super.init();
        
   

        if (getVDCRequestBean().getCurrentVDC() != null){
           templateList = vdcService.getOrderedTemplates(getVDCRequestBean().getCurrentVDCId());
           List<Template> networkTemplateList = vdcService.getOrderedNetworkTemplates();
           for (Template t:networkTemplateList){
               templateList.add(t);
           }
           defaultTemplateId= getVDCRequestBean().getCurrentVDC().getDefaultTemplate().getId(); 
        } else {
           templateList = vdcService.getOrderedNetworkTemplates();           
           defaultTemplateId = new Long(1);  /* network template*/
        }
        
        Template networkTemplate = vdcNetworkService.find().getDefaultTemplate();
        templateList.add(0, networkTemplate);
        networkTemplateId = networkTemplate.getId(); 
     

    }
    
    public String updateDefaultAction() {
        Template currentTemplate = (Template)((Object[])this.templateDataTable.getRowData())[0];
        vdcService.updateDefaultTemplate(getVDCRequestBean().getCurrentVDCId(),currentTemplate.getId());
        defaultTemplateId = currentTemplate.getId();
        
        return "/dvn#" + getVDCRequestBean().getCurrentVDCURL() +  "/faces/admin/ManageTemplatesPage.xhtml?faces-redirect=true?mode=4";
    }

    private List<Template> templateList;
    /**
     * Getter for property harvestSiteList.
     * @return Value of property harvestSiteList.
     */
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
        if (template.getId().equals(networkTemplateId)) {
            removeText = "Cannot remove - DVN default template";
        } else if (template.getId().equals(this.defaultTemplateId)) {
            removeText = "Cannot remove - dataverse default template";
        } else if (templateService.isTemplateUsed(template.getId())){
            removeText="Cannot remove - template associated with created studies";
        } else if (template.isNetwork()  && getVDCRequestBean().getCurrentVDCId() != null){
            removeText="Cannot remove - network level template";
        }
        return removeText;
    }
    
    Long networkTemplateId;

    public Long getNetworkTemplateId() {
        return networkTemplateId;
    }

    public void setNetworkTemplateId(Long networkTemplateId) {
        this.networkTemplateId = networkTemplateId;
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

    /**
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    public void preprocess() {
    }

    /**
     * <p>Callback method that is called just before rendering takes place.
     * This method will <strong>only</strong> be called for the page that
     * will actually be rendered (and not, for example, on a page that
     * handled a postback and then navigated to a different page).  Customize
     * this method to allocate resources that will be required for rendering
     * this page.</p>
     */
    public void prerender() {
    }

    /**
     * <p>Callback method that is called after rendering is completed for
     * this request, if <code>init()</code> was called (regardless of whether
     * or not this was the page that was actually rendered).  Customize this
     * method to release resources acquired in the <code>init()</code>,
     * <code>preprocess()</code>, or <code>prerender()</code> methods (or
     * acquired during execution of an event handler).</p>
     */
    public void destroy() {
    }

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() {
    }
    private String addTemplateAction() {
        return "templateForm";
    }
}
