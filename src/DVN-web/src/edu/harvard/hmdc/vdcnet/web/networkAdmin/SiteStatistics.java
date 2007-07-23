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

/**
 *
 * @author wbossons
 */

public class SiteStatistics {

    /** Creates a new instance of SiteStatistics */
    public SiteStatistics() {
    }

    /**
     * Use this to kick off reports
     * from outside the UI, e.g.
     *  the command line
     *
     *
     */
    public static void main(String[] args)
			throws java.io.IOException {
        writeMitReport(args[0]);
    }

    public void writeReport(String dataverse) {
        // this is blank for now
    }

    private static String line = null;

    /**
     * @param term could be a month daily weekly or yearly report
     * this may need additional information. Year and month?
     * @param config -- middle portion of the awstats config file
     * ex.awstats.mit.txt
     *
     *
     */
    public static void writeMitReport(String term)
    			throws java.io.IOException {
        BufferedReader inputStream = null;
        BufferedWriter outputStream = null;
        try {
		inputStream = new BufferedReader(new FileReader("c:\\data\\awstats072007.mit.txt"));
	        outputStream = new BufferedWriter(new FileWriter("c:\\data\\mitreportoutput.txt"));
                boolean isTargetCategory = false;
            while ((line = inputStream.readLine()) != null) {
                if (ReportConstants.BEGIN_GENERAL.contains(line)) {
                    // set some flag so that I know I'm in the right place
                    isTargetCategory = true;
                }
                // now I want to zero in on the value, in this case TotalVisits
                if (ReportConstants.TOTAL_VISITS.contains("TotalVisits") && isTargetCategory == true) {
                    // find the value through substringing the line
                    String reportValue = line.substring(line.indexOf(ReportConstants.DELIMITER) + 1);
                    outputStream.write(line);
                    outputStream.newLine();
                    isTargetCategory = false;
                }
                
            }
        } catch (Exception e) {
            // write an error
            System.out.println("Error " + e);
        } finally {
            inputStream.close();
            outputStream.close();
        }
    }

    private String category;

    private String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private int count;

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int visits;

    public int getVisits() {
        return this.visits;
    }

    public void setVisits(int totalvisits) {
        this.visits = totalvisits;
    }

    private int uniqueVisits;

    public int getUniqueVisits() {
        return this.uniqueVisits;
    }

    public void setUniqueVisits(int totalUniqueVisits) {
        this.uniqueVisits = totalUniqueVisits;
    }

    private int downloads;

    public int getDownloads() {
        return this.downloads;
    }

    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }

    /**
     * parsed from the access log
     *
     */
    private int uniqueDownloadIps;

    public int getUniqueDownloadIps() {
        //parsed from the access log
        return this.uniqueDownloadIps;
    }

    public void setUniqueDownloadIps(int unique) {
        this.uniqueDownloadIps = unique;
    }

    private int subsetAnalysis;

    private int getSubsetAnalysis() {
        return this.subsetAnalysis;
    }

    public void setSubsetAnalysis(int numsubsets) {
        this.subsetAnalysis = numsubsets;
    }

    /**
     * â€“ parsed from the access log
     * this is probably a list or array, not an int
     */
    private int subsetAnalysisIps;

    public int getSubsetAnalysisIps() {
        return this.subsetAnalysisIps;
    }

    public void setSubsetAnalysisIps(int numsubsets) {
        this.subsetAnalysisIps = numsubsets;
    }

  public boolean equals(Object obj) {
    return (obj instanceof String 
            && this.line == ((String)obj).toString());
  }
}
