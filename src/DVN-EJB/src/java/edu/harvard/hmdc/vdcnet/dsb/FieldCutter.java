/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.dsb;

import java.util.*;
    
/**
 *
 * @author asone
 */
public interface FieldCutter {
    public  void subsetFile(String infile, String outfile, Set<Integer> columns);

    public void subsetFile(String infile, String outfile, Set<Integer> columns,
        String delimiter);

}
