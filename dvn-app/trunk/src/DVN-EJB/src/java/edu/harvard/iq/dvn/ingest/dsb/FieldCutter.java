/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.ingest.dsb;

import java.io.InputStream;
import java.util.*;
    
/**
 *
 * @author asone
 */
public interface FieldCutter {
    public  void subsetFile(String infile, String outfile, Set<Integer> columns, Long numCases);

    public void subsetFile(String infile, String outfile, Set<Integer> columns, Long numCases,
        String delimiter);

    public void subsetFile(InputStream in, String outfile, Set<Integer> columns, Long numCases,
        String delimiter);
}
