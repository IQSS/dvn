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
 * VDCBaseBean.java
 *
 * Created on September 12, 2006, 4:36 PM
 *
 */

package edu.harvard.hmdc.vdcnet.web.common;

import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetwork;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import java.util.Iterator;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Ellen Kraffmiller
 */
public class VDCBaseBean extends  com.sun.rave.web.ui.appbase.AbstractPageBean {
    @EJB VDCServiceLocal vdcService;   
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    /**
     * Creates a new instance of VDCBaseBean
     */
    public VDCBaseBean() {
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
        // Perform initializations inherited from our superclass
        super.init();
        // Perform application initialization that must complete
        // *before* managed components are initialized
        // TODO - add your own initialiation code here

        try {
            _init();
        } catch (Exception e) {
            log("_init() Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e: new FacesException(e);
        }
        
        // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Initialization">
        // Initialize automatically managed components
        // *Note* - this logic should NOT be modified
        try {
            _init();
        } catch (Exception e) {
            log("AddCollectionsPage Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e: new FacesException(e);
        }
        // </editor-fold>
        // Perform application initialization that must complete
        // *after* managed components are initialized
        // TODO - add your own initialization code here

    }  
/*
    public void doInit() throws Exception {
        // this is the method that subclasses should override with their initiliaztion code
    }     
    */
    private void _init() throws Exception{
    }    

    
    public String getRequestParam(String name) {
        return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter(name);
    }
    
    public final boolean isFromPage(String pageNameToCheck) {
        String fromPageName =  getRequestParam("pageName");
        return (pageNameToCheck.equals(fromPageName));
    }

    public Object sessionGet(String key) {
       return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(key);      
    }
    
    public void sessionPut(String key, Object value) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(key,value);    
    }

 
    
    public void sessionRemove(String key) {
        this.getSessionMap().remove(key);
    }
    
     /** 
     * <p>Return a reference to the scoped data bean.</p>
     */
    protected VDCApplicationBean getVDCApplicationBean() {
        return (VDCApplicationBean)getBean("VDCApplication");
    }


    /** 
     * <p>Return a reference to the scoped data bean.</p>
     */
    protected VDCSessionBean getVDCSessionBean() {
        return (VDCSessionBean)getBean("VDCSession");
    }

    protected VDCRequestBean getVDCRequestBean() {
        return (VDCRequestBean)getBean("VDCRequest");
        
    }
    
/**
 * Looks for the given paramName in the request:
 * Either by the exact parameter name, or as part of a String (if this is a faces component)
 *  
 */
 public  String getParamFromRequestOrComponent(String paramName) {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return getParamFromRequestOrComponent(paramName,request);       
    }               
 
 public static String getParamFromRequestOrComponent(String paramName, HttpServletRequest request)    {
       String paramValue =request.getParameter(paramName);
        if (paramValue==null) {
            Iterator iter = request.getParameterMap().keySet().iterator();
            while (iter.hasNext()) {
                Object key = (Object) iter.next();
                if ( key instanceof String && ((String) key).indexOf(paramName) != -1 ) {
                    paramValue = request.getParameter((String)key);
                    break;
                }
            }
        }
        return paramValue;
 }
}
