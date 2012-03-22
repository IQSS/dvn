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
import org.apache.commons.lang.text.StrSubstitutor;
import static java.lang.System.*;

/**
 *
 * @author asone
 */

public class DvnReplicationCodeFileWriter {
    
    private static Logger dbgLog = Logger.getLogger(DvnReplicationCodeFileWriter.class.getPackage().getName());
    
    // static fields
    // message lines (to be factored out as a Message class)
    public static String headerTemplate =
        "# R Code File for Replicating the DVN analysis request ${PID} \n"+
        "# \n"+
        "# Dataverse Network Project (http://thedata.org/) \n"+
        "# ${RexecDate} \n"+
        "# \n"+
        "# This R code file lists R-command lines that replicate modeling \n"+
        "# results you obtained from the DVN site on your local R environment. \n"+
        "# \n"+
        "# The accompanying RData format file ('${dvn_RData_FileName}')\n" +
        "# stores the subset you chose in a data.frame named '${dvn_dataframe}'.\n"+
        "# While you might have requested row-subsetting and/or recoding,\n"+
        "# the data.frame was saved before the steps of row-subsetting\n"+
        "# and recoding.  Since this data.frame maintians the original number\n"+
        "# of rows and keeps the original columns (variables) intact without\n" +
        "# recoding or transformation, you can try different case-selections of\n"+
        "# requested variables without downloading them again and still\n"+
        "# use the following citation data (subset) for the data.frame.\n"+
        "# \n"+
        "#      ${offlineCitation}\n" +
        "#      ${variableList}\n" +
        "#      [VarGrp/@var(DDI)];\n"+
        "#      ${fileUNF}\n"+
        "# \n"+
        "# Metadata such as variable labels and value-label sets are \n"+
        "# attached to the data.frame as attributes ('var.labels','val.table',\n"+
        "# respectively) if available.  'str(${dvn_dataframe})' command can \n"+
        "# show other available meta-data and additional information about\n"+
        "# the data set you subset.\n"+
        "# \n"+
        "# The accompanying README file explains how to begin replication steps,\n"+
        "# and includes data citations for the whole data set and this subset\n"+
        "# (data.frame) you requested and additional support information.\n"+
        "# ";
    
    // instance fields

    // value map
    public Map<String, String> valueMap = new LinkedHashMap<String, String>();
    /* the value map is expected to have the following attributes at least;
     * String dvn_request_id
     * String dvn_RData_FileName
     * String zelig_formula
     * String zelig_model_name
     * String zelig_setx_condition_1
     *String zelig_setx_condition_2
    */
    
    
    public DvnReplicationCodeFileWriter(){
    }
    
    public DvnReplicationCodeFileWriter(Map<String, String> requestParameters){
        this.valueMap = requestParameters;

    }
    
    public String generateHeaderBlock(){
        StrSubstitutor sub = new StrSubstitutor(valueMap);
        return sub.replace(headerTemplate);
    }
    
    public String generateLibraryLine(){
        String template = "library(${library_1})";
        StrSubstitutor sub = new StrSubstitutor(valueMap);
        return sub.replace(template);
    }
    
    public String generateDvnRHelperFileLine(){
        String template = "source('./${dvn_R_helper_file}')";
        StrSubstitutor sub = new StrSubstitutor(valueMap);
        return sub.replace(template);
    }    
    public String generateLoadDataFileLine(){
        String template = "load('./${dvn_RData_FileName}')";
        StrSubstitutor sub = new StrSubstitutor(valueMap);
        return sub.replace(template);
    }
    
    public String generateSubsetLine(){
        String template = "${dvn_dataframe}<-subsetVariables(${dvn_dataframe})";
        StrSubstitutor sub = new StrSubstitutor(valueMap);
        return sub.replace(template);
    }
    
    public String generateRecodeLine(){
        String template = "${dvn_dataframe}<-recodeVariables(${dvn_dataframe})";
        StrSubstitutor sub = new StrSubstitutor(valueMap);
        return sub.replace(template);
    }
    public String generateZeligModelLine(){
        String template = "z.out<-zelig(${zelig_formula}, model='${model}', data=dvnData)";
        StrSubstitutor sub = new StrSubstitutor(valueMap);
        return sub.replace(template);
    }
    
    public String generateZeligSim1Line(){
        String template = "x.first <- setx(z.out, ${zelig_sim_1})";
        StrSubstitutor sub = new StrSubstitutor(valueMap);
        return sub.replace(template);
    }
    
    public String generateZeligSim2Line(){
        String template = "x.second <- setx(z.out, ${zelig_sim_2})";
        StrSubstitutor sub = new StrSubstitutor(valueMap);
        return sub.replace(template);
    }    
    public String generateIndexingLines(){
        String template =
            "${dvn_dataframe}<-createvalindex(dtfrm=${dvn_dataframe}, attrname='val.index')\n"+
            "${dvn_dataframe}<-createvalindex(dtfrm=${dvn_dataframe}, attrname='missval.index')\n";
        StrSubstitutor sub = new StrSubstitutor(valueMap);
        
        return sub.replace(template);
    } 
    public String generateEdaLines() {
        String template =
            "aol<-c(1,1,0)\n" +
            "try(dir.create('./Zlg_${PID}'))\n" +
            "htmlsink<-HTMLInitFile(outdir = './Zlg_${PID}', filename='Rout_${PID}', \n" +
            "extension='html', CSSFile='R2HTML.css', Title ='Dataverse Analysis: Request #${PID}')\n" +
            "hdrContents <- '<h1>Dataverse Analysis</h1><h2>Results</h2><p>Study\n" +
            " Title:testDataForDSB</p><hr />'\n"+
            "HTML(file=htmlsink,hdrContents)\n" +
            "try(dir.create('./Zlg_${PID}/visuals'))\n" +
            "try(${dvn_dataframe}<-univarStat(dtfrm=${dvn_dataframe}))\n"+
            "try({${dvn_dataframe}<-univarChart(dtfrm=${dvn_dataframe}, analysisoptn=aol,\n" +
            "imgflprfx='./Zlg_${PID}/visuals/Rvls.${PID}',standalone=F)})\n"+
            "try(univarStatHtmlBody(dtfrm=${dvn_dataframe},whtml=htmlsink, analysisoptn=aol))\n"+
            "HTMLEndFile()\n" +
            "file.rename('R2HTML.css', './Zlg_${PID}/R2HTML.css')\n" +
            "wrkdir<-'./Zlg_${PID}'\n" +
            "htmlfile <- paste(wrkdir,'/', list.files(wrkdir, pattern='.html'), sep='')\n" +
            "browseURL(htmlfile)\n";
        StrSubstitutor sub = new StrSubstitutor(valueMap);
        
        return sub.replace(template);
    }
    public String generateXtabLines() {
        String template =
            "aol<-c(0,0,1)\n" +
            "try(dir.create('./Zlg_${PID}'))\n" +
            "htmlsink<-HTMLInitFile(outdir = './Zlg_${PID}', filename='Rout_${PID}', \n" +
            "extension='html', CSSFile='R2HTML.css', Title ='Dataverse Analysis: Request #${PID}')\n" +
            "hdrContents <- '<h1>Dataverse Analysis</h1><h2>Results</h2><p>Study\n" +
            " Title:testDataForDSB</p><hr />'\n"+
            "HTML(file=htmlsink,hdrContents)\n" +
            
            "try(${dvn_dataframe}<-univarStat(dtfrm=${dvn_dataframe}))\n" +
            "library(VDCutil)\n" +
            "classVar<-c(${class_var_set})\n" +
            "freqVar<-c(${freq_var})\n" +
            
            "try(VDCcrossTabulation(HTMLfile=htmlsink, data=${dvn_dataframe},\n" +
            
            "classificationVars=classVar, freqVars=freqVar, wantPercentages=${wantPercentages}, \n" +
            
            "wantTotals=${wantTotals}, wantStats=${wantStats}, wantExtraTables=${wantExtraTables}))\n" +
            
            "HTMLEndFile()\n" +
            "file.rename('R2HTML.css', './Zlg_${PID}/R2HTML.css')\n" +
            "wrkdir<-'./Zlg_${PID}'\n" +
            "htmlfile <- paste(wrkdir,'/', list.files(wrkdir, pattern='.html'), sep='')\n" +
            "browseURL(htmlfile)\n";
        
        StrSubstitutor sub = new StrSubstitutor(valueMap);
        
        return sub.replace(template);
    }
    
    public void writeZeligCode(File rcf){
        OutputStream outs = null;
        try {
            outs = new BufferedOutputStream(new FileOutputStream(rcf));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(outs, "utf8"), true);
            pw.println(generateHeaderBlock()+"\n");
            
            pw.println(generateDvnRHelperFileLine());
            pw.println(generateLibraryLine());
            
            pw.println(generateLoadDataFileLine());
            
            if (valueMap.containsKey("subset")){
                pw.println(generateSubsetLine());
            }
            if (valueMap.containsKey("recode")){
                pw.println(generateRecodeLine());
            }
            pw.println(generateZeligModelLine());
            pw.println("summary(z.out)");
            if (valueMap.containsKey("zelig_sim_2")){
                pw.println(generateZeligSim1Line());
                pw.println(generateZeligSim2Line());
                pw.println("s.out <- sim(z.out, x = x.first, x1 = x.second)");
                pw.println("summary(s.out)");
                pw.println("plot(s.out)");
            } else if (valueMap.containsKey("zelig_sim_1")) {
                pw.println(generateZeligSim1Line());
                pw.println("s.out <- sim(z.out, x = x.first)");
                pw.println("summary(s.out)");
                pw.println("plot(s.out)");
            } else {
                pw.println("x.out <- setx(z.out)");
                pw.println("s.out <- sim(z.out, x=x.out)");
                pw.println("summary(s.out)");
                pw.println("plot(s.out)");
            }


           outs.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeXtabCode(File rcf){
        OutputStream outs = null;
        try {
            outs = new BufferedOutputStream(new FileOutputStream(rcf));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(outs, "utf8"), true);
            
            pw.println(generateHeaderBlock()+"\n");
            pw.println(generateDvnRHelperFileLine());
            
            pw.println(generateLoadDataFileLine());
            if (valueMap.containsKey("subset")){
                pw.println(generateSubsetLine());
            }
            if (valueMap.containsKey("recode")){
                pw.println(generateRecodeLine());
            }
            pw.println(generateIndexingLines());
            
            pw.println(generateXtabLines());
            
           outs.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void writeEdaCode(File rcf){
        OutputStream outs = null;
        try {
            outs = new BufferedOutputStream(new FileOutputStream(rcf));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(outs, "utf8"), true);
            
            pw.println(generateHeaderBlock()+"\n");
            pw.println(generateDvnRHelperFileLine());
            pw.println(generateLoadDataFileLine());
            if (valueMap.containsKey("subset")){
                pw.println(generateSubsetLine());
            }
            if (valueMap.containsKey("recode")){
                pw.println(generateRecodeLine());
            }
            pw.println(generateIndexingLines());
            
            pw.println(generateEdaLines());
            
           outs.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
