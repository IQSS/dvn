/*
 * StudyService.java
 *
 * Created on August 9, 2006, 3:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import org.w3c.dom.Document;

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyService {
    
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
    public void addStudy(Study study) {
        
    }
    
    public void updateStudy(Study study) {
        
    }
    
    public void deleteStudy(Study study) {
        
    }
    
}
