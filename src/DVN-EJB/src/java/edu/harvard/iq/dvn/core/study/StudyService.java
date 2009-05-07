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
