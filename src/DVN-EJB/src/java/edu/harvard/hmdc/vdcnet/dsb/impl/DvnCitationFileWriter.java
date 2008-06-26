/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.dsb.impl;

import java.util.*;
import java.io.*;
import org.apache.commons.lang.*;

/**
 *
 * @author asone
 */
public class DvnCitationFileWriter {
    
    
    String title = "_Citation for the full data set you chose_:\n";
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
            pw.println();
            pw.println(offlineCitation);
            pw.println("\n\n\n");
            pw.print(offlineCitation + " ");
            pw.print(StringUtils.join(variableNameSet, ", "));
            pw.println(" [VarGrp/@var(DDI)];"+ subsettingCriteria );
            pw.println(subsetUNF);
           outs.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        
    }
}
