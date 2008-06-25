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
public class StatisticalCodeFileWriter {
    
    public StatisticalCodeFileWriter(List<String> variableNames,
        List<String> variableTypes, 
        List<String> variableLabels, 
        Map<String, Map<String, String>> valueLabelTable){
        
        this.variableNames = variableNames;
        this.variableTypes = variableTypes;
        this.variableLabels = variableLabels;
        this.valueLabelTable = valueLabelTable;
            
    }
    
    
    public List<String> variableNames;
    
    public List<String> variableTypes;
    public List<String> variableLabels;
    
    public Map<String, Map<String, String>> valueLabelTable;
    
    
    public void writeSPSScodeFile(String codeFileName){
        
    }
    
    public void writeSAScodeFile(String codeFileName){
        
    }
    
    public void writeSTATAcodeFile(String codeFileName){
        
    }

}
