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
 * StudyPage.java
 *
 * Created on September 5, 2006, 4:25 PM
 *
 */
package edu.harvard.hmdc.vdcnet.core.web.study;


import edu.harvard.hmdc.vdcnet.core.study.EditStudyService;
import edu.harvard.hmdc.vdcnet.core.study.Study;
import edu.harvard.hmdc.vdcnet.core.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
@EJB(name="editStudy", beanInterface=edu.harvard.hmdc.vdcnet.core.study.EditStudyService.class)
public class DeleteStudyPage extends VDCBaseBean implements java.io.Serializable  {
    EditStudyService editStudyService;
    @EJB StudyServiceLocal studyService;
    
    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public DeleteStudyPage() {
        
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
         if ( isFromPage("DeleteStudyPage") ) {
            editStudyService = (EditStudyService) sessionGet(EditStudyService.class.getName());
            study = editStudyService.getStudy();
        }else {
                // we need to create the editStudyService bean
            try {
                Context ctx = new InitialContext();
                editStudyService = (EditStudyService) ctx.lookup("java:comp/env/editStudy");
            } catch(NamingException e) {
                e.printStackTrace();
                FacesContext context = FacesContext.getCurrentInstance();
                FacesMessage errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),null);
                context.addMessage(null,errMessage);
                
            }
            if (studyId != null) {
                //TODO: we need to store editStudyService by studyId, not just class name
               editStudyService.setStudy(getStudyId());
               sessionPut( EditStudyService.class.getName(), editStudyService);
               study = editStudyService.getStudy();
            } else {
                // TODO: replace this with real current VDC
                throw new IllegalArgumentException("Missing studyId in request parameters.");
                
            }
        }
        
    }
    
    
    
    /**
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    public void preprocess() {
        System.out.println("in preprocess");
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
        System.out.println("in prerender");
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
       //editStudyService.deleteStudy();
        studyService.deleteStudy(study.getId());
        return "success";
    }
    
    public String cancel() {
        this.getVDCRequestBean().setStudyId(editStudyService.getStudy().getId());
         editStudyService.cancel();
        return "viewStudy";
    }

    /**
     * Holds value of property studyId.
     */
    private Long studyId;

    /**
     * Getter for property studyId.
     * @return Value of property studyId.
     */
    public Long getStudyId() {
        return this.studyId;
    }

    /**
     * Setter for property studyId.
     * @param studyId New value of property studyId.
     */
    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }
    
}

