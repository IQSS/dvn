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

package edu.harvard.iq.dvn.core.web.common;

import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Ellen Kraffmiller
 */
public class VDCBaseBean  implements java.io.Serializable  {
    @EJB VDCServiceLocal vdcService;   
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    
    private boolean isInititalized = false;
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
    public void preRenderView() {
        if (!isInititalized) {
            init();
            isInititalized = true;
        }

    }  

    public void init() {
    }     

    
    public String getRequestParam(String name) {
        return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter(name);
    }
    
    public boolean isFromPage(String pageNameToCheck) {
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
    public static VDCApplicationBean getVDCApplicationBean() {
        return (VDCApplicationBean)getBean("VDCApplication");
    }


    /** 
     * <p>Return a reference to the scoped data bean.</p>
     */
    public static VDCSessionBean getVDCSessionBean() {
        return (VDCSessionBean)getBean("VDCSession");
    }

    public static VDCRequestBean getVDCRequestBean() {
        return (VDCRequestBean)getBean("VDCRequest");
        
    }
     /**
     * <p>Return a <code>Map</code> of the session scope attributes for the
     * current user's session.  Note that calling this method will cause a
     * session to be created if there is not already one associated with
     * this request.</p>
     */
    protected Map getSessionMap() {

        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

    }
    
   protected ExternalContext getExternalContext() {

        return FacesContext.getCurrentInstance().getExternalContext();

    }
   
    /**
     * <p>Return a <code>Map</code> of the request scope attributes for
     * the current request.</p>
     */
    protected Map getRequestMap() {

        return getExternalContext().getRequestMap();

    }
    
      /**
     * <p>Return the <code>FacesContext</code> instance for the current
     * request.</p>
     */
    protected FacesContext getFacesContext() {

        return FacesContext.getCurrentInstance();

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
 
   /**
     * <p>Mapping from the String version of the <code>Locale</code> for
     * this response to the corresponding character encoding.  For each
     * row, the first String is the value returned by the toString() method
     * for the java.util.Locale for the current view, and the second
     * String is the name of the character encoding to be used.</p>
     *
     * <p>Only locales that use an encoding other than the default (UTF-8)
     * need to be listed here.</p>
     */
    protected String encoding[][] = {
	{ "zh_CN", "GB2312" }, // NOI18N
    };

  public String getLocaleCharacterEncoding() {

	// Return the appropriate character encoding for this locale (if any)
	Locale locale = getFacesContext().getViewRoot().getLocale();
	if (locale == null) {
	    locale = Locale.getDefault();
	}
	String match = locale.toString();
	for (int i = 0; i < encoding.length; i++) {
	    if (match.equals(encoding[i][0])) {
		return encoding[i][1];
	    }
	}

	// Return the default encoding value
	return "UTF-8"; // NOI18N

    }
  public static Object getBean(String name) {

        return FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), name);

    }


    public void redirect(String url) {
        FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();
        HttpServletResponse response = (javax.servlet.http.HttpServletResponse) fc.getExternalContext().getResponse();
        try {
            response.sendRedirect("/dvn" + getVDCRequestBean().getCurrentVDCURL() + url);
            fc.responseComplete();
        } catch (IOException ex) {
            throw new RuntimeException("IOException thrown while trying to redirect to " + url);
        }
    }
}
