/*
 * VDCRequestBean.java
 *
 * Created on November 1, 2005, 8:42 AM
 * Copyright tony
 */
package edu.harvard.hmdc.vdcnet.web.common;

import com.sun.rave.web.ui.appbase.AbstractRequestBean;
import edu.harvard.hmdc.vdcnet.admin.PageDef;
import edu.harvard.hmdc.vdcnet.admin.PageDefServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetwork;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.StudyListing;
import java.util.Iterator;
import javax.ejb.EJB;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Request scope data bean for your application.  Create properties
 *  here to represent data that should be made available across different
 *  pages in the same HTTP request, so that the page bean classes do not
 *  have to be directly linked to each other.</p>
 *
 * <p>An instance of this class will be created for you automatically,
 * the first time your application evaluates a value binding expression
 * or method binding expression that references a managed bean using
 * this class.</p>
 */
public class VDCRequestBean extends AbstractRequestBean {
    @EJB VDCServiceLocal vdcService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;   
    @EJB PageDefServiceLocal pageDefService;
      
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    
    // </editor-fold>


    /** 
     * <p>Construct a new request data bean instance.</p>
     */
    public VDCRequestBean() {  
    }

    public String getRequestParam(String name) {
        return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter(name);
    }    
    
    /** 
     * <p>This method is called when this bean is initially added to
     * request scope.  Typically, this occurs as a result of evaluating
     * a value binding or method binding expression, which utilizes the
     * managed bean facility to instantiate this bean and store it into
     * request scope.</p>
     * 
     * <p>You may customize this method to allocate resources that are required
     * for the lifetime of the current request.</p>
     */
    public void init() {
        // Perform initializations inherited from our superclass
        super.init();
        // Perform application initialization that must complete
        // *before* managed components are initialized
        // TODO - add your own initialiation code here
        
        // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Initialization">
        // Initialize automatically managed components
        // *Note* - this logic should NOT be modified
        try {
            _init();
        } catch (Exception e) {
            log("RequestBean1 Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e: new FacesException(e);
        }
        // </editor-fold>
        // Perform application initialization that must complete
        // *after* managed components are initialized
        // TODO - add your own initialization code here

    }

    /** 
     * <p>This method is called when this bean is removed from
     * request scope.  This occurs automatically when the corresponding
     * HTTP response has been completed and sent to the client.</p>
     * 
     * <p>You may customize this method to clean up resources allocated
     * during the execution of the <code>init()</code> method, or
     * at any later time during the lifetime of the request.</p>
     */
    public void destroy() {
    }

    /** 
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }

    /** 
     * <p>Return a reference to the scoped data bean.</p>
     */
    protected VDCApplicationBean getVDCApplicationBean() {
        return (VDCApplicationBean)getBean("VDCApplicationBean");
    }


    /** 
     * <p>Return a reference to the scoped data bean.</p>
     */
    protected VDCSessionBean getVDCSessionBean() {
        return (VDCSessionBean)getBean("VDCSessionBean");
    }

    

    /**
     * Holds value of property studyId.
     */
    private Long studyId;

    /**
     * Getter for property editMode.
     * @return Value of property editMode.
     */
    public Long getStudyId() {
        return this.studyId;
    }

    /**
     * Setter for property editMode.
     * @param editMode New value of property editMode.
     */
    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }
    
    private String selectedTab;

    public String getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }    

    private boolean currentVDCinitialized;
    private VDC currentVDC;

    /**
     * Getter for property vdcId.
     * @return Value of property vdcId.
     */
    public VDC getCurrentVDC() {
        if (!currentVDCinitialized) {
            HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            setCurrentVDC( vdcService.getVDCFromRequest(req) );
            // set method also sets initialization boolean to true
        }        
        return currentVDC;
    }

   public boolean isLogoutPage() {
       HttpServletRequest httpRequest = (HttpServletRequest)this.getExternalContext().getRequest();
       PageDef pageDef = pageDefService.findByPath(httpRequest.getPathInfo());
       if (pageDef!=null && pageDef.getName().equals(PageDefServiceLocal.LOGOUT_PAGE)) {
           return true;
       }
       return false;
    }    
   
    public void setCurrentVDC(VDC currentVDC) {
        this.currentVDC = currentVDC;
        currentVDCinitialized = true;
    }

    
    public Long getCurrentVDCId() {
        if (getCurrentVDC() != null) {
            return getCurrentVDC().getId();
        } 
        
        return null;
    }
    
    public void setCurrentVDCId(Long id) {}  // dummy method since the get is just a wrapper around currentVDC

    public String getCurrentVDCURL() {
        String dataverseURL="";
        if (getCurrentVDC() != null) { 
            dataverseURL +="/dv/"+getCurrentVDC().getAlias();
        } 
        
        return dataverseURL;
    }
    
    public void setCurrentVDCURL(String dataverseURL) {}  // dummy method since the get is just a wrapper 
   
    
    /**
     * Holds value of property vdcNetwork.
     */
    private VDCNetwork vdcNetwork;

    /**
     * Getter for property vdcNetwork.
     * @return Value of property vdcNetwork.
     */
    public VDCNetwork getVdcNetwork() {
        if (vdcNetwork == null) {
            VDCNetwork network = vdcNetworkService.find(new Long(1));  // There is only one network, which will always have Id=1
            setVdcNetwork(network);              
        }

        return this.vdcNetwork;
    }

    /**
     * Setter for property vdcNetwork.
     * @param vdcNetwork New value of property vdcNetwork.
     */
    public void setVdcNetwork(VDCNetwork vdcNetwork) {
        this.vdcNetwork = vdcNetwork;
    }
    
    private StudyListing studyListing;

    public StudyListing getStudyListing() {
        return studyListing;
    }

    public void setStudyListing(StudyListing studyListing) {
        this.studyListing = studyListing;
    }    
}
