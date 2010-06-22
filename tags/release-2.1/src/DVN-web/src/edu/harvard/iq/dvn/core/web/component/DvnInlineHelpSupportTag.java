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
