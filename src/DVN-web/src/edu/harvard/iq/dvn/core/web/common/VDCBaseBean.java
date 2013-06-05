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
 * VDCBaseBean.java
 *
 * Created on September 12, 2006, 4:36 PM
 *
 */
package edu.harvard.iq.dvn.core.web.common;

import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
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


    @Inject VDCSessionBean vdcSessionBean;

    public VDCSessionBean getVDCSessionBean() {
        return vdcSessionBean;
    }

    @Inject VDCRequestBean vdcRequestBean;
    
    public VDCRequestBean getVDCRequestBean() {
        return vdcRequestBean;       
    }
    
    @Inject VDCRenderBean vdcRenderBean;

    public VDCRenderBean getVDCRenderBean() {
        return vdcRenderBean;        
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
        
        if ( !( url.startsWith("http://") || url.startsWith("https://") ) ) {        
            url = "/dvn" + getVDCRequestBean().getCurrentVDCURL() + url;
        }
        
        try {
            fc.getExternalContext().redirect(url);
            fc.responseComplete();
            
        } catch (IOException ex) {
            throw new RuntimeException("IOException thrown while trying to redirect to " + url);
        }
    }
    
    
    public String getContextSuffix() {
        String contextSuffix = "";
        
        if (getVDCRequestBean().getCurrentVDC() != null) {
            contextSuffix = "&vdcId=" + getVDCRequestBean().getCurrentVDCId();
        } else if (getVDCRequestBean().getCurrentSubnetwork() != null) {
            contextSuffix = "&vdcSubnetworkId=" + getVDCRequestBean().getCurrentSubnetwork().getId();
        }

        return contextSuffix;
    }    
    

    public List<SelectItem> createSelectItemList(List<Object[]> items) {
        return createSelectItemList(items, null);
    }

    // this methods expect a list of 2 dimensional object array, where the 2nd elements are Strings for the labels
    // the second parameter is an Object (corresponding to the first element of the Object[]) to exclude    
    public List<SelectItem> createSelectItemList(List<Object[]> items, Object excludeItem) {
        List selectItems = new ArrayList();
        for (Object[] item : items) {
            if (excludeItem == null || !excludeItem.equals(item[0])) {
                selectItems.add(new SelectItem(item[0], (String) item[1]));
            }
        }
        return selectItems;
    }
}
