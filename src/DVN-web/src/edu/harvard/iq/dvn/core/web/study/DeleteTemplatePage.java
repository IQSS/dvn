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
 * StudyPage.java
 *
 * Created on September 5, 2006, 4:25 PM
 *
 */
package edu.harvard.iq.dvn.core.web.study;


import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.Template;
import edu.harvard.iq.dvn.core.study.TemplateServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
@Named("DeleteTemplatePage")
@ViewScoped
@EJB(name="editStudy", beanInterface=edu.harvard.iq.dvn.core.study.EditStudyService.class)
public class DeleteTemplatePage extends VDCBaseBean implements java.io.Serializable  {
    @EJB TemplateServiceLocal templateService;
    
    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public DeleteTemplatePage() {
        
    }
    /**
     * <p>Callback method that is called whenever a page is navigated to,
     * either directly via a URL, or indirectly via page navigation.
     * Customize this method to acquire resources that will be needed
     * for event handlers and lifecycle methods, whether or not this
     * page is performing post back processing.</p>
     *
     * <p>Note that, if the current request is a postback, the property
     * values of the components do <strong>not</strong> represent any
     * values submitted with this request.  Instead, they represent the
     * property values that were saved for this view when it was rendered.</p>
     */
    
    public void init() {
        super.init();
        Template template = templateService.getTemplate(templateId);
        templateName = template.getName();

    }
       
    
    /**
     * Holds value of property study.
     */
    private Study study;
    
    /**
     * Getter for property study.
     * @return Value of property study.
     */
    public Study getStudy() {
        
        return this.study;
    }
    
    /**
     * Setter for property study.
     * @param study New value of property study.
     */
    public void setStudy(Study study) {
        System.out.println("Set Study is called");
        this.study = study;
    }
    
    
    public String delete() {
        templateService.deleteTemplate(templateId);
        getVDCRenderBean().getFlash().put("successMessage","Successfully deleted template.");
        if (getVDCRequestBean().getCurrentVDC() != null) {
            return "/admin/OptionsPage?faces-redirect=true" + getContextSuffix();
        } else {
            return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true&tab=templates";
        } 
    }
    
    public String cancel() {
        
        if (getVDCRequestBean().getCurrentVDC() != null) {
            return "/admin/OptionsPage?faces-redirect=true" + getContextSuffix();
        } else {
            return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true&tab=templates";
        } 
    }

   private Long templateId;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
   
    String templateName;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() {
    }
    
}

