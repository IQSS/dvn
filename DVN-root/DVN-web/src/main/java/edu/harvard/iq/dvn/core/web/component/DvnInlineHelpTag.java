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
 * DvnInlineHelpTag.java
 *
 * Created on March 29, 2007, 12:39 PM
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
public class DvnInlineHelpTag extends UIComponentTag implements java.io.Serializable  {
    
    String helpMessage = null;
    String linkText    = null;
    String cssClass    = null;
    String titleHelp   = null;
    String tooltipText = null;
    String eventType   = null;
    String heading     = null;
    
    /** Creates a new instance of DvnInlineHelpTag */
    public DvnInlineHelpTag() {
    }
    
    public String getComponentType() {
        //Associates tag with UI Component registered in faces-config.xml
        return "DvnInlineHelp";
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
        if (helpMessage != null) {
            if (isValueReference(helpMessage)) {
                FacesContext facescontext = FacesContext.getCurrentInstance();
                Application application   = facescontext.getApplication();
                ValueBinding valuebinding = application.createValueBinding(helpMessage);
                component.setValueBinding("helpMessage", valuebinding);
            } else {
                component.getAttributes().put("helpMessage", helpMessage);
            }
        }
        if (linkText != null) {
            if (isValueReference(linkText)) {
                FacesContext facescontext = FacesContext.getCurrentInstance();
                Application application   = facescontext.getApplication();
                ValueBinding valuebinding = application.createValueBinding(linkText);
                component.setValueBinding("linkText", valuebinding);
            } else {
                component.getAttributes().put("linkText", linkText);
            }
        }
        if (cssClass != null) {
            if (isValueReference(cssClass)) {
                FacesContext facescontext = FacesContext.getCurrentInstance();
                Application application   = facescontext.getApplication();
                ValueBinding valuebinding = application.createValueBinding(cssClass);
                component.setValueBinding("cssClass", valuebinding);
            } else {
                component.getAttributes().put("cssClass", cssClass);
            }
        }
        if (tooltipText != null) {
            if (isValueReference(tooltipText)) {
                FacesContext facescontext = FacesContext.getCurrentInstance();
                Application application   = facescontext.getApplication();
                ValueBinding valuebinding = application.createValueBinding(tooltipText);
                component.setValueBinding("tooltipText", valuebinding);
            } else {
                component.getAttributes().put("tooltipText", tooltipText);
            }
        }
        if (eventType != null) {
            if (isValueReference(eventType)) {
                FacesContext facescontext = FacesContext.getCurrentInstance();
                Application application   = facescontext.getApplication();
                ValueBinding valuebinding = application.createValueBinding(eventType);
                component.setValueBinding("eventType", valuebinding);
            } else {
                component.getAttributes().put("eventType", eventType);
            }
        }
        if (heading != null) {
            if (isValueReference(heading)) {
                FacesContext facescontext = FacesContext.getCurrentInstance();
                Application application   = facescontext.getApplication();
                ValueBinding valuebinding = application.createValueBinding(heading);
                component.setValueBinding("heading", valuebinding);
            } else {
                component.getAttributes().put("heading", heading);
            }
        }
    }
    
    /**
     * set the help message
     *
     * @author wbossons
     *
     */
    public void setHelpMessage(String helpMessage) {
        this.helpMessage = helpMessage;
    }

    public String getHelpMessage() {
        return helpMessage;
    }
    
    /** 
     * setter and getter the linkText
     *
     * @author wbossons
     *
     * 
     */
    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }
    
    public String getLinkText(){
        return linkText;
    }
    
       /** 
     * setter and getter the cssClass
     *
     * @author wbossons
     *
     * 
     */
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }
    
    public String getCssClass(){
        return cssClass;
    }
    
        /** 
     * setter and getter the tooltipText
     *
     * @author wbossons
     *
     * 
     */
    public void setTooltipText(String tooltipText) {
        this.tooltipText = tooltipText;
    }
    
    public String getTooltipText(){
        return tooltipText;
    }
    
   /** 
     * setter and getter the eventType
     *
     * @author wbossons
     *
     * 
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getEventType(){
        return eventType;
    }
    
    /** 
     * setter and getter the heading
     *
     * @author wbossons
     *
     * 
     */
    public void setHeading(String heading) {
        this.heading = heading;
    }
    
    public String getHeading(){
        return heading;
    }
    
    /**
     * call the super class' release method
     *
     */
    public void release() {
        super.release();
        helpMessage = null;
    }
}
