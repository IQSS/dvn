/*
 * VDCSessionBean.java
 *
 * Created on October 29, 2005, 6:37 PM
 * Copyright tony
 */
package edu.harvard.hmdc.vdcnet.web.common;
import com.sun.rave.web.ui.appbase.AbstractSessionBean;
import edu.harvard.hmdc.vdcnet.admin.EditUserService;
import edu.harvard.hmdc.vdcnet.admin.GroupServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.study.EditStudyService;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * <p>Session scope data bean for your application.  Create properties
 *  here to represent cached data that should be made available across
 *  multiple HTTP requests for an individual user.</p>
 *
 * <p>An instance of this class will be created for you automatically,
 * the first time your application evaluates a value binding expression
 * or method binding expression that references a managed bean using
 * this class.</p>
 */
public class VDCSessionBean extends AbstractSessionBean {
    @EJB GroupServiceLocal groupServiceLocal; 
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;

   
    // </editor-fold>


    /** 
     * <p>Construct a new session data bean instance.</p>
     */
    public VDCSessionBean() {
        // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Initialization">
       
        // </editor-fold>
        // TODO: Add your own initialization code here (optional)
    }

    /** 
     * <p>Return a reference to the scoped data bean.</p>
     */
    protected VDCApplicationBean getVDCApplicationBean() {
        return (VDCApplicationBean)getBean("VDCApplicationBean");
    }


    /** 
     * <p>This method is called when this bean is initially added to
     * session scope.  Typically, this occurs as a result of evaluating
     * a value binding or method binding expression, which utilizes the
     * managed bean facility to instantiate this bean and store it into
     * session scope.</p>
     * 
     * <p>You may customize this method to initialize and cache data values
     * or resources that are required for the lifetime of a particular
     * user session.</p>
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
            log("SessionBean1 Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e: new FacesException(e);
        }

        // </editor-fold>
        // Perform application initialization that must complete
        // *after* managed components are initialized
        // TODO - add your own initialization code here
    }

    /** 
     * <p>This method is called when the session containing it is about to be
     * passivated.  Typically, this occurs in a distributed servlet container
     * when the session is about to be transferred to a different
     * container instance, after which the <code>activate()</code> method
     * will be called to indicate that the transfer is complete.</p>
     * 
     * <p>You may customize this method to release references to session data
     * or resources that can not be serialized with the session itself.</p>
     */
    public void passivate() {
    }

    /** 
     * <p>This method is called when the session containing it was
     * reactivated.</p>
     * 
     * <p>You may customize this method to reacquire references to session
     * data or resources that could not be serialized with the
     * session itself.</p>
     */
    public void prerender() {
    }

    /** 
     * <p>This method is called when the session containing it was
     * reactivated.</p>
     * 
     * <p>You may customize this method to reacquire references to session
     * data or resources that could not be serialized with the
     * session itself.</p>
     */
    public void activate() {
    }


    /** 
     * <p>This method is called when this bean is removed from
     * session scope.  Typically, this occurs as a result of
     * the session timing out or being terminated by the application.</p>
     * 
     * <p>You may customize this method to clean up resources allocated
     * during the execution of the <code>init()</code> method, or
     * at any later time during the lifetime of the application.</p>
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
     * Holds value of property currentStudy.
     */
    private edu.harvard.hmdc.vdcnet.study.Study currentStudy;

    /**
     * Getter for property currentStudy.
     * @return Value of property currentStudy.
     */
    public edu.harvard.hmdc.vdcnet.study.Study getCurrentStudy() {
        return this.currentStudy;
    }

    /**
     * Setter for property currentStudy.
     * @param currentStudy New value of property currentStudy.
     */
    public void setCurrentStudy(edu.harvard.hmdc.vdcnet.study.Study currentStudy) {
        this.currentStudy = currentStudy;
    }
    

    /**
     * Holds value of property studyService.
     */
    private EditStudyService studyService;

    /**
     * Getter for property statefulStudyService.
     * @return Value of property statefulStudyService.
     */
    public EditStudyService getStudyService() {
        return this.studyService;
    }

    /**
     * Setter for property statefulStudyService.
     * @param statefulStudyService New value of property statefulStudyService.
     */
    public void setStudyService(EditStudyService studyService) {
        this.studyService = studyService;
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

    private String selectedTab;

    public String getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }


    /**
     * Holds value of property userService.
     */
    private EditUserService userService;

    /**
     * Getter for property editUserService.
     * @return Value of property editUserService.
     */
    public EditUserService getUserService() {
        return this.userService;
    }

    /**
     * Setter for property editUserService.
     * @param editUserService New value of property editUserService.
     */
    public void setUserService(EditUserService userService) {
        this.userService = userService;
    }

    /**
     * Holds value of property loginBean.
     */
    private LoginBean loginBean;

    /**
     * Getter for property loginBean.
     * @return Value of property loginBean.
     */
    public LoginBean getLoginBean() {
        return this.loginBean;
    }

    /**
     * Setter for property loginBean.
     * @param loginBean New value of property loginBean.
     */
    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    private Map termsfUseMap = new HashMap();

    public Map getTermsfUseMap() {
        return termsfUseMap;
    }

    public void setTermsfUseMap(Map termsfUseMap) {
        this.termsfUseMap = termsfUseMap;
    } 
    
    public VDCUser getUser() {
        if (loginBean != null) {
            return loginBean.getUser();
        }
        
        return null;
    }

    /**
     * Holds value of property ipUserGroup.
     */
    private UserGroup ipUserGroup;

    /**
     * Getter for property ipUserGroup.
     * @return Value of property ipUserGroup.
     */
    public UserGroup getIpUserGroup() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        if (session.getAttribute("ipUserGroup") != null) {
            return (UserGroup) session.getAttribute("ipUserGroup");
        } else {
            return null;
        }
    }

    /**
     * Setter for property ipUserGroup.
     * @param ipUserGroup New value of property ipUserGroup.
     */
    public void setIpUserGroup(UserGroup ipUserGroup) {
        this.ipUserGroup = ipUserGroup;
    }
    
    
}
