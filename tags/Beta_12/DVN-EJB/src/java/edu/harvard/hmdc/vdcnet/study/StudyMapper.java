/*
 * StudyMapper.java
 *
 * Created on August 7, 2006, 12:01 PM
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
public interface StudyMapper {

    public  Study importStudy(Document doc);

    public  Document exportStudy(Study study);
    
}
