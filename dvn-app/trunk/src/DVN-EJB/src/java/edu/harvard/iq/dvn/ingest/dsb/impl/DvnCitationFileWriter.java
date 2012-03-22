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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.ingest.dsb.impl;

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
    String subsetingCriteriaLineD = "_Row selection Criteria for the subset you chose_:\n";
    String subsetingCriteriaLineA = "_Row selection criteria for the subset used in your analysis_:\n";
    
    
    String offlineCitation;
    String subsetUNF;
    String variableList;
    String subsettingCriteria;
    String requestType;
    public DvnCitationFileWriter(String oc){
        offlineCitation = oc;
    }
    
    public DvnCitationFileWriter(Map<String, String> resultInfo){
        this.offlineCitation    = resultInfo.get("offlineCitation");
        this.subsetUNF          = resultInfo.get("fileUNF");
        this.variableList       = resultInfo.get("variableList");
        this.requestType        = resultInfo.get("option");
        if (   (resultInfo.containsKey("subsettingCriteria")) 
            && (resultInfo.get("subsettingCriteria") != null) 
            ){
                this.subsettingCriteria = resultInfo.get("subsettingCriteria");
        } else {
            this.subsettingCriteria = "";
        }
    }
    
    public String generateSubsetCriteriaLine(){
        String subsetCriteriaLine =null;
        String subsettingCriteriaHeader = null;
        if (requestType.equals("download")){
            subsettingCriteriaHeader = subsetingCriteriaLineD;
        } else {
            subsettingCriteriaHeader = subsetingCriteriaLineA;
        }

        if (subsettingCriteria.equals("")){
            subsetCriteriaLine = "";
        } else {
            subsetCriteriaLine = subsettingCriteriaHeader + subsettingCriteria + "\n";
        }
        return subsetCriteriaLine;
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
            
            if (generateSubsetCriteriaLine().equals("")){
                pw.println("\n\n");
            } else {
                pw.println("\n");
                pw.println(generateSubsetCriteriaLine()+"\n");
            }
            pw.println(subsetTitle);
            pw.print(offlineCitation + " ");
            pw.print(DvnDSButil.joinNelementsPerLine(variableNameSet,5));
            pw.println(" [VarGrp/@var(DDI)];");
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
            
            if (generateSubsetCriteriaLine().equals("")){
                pw.println("\n\n");
            } else {
                pw.println("\n");
                pw.println(generateSubsetCriteriaLine()+"\n");
            }
            pw.println(subsetTitle);
            pw.print(offlineCitation + " ");
            pw.print(variableList);
            pw.println(" [VarGrp/@var(DDI)];");
            pw.println(subsetUNF);
           outs.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeWholeFileCase(File cf){
        OutputStream outs = null;
        try {
            outs = new BufferedOutputStream(new FileOutputStream(cf));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(outs, "utf8"), true);
            pw.println(title);
            pw.println(offlineCitation);

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
