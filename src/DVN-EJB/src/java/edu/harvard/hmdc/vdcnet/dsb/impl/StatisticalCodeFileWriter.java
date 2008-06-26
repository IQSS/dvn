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
    
    public StatisticalCodeFileWriter(DvnRJobRequest sro){
        this.variableNames =    sro.getUpdatedVariableNames();
        this.variableTypes =    sro.getUpdatedVariableTypes();
        this.variableLabels =   sro.getUpdatedVariableLabels();
        this.valueLabelTable =  sro.getValueTable();
    }
    
    public StatisticalCodeFileWriter(String[] variableNames,
        int[] variableTypes, 
        String[] variableLabels, 
        Map<String, Map<String, String>> valueLabelTable){
        
        this.variableNames = variableNames;
        this.variableTypes = variableTypes;
        this.variableLabels = variableLabels;
        this.valueLabelTable = valueLabelTable;
            
    }
    
    
    public String[] variableNames;
    
    public int [] variableTypes;
    
    public String[] variableLabels;
    
    public Map<String, Map<String, String>> valueLabelTable;
    
    
    public void writeSPSScodeFile(String codeFileName){
        
    }
    
    public void writeSAScodeFile(String codeFileName){
        
    }
    
    public void writeSTATAcodeFile(String codeFileName){
        
    }

}
