/*
 * StudyRequestBean.java
 *
 * Created on November 3, 2006, 12:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyRequestBean {
    
    /** Creates a new instance of StudyRequestBean */
    public StudyRequestBean() {
    }

    
    public StudyRequestBean(StudyAccessRequest studyAccessRequest) {
        this.studyRequest=studyAccessRequest;
    }
    /**
     * Holds value of property accept.
     */
    private Boolean accept;

    /**
     * Getter for property accept.
     * @return Value of property accept.
     */
    public Boolean getAccept() {
        return this.accept;
    }

    /**
     * Setter for property accept.
     * @param accept New value of property accept.
     */
    public void setAccept(Boolean accept) {
        this.accept = accept;
    }

    /**
     * Holds value of property studyRequest.
     */
    private StudyAccessRequest studyRequest;

    /**
     * Getter for property studyRequest.
     * @return Value of property studyRequest.
     */
    public StudyAccessRequest getStudyRequest() {
        return this.studyRequest;
    }

    /**
     * Setter for property studyRequest.
     * @param studyRequest New value of property studyRequest.
     */
    public void setStudyRequest(StudyAccessRequest studyRequest) {
        this.studyRequest = studyRequest;
    }
    
}
