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
 * DvnInlineHelpSupportTag.java
 *
 * Created on May 3, 2007, 2:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.component;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;

/**
 *
 * @author wbossons
 */
public class DvnInlineHelpSupportTag extends UIComponentTag implements java.io.Serializable  {
    
    String writeHelpDiv = null;
    String writeTipDiv  = null;
    String writePopupDiv = null;
    
       /** Creates a new instance of DvnInlineHelpTag */
    public DvnInlineHelpSupportTag() {
    }
    
    public String getComponentType() {
        //Associates tag with UI Component registered in faces-config.xml
        return "DvnInlineHelpSupport";
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
