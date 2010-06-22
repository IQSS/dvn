/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.ingest.dsb;

import java.io.*;
/**
 *
 * @author asone
 */


public interface FormatConversionService {
    /**
     * 
     *
     * @param     
     * @return    
     */
    public void convert(File infile, String infrmt, 
        File outFile, String outfrmt);

    /**
     * 
     *
     * @param     
     * @return    
     */
    public String[] getConvertibleFormats ();
    
}
