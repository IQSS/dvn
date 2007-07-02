package edu.harvard.hmdc.vdcnet.web.admin;

import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;

/*
 * WebStatisticsPage.java
 *
 * Created on January 24, 2007, 1:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author wbossons
 */
public class WebStatisticsPage extends VDCBaseBean {
    
    /** Creates a new instance of WebStatisticsPage */
    public WebStatisticsPage() {
    }

     /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init() {
        super.init();
        setAlias(getVDCRequestBean().getCurrentVDC().getAlias());
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
    
    private String alias;
    
    public String getAlias(){
        return alias;
    }
    
    public void setAlias(String alias) {
        this.alias = alias;
    }  
}

