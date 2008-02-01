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
 * SiteStatistics.java
 *
 * Created on Jul 20, 2007, 2:26:01 PM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import java.io.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author wbossons
 */

public class ReportWriter extends ReportConstants {

	private static String awstatsDirectory = System.getProperty("dvn.awstats.dir");
        private static String dvnDataDirectory = System.getProperty("dvn.awstatsData.dir");
	private static String line             = null;
        
        private int numberOfMonths = 0;
	private String theYear     = new String("");
        private String reportee    = new String("");
        private String separator   = new String("");

	public ReportWriter(int months, String year, String reportee) {
		this.numberOfMonths    = months;
		this.theYear	       = year;
                this.reportee          = reportee;
                this.separator         = separator;
	}
        
        public ReportWriter(int months, String year, String reportee, String separator) {
		this.numberOfMonths    = months;
		this.theYear	       = year;
                this.reportee          = reportee;
                this.separator         = separator;
	}
        
    /**
     *
     * @description
     * This method creates the monthly report starting with the
     * July. Number of months specified in constructor will be used to build
     * an array of the monthly data. The month and year are used to create
     * the filename to be read.
     *
     *
     * middle portion of the awstats config file
     * ex.awstats.mit.txt
     *
     * TODO: parameterize the system directory
     * TODO: parameterize the config file name (ex. mit)
     */
    public void writeMitReport()
    			throws java.io.IOException {
        BufferedReader inputStream = null;
        BufferedWriter outputStream = null;
        try {
	    LinkedHashMap totalVisitsList = new LinkedHashMap();
	    LinkedHashMap totalUniqueList = new LinkedHashMap();
	    LinkedHashMap downloadsList   = new LinkedHashMap();
            LinkedHashMap downloadsIpList = new LinkedHashMap();
	    LinkedHashMap subsetsList     = new LinkedHashMap();
	    LinkedHashMap subsetsIpList   = new LinkedHashMap();
            LinkedHashMap missingReportData    = new LinkedHashMap();

	    String[][] monthsInReport = getReportFiles(numberOfMonths);
            for (int i = 0; i < monthsInReport.length; i++) {
                this.setAwstatsDirData(reportee);
                Integer year = getYear(monthsInReport[0][i]);
                String fileToRead = this.getAwstatsDirData() + "/awstats" + monthsInReport[0][i] + year.toString() + "." + reportee + ".txt";
                inputStream = new BufferedReader(new FileReader(fileToRead));
                boolean isGeneral = false;
                OUTER_LOOP:
                while ((line = inputStream.readLine()) != null) {
                    if (line.contains(ReportConstants.BEGIN_GENERAL)) {
                            isGeneral = true;
                    }
                    if (line.contains(ReportConstants.TOTAL_VISITS) && isGeneral == true) {
                            String reportValue = line.substring(line.indexOf(ReportConstants.DELIMITER) + 1);
                            totalVisitsList.put(monthsInReport[1][i], reportValue);
                    }
                    if (line.contains(ReportConstants.TOTAL_UNIQUE) && isGeneral == true) {
                            String reportValue = line.substring(line.indexOf(ReportConstants.DELIMITER) + 1);
                            totalUniqueList.put(monthsInReport[1][i], reportValue);
                    }
                    //extra1 (File Downloads)
                    if (line.contains(ReportConstants.BEGIN_EXTRA_1)) {
                            downloadsList.put(monthsInReport[1][i], this.getPageViews(inputStream, ReportConstants.END_EXTRA_1));
                    }
                    //extra2 (number of unique ip addresses downloading data and documentation)
                    if (line.contains(ReportConstants.BEGIN_EXTRA_2)) {
                            String reportValue = Integer.toString(getUniqueIps(inputStream, ReportConstants.END_EXTRA_2));
                            downloadsIpList.put(monthsInReport[1][i], reportValue);
                    }
                    //extra3 (Subsetting Views Resulting in a Subsetting and Analysis Request)
                    if (line.contains(ReportConstants.BEGIN_EXTRA_3)) {
                            subsetsList.put(monthsInReport[1][i], this.getPageViews(inputStream, ReportConstants.END_EXTRA_3));
                    }
                    //extra4 (number of unique ip addresses from which subsetting interface views were made)
                    if (line.contains(ReportConstants.BEGIN_EXTRA_4)) {
                            String reportValue = Integer.toString(getUniqueIps(inputStream, ReportConstants.END_EXTRA_4));
                            subsetsIpList.put(monthsInReport[1][i], reportValue);
                    }
                }
                if (i == monthsInReport.length - 1) inputStream.close();
            }
            //end looping through the monthly files
            //Now add each to the hashtable and send to the report writer.
            LinkedHashMap hashmap = new LinkedHashMap();
            hashmap.put(ReportConstants.TOTAL_VISITS_HEADING, totalVisitsList);
            hashmap.put(ReportConstants.TOTAL_UNIQUE_HEADING, totalUniqueList);
            hashmap.put(ReportConstants.NUM_DOWNLOADS_HEADING, downloadsList);
            hashmap.put(ReportConstants.NUM_UNIQUEDOWNLOADS_HEADING, downloadsIpList);
            hashmap.put(ReportConstants.NUM_SUBSETJOBS_HEADING, subsetsList);
            hashmap.put(ReportConstants.NUM_UNIQUESUBSETS_HEADING, subsetsIpList);
            if (separator.equals(""))
                writeReport(hashmap);
            else 
                writeWebReport(hashmap);
        } catch (IOException ioe) {
		System.out.println("Error " + ioe);
                String errorMessage = "The monthly totals have not been summarized. Please check the data file and verify that there is a data file for each month from July to the present.";
                writeErrorReport("Missing Report Data", errorMessage);
        }
    }

    /**
	*
	* writeReport
	*
	* @description This takes a linkedhashmap and then
	* writes the textual mit report data
	* mainly for command line use
	*
	* @param hashmap A hashmap with the report heading and data.
	*
        *  TODO: Use format to write this report in the proper format and / or
        *  TODO: Use dl to format this report 
	* @author wbossons
	*/    
    private void writeReport(LinkedHashMap hashmap)
				throws java.io.IOException {
            BufferedWriter outputStream = null;
            try {
                String fileToRead = dvnDataDirectory + "/" + reportee + "monthly.txt";
                outputStream = new BufferedWriter(new FileWriter(fileToRead));
                outputStream.write("Monthly Usage Report of MIT Affiliates of the Dataverse Network");
                outputStream.newLine();
                Iterator iterator = hashmap.keySet().iterator();
                int count = 1;
                while (iterator.hasNext()) {
                    String key = (String)iterator.next();
                    outputStream.write("\n\r" + count + ". " + key + "\n\r");
                    outputStream.newLine();
                    LinkedHashMap innermap = (LinkedHashMap)hashmap.get(key);
                    Iterator innerIterator = innermap.keySet().iterator();
                    while (innerIterator.hasNext()) {
                        String monthKey = (String)innerIterator.next();
                        outputStream.write(monthKey + ": " + innermap.get(monthKey));
                        outputStream.newLine();
                    }
                    count++;
                }


            } catch (IOException ioe) {
                    System.out.println("Error " + ioe.getCause().toString());
            } finally {
                    outputStream.close();
            }
	}
        
	/**
	*
	* writeWebReport
	*
	* @description This takes a linkedhashmap and then
	* writes the textual mit report data
	* mainly for command line use
	*
	* @param hashmap A hashmap with the report heading and data.
	*
        *  TODO: Use format to write this report in the proper format and / or
        *  TODO: Use dl to format this report 
	* @author wbossons
	*/
    
	private void writeWebReport(LinkedHashMap hashmap)
				throws java.io.IOException {
            BufferedWriter outputStream = null;
            String reportUrl = this.getReportUrl();
            try {
                String fileToRead = dvnDataDirectory + "/awstats." + reportee + ".txt";
                outputStream = new BufferedWriter(new FileWriter(fileToRead));
                outputStream.write("<div style=\"max-width:780; margin-top:25px; margin-right:auto; margin-left:auto;\">" +
                    "<span style=\"font-weight:800; font-size: medium;\">Monthly Usage Report of MIT Affiliates of the Dataverse Network</span>" + separator +
                    "<div style=\"margin-right:25px;text-align:right;\">" +
                        "<script type=\"text/javascript\">" +
                            "if (typeof(isChild) == 'undefined') { " +
                                "document.write(\"<input type=\\\"button\\\" value=\\\"Print\\\" " +
                                    "onclick=\\\"window.open('" + reportUrl  + "', 'newWindow','menubar=0,resizable=1,width=640,height=480')\\\"/>\") } else {" +
                                    "var scriptBegin = \"<scr\" + \"ipt type=\\\"text/javascript\\\">\"" +
                                    ";document.write(scriptBegin + \"onLoad=window.print();setTimeout('self.close()', 5000);<\\/scr\" + \"ipt>\") }" +
                        "</script>" +
                    "</div>" +
                    "<div style=\"margin-left:25px; margin-right:25px; font-size:medium; text-align:left;\">");
                Iterator iterator = hashmap.keySet().iterator();
                int count = 1;
                while (iterator.hasNext()) {
                    String key = (String)iterator.next();
                    outputStream.write(separator + count + ". " + key + separator);
                    //outputStream.newLine();
                    LinkedHashMap innermap = (LinkedHashMap)hashmap.get(key);
                    Iterator innerIterator = innermap.keySet().iterator();
                    while (innerIterator.hasNext()) {
                        String monthKey = (String)innerIterator.next();
                        outputStream.write(monthKey + ": " + innermap.get(monthKey));
                        outputStream.write("<br />");
                    }
                    count++;
                }
                outputStream.write("</div></div>");
            } catch (IOException ioe) {
                    System.out.println("Error " + ioe.getCause().toString());
            } finally {
                    outputStream.close();
            }
	}
        
        /** writeErrorReport
         *
         * report writer when some data is missing
         *
         * @param label A string
         * @param errorMessage A string
         *
         * @author wbossons
         */
        private void writeErrorReport(String label, String errorMessage) 
            throws java.io.IOException {
            BufferedWriter outputStream = null;
            String fileToWrite = dvnDataDirectory + "/awstats." + reportee + ".txt";
            try {
                outputStream = new BufferedWriter(new FileWriter(fileToWrite));
                outputStream.write("<div style=\"max-width:780; margin-top:25px; margin-right:auto; margin-left:auto;\">");
                outputStream.write("<span style=\"font-weight:800; font-size: medium;\">Monthly Usage Report of MIT Affiliates of the Dataverse Network</span>" + separator);
                outputStream.write("<div style=\"margin-left:25px; margin-right:25px; font-size:medium; text-align:left;\">");
                outputStream.write(label + ": " + errorMessage);
                outputStream.write("</div></div>");
            } catch (IOException ioe)  {
                System.out.println("An error occurred. Unable to write to error stream.");
            } finally {
                outputStream.close();
            }
            
        }

	/** getReportFiles
	*
	* @description This method returns the
	* data needed to create a valid awstats name
	* and to create associations for each month
	* with the report (e.g. July, August, September ...)
	*
	* @param numberofmonths how many months do you want to output?
	*
	* @return a string array with the file number builder and
	* the string month for the labels.
	*
	* @author wbossons
	*/
	private String[][] getReportFiles(int numberofmonths) {
            String[][] monthsAvailable = {{"07","08","09","10","11","12","01","02","03","04","05","06"},{"July", "August", "September", "October", "November", "December", "January", "February", "March", "April", "May", "June"}};
            //example of above is [0][0]=07 and [1][0]=July
            String[][] reportFiles = new String[numberofmonths][numberofmonths];
            for (int j = 0; j < numberofmonths; j++) {
                    reportFiles[0][j] = monthsAvailable[0][j];
                    reportFiles[1][j] = monthsAvailable[1][j];
            }
            return reportFiles;
	}

	/** getUniqueIps
	*
	* @description this method returns the
	* number of unique ip addresses associated
	* with a category like subsetting or downloads
	*
	* @param inputstream - the inputstream being read
	* @param lineend - string representing end of this section
	* @return int - the number of the downloads
	*
	* @author wbossons
	*/
	private int getUniqueIps(BufferedReader inputstream, String lineend)
						throws IOException {
            int numberUnique = 0;
            try {
                while ((line = inputstream.readLine()) != null) {
                    if (line.contains(lineend))
                            break;
                    numberUnique++;
                }
            } catch (IOException ioe) {
                    System.out.println("An error occurred in getUnique Ips");
            } finally {
                    return numberUnique;
            }
	}
        
       /** getPageViews
	*
	* @description this method returns the
	* number of page views associated with
	* a category like subsetting or downloads
	*
	* @param inputstream - the inputstream being read
	* @param lineend - string representing end of this section
	* @return String - the number of the downloads
	*
	* @author wbossons
	*/
	private String getPageViews(BufferedReader inputstream, String lineend)
						throws IOException {
            int pageViews = 0;
            String reportValue = null;
            try {
                //DEBUG
                Integer count    = new Integer(0);
                while ( (line = inputstream.readLine()) != null) {
                    if (line.contains(lineend))
                        break;
                    count += Integer.parseInt(Character.toString(line.charAt(line.indexOf(ReportConstants.DELIMITER) + 1)));
                }
                reportValue = count.toString();
                //downloadsList.put(monthsInReport[1][i], reportValue);
                //END DEBUG
            } catch (IOException ioe) {
                    System.out.println("An error occurred in getPageViews");
            } finally {
                    return reportValue;
            }
	}
        
        /** getReportUrl
         *
         * @description Returns the url for the 
         * print version of the web version of the monthly report
         *
         * @return String
         *
         * @author wbossons
         */
        private String getReportUrl() 
            throws UnsupportedEncodingException {
              String reportUrl = new String("");
              HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
              Enumeration enumeration = request.getHeaderNames();
              
              String protocol = request.getProtocol().substring(0, request.getProtocol().indexOf("/")).toLowerCase();
              reportUrl = request.getContextPath() + "/faces/networkAdmin/printPopup.jsp?reportee=" + reportee;
              return reportUrl;
        }
        
        /** getYear
         * 
         * selects year needed for
         * file reading -- when the year
         * turns, it must be decremented for 
         * the months leading up to the new
         * year
         * 
         */
        
        private Integer getYear(String monthsInReport) {
              Integer year = new Integer(0);
              Pattern pattern = Pattern.compile("(0[7-9])|(1[0-2])");
              Matcher match = pattern.matcher(monthsInReport);
              if (match.find())
                    year = Integer.parseInt(theYear) - 1;
                else
                    year = Integer.parseInt(theYear);
              match.reset();
              return year;
        }
        
        /** prop and accessor/mutator method
         * to determine the data storage location
         * as set in awstats.<name>.conf file
         *
         * @author wbossons
         *
         */
        
        private String awstatsDirData = null;

        public String getAwstatsDirData() {
            return awstatsDirData;
        }

        public void setAwstatsDirData(String reportee) 
            throws IOException {
            try {
                BufferedReader inputStream = null;
                String fileToRead = awstatsDirectory + "/awstats." + reportee + ".conf";
                inputStream = new BufferedReader(new FileReader(fileToRead));
                boolean isGeneral = false;
                String line = null;
                while ((line = inputStream.readLine()) != null) {
                    if (line.indexOf("DirData=") != -1) {
                        this.awstatsDirData = line.substring(line.indexOf("DirData=") + 9, line.length()-1);
                        break;
                    }
                }
            } catch (IOException ioe) {
                System.out.println("There was a problem reading the conf file. " + ioe.toString());
            } catch (Exception e) {
                System.out.println("An unexpected exception occurred while trying to read the conf file.");
            } 
        }

	public boolean equals(Object obj) {
	    return (obj instanceof String
	            && this.line == ((String)obj).toString());
  }

}
