/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.dsb.impl;

import java.util.*;
import java.io.*;
import org.apache.commons.lang.*;
import static java.lang.System.*;

/**
 *
 * @author asone
 */
public class DvnCitationFileWriter {
    
    
    String title = "_Citation for the full data set you chose_:\n";
    String subsetTitle = "_Citation for this subset you chose_:\n";
    String offlineCitation;
    
    public DvnCitationFileWriter(String oc){
        offlineCitation = oc;
    }
    public void Write(String citationFilename, 
        List<String> variableNameSet, String subsetUNF){
        
        this.Write(citationFilename, variableNameSet, 
            subsetUNF, null);
    }

    public void Write(String citationFilename,
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
            //File cf = File.createTempFile("citationfile.", ".txt");
            File cf = new File(citationFilename);
            outs = new BufferedOutputStream(new FileOutputStream(cf));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(outs, "utf8"), true);
            pw.println(title);
            pw.println(offlineCitation);
            pw.println("\n\n\n");
            pw.println(subsetTitle);
            pw.print(offlineCitation + " ");
            pw.print(JoinNelementsPerLine(variableNameSet, 5));
            pw.println("[VarGrp/@var(DDI)];"+ subsettingCriteria );
            pw.println(subsetUNF);
           outs.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public String JoinNelementsPerLine(List<String> vn, int divisor){
        boolean debug = false;
        String vnl = null;
        if (vn.size() < divisor){
            vnl = StringUtils.join(vn, ", ");
        } else {
            StringBuilder sb = new StringBuilder();
            
            int iter =  vn.size() / divisor;
            int lastN = vn.size() % divisor;
            if (lastN != 0){
                iter++;
            }
            for (int i= 0; i<iter; i++){
                int terminalN = divisor;
                if ((i == (iter-1))  && (lastN != 0)){
                    terminalN = lastN;
                }                
                for (int j = 0; j< terminalN; j++){
                    if ( (divisor*(iter-1) +j +1) == vn.size()){ 
                        sb.append(vn.get(j + i*divisor));
                    } else {
                        
                        sb.append(vn.get(j + i*divisor) + ", ");
                    }
                }
                if (i < (iter-1)){
                    sb.append(",\n");
                } else {
                    sb.append("\n");
                }
            }
            vnl = sb.toString();
            if (debug){
                out.println(vnl);
            }
        }
        return vnl;
    }
}
