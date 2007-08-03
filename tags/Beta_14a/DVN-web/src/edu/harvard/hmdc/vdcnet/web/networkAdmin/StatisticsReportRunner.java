/*
 * StatisticsReportRunner.java
 * 
 * Created on Jul 31, 2007, 11:00:57 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import java.util.*;
import java.io.*;
/**
 *
 * @author wbossons
 */
public class StatisticsReportRunner extends Thread {

    InputStream inputStream;
    String type;
    OutputStream outputStream;
    
    public StatisticsReportRunner(InputStream inputStream, String type) {
        this(inputStream, type, null);
    }

    public StatisticsReportRunner (InputStream inputStream, String type, OutputStream redirect) {
        this.inputStream = inputStream;
        this.type = type;
        this.outputStream = redirect;
    }
    
    public void run() {
        try {
            PrintWriter printwriter = null;
            if (outputStream != null)
                printwriter = new PrintWriter(outputStream);
                
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputReader);
            String line=null;
            boolean isIncluded = false;
            while ( (line = reader.readLine()) != null) {
                if (isIncluded == false && line.contains("<a name=\"top\">&nbsp;</a>")) {
                    isIncluded = true;
                    continue;
                } else if (isIncluded == false) {
                    continue;
                }
                if (isIncluded == true && line.contains("</body>")) {
                    isIncluded = false;
                    break;
                }
                if (printwriter != null)
                    printwriter.println(line);
                System.out.println(type + ">" + line);    
            }
            if (printwriter != null)
                printwriter.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();  
        }
    }   
}