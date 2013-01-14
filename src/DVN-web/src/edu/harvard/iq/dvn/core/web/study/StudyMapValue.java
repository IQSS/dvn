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
 * StudyMapValue.java
 *
 * Created on October 6, 2006, 2:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.study;

import edu.harvard.iq.dvn.core.study.TemplateField;

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyMapValue implements java.io.Serializable  {
    
    /**
     * Creates a new instance of StudyMapValue
     */
    public StudyMapValue() {
    }
    
   

    /**
     * Holds value of property rendered.
     */
    private boolean rendered;

    /**
     * Getter for property rendered.
     * @return Value of property rendered.
     */
    public boolean isRendered() {
        return this.rendered;
    }

    /**
     * Setter for property rendered.
     * @param rendered New value of property rendered.
     */
    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    /**
     * Holds value of property templateField.
     */
    private TemplateFieldUI templateFieldUI;

    public TemplateFieldUI getTemplateFieldUI() {
        return templateFieldUI;
    }

    public void setTemplateFieldUI(TemplateFieldUI templateFieldUI) {
        this.templateFieldUI = templateFieldUI;
    }

    /**
     * Getter for property templateField.
     * @return Value of property templateField.
     */
    public TemplateField getTemplateField() {
        return this.templateFieldUI.getTemplateField();
    }

   
    
    public boolean isRequired() {
        return templateFieldUI.getTemplateField().isRequired();
    }
    
    public boolean isRecommended() {
        return templateFieldUI.getTemplateField().isRecommended();
    }
 
    public void setRecommended(boolean isRecommended) {
        templateFieldUI.setRecommended(isRecommended);
    }

    public boolean isOptional() {
        return templateFieldUI.getTemplateField().isOptional();
    }
    
    public boolean isHidden() {
        return templateFieldUI.getTemplateField().isHidden();
    }
    
    public boolean isDisabled() {
        return templateFieldUI.getTemplateField().isDisabled();
    }
      
}
