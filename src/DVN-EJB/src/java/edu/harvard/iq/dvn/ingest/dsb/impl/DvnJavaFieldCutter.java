/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.ingest.dsb.impl;

import java.util.*;
import java.util.Scanner;
import java.util.logging.*;
import java.io.*;
import java.io.FileNotFoundException;
import static java.lang.System.*;
import org.apache.commons.lang.*;

import edu.harvard.iq.dvn.ingest.dsb.*;


/**
 *
 * @author a.sone
 */
 
public class DvnJavaFieldCutter implements FieldCutter{

    private static Logger dbgLog = Logger.getLogger(DvnJavaFieldCutter.class.getPackage().getName());

    
    public boolean debug=false;
    
    public  void subsetFile(String infile, String outfile, Set<Integer> columns, Long numCases) {
        subsetFile(infile, outfile, columns, numCases, "\t");
    }

    public void subsetFile(String infile, String outfile, Set<Integer> columns, Long numCases,
        String delimiter) {
        try {
          Scanner scanner =  new Scanner(new File(infile));
          
          dbgLog.fine("outfile="+outfile);
          
          BufferedWriter out = new BufferedWriter(new FileWriter(outfile));
          scanner.useDelimiter("\\n");

            for (long caseIndex = 0; caseIndex < numCases; caseIndex++) {
                if (scanner.hasNext()) {
                    String[] line = (scanner.next()).split(delimiter,-1);
                    List<String> ln = new ArrayList<String>();
                    for (Integer i : columns) {
                        ln.add(line[i]);
                    }
                    out.write(StringUtils.join(ln,"\t")+"\n");
                } else {
                    throw new RuntimeException("Tab file has fewer rows than the determined number of cases.");
                }
            }

          while (scanner.hasNext()) {
              if (!"".equals(scanner.next()) ) {
                  throw new RuntimeException("Tab file has extra nonempty rows than the determined number of cases.");

              }
          }
          
          scanner.close();
          out.close();
          
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
