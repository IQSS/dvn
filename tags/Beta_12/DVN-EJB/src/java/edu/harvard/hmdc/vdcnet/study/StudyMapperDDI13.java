/*
 * StudyMapperDDI13.java
 *
 * Created on August 7, 2006, 12:07 PM
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
public class StudyMapperDDI13 implements StudyMapper {
    
    /** Creates a new instance of StudyMapperDDI13 */
    public StudyMapperDDI13() {
    }   
    public  Study importStudy(Document doc){return null;}

    public  Document exportStudy(Study study){return null;}
}
