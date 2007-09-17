/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/*
 * StatisticsReportRunner.java
 * 
 * Created on Jul 31, 2007, 11:00:57 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import java.io.*;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
/**
 *
 * @author wbossons
 */
public class StatisticsReportRunner extends Thread {

    InputStream inputStream;
    String type;
    OutputStream outputStream;
    String urlToStaticFiles;
    
    public StatisticsReportRunner(InputStream inputStream, String type) {
        this(inputStream, type, null);
    }

    public StatisticsReportRunner (InputStream inputStream, String type, OutputStream redirect) {
        this.inputStream = inputStream;
        this.type = type;
        this.outputStream = redirect;
    }
    
    public StatisticsReportRunner (InputStream inputStream, String type, OutputStream redirect, String urlToStaticFiles) {
        this.inputStream = inputStream;
        this.type = type;
        this.outputStream = redirect;
        this.urlToStaticFiles = urlToStaticFiles;
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
                if (printwriter != null) {
                    if (line.indexOf("awstats.") != -1) {
                        printwriter.println(line.replace("awstats.", urlToStaticFiles + "/awstats."));
                    }
                    else
                        printwriter.println(line);
                }
            }
            if (printwriter != null)
                printwriter.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();  
        }
    }   
}