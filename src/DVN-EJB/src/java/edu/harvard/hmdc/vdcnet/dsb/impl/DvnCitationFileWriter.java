/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.dsb.impl;

import java.util.*;
/**
 *
 * @author asone
 */
public class DvnCitationFileWriter {
    
    
    public void Write(List<String> citationInfo, String citationFilename, 
        List<String> variableNameSet, String subsetUNF){
        
        this.Write(citationInfo, citationFilename, variableNameSet, 
            subsetUNF, null);
    }

    public void Write(List<String> CitationInfo, String citationFilename,
        List<String> variableNameSet, String subsetUNF, 
        String subsettingCriteria){
        
    }
}
