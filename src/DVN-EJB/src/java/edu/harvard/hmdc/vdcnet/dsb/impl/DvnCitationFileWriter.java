/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.dsb.impl;

import java.util.*;
import java.util.logging.*;
import java.io.*;
import org.apache.commons.lang.*;
import static java.lang.System.*;

/**
 *
 * @author asone
 */

public class DvnCitationFileWriter {
    
    private static Logger dbgLog = Logger.getLogger(DvnCitationFileWriter.class.getPackage().getName());
    
    String title = "_Citation for the full data set you chose_:\n";
    String subsetTitle = "_Citation for this subset you chose_:\n";
    
    String offlineCitation;
    String subsetUNF;
    String variableList;
    String subsettingCriteria;
    
    public DvnCitationFileWriter(String oc){
        offlineCitation = oc;
    }
    
    public DvnCitationFileWriter(Map<String, String> resultInfo){
        this.offlineCitation    = resultInfo.get("offlineCitation");
        this.subsetUNF          = resultInfo.get("fileUNF");
        this.variableList       = resultInfo.get("variableList");
        this.subsettingCriteria = resultInfo.get("subsettingCriteria");
        
    }
    
    public void write(String citationFilename, 
        List<String> variableNameSet, String subsetUNF){
        
        this.write(citationFilename, variableNameSet, 
            subsetUNF, null);
    }

    public void write(String citationFilename,
        List<String> variableNameSet, String subsetUNF, 
        String subsettingCriteria){
        OutputStream outs = null;
        if (subsetUNF == null){
            subsetUNF="";
        }
        if (subsettingCriteria == null){
            subsettingCriteria ="";
        }
        try {
            File cf = new File(citationFilename);
            outs = new BufferedOutputStream(new FileOutputStream(cf));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(outs, "utf8"), true);
            pw.println(title);
            pw.println(offlineCitation);
            pw.println("\n\n\n");
            pw.println(subsetTitle);
            pw.print(offlineCitation + " ");
            pw.print(DvnDSButil.joinNelementsPerLine(variableNameSet,5));
            pw.println(" [VarGrp/@var(DDI)];"+ subsettingCriteria );
            pw.println(subsetUNF);
           outs.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    
    public void write(File cf){

        OutputStream outs = null;
        try {
            outs = new BufferedOutputStream(new FileOutputStream(cf));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(outs, "utf8"), true);
            pw.println(title);
            pw.println(offlineCitation);
            pw.println("\n\n\n");
            pw.println(subsetTitle);
            pw.print(offlineCitation + " ");
            pw.print(variableList);
            pw.println(" [VarGrp/@var(DDI)];"+ subsettingCriteria );
            pw.println(subsetUNF);
           outs.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void write(String citationFilename){
        File cf = new File(citationFilename);
        write(cf);
    }
}
