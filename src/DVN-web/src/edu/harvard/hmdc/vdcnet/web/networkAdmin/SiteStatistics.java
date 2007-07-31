/*
 * SiteStatistics.java
 *
 * Created on Jul 20, 2007, 2:26:01 PM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import edu.harvard.hmdc.vdcnet.admin.GroupServiceLocal;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author wbossons
 */

public class SiteStatistics extends ReportConstants {

    private int startMonth = 7; // the default start month is July
    private int endMonth = 6; // the default end month is June.

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
    public static void main(String[] args) throws java.io.IOException {
        int months = Integer.parseInt(args[0]);
        ReportWriter reportwriter = new ReportWriter(months, args[1]);
        reportwriter.writeMitReport();
        //writeMitReport(args[0]);
    }
    /**viewStatistics
     * 
     * This is the action method from
     * theSiteStatistics page
     * 
     * @author wbossons
     */
    public String viewStatistics() {
        String success = "success";
        //set the directory and the conf file name
        setDirectory("awstats");//TODO parameterize this
        setNamedConfigFile("mit"); //TODO parameterize this
        try {
            //update the statistics database
            boolean isUpdated = this.updateStatistics();
            //send the response
            if (isUpdated == false) {
                System.out.println("Operation completed with errors. Please see the server log for details.");
            }
            //write out the html
            this.writeStatistics();
        } catch (Exception e) {
            System.out.println("An error occurred. Unable to complete the operation.");
            success = "failed";
        } finally {
        return success;
        }
    }
    
    /** updateStatistics
     * 
     * updates the awstats database file
     * 
     * 
     * 
     * @author wbossons
     */
    public synchronized boolean updateStatistics() {
        boolean isUpdated = false;
        try {
        Runtime runtime = Runtime.getRuntime();
        String command  = "perl " + this.getDirectory() + "/awstats.pl -config=" + getNamedConfigFile() + " -update";
        if (this.getDirectory() == null) {
            throw new Exception("System property \"dvn.awstats.dir\" has not been set.");
        }
        Process process = runtime.exec(command);
        StatisticsReportRunner errorRunner  = new StatisticsReportRunner(process.getInputStream(), "ERROR");            
        errorRunner.start();
        // An exitValue of 0 indicates termination with no errors                        
        int exitValue = process.waitFor();
        System.out.println("ExitValue: " + exitValue);
        isUpdated = true;
        } catch (Exception e) {
            System.out.println("An error occurred while updating awstats statistics. " + e.toString());
        } finally {
            return isUpdated;
        }
    }
    
    /** writeStatistics
     * 
     * writes the awstats data to html
     * 
     * 
     * 
     * @author wbossons
     */
    public synchronized boolean writeStatistics() throws IOException {
        boolean isWritten = false;
        try {
            String awstatsDirectory = this.getDirectory();
            if (awstatsDirectory == null) {
                    throw new Exception("System property \"dvn.awstats.dir\" has not been set.");
            }
            String awstatsFilePath = awstatsDirectory + "/custom/awstats." + getNamedConfigFile() + ".html";
            FileOutputStream fileoutput = new FileOutputStream((awstatsDirectory + "/custom/awstats.mit.html"));
            Runtime runtime = Runtime.getRuntime();
            String command  = "perl " + awstatsDirectory + "/awstats.pl -config=" + getNamedConfigFile() + " -output -staticlinks";
            Process process = runtime.exec(command);
            // check for errors and output
            StatisticsReportRunner errorRunner  = new StatisticsReportRunner(process.getErrorStream(), "ERROR");            
            StatisticsReportRunner reportRunner = new StatisticsReportRunner(process.getInputStream(), "OUTPUT", fileoutput);
            errorRunner.start();
            reportRunner.start();
            // An exitValue of 0 indicates termination with no errors                        
            int exitValue = process.waitFor();
            System.out.println("ExitValue: " + exitValue);
            fileoutput.flush();
            fileoutput.close(); 
            isWritten = true;
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            return isWritten;
        }
    }

    public void writeReport(String dataverse) {
        // this is blank for now
    }

    private String directory = null;
    
    public String getDirectory() {
        return directory;
    }
    
    public void setDirectory(String systemdir) {
        String systemdirectory = "dvn." + systemdir + ".dir";
        this.directory = System.getProperty(systemdirectory);
    }
    
    private String namedConfigFile = null;
    
    public String getNamedConfigFile() {
        return this.namedConfigFile;
    }
    
    public void setNamedConfigFile(String filename) {
        this.namedConfigFile = filename;
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
     *  parsed from the access log
     * this is probably a list or array, not an int
     */
    private int subsetAnalysisIps;

    public int getSubsetAnalysisIps() {
        return this.subsetAnalysisIps;
    }

    public void setSubsetAnalysisIps(int numsubsets) {
        this.subsetAnalysisIps = numsubsets;
    }

    private GroupServiceLocal lookupGroupServiceBean() {
        try {
            javax.naming.Context c = new javax.naming.InitialContext();
            return (edu.harvard.hmdc.vdcnet.admin.GroupServiceLocal) c.lookup("java:comp/env/GroupServiceBean");
        } catch (javax.naming.NamingException ne) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
