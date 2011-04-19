/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.ingest.dsb.impl;

import org.apache.commons.lang.*;
import java.util.*;
import java.util.logging.*;
/**
 *
 * @author asone
 */
public class DvnDSButil {

    /** logger setting(use the package name) */
    private static Logger dbgLog = Logger.getLogger(DvnCitationFileWriter.class.getPackage().getName());

    /**
     * 
     *
     * @param     
     * @return    
     */
    public static String joinNelementsPerLine(String[] vn, int divisor, String sp, 
        boolean quote, String qm, String lnsp){
        if (!(divisor >= 1)){
            divisor = 1;
        } else if ( divisor > vn.length) {
            divisor = vn.length;
        }
        String sep = null;
        if (sp != null){
            sep = sp;
        } else {
            sep = ", ";
        }
        String qmrk = null;
        if (quote){
            if (qm == null){
                qmrk = ",";
            } else {
                if (qm.equals("\"")){
                    qmrk = "\"";
                } else {
                    qmrk = qm;
                }
            }
        } else {
            qmrk = "";
        }
        String lineSep = null;
        if (lnsp == null){
            lineSep = "\n";
        } else {
            lineSep = lnsp;
        }
        
        String vnl = null;
        if (vn.length < divisor){
            vnl = StringUtils.join(vn, sep);
        } else {
            StringBuilder sb = new StringBuilder();
            
            int iter =  vn.length / divisor;
            int lastN = vn.length % divisor;
            if (lastN != 0){
                iter++;
            }
            int iterm = iter - 1;
            for (int i= 0; i<iter; i++){
                int terminalN = divisor;
                if ((i == iterm )  && (lastN != 0)){
                    terminalN = lastN;
                }                
                for (int j = 0; j< terminalN; j++){
                    if ( (divisor*i +j +1) == vn.length){ 
                        
                        sb.append(qmrk + vn[j + i*divisor] + qmrk);
                        
                    } else {
                        
                        sb.append(qmrk + vn[j + i*divisor] + qmrk + sep);
                        
                    }
                }
                if (i < (iter-1)){
                sb.append(lineSep);
                }
            }
            vnl = sb.toString();
            dbgLog.fine("results:\n"+vnl);
        }
        return vnl;
    }

    
    /**
     * List version
     *
     * @param     
     * @return    
     */
    public static String joinNelementsPerLine(List<String> vnl, int divisor, String sp, 
        boolean quote, String qm, String lnsp){
        String[] vn = (String[])vnl.toArray(new String[vnl.size()]);
        return joinNelementsPerLine(vn, divisor,sp,quote,qm, lnsp);
    }

    /**
     * A convenience version of joinNelementsPerLine method that takes only
     * a string array to be concatenated and option that qoute an element
     * or not.  Used for R-code/command-history writing. 
     * Uses a single-quation mark for quating and a comma for the separator
     * 
     * @param vn    An array of String
     * @param quote    ture for quoting or false for without quoting
     * @return    A concatanated String
     */
    public static String joinNelementsPerLine(String[] vn, boolean quote){
        return joinNelementsPerLine(vn, vn.length, ", ", quote, "'", null);
    }

    // List version
    public static String joinNelementsPerLine(List<String> vnl, boolean quote){
        return joinNelementsPerLine(vnl, vnl.size(), ", ", quote, "'", null);
    }

    /**
     * A convenience version of joinNelementsPerLine method.
     *  Used for writing a citation file 
     * 
     * @param vn    An array of String
     * @param columns    number of elements per line
     * @return    A concatanated String
     */

    public static String joinNelementsPerLine(String[] vn, int columns){
        return joinNelementsPerLine(vn, columns, ", ", false, null, "\n");
    }
    
    // List version
    public static String joinNelementsPerLine(List<String> vnl, int columns){
        return joinNelementsPerLine(vnl, columns, ", ", false, null, "\n");
    }

}
