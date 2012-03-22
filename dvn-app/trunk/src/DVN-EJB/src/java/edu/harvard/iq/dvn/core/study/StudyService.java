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
 * StudyService.java
 *
 * Created on August 9, 2006, 3:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

import org.w3c.dom.Document;

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyService implements java.io.Serializable {
    
    /** Creates a new instance of StudyService */
    public StudyService() {
    }
    
    /**
     *  Convert given XML document to a Study object.
     */
    public  Study importStudy(Document doc) {
      
        return null;
    }
    
    /**
     * Convert given Study to an XML Metatadata representation, based on schema type.
    */
    public Document exportStudy(String schemaType, Study study) {
        
         return null;
   }
    
    /**
     * Add given Study to persistent storage.
     */
 // commented out - not used   public void addStudy(Study study) {   }
    
    public void updateStudy(Study study) {
        
    }
    
    public void deleteStudy(Study study) {
        
    }
    
}
