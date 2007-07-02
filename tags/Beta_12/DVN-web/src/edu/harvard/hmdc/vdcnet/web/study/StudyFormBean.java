/*
 * StudyFormBean.java
 *
 * Created on September 19, 2006, 11:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.study;

import edu.harvard.hmdc.vdcnet.study.Study;

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyFormBean {
    
    /** Creates a new instance of StudyFormBean */
    public StudyFormBean() {
    }

    /**
     * Holds value of property study.
     */
    private Study study;

    /**
     * Getter for property study.
     * @return Value of property study.
     */
    public Study getStudy() {
        return this.study;
    }

    /**
     * Setter for property study.
     * @param study New value of property study.
     */
    public void setStudy(Study study) {
        this.study = study;
    }
    
}
