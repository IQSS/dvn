/*
 * InlineHelpSupportTag.java
 *
 * Created on May 3, 2007, 2:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.component;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;

/**
 *
 * @author wbossons
 */
public class InlineHelpSupportTag extends UIComponentTag {
    
    String writeHelpDiv = null;
    String writeTipDiv  = null;
    String writePopupDiv = null;
    
       /** Creates a new instance of TooltipTag */
    public InlineHelpSupportTag() {
    }
    
    public String getComponentType() {
        //Associates tag with UI Component registered in faces-config.xml
        return "InlineHelpSupport";
    }
    
    public String getRendererType() {
        //renderer is embedded in the component, return null
        return null;
    }
    
    //helper methods and override methods for custom attributes
    
    /**
     * process the superclass pros and then process the
     * incoming tag's custom attributes
     *
     * @author wbossons
     *
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);
         if (writeHelpDiv != null) {
            if (isValueReference(writeHelpDiv)) {
                FacesContext facescontext = FacesContext.getCurrentInstance();
                Application application   = facescontext.getApplication();
                ValueBinding valuebinding = application.createValueBinding(writeHelpDiv);
                component.setValueBinding("writeHelpDiv", valuebinding);
            } else {
                component.getAttributes().put("writeHelpDiv", writeHelpDiv);
            }
        }
        if (writeTipDiv != null) {
            if (isValueReference(writeTipDiv)) {
                FacesContext facescontext = FacesContext.getCurrentInstance();
                Application application   = facescontext.getApplication();
                ValueBinding valuebinding = application.createValueBinding(writeTipDiv);
                component.setValueBinding("writeTipDiv", valuebinding);
            } else {
                component.getAttributes().put("writeTipDiv", writeTipDiv);
            }
        }
        if (writePopupDiv != null) {
            if (isValueReference(writePopupDiv)) {
                FacesContext facescontext = FacesContext.getCurrentInstance();
                Application application   = facescontext.getApplication();
                ValueBinding valuebinding = application.createValueBinding(writePopupDiv);
                component.setValueBinding("writePopupDiv", valuebinding);
            } else {
                component.getAttributes().put("writePopupDiv", writePopupDiv);
            }
        }
    }
    
        /** 
     * setter and getter the writeHelpDiv
     *
     * @author wbossons
     *
     * 
     */
    public void setWriteHelpDiv(String writeHelpDiv) {
        this.writeHelpDiv = writeHelpDiv;
    }
    
    public String getWriteHelpDiv(){
        return writeHelpDiv;
    }
    
      /** 
     * setter and getter the writeTipDiv
     *
     * @author wbossons
     *
     * 
     */
    public void setWriteTipDiv(String writeTipDiv) {
        this.writeTipDiv = writeTipDiv;
    }
    
    public String getWriteTipDiv(){
        return writeTipDiv;
    }
    
    /** 
     * setter and getter the writePopupDiv
     *
     * @author wbossons
     *
     * 
     */
    public void setWritePopupDiv(String writePopupDiv) {
        this.writePopupDiv = writePopupDiv;
    }
    
    public String getWritePopupDiv(){
        return writePopupDiv;
    }
    
    
    /**
     * call the super class' release method
     *
     */
    public void release() {
        super.release();
    }
}
