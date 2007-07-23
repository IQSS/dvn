/*
 * SiteStatistics.java
 *
 * Created on Jul 20, 2007, 2:26:01 PM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
	private int endMonth   = 6; // the default end month is June.

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

    private static String line     = null;
	//private static List    list    = new ArrayList();
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
	    LinkedHashMap totalVisitsList = new LinkedHashMap();
	    LinkedHashMap totalUniqueList = new LinkedHashMap();
	    LinkedHashMap downloadsList   = new LinkedHashMap();
	    LinkedHashMap subsetsList     = new LinkedHashMap();

	    // do the following for each month
	    int numberOfMonths = 2;
	    String theYear = "2007";
	    String[][] monthsInReport = getReportFiles(numberOfMonths);
			for (int i = 0; i < monthsInReport.length; i++) {
				inputStream = new BufferedReader(new FileReader("c:\\data\\awstats" + monthsInReport[0][i] + theYear + ".mit.txt"));
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

						//extra1
						if (line.contains(ReportConstants.BEGIN_EXTRA_1)) {
							String reportValue = line.substring(line.indexOf(ReportConstants.DELIMITER) + 1);
							downloadsList.put(monthsInReport[1][i], reportValue);
						}
						//extra2
						if (line.contains(ReportConstants.BEGIN_EXTRA_2)) {
							String reportValue = line.substring(line.indexOf(ReportConstants.DELIMITER) + 1);
							subsetsList.put(monthsInReport[1][i], reportValue);
						}
					}
					if (i == monthsInReport.length - 1) inputStream.close();
			}//end adding the data from each month
			LinkedHashMap hashmap = new LinkedHashMap();
			hashmap.put(ReportConstants.TOTAL_VISITS_HEADING, totalVisitsList);
			hashmap.put(ReportConstants.TOTAL_UNIQUE_HEADING, totalUniqueList);
			hashmap.put(ReportConstants.NUM_DOWNLOADS_HEADING, downloadsList);
			hashmap.put(ReportConstants.NUM_SUBSETJOBS_HEADING, subsetsList);
			//end looping through the files. Now add each to the hashtable and send to the report writer.
			writeReport(hashmap);
        } catch (IOException ioe) {
			System.out.println("Error " + ioe);
		} //finally {
            //inputStream.close();
        //}
    }

	private static void writeReport(LinkedHashMap hashmap)
				throws java.io.IOException {
		BufferedWriter outputStream = null;
		try {
			outputStream = new BufferedWriter(new FileWriter("c:\\data\\mitreportoutput.txt"));
			Iterator iterator = hashmap.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String)iterator.next();
				outputStream.write("\n\r" + key + "\n\r");
				outputStream.newLine();
				LinkedHashMap innermap = (LinkedHashMap)hashmap.get(key);
				Iterator innerIterator = innermap.keySet().iterator();
				while (innerIterator.hasNext()) {
					String monthKey = (String)innerIterator.next();
					outputStream.write(monthKey + ": " + innermap.get(monthKey));
					outputStream.newLine();
			    }
			}
		} catch (IOException ioe) {
			System.out.println("Error " + ioe.getCause().toString());
		} finally {
			outputStream.close();
		}
	}

	private static String[][] getReportFiles(int numberofmonths) {
		String[][] monthsAvailable = {{"07","08","09","10","11","12","01","02","03","04","05","06"},{"July", "August", "September", "October", "November", "December", "January", "February", "March", "April", "May", "June"}};
		//example of above is [0][0]=07 and [1][0]=July
		String[][] reportFiles = new String[2][2];
		System.out.println("the month is " + monthsAvailable[1][0]);
        for (int j = 0; j < numberofmonths; j++) {
			System.out.println("j = " + j);
			reportFiles[0][j] = monthsAvailable[0][j];
			System.out.println("j = " + j + "loop again");
			reportFiles[1][j] = monthsAvailable[1][j];
		}
		return reportFiles;
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
     * – parsed from the access log
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
