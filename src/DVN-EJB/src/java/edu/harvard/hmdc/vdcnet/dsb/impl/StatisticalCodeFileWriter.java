/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.dsb.impl;

import java.util.*;
import java.io.*;
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
        this.subsetDataFileName= sro.getSubsetDataFileName();
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
    
    public StatisticalCodeFileWriter(String[] variableNames,
        int[] variableTypes, String[] variableLabels){
            this(variableNames, variableTypes, variableLabels, null);
    }
    public String subsetDataFileName;
    
    public String[] variableNames;
    
    public int [] variableTypes;
    
    public String[] variableLabels;
    
    public Map<String, Map<String, String>> valueLabelTable;
    
    
    public void writeSAScodeFile(File cf){
        OutputStream outs = null;
        try {
            outs = new BufferedOutputStream(new FileOutputStream(cf));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(outs, "utf8"), true);
            //String dataFileName = "data_19328.dat";
            String datalibName  = subsetDataFileName.replace('.', '_');
            pw.println("proc format;");
            pw.println("DATA "+datalibName+";");
            pw.println("INFILE '"+ subsetDataFileName +"' DELIMITER='09'x FIRSTOBS=2;" );
            pw.println("INPUT ");
            
            pw.println("run;");
           outs.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    public void writeSPSScodeFile(File cf){
        OutputStream outs = null;
        try {
            outs = new BufferedOutputStream(new FileOutputStream(cf));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(outs, "utf8"), true);
            //String dataFileName = "data_19328.dat";

            pw.println("GET TRANSLATE FILE='" + subsetDataFileName + "' /TYPE=TAB /FIELDNAMES .");
            pw.println("VARIABLE LABELS");
            pw.println(" .");
            pw.println("VALUE LABELS");
            pw.println(" .");
            pw.println("MISSING VALUES");
            pw.println(" .");
            pw.println("execute.");
           outs.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void writeSTATAcodeFile(File cf){
        OutputStream outs = null;
        try {
            outs = new BufferedOutputStream(new FileOutputStream(cf));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(outs, "utf8"), true);
            //String dataFileName = "data_19328.dat";
            pw.println("insheet using "+ subsetDataFileName + ", tab ");

           outs.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void write(File sas, File spss, File stata){
        writeSAScodeFile(sas);
        writeSPSScodeFile(spss);
        writeSTATAcodeFile(stata);
    }

}
