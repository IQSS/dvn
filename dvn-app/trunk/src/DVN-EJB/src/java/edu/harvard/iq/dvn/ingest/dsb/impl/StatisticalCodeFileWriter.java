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
import java.io.*;
/**
 *
 * @author asone
 */
public class StatisticalCodeFileWriter {
    
    public StatisticalCodeFileWriter(DvnRJobRequest sro){
        variableNames =    sro.getUpdatedVariableNames();
        variableTypes =    sro.getUpdatedVariableTypes();
        variableLabels =   sro.getUpdatedVariableLabels();
        valueLabelTable =  sro.getValueTable();
        subsetDataFileName= sro.getSubsetDataFileName();
        variableIds   =    sro.getUpdatedVariableIds();
        
        if ((valueLabelTable !=null) && (valueLabelTable.size() >0)) {
            for (int i = 0; i< variableNames.length; i++) {
                IdToName.put(variableIds[i], variableNames[i]);
            }
        }
        
        for (int i = 0; i< variableNames.length; i++) {
            IdToType.put(variableIds[i], variableTypes[i]);
        }
        

    }
    
    public StatisticalCodeFileWriter(String[] vn, int[] vt, String[] vl, 
        Map<String, Map<String, String>> vlt, String[] vi, String fln){
        
        variableNames = vn;
        variableTypes = vt;
        variableLabels = vl;
        valueLabelTable = vlt;
        variableIds     = vi;
        subsetDataFileName = fln;
        
        if ((vlt !=null) && (vlt.size() >0)) {
            for (int i = 0; i< vn.length; i++) {
                IdToName.put(vi[i], vn[i]);
            }
        }
        
        for (int i = 0; i< vn.length; i++) {
            IdToType.put(vi[i], vt[i]);
        }
        

    }
    
    public StatisticalCodeFileWriter(String[] vn, int[] vt, String[] vl,
        String fln){
            this(vn, vt, vl, null, null, fln);
    }
    public String subsetDataFileName;
    
    public String[] variableNames;
    
    public int [] variableTypes;
    
    public String[] variableLabels;
    
    public Map<String, Map<String, String>> valueLabelTable;
    
    public String[] formatCatgry;
    
    public String[] variableIds;
    
    public Map <String, String> IdToName = new LinkedHashMap<String, String>();

    public Map <String, Integer> IdToType = new LinkedHashMap<String, Integer>();

    
    public void writeSAScodeFile(File cf){
        OutputStream outs = null;
        try {
            outs = new BufferedOutputStream(new FileOutputStream(cf));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(outs, "utf8"), true);
            
            
            Set<String> vnWithVl = new LinkedHashSet<String>();
            if (valueLabelTable != null){
                // proc format 
                pw.println("proc format;");
                //
    //            VALUE LOChex5FINThex5FnewFMT
    //                1 = "INTERESTED"
    //                0 = "NOT INTERESTED"
    //            ;
                
                for (Map.Entry<String, Map<String, String>> vlti :valueLabelTable.entrySet()){
                    if (vlti.getValue().size() > 0) {
                        if (IdToType.get(vlti.getKey()) > 0){
                            pw.println("VALUE "+ IdToName.get(vlti.getKey()) + "FMT");
                        } else {
                            pw.println("VALUE $"+ IdToName.get(vlti.getKey()) + "FMT");
                        }
                        vnWithVl.add(IdToName.get(vlti.getKey()));
                        for (Map.Entry<String, String> vlt : vlti.getValue().entrySet()) {
                        if (vlt.getValue() != null){
                            if (IdToType.get(vlti.getKey()) > 0){
                                pw.println("    "+ vlt.getKey() + " = \""+ getSafeLabel(vlt.getValue(), 40) +"\"");
                            } else {
                                pw.println("    \""+ vlt.getKey() + "\" = \""+ getSafeLabel(vlt.getValue(), 40) +"\"");
                            }
                        }
                        }
                        pw.println(";\n");
                    }
                }
            }
            
            // data 
            //String dataFileName = "data_19328.dat";
            String datalibName  = subsetDataFileName.replace('.', '_');
            pw.println("DATA "+datalibName+";");
            pw.println("    INFILE '"+ subsetDataFileName +"' DELIMITER='09'x FIRSTOBS=2;" );
            
            StringBuilder sb = new StringBuilder();
            for ( int i=0; i< variableNames.length;i++){
                if (variableTypes[i]> 0){
                    // TODO handle date/time special cases
                    
                    sb.append(variableNames[i] + " ");
                } else {
                    sb.append(variableNames[i] + " $ ");
                }
            }
            
            pw.println("    INPUT " + sb.toString() + ";");
            //  label WThex5F2517 = "weight for rep. sample-see documentation";
            pw.println();
            for ( int i=0; i< variableNames.length;i++){
                if (variableLabels[i] != null){
                    pw.println("label " +variableNames[i]+ " = \"" + getSafeLabel(variableLabels[i], 80) +"\";");
                }
            }
            pw.println();
            if (valueLabelTable != null) {
                // format
    //            FORMAT
    //            LOChex5FINT LOChex5FINTFMT.
    //            ;
                pw.println("FORMAT");
                for ( int i=0; i< variableNames.length;i++){
                    if (vnWithVl.contains(variableNames[i])) {
                        pw.println(variableNames[i] +" "+ variableNames[i] +"FMT.");
                    }
                }
                pw.println(";\n");
            }
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
            pw.println("GET TRANSLATE FILE='" + subsetDataFileName + "'\n /TYPE=TAB\n /FIELDNAMES\n .\n");
            pw.println("VARIABLE LABELS");
            
            for ( int i=0; i< variableNames.length;i++){
                if (variableLabels[i] != null ){
                    if (i > 0){
                        pw.println(" / " +variableNames[i]+ " \"" + getSafeLabel(variableLabels[i], 40) +"\"");
                    } else {
                        pw.println(" " +variableNames[i]+ " \"" + getSafeLabel(variableLabels[i], 40) +"\"");
                    }
                }
            }
            pw.println(" .\n");
            // value labels
            if (valueLabelTable != null) {
                pw.println("VALUE LABELS");
                int counter = 0;
                int vltSize = valueLabelTable.size();
                for (Map.Entry<String, Map<String, String>> vlti :valueLabelTable.entrySet()){
                    if (vlti.getValue().size() > 0) {
                        pw.println(" "+ IdToName.get(vlti.getKey()));
                        counter ++;
                        for (Map.Entry<String, String> vlt : vlti.getValue().entrySet()) {
                        if (vlt.getValue() != null){
                            if (IdToType.get(vlti.getKey()) > 0){
                                pw.println("   "+ vlt.getKey() + " = \""+ getSafeLabel(vlt.getValue(), 40) +"\"");
                            } else {
                                pw.println("   \""+ vlt.getKey() + "\" = \""+ getSafeLabel(vlt.getValue(), 40) +"\"");
                            }
                        }
                        }
                        if (counter < vltSize) {
                            pw.println(" /");
                        }
                    }
                }
                pw.println(" .\n");
            }
            pw.println("MISSING VALUES");
            pw.println(" .\n");
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
            pw.println("insheet using "+ subsetDataFileName + ", tab\n");
            // label variable wthex5f2517 "weight for rep. sample-see documentation"
            for ( int i=0; i< variableNames.length;i++){
                if (variableLabels[i] != null){
                    pw.println("label variable " +variableNames[i]+ " \"" + getSafeLabel(variableLabels[i], 80) +"\"");
                }
            }
            if (valueLabelTable != null) {
                pw.println();
                // value labels: e.g.
    //            lab def lochex5fint_l 8 "DONT KNOW", add
    //            label values lochex5fint lochex5fint_l
                for (Map.Entry<String, Map<String, String>> vlti :valueLabelTable.entrySet()){
                    if ((vlti.getValue().size() > 0) && (IdToType.get(vlti.getKey()) > 0)) {
                        for (Map.Entry<String, String> vlt : vlti.getValue().entrySet()) {
                            if (vlt.getValue() != null){
                                pw.println("lab def "+ IdToName.get(vlti.getKey()) +"_l "+ vlt.getKey() + " = \""+ getSafeLabel(vlt.getValue(), 40) +"\", add");
                            }
                        }
                        pw.println("label values "+IdToName.get(vlti.getKey()) + " " + IdToName.get(vlti.getKey()) +"_l\n");
                    }
                }
            }

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

    public String getSafeLabel(String rawLabel, int maxLen) {
        String tmp = null;
        if (rawLabel != null){
            tmp = rawLabel.replaceAll("\"", "'");
            if (tmp.length() > maxLen) {
                return tmp.substring(0, maxLen-1);
            } else {
                return tmp;
            }
        } else {
           return "";
        }
    }
    
}
