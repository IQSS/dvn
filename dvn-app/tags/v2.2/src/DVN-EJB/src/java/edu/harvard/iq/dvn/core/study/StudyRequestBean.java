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
    
}
