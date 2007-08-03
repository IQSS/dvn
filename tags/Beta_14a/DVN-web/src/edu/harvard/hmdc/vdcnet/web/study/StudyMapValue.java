/*
 * StudyMapValue.java
 *
 * Created on October 6, 2006, 2:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.study;

import edu.harvard.hmdc.vdcnet.study.TemplateField;

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyMapValue {
    
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
    private TemplateField templateField;

    /**
     * Getter for property templateField.
     * @return Value of property templateField.
     */
    public TemplateField getTemplateField() {
        return this.templateField;
    }

    /**
     * Setter for property templateField.
     * @param templateField New value of property templateField.
     */
    public void setTemplateField(TemplateField templateField) {
        this.templateField = templateField;
    }
    
    public boolean isRequired() {
        return templateField.isRequired();
    }
    
    public boolean isRecommended() {
        return templateField.isRecommended();
    }
    
   
}
