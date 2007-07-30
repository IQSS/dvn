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
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.context.*;
import javax.servlet.http.HttpServletRequest;

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
    
    public void doReport(String dataverse) {
        // this is blank for now
        String action = new String("");
        try {
            if (isGUIReport) {
                //who is the report for
                String reportee = getReportee();
                //then generate the Report
                generateGUIReport(reportee);
            } else if (isMitMonthlyReport) {
                int months = getMonths();
                int year = getYear();
                boolean isReport = generateReport(months, year); // run awstats.pl and awstats.mit.html
            }
        } catch (Exception e) {
            System.out.println("An exception was caught ... " + e);
            action = "Failed";
        } finally {
            return success;
        }
        
        
        return action;
    }
    
    //common report mutators and accessors
    private boolean isGUIReport = false;
    
    public boolean getIsGUIReport() {
        return this.isGUIReport;
    }
    
    public void setIsGUIReport(boolean guiReport) {
        this.isGUIReport = guiReport;
    }
    private String reportee = null;
    
    public String getReportee() {
        return this.reportee;
    }
    
    public void setReportee(String reportee) {
        this.reportee = reportee;
    }
    
    //GUI report helpers
    /** generateGUIReport
     *
     * @description This will open a url connection
     * to run the awstats perl script
     * for the selected reportee. Then it will
     * generate the html and return a response
     * which will include the html report
     * in its content, e.g. inside the wrapper
     *
     * @param reportee String -- network or MIT
     *
     * @return action String -- indicates navigation
     * action
     *
     * @author wbossons
     *
     */
    private String generateGUIReport(String reportee) {
        String action = new String("");
        //TODO build this url dynamically from protocol to server_name, server_port and context path.
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        System.out.println("The contextpath is "  + request.getProtocol());
        try {
            //build the url
            URL awstats = new URL("http://localhost:8080/dvn/faces/cgi-bin/awstats/awstats.pl?config=mit");
            URLConnection awstatsConnection = awstats.openConnection();
             //run the pl script
            awstatsConnection.connect();
            //run the html generating script
            URL htmlReport = new URL("http://localhost:8080/dvn/faces/cgi-bin/awstats/awstats.pl?config=mit&output=main&framename=index=awstats.mit.html");
            URLConnection htmlReportConnection = htmlReport.openConnection();
             //run the pl script
            htmlReportConnection.connect();
            
        } catch (MalformedURLException e) {     // new URL() failed
            System.out.println("An error occurred because the url was not formed properly.");
        } catch (IOException e) {               // openConnection() failed
            System.out.println("An error occurred with the connection. " + e.toString());
        } finally {
        //based on the response, generate the HTML
        //
        return action;
        }
    }
    
    /** 
     * @description A helper method for the generateGUIReport method
     * 
     * Basically if the html report is there,
     * it will return some content.
     * 
     * 
     */
    private boolean uRLConnectionReader {
        URL yahoo = new URL("http://www.yahoo.com/");
        URLConnection yc = yahoo.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                yc.getInputStream()));
        String inputLine;
        boolean isData = false;
        if ((inputLine = in.readLine()) != null)
            isData = true;
        in.close();
        return input;
}
    
    //MIT monthly report mutators and accessors
    private int months = 0;
    
    public int getMonths() {
        return this.months;
    }
    
    public void setMonths(int months) {
        this.months = months;
    }
    
    private String year = null;
    
    public String getYear() {
        return this.year;
    }
    
    public void setMonths(String year) {
        this.year = year;
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
}
