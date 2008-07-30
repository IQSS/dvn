/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.dsb.impl;


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
        "# R Code File for Replicating the DVN analysis request ${dvn_request_id} \n"+
        "# Dataverse Network Project (http://thedata.org/) \n"+
        "# ${file_creation_date} \n"+
        "# \n"+
        "# This R code file lists R-command lines that replicate modeling \n"+
        "# results you obtained from the DVN site on your local R environment. \n"+
        "# \n"+
        "# About the dvn-data.frame format\n"+
        "# Medata such as variable labels and value-label sets are \n"+
        "# attached to the data.frame named 'dvnData', as attributes.\n"+
        "# 'str(dvnData)' command shows these meta-data attributes.\n"+
        "# \n";
    
    public static String[] subsetLines = {
        "if (!is.null(attr(dvnData,'subsetLines')){",
            "subsetLines <- attr(dvnData,'subsetLines');",
            "for(i in 1:length(subsetLines)) {",
            "eval(parse(text=sbsetLines[i],n=1))",
        "}"
    };
    
    public static String[] recodeLines = {
        "if (!is.null(attr(dvnData,'recodeLines')){",
            "recodeLines <- attr(dvnData,'recodeLines');",
            "for(i in 1:length(recodeLines)) {",
            "eval(parse(text=recodeLines[i],n=1))",
        "}"
    };
    
    public static String[] valueTalbeIndexingLines ={
        "x<-createvalindex(dtfrm=dvnData, attrname='val.index');",
        "x<-createvalindex(dtfrm=dvnData, attrname='missval.index');"
    }
        
        ;
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
        String template = "source(${dvn_R_helper_file})";
        StrSubstitutor sub = new StrSubstitutor(valueMap);
        return sub.replace(template);
    }    
    public String generateLoadDataFileLine(){
        String template = "load('./${dvn_RData_FileName}')";
        StrSubstitutor sub = new StrSubstitutor(valueMap);
        return sub.replace(template);
    }
    
    public String generateZeligModelLine(){
        String template = "z.out<-Zelig(${zelig_formula}, model='${zelig_model_name}', data=dvnData)";
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
    
    
    public void writeZeligCode(File rcf){
        OutputStream outs = null;
        try {
            outs = new BufferedOutputStream(new FileOutputStream(rcf));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(outs, "utf8"), true);
            pw.println(generateHeaderBlock()+"\n");
            
            pw.println(generateLibraryLine());
            
            pw.println(generateLoadDataFileLine());
            
            if (valueMap.containsKey("subset")){
                pw.println(StringUtils.join(subsetLines, "\n"));
            }
            if (valueMap.containsKey("recode")){
                pw.println(StringUtils.join(recodeLines, "\n"));
            }
            pw.println(generateZeligModelLine());
            
            if (valueMap.containsKey("zelig_sim_2")){
                pw.println(generateZeligSim1Line());
                pw.println(generateZeligSim2Line());
                pw.println("s.out <- sim(z.out, x = x.first, x1 = x.second)");
                pw.println("summary(s.out);");
                pw.println("plot(s.out);");
            } else if (valueMap.containsKey("zelig_sim_1")) {
                pw.println(generateZeligSim1Line());
                pw.println("s.out <- sim(z.out, x = x.first)");
                pw.println("summary(s.out);");
                pw.println("plot(s.out);");
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
            // call VDCuitl
            pw.println(generateLibraryLine());
            
            if (valueMap.containsKey("subset")){
                pw.println(StringUtils.join(subsetLines, "\n"));
            }
            if (valueMap.containsKey("recode")){
                pw.println(StringUtils.join(recodeLines, "\n"));
            }
            pw.println(StringUtils.join(valueTalbeIndexingLines, "\n"));
            
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
            // call VDCuitl
            pw.println(generateLibraryLine());
            
            if (valueMap.containsKey("subset")){
                pw.println(StringUtils.join(subsetLines, "\n"));
            }
            if (valueMap.containsKey("recode")){
                pw.println(StringUtils.join(recodeLines, "\n"));
            }
            pw.println(StringUtils.join(valueTalbeIndexingLines, "\n"));
            
           outs.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
