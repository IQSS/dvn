/*
 * ReportConstants.java
 *
 * Created on Jul 23, 2007, 10:06:19 AM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author wbossons
 */
public class ReportConstants {

 public ReportConstants() {

 }

 public static void main(String[] args)
 			throws java.io.IOException {
    System.out.println("in the ReportConstants class");
 }

  //Common Strings
  public static final String NEW_LINE = System.getProperty("line.separator");
  public static final String FILE_SEPARATOR = System.getProperty("file.separator");
  public static final String PATH_SEPARATOR = System.getProperty("path.separator");
  public static final String DELIMITER = " ";

  public static final String BEGIN_GENERAL = "BEGIN_GENERAL";
  public static final String TOTAL_VISITS  = "TotalVisits";
  public static final String TOTAL_VISITS_HEADING 	= "Total number of hits by MIT users.";
  public static final String TOTAL_UNIQUE  			= "TotalUnique";
  public static final String TOTAL_UNIQUE_HEADING  	= "Number of Unique MIT-assigned IP Addresses Accessing the IQSS Dataverse Network";
  //BEGIN_EXTRA_1
  public static final String BEGIN_EXTRA_1 			= "BEGIN_EXTRA_1";
  public static final String NUM_DOWNLOADS_HEADING 	= "Number of downloads of data and documentation from the IQSS Dataverse Network by MIT Users";
 //BEGIN_EXTRA_2
  public static final String BEGIN_EXTRA_2 			= "BEGIN_EXTRA_2";
  public static final String NUM_SUBSETJOBS_HEADING = "Number of  subsetting interface views resulting in one or more actions of running an actual subsetting/statistical job by MIT Users";
  //BEGIN_EXTRA_3
  public static final String BEGIN_EXTRA_3 			= "BEGIN_EXTRA_3";
  public static final String NUM_UNIQUEDOWNLOADS_HEADING = "Number of unique ip addresses downloading data and documentation from the VDC";
  public static final String END_EXTRA_3 			= "END_EXTRA_3";
  //BEGIN_EXTRA_4
  public static final String BEGIN_EXTRA_4 			= "BEGIN_EXTRA_4";
  public static final String NUM_UNIQUESUBSETS_HEADING = "Number of unique ip addresses generating subsetting interface views.";
  public static final String END_EXTRA_4 			= "END_EXTRA_4";

  public static final String COMMA = ",";
  public static final String DOUBLE_QUOTE = "\"";
  public static final String SINGLE_QUOTE = "'";

  //public static final int FAILURE = -1;
  //public static final int NOT_FOUND = -1;
  //public static final boolean PASS = true;
  //public static final boolean FAIL = false;

  // PRIVATE //
  /*
  * Implementation Note :
  * Only refer to primitives and immutable objects.
  *
  * Arrays present a problem since arrays are always mutable.
  * DO NOT USE public static final array fields.
  * One style is to replace with an umodifiable List, built in a static initializer block.
  *
  * Another style is to use a private array and wrap it up like so:
  * <pre>
  *  private static final Vehicle[] PRIVATE_VEHICLES = {...};
  *  public static final List VEHICLES =
  *    Collections.unmodifiableList(Arrays.asList(PRIVATE_VEHICLES))
  *  ;
  * </pre>
  */

}
