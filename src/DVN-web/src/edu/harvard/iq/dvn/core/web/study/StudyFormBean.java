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
 * StudyFormBean.java
 *
 * Created on September 19, 2006, 11:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.study;

import edu.harvard.iq.dvn.core.study.Study;

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyFormBean implements java.io.Serializable  {
    
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
