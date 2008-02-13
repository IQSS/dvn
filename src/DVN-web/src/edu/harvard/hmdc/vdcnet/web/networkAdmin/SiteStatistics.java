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

import edu.harvard.hmdc.vdcnet.admin.GroupServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author wbossons
 */



public class SiteStatistics extends VDCBaseBean implements java.io.Serializable  {

    private int startMonth = 7; // the default start month is July
    private int endMonth = 6; // the default end month is June.

    /** Creates a new instance of SiteStatistics */
    public SiteStatistics() {
    }

    public void init() {
        super.init();
        success = false;
        setDirectory("awstats"); //TODO should this be parameterized?
        try {
            setDataDirectory("awstatsData");
        } catch (IOException ioe) {
            System.out.println("Error initializing web statistics files.");
        }

        //check to see if a reportee is in request
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String reportee = request.getParameter("reportee");
        if (reportee == null) {
            Iterator iterator = request.getParameterMap().keySet().iterator();
            while (iterator.hasNext()) {
                Object key = (Object) iterator.next();
                if ( key instanceof String && ((String) key).indexOf("reportee") != -1 && !request.getParameter((String)key).equals("")) {
                    this.setReportee(request.getParameter((String)key));
                }
                if ( key instanceof String && ((String) key).indexOf("reportType") != -1 && !request.getParameter((String)key).equals("")) {
                    this.setReportType(request.getParameter((String)key));
                }
            }
        }
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
        ReportWriter reportwriter = new ReportWriter(months, args[1], args[2]);
        reportwriter.writeMitReport();
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
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        if (this.getReportee().equals("mitMonthly") ){
            request.setAttribute("reportee", "mit");
            request.setAttribute("reportType", "txt");
            this.setReportType("txt");
        } else {
            request.setAttribute("reportee", this.getReportee());//used by view page to construct include
            request.setAttribute("reportType", "html");
        }
        try {
            if ( this.getReportType().equals("html") ) {
                setNamedConfigFile(this.getReportee());
                //update the statistics database
                boolean isUpdated = this.updateStatistics();
                if (isUpdated == false) {
                    System.out.println("Operation completed with errors. Please see the server log for details.");
                }
                String command = "perl " + this.getDirectory() + "/tools/awstats_buildstaticpages.pl -config=" + this.getReportee() 
                                + " -awstatsprog=" + this.getDirectory() 
                                + "/awstats.pl -dir=" + this.getDataDirectory();
                isUpdated = this.updateStatistics(command);
                if (isUpdated == false) {
                    System.out.println("Operation completed with errors. No static links were generated.");
                }
                //write out the html
                this.writeStatistics();
                success = "guiReport";
            } else if (this.getReportType().equals("txt")) { //TODO: parameterize monthly reports
                setNamedConfigFile("mit");
                boolean isUpdated = this.updateStatistics();
                int months  = this.getMonths() ;
                String year = this.getYear();
                ReportWriter reportwriter = new ReportWriter(months, year, this.getNamedConfigFile(), "<br /><br />");
                reportwriter.writeMitReport();
                success = "monthlyReport";
            }
            //System.out.println("the report to print is " + this.getReportType() + " the reportee is " + this.getReportee());
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
            String command = "perl " + this.getDirectory() + "/awstats.pl -config=" + getNamedConfigFile() + " -update -staticlinks";
            if (this.getDirectory() == null) {
                throw new Exception("System property \"dvn.awstats.dir\" has not been set.");
            }
            Process process = runtime.exec(command);
            StatisticsReportRunner errorRunner = new StatisticsReportRunner(process.getInputStream(), "ERROR");
            errorRunner.start();
            // An exitValue of 0 indicates termination with no errors
            int exitValue = process.waitFor();
            System.out.println("Update Statistics - ExitValue: " + exitValue);
            if (exitValue == 0)
                isUpdated = true;
            else
                isUpdated = false;
        } catch (Exception e) {
            System.out.println("An error occurred while updating awstats statistics. " + e.toString());
        } finally {
            return isUpdated;
        }
    }
    
    /** overloaded method to avoid changing the api
     * this will allow other awstats scripts to be run
     * just add the command
     *
     * @param command the awstats command to be run
     *
     * @author wbossons
     */
    public synchronized boolean updateStatistics(String command) {
        boolean isUpdated = false;
        try {
            Runtime runtime = Runtime.getRuntime();
            if (this.getDirectory() == null) {
                throw new Exception("System property \"dvn.awstats.dir\" has not been set.");
            }
            Process process = runtime.exec(command);
            StatisticsReportRunner errorRunner = new StatisticsReportRunner(process.getInputStream(), "ERROR");
            errorRunner.start();
            // An exitValue of 0 indicates termination with no errors
            int exitValue = process.waitFor();
            System.out.println("Update Static Pages Statistics - ExitValue: " + exitValue);
            if (exitValue == 0)
                isUpdated = true;
            else
                isUpdated = false;
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
            String dvnDataDirectory = this.getDataDirectory();
            if (awstatsDirectory == null || dvnDataDirectory == null) {
                throw new Exception("System property \"dvn.awstats.dir\" or\"dvn.awstatsData.dir\" has not been set.");
            }
            HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String reportUrl = request.getContextPath() + "/faces/networkAdmin/webstatistics";
            String urlToStaticFiles = reportUrl;
            FileOutputStream fileoutput = new FileOutputStream(dvnDataDirectory + "/awstats." + this.getNamedConfigFile() + ".html");
            Runtime runtime = Runtime.getRuntime();
            String command = "perl " + awstatsDirectory + "/awstats.pl -config=" + getNamedConfigFile() + " -output -staticlinks";
            Process process = runtime.exec(command);
            // check for errors and output
            StatisticsReportRunner errorRunner = new StatisticsReportRunner(process.getErrorStream(), "ERROR");
            StatisticsReportRunner reportRunner = new StatisticsReportRunner(process.getInputStream(), "OUTPUT", fileoutput, urlToStaticFiles);
            errorRunner.start();
            reportRunner.start();
            // An exitValue of 0 indicates termination with no errors
            int exitValue = process.waitFor();
            System.out.println("Write Statistics - ExitValue: " + exitValue);
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
    
    private String dataDirectory = null;

    public String getDataDirectory() {
        return dataDirectory;
    }

    public void setDataDirectory(String systemdir) 
            throws IOException {
        String systemdirectory = "dvn." + systemdir + ".dir";
        this.dataDirectory = System.getProperty(systemdirectory);
        //also check for this dir and create if needed
        boolean exists = (new File(this.dataDirectory)).exists();
        if (!exists) {
            File webStatisticsDir = new File(dataDirectory + "/");
            webStatisticsDir.mkdir(); 
        }
        // Make the temporary files if no files exist
        try {
            String[] files = {"awstats.iqss.html", "awstats.mit.html", "awstats.mit.txt"};
            int i = 0;
            while (i < files.length) {
                exists = (new File(this.dataDirectory + "/" + files[i]).exists());
                if (!exists) {
                    File file = new File (this.dataDirectory + "/" + files[i]);
                    file.createNewFile();
                }
                i++;
            }
        } catch (IOException ioe) {
            System.out.println("Unable to create the web statistics files.");
        }
    }

    private String namedConfigFile = null;

    public String getNamedConfigFile() {
        return this.namedConfigFile;
    }

    public void setNamedConfigFile(String filename) {
        this.namedConfigFile = filename;
    }

    private String reportee = null;

    public String getReportee() {
        return reportee;
    }

    public void setReportee(String reportee) {
        this.reportee = reportee;
    }

    private List<SelectItem> reportees = null;

    public List getReportees() {
        if (this.reportees == null) {
            reportees = new ArrayList();
            //which reportees are configured by awstats.
            if (this.getDirectory() != null) {
                File directory = new File(this.getDirectory());
                File[] files   = directory.listFiles();
                int i = 0;
                while (i < files.length) {
                    if (files[i].getName().indexOf(".conf") != -1) {
                        int startIndex = files[i].getName().indexOf(".") + 1;
                        int endIndex   = files[i].getName().indexOf(".", startIndex);
                        reportees.add(new SelectItem(files[i].getName().substring(startIndex, endIndex), files[i].getName().substring(startIndex, endIndex).toUpperCase() + " Graphical Report"));
                        if (files[i].getName().indexOf("mit") != -1) {
                            reportees.add(new SelectItem("mitMonthly", "MIT Monthly"));
                        }
                    }
                    i++;
                }
            }
            return reportees;
        }
        return reportees;
    }

    public void changeReportee(ValueChangeEvent event) {
        String newValue = (String) event.getNewValue();
        setReportee(newValue);
        if (newValue.equals("mitMonthly"))
            this.setReportType("txt");
        else
            this.setReportType("html");
    }
    
    private String reportType = null;

    public String getReportType() {
        if (reportType == null)
            this.setReportType("html");
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    
    public void changeReportType(ValueChangeEvent event) {
        String newValue = (String) event.getNewValue();
        setReportType(newValue);
    }

    private int months = 0;

    public int getMonths() {
        if (months == 0) {
            Date date = new Date();
            String reportDate;
            int currentMonth = 0;
            SimpleDateFormat simpledateformat = new SimpleDateFormat("MM");
            reportDate = simpledateformat.format(date);
            currentMonth = Integer.parseInt(reportDate);
            int startIndex = 6;
            if (currentMonth < 7)
                months = (currentMonth + 13) - 7;
            else if (currentMonth >= 7) 
                months = (currentMonth + 1) - 7;
        }
        return months;
    }

    public void setMonth(int months) {
        this.months = months;
    }

    private String year = null;

    public String getYear() {
        if (year == null) {
            Date date = new Date();
            String reportDate;
            SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy");
            reportDate = simpledateformat.format(date);
            this.year = reportDate;
        }
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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
