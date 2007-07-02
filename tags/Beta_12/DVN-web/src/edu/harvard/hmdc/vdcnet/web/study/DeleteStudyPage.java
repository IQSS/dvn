/*
 * StudyPage.java
 *
 * Created on September 5, 2006, 4:25 PM
 * Copyright mcrosas
 */
package edu.harvard.hmdc.vdcnet.web.study;


import edu.harvard.hmdc.vdcnet.study.EditStudyService;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import javax.ejb.EJB;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class DeleteStudyPage extends VDCBaseBean {
    @EJB EditStudyService editStudyService;
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
            editStudyService = (EditStudyService) sessionGet(editStudyService.getClass().getName());
            study = editStudyService.getStudy();
        }else {
            if (studyId != null) {
                //TODO: we need to store editStudyService by studyId, not just class name
               editStudyService.setStudy(getStudyId());
               sessionPut( editStudyService.getClass().getName(), editStudyService);
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
        editStudyService.deleteStudy();
        getVDCSessionBean().setStudyService(null);
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

