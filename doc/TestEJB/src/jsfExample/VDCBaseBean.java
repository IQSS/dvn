/*
 * VDCBaseBean.java
 *
 * Created on September 12, 2006, 4:36 PM
 *
 */

package jsfExample;

/**
 *
 * @author Ellen Kraffmiller
 */
public class VDCBaseBean extends  com.sun.rave.web.ui.appbase.FacesBean {
    
    /**
     * Creates a new instance of VDCBaseBean
     */
    public VDCBaseBean() {
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

    protected VDCRequestBean getVDCRequestBean() {
        return (VDCRequestBean)getBean("VDCRequestBean");
        
    }
    
}
