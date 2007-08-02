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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author wbossons
 */

public class ReportWriter extends ReportConstants {

	private static String awstatsDirectory = System.getProperty("dvn.awstats.dir");
	private static String line             = null;
        
        private int numberOfMonths = 0;
	private String theYear     = new String("");
        private String reportee    = new String("");
        private String separator   = new String("");

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
	    LinkedHashMap subsetsList     = new LinkedHashMap();
	    LinkedHashMap downloadsIpList = new LinkedHashMap();
	    LinkedHashMap subsetsIpList   = new LinkedHashMap();

	    String[][] monthsInReport = getReportFiles(numberOfMonths);
            for (int i = 0; i < monthsInReport.length; i++) {
                String fileToRead = awstatsDirectory + "/data/awstats" + monthsInReport[0][i] + theYear + "." + reportee + ".txt";
                inputStream = new BufferedReader(new FileReader(fileToRead));
                boolean isGeneral = false;
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
                            String reportValue = line.substring(line.indexOf(ReportConstants.DELIMITER) + 1);
                            downloadsList.put(monthsInReport[1][i], reportValue);
                    }
                    //extra2 (Subsetting Views Resulting in a Subsetting and Analysis Request)
                    if (line.contains(ReportConstants.BEGIN_EXTRA_2)) {
                            String reportValue = line.substring(line.indexOf(ReportConstants.DELIMITER) + 1);
                            subsetsList.put(monthsInReport[1][i], reportValue);
                    }
                    //extra3 (number of unique ip addresses downloading data and documentation
                    if (line.contains(ReportConstants.BEGIN_EXTRA_3)) {
                            String reportValue = Integer.toString(getUniqueIps(inputStream, ReportConstants.END_EXTRA_3));//line.substring(line.indexOf(ReportConstants.DELIMITER) + 1);
                            System.out.println("the report value is " + reportValue);
                            downloadsIpList.put(monthsInReport[1][i], reportValue);
                    }
                    //extra4 (number of unique ip addresses from which subsetting interface views were made)
                    if (line.contains(ReportConstants.BEGIN_EXTRA_4)) {
                            String reportValue = Integer.toString(getUniqueIps(inputStream, ReportConstants.END_EXTRA_4));//line.substring(line.indexOf(ReportConstants.DELIMITER) + 1);
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
            hashmap.put(ReportConstants.NUM_SUBSETJOBS_HEADING, subsetsList);
            hashmap.put(ReportConstants.NUM_UNIQUEDOWNLOADS_HEADING, downloadsIpList);
            hashmap.put(ReportConstants.NUM_UNIQUESUBSETS_HEADING, subsetsIpList);
            writeReport(hashmap);
        } catch (IOException ioe) {
			System.out.println("Error " + ioe);
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
                String fileToRead = awstatsDirectory + "/custom/awstats." + reportee + ".txt";
                outputStream = new BufferedWriter(new FileWriter(fileToRead));
                if (separator.contains("<br />")) {
                    outputStream.write("<div style=\"max-width:780; margin-top:25px; margin-right:auto; margin-left:auto;\">");
                    outputStream.write("<span style=\"font-weight:800; font-size: medium;\">Monthly Usage Report of MIT Affiliates of the Dataverse Network</span>" + separator);
                    outputStream.write("<div style=\"text-align:right;\"><input type=\"button\" value=\"Print\" onclick=\"window.open('" + awstatsDirectory + "/custom/awstats.' + reportee + '.txt', 'newWindow','menubar=0,resizable=1,width=640,height=480');\"/></div>");
                    outputStream.write("<div style=\"margin-left:25px; margin-right:25px; font-size:medium; text-align:left;\">");
                } else {
                    outputStream.write("Monthly Usage Report of MIT Affiliates of the Dataverse Network" + separator);
                }
                Iterator iterator = hashmap.keySet().iterator();
                int count = 1;
                while (iterator.hasNext()) {
                    String key = (String)iterator.next();
                    outputStream.write(separator + count + ". " + key + separator);
                    outputStream.newLine();
                    LinkedHashMap innermap = (LinkedHashMap)hashmap.get(key);
                    Iterator innerIterator = innermap.keySet().iterator();
                    while (innerIterator.hasNext()) {
                        String monthKey = (String)innerIterator.next();
                        outputStream.write(monthKey + ": " + innermap.get(monthKey));
                        if (separator.contains("<br />"))
                            outputStream.write("<br />");
                        outputStream.newLine();
                    }
                    count++;
                }
                if (separator.contains("<br />"))
                    outputStream.write("</div></div>");
            } catch (IOException ioe) {
                    System.out.println("Error " + ioe.getCause().toString());
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
            String[][] reportFiles = new String[2][2];
            System.out.println("the month is " + monthsAvailable[1][0]);
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

	public boolean equals(Object obj) {
	    return (obj instanceof String
	            && this.line == ((String)obj).toString());
  }

}
