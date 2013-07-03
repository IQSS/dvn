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
 * StudyRequestBean.java
 *
 * Created on November 3, 2006, 12:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyRequestBean implements java.io.Serializable {
    
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
    
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    
}
